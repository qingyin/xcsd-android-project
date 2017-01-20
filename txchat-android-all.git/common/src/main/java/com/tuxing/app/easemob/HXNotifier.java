/************************************************************
 *  * EaseMob CONFIDENTIAL 
 * __________________ 
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved. 
 *  
 * NOTICE: All information contained herein is, and remains 
 * the property of EaseMob Technologies.
 * Dissemination of this information or reproduction of this material 
 * is strictly forbidden unless prior written permission is obtained
 * from EaseMob Technologies.
 */
package com.tuxing.app.easemob;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.EasyUtils;
import com.tuxing.app.easemob.ui.ChatActivity;
import com.tuxing.app.util.Utils;
import com.tuxing.sdk.utils.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * 新消息提醒class 2.1.8把新消息提示相关的api移除出sdk，方便开发者自由修改 开发者也可以继承此类实现相关的接口
 * 
 * this class is subject to be inherited and implement the relative APIs
 */
public class HXNotifier {
	private final static String TAG = "notify";
	Ringtone ringtone = null;

	protected final static String[] msg_eng = { "sent a message",
			"sent a picture", "sent a voice", "sent location message",
			"sent a video", "sent a file", "%1 contacts sent %2 messages" };
	protected final static String[] msg_ch = { "发来一条消息", "发来一张图片", "发来一段语音",
			"发来位置信息", "发来一个视频", "发来一个文件", "您有%1条未读消息" };

	protected static int notifyID = 0; // start notification id
	protected static int foregroundNotifyID = 0555;

	protected NotificationManager notificationManager = null;

	protected HashSet<String> fromUsers = new HashSet<String>();
	protected int notificationNum = 0;

	protected Context appContext;
	protected String packageName;
	protected String[] msgs;
	protected long lastNotifiyTime;
	protected AudioManager audioManager;
	protected Vibrator vibrator;
	protected HXNotificationInfoProvider notificationInfoProvider;
	private int type;

	public HXNotifier() {
	}

	/**
	 * 开发者可以重载此函数 this function can be override
	 * 
	 * @param context
	 * @return
	 */
	public HXNotifier init(Context context) {
		appContext = context;
		notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		packageName = appContext.getApplicationInfo().packageName;
		if (Locale.getDefault().getLanguage().equals("zh")) {
			msgs = msg_ch;
		} else {
			msgs = msg_eng;
		}

		audioManager = (AudioManager) appContext
				.getSystemService(Context.AUDIO_SERVICE);
		vibrator = (Vibrator) appContext
				.getSystemService(Context.VIBRATOR_SERVICE);

		return this;
	}

	/**
	 * 开发者可以重载此函数 this function can be override
	 */
	public void reset() {
		resetNotificationCount();
		cancelNotificaton();
	}

	void resetNotificationCount() {
		notificationNum = 0;
		fromUsers.clear();
	}

	void cancelNotificaton() {
		if (notificationManager != null)
			notificationManager.cancel(notifyID);
	}

	/**
	 * 处理新收到的消息，然后发送通知
	 * 
	 * 开发者可以重载此函数 this function can be override
	 * 
	 * @param message
	 */
	public synchronized void onNewMsg(EMMessage message) {

		HXSDKModel model = HXSDKHelper.getInstance().getModel();
		List<String> conversions = EMChatManager.getInstance().getChatOptions()
				.getGroupsOfNotificationDisabled();
		if (conversions != null) {
			for (int i = 0; i < conversions.size(); i++) {
				if (conversions.get(i).equals(message.getTo())) {
					return;
				}
			}
		}

		if (!model.getSettingMsgNotification()) {
			return;
		}

		if (EMChatManager.getInstance().isSlientMessage(message)) {
			return;
		}

		// 判断app是否在后台
		if (!EasyUtils.isAppRunningForeground(appContext)) {
			EMLog.d(TAG, "app is running in backgroud");
			sendNotification(message, false);
		} else {
			sendNotification(message, true);

		}
		viberateAndPlayTone(message);
	}

	public synchronized void onNewMesg(List<EMMessage> messages) {

		HXSDKModel model = HXSDKHelper.getInstance().getModel();
		List<String> conversions = EMChatManager.getInstance().getChatOptions()
				.getGroupsOfNotificationDisabled();
		if (conversions != null) {
			for (int i = 0; i < conversions.size(); i++) {
				if (conversions.get(i).equals(
						messages.get(messages.size()).getTo())) {
					return;
				}
			}
		}

		if (!model.getSettingMsgNotification()) {
			return;
		}

		if (EMChatManager.getInstance().isSlientMessage(
				messages.get(messages.size() - 1))) {
			return;
		}
		// 判断app是否在后台
		if (!EasyUtils.isAppRunningForeground(appContext)) {
			EMLog.d(TAG, "app is running in backgroud");
			sendNotification(messages, false);
		} else {
			sendNotification(messages, true);
		}
		viberateAndPlayTone(messages.get(messages.size() - 1));
	}

	/**
	 * 发送通知栏提示 This can be override by subclass to provide customer
	 * implementation
	 * 
	 * @param messages
	 * @param isForeground
	 */
	protected void sendNotification(List<EMMessage> messages,
			boolean isForeground) {
		for (EMMessage message : messages) {
			if (!isForeground) {
				notificationNum++;
				fromUsers.add(message.getFrom());
			}
		}
		sendNotification(messages.get(messages.size() - 1), isForeground, false);
	}

	protected void sendNotification(EMMessage message, boolean isForeground) {
		sendNotification(message, isForeground, true);
	}

	/**
	 * 发送通知栏提示 This can be override by subclass to provide customer
	 * implementation
	 * 
	 * @param message
	 */
	protected void sendNotification(EMMessage message, boolean isForeground,
			boolean numIncrease) {
		String username = "";
		PackageManager packageManager = appContext.getPackageManager();
		String notifyTitle = (String) packageManager
				.getApplicationLabel(appContext.getApplicationInfo());
		String notifyText = "";
		try {
			username = message.getStringAttribute("name");
		} catch (EaseMobException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			if (!StringUtils.isBlank(username)
					|| notificationInfoProvider == null) {
				notifyText = username + ": ";
				switch (message.getType()) {
				case TXT:
					notifyText += msgs[0];
					break;
				case IMAGE:
					notifyText += msgs[1];
					break;
				case VOICE:
					notifyText += msgs[2];
					break;
				}
			} else {
				notifyText = notificationInfoProvider.getDisplayedText(message);
			}

			if (StringUtils.isBlank(notifyTitle)
					|| notificationInfoProvider == null) {
				notifyTitle = notificationInfoProvider.getTitle(message);
			}

			Intent msgIntent = appContext.getPackageManager()
					.getLaunchIntentForPackage(packageName);
			if (notificationInfoProvider != null) {
				msgIntent = notificationInfoProvider.getLaunchIntent(message);
			}
			if (message.getChatType().equals(EMMessage.ChatType.GroupChat)) {
				msgIntent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
				msgIntent.putExtra("groupId", Long.valueOf(message.getTo()));
			} else {
				msgIntent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
				msgIntent.putExtra("userId", Long.valueOf(message.getFrom()));
			}
			if (numIncrease) {
				if (!isForeground) {
					notificationNum++;
					fromUsers.add(message.getFrom());
				}
			}

			int fromUsersNum = fromUsers.size();
			String summaryBody = msgs[6].replaceFirst("%1",
					Integer.toString(notificationNum));

			if (notificationInfoProvider != null) {
				String customSummaryBody = notificationInfoProvider
						.getLatestText(message, fromUsersNum, notificationNum);
				if (customSummaryBody != null) {
					summaryBody = customSummaryBody;
				}
			}
			msgIntent.putExtra("isNotifier",true);
			 PendingIntent pendingIntent = PendingIntent.getActivity(appContext, notifyID, msgIntent,PendingIntent.FLAG_UPDATE_CURRENT);
			
			 NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(appContext)
           .setSmallIcon(appContext.getApplicationInfo().icon)
           .setWhen(System.currentTimeMillis())
           .setAutoCancel(true);
			 
			  mBuilder.setContentTitle(notifyTitle);
            mBuilder.setTicker(notifyText);
            mBuilder.setContentText(summaryBody);
            mBuilder.setContentIntent(pendingIntent);
            Notification notification = mBuilder.build();
            notification.defaults |= Notification.DEFAULT_LIGHTS;

//			Notification notification = new Notification(R.drawable.logo,notifyText, System.currentTimeMillis());
//			PendingIntent pendingIntent = PendingIntent.getActivity(appContext,1, msgIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//			notification.setLatestEventInfo(appContext, notifyTitle,summaryBody, pendingIntent);

			if (!isForeground) {
				notificationManager.notify(notifyID, notification);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * msg 提醒方式 0 静音 1 声音 震动 2 声音 3 震动
	 * 
	 */
	/**
	 * 手机震动和声音提示
	 */
	public void viberateAndPlayTone(EMMessage message) {
		type = Utils.getMsgRemind(appContext);
		if (type == 0)
			return;
		if (message != null) {
			if (EMChatManager.getInstance().isSlientMessage(message)) {
				return;
			}
		}
		HXSDKModel model = HXSDKHelper.getInstance().getModel();
		List<String> conversions = EMChatManager.getInstance().getChatOptions()
				.getGroupsOfNotificationDisabled();
		if (conversions != null) {
			for (int i = 0; i < conversions.size(); i++) {
				if (conversions.get(i).equals(message.getTo())) {
					return;
				}
			}
		}

		if (!model.getSettingMsgNotification()) {
			return;
		}

		try {

			lastNotifiyTime = System.currentTimeMillis();

			// 判断是否处于静音模式
			if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
				EMLog.e(TAG, "in slient mode now");
				return;
			}

			if ((type == 1 || type == 3) && model.getSettingMsgVibrate()) {
				long[] pattern = new long[] { 0, 180, 80, 120 };
				vibrator.vibrate(pattern, -1);
			}

			if ((type == 1 || type == 2) && model.getSettingMsgSound()) {
				if (ringtone == null) {
					Uri notificationUri = RingtoneManager
							.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

					ringtone = RingtoneManager.getRingtone(appContext,
							notificationUri);
					if (ringtone == null) {
						EMLog.d(TAG,
                                "cant find ringtone at:"
                                        + notificationUri.getPath());
						return;
					}
				}

				if (!ringtone.isPlaying()) {
					String vendor = Build.MANUFACTURER;

					ringtone.play();
					// for samsung S3, we meet a bug that the phone will
					// continue ringtone without stop
					// so add below special handler to stop it after 3s if
					// needed
					if (vendor != null
							&& vendor.toLowerCase().contains("samsung")) {
						Thread ctlThread = new Thread() {
							public void run() {
								try {
									Thread.sleep(3000);
									if (ringtone.isPlaying()) {
										ringtone.stop();
									}
								} catch (Exception e) {
								}
							}
						};
						ctlThread.run();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置NotificationInfoProvider
	 * 
	 * @param provider
	 */
	public void setNotificationInfoProvider(HXNotificationInfoProvider provider) {
		notificationInfoProvider = provider;
	}

	public interface HXNotificationInfoProvider {
		/**
		 * 设置发送notification时状态栏提示新消息的内容(比如Xxx发来了一条图片消息)
		 * 
		 * @param message
		 *            接收到的消息
		 * @return null为使用默认
		 */
		String getDisplayedText(EMMessage message);

		/**
		 * 设置notification持续显示的新消息提示(比如2个联系人发来了5条消息)
		 * 
		 * @param message
		 *            接收到的消息
		 * @param fromUsersNum
		 *            发送人的数量
		 * @param messageNum
		 *            消息数量
		 * @return null为使用默认
		 */
		String getLatestText(EMMessage message, int fromUsersNum, int messageNum);

		/**
		 * 设置notification标题
		 * 
		 * @param message
		 * @return null为使用默认
		 */
		String getTitle(EMMessage message);

		/**
		 * 设置小图标
		 * 
		 * @param message
		 * @return 0使用默认图标
		 */
		int getSmallIcon(EMMessage message);

		/**
		 * 设置notification点击时的跳转intent
		 * 
		 * @param message
		 *            显示在notification上最近的一条消息
		 * @return null为使用默认
		 */
		Intent getLaunchIntent(EMMessage message);
	}
}
