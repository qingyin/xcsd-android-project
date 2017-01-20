package com.tuxing.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tuxing.app.activity.CocosJSActivity;
import com.tuxing.app.activity.SelectFolksActivity;
import com.tuxing.app.activity.WebSubDataActivity;
import com.tuxing.app.activity.WebSubUrlActivity;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.util.PhoneUtils;
import com.tuxing.app.util.PreferenceUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.UmengData;
import com.tuxing.sdk.event.SecurityEvent;
import com.tuxing.sdk.event.UserEvent;
import com.tuxing.sdk.utils.Constants;
import com.umeng.analytics.MobclickAgent;

/**
 */
public class ActiveActivity extends BaseActivity implements OnClickListener {

    public static final String TAG = ActiveActivity.class.getSimpleName();
    View view;
    private EditText et_username;
    private EditText et_verification;
    private EditText et_password;
    private LinearLayout ll_get_verification;
    private LinearLayout phone_verification;
    private LinearLayout phone_verification_msg;
    private TextView tv_phone_verification;
    private TextView tv_verification;
    private TextView tv_agreement;
    private FinishReceiver receiver;
    private boolean isPhone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.layout_active);
        initView();
        setTitle("注册");
        setLeftBack("", true, false);
        setRightNext(true, "下一步", 0,false);
        receiver = new FinishReceiver();
        IntentFilter filter = new IntentFilter(SysConstants.FINISH_ACTIVE);
        registerReceiver(receiver,filter);
    }

    private void initView() {
        et_username = (EditText) findViewById(R.id.et_username);
        showInput(et_username);
        ll_get_verification = (LinearLayout) findViewById(R.id.ll_get_verification);
        phone_verification = (LinearLayout) findViewById(R.id.phone_verification);
        phone_verification_msg = (LinearLayout) findViewById(R.id.phone_verification_msg);
        tv_phone_verification = (TextView) findViewById(R.id.tv_phone_verification);
        ll_get_verification.setOnClickListener(this);
        tv_verification = (TextView) findViewById(R.id.tv_verification);
        et_verification = (EditText) findViewById(R.id.et_verification);
        et_password = (EditText) findViewById(R.id.et_password);
        tv_agreement = (TextView) findViewById(R.id.tv_agreement);
        tv_agreement.setOnClickListener(this);
        tv_phone_verification.setOnClickListener(this);
        if (!TextUtils.isEmpty(et_verification.getText().toString())&&!TextUtils.isEmpty(et_username.getText().toString())) {
            setRightNext(true, "下一步", 0, true);
        }else
            setRightNext(true, "下一步", 0, false);
        et_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(et_verification.getText().toString())&&!TextUtils.isEmpty(et_username.getText().toString())) {
                    setRightNext(true, "下一步", 0, true);
                }else
                    setRightNext(true, "下一步", 0, false);
            }
        });
        et_verification.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(et_verification.getText().toString()) && !TextUtils.isEmpty(et_username.getText().toString())) {
                    setRightNext(true, "下一步", 0, true);
                } else
                    setRightNext(true, "下一步", 0, false);
            }
        });
//        et_username.setOnEditorActionListener();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_get_verification) {//发送验证码
            if ("".equals(et_username.getText().toString())) {
                Toast.makeText(ActiveActivity.this, getResources().getString(R.string.name_null), Toast.LENGTH_SHORT).show();
                return;
            } else {
//                Pattern p = Pattern.compile(SysConstants.PHONEREG);
//                Matcher m = p.matcher(et_username.getText().toString());
//                if (!m.find()) {
//                    Toast.makeText(mContext, getResources().getString(R.string.name_format_null), Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if(et_username.getText().toString().trim().length()<11){
                    Toast.makeText(mContext, getResources().getString(R.string.name_format_null), Toast.LENGTH_SHORT).show();
                    return;
                }
                showProgressDialog(mContext, "", true, null);
                PhoneUtils.colseKeyboard(mContext, et_password);
                isPhone = false;
//                //获取验证码
//                countTimer.start();
                getService().getSecurityManager().sendVerifyCode(et_username.getText().toString(),
                        Constants.VERIFY_CODE_TYPE.ACTIVATE, false);//获取验证码接口
                phone_verification.setVisibility(View.VISIBLE);

            }
        }else if (v.getId() == R.id.tv_agreement) {//服务协议
//            WebSubDataActivity.invoke(mContext,SysConstants.AGREEMENTURL,getResources().getString(R.string.service_deal));
            WebSubDataActivity.invoke(mContext, SysConstants.AGREEMENTURL_XCSD, getResources().getString(R.string.service_deal));
        }else if (v.getId() == R.id.tv_phone_verification) {//语音验证码
        	 isPhone = true;
        	 getService().getSecurityManager().sendVerifyCode(et_username.getText().toString(),
                        Constants.VERIFY_CODE_TYPE.ACTIVATE, true);
        	phone_verification_msg.setVisibility(View.VISIBLE);
    }
    }
    
    public void onEventMainThread(SecurityEvent event) {
    	if(isActivity){
    		
    	
    	switch (event.getEvent()) {
    	case SEND_VERIFY_CODE_SUCCESS:
    		if(!isPhone){
    			showToast("验证码发送成功!");
                //获取验证码
                countTimer.start();
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
    	}}
    }


    public void onEventMainThread(UserEvent event) {
        disProgressDialog();
        switch (event.getEvent()) {
            case ACTIVE_USER_SUCCESS:
                if(TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())){
                    //身份选择页面
                    Intent intent = new Intent(mContext, SelectFolksActivity.class);
                    intent.putExtra("user",event.getUser());
                    startActivity(intent);
//                finish();
                }else if(TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())){
                    //身份选择页面
                    PreferenceUtils.setPrefString(mContext, SysConstants.userName, et_username.getText().toString());
                    Intent intent = new Intent(mContext, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                user = getService().getLoginManager().getCurrentUser();
                if(user != null) {
                    getService().getContactManager().syncContact();
                }
                CocosJSActivity.resetData();
                break;
            case ACTIVE_USER_FAILED:
                showToast(event.getMsg());
                break;
            case ACTIVE_USER_VERIFY_CODE_INCORRECT:
                showToast(event.getMsg());
                break;
        }
    }

    private CountDownTimer countTimer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
        	ll_get_verification.setBackgroundResource(R.drawable.yzm_time);
            tv_verification.setText((millisUntilFinished / 1000) + "秒后可重新获取");
            tv_verification.setTextColor(R.color.gray);
            ll_get_verification.setEnabled(false);
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
        MobclickAgent.onEvent(mContext, "login_active",UmengData.login_active);
        checkData();
       }

    private void checkData() {
        if ("".equals(et_username.getText().toString())) {
            Toast.makeText(ActiveActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        if ("".equals(et_verification.getText().toString())) {
            Toast.makeText(ActiveActivity.this, "请输入验证码", Toast.LENGTH_SHORT).show();
            return;
        }
        if ("".equals(et_password.getText().toString())) {
            Toast.makeText(ActiveActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        String password = et_password.getText().toString();
        if (password.length() < 6||password.length() >16) {
            showToast("密码长度6~16个字符，由数字、大写字母、小写字母组成");
            return;
        }
//        Pattern p = Pattern.compile(SysConstants.PHONEREG);
//        Matcher m = p.matcher(et_username.getText().toString());
//        if (!m.find()) {
//            Toast.makeText(mContext, getResources().getString(R.string.name_format_null), Toast.LENGTH_SHORT).show();
//            return;
//        }
        hiddenInput(ActiveActivity.this);
        showProgressDialog(mContext, "", true, null);
        PhoneUtils.colseKeyboard(mContext, et_password);
        getService().getLoginManager().activeUser(et_username.getText().toString(), et_verification.getText().toString(), et_password.getText().toString());//获取验证码接口
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver!=null){
            unregisterReceiver(receiver);
        }
    }
    class FinishReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction()==SysConstants.FINISH_ACTIVE){
                finish();
            }
        }
    }
    
    @Override
    protected void onPause() {
    	phone_verification_msg.setVisibility(View.GONE);
    	super.onPause();
    }
}
