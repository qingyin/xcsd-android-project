package com.tuxing.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.util.UmengData;
import com.tuxing.sdk.utils.Constants;
import com.umeng.analytics.MobclickAgent;

public class MyRoleActivity extends BaseActivity implements View.OnClickListener{

    public RelativeLayout rl_change_phone;
    public RelativeLayout rl_change_pwd;
    public TextView tv_phone_no;
    public ImageView iv_arrow;
    public ImageView iv_arrow_change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.layout_my_role);
        init();
        initView();
    }

    public void initView() {
        rl_change_pwd = (RelativeLayout)findViewById(R.id.rl_change_pwd);
        rl_change_phone = (RelativeLayout)findViewById(R.id.rl_change_phone);
        rl_change_pwd.setOnClickListener(this);
        rl_change_phone.setOnClickListener(this);
        tv_phone_no = (TextView)findViewById(R.id.tv_phone_no);
        iv_arrow = (ImageView) findViewById(R.id.iv_arrow);
        iv_arrow_change = (ImageView) findViewById(R.id.iv_arrow_change);
        if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())){
            iv_arrow.setImageResource(R.drawable.arrow_right_p);
            iv_arrow_change.setImageResource(R.drawable.arrow_right_p);

        }else if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())){
            iv_arrow.setImageResource(R.drawable.arrow_right_t);
            iv_arrow_change.setImageResource(R.drawable.arrow_right_t);
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        if(user!=null) {
            tv_phone_no.setText(user.getMobile() + "");
        }
    }

    private void init() {
        setTitle(getString(R.string.account_safety));
        setLeftBack("", true, false);
        setRightNext(false, "", 0);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.rl_change_phone) {//修改手机号
            Intent intent = new Intent(mContext,MyPhoneNumActivity.class);
            startActivity(intent);
            MobclickAgent.onEvent(mContext, UmengData.my_fix_phone);
        } else if (id == R.id.rl_change_pwd) {//修改密码
            Intent intent = new Intent(mContext,PwdSubmitActivity.class);
            intent.putExtra("title", " 修改密码");
            startActivity(intent);
            MobclickAgent.onEvent(mContext,"my_fix_psw", UmengData.my_fix_psw);
        }
    }

}
