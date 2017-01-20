package com.tuxing.app.qzq;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.activity.NewPicActivity;
import com.tuxing.app.activity.PicListActivity;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.util.ImageUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.UmengData;
import com.tuxing.app.util.Utils;
import com.tuxing.app.util.ViewUtils;
import com.tuxing.app.view.DialogView;
import com.tuxing.app.view.MyGridView;
import com.tuxing.app.view.MyImageView;
import com.tuxing.sdk.db.entity.Department;
import com.tuxing.sdk.db.entity.UploadFile;
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

public class CircleReleaseActivity extends BaseActivity implements OnItemClickListener {
	private EditText etRequest;
	private TextView surplusNum;
	private MyGridView iconView;
	private ImageGridAdapter adapter;
	private final static int PHOTO = 104;
	private final static int PHOTOSELECT = 105;
	private final static int DEPATSELECT = 0x106;
	private int state;
	private SelectedImage iconAdd;
    private List<SelectedImage> imageList = Collections.synchronizedList(new ArrayList<SelectedImage>());

	private String TAG = CircleReleaseActivity.class.getSimpleName();
	private String IMAGE_FILE_NAME;
	private  RelativeLayout rl_visible_dept;
	private  List<Department> deptTempList = new ArrayList<Department>();
	private  List<Department> deptList = new ArrayList<Department>();
	private TextView tv_visible_dept;
	private TextView tv_visible_label;
	private List<Long> departmentIds;
	private String contentString;
	private List<Attachment> attachmentList;
	private ToggleButton tb_class_picture_button;
	private RelativeLayout rl_class_picture;

    private RelativeLayout rl_sysnc_growup;
    private CheckBox sysc_growup_btn;
    private int max = 20;
    private String isFrom = "";
    private int flag; // 0 拍照 1 选择

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentLayout(R.layout.circle_release_layout);
		setContentLayout(R.layout.circle_release_pic_layout);
		isFrom = getIntent().getStringExtra("isFrom");
        flag = getIntent().getIntExtra("flag", 0);
		deptList = getService().getContactManager().getAllDepartment();

        if(flag == 0){
            onclickBtn1();
        }else{
            state = PHOTOSELECT;
            Intent intent = new Intent(mContext, PicListActivity.class);
            intent.putExtra("max", max);
            startActivityForResult(intent, PHOTOSELECT);

        }



        init();
	}

	private void init() {
		setTitle(getString(R.string.tab_circle));
		setLeftBack("", true, false);
		setRightNext(true, getString(R.string.button_send), 0, false);
		tv_visible_dept = (TextView)findViewById(R.id.tv_visible_dept);
		tv_visible_label = (TextView)findViewById(R.id.tv_visible_label);
		rl_visible_dept = (RelativeLayout)findViewById(R.id.rl_visible_dept);
		rl_visible_dept.setOnClickListener(this);

        sysc_growup_btn = (CheckBox) findViewById(R.id.sysc_growup_btn);
        rl_sysnc_growup = (RelativeLayout)findViewById(R.id.rl_sysnc_growup);

		if(deptList!=null&&deptList.size()==1)
			rl_visible_dept.setVisibility(View.GONE);
		rl_class_picture = (RelativeLayout)findViewById(R.id.rl_class_picture);
		if(TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())){
            sysc_growup_btn.setChecked(true);
			rl_class_picture.setVisibility(View.GONE);
            rl_sysnc_growup.setVisibility(View.GONE);
		}else{
			rl_class_picture.setVisibility(View.VISIBLE);
            rl_sysnc_growup.setVisibility(View.GONE);
		}
		etRequest = (EditText) findViewById(R.id.et_demand);
		surplusNum = (TextView) findViewById(R.id.hey_surplus_num);
		iconView = (MyGridView) findViewById(R.id.demand_icon_gridview);
		iconView.setOnItemClickListener(this);
		etRequest.addTextChangedListener(new MaxLengthWatcher(500, etRequest));
		tb_class_picture_button = (ToggleButton)findViewById(R.id.tb_class_picture_button);
		tb_class_picture_button.setChecked(true);
		tb_class_picture_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                tb_class_picture_button.setChecked(b);
            }
        });
        iconAdd = new SelectedImage(null, BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.add_icon), null, false,true);
		imageList.add(iconAdd);
		surplusNum.setFocusable(true);
		updateAdapter();

        sysc_growup_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sysc_growup_btn.setChecked(b);
            }
        });

	}


	public void onEventMainThread(UploadFileEvent event){
        switch (event.getEvent()){
            case UPLOAD_COMPETED:
                    adapter.notifyDataSetChanged();
                    if(event.getAttachment() != null){
                        showAndSaveLog(TAG, "上传图片成功" + event.getAttachment().getFileUrl(), false);
                    }
                break;
            case UPLOAD_FAILED:
                adapter.notifyDataSetChanged();
                showAndSaveLog(TAG, "上传图片失败" + event.getMsg(), false);
                break;
            case UPLOAD_PROGRESS_UPDATED:
                adapter.notifyDataSetChanged();
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
				disProgressDialog();
				//TODO 退到home
                getService().getDataReportManager().reportEvent(EventType.FEED);
				showAndSaveLog(TAG, "发布成功  " , false);
				Intent intent = new Intent(TuxingApp.packageName + SysConstants.UPDATECIRCLELIST);
				intent.putExtra("isFrom",isFrom);
				intent.putExtra("scoreCount",event.getBonus());
				sendBroadcast(intent);
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
		if(adapter == null){
			adapter = new ImageGridAdapter(imageList);
			iconView.setAdapter(adapter);
		}else{
			adapter.notifyDataSetChanged();
		}
	}


    public class ImageGridAdapter extends BaseAdapter {
        private List<SelectedImage> data;

        public ImageGridAdapter(List<SelectedImage> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(CircleReleaseActivity.this).inflate(R.layout.grideview_circle_item_layout, null);
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
            viewHolder.faildRl.setTag(position);

            final SelectedImage image = data.get(position);
            if (image == null || StringUtils.isBlank(image.imagePath) && image.bitmap == null) {
                return convertView;
            }

//            if (image.bitmap == null) {
//                image.bitmap = Utils.revitionImageSize(image.imagePath,SysConstants.IMAGEIMPLESIZE_256);
//            }
//
//            viewHolder.icon.setImageBitmap(image.bitmap);
//            if (image.deletable) {
//                viewHolder.delIcon.setVisibility(View.VISIBLE);
//            }else{
//                viewHolder.delIcon.setVisibility(View.GONE);
//            }

            if(!TextUtils.isEmpty(image.imagePath)){
                String imgUrl = "file://" + image.imagePath;
                ImageLoader.getInstance().displayImage(imgUrl, viewHolder.icon, ImageUtils.DIO_UPLOAD);
            }else{
                ImageLoader.getInstance().displayImage(image.imagePath, viewHolder.icon, ImageUtils.DIO_UPLOAD);
            }
            if (image.deletable) {
                viewHolder.delIcon.setVisibility(View.VISIBLE);
            }else{
                viewHolder.delIcon.setVisibility(View.GONE);
            }

            if (image.attachment != null) {
                switch (image.attachment.getStatus()) {
                    case Constants.ATTACHMENT_STATUS.UPLOAD_COMPLETED:
                        viewHolder.progressRl.setVisibility(View.GONE);
                        viewHolder.faildRl.setVisibility(View.GONE);
                        break;
                    case Constants.ATTACHMENT_STATUS.UPLOAD_FAILED:
                        viewHolder.faildRl.setVisibility(View.VISIBLE);
                        viewHolder.progressRl.setVisibility(View.GONE);
                        break;
                    case Constants.ATTACHMENT_STATUS.NOT_UPLOAD:
                    case Constants.ATTACHMENT_STATUS.UPLOAD_IN_PROGRESS:
                        viewHolder.progressRl.setVisibility(View.VISIBLE);
                        viewHolder.faildRl.setVisibility(View.GONE);
                        if (image.attachment.getProgress() != null) {
                            viewHolder.load_size.setText((image.attachment.getProgress() + "%"));
                        }
                        break;
                }
            } else {
                viewHolder.faildRl.setVisibility(View.GONE);
                if (image.deletable) {
                    viewHolder.progressRl.setVisibility(View.VISIBLE);
                }else{
                    viewHolder.progressRl.setVisibility(View.GONE);
                }
            }

            viewHolder.delIcon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.size() > position) {
                        if(data.size() == 9 && data.get(data.size() - 1).deletable){
                            data.add(iconAdd);
                        }

                        SelectedImage deletedImage = data.remove(position);
                        if (deletedImage != null && deletedImage.attachment != null) {
                            deletedImage.attachment.setIsUploadCancel(true);
                        }

                        notifyDataSetChanged();
                    }
                }
            });

            viewHolder.faildRl.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int index = (Integer) v.getTag();
                    DialogView.setBtnNames(new String[]{
                            getResources().getString(R.string.btn_retry),
                            getResources().getString(R.string.delete),
                            getResources().getString(R.string.cancel)});
                    final DialogView mDialog = new DialogView(mContext);
                    mDialog.setCancelable(true);
                    mDialog.setCanceledOnTouchOutside(true);
                    mDialog.setOnDialogItemClick(new DialogView.OnDialogItemClick() {
                        @Override
                        public void OnDialogItemClick(int viewId) {
                            if (viewId == R.id.btn_dialog1) {
                                if (image != null && image.attachment != null) {
                                    image.attachment.setProgress(0);
                                    if(!data.get(index).isCompressSuccess){
                                        image.attachment.setLocalFilePath(data.get(index).imagePath);
                                    }
                                    image.attachment.setIsUploadCancel(false);
                                    image.attachment.setStatus(Constants.ATTACHMENT_STATUS.NOT_UPLOAD);
                                    getService().getFileManager().uploadFile(image.attachment);
                                    notifyDataSetChanged();
                                }

                                if (mDialog != null && mDialog.isShowing()) {
                                    mDialog.dismiss();
                                }
                            } else if (viewId == R.id.btn_dialog2) {
                                if (data.size() > position) {
                                    if(data.size() == max && data.get(data.size() - 1).deletable){
                                        data.add(iconAdd);
                                    }
                                    data.remove(position);
                                    notifyDataSetChanged();
                                }

                                if (mDialog != null && mDialog.isShowing()) {
                                    mDialog.dismiss();
                                }
                            } else if (viewId == R.id.btn_dialog3) {
                                if (mDialog != null && mDialog.isShowing()) {
                                    mDialog.dismiss();
                                }
                            }
                        }
                    });
                    mDialog.show();

                }
            });

            return convertView;
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
		if(v.getId() == R.id.rl_visible_dept){
			state = DEPATSELECT;
			Intent intent = new Intent(mContext,SelectCircleReceiverClassActivity.class);
			if(tv_visible_dept.getVisibility() == View.VISIBLE){
				intent.putExtra("deptTempList",(Serializable)deptList);
			}else {
                intent.putExtra("deptTempList", (Serializable) deptTempList);
            }

            startActivityForResult(intent, DEPATSELECT);

		}
	}

	@Override
	public void onclickRightNext() {
        super.onclickRightNext();
        for (SelectedImage image : imageList) {
            if (image.deletable == true && (image.attachment == null ||
                    image.attachment.getStatus() != Constants.ATTACHMENT_STATUS.UPLOAD_COMPLETED)) {
                showToast("图片正在上传或上传失败");
                return;
            }
        }
        if (ViewUtils.isFirstClick()) {
            String content = etRequest.getText().toString().trim();
            if (imageList.size() > 1 || !StringUtils.isBlank(content)) {
                try {
                    List<Long> deptIdList = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(deptList) && deptList.size() == 1) {
                        deptIdList.add(deptList.get(0).getDepartmentId());
                    } else {
                        if (tv_visible_dept.getVisibility() == View.VISIBLE) {
                            for (int i = 0; i < deptList.size(); i++) {
                                deptIdList.add(deptList.get(i).getDepartmentId());
                            }
                        } else {
                            for (int i = 0; i < deptTempList.size(); i++) {
                                deptIdList.add(deptTempList.get(i).getDepartmentId());
                            }
                        }
                    }

                    List<Attachment> attachments = new ArrayList<Attachment>();
                    for (SelectedImage image : imageList) {
                        if (image.attachment != null && image.attachment.getStatus()
                                == Constants.ATTACHMENT_STATUS.UPLOAD_COMPLETED) {
                            attachments.add(image.attachment);
                        }
                    }

                    departmentIds = deptIdList;
                    contentString = content;
                    attachmentList = attachments;
                    showProgressDialog(mContext, "", false, null);
                    if (!TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion()) || CollectionUtils.isEmpty(attachments)) {
                        if (!CollectionUtils.isEmpty(attachments)) {
//                            getService().getFeedManager().publishFeed(deptIdList, content, attachments, false, sysc_growup_btn.isChecked());
                            getService().getFeedManager().publishFeed(deptIdList, content, attachments, false);
                        } else {
                            getService().getFeedManager().publishFeed(deptIdList, content, attachments, false);
                        }
                    } else {
                        if (tb_class_picture_button.isChecked()) {
                            getService().getFeedManager().publishFeed(departmentIds, contentString, attachmentList, true);
                        } else
                            getService().getFeedManager().publishFeed(departmentIds, contentString, attachmentList, false);
                    }

                    if (attachments != null && attachments.size() > 0) {
                        MobclickAgent.onEvent(mContext, "qzq_release_is_pic", UmengData.qzq_release_is_pic);
                    } else {
                        MobclickAgent.onEvent(mContext, "qzq_release_no_pic", UmengData.qzq_release_no_pic);
                    }
                } catch (Exception e) {
                    disProgressDialog();
                    e.printStackTrace();
                }
            } else {
                showToast("请输入内容");
            }
        }
    }

	@Override
	public void onclickBtn1() {
        state = PHOTO;
        IMAGE_FILE_NAME = Utils.getTakePhonePath() + System.currentTimeMillis() + ".jpg";
        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断存储卡是否可以用，可用进行存储
        intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(IMAGE_FILE_NAME)));
        startActivityForResult(intentFromCapture, state);
        MobclickAgent.onEvent(mContext,"qzq_release_pic_from_camera",UmengData.qzq_release_pic_from_camera);
	}

        @Override
        public void onclickBtn2 () {
            super.onclickBtn1();
            int imageCount = imageList.get(imageList.size() - 1).deletable ? imageList.size() : imageList.size() - 1;
            Utils.getMultiPhoto(CircleReleaseActivity.this, PHOTOSELECT, 9 - imageCount);
            state = PHOTOSELECT;
            MobclickAgent.onEvent(mContext, "qzq_release_pic_from_pic", UmengData.qzq_release_pic_from_pic);

        }

        @Override
        public void onItemClick (AdapterView < ? > parent, View view,int position, long id){
            if (position < imageList.size() && !imageList.get(position).deletable) {
                showBtnDialog(new String[]{getString(R.string.btn_info_photo),
                        getString(R.string.btn_info_photo_album),
                        getString(R.string.btn_cancel)});

            } else {
                ArrayList<String> imagePathList = new ArrayList<String>();
                for (SelectedImage image : imageList) {
                    if (image.deletable) {
                        imagePathList.add(image.imagePath);
                    }
                }
                if (position < imagePathList.size()) {
                    NewPicActivity.invoke(CircleReleaseActivity.this, imagePathList.get(position), false, imagePathList);
                }
            }
        }

        @Override
        protected void onActivityResult ( int requestCode, int resultCode, Intent data){
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                if (state == PHOTOSELECT || state == PHOTO) {
                    if (state == PHOTO) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    setRightNext(true, getString(R.string.button_send), 0, true);
                    // 选择图片
                    List<UploadFile> phonePath = new ArrayList<>();
                    if (state == PHOTOSELECT) {
                        List<UploadFile> picPhotos = (ArrayList<UploadFile>) data.getSerializableExtra("file_next_uris");
                        if (!CollectionUtils.isEmpty(picPhotos)) {
                            phonePath.addAll(picPhotos);
                        }
                    } else {
                        UploadFile photo = new UploadFile(IMAGE_FILE_NAME, System.currentTimeMillis());
                        phonePath.add(photo);
                    }

                    final List<Pair<String, Attachment>> uploadImages = new ArrayList<Pair<String, Attachment>>();

                    for (UploadFile photo : phonePath) {
//                    for (int i=0; i< phonePath.size();i++) {
//                        String url = phonePath.get(i).url;
                        String fileKey = UUID.randomUUID().toString();
                        String uploadFilePath = SysConstants.FILE_upload_ROOT + fileKey;

                        Attachment attachment = new Attachment();
                        attachment.setFileUrl(fileKey);
                        attachment.setLocalFilePath(uploadFilePath);
                        attachment.setType(ATTACHMENT_TYPE.IMAGE);
                        attachment.setStatus(Constants.ATTACHMENT_STATUS.NOT_UPLOAD);
                        attachment.setProgress(0);
                        attachment.setCreatOn(photo.createOn);
                        attachment.setIsUploadCancel(false);

                        uploadImages.add(Pair.create(photo.url, attachment));

                        SelectedImage image = new SelectedImage(photo.url, null, attachment, true, true);
                        imageList.add(imageList.size() - 1, image);
                    }

                    while (imageList.size() > 9) {
                        imageList.remove(imageList.size() - 1);
                    }

                    updateAdapter();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
//                        for(Pair<String, Attachment> uploadImage : uploadImages){
                            for (int i = 0; i < uploadImages.size(); i++) {
                                Pair<String, Attachment> uploadImage = uploadImages.get(i);
                                String srcImagePath = uploadImage.first;
                                Attachment attachment = uploadImage.second;
                                boolean isSuccess = Utils.saveBitmap(srcImagePath, attachment.getFileUrl(), SysConstants.FILE_upload_ROOT, 300);
                                if (!isSuccess) {
                                    if (imageList.get(i).imagePath.equals(srcImagePath)) {
                                        imageList.get(i).isCompressSuccess = false;
                                    }
                                }
                                getService().getFileManager().uploadFile(attachment);
                            }

                        }
                    }).start();
                } else if (state == DEPATSELECT) {//选择班级
                    if (data != null) {
                        deptTempList = (List<Department>) data.getSerializableExtra("depts");
                        StringBuilder name = new StringBuilder("");
                        name.append("可见人员 :  ");
                        if (deptTempList.size() > 0) {
                            for (Department de : deptTempList) {
                                name.append(de.getName());
                                name.append(",");
                            }
                            tv_visible_label.setText(name.substring(0, name.length() - 1));
                            tv_visible_dept.setVisibility(View.GONE);
                        }
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
                if ((imageList != null && imageList.size() > 1) ||
                        !TextUtils.isEmpty(editText.getText().toString().trim())) {
                    setRightNext(true, getString(R.string.button_send), 0, true);
                } else
                    setRightNext(true, getString(R.string.button_send), 0, false);
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

        @Override
        public void finish () {
            //如果还有没有上传完的图片，取消上传
            for (SelectedImage selectedImage : imageList) {
                if (selectedImage.attachment != null &&
                        selectedImage.attachment.getStatus() != Constants.ATTACHMENT_STATUS.UPLOAD_COMPLETED) {
                    selectedImage.attachment.setIsUploadCancel(true);
                }
            }

            super.finish();
        }


        class SelectedImage {
            public String imagePath;
            public Bitmap bitmap;
            public Attachment attachment;
            public boolean deletable;
            public boolean isCompressSuccess;

            public SelectedImage(String imagePath, Bitmap bitmap, Attachment attachment, boolean deletable, boolean isCompressSuccess) {
                this.imagePath = imagePath;
                this.bitmap = bitmap;
                this.attachment = attachment;
                this.deletable = deletable;
                this.isCompressSuccess = isCompressSuccess;
            }
        }
}
