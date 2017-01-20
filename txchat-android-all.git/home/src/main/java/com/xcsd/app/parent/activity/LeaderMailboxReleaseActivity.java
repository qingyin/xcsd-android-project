package com.xcsd.app.parent.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.parent.R;
import com.tuxing.app.util.UmengData;
import com.tuxing.sdk.event.GardenMailEvent;
import com.umeng.analytics.MobclickAgent;

public class LeaderMailboxReleaseActivity extends BaseActivity {
	private EditText etRequest;
	private TextView surplusNum;
	private ToggleButton nimingButton;
	private TextView send;
	private String TAG = LeaderMailboxReleaseActivity.class.getSimpleName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.leader_mailbox_release_layout);
		init();
	}

	private void init() {

		setTitle(getString(R.string.mailbox));
		setLeftBack("取消", false,false);
		setRightNext(true, getString(R.string.button_send), 0);
		etRequest = (EditText) findViewById(R.id.et_mailbox);
		surplusNum = (TextView) findViewById(R.id.mailbox_surplus_num);
		nimingButton = (ToggleButton) findViewById(R.id.mailbox_niming_button);
		etRequest.addTextChangedListener(new MaxLengthWatcher(200, etRequest));
		
		
		nimingButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				nimingButton.setChecked(isChecked);
			}
		});
	}
	
	@Override
	public void onclickRightNext() {
		super.onclickRightNext();
		if(etRequest.getText().toString().trim().length() == 0){
			showToast("内容不能为空");
		}else{
			showProgressDialog(mContext, "", true, null);
			getService().getFeedManager().sendGardenMail(etRequest.getText().toString().trim(), nimingButton.isChecked());
			MobclickAgent.onEvent(mContext,"mail_release",UmengData.mail_release);
		}
	}
	
	 public void onEventMainThread(GardenMailEvent event){
		 if(isActivity){
			 switch (event.getEvent()){
			 case SEND_PRINCIPAL_MAIL_SUCCESS:
				 Intent intent = new Intent(mContext,LeaderMailBoxActivity.class);
				 setResult(RESULT_OK, intent);
				 finish();
				 disProgressDialog();
				 showAndSaveLog(TAG, "发送邮件成功", false);
				 break;
			 case SEND_PRINCIPAL_MAIL_FAILED:
				 disProgressDialog();
				 showDialog("", getString(R.string.replay_msg), "", getResources().getString(R.string.btn_ok));
				 showAndSaveLog(TAG, "发送邮件失败"+ event.getMsg(), false);
				 break;
			 }
		 } }



	public class MaxLengthWatcher implements TextWatcher {  
	    private int maxLen = 0;  
	    private EditText editText = null;  
	    public MaxLengthWatcher(int maxLen, EditText editText) {  
	        this.maxLen = maxLen;  
	        this.editText = editText;  
	    }  
	  
	    public void afterTextChanged(Editable arg0) {  
	    }  
	    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,  
	            int arg3) {  
	    }  
	    public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {  
	    	surplusNum.setText(s.length()+" ");
	        Editable editable = editText.getText();  
	        int len = editable.length();  
	        if(len >= maxLen)  
	        {  
	        	showToast(getResources().getString(R.string.edit_number_count));
	        }  
	    }  
	  
	}
	
}
