package com.tuxing.app.activity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.util.AppUpdate;
import com.tuxing.sdk.event.UpgradeEvent;
import com.tuxing.sdk.modle.UpgradeInfo;
import com.tuxing.sdk.utils.Constants;

import java.io.File;

public class AboutActivity extends BaseActivity {

    AppUpdate mAppUpdate;
    TextView about_left;
    Button about_check_new;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_about);
        //更新
        mAppUpdate = new AppUpdate(this);
        initViews();
    }

    private void initViews() {

        about_left = (TextView) findViewById(R.id.about_left);
        about_check_new = (Button) findViewById(R.id.about_check_new);
        if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())) {
            about_left.setTextColor(getResources().getColor(R.color.text_parent));
            about_check_new.setBackgroundColor(getResources().getColor(R.color.text_parent));
            Drawable drawable = null;
            drawable = getResources().getDrawable(R.drawable.ic_back_title_p);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            about_left.setCompoundDrawables(drawable, null, null, null);
        } else if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {
            about_left.setTextColor(getResources().getColor(R.color.text_teacher));
            about_check_new.setBackgroundColor(getResources().getColor(R.color.text_teacher));
            Drawable drawable = null;
            drawable = getResources().getDrawable(R.drawable.ic_back_title_t);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            about_left.setCompoundDrawables(drawable, null, null, null);

        }

        TextView version = (TextView) findViewById(R.id.versionTV);
        String versionName = getString(R.string.version, getVersionName(this));
        versionName = getString(R.string.version_test, getVersionName(this));
        findViewById(R.id.about_check_new).setOnClickListener(this);
        findViewById(R.id.about_left).setOnClickListener(this);
        version.setText(versionName);

    }

    private String getVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            return info.versionName;
        } catch (NameNotFoundException e) {
        }
        return "";
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.about_check_new) {
//			showToast("检查更新");
            showProgressDialog(mContext, "检查版本中..", true, null);
            getService().getUpgradeManager().getUpgradeInfo();
        } else if (id == R.id.about_left) {
            finish();
        }
    }

    UpgradeInfo upgradeInfo = null;

    public void onEventMainThread(UpgradeEvent event) {
        if (isActivity) {
            disProgressDialog();
            switch (event.getEvent()) {
                case GET_UPGRADE_SUCCESS:
                    upgradeInfo = event.getInfo();
                    if (upgradeInfo != null && upgradeInfo.isHasNewVersion()) {//这个字段 isShowAtMain 只在main里起作用
                        showDialog("", upgradeInfo.getShowMsg(), "取消", "升级");
                    } else if (upgradeInfo != null &&
                            upgradeInfo.isHasNewVersion() && upgradeInfo.isForceUpgrade()) {
                        showDialog("", upgradeInfo.getShowMsg(), "", "升级");
                    } else {
                        showToast(getResources().getString(R.string.hint_update_info));
                    }
                    break;
                case GET_UPGRADE_FAILED:
                    showToast(getResources().getString(R.string.hint_update_info));
                    break;
            }
        }
    }

    @Override
    public void onConfirm() {
        super.onConfirm();
        try {
            if (upgradeInfo != null) {
                File f = mAppUpdate.download(upgradeInfo);
                if (f != null && f.exists()) {
                    AppUpdate.setupApk(mContext, f);
                } else {
                    Toast.makeText(mContext, "新版本下载已开始...", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "下载失败，请稍候重试", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCancel() {
        super.onCancel();
    }

}
