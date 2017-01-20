package com.xcsd.app.teacher.tea;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
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
import com.tuxing.app.activity.NewPicActivity;
import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.teacher.activity.NoticeInboxActivity;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.DateTimePickDialogUtil.SelectListener;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.UmengData;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.MyGridView;
import com.tuxing.app.view.MyImageView;
import com.tuxing.sdk.db.entity.Department;
import com.tuxing.sdk.event.GetDepartmentMemberEvent;
import com.tuxing.sdk.event.NoticeEvent;
import com.tuxing.sdk.event.UploadFileEvent;
import com.tuxing.sdk.modle.Attachment;
import com.tuxing.sdk.modle.DepartmentMember;
import com.tuxing.sdk.modle.NoticeDepartmentReceiver;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.Constants;
import com.tuxing.sdk.utils.Constants.ATTACHMENT_TYPE;
import com.umeng.analytics.MobclickAgent;
import com.xcsd.rpc.proto.EventType;

import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NoticeReleaseActivity extends BaseActivity implements OnItemClickListener, SelectListener {
    private EditText etRequest;
    private TextView surplusNum;
    private MyGridView iconView;
    private IconAdapter adapter;
    private final static int PHOTO = 104;
    private final static int PHOTOSELECT = 105;
    private final static int CLASSSELECT = 0x106;
    private int state;
    private List<String> phonePath;
    private List<Bitmap> imageList = Collections.synchronizedList(new ArrayList<Bitmap>());
	private List<String> tempPaths = Collections.synchronizedList(new ArrayList<String>());
	private Map<String, SelectedImage> dataMap = new ConcurrentHashMap<String, SelectedImage>();
    private int index = 0;
    private Attachment attachment = null;
    private String TAG = NoticeReleaseActivity.class.getSimpleName();
    private boolean isAddIcon = true;
    private String IMAGE_FILE_NAME;
    private boolean isUpdate = false;
    private LinearLayout ll;
    private List<NoticeDepartmentReceiver> receivers;
    private TextView tv_visible_dept;
    private List<Department> departments;
    private RelativeLayout rl_visible_dept;
    private TextView tv_dept_count;
    private Department currentDepartment;
    private Map<Long, List<DepartmentMember>> allDepartmentMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.notice_release_layout);
        allDepartmentMember = new HashMap<Long, List<DepartmentMember>>();
        receivers = new ArrayList<NoticeDepartmentReceiver>();
        departments = getService().getContactManager().getAllDepartment();
        currentDepartment = new Department();
        Long num = 0l;
        for (Department department : departments) {
            if (department.getType() == Constants.DEPARTMENT_TYPE.GARDEN) {
                getService().getContactManager().getDepartmentMemberCountByUserType(department.getDepartmentId(), Constants.USER_TYPE.TEACHER);
            } else if (department.getType() == Constants.DEPARTMENT_TYPE.CLASS) {
                getService().getContactManager().getDepartmentMemberCountByUserType(department.getDepartmentId(), Constants.USER_TYPE.CHILD);
            }
            if ((user.getPositionId() == 1 || user.getPositionId() == 2)) {
                if(department.getType() == Constants.DEPARTMENT_TYPE.GARDEN){
                    currentDepartment = department;
                    num = getService().getContactManager().getDepartmentMemberCountByUserType(currentDepartment.getDepartmentId(), Constants.USER_TYPE.TEACHER);
                }
            } else {//现在老师只有一个班级??
                if(department.getType() == Constants.DEPARTMENT_TYPE.CLASS){
                    currentDepartment = department;
                    num = getService().getContactManager().getDepartmentMemberCountByUserType(currentDepartment.getDepartmentId(), Constants.USER_TYPE.CHILD);
                }
            }
        }
        NoticeDepartmentReceiver receiver = new NoticeDepartmentReceiver();
        receiver.setDepartmentId(currentDepartment.getDepartmentId());
        receiver.setAll(true);
        receiver.setDepartmentName(currentDepartment.getName());
        receiver.setMemberCount((int) num.longValue());
        receivers.add(receiver);
        initView();
        init();
    }

    public void onEventMainThread(GetDepartmentMemberEvent event) {//得到全部全部的人员
        if (isActivity) {
            List<DepartmentMember> departmentMembers = new ArrayList<DepartmentMember>();
            if (!CollectionUtils.isEmpty(departmentMembers)) {
                allDepartmentMember.put(departmentMembers.get(0).getUser().getClassId(), departmentMembers);
            }
        }
    }

    private void initView() {
        setTitle(getString(R.string.notice));
        setLeftBack("", true, false);
        setRightNext(true, getString(R.string.button_send), 0);
        etRequest = (EditText) findViewById(R.id.et_demand);
        surplusNum = (TextView) findViewById(R.id.hey_surplus_num);
        iconView = (MyGridView) findViewById(R.id.demand_icon_gridview);
        tv_visible_dept = (TextView) findViewById(R.id.tv_visible_dept);
        rl_visible_dept = (RelativeLayout) findViewById(R.id.rl_visible_dept);
        rl_visible_dept.setOnClickListener(this);
        tv_dept_count = (TextView) findViewById(R.id.tv_dept_count);
    }

    private void init() {
        if (currentDepartment.getName()==null){
            tv_visible_dept.setText("未选择");
        }else {
            tv_visible_dept.setText(currentDepartment.getName() + "");
        }
        tv_dept_count.setText(checkNum(receivers) + "人");
        ll = (LinearLayout) findViewById(R.id.ll);
        phonePath = new ArrayList<String>();
        iconView.setOnItemClickListener(this);
        etRequest.addTextChangedListener(new MaxLengthWatcher(500, etRequest));
        imageList.add(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.add_icon));
        surplusNum.setFocusable(true);
        updateAdapter();
        ll.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                //比较Activity根布局与当前布局的大小
                int heightDiff = ll.getRootView().getHeight() - ll.getHeight();
                if (heightDiff > 200) {
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
     *
     * @param event
     */
    public void onEventMainThread(NoticeEvent event) {
        switch (event.getEvent()) {
            case NOTICE_SEND_SUCCESS:
                disProgressDialog();
                showToast("通知发布成功");
                getService().getDataReportManager().reportEvent(EventType.NOTICE);
                Intent intent = new Intent(mContext, NoticeInboxActivity.class);
                intent.putExtra("scoreCount",event.getBonus());
                setResult(Activity.RESULT_OK,intent);
                finish();
                showAndSaveLog(TAG, "发送通知成功  ", false);
                break;
            case NOTICE_SEND_FAILED:
                disProgressDialog();
                showToast(event.getMsg());
                showAndSaveLog(TAG, "发送通知失败  ", false);
                break;

        }
    }


    public void updateAdapter() {
        isUpdate = true;
        if (adapter == null) {
            adapter = new IconAdapter(mContext, imageList);
            iconView.setAdapter(adapter);
        } else {
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
        ImageView imageView;
        TextView load_size;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_visible_dept:
                state = CLASSSELECT;
                Intent intent = new Intent(mContext, ChooseReceiverClassActivity.class);
                intent.putExtra("receivers", (Serializable) receivers);
                startActivityForResult(intent, CLASSSELECT);
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
        List<Attachment> attachments = new ArrayList<Attachment>();
		for(SelectedImage image : dataMap.values()){
			if(image.attachment != null){
				attachments.add(image.attachment);
			}
		}
        if (!TextUtils.isEmpty(etRequest.getText().toString().trim())) {
            if (checkNum(receivers)>0){
                showProgressDialog(mContext, "", true, null);
                getService().getNoticeManager().sendNotice(etRequest.getText().toString().trim(), attachments, receivers);
                MobclickAgent.onEvent(mContext,"send_notice",UmengData.send_notice);
            }else{
                showToast("请选择收件人");
            }
        } else {
            showToast("请填写信息");
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
			Utils.getMultiPhoto(NoticeReleaseActivity.this, PHOTOSELECT, 9 - tempPaths.size());
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
			NewPicActivity.invoke(NoticeReleaseActivity.this, paths.get(position), false, paths, true);
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
                            Utils.saveBitmap(imagePath,tempFileName, SysConstants.FILE_upload_ROOT,300);
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
					Utils.saveBitmap(imagePath, tempFile.getName(),SysConstants.FILE_upload_ROOT,300);
					Attachment attachment = getService().getFileManager().uploadFile(new File(tempImagePath), ATTACHMENT_TYPE.IMAGE);
					dataMap.get(tempImagePath).attachment = attachment;
					updateAdapter();
	             }
            } else if (state == CLASSSELECT && data != null) {
                receivers = (List<NoticeDepartmentReceiver>) data.getSerializableExtra("receivers");
                StringBuilder receiverText = new StringBuilder("");
                for (NoticeDepartmentReceiver receiver : receivers) {
                    for (Department department : departments) {
                        if (receiver.getDepartmentId() == department.getDepartmentId()) {
                            receiverText.append(department.getName());
                            receiverText.append(",");
                        }
                    }
                }
                String text = receiverText.length() > 0 ? receiverText.substring(0, receiverText.length() - 1) : "";
                tv_visible_dept.setText(text);
                tv_dept_count.setText(checkNum(receivers) + "人");
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
            surplusNum.setText(s.length() + " ");
            Editable editable = editText.getText();
            int len = editable.length();

            if (len >= maxLen) {
            	showToast(getResources().getString(R.string.edit_number_count));
            }
        }

    }

    public AnimationDrawable getAnimation(final ImageView imageView) {
        // TODO Auto-generated method stub
        AnimationDrawable spinner = (AnimationDrawable) imageView.getBackground();
        return spinner;
    }

    @Override
    public void updateInfo() {

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
    private int checkNum(List<NoticeDepartmentReceiver> receiversList) {
        int count = 0;
        for (NoticeDepartmentReceiver noticeDepartmentReceiver : receiversList) {
            if (noticeDepartmentReceiver.isAll()) {//班级全选
                for (Department depart : departments) {
                    if (noticeDepartmentReceiver.getDepartmentId() == depart.getDepartmentId()) {
                        if (depart.getType() == Constants.DEPARTMENT_TYPE.SCHOOL) {//老师班级
//                        if (depart.getType() == Constants.DEPARTMENT_TYPE.GARDEN) {//老师班级
                            Long num = getService().getContactManager().getDepartmentMemberCountByUserType(depart.getDepartmentId(), Constants.USER_TYPE.TEACHER);
                            count = count + (int) num.longValue()-1;
                        } else if (depart.getType() == Constants.DEPARTMENT_TYPE.CLASS) {//幼儿班级
                            Long num = getService().getContactManager().getDepartmentMemberCountByUserType(depart.getDepartmentId(), Constants.USER_TYPE.CHILD);
                            count = count + (int) num.longValue();
                        } else if (depart.getType() == Constants.DEPARTMENT_TYPE.GARDEN) {//幼儿班级
                            Long num = getService().getContactManager().getDepartmentMemberCountByUserType(depart.getDepartmentId(), Constants.USER_TYPE.TEACHER);
                            count = count + (int) num.longValue()-1;
                        }
                    }
                }
            } else {//不是全选
                count = count + noticeDepartmentReceiver.getMemberUserIds().size();
            }
        }
        return count;
    }


}
