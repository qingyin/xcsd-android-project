package com.tuxing.app.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import com.tuxing.app.R;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.util.PhoneUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.sdk.event.RelativeEvent;
import com.tuxing.sdk.event.SecurityEvent;
import com.tuxing.sdk.utils.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class InviteVerificationActivity extends BaseActivity implements OnClickListener {

    public static final String TAG = InviteVerificationActivity.class.getSimpleName();
    View view;
    private EditText et_username;
    private EditText et_verification;
    private LinearLayout ll_get_verification;
    private TextView tv_verification;
    private String title ="";
    private  LinearLayout ll_password;
    private  EditText et_password;
    private  int relationType;
    private Button titleRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
//        setContentLayout(R.layout.layout_forget_pwd);
        setContentView(R.layout.layout_forget_pwd);
        title = getIntent().getStringExtra("title");
        TextView titleTv = (TextView) findViewById(R.id.tv_title);
        LinearLayout leftLL = (LinearLayout) findViewById(R.id.ll_left);
       titleRight = (Button) findViewById(R.id.tv_right);
       titleRight.setVisibility(View.VISIBLE);
       titleRight.setText("下一步");
       titleRight.setOnClickListener(this);
       titleRight.setClickable(false);
		titleRight.setTextColor(R.color.bg);
		titleTv.setText(title);
        leftLL.setOnClickListener(this);
        relationType = getIntent().getIntExtra("relationType",-1);
        initView();
        et_username.setHint("请输入手机号");
        et_verification.setHint("请输入验证码");
        
//        setRightNext(true, "下一步", 0);
//        if (EventBus.getDefault() != null && !EventBus.getDefault().isRegistered(this))
//            EventBus.getDefault().registerSticky(this);
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
				if(s.toString().trim().length() > 0){
					titleRight.setClickable(true);
					titleRight.setTextColor(R.color.black);
				}else{
					titleRight.setClickable(false);
					titleRight.setTextColor(R.color.bg);
				}
			}
		});
    }

    private void initView() {
        ll_password = (LinearLayout)findViewById(R.id.ll_password);
        ll_password.setVisibility(View.VISIBLE);
        et_password = (EditText)findViewById(R.id.et_password);
        et_username = (EditText) findViewById(R.id.et_username);
        showInput(et_username);
        ll_get_verification = (LinearLayout) findViewById(R.id.ll_get_verification);
        ll_get_verification.setOnClickListener(this);
        tv_verification = (TextView) findViewById(R.id.tv_verification);
        et_verification = (EditText) findViewById(R.id.et_verification);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_get_verification) {//发送验证码
            if ("".equals(et_username.getText().toString())) {
                Toast.makeText(InviteVerificationActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
                return;
            }
            if(et_username.getText().toString().trim().length()<11){
                Toast.makeText(mContext, getResources().getString(R.string.name_format_null), Toast.LENGTH_SHORT).show();
                return;
            }
            String regExp = "^1[0-9]{10}$";//
            Pattern p = Pattern.compile(regExp);
            Matcher m = p.matcher(et_username.getText().toString());
            if (!m.find()) {
                showToast("手机号格式不正确");
                return;
            }
            showProgressDialog(mContext, "", true, null);
            PhoneUtils.colseKeyboard(mContext, et_verification);
            //获取验证码
            countTimer.start();
            getService().getSecurityManager().sendVerifyCode(et_username.getText().toString(),
                    Constants.VERIFY_CODE_TYPE.INVITATION_ACTIVATE, false);//获取验证码接口

        }else if(v.getId() == R.id.ll_left){
        	finish();
        }else if(v.getId() == R.id.tv_right){
        	checkData();
            hiddenInput(InviteVerificationActivity.this);
        }
        
    }
    
    public void onEventMainThread(SecurityEvent event) {
    	if(isActivity){
    	switch (event.getEvent()) {
    	case SEND_VERIFY_CODE_SUCCESS:
    		 showToast("验证码发送成功!");
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
        checkData();
        hiddenInput(InviteVerificationActivity.this);
    }

    private void checkData() {
        if ("".equals(et_verification.getText().toString())) {
            Toast.makeText(InviteVerificationActivity.this, "请输入验证码", Toast.LENGTH_SHORT).show();
            return;
        }
        if ("".equals(et_username.getText().toString())) {
            Toast.makeText(InviteVerificationActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        if ("".equals(et_password.getText().toString())) {
        	Toast.makeText(InviteVerificationActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
        	return;
        }
        String password = et_password.getText().toString();
        if (password.length() < 6||password.length() >16) {
            showToast("密码长度6~16个字符，由数字、大写字母、小写字母组成");
            return;
        }
        showProgressDialog(mContext, "", true, null);
        PhoneUtils.colseKeyboard(mContext, et_verification);
        getService().getRelativeManager().addRelative(et_username.getText().toString(),
                et_verification.getText().toString(),relationType,et_password.getText().toString());
    }

    public void onEventMainThread(RelativeEvent event) {
        disProgressDialog();
        switch (event.getEvent()) {
            case ADD_RELATIVE_SUCCESS:
                showToast("邀请成功!");
                sendTouChuan(SysConstants.TOUCHUAN_UNBINDUSER);
                finish();
                break;
            case ADD_RELATIVE_FAILED:
                showToast(event.getMsg());
                break;
            case ADD_RELATIVE_VERIFY_CODE_INCORRECT:
                showToast(event.getMsg());
//	            	countTimer.start();
                break;
            case ADD_RELATIVE_BIND_BY_OTHER:
                showToast(event.getMsg());
                break;
        }
    }
}
