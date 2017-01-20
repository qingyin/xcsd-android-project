package com.tuxing.app;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMGroupChangeListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.mechat.mechatlibrary.MCClient;
import com.mechat.mechatlibrary.MCOptions;
import com.mechat.mechatlibrary.MCUserConfig;
import com.mechat.mechatlibrary.callback.OnInitCallback;
import com.tencent.bugly.crashreport.CrashReport;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.easemob.HXSDKHelper;
import com.tuxing.app.easemob.domain.User;
import com.tuxing.app.easemob.iml.TuxingHXSDKHelper;
import com.tuxing.app.fragment.DiscoveryFragment;
import com.tuxing.app.fragment.HomeFragment;
import com.tuxing.app.fragment.MessageFragment;
import com.tuxing.app.fragment.MyProfileFragment;
import com.tuxing.app.util.AppUpdate;
import com.tuxing.app.util.DateTimeUtils;
import com.tuxing.app.util.MyLog;
import com.tuxing.app.util.PreferenceUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.MyFragmentTabHost;
import com.tuxing.sdk.db.entity.RevokedMessage;
import com.tuxing.sdk.db.helper.UmengDbHelper;
import com.tuxing.sdk.event.CheckInEvent;
import com.tuxing.sdk.event.ContentItemGroupEvent;
import com.tuxing.sdk.event.LoginEvent;
import com.tuxing.sdk.event.NoticeEvent;
import com.tuxing.sdk.event.StateChangeEvent;
import com.tuxing.sdk.event.UpgradeEvent;
import com.tuxing.sdk.event.UserEvent;
import com.tuxing.sdk.modle.UpgradeInfo;
import com.tuxing.sdk.receiver.AlarmReceiver;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.Constants;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.UmengRegistrar;
import com.xcsd.rpc.proto.EventType;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 */
public class MainActivity extends BaseActivity implements EMEventListener {
	AppUpdate mAppUpdate;
	// 账号在别处登录
	public boolean isConflict = false;
	private NotificationManager mNM;
	private Notification notification;
	private Vibrator mVibrator;

	// 账号被移除
	private boolean isCurrentAccountRemoved = false;

	//定义FragmentTabHost对象
	private MyFragmentTabHost mTabHost;

	//定义一个布局
	private LayoutInflater layoutInflater;

	//定义数组来存放Fragment界面
	private Class[] fragmentArray = {
			MessageFragment.class,
			HomeFragment.class,
			DiscoveryFragment.class,
			MyProfileFragment.class
	};

	private Map<Integer, Boolean> tabHasNew = new HashMap<Integer, Boolean>();

	//定义数组来存放按钮图片
	private int mImageViewArray[] = {
			R.drawable.tab_msg_btn,
			R.drawable.tab_home_btn,
			R.drawable.tab_find_btn,
			R.drawable.tab_setting_btn
	};
	private int mImageViewArray_t[] = {
			R.drawable.tab_msg_btn_t,
			R.drawable.tab_home_btn_t,
			R.drawable.tab_find_btn_t,
			R.drawable.tab_setting_btn_t
	};
	private int mImageViewArray_p[] = {
			R.drawable.tab_msg_btn_p,
			R.drawable.tab_home_btn_p,
			R.drawable.tab_find_btn_p,
			R.drawable.tab_setting_btn_p
	};


	//Tab选项卡的文字
	private String mTextviewArray[] = {"消息", "学堂", "发现", "我"};

	// 当前fragment的index
	private int currentTabIndex;

	String deviceId = "";
	public String TAG = MainActivity.class.getSimpleName();
	public static int unreadSize = 0;

	FinishReceiver receiver;
	LoginChatReceiver loginChatReceiver;
	DelRecivier delReceicer;
	RefreshRecivier refreshReceicer;
	HomeWatcherReceiver homeWatcherReceiver;
	private AlarmReceiver alarmReceiver;
	private PendingIntent pendingIntent;

	boolean isteacher =false;
	boolean isparent =false;

//	*********
public static MainActivity instance = null;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null && savedInstanceState.getBoolean(SysConstants.ACCOUNT_REMOVED, false)) {
			// 防止被移除后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理
			TuxingHXSDKHelper.getInstance().logout(null);
			finish();
			startActivity(new Intent(this, LoginActivity.class));
			return;
		} else if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
			// 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理
			finish();
			startActivity(new Intent(this, LoginActivity.class));
			return;
		}
		mVibrator = ( Vibrator )getSystemService(Service.VIBRATOR_SERVICE);
		mNM = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		initView();
		sendBroadcast(new Intent(SysConstants.FINISH_LOGIN));
		sendBroadcast(new Intent(SysConstants.FINISH_ACTIVE));
        if(TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())){
            Constants.TAB_COUNTER_MAP.get(Constants.EXPLORE_TAB_INDEX).add(Constants.COUNTER.LEARN_GARDEN);
            Constants.COUNTER_TAB_MAP.get(Constants.COUNTER.LEARN_GARDEN).add(Constants.EXPLORE_TAB_INDEX);
        }
        PreferenceUtils.setPrefBoolean(mContext, SysConstants.ISACTIVITYACTIVITY, true);
        initPush();
        initEMChat();
        initReceivers();
        startAlarm(TuxingApp.VersionType);
		//更新
        if (EMChat.getInstance().isLoggedIn()){
            dealRevokedMessage();//清理撤回消息
        }
		initData();
		mAppUpdate = new AppUpdate(this);
		getService().getUpgradeManager().getUpgradeInfo();
		delReceicer = new DelRecivier();
		registerReceiver(delReceicer, new IntentFilter(TuxingApp.packageName + SysConstants.DELREFRESHACTION));
		refreshReceicer = new RefreshRecivier();
		registerReceiver(refreshReceicer, new IntentFilter(TuxingApp.packageName + SysConstants.REFRESHMSGLIST));
		initUmengDB();
        checkIn();

		getService().getDataReportManager().noticeFinishLogin();
	}
	//		todo
	private void initUmengDB() {
		boolean umTBIsExists = PreferenceUtils.getPrefBoolean(mContext, "umTBIsExists", false);
		if(!umTBIsExists){
			try{
				new Thread(new Runnable() {
					@Override
					public void run() {
						UmengDbHelper helper = UmengDbHelper.getHelper(mContext);
						helper.createUmTable();
						PreferenceUtils.setPrefBoolean(mContext, "umTBIsExists", true);
					}
				}).start();
			}catch (Exception e){
				MyLog.getLogger(MainActivity.class.getSimpleName()).d("创建umeng table Exception= " + e.getMessage());
			}
		}
	}


	private void initPush() {
        final String deviceToken= UmengRegistrar.getRegistrationId(mContext);
		deviceId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        if(!TextUtils.isEmpty(deviceToken)){
            getService().getSecurityManager().updateDeviceToken(deviceToken, deviceId, Build.VERSION.RELEASE, Build.MODEL);
		}

		TuxingApp.mPushAgent.onAppStart();
//		TuxingApp.mPushAgent.enable(new IUmengRegisterCallback() {
//            @Override
//            public void onRegistered(String registrationId) {
//                if(TextUtils.isEmpty(deviceToken)){
//                    getService().getSecurityManager().updateDeviceToken(registrationId, deviceId, Build.VERSION.RELEASE, Build.MODEL);
//                }
//            }
//        });

		TuxingApp.mPushAgent.enable(new IUmengRegisterCallback() {
			@Override
			public void onRegistered(String registrationId) {
//				MyLog.getLogger(TAG).d("回调推送注册状态**************" + TuxingApp.mPushAgent.isRegistered());
//				MyLog.getLogger(TAG).d("回调推送注册工作状态=============" + TuxingApp.mPushAgent.isEnabled());
				if (TextUtils.isEmpty(deviceToken)) {
					getService().getSecurityManager().updateDeviceToken(registrationId, deviceId, Build.VERSION.RELEASE, Build.MODEL);
//					showToast("来啦");
				}
			}
		});


	}

	private void initReceivers(){
		receiver = new FinishReceiver();
		IntentFilter filter = new IntentFilter(SysConstants.FINISH_MAIN);
		registerReceiver(receiver, filter);
		loginChatReceiver = new LoginChatReceiver();
		IntentFilter filter1 = new IntentFilter(SysConstants.LOGINHX);
		registerReceiver(loginChatReceiver, filter1);

		homeWatcherReceiver = new HomeWatcherReceiver();
		IntentFilter filter2 = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		registerReceiver(homeWatcherReceiver, filter2);
	}

	private void startAlarm(String version){
		if(alarmReceiver == null) {
			alarmReceiver = new AlarmReceiver(version);
			this.registerReceiver(alarmReceiver, new IntentFilter(alarmReceiver.getAction()));
		}

		if(pendingIntent == null){
			pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(
					alarmReceiver.getAction()), PendingIntent.FLAG_UPDATE_CURRENT);

			AlarmManager alarmManager = (AlarmManager) this.getSystemService(Service.ALARM_SERVICE);
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
					System.currentTimeMillis() + Constants.ALARM_REPEAT_INTERVAL * 1000,
					Constants.ALARM_REPEAT_INTERVAL * 1000, pendingIntent);
		}
	}

	private void stopAlarm(){
		if(pendingIntent != null){
			AlarmManager alarmManager = (AlarmManager) this.getSystemService(Service.ALARM_SERVICE);
			alarmManager.cancel(pendingIntent);
		}

		try{
			this.unregisterReceiver(alarmReceiver);
		}catch(IllegalArgumentException e){
			//Ignore unregister errors.
		}
	}

	private void initData(){
		try {
			//获取currentUser profile
			getService().getUserManager().updateUserProfile();
			com.tuxing.sdk.db.entity.User uss = user;
            getService().getContactManager().syncContact();
			getService().getCounterManager().updateCounters();
		}catch (Exception e){
			showAndSaveLog(TAG, "syncContact error", false);
		}
		if (user != null) {
			CrashReport.setUserId("userId :" + String.valueOf(user.getUserId()));
		}

		String deviceId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
		//updateTabHintNum();
	}

	private void initEMChat(){
		if(user != null){
			loginEMChat(String.valueOf(user.getUserId()), user.getPassword());
		}

		// setContactListener监听联系人的变化等
		//EMContactManager.getInstance().setContactListener(new MyContactListener());
		// 注册一个监听连接状态的listener
		EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());
		// 注册群聊相关的listener
		EMGroupManager.getInstance().addGroupChangeListener(new MyGroupChangeListener());
		// 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
		EMChat.getInstance().setAppInited();
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!isConflict && !isCurrentAccountRemoved) {
			EMChatManager.getInstance().activityResumed();
		}

		// unregister this event listener when this actiƒvity enters the
		// background
		TuxingHXSDKHelper sdkHelper = (TuxingHXSDKHelper) HXSDKHelper.getInstance();
		sdkHelper.pushActivity(this);

		// register the event listener when enter the foreground
		EMChatManager.getInstance().registerEventListener(this,
				new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage,
						EMNotifierEvent.Event.EventOfflineMessage,
						EMNotifierEvent.Event.EventConversationListChanged});

		for(int index = 0; index <= Constants.EXPLORE_TAB_INDEX; index++) {
			tabHasNew.put(index, checkTabHasNew(index));
			setTabHasNew(index, tabHasNew.get(index));
		}
	}


	/**
	 * 初始化组件
	 */
	private void initView(){
		setContentLayout(R.layout.layout_main);
//		******
		instance = this;
//		******

		//实例化布局对象
		layoutInflater = LayoutInflater.from(this);

		setLeftBack("", false, false);
		setRightNext(false, "下一步", 0);
//		判断家长还是教师
		isteacher =TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion());
		isparent =TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion());
		if(isteacher){
			mImageViewArray = mImageViewArray_t;
		}else if(isparent){
			mImageViewArray = mImageViewArray_p;
		}
		//实例化TabHost对象，得到TabHost
		mTabHost = (MyFragmentTabHost)findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
//		initMeiqia();
		//得到fragment的个数
		int count = fragmentArray.length;

		for(int i = 0; i < count; i++){
			//为每一个Tab按钮设置图标、文字和内容
			TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
			//将Tab按钮添加进Tab选项卡中
			mTabHost.addTab(tabSpec, fragmentArray[i], null);
			mTabHost.getTabWidget().setDividerDrawable(null);
			//设置Tab按钮的背景
//			mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
		}
		View tabW = mTabHost.getTabWidget().getChildAt(1);
		currentTabIndex = 1;

		mTabHost.setCurrentTab(1);
		if (isteacher){
			((TextView) tabW.findViewById(R.id.tv_tab_title)).setTextColor(getResources().getColor(R.color.text_teacher));
		}else if(isparent){
			((TextView) tabW.findViewById(R.id.tv_tab_title)).setTextColor(getResources().getColor(R.color.text_parent));
		}

		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@SuppressLint("ResourceAsColor")
			@Override
			public void onTabChanged(String tabId) {
				currentTabIndex = mTabHost.getCurrentTab();
				System.out.println("tabId:" + tabId + mTabHost.getCurrentTabTag());

				for (int i = 0; i < fragmentArray.length; i++) {
					//为每一个Tab按钮设置图标、文字和内容
					//设置Tab按钮的背景
					if (tabId.equals(mTextviewArray[i])) {
						View tabW = mTabHost.getTabWidget().getChildAt(i);
						if (isteacher){
							((TextView) tabW.findViewById(R.id.tv_tab_title)).setTextColor(getResources().getColor(R.color.text_teacher));
						}else if(isparent){
							((TextView) tabW.findViewById(R.id.tv_tab_title)).setTextColor(getResources().getColor(R.color.text_parent));
						}
					} else {
						View tabW = mTabHost.getTabWidget().getChildAt(i);
						((TextView) tabW.findViewById(R.id.tv_tab_title)).setTextColor(getResources().getColor(R.color.gray));
					}
				}
			}
		});
	}

	/**
	 * 给Tab按钮设置图标和文字
	 */
	private View getTabItemView(int index){
		View view = layoutInflater.inflate(R.layout.tab_item_view, null);

		ImageView imageView = (ImageView) view.findViewById(R.id.iv_tab_img);
		imageView.setImageResource(mImageViewArray[index]);

		TextView textView = (TextView) view.findViewById(R.id.tv_tab_title);
		textView.setText(mTextviewArray[index]);

		ImageView tab_item_isnew = (ImageView)view.findViewById(R.id.tab_item_isnew);
		tab_item_isnew.setVisibility(View.GONE);
		return view;
	}

	@Override
	public void onCancel() {
		super.onCancel();
//		finish();
//		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		startActivity(intent);
	}



	/**
	 * 连接监听listener
	 *
	 */
	private class MyConnectionListener implements EMConnectionListener {
		@Override
		public void onConnected() {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Intent intent_ok = new Intent(SysConstants.NETWORKERROR);
					intent_ok.putExtra("net", true);
					sendBroadcast(intent_ok);
				}
			});
		}

		@Override
		public void onDisconnected(final int error) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (error == EMError.USER_REMOVED) {
						showAndSaveLog(MainActivity.class.getSimpleName(), "用户被移除", false);
						// 显示帐号已经被移除
						onRemove();
						return;
					} else if (error == EMError.CONNECTION_CONFLICT) {
						// 显示帐号在其他设备登陆dialog
						showAndSaveLog(MainActivity.class.getSimpleName(), "用户登录冲突", false);
						onConflict(getResources().getString(R.string.connect_conflict));
						return;
					} else {
						//网络失败
						Intent intent = new Intent(SysConstants.NETWORKERROR);
						intent.putExtra("net",false);
						sendBroadcast(intent);
					}
				}

			});
		}
	}

	/**
	 * 检查当前用户是否被删除
	 */
	public boolean getCurrentAccountRemoved() {
		return isCurrentAccountRemoved;
	}



	public void loginEMChat(final String username,final String password){
		EMChatManager.getInstance().login(username, password, new EMCallBack() {
			@Override
			public void onSuccess() {
				try {
					// ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
					// ** manually load all local groups and
					TuxingHXSDKHelper.getInstance().setHXId(username);
					TuxingHXSDKHelper.getInstance().setPassword(password);

					EMGroupManager.getInstance().loadAllGroups();
					EMChatManager.getInstance().loadAllConversations();
					EMChatManager.getInstance().updateCurrentUserNick(user.getNickname());
					// 处理好友和群组
					processContactsAndGroups();

					EventBus.getDefault().post(new StateChangeEvent(
							StateChangeEvent.EventType.STATE_CHANGED,
							null,
							Arrays.asList(Constants.MESSAGE_TAB_INDEX)
					));
				} catch (Exception e) {
					Log.e(TAG, "Error when init EMChatManager", e);
				}

				// 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
				if (!EMChatManager.getInstance().updateCurrentUserNick(user.getNickname())) {
					showAndSaveLog(MainActivity.class.getSimpleName(), "update current currentUser nick fail", false);
				}
			}

			@Override
			public void onProgress(int progress, String status) {
			}

			@Override
			public void onError(final int code, final String message) {
				EventBus.getDefault().post(new ReloginEMChatEvent(username, password));
				showAndSaveLog(MainActivity.class.getSimpleName(), "EMChat login Error: " + code + " msg: " + message,
						false);
			}
		});
	}

	private void processContactsAndGroups() throws EaseMobException {
		// demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定
		List<String> userNames = EMContactManager.getInstance().getContactUserNames();
		EMLog.d("roster", "contacts size: " + userNames.size());
		Map<String, User> userList = new HashMap<String, User>();
		for (String username : userNames) {
			User user = new User();
			user.setUsername(username);
			setUserHearder(username, user);
			userList.put(username, user);
		}
		// 存入内存
		TuxingHXSDKHelper.getInstance().setContactList(userList);
	}

	/**
	 * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
	 *
	 * @param username
	 * @param user
	 */
	protected void setUserHearder(String username, User user) {
		String headerName = null;
		if (!TextUtils.isEmpty(user.getNick())) {
			headerName = user.getNick();
		} else {
			headerName = user.getUsername();
		}
		if (username.equals(SysConstants.NEW_FRIENDS_USERNAME)) {
			user.setHeader("");
		} else if (Character.isDigit(headerName.charAt(0))) {
			user.setHeader("#");
		} else {
			user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1)
					.toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
	}

	/**
	 * 监听事件
	 */
	@Override
	public void onEvent(EMNotifierEvent event) {
		switch (event.getEvent()) {
			case EventNewMessage: // 普通消息
			{
				EMMessage message = (EMMessage) event.getData();

				RevokedMessage msg = getService().getMessageManager().getRevokedMessage(message.getMsgId());
				if(null!=msg){
					EMConversation conversation = EMChatManager.getInstance().getConversation(message.getFrom());
					EMMessage delMsg = conversation.getMessage(message.getMsgId());
					if(null!=delMsg) {
						conversation.removeMessage(message.getMsgId());
						getService().getMessageManager().deleteRevokedMessage(msg.getMsgId());
					}
				}else{
					// 提示新消息
					HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
				}
				EventBus.getDefault().post(new StateChangeEvent(
						StateChangeEvent.EventType.STATE_CHANGED,
						null,
						Arrays.asList(Constants.MESSAGE_TAB_INDEX)
				));
				refreshUI(Constants.MESSAGE_TAB_INDEX);
				break;
			}
			case EventOfflineMessage: {
				List<EMMessage> messages = (List<EMMessage>) event.getData();

				List<EMMessage> tmpMessages = new ArrayList<EMMessage>();
				if(!CollectionUtils.isEmpty(messages)) {
					for(EMMessage message:messages){
						RevokedMessage msg = getService().getMessageManager().getRevokedMessage(message.getMsgId());
						if(null!=msg){
							EMConversation conversation = EMChatManager.getInstance().getConversation(message.getFrom());
							EMMessage delMsg = conversation.getMessage(message.getMsgId());
							if(null!=delMsg) {
								conversation.removeMessage(message.getMsgId());
								getService().getMessageManager().deleteRevokedMessage(msg.getMsgId());
							}
						}else{
							// 提示新消息
							tmpMessages.add(message);
						}
					}

					// 提示新消息
					if(!CollectionUtils.isEmpty(tmpMessages)){
						HXSDKHelper.getInstance().getNotifier().onNewMsg(tmpMessages.get(tmpMessages.size() - 1));
					}

					EventBus.getDefault().post(new StateChangeEvent(
							StateChangeEvent.EventType.STATE_CHANGED,
							null,
							Arrays.asList(Constants.MESSAGE_TAB_INDEX)
					));
				}
				refreshUI(Constants.MESSAGE_TAB_INDEX);
				break;
			}
			case EventConversationListChanged: {
				EventBus.getDefault().post(new StateChangeEvent(
						StateChangeEvent.EventType.STATE_CHANGED,
						null,
						Arrays.asList(Constants.MESSAGE_TAB_INDEX)
				));
				refreshUI(Constants.MESSAGE_TAB_INDEX);
				break;
			}
			default:
				break;
		}
	}

	public void onEventMainThread(LoginEvent event){
		if(event.getEvent() == LoginEvent.EventType.TOKEN_EXPIRED){
			onConflict(getResources().getString(R.string.user_kickoff));
		}
	}

	public void onEvent(NoticeEvent event){
		switch (event.getEvent()){
			case NOTICE_INBOX_LATEST_NOTICE_SUCCESS:
				refreshUI(Constants.MESSAGE_TAB_INDEX);
				break;
		}
	}

	public void onEvent(CheckInEvent event){
		switch (event.getEvent()){
			case CHECKIN_LATEST_RECORDS_SUCCESS:
				refreshUI(Constants.MESSAGE_TAB_INDEX);
				break;
		}
	}

	public void onEvent(ContentItemGroupEvent event){
		switch (event.getEvent()){
			case FETCH_LATEST_ITEM_GROUP_SUCCESS:
				refreshUI(Constants.MESSAGE_TAB_INDEX);
				break;
		}
	}

	private void refreshUI(int index) {
		if (currentTabIndex == index) {
			switch (currentTabIndex) {
				case Constants.HOME_TAB_INDEX:
					Intent intent = new Intent(SysConstants.HOMEREFRESHACTION);
					sendBroadcast(intent);
					break;
				case Constants.MESSAGE_TAB_INDEX:
					intent = new Intent(SysConstants.MSGLISTREFRESHACTION);
					sendBroadcast(intent);
					break;
				case Constants.EXPLORE_TAB_INDEX:
					intent = new Intent(SysConstants.UPDATENEWEXPLORE);
					sendBroadcast(intent);
			}
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(receiver!=null)
			unregisterReceiver(receiver);
		if(delReceicer!=null){
			unregisterReceiver(delReceicer);
		}
		if(refreshReceicer!=null){
			unregisterReceiver(refreshReceicer);
		}
		if(loginChatReceiver!=null){
			unregisterReceiver(loginChatReceiver);
		}
		if(homeWatcherReceiver != null){
			unregisterReceiver(homeWatcherReceiver);
		}

		stopAlarm();
	}

	private long mExitTime;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		System.out.println("tet");
		if (keyCode == KeyEvent.KEYCODE_BACK ) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				showToast("再按一次退出");
				mExitTime = System.currentTimeMillis();
			} else {
				SysConstants.ishome=false;
				finish();
			}
			return true;
		}
		// 拦截MENU按钮点击事件，让他无任何操作
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}

		if(keyCode==KeyEvent.KEYCODE_HOME){

			//写要执行的动作或者任务
//			showToast("KEYCODE_HOME退到后台了");
		}
		return super.onKeyDown(keyCode, event);
	}



	@Override
	protected void onStop() {
//		if(ishome){
//			getService().getDataReportManager().reportNow();
//			showToast("onStop退到后台了");
//		}
		super.onStop();
	}


	private void setTabHasNew(Integer index, boolean hasNew){
			View tab = mTabHost.getTabWidget().getChildAt(index);
			ImageView isNew = ((ImageView) tab.findViewById(R.id.tab_item_isnew));

			if(hasNew) {
				isNew.setVisibility(View.VISIBLE);
				refreshUI(index);
			}else{
				isNew.setVisibility(View.GONE);
			}
	}

	/**
	 * 后台拉去数据变化
	 * @param event
	 */

	public void onEventBackgroundThread(StateChangeEvent event) {
		switch (event.getEvent()) {
			case STATE_CHANGED:

				for(Integer index : event.getChangeList()){
					Boolean newState = checkTabHasNew(index);
					if(tabHasNew.get(index) != newState) {
						tabHasNew.put(index, checkTabHasNew(index));
					}
				}

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						for (Integer index : tabHasNew.keySet()) {
							setTabHasNew(index, tabHasNew.get(index));
						}

					}
				});

				break;
		}
	}

	private boolean checkTabHasNew(int index){
		Map<String, Integer> counters = getService().getCounterManager().getCounters();
		if(Constants.TAB_COUNTER_MAP.containsKey(index)) {
			for (String item : Constants.TAB_COUNTER_MAP.get(index)){
				if(counters.containsKey(item) && counters.get(item) > 0){
					return true;
				}
			}
		}

		if(index == Constants.MESSAGE_TAB_INDEX){
			if(EMChatManager.getInstance().getUnreadMsgsCount() > 0){
				return true;
			}
		}

		return false;
	}

	public void onEventAsync(ReloginEMChatEvent event){
		try {
			//sleep 3s and retry login
			Thread.sleep(3 * 1000);
		} catch (InterruptedException e) {
			// Nothing to do
		}

		loginEMChat(event.getUsername(), event.getPassword());
	}

	class FinishReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction() == SysConstants.FINISH_MAIN){
				finish();
			}
		}
	}
	class LoginChatReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction() == SysConstants.LOGINHX){
				loginEMChat(String.valueOf(user.getUserId()), user.getPassword());
			}
		}
	}
	class HomeWatcherReceiver extends BroadcastReceiver {
		private static final String LOG_TAG = "HomeReceiver";
		private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
		private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
		private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
		private static final String SYSTEM_DIALOG_REASON_LOCK = "lock";
		private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.i(LOG_TAG, "onReceive: action: " + action);
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				// android.intent.action.CLOSE_SYSTEM_DIALOGS
				String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
				Log.i(LOG_TAG, "reason: " + reason);

				if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
					// 短按Home键
					Log.i(LOG_TAG, "homekey");

					if(Utils.isAppOnForeground(getApplicationContext())){
						//getService().getDataReportManager().reportNow();
					}
				}
				else if (SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason)) {
					// 长按Home键 或者 activity切换键
					Log.i(LOG_TAG, "long press home key or activity switch");

				}
				else if (SYSTEM_DIALOG_REASON_LOCK.equals(reason)) {
					// 锁屏
					Log.i(LOG_TAG, "lock");
				}
				else if (SYSTEM_DIALOG_REASON_ASSIST.equals(reason)) {
					// samsung 长按Home键
					Log.i(LOG_TAG, "assist");
				}

			}
		}
	}


	class ReloginEMChatEvent{
		private String username;
		private String password;

		public ReloginEMChatEvent(String username, String password) {
			this.username = username;
			this.password = password;
		}

		public String getUsername() {
			return username;
		}

		public String getPassword() {
			return password;
		}
	}

	private void onConflict(String msg){
//		HXSDKHelper.getInstance().logout(null);
//		getService().getLoginManager().logout();
		new Thread(){
			@Override
			public void run() {
				HXSDKHelper.getInstance().logout(null);
				getService().getLoginManager().logout();
			}
		}.start();
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("isConflict", true);
		intent.putExtra("msg", msg);
		startActivity(intent);
//		new Thread(){
//			@Override
//			public void run() {
//				finishActivity();
//			}
//		}.start();
//		finishActivity();
		finish();

//		startActivity(intent);
//		finishActivity();

//		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//		intent.putExtra("isConflict", true);
//		intent.putExtra("msg", msg);
//		PendingIntent restartIntent = PendingIntent.getActivity(
//				mContext, 0, intent,
//				Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		//退出程序
//		AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//		mgr.set(AlarmManager.RTC, System.currentTimeMillis(),
//				restartIntent); // 1秒钟后重启应用
//		finishActivity();
	}
	private void onRemove(){
		new Thread(){
			@Override
			public void run() {
				HXSDKHelper.getInstance().logout(null);
				getService().getLoginManager().logout();
			}
		}.start();
//		HXSDKHelper.getInstance().logout(null);
//		getService().getLoginManager().logout();
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("isRemove", true);
		startActivity(intent);
//		new Thread(){
//			@Override
//			public void run() {
//				finishActivity();
//			}
//		}.start();
//		finishActivity();
		finish();


//		startActivity(intent);
//		finishActivity();

//		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//		intent.putExtra("isRemove", true);
//		PendingIntent restartIntent = PendingIntent.getActivity(
//				mContext, 0, intent,
//				Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		//退出程序
//		AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//		mgr.set(AlarmManager.RTC, System.currentTimeMillis(),
//				restartIntent); // 1秒钟后重启应用
//		finishActivity();
	}
	UpgradeInfo upgradeInfo = null;
	public void onEventMainThread(UpgradeEvent event) {
		if (isActivity) {
			switch (event.getEvent()) {
				case GET_UPGRADE_SUCCESS:
					upgradeInfo = event.getInfo();
//
					if (upgradeInfo != null && upgradeInfo.isShowAtMain() &&
							upgradeInfo.isHasNewVersion() && upgradeInfo.isForceUpgrade()) {
						showDialog("", upgradeInfo.getShowMsg(), "", "升级", false);
					} else if (upgradeInfo != null && upgradeInfo.isShowAtMain() && upgradeInfo.isHasNewVersion()) {
						showDialog("", upgradeInfo.getShowMsg(), "取消", "升级");
					}
					break;
				case GET_UPGRADE_FAILED:
					break;
			}
		}
	}



	@Override
	public void onConfirm() {
		super.onConfirm();
		try {
			if(upgradeInfo!=null){
				File f = mAppUpdate.download(upgradeInfo);
				if (f != null && f.exists()) {
					AppUpdate.setupApk(mContext, f);
				} else {
					Toast.makeText(mContext, "新版本下载已开始...", Toast.LENGTH_LONG).show();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(mContext, "下载失败，请稍候重试", Toast.LENGTH_LONG).show();
		}

	}

	public class DelRecivier extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(TuxingApp.packageName + SysConstants.DELREFRESHACTION)){
				getService().getCounterManager().updateCounters();
				String action = intent.getStringExtra("action");
				if(!TextUtils.isEmpty(action) && action.equals("notice")){
					MessageFragment.isNoticeDeleted = true;
					MyLog.getLogger(TAG).d("清除通知,拉去数据");
				}else if(!TextUtils.isEmpty(action) && action.equals("card")){
					MessageFragment.isCheckInDeleted = true;
					MyLog.getLogger(TAG).d("清除刷卡,拉去数据");
				}
			}
		}
	}

	public class RefreshRecivier extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(TuxingApp.packageName + SysConstants.REFRESHMSGLIST)){
				refreshUI(Constants.MESSAGE_TAB_INDEX);
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	private void initMeiqia() {
		MCClient.init(this, SysConstants.MEIQIAKEY, new OnInitCallback() {
			@Override
			public void onSuccess(String response) {
			}

			@Override
			public void onFailed(String response) {
			}
		});
		MCOptions options = new MCOptions(this);
		options.setShowVoiceMessage(false);
		if(user != null){
		MCUserConfig mcUserConfig = new MCUserConfig();
		Map<String,String> userInfo = new HashMap<String,String>();
		userInfo.put(MCUserConfig.PersonalInfo.REAL_NAME,user.getRealname());
		userInfo.put(MCUserConfig.PersonalInfo.AVATAR,user.getAvatar());
		userInfo.put(MCUserConfig.PersonalAccount.NICK_NAME,user.getNickname());
		userInfo.put(MCUserConfig.PersonalAccount.USER_NAME,user.getUsername());
		userInfo.put(MCUserConfig.PersonalInfo.APP_USER_ID,String.valueOf(user.getUserId()));
		userInfo.put(MCUserConfig.Contact.TEL,user.getMobile());
		mcUserConfig.setUserInfo(mContext,userInfo,null,null);
		}

	}

	/**
	 * 监听群组信息
	 * MyGroupChangeListener
	 */
	public class MyGroupChangeListener implements EMGroupChangeListener {

		@Override
		public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
		}

		@Override
		public void onInvitationAccpted(String groupId, String inviter, String reason) {
		}

		@Override
		public void onInvitationDeclined(String groupId, String invitee, String reason) {
		}

		@Override
		public void onUserRemoved(String groupId, String groupName) {

			// 提示用户被T了
			// 刷新ui
			try {
				//获取currentUser profile
				getService().getUserManager().updateUserProfile();
				Intent intent = new Intent(SysConstants.MSGLISTREFRESHACTION);
				sendBroadcast(intent);
				// added on 0911
				showAndSaveLog(MainActivity.class.getSimpleName(), "用户从群里移除", false);
				onConflict(getResources().getString(R.string.user_kickoff));
			}catch (Exception e){
				showAndSaveLog(TAG, "syncContact error", false);
			}
		}

		@Override
		public void onGroupDestroy(String groupId, String groupName) {
			// 群被解散
			// 提示用户群被解散
			// 刷新ui
			try {
				//获取currentUser profile
				getService().getUserManager().updateUserProfile();
				Intent intent = new Intent(SysConstants.MSGLISTREFRESHACTION);
				sendBroadcast(intent);
			}catch (Exception e){
				showAndSaveLog(TAG, "syncContact error", false);
			}
		}

		@Override
		public void onApplicationReceived(String groupId, String groupName, String applyer, String reason) {
		}

		@Override
		public void onApplicationAccept(String groupId, String groupName, String accepter) {
		}

		@Override
		public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {
		}
	}


	/**
	 * 0  静音
	 * 1 	 声音  震动
	 * 2	声音
	 * 3	震动
	 *
	 */
	public void showNotice(String title,String content){
		notification = new Notification(mContext.getApplicationInfo().icon,title, System.currentTimeMillis());

        int msgRemind = Utils.getMsgRemind(mContext);
        switch (msgRemind) {
		case 1:
			notification.defaults |= Notification.DEFAULT_SOUND;
			mVibrator.vibrate(new long[]{0, 400, 200, 400 },-1);
			break;
		case 2:
			notification.defaults |= Notification.DEFAULT_SOUND;
			break;
		case 3:
			mVibrator.vibrate(new long[]{0, 400, 200, 400 },-1);
			break;

		}

        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
		int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
		PendingIntent contentIntent = PendingIntent.getActivity(mContext,
				iUniqueId, new Intent(mContext, MainActivity.class), 0);
		notification.setLatestEventInfo(mContext, title,content, contentIntent);
			mNM.notify(0, notification);
	}
	public void  dealRevokedMessage(){
		List<RevokedMessage> msgList = getService().getMessageManager().getAllRevokedMessage();
		if(!CollectionUtils.isEmpty(msgList)) {
			for(RevokedMessage revokedMessage:msgList){
				String from = "";
				if(revokedMessage.getIsGroup()){
					from = revokedMessage.getTo();
				}else{
					from = revokedMessage.getFrom();
				}
				EMConversation conversation = EMChatManager.getInstance().getConversation(from);
				EMMessage delMsg = conversation.getMessage(revokedMessage.getMsgId());
				if (null != delMsg) {
					conversation.removeMessage(delMsg.getMsgId());
					getService().getMessageManager().deleteRevokedMessage(revokedMessage.getMsgId());
				}
			}
		}
	}
    private void checkIn(){
		if(user != null){
			String today = DateTimeUtils.getDateString(new Date(), "yyyyMMdd");
			String latestDate = PreferenceUtils.getPrefString(mContext, user.getUserId() + "_latest_check_in_date", "");

			if(!today.equals(latestDate)){
				getService().getUserManager().checkIn();
			}
		}

    }
	public void onEventMainThread(UserEvent event) {
		disProgressDialog();
//        if (isActivity) {
		switch (event.getEvent()) {
			case UPDATEUSERPROFILE_SUCCESS:
				HomeFragment.instance.initMenu();
//				HomeFragment.instance.updateAdapter();
				break;

			default:
				break;
		}
		//}
//        }
	}

	public GestureDetector detector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//			if ((e2.getRawX() - e1.getRawX()) > 80) {
			if ((e2.getRawX() - e1.getRawX()) > 200) {
//				showNext();
				if ((e2.getRawY() - e1.getRawY()) > 150) {

				}else if ((e1.getRawY() - e2.getRawY()) > 150){

				}else{
					showPre();
				}
				return true;
			}

			if ((e1.getRawX() - e2.getRawX()) > 200) {
//				showPre();
				if ((e2.getRawY() - e1.getRawY()) > 150) {

				}else if ((e1.getRawY() - e2.getRawY()) > 150){

				}else{
					showNext();
				}
				return true;
			}
			return super.onFling(e1, e2, velocityX, velocityY);
		}

	});

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);
		return super.onTouchEvent(event);
//		return true;
	}

	int i = 0;

	/**
	 * 显示下一个页面
	 */
	protected void showNext() {
		if (mTabHost.getCurrentTab()+1>3){
			mTabHost.setCurrentTab(0);
		}else{
			mTabHost.setCurrentTab(mTabHost.getCurrentTab()+1);
		}
	}

	/**
	 * 显示前一个页面
	 */
	protected void showPre() {
		if (mTabHost.getCurrentTab()-1<0){
			mTabHost.setCurrentTab(3);
		}else{
			mTabHost.setCurrentTab(mTabHost.getCurrentTab()-1);
		}

	}


}
