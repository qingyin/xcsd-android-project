
package com.tuxing.app.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.*;
import android.content.DialogInterface.OnCancelListener;
import android.database.ContentObserver;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.easemob.EMCallBack;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.tuxing.app.MainActivity;
import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.easemob.iml.TuxingHXSDKHelper;
import com.tuxing.app.ui.dialog.CommonDialogReward;
import com.tuxing.app.ui.dialog.DialogHelper;
import com.tuxing.app.util.*;
import com.tuxing.app.view.CustomDialog;
import com.tuxing.app.view.DialogAlbumView;
import com.tuxing.app.view.MyToast;
import com.tuxing.app.view.ProgressHUD;
import com.tuxing.sdk.db.entity.Department;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.event.NetworkEvent;
import com.tuxing.sdk.event.UserCheckInEvent;
import com.tuxing.sdk.event.UserEvent;
import com.tuxing.sdk.facade.CoreService;
import com.tuxing.sdk.modle.Attachment;
import com.tuxing.sdk.utils.Constants;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.IUmengUnregisterCallback;
import com.xcsd.rpc.proto.EventType;

import de.greenrobot.event.EventBus;

import java.util.*;

public class BaseActivity extends FragmentActivity implements OnClickListener {

	private Dialog centerDialog;
	protected Context mContext;
	public InputMethodManager manager;
	public TextView tv_title;
	public LinearLayout ll_left;//左边layout
	public TextView tv_left_text;//左边按钮
	public ProgressBar pullProgressBar;
	public TextView tv_left;//左标题

	public Button tv_right;//右标题
	public ImageView iv_right;//右图标
	public ImageView iv_second_right;//右图标
	public LinearLayout ll_right;
	public LinearLayout ll_second_right;
	public TuxingApp app;
	private String[] btnNams;
	protected boolean activityExist = true;
	private Dialog bottomDialog;
	private ProgressHUD progressDialog;
	//换头像时使用
	public User user;
	public LinearLayout llContent;
	public RelativeLayout networkErroy;
	public View contentView;
	public boolean isActivity = false;
	public String TAG = BaseActivity.class.getSimpleName();
	//qzq
	private RelativeLayout rl_title_bar;
	private CommonDialogReward dialogReward = null;
	private boolean isteacher=false;
	private boolean isparent=false;


	public BaseActivity() {
		mContext = BaseActivity.this;
	}

	/** 手势监听 */
	GestureDetector mGestureDetector;
	/** 是否需要监听手势关闭功能 */
	private boolean mNeedBackGesture = false;
	public SharedPreferences sys_config;
	private CustomDialog dialog;
	private TouChuanReceiver receiver;
	private PopupWindow mPopuwindows;
	public RotationObserver mRotationObserver;

	public boolean isForeground = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		app = (TuxingApp) this.getApplication();
		user = CoreService.getInstance().getLoginManager().getCurrentUser();
		manager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		setContentView(R.layout.layout_base);
		 isActivity = true;
//		startIMService();
		EventBus.getDefault().register(this);
		setupView();
		receiver = new TouChuanReceiver();
		registerReceiver(receiver, new IntentFilter(SysConstants.TOUCHUANACTION));
		SysConstants.listActivitys.add(this);
//		showToast("添加了listActivitys");
	}

	public CoreService getService(){
		return CoreService.getInstance();
	}

	public void setupView() {
		llContent = (LinearLayout) findViewById(R.id.content);
		ll_left = (LinearLayout)findViewById(R.id.ll_left);
		tv_left = (TextView)findViewById(R.id.tv_left);
		tv_left_text = (TextView ) findViewById(R.id.tv_left_text);//返回图标
		tv_title = (TextView) findViewById(R.id.tv_title);//标题
		tv_right = (Button) findViewById(R.id.tv_right);//右边文字,如下一步 确定
		ll_right = (LinearLayout)findViewById(R.id.ll_right);
		iv_right = (ImageView)findViewById(R.id.iv_right);//右边图标
		iv_second_right = (ImageView)findViewById(R.id.iv_second_right);//右边图标
		ll_second_right = (LinearLayout)findViewById(R.id.ll_second_right);
		networkErroy = (RelativeLayout) findViewById(R.id.network_erroy);
		//qzq
		rl_title_bar = (RelativeLayout)findViewById(R.id.rl_title_bar);

		isteacher = TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion());
		isparent = TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion());
		if (isteacher){
			tv_left.setTextColor(getResources().getColor(R.color.text_teacher));
			tv_right.setTextColor(getResources().getColor(R.color.text_teacher));
			Drawable drawable = null;
			drawable = getResources().getDrawable(R.drawable.ic_back_title_t);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
			tv_left.setCompoundDrawables(drawable,null,null,null);
		}else if(isparent){
			tv_left.setTextColor(getResources().getColor(R.color.text_parent));
			tv_right.setTextColor(getResources().getColor(R.color.text_parent));
			Drawable drawable = null;
			drawable = getResources().getDrawable(R.drawable.ic_back_title_p);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
			tv_left.setCompoundDrawables(drawable,null,null,null);
		}
	}
	public void setQzqTitleBarBg(boolean isMyself){
		if(isMyself){//自己
			setRightNext(false,"", R.drawable.ic_circle_more);
		}else{
			setRightNext(false, "", 0);
		}
		tv_left.setTextColor(getResources().getColor(R.color.white));
//		rl_title_bar.setBackgroundColor(Color.argb(225,0,0,0));
		rl_title_bar.setBackground(getResources().getDrawable(R.drawable.qzq_title_bar_bg));
		tv_title.setTextColor(getResources().getColor(R.color.white));
		Drawable drawable= getResources().getDrawable(R.drawable.ic_back_white);
		/// 这一步必须要做,否则不会显示.
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		tv_left.setCompoundDrawables(drawable, null, null, null);
		iv_right.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle_more_white));
	}

	/***
	 * 设置内容区域
	 * @param
	 */
	public void setContentLayout(int layoutResId) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		contentView = inflater.inflate(layoutResId, null);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		contentView.setLayoutParams(layoutParams);
		if (null != llContent) {
			llContent.addView(contentView);
		}
	}
	/***
	 * 设置内容区域
	 * @param view
	 */
	public void setContentLayout(View view) {
		if (null != llContent) {
			llContent.addView(view);
		}
	}
	/**
	 * 得到内容的View
	 * @return
	 */
	public View getLyContentView() {
		return contentView;
	}
	@Override
	protected void onResume() {

		if (isForeground) {
			getService().getDataReportManager().reportEvent(EventType.ENTER_FOREGROUND);
//			showToast("app 从后台唤醒，进入前台");
			isForeground=false;
		}

		// TODO Auto-generated method stub
		super.onResume();
		isActivity = true;
		if(user == null){
			user = CoreService.getInstance().getLoginManager().getCurrentUser();
		}
		TuxingHXSDKHelper.getInstance().getNotifier().reset();
		MobclickAgent.onResume(this);
		if(!this.getClass().getSimpleName().contains("MainActivity")){
			if(this.getClass().getSimpleName().contains("ChatActivity")){
					MobclickAgent.onPageStart("聊天界面");
			}else if(!TextUtils.isEmpty(tv_title.getText().toString())){
					MobclickAgent.onPageStart(tv_title.getText().toString());
			}else{
				MobclickAgent.onPageStart(this.getClass().getSimpleName());
			}
			}
	}

	/**
	 * 显示键盘
	 *
	 * @param edit
	 */
	public void showInput(final EditText edit) {
		edit.setFocusable(true);
		edit.setFocusableInTouchMode(true);
		edit.requestFocus();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				InputMethodManager inputManager = (InputMethodManager) mContext
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(edit, 0);
			}
		}, 300);
	}

	/**
	 * 隐藏键盘
	 */
	public void hiddenInput(Activity activity) {
		manager.hideSoftInputFromWindow(activity.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		if(dialog!=null){
			dialog.dismiss();
		}
	}

	private void changeDialogBtStyle(View view) {
		//改变窗口按钮的颜色样式
		if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())) {//家长
			view.setBackgroundResource(R.drawable.dialog_parent_btn_selector);
		} else if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {//教师
			view.setBackgroundResource(R.drawable.dialog_teacher_btn_selector);
		} else {//园长
			view.setBackgroundResource(R.drawable.dialog_kindergarten_btn_selector);
		}
	}

	/**
	 * 文字居左
	 * @param title
	 * @param content
	 * @param cancel
	 * @param confirm
	 * @param isCanceled
	 */
	public void showDialog(String title,String content,String cancel,String confirm,boolean isCanceled) {
		if (dialog != null && dialog.isShowing()) {
			return;
		}else{
			View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.customdialog, null);
			dialog = new CustomDialog(this, R.style.dialog_alert_style,0);

			// 根据id在布局中找到控件对象
			TextView tv_dialog_title = (TextView)view.findViewById(R.id.tv_dialog_title);
			TextView tv_dialog_content = (TextView)view.findViewById(R.id.tv_dialog_content);
			Button confirmBtn = (Button) view.findViewById(R.id.confirm_btn);
			Button cancelBtn = (Button) view.findViewById(R.id.cancel_btn);

			if (title != null && !"".equals(title)) {
				tv_dialog_title.setText(title);
				tv_dialog_title.setVisibility(View.VISIBLE);
			} else {
				tv_dialog_title.setVisibility(View.GONE);
			}
			if (content != null && !"".equals(content)) {
				tv_dialog_content.setText(content);
				tv_dialog_content.setVisibility(View.VISIBLE);
			} else {
				tv_dialog_content.setVisibility(View.GONE);
			}
			if (cancel != null && !"".equals(cancel)) {
				cancelBtn.setOnClickListener(this);
				cancelBtn.setText(cancel);
				cancelBtn.setVisibility(View.VISIBLE);
			} else {
				cancelBtn.setVisibility(View.GONE);
			}
			if (confirm != null && !"".equals(confirm)) {
				confirmBtn.setText(confirm);
				confirmBtn.setOnClickListener(this);
				confirmBtn.setVisibility(View.VISIBLE);
			} else {
				confirmBtn.setVisibility(View.GONE);
			}
			changeDialogBtStyle(confirmBtn);
			dialog.setContentView(view, new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			dialog.setCanceledOnTouchOutside(false);
			dialog.setCancelable(isCanceled);
			dialog.show();
		}
	}

	@Override
	protected void onDestroy() {
		activityExist = false;
		super.onDestroy();
		//added by wangst
		if(receiver != null){
			unregisterReceiver(receiver);
		}

		disProgressDialog();
		EventBus.getDefault().unregister(this);
		SysConstants.listActivitys.remove(this);
		if(dialogReward!=null){
			dialogReward.dismiss();
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (mNeedBackGesture) {
			return mGestureDetector.onTouchEvent(ev)
					|| super.dispatchTouchEvent(ev);
		}
		return super.dispatchTouchEvent(ev);
	}

	/*
	 * 设置是否进行手势监听
	 */
	public void setNeedBackGesture(boolean mNeedBackGesture) {
		this.mNeedBackGesture = mNeedBackGesture;
	}

	/*
	 * 返回
	 */
	public void doBack(View view) {
		onBackPressed();
	}

	/**
	 * Takes a given intent and either starts a new activity to handle it (the
	 * default behavior), or creates/updates a fragment (in the case of a
	 * multi-pane activity) that can handle the intent.
	 *
	 * Must be called from the main (UI) thread.
	 */
	public void openActivityOrFragment(Intent intent) {
		// Default implementation simply calls startActivity
		startActivity(intent);
	}

	/**
	 * Converts an intent into a {@link Bundle} suitable for use as fragment
	 * arguments.
	 */
	public static Bundle intentToFragmentArguments(Intent intent) {
		Bundle arguments = new Bundle();
		if (intent == null) {
			return arguments;
		}

		final Uri data = intent.getData();
		if (data != null) {
			arguments.putParcelable("_uri", data);
		}

		final Bundle extras = intent.getExtras();
		if (extras != null) {
			arguments.putAll(intent.getExtras());
		}

		return arguments;
	}

	/**
	 * Converts a fragment arguments bundle into an intent.
	 */
	public static Intent fragmentArgumentsToIntent(Bundle arguments) {
		Intent intent = new Intent();
		if (arguments == null) {
			return intent;
		}

		final Uri data = arguments.getParcelable("_uri");
		if (data != null) {
			intent.setData(data);
		}

		intent.putExtras(arguments);
		intent.removeExtra("_uri");
		return intent;
	}


	private List<SelectedImage> imageList = Collections.synchronizedList(new ArrayList<SelectedImage>());
	boolean isSysncPic;

	protected class SelectedImage {
		public String imagePath;
		public Attachment attachment;
		public boolean isCompressSuccess;

		public SelectedImage() {
		}

		public SelectedImage(String imagePath, Attachment attachment, boolean isCompressSuccess) {
			this.imagePath = imagePath;
			this.attachment = attachment;
			this.isCompressSuccess = isCompressSuccess;
		}
	}

	@Override
	public void finish() {
		super.finish();
		final View v = getWindow().peekDecorView();
    	if (v != null && v.getWindowToken() != null) {
    		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    	}
			overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
		}

	public void setLeftBack(String text,boolean isBack,final boolean isAlert){
		if(isBack){
			if (null != tv_left_text) {
				tv_left_text.setVisibility(View.GONE);
			}
			if (null != tv_left) {
				tv_left.setVisibility(View.VISIBLE);
			}
			ll_left.setVisibility(View.VISIBLE);
			ll_left.setOnClickListener(this);
			ll_left.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();//返回
				}
			});
		}else{//取消
			if (null != tv_left) {
				tv_left.setVisibility(View.GONE);
			}
			if(null!=tv_left_text){
				tv_left_text.setVisibility(View.VISIBLE);
				tv_left_text.setText(text);
				ll_left.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(isAlert){
							onCancelAlert();
						}else
							finish();//例如 取消
					}
				});
			}
		}
	}
	public void setRightNext(boolean isText,String text,int resId){
//		tv_left.setTextColor(getResources().getColor(R.color.black));
//		rl_title_bar.setBackgroundColor(Color.argb(0, 255, 255, 255));
////		rl_title_bar.setBackground(getResources().getDrawable(R.drawable.qzq_title_bar_bg));
//		tv_title.setTextColor(getResources().getColor(R.color.black));
		if(isText){//例如 下一步
			if("".equals(text)){
				iv_right.setVisibility(View.GONE);
				ll_right.setVisibility(View.GONE);
				tv_right.setVisibility(View.GONE);
			}else{
				if (null != iv_right) {
					ll_right.setVisibility(View.GONE);
					iv_right.setVisibility(View.GONE);
				}
				if (null != tv_right) {
					tv_right.setVisibility(View.VISIBLE);
					tv_right.setText(text);

					tv_right.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							onclickRightNext();
						}
					});
				}
			}
		}else{//icon
			if(resId==0){
				if (null != iv_right) {
					ll_right.setVisibility(View.GONE);
					iv_right.setVisibility(View.GONE);
				}
			}else{
				if (null != tv_right) {
					tv_right.setVisibility(View.GONE);
				}
				if (null != iv_right) {
					ll_right.setVisibility(View.VISIBLE);
					iv_right.setVisibility(View.VISIBLE);
					iv_right.setImageResource(resId);
					ll_right.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							onclickRightImg();
						}
					});
				}
			}
		}
	}
	public void setSecondRight(boolean isShow,int resId) {
		if (isShow) {
			if (resId == 0) {
				if (null != iv_second_right) {
					ll_second_right.setVisibility(View.GONE);
					iv_second_right.setVisibility(View.GONE);
				}
			} else {
				if (null != iv_second_right) {
					ll_second_right.setVisibility(View.VISIBLE);
					iv_second_right.setVisibility(View.VISIBLE);
					iv_second_right.setImageResource(resId);
					ll_second_right.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							onclickSecondRightImg();
						}
					});
				}
			}
		}
	}
	public void setRightNext(boolean isText,String text,int resId,final  boolean isclick){
		if(isText){//例如 下一步
			if("".equals(text)){
				iv_right.setVisibility(View.GONE);
				ll_right.setVisibility(View.GONE);
				tv_right.setVisibility(View.GONE);
			}else{
				if (null != iv_right) {
					ll_right.setVisibility(View.GONE);
					iv_right.setVisibility(View.GONE);
				}
				if (null != tv_right) {
					tv_right.setVisibility(View.VISIBLE);
					tv_right.setText(text);
					tv_right.setClickable(isclick);
					if(isclick){
						tv_right.setTextColor(getResources().getColor(R.color.black));
						tv_right.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								onclickRightNext();
							}
						});
					}else
						tv_right.setTextColor(getResources().getColor(R.color.bg));
				}
			}
		}else{//icon
			if(resId==0){
				if (null != iv_right) {
					ll_right.setVisibility(View.GONE);
					iv_right.setVisibility(View.GONE);
				}
			}else{
				if (null != tv_right) {
					tv_right.setVisibility(View.GONE);
				}
				if (null != iv_right) {
					ll_right.setVisibility(View.VISIBLE);
					iv_right.setVisibility(View.VISIBLE);
					iv_right.setImageResource(resId);
					ll_right.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							onclickRightImg();
						}
					});
				}
			}
		}
	}

	public void setsetRightNextColor(int colorId){
		if(tv_right != null){
			tv_right.setTextColor(getResources().getColor(colorId));
		}
	}


	public void showDialog(String title,String content,String cancel,String confirm) {
		if (dialog != null && dialog.isShowing()) {
			return;
		}else{
			View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.customdialog, null);
			dialog = new CustomDialog(this, R.style.dialog_alert_style,0);

			// 根据id在布局中找到控件对象
			TextView tv_dialog_title = (TextView)view.findViewById(R.id.tv_dialog_title);
			TextView tv_dialog_content = (TextView)view.findViewById(R.id.tv_dialog_content);
			Button confirmBtn = (Button) view.findViewById(R.id.confirm_btn);
			if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())){
				confirmBtn.setBackgroundColor(getResources().getColor(R.color.text_parent));
			}
			Button cancelBtn = (Button) view.findViewById(R.id.cancel_btn);


			if(title!=null&&!"".equals(title)){
				tv_dialog_title.setText(title);
				tv_dialog_title.setVisibility(View.VISIBLE);
			}else{
				tv_dialog_title.setVisibility(View.GONE);
			}
			if(content!=null&&!"".equals(content)){
				tv_dialog_content.setText(content);
				tv_dialog_content.setVisibility(View.VISIBLE);
			}else{
				tv_dialog_content.setVisibility(View.GONE);
			}
			if(cancel!=null&&!"".equals(cancel)){
				cancelBtn.setOnClickListener(this);
				cancelBtn.setText(cancel);
				cancelBtn.setVisibility(View.VISIBLE);
			}else {
				cancelBtn.setVisibility(View.GONE);
			}
			if(confirm!=null&&!"".equals(confirm)){
				confirmBtn.setText(confirm);
				confirmBtn.setOnClickListener(this);
				confirmBtn.setVisibility(View.VISIBLE);
			}else
				confirmBtn.setVisibility(View.GONE);

			dialog.setContentView(view, new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}
	}

	@Override
	public void onClick(View v) {
		if (bottomDialog != null && bottomDialog.isShowing()) {
			bottomDialog.dismiss();
		}
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}

		if (v.getId() == R.id.confirm_btn) {
			onConfirm();
		} else if (v.getId() == R.id.cancel_btn) {
			onCancel();
		}else if (v.getId() == R.id.btn_dialog1) {
			onclickBtn1();
		} else if (v.getId() == R.id.btn_dialog2) {
			onclickBtn2();
		} else if (v.getId() == R.id.btn_dialog3) {
			onclickBtn3();
		} else if (v.getId() == R.id.btn_dialog4) {
			onclickBtn4();
		} else if (v.getId() == R.id.media_flow_all) {
			if (centerDialog.isShowing()){
				centerDialog.dismiss();
			}
			onclickFlowAll();
		} else if (v.getId() == R.id.media_flow_cancle) {
			if (centerDialog.isShowing()){
				centerDialog.dismiss();
			}
			onclickflowCancle();
		}
	}

	public void back(View view) {
		finish();
	}

	public void onCancel() {

	}

	public void onConfirm() {
	}

	public void onclickBtn4() {
	}
	public void onclickBtn3() {
	}
	public void onclickBtn2() {
	}
	public void onclickBtn1() {
	}
	public void onclickRightImg() {
	}
	public void onclickSecondRightImg() {
	}
	public void onclickRightNext() {
	}
	public void onCancelAlert() {
	}
	public void setTextClour() {
	}
	public void onclickFlowAll() {
	}
	public void onclickflowCancle() {
	}


	/**
	 * 底部dialog
	 */
	public void showBottomDialog() {
		List<Button> btnList = new ArrayList<Button>();
		btnList.clear();
		View view = getLayoutInflater().inflate(R.layout.dialog_view, null);
		Button btn1 = (Button) view.findViewById(R.id.btn_dialog1);
		Button btn2 = (Button) view.findViewById(R.id.btn_dialog2);
		Button btn3 = (Button) view.findViewById(R.id.btn_dialog3);
		Button btn4 = (Button) view.findViewById(R.id.btn_dialog4);

		if (btnNams != null) {
			switch (btnNams.length) {
				case 4:
					btn4.setVisibility(View.VISIBLE);
					btn4.setText(btnNams[3]);
					btn4.setOnClickListener(this);
					btnList.add(btn4);
				case 3:
					btn3.setVisibility(View.VISIBLE);
					btn3.setText(btnNams[2]);
					btn3.setOnClickListener(this);
					btnList.add(btn3);
				case 2:
					btn2.setVisibility(View.VISIBLE);
					btn2.setText(btnNams[1]);
					btn2.setOnClickListener(this);
					btnList.add(btn2);
				case 1:
					btn1.setVisibility(View.VISIBLE);
					btn1.setText(btnNams[0]);
					btn1.setOnClickListener(this);
					btnList.add(btn1);
				default:
					break;
			}
		}
		btnList.get(0).setTextColor(getResources().getColor(R.color.text_gray));
		bottomDialog = new Dialog(this, R.style.transparentFrameWindowStyle);
		bottomDialog.setContentView(view, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		Window window = bottomDialog.getWindow();
		// 设置显示动画
		window.setWindowAnimations(R.style.main_menu_animstyle);
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.x = 0;
		wl.y = getWindowManager().getDefaultDisplay().getHeight();
		wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		// 设置显示位置
		bottomDialog.onWindowAttributesChanged(wl);
		// 设置点击外围解散
		bottomDialog.setCanceledOnTouchOutside(true);
		bottomDialog.show();
	}
	/**
	 * 底部dialog
	 * @param btnNames
	 */
	public void showBtnDialog(String[] btnNames) {
		this.btnNams = btnNames;
		showBottomDialog();
	}

	/**
	 * 等待加载框
	 * @param context
	 * @param message
	 * @param cancelable
	 * @param cancelListener
	 */
	public void showProgressDialog(Context context, CharSequence message, boolean cancelable,
								   OnCancelListener cancelListener){
		if(progressDialog == null||(progressDialog != null&&!progressDialog.isShowing())){
			progressDialog = ProgressHUD.show(context, message, cancelable, cancelListener);
		}
	}

	/**
	 * 取消等待框
	 */
	public void disProgressDialog(){
		if(progressDialog != null && progressDialog.isShowing()){
			progressDialog.dismiss();
		}
	}


	/**
	 * Toast提示
	 *
	 * @param msg
	 */
	public void showToast(String msg) {
		MyToast.showToast(mContext, msg, 1000);

	}

	/**
	 * 打印或保存Log
	 * @param tag
	 * @param msg
	 * @param isSave
	 */
	public void showAndSaveLog(String tag,String msg,boolean isSave){
		if(isSave){

		}else{
			MyLog.getLogger(tag).d(msg);
			Log.i(tag, msg);
		}
	}


	/**
	 * 设置标题
	 * @param title
	 */
	public void setTitle(String title) {
		if (null != tv_title) {
			tv_title.setText(title);
		}
	}
	public View getLeftText() {
		return tv_left_text;
	}
	public View getLeftBtnText() {
		return tv_left;
	}
	public View getRightText() {
		return tv_right;
	}
	public View getRightImg() {
		return iv_right;
	}

	public void onEventMainThread(NetworkEvent event){
		switch (event.getEvent()){
			case NETWORK_AVAILABLE:
				networkAvailable();
				getService().getUserManager().updateUserProfile();//更新个人profile 跟ios一样
				showAndSaveLog(TAG, "网络连接成功 ", false);
				break;
			case NETWORK_UNAVAILABLE:
				networkUnAvailable();
				disProgressDialog();
				showAndSaveLog(TAG, "网络连接失败" + event.getMsg(), false);
				break;
			case NETWORK_REQUEST_TIMEOUT:
				disProgressDialog();
				timeOut();
				showAndSaveLog(TAG, "请求数据超时"+ event.getMsg(), false);
				break;
		} }

	@Override
	protected void onPause() {

//		if (!isAppOnForeground()) {
//		}

		isActivity = false;
		MobclickAgent.onPause(this);
		if(!this.getClass().getSimpleName().contains("MainActivity")){
			if(this.getClass().getSimpleName().contains("ChatActivity")){
				MobclickAgent.onPageEnd("聊天界面");
			}else if(!TextUtils.isEmpty(tv_title.getText().toString())){
				MobclickAgent.onPageEnd(tv_title.getText().toString());
			}else{
				MobclickAgent.onPageEnd(this.getClass().getSimpleName());
			}
		}
		super.onPause();
	}


	public  void getData(){};
	public  void networkAvailable(){};
	public  void networkUnAvailable(){};
	public  void timeOut(){};
	
	/**
	 * touchuan
	 * @param flagContent
	 */
	private String flagContent;
	public void sendTouChuan(final String flagContent){
		EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
		cmdMsg.setChatType(ChatType.GroupChat);
		String action = flagContent;
		CmdMessageBody cmdBody=new CmdMessageBody(action);
		List<Department> allDepartment = getService().getContactManager().getAllDepartment();
		for(int i = 0; i < allDepartment.size(); i++){
			cmdMsg.setReceipt(String.valueOf(allDepartment.get(i).getChatGroupId()));
			cmdMsg.setAttribute("a", "a");//支持自定义扩展
			cmdMsg.addBody(cmdBody); 
			EMChatManager.getInstance().sendMessage(cmdMsg, new EMCallBack() {
				
				@Override
				public void onSuccess() {
					MyLog.getLogger(Utils.class.getSimpleName()).d("发送透传成功 action = " + flagContent);
				}
				
				@Override
				public void onProgress(int arg0, String arg1) {
				}
				
				@Override
				public void onError(int arg0, String arg1) {
					MyLog.getLogger(Utils.class.getSimpleName()).d("发送透传失败  action = " + flagContent);
				}
			});
		}
		
	}
	
	class TouChuanReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(user!=null&&intent.getAction() == SysConstants.TOUCHUANACTION){
				String flagContent = intent.getStringExtra("cmd_value");
				if(flagContent.equals("unbindUser")){
                	//TODO 同步联系人信息
					getService().getContactManager().syncContact();
                }else if(flagContent.equals("gagUser") || flagContent.equals("profileChange")){
                	//TODO 同步禁言列表
                	getService().getUserManager().updateUserProfile();//更新个人profile
                }
			}
		}
	}


//	ArrayList<Activity> listActivitys = new ArrayList<Activity>();
	/**
	 * Activity关闭时，删除Activity列表中的Activity对象*/
	public void removeActivity(Activity a){
		SysConstants.listActivitys.remove(a);
	}

	/**
	 * 向Activity列表中添加Activity对象*/
	public void addActivity(Activity a){
		SysConstants.listActivitys.add(a);
	}

	/**
	 * 关闭Activity列表中的所有Activity*/
	public void finishActivity(){
		for (Activity activity : SysConstants.listActivitys) {
			if (null != activity) {
				activity.finish();
			}
		}
		//杀死该应用进程
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	public IUmengRegisterCallback mRegisterCallback = new IUmengRegisterCallback() {

		@Override
		public void onRegistered(String registrationId) {
			// TODO Auto-generated method stub
			MyLog.getLogger(TAG).d("mRegisterCallback");
		}
	};

	public IUmengUnregisterCallback mUnregisterCallback = new IUmengUnregisterCallback() {

		@Override
		public void onUnregistered(String registrationId) {
			// TODO Auto-generated method stub
			MyLog.getLogger(TAG).d("mUnregisterCallback");
		}
	};
	/**
	 * 如果被禁言
	 * @return
	 */
	public  boolean checkFeedMute(){
		String tempString = getService().getUserManager().getUserProfile(Constants.SETTING_FIELD.FEED_MUTE,"");
		if(!TextUtils.isEmpty(tempString)&&"true".equals(tempString)){//禁言
			showToast("亲子圈暂不可用");
			return true;
		}
		return false;
	}

    public void onEventMainThread(UserCheckInEvent event){
        if(event.getEvent() == UserCheckInEvent.EventType.USER_CHECK_IN_SUCCESS){
            String today = DateTimeUtils.getDateString(new Date(), "yyyyMMdd");
			PreferenceUtils.setPrefString(this, user.getUserId() + "_latest_check_in_date", today);

			if(event.getBonus()>0){
//				微豆签到取消
//				showContextMenuCheckin(event.getBonus());
			}
        }
    }

	public void showPopupWindow(View view){

		TextView anim = new TextView(this);
		anim.setText("+1");
		anim.setTextColor(getResources().getColor(R.color.teacher_help_theme_color));
		anim.setTextSize(16);

		mPopuwindows = new PopupWindow(anim, Utils.dip2px(mContext, 20), Utils.dip2px(mContext, 20), false);
		mPopuwindows.setFocusable(true);
		mPopuwindows.setTouchable(true);
		mPopuwindows.setOutsideTouchable(true);
		mPopuwindows.setAnimationStyle(com.tuxing.app.R.style.like_anim2);
		mPopuwindows.setBackgroundDrawable(new ColorDrawable());
		int loc[] = new int[2];
		view.getLocationInWindow(loc);
		mPopuwindows.showAtLocation(view, Gravity.NO_GRAVITY, loc[0] + view.getWidth() / 2, loc[1]);
		view.postDelayed(new Runnable() {
			@Override
			public void run() {
				mPopuwindows.dismiss();
			}
		}, 500);
	}

	public void showCenterBtnDialog() {
		if (centerDialog != null && centerDialog.isShowing()) {
			return;
		}else{
			View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_dialog, null);
			centerDialog = new CustomDialog(this, R.style.dialog_alert_style,0);


			Button flowAll = (Button) view.findViewById(R.id.media_flow_all);
			Button flowCancle = (Button) view.findViewById(R.id.media_flow_cancle);
			flowAll.setOnClickListener(this);
			flowCancle.setOnClickListener(this);


			centerDialog.setContentView(view, new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			centerDialog.setCanceledOnTouchOutside(false);
			centerDialog.show();
		}
	}

	public void screenOrientAtion(){};

	//监听屏幕旋转设置改变
	public class RotationObserver extends ContentObserver {
		ContentResolver mResolver;
		public RotationObserver(Handler handler) {
			super(handler);
			mResolver = getContentResolver();
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			if(Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1){
				screenOrientAtion();
			}
		}

		public void startObserver(){
			mResolver.registerContentObserver(Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION), false,this);
		}

		public void stopObserver() {
			mResolver.unregisterContentObserver(this);
		}
	}

//	public void showContextMenuCheckin(int score) {
//		dialogReward = new CommonDialogReward(mContext,
//				R.style.dialog_common_checkin);
//		dialogReward.setCanceledOnTouchOutside(true);
//		dialogReward.setTitle("");
//		final LinearLayout rl_common = (LinearLayout)dialogReward.findViewById(R.id.rl_common);
//		final TextView tv_score_number_checkin = (TextView)dialogReward.findViewById(R.id.tv_score_number_checkin);
//		Window dialogWindow = dialogReward.getWindow();
//		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//		lp.dimAmount=0.3f;
//		dialogWindow.setGravity(Gravity.CENTER);
//		dialogWindow.setAttributes(lp);
//		dialogReward.show();
//		rl_common.setVisibility(View.VISIBLE);
//		tv_score_number_checkin.setText("+"+score);
//		rl_common.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.ta_checkin));
//		rl_common.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				dialogReward.dismiss();
//			}
//		});
//
//		new Handler().postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				if(isActivity){
//					dialogReward.dismiss();
//				}
//			}
//		},2000);
//	}

	public void showContextMenuScore(int score) {
		dialogReward = DialogHelper
				.getPinterestDialogCancelableReward(mContext);
		dialogReward.setTitle("");
		final RelativeLayout rl_score_number = (RelativeLayout)dialogReward.findViewById(R.id.rl_score_number);
		final TextView tv_score_number = (TextView)dialogReward.findViewById(R.id.tv_score_number);
		rl_score_number.setVisibility(View.VISIBLE);
		tv_score_number.setText("+"+score);
		Window dialogWindow = dialogReward.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);
		lp.height = Utils.getDisplayHeight(mContext); ; // 动态高度
		// 当Window的Attributes改变时系统会调用此函数,可以直接调用以应用上面对窗口参数的更改,也可以用setAttributes
		dialogWindow.setAttributes(lp);
//		dialogReward.show();
//		rl_score_number.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.ta_checkin));
//		rl_score_number.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				if(isActivity){
//					dialogReward.dismiss();
//				}
//			}
//		});
//		new Handler().postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				if(isActivity){
//					rl_score_number.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.ta));
//				}
//			}
////		},1000);
//		},1);
//		new Handler().postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				if(isActivity){
//					dialogReward.dismiss();
//				}
//			}
//		},1500);
	}

	/**
	 * 使用前必须调用以下方法
	 * setIsShareVisible()
	 * setFunctionMenuVisible()
	 * 来设置图标显示状态
	 * 新版本的dialog
	 */
	private void showAlbumDialog(String[] names,int[] iconIds) {
		final DialogAlbumView mDialog = new DialogAlbumView(this);
		mDialog.setCancelable(true);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.setShareViewVisible(false);
		mDialog.setFunctionMenuVisible(names,iconIds);
		mDialog.setOnDialogItemClick(new DialogAlbumView.OnDialogItemClick() {
			@Override
			public void OnDialogItemClick(int viewId) {
				if (viewId == R.id.ly_share_weichat_circle) {
//					shareWeiChatCircle();
				} else if (viewId == R.id.ly_share_weichat) {
//					shareWeiChat();
				} else if (viewId == R.id.ly_share_qq) {
//					shareQQ();
				} else if (viewId == R.id.ly_share_copy_link) {
//					copyLink();
				} else if (viewId == R.id.ll1) {
					onclickBtn1();
				} else if (viewId == R.id.ll2) {
					onclickBtn2();
				} else if (viewId == R.id.ll3) {
					onclickBtn3();
				} else if (viewId == R.id.ll4) {
					onclickBtn4();
				}  else if (viewId == R.id.cancel_tv) {
				}
				mDialog.dismiss();
			}
		});
		mDialog.show();
	}

	@Override
	protected void onStop() {
		if (!isAppOnForeground()) {
			isForeground =true;
			if (SysConstants.ishome){
				getService().getDataReportManager().reportEvent(EventType.ENTER_BACKGROUND);
				getService().getDataReportManager().reportNow();
//				showToast("app 进入后台");
			}else{
				SysConstants.ishome = true;
			}
		}
		super.onStop();
	}



	/**
	 * 程序是否在前台运行
	 *
	 * @return
	 */
	public boolean isAppOnForeground() {
		// Returns a list of application processes that are running on the
		// device

		ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = getApplicationContext().getPackageName();

		List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		if (appProcesses == null)
			return false;

		for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
			// The name of the process that this object is associated with.
			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}

		return false;
	}



}
