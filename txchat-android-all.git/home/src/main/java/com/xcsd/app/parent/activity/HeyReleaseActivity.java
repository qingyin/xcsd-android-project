package com.xcsd.app.parent.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.tuxing.app.activity.NewPicActivity;
import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.parent.R;
import com.tuxing.app.util.DateTimePickDialogUtil;
import com.tuxing.app.util.DateTimePickDialogUtil.SelectListener;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.UmengData;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.MyGridView;
import com.tuxing.app.view.MyImageView;
import com.tuxing.sdk.event.FeedMedicineTaskEvent;
import com.tuxing.sdk.event.UploadFileEvent;
import com.tuxing.sdk.modle.Attachment;
import com.tuxing.sdk.utils.Constants;
import com.tuxing.sdk.utils.Constants.ATTACHMENT_TYPE;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HeyReleaseActivity extends BaseActivity implements OnItemClickListener,SelectListener {
	private EditText etRequest;
	private TextView surplusNum;
	private TextView demandTime;
	private MyGridView iconView;
	private IconAdapter adapter;
	private final static int PHOTO = 104;
	private final static int PHOTOSELECT = 105;
	private int state;
	private List<String> phonePath;
	private List<Bitmap> imageList = Collections.synchronizedList(new ArrayList<Bitmap>());
	private List<String> tempPaths = Collections.synchronizedList(new ArrayList<String>());
	private Map<String, SelectedImage> dataMap = new ConcurrentHashMap<String, SelectedImage>();
	private int index = 0;
	private String TAG = HeyReleaseActivity.class.getSimpleName();
	private boolean isAddIcon = true;
	private String IMAGE_FILE_NAME;
	private boolean isUpdate = false;
	private LinearLayout ll;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.hey_release_layout);
		init();
	}

	private void init() {
		setTitle(getString(R.string.eat_drug));
		setLeftBack("取消", false,false);
		setRightNext(true, getString(R.string.button_send), 0);
		etRequest = (EditText) findViewById(R.id.et_demand);
		surplusNum = (TextView) findViewById(R.id.hey_surplus_num);
		demandTime = (TextView) findViewById(R.id.demand_time);
		iconView = (MyGridView) findViewById(R.id.demand_icon_gridview);
		ll = (LinearLayout) findViewById(R.id.ll);
		phonePath = new ArrayList<String>();
		findViewById(R.id.demand_time_rl).setOnClickListener(this);
		demandTime.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));
		iconView.setOnItemClickListener(this);
		etRequest.addTextChangedListener(new MaxLengthWatcher(100, etRequest));
		imageList.add(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.add_icon));
		surplusNum.setFocusable(true);
		updateAdapter();
		ll.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener(){

		    @Override
		    public void onGlobalLayout(){
		       //比较Activity根布局与当前布局的大小
		        int heightDiff = ll.getRootView().getHeight()- ll.getHeight();
		        if(heightDiff >200){
		        //大小超过100时，一般为显示虚拟键盘事件
		        	isUpdate = false;
		       }
		     }
		});
	}
	
	
	public void onEventMainThread(UploadFileEvent event){
		 disProgressDialog();
	        switch (event.getEvent()){
	            case UPLOAD_COMPETED:
	            		adapter.updateView(event.getAttachment());
	            		if(event.getAttachment() != null){
		            		showAndSaveLog(TAG, "上传图片成功" + event.getAttachment().getFileUrl(), false);
		            	}
	                break;
	            case UPLOAD_FAILED:
	            	 adapter.updateView(event.getAttachment());
	            	showAndSaveLog(TAG, "上传图片失败" + event.getMsg(), false);
	            	break;
	            case UPLOAD_PROGRESS_UPDATED:
	            	adapter.updateView(event.getAttachment());
	            	break;
	        }
	    }

	 /**
		 * 服务器返回
		 * @param event
		 */
		 public void onEventMainThread(FeedMedicineTaskEvent event){
			 switch (event.getEvent()) {
			 case ADD_FEED_MEDICINE_SUCCESS:
				 disProgressDialog();
				 showToast("发送成功,请等待老师的回复");
				 Intent intent = new Intent(mContext,HeyActivity.class);
				 setResult(RESULT_OK, intent);
				 finish();
				 showAndSaveLog(TAG, "发送喂药成功  " , false);
				 break;
			 case ADD_FEED_MEDICINE_FAILED:
				 disProgressDialog();
				 showDialog("", getString(R.string.replay_msg), getResources().getString(R.string.btn_ok), "");
				 showAndSaveLog(TAG, "发送喂药失败  ", false);
				 break;
				 
			}
		    }
		
	
	public void updateAdapter(){
		isUpdate = true;
		if(adapter == null){
			adapter = new IconAdapter(mContext, imageList);
			iconView.setAdapter(adapter);
		}else{
			adapter.notifyDataSetChanged();
		}
	}
	
	public class IconAdapter extends BaseAdapter {
		private Context context;
		private List<Bitmap> list;

		public IconAdapter(Context context, List<Bitmap> list) {
			this.context = context;
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size() + tempPaths.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.grideview_replase_layout, null);
				viewHolder = new ViewHolder();
				viewHolder.icon = (MyImageView) convertView.findViewById(R.id.grid_item1_icon);
				viewHolder.delIcon = (ImageView) convertView.findViewById(R.id.gride_imtm1_faild_del);
				viewHolder.progressRl = (RelativeLayout) convertView.findViewById(R.id.gride_imtm1_progress_rl);
				viewHolder.faildRl = (RelativeLayout) convertView.findViewById(R.id.gride_imtm1_faile_rl);
				viewHolder.load_size = (TextView) convertView.findViewById(R.id.load_size);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if(isUpdate){
				if(iconView.getChildCount() == position){
					 if((position + 1) != (imageList.size() + tempPaths.size()) && tempPaths.size() >= 1){
						 String tempImagePath = tempPaths.get(position);
						 SelectedImage image = dataMap.get(tempImagePath);
						 if(image != null) {
							 viewHolder.delIcon.setVisibility(View.VISIBLE);
							 viewHolder.progressRl.setVisibility(View.VISIBLE);
							 viewHolder.icon.setImageBitmap(Utils.revitionImageSize(image.imagePath,SysConstants.IMAGEIMPLESIZE_256));

							 if(image.attachment != null) {
								 if (image.attachment.getStatus() == 5) {
									 //成功
									 viewHolder.progressRl.setVisibility(View.GONE);
									 viewHolder.faildRl.setVisibility(View.GONE);
								 } else if (image.attachment.getStatus() == 7) {
									 //失败
									 viewHolder.faildRl.setVisibility(View.VISIBLE);
									 viewHolder.progressRl.setVisibility(View.GONE);
								 } else {
									 viewHolder.faildRl.setVisibility(View.GONE);
									 viewHolder.progressRl.setVisibility(View.VISIBLE);
								 }
							 }else{
								 viewHolder.faildRl.setVisibility(View.GONE);
								 viewHolder.progressRl.setVisibility(View.VISIBLE);
							 }
						 }
					 }else{
						 if(tempPaths.size() >= 9){
								viewHolder.icon.setVisibility(View.GONE);
							}else{
								viewHolder.icon.setVisibility(View.VISIBLE);
								viewHolder.icon.setImageBitmap(imageList.get(0));
							}
							viewHolder.delIcon.setVisibility(View.GONE);
							viewHolder.progressRl.setVisibility(View.GONE);
							viewHolder.faildRl.setVisibility(View.GONE);
					 }	
				}else{
					 if(!((position + 1) != (imageList.size() + tempPaths.size()) && tempPaths.size() >= 1)){
						 if(tempPaths.size() >= 9){
								viewHolder.icon.setVisibility(View.GONE);
							}else{
								viewHolder.icon.setVisibility(View.VISIBLE);
								viewHolder.icon.setImageBitmap(imageList.get(0));
							}
							viewHolder.delIcon.setVisibility(View.GONE);
						 viewHolder.progressRl.setVisibility(View.GONE);
							viewHolder.faildRl.setVisibility(View.GONE);
					 }
				}
				
			}
			viewHolder.delIcon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO
					index = position;
					isAddIcon = false;
					if(dataMap.size() - 1 >= index){
						String deletedImagePath = tempPaths.remove(index);
						SelectedImage deletedImage = dataMap.remove(deletedImagePath);
						if(deletedImage != null && deletedImage.attachment != null){
							deletedImage.attachment.setIsUploadCancel(true);
						}

					}
					onclickBtn2();
				}
			});

			viewHolder.faildRl.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					isAddIcon = false;
					index = position;
					showBtnDialog(new String[] {"重试",getString(R.string.btn_delete) });
				}
			});

			return convertView;
		}
		
		private void updateView(Attachment data){
			if(data != null && !data.getIsUploadCancel()){
				SelectedImage image = new SelectedImage();
				image.imagePath = data.getLocalFilePath();
				image.attachment = data;
				dataMap.put(data.getLocalFilePath(), image);
			int index = tempPaths.indexOf(data.getLocalFilePath());
			View view = iconView.getChildAt(index);
			ViewHolder holder = (ViewHolder)view.getTag();
			holder.load_size.setText((data.getProgress() + "%"));
			if(dataMap.containsKey(data.getLocalFilePath())){
				holder.delIcon.setVisibility(View.VISIBLE);
			}else{
				holder.delIcon.setVisibility(View.GONE);
			}
			 if(dataMap.size() > 0){
				 if(data.getStatus() == 5){
					 //成功
					 dataMap.get(data.getLocalFilePath()).attachment.setStatus(5);
					 holder.progressRl.setVisibility(View.GONE);
					 holder.faildRl.setVisibility(View.GONE);
				 }else if(data.getStatus() == 7){
					 //失败
					 holder.faildRl.setVisibility(View.VISIBLE);
					 holder.progressRl.setVisibility(View.GONE);
				 }else{
					 holder.faildRl.setVisibility(View.GONE);
					 holder.progressRl.setVisibility(View.VISIBLE);
					}
			 }
		}	
		}
	}

	public class ViewHolder {
		MyImageView icon;
		ImageView delIcon;
		RelativeLayout progressRl;
		RelativeLayout faildRl;
		TextView load_size;
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.demand_time_rl:
			DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
					mContext, demandTime.getText().toString(),demandTime,false,new HeyReleaseActivity());
			dateTimePicKDialog.dateTimePicKDialog();
			break;
		}
	}
	
	@Override
	public void onclickRightNext() {
		super.onclickRightNext();
		for(SelectedImage image : dataMap.values()){
			if(image.attachment == null || image.attachment.getStatus() != 5){
				showToast("图片正在上传");
				return;
			}
		}

			if(!"".equals(demandTime.getText().toString().trim()) && demandTime.getText().toString().trim() != null &&
					!"".equals(etRequest.getText().toString().trim()) && etRequest.getText().toString().trim() != null){
			
				showDialog("", getString(R.string.eat_drug_request), "再检查一下", "确定");
				MobclickAgent.onEvent(mContext,"wy_release",UmengData.wy_release);
			}else{
				showToast("请认真填写完喂药信息");
		}
	}
	
	
	@Override
	public void onConfirm() {
		try {
			List<Attachment> attachments = new ArrayList<Attachment>();
			for(SelectedImage image : dataMap.values()){
				if(image.attachment != null){
					attachments.add(image.attachment);
				}
			}
			showProgressDialog(mContext, "", true, null);
			  long timeStemp = new SimpleDateFormat("yyyy-MM-dd").parse(demandTime.getText().toString()).getTime();
				getService().getFeedManager().addFeedMedicineTask(timeStemp, etRequest.getText().toString().trim(), attachments);
			} catch (ParseException e) {
				 disProgressDialog();
				e.printStackTrace();
			}
	}
	

	@Override
	public void onclickBtn1() {
		super.onclickBtn1();
		// TODO 重试 or 照相
		if(isAddIcon){
			//TODOst
			state = PHOTO;
			IMAGE_FILE_NAME = SysConstants.FILE_DIR_ROOT + System.currentTimeMillis() + ".jpg";
			Intent intentFromCapture = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);  
	        //判断存储卡是否可以用，可用进行存储
			intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(IMAGE_FILE_NAME)));  
	        startActivityForResult(intentFromCapture, state); 
			
		}else{
			dataMap.get(tempPaths.get(index)).attachment.setStatus(0);
			getService().getFileManager().uploadFile(new File(tempPaths.get(index)), ATTACHMENT_TYPE.IMAGE);
			adapter.updateView(dataMap.get(tempPaths.get(index)).attachment);
		}
		
	}
	@Override
	public void onclickBtn2() {
		super.onclickBtn1();
		// TODO 删除  or 选择照片
		if(isAddIcon){
			Utils.getMultiPhoto(HeyReleaseActivity.this, PHOTOSELECT, 9 - tempPaths.size());
			state = PHOTOSELECT;
		}else{
			updateAdapter();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if((position + 1) == imageList.size() + tempPaths.size() && 9 > tempPaths.size()){
			isAddIcon = true;
			showBtnDialog(new String[] { getString(R.string.btn_info_photo),
					getString(R.string.btn_info_photo_album),
					getString(R.string.btn_cancel) });

		}else{
			ArrayList<String> paths = new ArrayList<String>();
			for(String tempImagePath : tempPaths){
				if(dataMap.containsKey(tempImagePath)) {
					paths.add(dataMap.get(tempImagePath).imagePath);
				}
			}
			NewPicActivity.invoke(HeyReleaseActivity.this, paths.get(position), false, paths, true);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (state == PHOTOSELECT) {
				// 选择图片
				List<String> phonePath = data.getStringArrayListExtra("file_next_uris");
				final Map<String, String> tempImageNames = new HashMap<String, String>();
				for(String imagePath : phonePath){
					String tempFileName = UUID.randomUUID().toString();
					String tempImagePath = SysConstants.FILE_upload_ROOT + tempFileName;
					tempPaths.add(tempImagePath);
					tempImageNames.put(imagePath, tempFileName);

					SelectedImage image = new SelectedImage();
					image.imagePath = imagePath;
					dataMap.put(tempImagePath, image);
				}

				updateAdapter();
				new Thread(new Runnable() {
					@Override
					public void run() {
						for(String imagePath : tempImageNames.keySet()){
							String tempFileName = tempImageNames.get(imagePath);
							String tempImagePath = SysConstants.FILE_upload_ROOT + tempFileName;
							Utils.saveBitmap(imagePath,tempFileName, SysConstants.FILE_upload_ROOT, 300);
							Attachment attachment = getService().getFileManager().uploadFile(new File(tempImagePath), ATTACHMENT_TYPE.IMAGE);
							if(dataMap.containsKey(tempImagePath)){
								dataMap.get(tempImagePath).attachment = attachment;
							}
						}
					}
				}).start();
			}else if(state == PHOTO){
				File tempFile = new File(IMAGE_FILE_NAME);
				if(tempFile.length()>0){
					String tempImagePath = SysConstants.FILE_upload_ROOT + tempFile.getName();
					String imagePath = tempFile.getAbsolutePath();
					tempPaths.add(tempImagePath);
					SelectedImage image = new SelectedImage();
					image.imagePath = imagePath;
					dataMap.put(tempImagePath, image);
					Utils.saveBitmap(imagePath,tempFile.getName(), SysConstants.FILE_upload_ROOT,300);
					Attachment attachment = getService().getFileManager().uploadFile(new File(tempImagePath), ATTACHMENT_TYPE.IMAGE);
					dataMap.get(tempImagePath).attachment = attachment;
					updateAdapter();
	             }
			}
		}

	}

	public class MaxLengthWatcher implements TextWatcher {  
	    private int maxLen = 0;  
	    private EditText editText = null;  
	    public MaxLengthWatcher(int maxLen, EditText editText) {  
	        this.maxLen = maxLen;  
	        this.editText = editText;  
	    }  
	  
	    public void afterTextChanged(Editable arg0) {  
	    }  
	  
	    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,  
	            int arg3) {  
	    }  
	  
	    public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {  
	    	surplusNum.setText(s.length()+" ");
	        Editable editable = editText.getText();  
	        int len = editable.length();  
	          
	        if(len >= maxLen)  
	        {  
	        	showToast(getResources().getString(R.string.edit_number_count));
	        }  
	    }  
	  
	}
	
	@Override
	public void updateInfo() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void finish() {
		for(SelectedImage selectedImage : dataMap.values()){
			if(selectedImage.attachment != null &&
			selectedImage.attachment.getStatus() != Constants.ATTACHMENT_STATUS.UPLOAD_COMPLETED){
				selectedImage.attachment.setIsUploadCancel(true);
			}
		}
		super.finish();
	}
	
	class SelectedImage{
		public String imagePath;
		public Attachment attachment;
	}
}
