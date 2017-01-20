package com.tuxing.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.tuxing.app.R;
import com.tuxing.app.R.id;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.util.SysConstants;
import com.tuxing.sdk.event.UserEvent;

/**
 */
public class PwdSubmitActivity extends BaseActivity  implements OnClickListener{

	public static final String TAG = PwdSubmitActivity.class.getSimpleName();
	View view;
	private EditText et_old_pwd;
	private EditText et_new_pwd;
	private EditText et_new_pwd1;
	private String title = "";
	private LinearLayout ll_old_pwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.layout_pwd_submit);
		title = getIntent().getStringExtra("title");
		setTitle(title);
		initView();
		setLeftBack("", true,false);
		setRightNext(true, "确定", 0);
	}
	private void initView() {
		ll_old_pwd = (LinearLayout)findViewById(R.id.ll_old_pwd);

		et_old_pwd = (EditText)findViewById(R.id.et_old_pwd);
		et_new_pwd = (EditText)findViewById(R.id.et_new_pwd);
		et_new_pwd1 = (EditText)findViewById(id.et_new_pwd1);
		ll_old_pwd.setVisibility(View.VISIBLE);
	}

	public void onclickRightNext() {
		if("".equals(et_old_pwd.getText().toString())){
			showToast("原密码不能为空");
			return;
		}
		if("".equals(et_new_pwd.getText().toString())){
			showToast("新密码不能为空");
			return;
		}
		if(!et_new_pwd.getText().toString().equals(et_new_pwd1.getText().toString())){
			showToast("新密码两次输入的密码不一致");
			return;
		}
		if(et_new_pwd.getText().toString().length()<6||et_new_pwd.getText().toString().length()>16){
			showToast("新密码格式不正确,必须为6-16位数字或字母");
			return;
		}
		showProgressDialog(mContext, "", true, null);
		getService().getUserManager().changePassword(et_old_pwd.getText().toString(), et_new_pwd.getText().toString());
	}
	public void onEventMainThread(UserEvent event) {
		disProgressDialog();
		switch (event.getEvent()) {
			case CHANGE_PASSWORD_SUCCESS:
				disProgressDialog();
				showToast("修改密码成功");
				 sendTouChuan(SysConstants.TOUCHUAN_PROFILECHANGE);
				//通知登录下HX
				sendBroadcast(new Intent(SysConstants.LOGINHX));
				finish();
				break;
			case CHANGE_PASSWORD_FAILED:
				disProgressDialog();
				showToast(event.getMsg());
				break;
		}
	}
}
