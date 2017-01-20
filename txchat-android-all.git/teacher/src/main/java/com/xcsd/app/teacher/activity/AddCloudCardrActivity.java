package com.xcsd.app.teacher.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.UmengData;
import com.tuxing.sdk.event.CheckInCardEvent;
import com.umeng.analytics.MobclickAgent;


public class AddCloudCardrActivity extends BaseActivity {

	private TextView name;
	private EditText etCardNumber;
	private Button tvTitleRight;
	private String intentName;
	private String intentCardNumber;
	private Long childUserId;
	private boolean isSave = false;
	private Button btn;
	private String TAG = AddCloudCardrActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.my_card_number_add_layout);
		init();
		initData();
	}

	private void initData() {
		intentName = getIntent().getStringExtra("name");
		intentCardNumber = getIntent().getStringExtra("cardNumber");
		if(intentCardNumber == null  || intentCardNumber.equals("")){
			btn.setVisibility(View.GONE);
		}else{
			isSave = false;
			btn.setVisibility(View.VISIBLE);
			btn.setText(getString(R.string.card_loss));
			etCardNumber.setText(intentCardNumber);
		}
		name.setText(intentName + "的卡号");
		
		etCardNumber.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				// TODO 
				if (s.toString().trim().length() > 0) {
					if (intentCardNumber != null
							|| !intentCardNumber.equals("")) {
						if (!s.toString().trim().equals(intentCardNumber)) {
							if (s.toString().trim().length() == 8) {
								isSave = true;
								btn.setVisibility(View.VISIBLE);
								btn.setText(getString(R.string.save));
							} else {
								btn.setVisibility(View.GONE);
							}
						} else {
							isSave = false;
							btn.setVisibility(View.VISIBLE);
							btn.setText(getString(R.string.card_loss));
						}
					} else {
						if (s.toString().trim().length() == 8) {
							isSave = true;
							btn.setVisibility(View.VISIBLE);
							btn.setText(getString(R.string.save));
						} else {
							btn.setVisibility(View.GONE);
						}
					}
				}else{
					btn.setVisibility(View.GONE);
				}
			}
		});
	}

	private void init() {
		
		setTitle(getString(R.string.cloudcard_title));
		setLeftBack("取消", false,true);
		setRightNext(true, "", 0);
		name = (TextView) findViewById(R.id.add_card_name);
		btn = (Button) findViewById(R.id.btn_cloud);
		btn.setOnClickListener(this);
		etCardNumber = (EditText) findViewById(R.id.et_card_number);
		if(user != null)
		childUserId = user.getChildUserId();



	}
	
	 public void onEventMainThread(CheckInCardEvent event){
		 disProgressDialog();
		 switch (event.getEvent()) {
		 case CARD_BIND_SUCCESS:
			 showAndSaveLog(TAG, "绑定卡号成功  --", false);
			 finish();
				break;
		 case CARD_BIND_FAILED:
			 showAndSaveLog(TAG, "绑定卡号失败   --msg = " + event.getMsg(), false);
			 showToast(event.getMsg());
			 break;
		 case CARD_UNBIND_SUCCESS:
			 showAndSaveLog(TAG, "挂失成功     --msg = " + event.getMsg(), false);
			 showToast("挂失成功");
			 finish();
			 break;
		 case CARD_UNBIND_FAILED:
			 showAndSaveLog(TAG, "挂失失败   --msg = " + event.getMsg(), false);
			 showToast(event.getMsg());
			 break;
		 }
	 }
	

	 @Override
	public void onClick(View v) {
		 switch (v.getId()) {
		case R.id.btn_cloud:
			btnAddOrLoss();
			break;
		}
		super.onClick(v);
	}
	@Override
	public void onCancelAlert() {
		super.onCancelAlert();
		if(etCardNumber.getText().toString().trim().length() > 0){
			showDialog(null,"您确定要放弃此编辑吗？",getString(R.string.cancel),getString(R.string.ok));
		}else{
			finish();
		}
	}
	
	@Override
	public void onConfirm() {
		super.onConfirm();
		finish();
	}

	public void btnAddOrLoss() {
		showProgressDialog(mContext, "", true, null);
		if(isSave){
			if(intentCardNumber == null || intentCardNumber.equals("")){
				//TODO 
				String data = etCardNumber.getText().toString();
				if(!"".equals(data) && data.length() > 0){
					getService().getCheckInManager().bindCheckInCard(data, childUserId);
				}else{
					showToast("输入内容不能为空");
				}
			}else{
				String data = etCardNumber.getText().toString();
				if("".equals(data) || data.length() > 0){
					getService().getCheckInManager().bindCheckInCard(data, childUserId);
				}else{
					showToast("输入内容不能为空");
				}
				
			}
			MobclickAgent.onEvent(mContext,"my_card_bound",UmengData.my_card_bound);
		}else{
			getService().getUserManager().unbindCheckInCard(etCardNumber.getText().toString());
			MobclickAgent.onEvent(mContext,"my_card_remove",UmengData.my_card_remove);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(etCardNumber.getText().toString().trim().length() > 0){
				showDialog(null,"您确定要放弃此编辑吗？",getString(R.string.cancel),getString(R.string.ok));
			}else{
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
