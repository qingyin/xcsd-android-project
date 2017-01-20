package com.xcsd.app.parent.activity;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tuxing.app.activity.ClipPictureActivity;
import com.tuxing.app.activity.EditFolksNameActivity;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.ui.dialog.MyQrodeDialog;
import com.tuxing.app.util.DateTimePickDialogUtil;
import com.tuxing.app.util.DateTimePickDialogUtil.SelectListener;
import com.tuxing.app.util.MyLog;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.UmengData;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.event.RelativeEvent;
import com.tuxing.sdk.event.UploadFileEvent;
import com.tuxing.sdk.event.UserEvent;
import com.tuxing.sdk.modle.Attachment;
import com.tuxing.sdk.utils.Constants.ATTACHMENT_TYPE;
import com.tuxing.sdk.utils.Constants.RELATIVE_TYPE;
import com.umeng.analytics.MobclickAgent;
import com.xcsd.app.parent.R;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyUserInfoActivity extends BaseActivity {

    private RoundImageView icon;
    private RoundImageView riChildPic;
    private TextView identity;
    private TextView parentName;
    private TextView babyName;
    private TextView babySex;
    private TextView birth;
    private TextView className;
    private TextView school;
    private User parentData;
    private User childData;
    private Attachment attachment = null;
    private Map<Integer, String> identitMap = new HashMap<Integer, String>();
    private String TAG = MyUserInfoActivity.class.getSimpleName();
    private static final int SELECT_PIC_KITKAT = 103;

    private static final int IMAGE_REQUEST_CODE = 104;
    private static final int CAMERA_REQUEST_CODE = 105;
    public static final int RESULT_SELECTNAME = 107;
    public static final int RESULT_CLIPPICTURE = 108;
    public static final int RESULT_SELECTPARENTNAME = 109;
    private Calendar calendar;

    private String photoName, photoPath, photoNextPath;
    private int selectNameId = 0;
    private int state = 0; //2 姓名  3 性别  4生日
    private LinearLayout ll_my_wipe_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.my_userinfo_layout);
        init();
    }

    private void initParentData(User data) {
        if (data != null) {
            icon.setImageUrl(data.getAvatar(), R.drawable.default_info_image);
            identity.setText(identitMap.get(data.getRelativeType()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivity = true;
    }

    private void initBabyData(User data) {
        if (data != null) {
            String timeStame = "";

            if (data.getBirthday() != null) {
                timeStame = new SimpleDateFormat("yyyy-MM-dd").format(new Date(data.getBirthday()));
            }
            className.setText(data.getClassName());
            parentName.setText(data.getGuarder());
            school.setText(data.getGardenName());
            babyName.setText(data.getNickname());
            if (data.getGender() != null) {
                if (data.getGender() == 1)
                    babySex.setText("女");
                else
                    babySex.setText("男");
            }
            birth.setText(timeStame);
        }
    }

    private void init() {

        setTitle(getString(R.string.user_info));
        setLeftBack("", true, false);
        setRightNext(false, "", 0);
        calendar = Calendar.getInstance();
        icon = (RoundImageView) findViewById(R.id.my_icon);
        riChildPic = (RoundImageView) findViewById(R.id.ri_wipe_child);
        identity = (TextView) findViewById(R.id.my_identity);
        parentName = (TextView) findViewById(R.id.my_parent_name);
        babyName = (TextView) findViewById(R.id.my_baby_name);
        birth = (TextView) findViewById(R.id.my_baby_birth);
        babySex = (TextView) findViewById(R.id.my_baby_sex);
        className = (TextView) findViewById(R.id.my_class);
        school = (TextView) findViewById(R.id.my_school);


        findViewById(R.id.my_camera).setOnClickListener(this);
        findViewById(R.id.my_identity_rl).setOnClickListener(this);
        icon.setOnClickListener(this);
        riChildPic.setOnClickListener(this);
        findViewById(R.id.my_parent_name_rl).setOnClickListener(this);
        findViewById(R.id.my_baby_birth_rl).setOnClickListener(this);
        findViewById(R.id.my_baby_sex_rl).setOnClickListener(this);
        ll_my_wipe_code = (LinearLayout) findViewById(R.id.ll_my_wipe_code);
        ll_my_wipe_code.setOnClickListener(this);
        identitMap.put(RELATIVE_TYPE.FATHER, "爸爸");
        identitMap.put(RELATIVE_TYPE.MOTHER, "妈妈");
        identitMap.put(RELATIVE_TYPE.PATERNAL_GRANDFATHER, "爷爷");
        identitMap.put(RELATIVE_TYPE.PATERNAL_GRANDMOTHER, "奶奶");
        identitMap.put(RELATIVE_TYPE.MATERNAL_GRANDFATHER, "姥爷");
        identitMap.put(RELATIVE_TYPE.MATERNAL_GRANDMOTHER, "姥姥");
        identitMap.put(RELATIVE_TYPE.OTHER, "亲属");
        if (user != null) {
            parentData = getService().getUserManager().getUserInfo(user.getUserId());
            initParentData(parentData);
            if (parentData != null) {
                childData = getService().getUserManager().getUserInfo(parentData.getChildUserId());
                initBabyData(childData);
            }
        }
    }

    public void onEventMainThread(UploadFileEvent event) {
        if (isActivity) {
            switch (event.getEvent()) {
                case UPLOAD_COMPETED:
                    attachment = event.getAttachment();
                    if (parentData != null && attachment != null) {
                        icon.setImageBitmap(Utils.revitionImageSize(photoNextPath, SysConstants.IMAGEIMPLESIZE_256));
                        parentData.setAvatar(attachment.getFileUrl());
                        user.setAvatar(attachment.getFileUrl());
                        getService().getUserManager().updateUserInfo(parentData);
                        disProgressDialog();
                        state = 5;
                        sendTouChuan(SysConstants.TOUCHUAN_PROFILECHANGE);
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
                    if (state == 1) {
                        showToast("修改身份成功");
                    } else if (state == 2) {
                        showToast("修改监护人成功");
                    } else if (state == 3) {
                        showToast("修改性别成功");
                    } else if (state == 4) {
                        showToast("修改生日成功");
                    } else if (state == 5) {
                        showToast("修改头像成功");
                    }
                    sendTouChuan(SysConstants.TOUCHUAN_PROFILECHANGE);
                    showAndSaveLog(TAG, "修改信息成功", false);
                    break;
                case UPDATE_USER_FAILED:
                    showToast(event.getMsg());
                    showAndSaveLog(TAG, "修改信息失败" + event.getMsg(), false);
                    break;
            }
        }
    }

    public void onEventMainThread(RelativeEvent event) {
        if (isActivity) {
            switch (event.getEvent()) {
                case UPDATE_RELATIVE_SUCCESS:
                    showToast("修改身份成功");
                    showAndSaveLog(TAG, "修改身份成功", false);
                    break;
                case UPDATE_RELATIVE_FAILED:
                    showToast(event.getMsg());
                    showAndSaveLog(TAG, "修改身份失败" + event.getMsg(), false);
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.my_baby_birth_rl:
                if (childData != null) {
                    DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                            mContext, birth.getText().toString(), birth, true, new MySelectListener());
                    dateTimePicKDialog.dateTimePicKDialog();

                } else {
                    showToast("没有小孩信息,不可操作");
                }

                break;
            case R.id.my_parent_name_rl:
                Intent intent = new Intent(this, EditFolksNameActivity.class);
                intent.putExtra("name", parentName.getText().toString());
                startActivityForResult(intent, RESULT_SELECTPARENTNAME);
                break;
            case R.id.my_icon:
                showBtnDialog(new String[]{getString(R.string.btn_info_photo),
                        getString(R.string.btn_info_photo_album),
                        getString(R.string.btn_cancel)});
                MobclickAgent.onEvent(mContext, "my_cion", UmengData.my_cion);
                break;
            case R.id.my_camera:
                showBtnDialog(new String[]{getString(R.string.btn_info_photo),
                        getString(R.string.btn_info_photo_album),
                        getString(R.string.btn_cancel)});
                MobclickAgent.onEvent(mContext, "my_cion", UmengData.my_cion);
                break;
            case R.id.my_identity_rl:
                //TODO 选择身份
                Intent nameIntent = new Intent(this, SelectIdentityActivity.class);
                nameIntent.putExtra("name", identity.getText().toString());
                startActivityForResult(nameIntent, RESULT_SELECTNAME);
                break;
            case R.id.my_baby_sex_rl:
                if (childData != null) {
                    showSexDialog(babySex.getText().toString());
                } else {
                    showToast("没有小孩信息,不可操作");
                }
                break;
            case R.id.ll_my_wipe_code://我的二维码
                MyQrodeDialog dialog = new MyQrodeDialog(mContext, identity.getText().toString());
                dialog.show();
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
            case RESULT_SELECTNAME:
                //身份
                if (null == data)
                    return;
                if (data.getStringExtra("selectName") != null) {
                    state = 1;
                    identity.setText(data.getStringExtra("selectName"));
                    selectNameId = data.getIntExtra("selectNameId", 0);
                    getService().getRelativeManager().changeRelative(parentData.getUserId(), selectNameId);
                }
                break;
            case RESULT_SELECTPARENTNAME:
                //监护人
                if (null == data)
                    return;
                if (data.getStringExtra("name") != null) {
                    parentName.setText(data.getStringExtra("name"));
                    state = 2;
                    updataChildInfo();
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
                    Utils.saveBitmap(path, "user" + ".png", SysConstants.FILE_upload_ROOT, 300);
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

        if (uri == null) {
            return null;
        }
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
                babySex.setText("男");
                state = 3;
                updataChildInfo();
                dialog.dismiss();
            }
        });
        rlWoman.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                babySex.setText("女");
                state = 3;
                updataChildInfo();
                dialog.dismiss();
            }
        });

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view, new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    public void updataParentInfo() {
        if (parentData != null) {

            if (selectNameId != 0) {
                parentData.setRelativeType(selectNameId);
            }
            getService().getUserManager().updateUserInfo(parentData);
        }
    }

    public void updataChildInfo() {
        if (childData != null) {
            try {
                childData.setRealname(babyName.getText().toString());
                if (babySex.getText().toString().equals("男")) {
                    childData.setGender(2);
                } else if (babySex.getText().toString().equals("女")) {
                    childData.setGender(1);
                }
                if (!TextUtils.isEmpty(birth.getText().toString())) {
                    long timeStemp = new SimpleDateFormat("yyyy-MM-dd").parse(birth.getText().toString()).getTime();
                    childData.setBirthday(timeStemp);
                }
                if (!TextUtils.isEmpty(parentName.getText().toString())) {
                    childData.setGuarder(parentName.getText().toString());
                }
                childData.setClassName(className.getText().toString());
                childData.setGardenName(school.getText().toString());

                getService().getUserManager().updateUserInfo(childData);
            } catch (ParseException e) {
                showAndSaveLog(TAG, "修小孩信息失败   msg = " + e.toString(), false);
                e.printStackTrace();
            } catch (Exception e) {
                showAndSaveLog(TAG, "修小孩信息失败   msg = " + e.toString(), false);
                e.printStackTrace();
            }
        }
    }

    public class MySelectListener implements SelectListener {
        @Override
        public void updateInfo() {
            state = 4;
            updataChildInfo();
        }
    }

    @Override
    public void onConfirm() {
        // TODO Auto-generated method stub
        super.onConfirm();
    }

}
