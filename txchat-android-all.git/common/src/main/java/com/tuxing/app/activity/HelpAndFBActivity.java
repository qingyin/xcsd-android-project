package com.tuxing.app.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.mechat.mechatlibrary.MCClient;
import com.mechat.mechatlibrary.MCOnlineConfig;
import com.tuxing.app.R;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.view.ProgressHUD;
import com.tuxing.sdk.event.DebugFileEvent;
import com.tuxing.sdk.event.UploadFileEvent;
import com.tuxing.sdk.utils.Constants.ATTACHMENT_TYPE;

import java.io.File;

public class HelpAndFBActivity extends BaseActivity {
	
	public Button my_setting_exit_login;
    ProgressHUD _pdPUD;
	private String TAG = HelpAndFBActivity.class.getSimpleName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.layout_help);
		init();
	}

	private void init() {
		setTitle(getString(R.string.help_feedback));
		setLeftBack("", true,false);
		setRightNext(false, "", 0);
		findViewById(R.id.feedback_rl).setOnClickListener(this);
		findViewById(R.id.diagnose_rl).setOnClickListener(this);
		
	}
	
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		int id = v.getId();
		if(id == R.id.feedback_rl){
			//TODO 在线反馈
			intMeiqia();
		}else if(id == R.id.diagnose_rl){
			_pdPUD = ProgressHUD.show(mContext, "正在上传日志信息", true, null);
			zhenduan();
		}
	}

	
	private void zhenduan() {
		getService().getFileManager().collectDebugFile();
		
	}

	public void intMeiqia(){
		// 设置用户上线参数
		MCOnlineConfig onlineConfig = new MCOnlineConfig();
		// 启动客服对话界面
		MCClient.getInstance().startMCConversationActivity(onlineConfig);
	}
	
	 public void onEventMainThread(DebugFileEvent event){
		  disProgressDialog();
		  if(isActivity){
		  
	        switch (event.getEvent()){
	            case COLLECT_EVENT_SUCCESS:
	            	 getService().getFileManager().uploadFile(new File(event.getFilePath()), ATTACHMENT_TYPE.OTHER);
	            	showAndSaveLog(TAG, "打包成功  FilePath = " + event.getFilePath() , false);
	                break;
	            case COLLECT_EVENT_FAILED:
	            	disPd_pud();
	            	showToast(event.getMsg());
	            	showAndSaveLog(TAG, "打包失败" + event.getMsg(), false);
	            	break;
	            case UPLOAD_INFO_SUCCESS:
	            	showAndSaveLog(TAG, "上传fileK成功 " , false);
	            	break;
	            case UPLOAD_INFO_FAILED:
	            	showAndSaveLog(TAG, "上传fileK失败"  , false);
	            	break;
	        }}
	    }
	 
	 public void onEventMainThread(UploadFileEvent event){
		 if(isActivity){
	        switch (event.getEvent()){
	            case UPLOAD_COMPETED:
	            	disPd_pud();
					showToast("上传日志信息成功");
					getService().getFileManager().uploadDebugFileInfo(event.getAttachment().getFileUrl());
					showAndSaveLog(TAG, "上传zip成功" + event.getAttachment().getFileUrl(), false);
	                break;
	            case UPLOAD_FAILED:
	            	disPd_pud();
					 showDialog("", getString(R.string.upload_icon_msg), "", getResources().getString(R.string.btn_ok));
	            	showAndSaveLog(TAG, "上传zip失败"+ event.getMsg(), false);
	            	break;
	    } } }
	
	 /***
	     * 向服务器上报debug文件的key
	     * 上报成功，触发事件DebugFileEvent.EventType.UPLOAD_INFO_SUCCESS
	     * 上报失败，触发事件DebugFileEvent.EventType.UPLOAD_INFO_FAILED
	     *
	     * @param fileKey 文件key
	     */
//	    public void uploadDebugFileInfo(String fileKey);

	
	
	public static void delete(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}

		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				delete(childFiles[i]);
			}
			file.delete();
		}
	}
	
	public void disPd_pud(){
		if(_pdPUD != null){
			_pdPUD.dismiss();
		}
	}
	
}
