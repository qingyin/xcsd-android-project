package com.xcsd.app.teacher.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tuxing.app.activity.NewPicActivity;
import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.ImageUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.MyGridView;
import com.tuxing.sdk.event.UploadFileEvent;
import com.tuxing.sdk.modle.Attachment;
import com.tuxing.sdk.modle.QuestionTag;
import com.tuxing.sdk.utils.Constants;
import com.tuxing.sdk.utils.Constants.ATTACHMENT_TYPE;
import com.tuxing.sdk.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class QuestionReleaseActivity extends BaseActivity implements OnItemClickListener {
	private EditText etContent;
	private MyGridView iconView;
	private ImageGridAdapter adapter;
	private final static int PHOTO = 104;
	private final static int PHOTOSELECT = 105;
	private final static int QUESTIONSEL = 106;
	private int state;
	private SelectedImage iconAdd;
    private List<SelectedImage> imageList = Collections.synchronizedList(new ArrayList<SelectedImage>());

	private String TAG = QuestionReleaseActivity.class.getSimpleName();
	private String IMAGE_FILE_NAME;

    private Long expertId = null;
    private QuestionTag questionTag;
    private boolean isQuesFragement = false;
    private String questionTitle;
    private ReplaceReceiver questionReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.question_release_layout);
        questionTitle = getIntent().getStringExtra("questionTitle");
        questionTag = (QuestionTag)getIntent().getSerializableExtra("questionTag");
        if(getIntent().hasExtra("expert")){
            expertId = getIntent().getLongExtra("expert", 0);
        }
        isQuesFragement = getIntent().getBooleanExtra("isQuesFragement",false);
		init();
	}

	private void init() {
		setLeftBack("", true, false);
		setRightNext(true, getString(R.string.question_next), 0, false);

		etContent = (EditText) findViewById(R.id.question_content);
		iconView = (MyGridView) findViewById(R.id.demand_icon_gridview);
		iconView.setOnItemClickListener(this);
		etContent.addTextChangedListener(new MaxLengthWatcher(500, etContent));
        SysConstants.attachments.clear();
        questionReceiver = new ReplaceReceiver();
        registerReceiver(questionReceiver, new IntentFilter(SysConstants.UPDATEQUESTION));

        iconAdd = new SelectedImage(null, BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.add_icon), null, false);
		imageList.add(iconAdd);
		updateAdapter();
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
                convertView = LayoutInflater.from(QuestionReleaseActivity.this).inflate(R.layout.grideview_circle_item_layout, null);
                viewHolder = new ViewHolder();
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.grid_item1_icon);
                viewHolder.delIcon = (ImageView) convertView.findViewById(R.id.gride_imtm1_faild_del);
                viewHolder.progressRl = (RelativeLayout) convertView.findViewById(R.id.gride_imtm1_progress_rl);
                viewHolder.faildRl = (RelativeLayout) convertView.findViewById(R.id.gride_imtm1_faile_rl);
                viewHolder.load_size = (TextView) convertView.findViewById(R.id.load_size);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final SelectedImage image = data.get(position);
            if (image == null || StringUtils.isBlank(image.imagePath) && image.bitmap == null) {
                return convertView;
            }
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
                    View view = getLayoutInflater().inflate(R.layout.dialog_view, null);
                    final Dialog bottomDialog = new Dialog(QuestionReleaseActivity.this, R.style.transparentFrameWindowStyle);
                    bottomDialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                            ViewGroup.LayoutParams.FILL_PARENT));

                    Button btn1 = (Button) view.findViewById(R.id.btn_dialog1);
                    Button btn2 = (Button) view.findViewById(R.id.btn_dialog2);
                    Button btn3 = (Button) view.findViewById(R.id.btn_dialog3);
                    Button btn4 = (Button) view.findViewById(R.id.btn_dialog4);

                    btn1.setText(R.string.btn_retry);
                    btn1.setVisibility(View.VISIBLE);
                    btn1.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (image != null && image.attachment != null) {
                                image.attachment.setProgress(0);
                                image.attachment.setIsUploadCancel(false);
                                image.attachment.setStatus(Constants.ATTACHMENT_STATUS.NOT_UPLOAD);

                                getService().getFileManager().uploadFile(image.attachment);

                                notifyDataSetChanged();
                            }

                            if (bottomDialog != null && bottomDialog.isShowing()) {
                                bottomDialog.dismiss();
                            }
                        }
                    });

                    btn2.setText(R.string.btn_delete);
                    btn2.setVisibility(View.VISIBLE);
                    btn2.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (data.size() > position) {
                                if(data.size() == 9 && data.get(data.size() - 1).deletable){
                                    data.add(iconAdd);
                                }
                                data.remove(position);
                                notifyDataSetChanged();
                            }

                            if (bottomDialog != null && bottomDialog.isShowing()) {
                                bottomDialog.dismiss();
                            }
                        }
                    });
                    btn3.setText(R.string.cancel);
                    btn3.setVisibility(View.VISIBLE);
                    btn3.setTextColor(getResources().getColor(R.color.text_gray));
                    btn3.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (bottomDialog != null && bottomDialog.isShowing()) {
                                bottomDialog.dismiss();
                            }
                        }
                    });
                    btn4.setVisibility(View.GONE);

                    Window window = bottomDialog.getWindow();
                    // 设置显示动画
                    window.setWindowAnimations(R.style.main_menu_animstyle);
                    WindowManager.LayoutParams wl = window.getAttributes();
                    wl.x = 0;
                    wl.y = getWindowManager().getDefaultDisplay().getHeight();
                    // 设置显示位置
                    bottomDialog.onWindowAttributesChanged(wl);
                    // 设置点击外围解散
                    bottomDialog.setCanceledOnTouchOutside(true);
                    bottomDialog.show();
                }
            });

            return convertView;
        }
    }

	public class ViewHolder {
		ImageView icon;
		ImageView delIcon;
		RelativeLayout progressRl;
		RelativeLayout faildRl;
		TextView load_size;
	}


	@Override
	public void onclickRightNext() {
		super.onclickRightNext();
		for(SelectedImage image : imageList){
			if(image.deletable == true && (image.attachment == null ||
                    image.attachment.getStatus() != Constants.ATTACHMENT_STATUS.UPLOAD_COMPLETED)){
				showToast("图片正在上传或上传失败");
				return;
			}
		}

		String content = etContent.getText().toString().trim();

        if(StringUtils.isBlank(content)){
            showToast("请输入问题相关描述");
        }else{
			try {
				List<Attachment> attachments = new ArrayList<Attachment>();
                SysConstants.attachments.clear();
                attachments.clear();
				for(SelectedImage image : imageList){
					if(image.attachment != null && image.attachment.getStatus()
                            == Constants.ATTACHMENT_STATUS.UPLOAD_COMPLETED){
						attachments.add(image.attachment);
					}
				}
				SysConstants.attachments.addAll(attachments);

                Intent intent = new Intent(QuestionReleaseActivity.this,QuestionSelectActivity.class);
                intent.putExtra("questionTitle",questionTitle);
                intent.putExtra("questionContent",content);
                intent.putExtra("expertId",expertId);
                startActivity(intent);
			} catch (Exception e) {
				disProgressDialog();
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onclickBtn1() {
        state = PHOTO;
        IMAGE_FILE_NAME = SysConstants.FILE_DIR_ROOT + System.currentTimeMillis() + ".png";
        Intent intentFromCapture = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);
        //判断存储卡是否可以用，可用进行存储
        intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(IMAGE_FILE_NAME)));
        startActivityForResult(intentFromCapture, state);
	}

	@Override
	public void onclickBtn2() {
		super.onclickBtn1();
        int imageCount = imageList.get(imageList.size() - 1).deletable ? imageList.size() : imageList.size() - 1;
        Utils.getMultiPhoto(QuestionReleaseActivity.this, PHOTOSELECT, 9 - imageCount);
        state = PHOTOSELECT;

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(position < imageList.size() && !imageList.get(position).deletable){
			showBtnDialog(new String[] { getString(R.string.btn_info_photo),
					getString(R.string.btn_info_photo_album),
					getString(R.string.btn_cancel) });

		}else{
			ArrayList<String> imagePathList = new ArrayList<String>();
			for(SelectedImage image : imageList){
                if(image.deletable) {
                    imagePathList.add(image.imagePath);
                }
			}
			NewPicActivity.invoke(QuestionReleaseActivity.this, imagePathList.get(position), false, imagePathList);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (state == PHOTOSELECT || state == PHOTO) {
                if(state == PHOTO){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                setRightNext(true, getString(R.string.question_next), 0, true);
                setsetRightNextColor(R.color.text_teacher);
				// 选择图片
                List<String> phonePath = new ArrayList<String>();
                if(state == PHOTOSELECT) {
                    phonePath.addAll(data.getStringArrayListExtra("file_next_uris"));
                }else{
                    phonePath.add(IMAGE_FILE_NAME);
                }

                final List<Pair<String, Attachment>> uploadImages = new ArrayList<Pair<String, Attachment>>();

                for(String imagePath : phonePath){
                    String fileKey = UUID.randomUUID().toString();
                    String uploadFilePath = SysConstants.FILE_upload_ROOT + fileKey;

                    Attachment attachment = new Attachment();
                    attachment.setFileUrl(fileKey);
                    attachment.setLocalFilePath(uploadFilePath);
                    attachment.setType(ATTACHMENT_TYPE.IMAGE);
                    attachment.setStatus(Constants.ATTACHMENT_STATUS.NOT_UPLOAD);
                    attachment.setProgress(0);
                    attachment.setIsUploadCancel(false);

                    uploadImages.add(Pair.create(imagePath, attachment));

                    SelectedImage image = new SelectedImage(imagePath, null, attachment, true);
                    imageList.add(imageList.size() - 1, image);
                }

                while(imageList.size() > 9){
                    imageList.remove(imageList.size() - 1);
                }

                updateAdapter();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for(Pair<String, Attachment> uploadImage : uploadImages){
                            String srcImagePath = uploadImage.first;
                            Attachment attachment = uploadImage.second;
                            Utils.saveBitmap(srcImagePath, attachment.getFileUrl(), SysConstants.FILE_upload_ROOT, 300);

                            getService().getFileManager().uploadFile(attachment);
                        }

                    }
                }).start();
			}else if(state == QUESTIONSEL){
                questionTag = (QuestionTag)data.getSerializableExtra("questionTag");
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
			if ((imageList !=null && imageList.size() > 1) ||
                    editText.getText().toString().trim().length() >= 5) {
				setRightNext(true, getString(R.string.question_next), 0, true);
                setsetRightNextColor(R.color.text_teacher);
			} else
				setRightNext(true,getString(R.string.question_next), 0, false);
		}

		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
									  int arg3) {
		}

		public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {

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
		for(SelectedImage selectedImage : imageList){
			if(selectedImage.attachment != null &&
			selectedImage.attachment.getStatus() != Constants.ATTACHMENT_STATUS.UPLOAD_COMPLETED){
				selectedImage.attachment.setIsUploadCancel(true);
			}
		}
        if(questionReceiver != null){
            unregisterReceiver(questionReceiver);
        }

		super.finish();
	}

	class SelectedImage{
		public String imagePath;
        public Bitmap bitmap;
		public Attachment attachment;
        public boolean deletable;

        public SelectedImage(String imagePath, Bitmap bitmap, Attachment attachment, boolean deletable) {
            this.imagePath = imagePath;
            this.bitmap = bitmap;
            this.attachment = attachment;
            this.deletable = deletable;
        }
    }

    class ReplaceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }

}
