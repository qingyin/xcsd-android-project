package com.tuxing.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import com.tuxing.app.MainActivity;
import com.tuxing.app.R;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.util.PhoneUtils;
import com.tuxing.app.util.PreferenceUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.sdk.event.LoginEvent;
import com.tuxing.sdk.event.SecurityEvent;
import com.tuxing.sdk.event.SetPasswordEvent;
import com.tuxing.sdk.event.UserEvent;
import com.tuxing.sdk.utils.Constants;

/**
 */
public class GetVerificationActivity extends BaseActivity implements OnClickListener {

    public static final String TAG = GetVerificationActivity.class.getSimpleName();
    View view;
    private EditText et_username;
    private EditText et_verification;
    private EditText et_password;
    private LinearLayout ll_get_verification;
    private TextView tv_verification;
    private int type = -1;//type 0  正常重置密码   type 1 换手机号  2 激活用户
    private String title = "";
    private Button titleRight;
    private LinearLayout phone_verification;
    private LinearLayout phone_verification_msg;
    private TextView tv_phone_verification;
    private LinearLayout ll_password;
    private boolean isPhone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
//        setContentLayout(R.layout.layout_forget_pwd);
        setContentView(R.layout.layout_forget_pwd);
        type = getIntent().getIntExtra("type", -1);
        title = getIntent().getStringExtra("title");
        initView();
    }

    private void initView() {
        ll_password = (LinearLayout) findViewById(R.id.ll_password);
        TextView titleTv = (TextView) findViewById(R.id.tv_title);
        LinearLayout leftLL = (LinearLayout) findViewById(R.id.ll_left);
        titleRight = (Button) findViewById(R.id.tv_right);
        phone_verification = (LinearLayout) findViewById(R.id.phone_verification);
        phone_verification_msg = (LinearLayout) findViewById(R.id.phone_verification_msg);
        tv_phone_verification = (TextView) findViewById(R.id.tv_phone_verification);
        tv_phone_verification.setOnClickListener(this);
        titleRight.setVisibility(View.VISIBLE);
        titleRight.setText("确定");
        titleRight.setOnClickListener(this);
        titleRight.setClickable(false);
        titleRight.setTextColor(R.color.bg);
        titleTv.setText(title);
        leftLL.setOnClickListener(this);


        et_username = (EditText) findViewById(R.id.et_username);
        et_verification = (EditText) findViewById(R.id.et_verification);
        et_password = (EditText) findViewById(R.id.et_password);

        ll_get_verification = (LinearLayout) findViewById(R.id.ll_get_verification);
        if (type == SysConstants.VERIFICATION_CODE_RESET_PASSWORD) {//重置密码
            ll_password.setVisibility(View.VISIBLE);
        } else if (type == SysConstants.VERIFICATION_CODE_CHANGE_MOBILE||type == SysConstants.VERIFICATION_CODE_ACTIVATE_USER) {
            ll_password.setVisibility(View.GONE);
            et_username.setHint("输入新的手机号");
            et_verification.setHint("输入验证码");
        }
        showInput(et_username);

        ll_get_verification.setOnClickListener(this);
        tv_verification = (TextView) findViewById(R.id.tv_verification);
        et_verification = (EditText) findViewById(R.id.et_verification);
        et_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() > 0) {
                    titleRight.setClickable(true);
                    titleRight.setTextColor(R.color.black);
                } else {
                    titleRight.setClickable(false);
                    titleRight.setTextColor(R.color.white);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_get_verification) {//发送验证码
            if ("".equals(et_username.getText().toString())) {
                Toast.makeText(GetVerificationActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
                return;
            } else {
                if(et_username.getText().toString().trim().length()<11){
                    Toast.makeText(mContext, getResources().getString(R.string.name_format_null), Toast.LENGTH_SHORT).show();
                    return;
                }
                showProgressDialog(mContext, "", true, null);
                PhoneUtils.colseKeyboard(mContext, et_verification);
                //获取验证码
                countTimer.start();


                if (type == SysConstants.VERIFICATION_CODE_ACTIVATE_USER) {
                	isPhone = false;
                    getService().getSecurityManager().sendVerifyCode(et_username.getText().toString(),
                            Constants.VERIFY_CODE_TYPE.ACTIVATE, false);//获取验证码接口
                    phone_verification.setVisibility(View.VISIBLE);
                } else if (type == SysConstants.VERIFICATION_CODE_RESET_PASSWORD) {
                	isPhone = false;
                    getService().getSecurityManager().sendVerifyCode(et_username.getText().toString(),
                            Constants.VERIFY_CODE_TYPE.FORGET_PASSWORD, false);//获取验证码接口
                } else if (type == SysConstants.VERIFICATION_CODE_CHANGE_MOBILE) {
                	isPhone = false;
                    getService().getSecurityManager().sendVerifyCode(et_username.getText().toString(),
                            Constants.VERIFY_CODE_TYPE.UPDATE_MOBILE, false);//获取验证码接口
                }

            }
        } else if (v.getId() == R.id.tv_agreement) {//服务协议
            Toast.makeText(mContext, "服务协议", Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.ll_left) {
            finish();
        } else if (v.getId() == R.id.tv_right) {
            onclickRightNext();
        }else if (v.getId() == R.id.tv_phone_verification) {//语音验证码
       	 isPhone = true;
       	 getService().getSecurityManager().sendVerifyCode(et_username.getText().toString(),
                       Constants.VERIFY_CODE_TYPE.ACTIVATE, true);
       	phone_verification_msg.setVisibility(View.VISIBLE);
   }
    }

    public void onEventMainThread(UserEvent event) {
        disProgressDialog();
        switch (event.getEvent()) {
            case UPDATE_MOBILE_SUCCESS:
                if (user != null && user.isActive()) {
                    showToast("更换手机号成功!");
                    sendTouChuan(SysConstants.TOUCHUAN_PROFILECHANGE);
                    PreferenceUtils.setPrefString(mContext, SysConstants.userName, et_username.getText().toString());
                    finish();
                } else {//1.0
                    if ((user.getType() == Constants.USER_TYPE.CHILD || user.getType() == Constants.USER_TYPE.PARENT)) {//需要绑定小孩
                        Intent intent = new Intent(mContext, SelectFolksActivity.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(GetVerificationActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    finish();
                }
                break;
            case UPDATE_MOBILE_FAILED:
                showToast(event.getMsg());
                break;
            case UPDATE_MOBILE_VERIFY_CODE_INCORRECT:
                showToast(event.getMsg());
                break;
        }
    }

    public void onEventMainThread(SecurityEvent event) {
        if (isActivity) {
            switch (event.getEvent()) {
                case SEND_VERIFY_CODE_SUCCESS:
                	if(!isPhone){
                		showToast("验证码发送成功!");
                	}
                    disProgressDialog();
                    break;
                case SEND_VERIFY_CODE_FAILED:
                    showToast(event.getMsg());
                    countTimer.cancel();
                    ll_get_verification.setEnabled(true);
                    tv_verification.setText("获取验证码");
                    tv_verification.setTextColor(R.color.skin_text1);
                    ll_get_verification.setBackgroundResource(R.drawable.verification_select);
                    disProgressDialog();
                    break;
            }
        }
    }

    private CountDownTimer countTimer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            tv_verification.setText((millisUntilFinished / 1000) + "秒后可重新获取");
            tv_verification.setTextColor(R.color.gray);
            ll_get_verification.setEnabled(false);
            ll_get_verification.setBackgroundResource(R.drawable.yzm_time);
        }

        @Override
        public void onFinish() {
            ll_get_verification.setEnabled(true);
            tv_verification.setText("获取验证码");
            tv_verification.setTextColor(R.color.skin_text1);
            ll_get_verification.setBackgroundResource(R.drawable.verification_select);
        }
    };

    public void onclickRightNext() {
        if ("".equals(et_verification.getText().toString())) {
            Toast.makeText(GetVerificationActivity.this, "请输入验证码", Toast.LENGTH_SHORT).show();
            return;
        }
        if ("".equals(et_username.getText().toString())) {
            Toast.makeText(GetVerificationActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        PhoneUtils.colseKeyboard(mContext, et_verification);
        hiddenInput(GetVerificationActivity.this);
        if (type == SysConstants.VERIFICATION_CODE_RESET_PASSWORD) {
            if ("".equals(et_password.getText().toString())) {
                showToast("密码不能为空");
                return;
            }
            if (et_password.getText().toString().length() < 6 || et_password.getText().toString().length() > 16) {
                showToast("新密码格式不正确,必须为6-16位数字或字母");
                return;
            }
            getService().getSecurityManager().setPassword(et_username.getText().toString(), et_verification.getText().toString(), et_password.getText().toString());
        } else if (type == SysConstants.VERIFICATION_CODE_CHANGE_MOBILE||type == SysConstants.VERIFICATION_CODE_ACTIVATE_USER) {
            getService().getUserManager().updatePhoneNum(et_username.getText().toString(), et_verification.getText().toString());
        }
        showProgressDialog(mContext, "", true, null);
    }

    public void onEventMainThread(SetPasswordEvent event) {

        switch (event.getEvent()) {
            case RESET_PASSWORD_SUCCESS:
                showToast("重置密码成功！");
                sendTouChuan(SysConstants.TOUCHUAN_PROFILECHANGE);
                showProgressDialog(mContext, "", true, null);
                getService().getLoginManager().login(et_username.getText().toString(), et_password.getText().toString());
                break;
            case RESET_PASSWORD_FAILED:
            case VERIFY_CODE_INCORRECT:
                disProgressDialog();
                showToast("重置密码失败！");
                break;
        }
    }

    public void onEventMainThread(LoginEvent event) {

        if (isActivity) {
            disProgressDialog();
            switch (event.getEvent()) {
                case LOGIN_SUCCESS:
                    PreferenceUtils.setPrefString(mContext, SysConstants.userName, et_username.getText().toString());
                    Intent intent = new Intent(GetVerificationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case LOGIN_NO_SUCH_USER:
                case LOGIN_FAILED_UNKNOWN_REASON:
                    showToast(event.getMsg());
                    break;
                case LOGOUT:
//                Toast.makeText(getApplicationContext(), event.getMsg(), Toast.LENGTH_LONG).show();
                    break;
                case LOGIN_AUTH_FAILED:
                    showToast(event.getMsg());
                    break;
                case KICK_OFF://这两个要处理下
                    showToast(event.getMsg());
                    break;
                default:
                    break;
            }
            //}
        }
    }

    @Override
    protected void onResume() {
        isActivity = true;
        super.onResume();
    }
}
