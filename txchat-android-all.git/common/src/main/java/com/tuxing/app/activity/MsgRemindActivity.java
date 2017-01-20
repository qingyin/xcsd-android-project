package com.tuxing.app.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.util.PreferenceUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.sdk.event.NotificationSettingEvent;
import com.tuxing.sdk.modle.NotificationSetting;
import com.tuxing.sdk.utils.Constants;

/**
 * 消息提醒设置界面
 * 
 * @author zhaomeng
 * 
 */
public class MsgRemindActivity extends BaseActivity implements OnClickListener {
	
	private static final String LOG_TAG = "MsgRemindActivity";
//	消息提醒声音框
	private CheckBox cbVoice;
//	新消息提醒震动框
	private CheckBox cbShake;
	private RadioGroup rgMdgDisturb;
//	免打扰选择时段
	private TextView rbAll;
	private TextView rbNight;
	private TextView rbClose;

	private String TAG  = MsgRemindActivity.class.getSimpleName();
	private NotificationSetting setting;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.msg_remind_layout);
		init();

	}
	private void init() {
		
		setTitle(getString(R.string.msg_remind));
		setLeftBack("", true, false);
		setRightNext(false, "", 0);
		cbVoice = (CheckBox) findViewById(R.id.cb_Voice);
		cbShake = (CheckBox) findViewById(R.id.cb_Shake);
		if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())){
			cbVoice.setBackground(getResources().getDrawable(R.drawable.ios7_btn_p));
			cbShake.setBackground(getResources().getDrawable(R.drawable.ios7_btn_p));
		}
		rbAll = (TextView) findViewById(R.id.rbAll);
		rbNight = (TextView) findViewById(R.id.rbNight);
		rbClose = (TextView) findViewById(R.id.rbClose);
		findViewById(R.id.rbAll_ll).setOnClickListener(this);
		findViewById(R.id.rbNight_ll).setOnClickListener(this);
		findViewById(R.id.rbClose_ll).setOnClickListener(this);
		
		getMsgRemind();
		cbVoice.setOnClickListener(this);
		cbShake.setOnClickListener(this);
		cbVoice.setChecked(PreferenceUtils.getPrefBoolean(mContext, SysConstants.voice_remind, true));
		cbShake.setChecked(PreferenceUtils.getPrefBoolean(mContext, SysConstants.shake_remind, true));
		int disturbId = PreferenceUtils.getPrefInt(mContext, SysConstants.remind_disturb, 0);
		setDisturb(disturbId);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		int id = v.getId();
		if (id == R.id.rbAll_ll) {
			isVisi(true,false,false);
			 setting = new NotificationSetting();
				setting.setDisturbFreeSetting(1);
				setMsgRemind(setting);
		} else if (id == R.id.rbNight_ll) {
			isVisi(false,true,false);
			setting = new NotificationSetting();
			setting.setDisturbFreeSetting(2);
			setMsgRemind(setting);
		} else if (id == R.id.rbClose_ll) {
			isVisi(false,false,true);
			setting = new NotificationSetting();
			setting.setDisturbFreeSetting(0);
			setMsgRemind(setting);
		}else if(id == R.id.cb_Voice){
			setting = new NotificationSetting();
			if(cbVoice.isChecked()){
				setSound(1);
				 setting.setEnableSound(1);
			}else{
				setSound(0);
				setting.setEnableSound(0);
			}
			setMsgRemind(setting);
		}else if(id == R.id.cb_Shake){
			setting = new NotificationSetting();
			if(cbShake.isChecked()){
				setVibration(1);
				 setting.setEnableVibration(1);
			}else{
				setVibration(0);
				 setting.setEnableVibration(0);
			}
			setMsgRemind(setting);
		}
	}
	
	private void setMsgRemind(NotificationSetting setSetting){
		getService().getUserManager().changeNotificationSetting(setSetting);
	}
	
	private void getMsgRemind(){
		getService().getUserManager().getNotificationSetting();
	}
	
	 public void onEventMainThread(NotificationSettingEvent event){
			 switch (event.getEvent()){
			 case GET_SETTING_SUCCESS:
				 setting = event.getSetting();
				 if(setting != null){
					 setDisturb(setting.getDisturbFreeSetting());
					 setSound(setting.getEnableSound());
					 setVibration(setting.getEnableVibration());
				 }
				 showAndSaveLog(TAG, "获取设置成功", false);
				 break;
			 case GET_SETTING_FAILED:
					int disturbId = PreferenceUtils.getPrefInt(mContext, SysConstants.remind_disturb, 0);
					setDisturb(disturbId);
					cbVoice.setChecked(PreferenceUtils.getPrefBoolean(mContext, SysConstants.voice_remind, true));
					cbShake.setChecked(PreferenceUtils.getPrefBoolean(mContext, SysConstants.shake_remind, true));
					showToast(event.getMsg());
				 showAndSaveLog(TAG, "获取设置失败"+ event.getMsg(), false);
				 break;
			 case CHANGE_SETTING_SUCCESS:
				 showAndSaveLog(TAG, "修改设置成功", false);
				 break;
			 case CHANGE_SETTING_FAILED:
				 showToast(event.getMsg());
				 showAndSaveLog(TAG, "修改设置失败"+ event.getMsg(), false);
				 break;
			 }
		 } 
	
	 /**
	  * 0关闭
	  * 1开启
	  * @param sound
	  */
	 public void setSound(Integer sound){
		
		 switch (sound) {
			case 0:
				PreferenceUtils.setPrefBoolean(mContext, SysConstants.voice_remind, false);
				cbVoice.setChecked(false);
				break;
			case 1:
				PreferenceUtils.setPrefBoolean(mContext, SysConstants.voice_remind, true);
				cbVoice.setChecked(true);
				break;
			}
	 }
	 public void setVibration(Integer vibration){
		 switch (vibration) {
		 case 0:
			 PreferenceUtils.setPrefBoolean(mContext, SysConstants.shake_remind, false);
			 cbShake.setChecked(false);
			 break;
		 case 1:
			 PreferenceUtils.setPrefBoolean(mContext, SysConstants.shake_remind, true);
			 cbShake.setChecked(true);
			 break;
		 }
	 }

	 /**
	  * 0 关闭
	  * 1开启
	  * 2夜间
	  * @param disturb
	  */
	 public void setDisturb(Integer disturb){
		 switch (disturb) {
			case 0:
				isVisi(false,false,true);
				break;
			case 1:
				isVisi(true,false,false);
				break;
			case 2:
				isVisi(false,true,false);
				
				break;
			}
	 }
	
	public void isVisi(boolean isAll,boolean isNight,boolean isClose){
		if(isAll){
			rbAll.setVisibility(View.VISIBLE);
			rbNight.setVisibility(View.GONE);
			rbClose.setVisibility(View.GONE);
			if(PreferenceUtils.getPrefInt(mContext, SysConstants.remind_disturb, 0) == 1){
				return;
			}
			PreferenceUtils.setPrefInt(mContext, SysConstants.remind_disturb, 1);
		}else if(isNight){
			rbNight.setVisibility(View.VISIBLE);
			rbAll.setVisibility(View.GONE);
			rbClose.setVisibility(View.GONE);
			if(PreferenceUtils.getPrefInt(mContext, SysConstants.remind_disturb, 0) == 2){
				return;
			}
			PreferenceUtils.setPrefInt(mContext, SysConstants.remind_disturb, 2);
		}else if(isClose){
			rbClose.setVisibility(View.VISIBLE);
			rbAll.setVisibility(View.GONE);
			rbNight.setVisibility(View.GONE);
			if(PreferenceUtils.getPrefInt(mContext, SysConstants.remind_disturb, 0) == 0){
				return;
			}
			PreferenceUtils.setPrefInt(mContext, SysConstants.remind_disturb, 0);
		}
	}

}
