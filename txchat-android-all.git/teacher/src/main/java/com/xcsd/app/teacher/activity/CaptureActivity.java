/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xcsd.app.teacher.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.tuxing.app.view.QRCodeReaderView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.teacher.R;
import com.tuxing.sdk.db.entity.AttendanceRecord;
import com.tuxing.sdk.utils.Constants;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public final class CaptureActivity extends BaseActivity implements
        QRCodeReaderView.OnQRCodeReadListener{

    private static final String TAG = CaptureActivity.class.getSimpleName();

    private QRCodeReaderView mydecoderview;
    private ImageView scanLine;

    private ImageView mFlash;
    private LinearLayout ll_left;
    private Button tv_right;
    private AttendanceRecord record = new AttendanceRecord();

    @Override
    public void onCreate(Bundle data) {
        super.onCreate(data);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_qr_scan);
        initView();

        mydecoderview = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        mydecoderview.setOnQRCodeReadListener(this);
        scanLine = (ImageView) findViewById(R.id.capture_scan_line);
        mFlash = (ImageView) findViewById(R.id.capture_flash);
        mFlash.setOnClickListener(this);

        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.95f);
        animation.setDuration(2500);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        scanLine.startAnimation(animation);

    }

    @Override
    public void onQRCodeRead(String text) {
        if(text.startsWith(Constants.app_prefix)){
            final Map<String,String> mapParam =  splitCheckinParams(text);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(mapParam.get("user_id"))) {
                        String userName = "";
                        try {
                            if(mapParam.get("user_name")!=null){
                                userName = URLDecoder.decode(mapParam.get("user_name"), "UTF-8");
                                record = new AttendanceRecord(null,
                                        Long.valueOf(mapParam.get("user_id")), userName, "", null, new Date().getTime());
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        mydecoderview.getCameraManager().stopPreview();
                        //mydecoderview.getCameraManager().getCamera().setPreviewCallback(null);
                        showDialog("", userName + "签到成功!", "取消", "确定");
                    } else {
                        showDialog("", mapParam.get("user_cardnumber") + "签到成功!", "取消", "确定");
                    }
                }
            });
        }
    }


    // Called when your device have no camera
    @Override
    public void cameraNotFound() {
        showAndSaveLog(TAG, "相机打开出错", false);
        displayFrameworkBugMessageAndExit();
    }

    // Called when there's no QR codes in the camera preview image
    @Override
    public void QRCodeNotFoundOnCamImage() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mydecoderview.getCameraManager().startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mydecoderview.getCameraManager().stopPreview();
    }

    private void initView() {
        ll_left = (LinearLayout)findViewById(R.id.ll_left);
        ll_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_right = (Button)findViewById(R.id.tv_right);
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext,ScanRecordListActivity.class));
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConfirm() {
        super.onConfirm();
        try {
            getService().getAttendanceManager().submitRecord(record);
            mydecoderview.getCameraManager().getCamera().setPreviewCallback(this.mydecoderview);
            mydecoderview.getCameraManager().startPreview();
        }catch (Exception e){
            e.printStackTrace();
            showAndSaveLog(TAG, ""+e.getMessage(), false);
        }
    }

    @Override
    public void onCancel() {
        super.onCancel();
        mydecoderview.getCameraManager().getCamera().setPreviewCallback(this.mydecoderview);
        mydecoderview.getCameraManager().startPreview();
    }


    private void displayFrameworkBugMessageAndExit() {
        // camera error
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage("相机打开出错，请稍后重试");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }

        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        builder.show();
    }

    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    //wjyteacher://check_in_with_user_id?user_id=%@&user_name=%@&user_type=%@&user_cardnumber=%@
    private Map<String, String> splitCheckinParams(String str) {
        Map<String, String> paramsMap = new HashMap<String, String>();
        String[] str1 = str.split("\\?");
//        String[] host = str1[0].split("\\:");
        if (str1.length > 1) {
            String[] params = str1[1].split("\\&");
            for (String parmCp : params) {
                String[] subParams = parmCp.split("\\=");
                if (subParams.length > 1) {
                    paramsMap.put(subParams[0], subParams[1]);
                }
            }
        }
        return paramsMap;
    }
}