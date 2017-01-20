package com.tuxing.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tuxing.app.R;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.util.SysConstants;

public class MyPhoneNumActivity extends BaseActivity implements View.OnClickListener{

    private LinearLayout ll_change_phonenum;
    private TextView tv_phone_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.layout_change_phonenum);
        init();
        initView();
    }

    public void initView() {
        ll_change_phonenum = (LinearLayout)findViewById(R.id.ll_change_phonenum);
        ll_change_phonenum.setOnClickListener(this);
        tv_phone_no = (TextView)findViewById(R.id.tv_phone_no);
    }

    @Override
    public void onResume(){
        super.onResume();
        if(user != null) {
            tv_phone_no.setText(user.getMobile());
        }
    }

    private void init() {
        setTitle("更换手机号");
        setLeftBack("", true, false);
        setRightNext(false, "", 0);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.ll_change_phonenum) {//修改手机号
            Intent intent = new Intent(mContext,GetVerificationActivity.class);
            intent.putExtra("type", SysConstants.VERIFICATION_CODE_CHANGE_MOBILE);
            intent.putExtra("title","更换手机号");
            startActivity(intent);
        }
    }

}
