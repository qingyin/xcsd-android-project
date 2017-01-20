package com.xcsd.app.teacher.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tuxing.app.activity.ClipPictureActivity;
import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.MyLog;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.UmengData;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.event.UploadFileEvent;
import com.tuxing.sdk.event.UserEvent;
import com.tuxing.sdk.modle.Attachment;
import com.tuxing.sdk.utils.Constants.ATTACHMENT_TYPE;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.Calendar;

public class MyUserInfoActivity extends BaseActivity {

    private RoundImageView icon;
    private TextView tv_name;
    private TextView tv_sex;
    private TextView tv_school;
    private TextView tv_career;
    private Attachment attachment = null;
    private String TAG = MyUserInfoActivity.class.getSimpleName();
    private static final int SELECT_PIC_KITKAT = 103;

    private static final int IMAGE_REQUEST_CODE = 104;
    private static final int CAMERA_REQUEST_CODE = 105;
    public static final int RESULT_CLIPPICTURE = 108;
    private Calendar calendar;

    private String photoName, photoPath, photoNextPath;
    private int selectNameId = 0;
    private int state = 0; //2 姓名  3 性别  4生日

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.my_userinfo_layout);
        init();
    }
    @Override
    protected void onResume() {
        super.onResume();
        isActivity = true;
    }

    private void init() {
        setTitle(getString(R.string.user_info));
        setLeftBack("", true, false);
        setRightNext(false, "", 0);
        calendar = Calendar.getInstance();
        icon = (RoundImageView) findViewById(R.id.my_icon);
        icon.setOnClickListener(this);
        findViewById(R.id.my_camera).setOnClickListener(this);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_school = (TextView) findViewById(R.id.tv_school);
        tv_career = (TextView) findViewById(R.id.tv_career);
        findViewById(R.id.my_sex_rl).setOnClickListener(this);

        if (user != null) {
            icon.setImageUrl(user.getAvatar(), R.drawable.default_info_image);
            tv_name.setText(user.getNickname());
            tv_sex.setText((user.getGender()!=null&&user.getGender() == 1) ? "女" :"男");
            tv_school.setText(user.getGardenName());
            tv_career.setText(user.getPositionName());
        }
    }

    public void onEventMainThread(UploadFileEvent event) {
        if (isActivity) {
            switch (event.getEvent()) {
                case UPLOAD_COMPETED:
                    attachment = event.getAttachment();
                    if (user != null && attachment != null) {
                        icon.setImageBitmap(Utils.revitionImageSize(photoNextPath, SysConstants.IMAGEIMPLESIZE_256));
                        user.setAvatar(attachment.getFileUrl());
                        getService().getUserManager().updateUserInfo(user);
                        disProgressDialog();
                        state = 5;
                        showAndSaveLog(TAG, "上传头像成功" + attachment.getFileUrl(), false);
                    }
                    break;
                case UPLOAD_FAILED:
                    disProgressDialog();
                    showDialog("", getString(R.string.upload_icon_msg), "", getResources().getString(R.string.btn_ok));
                    showAndSaveLog(TAG, "上传图头像失败" + event.getMsg(), false);
                    break;
            }
        }
    }

    public void onEventMainThread(UserEvent event) {
        if (isActivity) {
            switch (event.getEvent()) {
                case UPDATE_USER_SUCCESS:
                    if (state == 3) {
                        showToast("修改性别成功");
                    } else if (state == 5) {
                        showToast("修改头像成功");
                    }
                    showAndSaveLog(TAG, "修改信息成功", false);
                    break;
                case UPDATE_USER_FAILED:
                    showToast(event.getMsg());
                    showAndSaveLog(TAG, "修改信息失败" + event.getMsg(), false);
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.my_icon:
                showBtnDialog(new String[]{getString(R.string.btn_info_photo),
                        getString(R.string.btn_info_photo_album),
                        getString(R.string.btn_cancel)});
                MobclickAgent.onEvent(mContext,"my_cion",UmengData.my_cion);
                break;
            case R.id.my_camera:
                showBtnDialog(new String[]{getString(R.string.btn_info_photo),
            			getString(R.string.btn_info_photo_album),
            			getString(R.string.btn_cancel)});
                MobclickAgent.onEvent(mContext,"my_cion",UmengData.my_cion);
            	break;
            case R.id.my_sex_rl:
                showSexDialog(tv_sex.getText().toString());
                break;
        }
    }


    /**
     * 打开照相机
     */
    @Override
    public void onclickBtn1() {
        photoName = System.currentTimeMillis() + ".jpg";
        photoPath = SysConstants.FILE_DIR_ROOT + photoName;
        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断存储卡是否可以用，可用进行存储
        intentFromCapture.putExtra(
                MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(SysConstants.FILE_DIR_ROOT + "test.jpg")));
        startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
    }

    /**
     * 打开相册
     */
    @Override
    public void onclickBtn2() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/jpeg");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            startActivityForResult(intent, SELECT_PIC_KITKAT);
        } else {
            startActivityForResult(intent, IMAGE_REQUEST_CODE);
        }
    }

    /**
     * 设置头像信息
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_CANCELED)
            return;
//		if (resultCode == RESULT_OK) {
        switch (requestCode) {
            case IMAGE_REQUEST_CODE:
                if (null == data)
                    return;
                System.out.println("data.getData():" + data.getData());
                MyLog.getLogger(getClass()).d("上传头像==>MyInfoActivity:data.getData():" + data.getData());
                String url1 = getPath(mContext, data.getData());
                System.out.println("url:" + url1);
                Intent intent1 = new Intent(mContext, ClipPictureActivity.class);
                intent1.putExtra("path", url1);
                intent1.putExtra("type", "1");
                startActivityForResult(intent1, RESULT_CLIPPICTURE);
                break;
            case SELECT_PIC_KITKAT:
                if (null == data)
                    return;
                System.out.println("data.getData():" + data.getData());
                String url = getPath(mContext, data.getData());
                System.out.println("url:" + url);
                Intent intent = new Intent(mContext, ClipPictureActivity.class);
                intent.putExtra("path", url);
                intent.putExtra("type", "1");
                startActivityForResult(intent, RESULT_CLIPPICTURE);
                break;
            case CAMERA_REQUEST_CODE:
                File tempFile = new File(SysConstants.FILE_DIR_ROOT + "test.jpg");
                if (tempFile.length() > 0) {
                    MyLog.getLogger(getClass()).d("上传头像==>MyInfoActivity:tempFile:" + tempFile.getAbsolutePath());
                    Intent intent2 = new Intent(mContext, ClipPictureActivity.class);
                    intent2.putExtra("path", tempFile.getAbsolutePath());
                    intent2.putExtra("type", "0");
                    startActivityForResult(intent2, RESULT_CLIPPICTURE);
                }
                break;
            case RESULT_CLIPPICTURE:
                if (null == data)
                    return;
                String path = data.getStringExtra("picPath");
                if (!TextUtils.isEmpty(path)) {
                    showProgressDialog(mContext, "正在上传头像", true, null);
                    photoNextPath = path;
                    String newPath = SysConstants.FILE_upload_ROOT + "user" + ".png";
                    Utils.saveBitmap(path,"user" + ".png", SysConstants.FILE_upload_ROOT,300);
                    getService().getFileManager().uploadFile(new File(newPath), ATTACHMENT_TYPE.IMAGE);
                }
                break;
//         }  
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    //以下是关键，原本uri返回的是file:///...来着的，android4.4返回的是content:///...  
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider  
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider  
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider  
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address  
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File  
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public void showSexDialog(String sex) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_sex_view, null);
        final Dialog dialog = new Dialog(mContext, R.style.dialog_common);

        // 根据id在布局中找到控件对象
        RelativeLayout rlMan = (RelativeLayout) view.findViewById(R.id.rl_man);
        RelativeLayout rlWoman = (RelativeLayout) view.findViewById(R.id.rl_woman);
        final ImageView manIV = (ImageView) view.findViewById(R.id.iv_man);
        final ImageView womanIV = (ImageView) view.findViewById(R.id.iv_woman);
        if (sex.equals("男")) {
            manIV.setVisibility(View.VISIBLE);
        } else if (sex.equals("女")) {
            womanIV.setVisibility(View.VISIBLE);
        } else {
            manIV.setVisibility(View.GONE);
            womanIV.setVisibility(View.GONE);
        }
        rlMan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_sex.setText("男");
                state = 3;
                getService().getUserManager().updateUserInfo(user);
                user.setGender(2);
                dialog.dismiss();
            }
        });
        rlWoman.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_sex.setText("女");
                state = 3;
                user.setGender(1);
                getService().getUserManager().updateUserInfo(user);
                dialog.dismiss();
            }
        });
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view, new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
