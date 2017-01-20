package com.tuxing.app.qzq;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.easemob.util.ImageUtils;
import com.squareup.okhttp.internal.Util;
import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.activity.MediaRecorderActivity;
import com.tuxing.app.activity.NewPicActivity;
import com.tuxing.app.activity.SelectVideoActivity;
import com.tuxing.app.activity.VideoPlayerActivity;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.util.FileUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.UmengData;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.MyGridView;
import com.tuxing.app.view.MyImageView;
import com.tuxing.sdk.db.entity.Department;
import com.tuxing.sdk.event.FeedEvent;
import com.tuxing.sdk.event.UploadFileEvent;
import com.tuxing.sdk.modle.Attachment;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.Constants;
import com.tuxing.sdk.utils.Constants.ATTACHMENT_TYPE;
import com.tuxing.sdk.utils.StringUtils;
import com.umeng.analytics.MobclickAgent;
import com.xcsd.rpc.proto.EventType;

import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CircleViedeoReleaseActivity extends BaseActivity implements OnItemClickListener {
	private EditText etRequest;
	private TextView surplusNum;
	private MyGridView iconView;
	private IconAdapter adapter;
	private final static int DEPATSELECT = 0x106;
	public static final int RECORD_VIDEO = 107;
	public static final int SELECT_VIDEO = 108;
	private int state;
	private List<String> tempPaths = Collections.synchronizedList(new ArrayList<String>());
	private Map<String, SelectedImage> dataMap = new ConcurrentHashMap<String, SelectedImage>();
	private int index = 0;
	private String TAG = CircleViedeoReleaseActivity.class.getSimpleName();
	private String IMAGE_FILE_NAME = SysConstants.FILE_DIR_ROOT + System.currentTimeMillis() + ".jpg";
	private  RelativeLayout rl_visible_dept;
	private  RelativeLayout rl_msg;

	private  List<Department> deptTempList = new ArrayList<Department>();
	private  List<Department> deptList = new ArrayList<Department>();
	private TextView tv_visible_dept;
	private TextView tv_visible_label;
	public boolean isUpdate = false;
	private LinearLayout ll;
	private String isFrom = "";

	private long createOn = 0;
	private RelativeLayout rl_sysnc_growup;
	private CheckBox sysc_growup_btn;
	private RelativeLayout rl_class_picture;
	private ToggleButton tb_class_picture_button;
	private int flag;// 0 拍视频  1 选择视频

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.circle_release_layout);
		isFrom = getIntent().getStringExtra("isFrom");

		flag = getIntent().getIntExtra("flag", 0);

		deptList = getService().getContactManager().getAllDepartment();
		init();
//		recordVideo();
		if(flag == 0){
			recordVideo();
		}else{
			// 选择视频
			Intent intent = new Intent(mContext, SelectVideoActivity.class);
			intent.putExtra("isVideo",true);
			startActivityForResult(intent, SELECT_VIDEO);
		}
	}

	private void init() {
		setTitle(getString(R.string.tab_circle));
		setLeftBack("", true, false);
		setRightNext(true, getString(R.string.button_send), 0, false);
		tv_visible_dept = (TextView)findViewById(R.id.tv_visible_dept);
		tv_visible_label = (TextView)findViewById(R.id.tv_visible_label);
		rl_visible_dept = (RelativeLayout)findViewById(R.id.rl_visible_dept);
		rl_msg = (RelativeLayout)findViewById(R.id.rl_msg);
		rl_msg.setVisibility(View.GONE);
		rl_visible_dept.setOnClickListener(this);
		if(deptList!=null&&deptList.size()==1)
			rl_visible_dept.setVisibility(View.GONE);
		etRequest = (EditText) findViewById(R.id.et_demand);
		surplusNum = (TextView) findViewById(R.id.hey_surplus_num);
		iconView = (MyGridView) findViewById(R.id.demand_icon_gridview);
		ll = (LinearLayout) findViewById(R.id.ll);
		iconView.setOnItemClickListener(this);
		etRequest.addTextChangedListener(new MaxLengthWatcher(500, etRequest));

		surplusNum.setFocusable(true);
		updateAdapter();
		ll.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener(){

		    @Override
		    public void onGlobalLayout(){
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
		            		showAndSaveLog(TAG, "上传视频成功" + event.getAttachment().getFileUrl(), false);
		            	}
	                break;
	            case UPLOAD_FAILED:
	            	 adapter.updateView(event.getAttachment());
	            	showAndSaveLog(TAG, "上传视频失败" + event.getMsg(), false);
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
	public void onEventMainThread(FeedEvent event){
		switch (event.getEvent()) {
			case SEND_FEED_SUCCESS:
				deptTempList.clear();

				//TODO 退到home
				showAndSaveLog(TAG, "发布成功  ", false);
				getService().getDataReportManager().reportEvent(EventType.FEED);
				Intent intent = new Intent(TuxingApp.packageName + SysConstants.UPDATECIRCLELIST);
				intent.putExtra("isFrom",isFrom);
				intent.putExtra("scoreCount",event.getBonus());
				sendBroadcast(intent);
				String folder = tempPaths.get(0).split("/")[tempPaths.get(0).split("/").length - 2];
				String saveKey = Utils.getVideoToken( Constants.QI_NIU_DOMAIN + dataMap.get(tempPaths.get(0)).attachment.getFileUrl());
				Utils.copyFile(tempPaths.get(0),SysConstants.FILE_DIR_VIDEO + saveKey);
				FileUtils.DeleteFolder(SysConstants.FILE_DIR_VIDEO + folder);
				disProgressDialog();
				finish();
				break;
			case SEND_FEED_FAILED:
				showToast(event.getMsg());
				disProgressDialog();
				showAndSaveLog(TAG, "发布失败  ", false);
				break;
		}
	}

	public void updateAdapter(){
		isUpdate = true;
		if(adapter == null){
			adapter = new IconAdapter(mContext, tempPaths);
			iconView.setAdapter(adapter);
		}else{
			adapter.notifyDataSetChanged();
		}
	}

	public class IconAdapter extends BaseAdapter {
		private Context context;
		private List<String> list;
		MediaMetadataRetriever media;
		public IconAdapter(Context context, List<String> list) {
			this.context = context;
			this.list = tempPaths;
			media = new MediaMetadataRetriever();
		}

		@Override
		public int getCount() {
			return tempPaths.size();
		}

		@Override
		public Object getItem(int position) {
			return tempPaths.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}



		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.grideview_circle_item_layout, null);
				viewHolder = new ViewHolder();
				viewHolder.icon = (MyImageView) convertView.findViewById(R.id.grid_item1_icon);
				viewHolder.delIcon = (ImageView) convertView.findViewById(R.id.gride_imtm1_faild_del);
				viewHolder.progressRl = (RelativeLayout) convertView.findViewById(R.id.gride_imtm1_progress_rl);
				viewHolder.faildRl = (RelativeLayout) convertView.findViewById(R.id.gride_imtm1_faile_rl);
				viewHolder.load_size = (TextView) convertView.findViewById(R.id.load_size);
				viewHolder.status_btn = (LinearLayout)convertView.findViewById(R.id.ll_status_btn);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if(isUpdate){
				if(iconView.getChildCount() == position){
						 String tempImagePath = tempPaths.get(position);
						 SelectedImage image = dataMap.get(tempImagePath);
						 if(image != null) {
							 viewHolder.delIcon.setVisibility(View.GONE);
							 viewHolder.progressRl.setVisibility(View.VISIBLE);
							 media.setDataSource(tempImagePath);
							 viewHolder.icon.setImageBitmap(media.getFrameAtTime());
							 if(image.attachment != null) {
								 if (image.attachment.getStatus() == 5) {
									 //成功
									 viewHolder.status_btn.setVisibility(View.VISIBLE);
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
				}
				
			}


			viewHolder.faildRl.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					index = position;
					showBtnDialog(new String[] {"重试",getString(R.string.btn_delete) });
				}
			});

			return convertView;
		}
		
		private void updateView(Attachment data){
			if(data != null && !data.getIsUploadCancel()){
				dataMap.clear();
				SelectedImage image = new SelectedImage();
				image.imagePath = data.getLocalFilePath();
				image.attachment = data;
				dataMap.put(data.getLocalFilePath(), image);
			int index = tempPaths.indexOf(data.getLocalFilePath());
			View view = iconView.getChildAt(index);
			ViewHolder holder = (ViewHolder)view.getTag();
			
			holder.load_size.setText((data.getProgress() + "%"));
			if(dataMap.containsKey(data.getLocalFilePath())){
				holder.delIcon.setVisibility(View.GONE);
			}else{
				holder.delIcon.setVisibility(View.GONE);
			}
			 if(dataMap.size() > 0){
				 if(data.getStatus() == 5){
					 //成功
					 dataMap.get(data.getLocalFilePath()).attachment.setStatus(5);
					 holder.progressRl.setVisibility(View.GONE);
					 holder.faildRl.setVisibility(View.GONE);
					 setRightNext(true, getString(R.string.button_send), 0, true);
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
		LinearLayout status_btn;
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if(v.getId() == R.id.rl_visible_dept){
			state = DEPATSELECT;
			Intent intent = new Intent(mContext,SelectCircleReceiverClassActivity.class);
			if(tv_visible_dept.getVisibility() == View.VISIBLE){
				intent.putExtra("deptTempList",(Serializable)deptList);
			}else
			intent.putExtra("deptTempList",(Serializable)deptTempList);
			startActivityForResult(intent, DEPATSELECT);
		}
	}

	@Override
	public void onclickRightNext() {
		super.onclickRightNext();
		for(SelectedImage image : dataMap.values()){
			if(image.attachment == null || image.attachment.getStatus() != 5){
				showToast("视频正在上传");
				return;
			}
		}

		String content = etRequest.getText().toString().trim();
		if(!CollectionUtils.isEmpty(tempPaths) || !StringUtils.isBlank(content)){
			try {
				showProgressDialog(mContext, "", false, null);
				List<Long>  deptIdList = new ArrayList<Long>();
				if(!CollectionUtils.isEmpty(deptList)&&deptList.size()==1){
					deptIdList.add(deptList.get(0).getDepartmentId());
				}else{
					if(tv_visible_dept.getVisibility() == View.VISIBLE){
						for(int i =0;i<deptList.size();i++){
							deptIdList.add(deptList.get(i).getDepartmentId());
						}
					}else{
						for(int i =0;i<deptTempList.size();i++){
							deptIdList.add(deptTempList.get(i).getDepartmentId());
						}
					}
				}

				List<Attachment> attachments = new ArrayList<Attachment>();
				for(SelectedImage image : dataMap.values()){
					if(image.attachment != null){
						attachments.add(image.attachment);
					}
				}
				getService().getFeedManager().publishFeed(deptIdList, content, attachments,false);
				if(attachments != null && attachments.size() > 0){
					MobclickAgent.onEvent(mContext,"qzq_release_is_pic",UmengData.qzq_release_is_pic);
				}else{
					MobclickAgent.onEvent(mContext,"qzq_release_no_pic",UmengData.qzq_release_no_pic);
				}
			} catch (Exception e) {
				disProgressDialog();
				e.printStackTrace();
			}
		}else{
			showToast("请输入内容");
		}
	}



	@Override
	public void onclickBtn1() {
		super.onclickBtn1();

			dataMap.get(tempPaths.get(index)).attachment.setStatus(0);
			getService().getFileManager().uploadFile(new File(tempPaths.get(index)), ATTACHMENT_TYPE.VIDEO);
			adapter.updateView(dataMap.get(tempPaths.get(index)).attachment);


	}
	@Override
	public void onclickBtn2() {
		super.onclickBtn1();

			updateAdapter();

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
		Intent intent = new Intent(this, VideoPlayerActivity.class);
		intent.putExtra("localpath",tempPaths.get(0));
		startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if(state == DEPATSELECT){//选择班级
				if(data!=null){
					deptTempList = (List<Department>)data.getSerializableExtra("depts");
					StringBuilder name = new StringBuilder("");
					name.append("可见人员 :  ");
					if(deptTempList.size()>0){
						for(Department de:deptTempList){
							name.append(de.getName());
							name.append(",");
						}
						tv_visible_label.setText(name.substring(0,name.length()-1));
						tv_visible_dept.setVisibility(View.GONE);
//						tv_visible_dept.setText(name.substring(0,name.length()-1));
					}
				}
			}else if(requestCode == SELECT_VIDEO){
				//接受到视频
				if(data != null){
					String videoPath = data.getStringExtra("videoPath");
					dataMap.clear();
					if (new File(videoPath).exists()) {
						tempPaths.add(videoPath);
						SelectedImage image = new SelectedImage();
						image.imagePath = videoPath;
						dataMap.put(videoPath, image);
						updateAdapter();
						getService().getFileManager().uploadFile(new File(videoPath), ATTACHMENT_TYPE.VIDEO);
					}
				}else{
					finish();
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
			if ((dataMap!=null&&!CollectionUtils.isEmpty(dataMap.values()))||!TextUtils.isEmpty(editText.getText().toString().trim())) {
				setRightNext(true, getString(R.string.button_send), 0, true);
			} else
				setRightNext(true,getString(R.string.button_send), 0, false);
		}

		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
									  int arg3) {
		}

		public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
			surplusNum.setText(s.length()+" ");
			Editable editable = editText.getText();
			int len = editable.length();

			if(len >= maxLen){
				showToast(getResources().getString(R.string.edit_number_count));
			}
		}

	}

	@Override
	public void finish(){
		//如果还有没有上传完的图片，取消上传
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

	public void recordVideo(){
		Intent intent = new Intent(this,MediaRecorderActivity.class);
		startActivityForResult(intent, SELECT_VIDEO);


	}


}
