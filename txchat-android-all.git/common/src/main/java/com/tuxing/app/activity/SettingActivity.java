package com.tuxing.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.tuxing.app.LoginActivity;
import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.easemob.iml.TuxingHXSDKHelper;
import com.tuxing.app.util.MyLog;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.UmengData;
import com.tuxing.sdk.event.LoginEvent;
import com.tuxing.sdk.utils.Constants;
import com.umeng.analytics.MobclickAgent;
import com.xcsd.rpc.proto.EventType;

import static java.lang.Thread.sleep;

public class SettingActivity extends BaseActivity {
	
	public Button my_setting_exit_login;
	public ImageView iv_about_app;
	public ImageView iv_service_deal;
	public ImageView iv_msg_clear;
	public ImageView iv_msg_remind;
	private String userName = "";
	private Handler msgHandler = new Handler(){
		public void handleMessage(Message msg) {
			showToast("清空缓存成功");
		}
	};

	private boolean reportNowlogout = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.layout_setting);
		init();
		initView();
		
	}
	public void initView(){
		my_setting_exit_login = (Button)findViewById(R.id.my_setting_exit_login);
		iv_about_app = (ImageView) findViewById(R.id.iv_about_app);
		iv_service_deal = (ImageView) findViewById(R.id.iv_service_deal);
		iv_msg_clear = (ImageView) findViewById(R.id.iv_msg_clear);
		iv_msg_remind = (ImageView) findViewById(R.id.iv_msg_remind);
		my_setting_exit_login.setOnClickListener(this);
		if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())){
			my_setting_exit_login.setBackgroundColor(getResources().getColor(R.color.text_parent));
		}
	}

	private void init() {
		setTitle(getString(R.string.tab_setting));
		setLeftBack("", true,false);
		setRightNext(false, "", 0);
		
		findViewById(R.id.my_setting_msg_remind).setOnClickListener(this);
		findViewById(R.id.my_setting_msg_clear).setOnClickListener(this);
		findViewById(R.id.my_setting_service_deal).setOnClickListener(this);
		findViewById(R.id.my_setting_about_app).setOnClickListener(this);
	}
	
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		int id = v.getId();
		if(id == R.id.my_setting_msg_remind){
			openActivityOrFragment(new Intent(mContext, MsgRemindActivity.class));
			MobclickAgent.onEvent(mContext,"my_remind",UmengData.my_remind);
		}else if(id == R.id.my_setting_msg_clear){
			showBtnDialog(new String[] {getString(R.string.btn_clear),
					getString(R.string.btn_cancel) });
			MobclickAgent.onEvent(mContext,"my_exit",UmengData.my_exit);
		}else if(id == R.id.my_setting_service_deal){
			WebSubDataActivity.invoke(mContext,SysConstants.AGREEMENTURL_XCSD,getResources().getString(R.string.service_deal));
		}else if(id == R.id.my_setting_about_app){
			openActivityOrFragment(new Intent(mContext,AboutActivity.class));
		}
		else if(id == R.id.my_setting_exit_login){
			reportNowlogout = true;
			getService().getDataReportManager().reportEvent(EventType.APP_LOGOUT);
			getService().getDataReportManager().reportNow();
			showProgressDialog(mContext, "退出中", false, null);

//			TuxingApp.mPushAgent.disable();
//			getService().getLoginManager().logout();
		}
	}
	
	@Override
	public void onclickBtn1() {
		// TODO 清空消息
		super.onclickBtn1();
		showProgressDialog(mContext, "清除中...", false, null);
//		getService().getUserManager().clearUserCache();

		new Thread(new Runnable() {
			@Override
			public void run() {
				EMChatManager.getInstance().deleteAllConversation();
				msgHandler.sendEmptyMessage(0);
			}
		}).start();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				disProgressDialog();
			}
		},2000);
	}
	
	  public void onEventMainThread(LoginEvent event){
	        switch (event.getEvent()){
				case REOPRTNOEWLOGOUT:
					if (reportNowlogout){
						reportNowlogout = false;
						TuxingApp.mPushAgent.disable();
						getService().getLoginManager().logout();
					}
					break;
	            case LOGOUT:
					disProgressDialog();
					//环信退出
					TuxingHXSDKHelper.getInstance().logout(new EMCallBack() {
						@Override
						public void onSuccess() {
						}

						@Override
						public void onError(int i, String s) {

						}

						@Override
						public void onProgress(int i, String s) {

						}
					});
					sendBroadcast(new Intent(SysConstants.FINISH_MAIN));
					Intent intent = new Intent(mContext, LoginActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(intent);
					finish();


					break;
	            default:
	                break;
	        }
	    }
	  @Override
	protected void onResume() {
		  isActivity = true;
		super.onResume();
	}
	  

}
