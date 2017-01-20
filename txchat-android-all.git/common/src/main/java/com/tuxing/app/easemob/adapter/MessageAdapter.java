/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tuxing.app.easemob.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.ClipboardManager;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.BufferType;
import com.easemob.EMCallBack;
import com.easemob.chat.*;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.DateUtils;
import com.easemob.util.EMLog;
import com.easemob.util.TextFormater;
import com.tuxing.app.MainActivity;
import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.activity.NewPicActivity;
import com.tuxing.app.activity.VideoPlayerActivity;
import com.tuxing.app.activity.WebSubDataActivity;
import com.tuxing.app.easemob.task.LoadImageTask;
import com.tuxing.app.easemob.task.LoadVideoImageTask;
import com.tuxing.app.easemob.ui.ChatActivity;
import com.tuxing.app.easemob.util.ImageCache;
import com.tuxing.app.easemob.util.ImageUtils;
import com.tuxing.app.easemob.util.SmileUtils;
import com.tuxing.app.ui.dialog.CommonDialog;
import com.tuxing.app.ui.dialog.DialogHelper;
import com.tuxing.app.util.MyLog;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.MyImageView;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.event.UserEvent;
import com.tuxing.sdk.facade.CoreService;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.Constants;
import com.tuxing.sdk.utils.NetworkUtils;
import com.tuxing.sdk.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MessageAdapter extends BaseAdapter{

	private final static String TAG = "msg";

	private static final int MESSAGE_TYPE_RECV_TXT = 0;
	private static final int MESSAGE_TYPE_SENT_TXT = 1;
	private static final int MESSAGE_TYPE_SENT_IMAGE = 2;
	private static final int MESSAGE_TYPE_RECV_IMAGE = 5;
	private static final int MESSAGE_TYPE_SENT_VOICE = 6;
	private static final int MESSAGE_TYPE_RECV_VOICE = 7;
	private static final int MESSAGE_TYPE_SENT_VIDEO = 8;
	private static final int MESSAGE_TYPE_RECV_VIDEO = 9;

	public static final String IMAGE_DIR = "chat/image/";
	public static final String VOICE_DIR = "chat/audio/";

	private String username;
	private LayoutInflater inflater;
	private Activity activity;

	private static final int HANDLER_MESSAGE_REFRESH_LIST = 0;
	private static final int HANDLER_MESSAGE_SELECT_LAST = 1;
	private static final int HANDLER_MESSAGE_SEEK_TO = 2;
	// reference to conversation object in chatsdk
	private EMConversation conversation;
	EMMessage[] messages = null;
	TuxingApp app;
	private Context context;
	private Map<String, Timer> timers = new Hashtable<String, Timer>();
	private Map<Long,User> users;
	private CoreService service = null;
	private  ArrayList<String> paths;
	private  int mChatType;
	public MessageAdapter(Context context, String username, int chatType,CoreService txservice) {
		this.username = username;
		this.context = context;
		inflater = LayoutInflater.from(context);
		activity = (Activity) context;
		this.conversation = EMChatManager.getInstance().getConversation(username);
		app = (TuxingApp)context.getApplicationContext();
		service = txservice;
		paths = new ArrayList<String>();
		users = new HashMap<Long, User>();
		if(!EventBus.getDefault().isRegistered(MessageAdapter.this))
			EventBus.getDefault().registerSticky(MessageAdapter.this);
		mChatType = chatType;
		initUserData(chatType);
	}

	private void initUserData(int chatType){
		if(chatType == ChatActivity.CHATTYPE_SINGLE){
			Long userId = Long.valueOf(username);
			User user = service.getUserManager().getUserInfo(userId);

			if(user != null){
				users.put(user.getUserId(), user);
			}else{
				service.getUserManager().requestUserInfoFromServer(userId);
			}

			users.put(service.getLoginManager().getCurrentUser().getUserId(),
					service.getLoginManager().getCurrentUser());

		}else if(chatType == ChatActivity.CHATTYPE_GROUP){
			if(service!=null){
				try{
					List<User> userList = service.getDepartmentManager().getDepartmentMembersByGroupId(username);
					if(!CollectionUtils.isEmpty(userList)){
						for(User user : userList){
							users.put(user.getUserId(), user);
						}
					}
				}catch (Exception e){
					MyLog.getLogger(MessageAdapter.TAG).d("聊天消息页面,action = getDepartmentMembersByGroupId :"+e.getMessage());
				}
			}
		}
	}

	Handler handler = new Handler() {
		private void refreshList() {
			// UI线程不能直接使用conversation.getAllMessages()
			// 否则在UI刷新过程中，如果收到新的消息，会导致并发问题
			messages = (EMMessage[]) conversation.getAllMessages().toArray(new EMMessage[conversation.getAllMessages().size()]);
			notifyDataSetChanged();
			paths.clear();
			for (int i = 0; i < messages.length; i++) {
				if(messages[i].getType() == EMMessage.Type.IMAGE){
					ImageMessageBody imgBody = (ImageMessageBody) messages[i].getBody();
//					File file = new File(SysConstants.FILE_DIR_ROOT + Utils.getImageToken(imgBody.getRemoteUrl())+ "_min");
//					if (!file.exists()) {
//						DownBigImageActivity.GetInstance().downbigImage(imgBody.getRemoteUrl());
//					}
					if(messages[i].direct == EMMessage.Direct.SEND){
						paths.add(imgBody.getLocalUrl());
					}else{
//						String localPath = SysConstants.FILE_DIR_ROOT + Utils.getImageToken(imgBody.getRemoteUrl())+ "_min";
						paths.add(imgBody.getRemoteUrl());
					}
					
			}}
		}

		@Override
		public void handleMessage(android.os.Message message) {
			switch (message.what) {
				case HANDLER_MESSAGE_REFRESH_LIST:
					refreshList();
					break;
				case HANDLER_MESSAGE_SELECT_LAST:
					if (activity instanceof ChatActivity) {
						ListView listView = ((ChatActivity)activity).getListView();
						if (messages.length > 0) {
							listView.setSelection(messages.length);
						}
					}
					break;
				case HANDLER_MESSAGE_SEEK_TO:
					int position = message.arg1;
					if (activity instanceof ChatActivity) {
						ListView listView = ((ChatActivity)activity).getListView();
						listView.setSelection(position);
					}
					break;
				default:
					break;
			}
		}
	};


	/**
	 * 获取item数
	 */
	public int getCount() {
		return messages == null ? 0 : messages.length;
	}

	/**
	 * 刷新页面
	 */
	public void refresh() {
		if (handler.hasMessages(HANDLER_MESSAGE_REFRESH_LIST)) {
			return;
		}
		android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST);
		handler.sendMessage(msg);
	}

	/**
	 * 刷新页面, 选择最后一个
	 */
	public void refreshSelectLast() {
		handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
		handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_SELECT_LAST));
	}

	/**
	 * 刷新页面, 选择Position
	 */
	public void refreshSeekTo(int position) {
		handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
		android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_SEEK_TO);
		msg.arg1 = position;
		handler.sendMessage(msg);
	}

	public EMMessage getItem(int position) {
		if (messages != null && position < messages.length) {
			return messages[position];
		}
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	/**
	 * 获取item类型数
	 */
	public int getViewTypeCount() {
		return 16;
	}

	/**
	 * 获取item类型
	 */
	public int getItemViewType(int position) {
		EMMessage message = getItem(position);
		if (message == null) {
			return -1;
		}
		if (message.getType() == EMMessage.Type.TXT) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TXT : MESSAGE_TYPE_SENT_TXT;
		}
		if (message.getType() == EMMessage.Type.IMAGE) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_IMAGE : MESSAGE_TYPE_SENT_IMAGE;

		}
		if (message.getType() == EMMessage.Type.VOICE) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE : MESSAGE_TYPE_SENT_VOICE;
		}
		if (message.getType() == EMMessage.Type.VIDEO) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO : MESSAGE_TYPE_SENT_VIDEO;
		}
		return -1;// invalid
	}


	private View createViewByMessage(EMMessage message, int position) {
		switch (message.getType()) {
			case IMAGE:
				return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_picture, null) : inflater.inflate(
						R.layout.row_sent_picture, null);

			case VOICE:
				return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_voice, null) : inflater.inflate(
						R.layout.row_sent_voice, null);
			case VIDEO:
				return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_video, null) : inflater.inflate(
						R.layout.row_sent_video, null);
			default:
				return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_message, null) : inflater.inflate(
						R.layout.row_sent_message, null);
		}
	}

	@SuppressLint("NewApi")
	public View getView(final int position, View convertView, ViewGroup parent) {
		final EMMessage message = getItem(position);
		ChatType chatType = message.getChatType();
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = createViewByMessage(message, position);//获取对应的视图
			if (message.getType() == EMMessage.Type.IMAGE) {
				try {
					holder.iv = ((ImageView) convertView.findViewById(R.id.iv_sendPicture));
					holder.iv_avatar = (RoundImageView) convertView.findViewById(R.id.iv_userhead);
					holder.tv = (TextView) convertView.findViewById(R.id.percentage);
					holder.pb = (ProgressBar) convertView.findViewById(R.id.progressBar);
					holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
					holder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid);
					holder.tv_mute = (TextView)convertView.findViewById(R.id.tv_mute);
				} catch (Exception e) {
				}

			} else if (message.getType() == EMMessage.Type.TXT) {

				try {
					holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
					holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
					holder.iv_avatar = (RoundImageView) convertView.findViewById(R.id.iv_userhead);
					// 这里是文字内容
					holder.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
					holder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid);
					holder.tv_mute = (TextView)convertView.findViewById(R.id.tv_mute);

					holder.iv_send_pic = (MyImageView) convertView.findViewById(R.id.iv_send_pic);
					holder.rl_share_sent = (RelativeLayout) convertView.findViewById(R.id.rl_share_sent);
					holder.tvshare = (TextView) convertView.findViewById(R.id.tv_chatcontent1);
				} catch (Exception e) {
				}
			} else if (message.getType() == EMMessage.Type.VOICE) {
				try {
					holder.iv = ((ImageView) convertView.findViewById(R.id.iv_voice));
					holder.iv_avatar = (RoundImageView) convertView.findViewById(R.id.iv_userhead);
					holder.tv = (TextView) convertView.findViewById(R.id.tv_length);
					holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
					holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
					holder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid);
					holder.iv_read_status = (ImageView) convertView.findViewById(R.id.iv_unread_voice);
					holder.iv_voice_rl = (RelativeLayout) convertView.findViewById(R.id.iv_voice_rl);
					holder.tv_mute = (TextView)convertView.findViewById(R.id.tv_mute);
				} catch (Exception e) {
				}
			}else if (message.getType() == EMMessage.Type.VIDEO) {
				try {
					holder.iv = ((ImageView) convertView.findViewById(R.id.chatting_content_iv));
					holder.iv_avatar = (RoundImageView) convertView.findViewById(R.id.iv_userhead);
					holder.tv = (TextView) convertView.findViewById(R.id.percentage);
					holder.pb = (ProgressBar) convertView.findViewById(R.id.progressBar);
					holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
					holder.size = (TextView) convertView.findViewById(R.id.chatting_size_iv);
					holder.timeLength = (TextView) convertView.findViewById(R.id.chatting_length_iv);
					holder.playBtn = (ImageView) convertView.findViewById(R.id.chatting_status_btn);
					holder.container_status_btn = (LinearLayout) convertView.findViewById(R.id.container_status_btn);
					holder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid);
					holder.tv_mute = (TextView)convertView.findViewById(R.id.tv_mute);

				} catch (Exception e) {
				}
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Long userId = Long.valueOf(message.getFrom());
		User user = users.get(Long.valueOf(message.getFrom()));

		if(user == null) {
			users.put(userId, new User());
			service.getUserManager().requestUserInfoFromServer(Long.valueOf(message.getFrom()));
		}

		// 群聊时，显示接收的消息的发送人的名称
		if ((chatType == ChatType.GroupChat) && message.direct == EMMessage.Direct.RECEIVE){
			String nickName = null;

			if(user != null){
				nickName = user.getNickname();
			}

			if(StringUtils.isBlank(nickName)) {
				try {
					nickName = message.getStringAttribute("name");
				} catch (EaseMobException e) {
				}
			}

			if(!StringUtils.isBlank(nickName)){
				holder.tv_usernick.setText(nickName);
				holder.tv_usernick.setVisibility(View.VISIBLE);
			}else {
				holder.tv_usernick.setVisibility(View.GONE);
			}
		}
		holder.iv_avatar.setTag(message);

		String imageUrl = null;
		if(user != null) {
			imageUrl = user.getAvatar();
		}

		holder.iv_avatar.setImageUrl(imageUrl + SysConstants.Imgurlsuffix90, R.drawable.default_avatar);


		switch (message.getType()) {
			// 根据消息type显示item
			case IMAGE: // 图片
				handleImageMessage(message, holder, position, convertView);
				break;
			case TXT: // 文本
					handleTextMessage(message, holder, position);
				break;
			case VOICE: // 语音
				handleVoiceMessage(message, holder, position, convertView);
				break;
			case VIDEO: // 视频
				handleVideoMessage(message, holder, position, convertView);
				break;
		}

		if (message.direct == EMMessage.Direct.SEND) {
			View statusView = convertView.findViewById(R.id.msg_status);
			// 重发按钮点击事件
			statusView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showContextMenu(R.array.text_menu_option_retry, message, holder);
				}
			});

		}
		holder.iv_avatar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				EMMessage emMessage = (EMMessage)view.getTag();
				Intent intent = new Intent(TuxingApp.packageName+ SysConstants.PARENTACTION);
//					intent = new Intent(SysConstants.TEAACTION);
				intent.putExtra("userId",Long.valueOf(emMessage.getFrom()));
				context.startActivity(intent);
			}
		});


		TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);

		if (position == 0) {
			timestamp.setText(Utils.getDateTime(context, message.getMsgTime()));
			timestamp.setVisibility(View.VISIBLE);
		} else {
			// 两条消息时间离得如果稍长，显示时间
			EMMessage prevMessage = getItem(position - 1);
			if (prevMessage != null && isCloseEnough(message.getMsgTime(), prevMessage.getMsgTime())) {
				timestamp.setVisibility(View.GONE);
			} else {
				timestamp.setText(Utils.getDateTime(context,message.getMsgTime()));
				timestamp.setVisibility(View.VISIBLE);
			}
		}
		return convertView;
	}
	public static boolean isCloseEnough(long var0, long var2) {
		long var4 = var0 - var2;
		if(var4 < 0L) {
			var4 = -var4;
		}

		return var4 < 60000*5L;
	}

	/**
	 * 文本消息
	 *
	 * @param message
	 * @param holder
	 * @param position
	 */
	private void handleTextMessage(final EMMessage message, final ViewHolder holder, final int position) {
		TextMessageBody txtBody = (TextMessageBody) message.getBody();
//		Spannable span = SmileUtils.getSmiledText(context, txtBody.getMessage());
		Spannable span = SmileUtils.getChatSmiledText(context, txtBody.getMessage());//聊天表情

		String  str =span.toString();
		if (!message.getStringAttribute("url","").equals("")){

//		if (str.contains("[链接]")&&str.contains("{")){
//分享判断
//			String sp[] = str.split("-");
//			String jsons ="";
//			for(int i=1;i<sp.length;i++){
//				if (i>1){
//					jsons = jsons+"-"+sp[i];
//				}else{
//					jsons = jsons+sp[i];
//				}
//			}
//
//			JSONObject json = null;
//			String url = "";
//			String title = "";
//			String imageurl = "";
//			try {
//				json = new JSONObject(jsons);
//				url = json.get("url").toString();
//				title=json.get("articleTitle").toString();
//				imageurl=json.get("coverImageUrl").toString();
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}

			holder.rl_share_sent.setVisibility(View.VISIBLE);
			holder.tv.setVisibility(View.GONE);
			holder.tvshare.setText(span, BufferType.SPANNABLE);

			if (holder.iv_send_pic!=null){
				holder.iv_send_pic.setVisibility(View.VISIBLE);
				holder.iv_send_pic.setImageUrl(message.getStringAttribute("coverImageUrl",""), R.drawable.defal_down_proress,false);
				// 设置长按事件监听
				final String finalUrl = message.getStringAttribute("url","");
				holder.iv_send_pic.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						SysConstants.titlename=message.getStringAttribute("articleTitle","");
						SysConstants.shareimage=message.getStringAttribute("coverImageUrl","");
						SysConstants.messagein=true;
						WebSubDataActivity.invoke(context, finalUrl, "理解孩子");
					}
				});
			}

		}else{
			holder.tv.setVisibility(View.VISIBLE);
			holder.rl_share_sent.setVisibility(View.GONE);
			// 设置内容
			holder.tv.setText(span, BufferType.SPANNABLE);
		}
		// 设置长按事件监听
		holder.tv.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				User user = CoreService.getInstance().getUserManager().getUserInfo(Long.valueOf(message.getFrom()));
				if ((TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion()) && mChatType == ChatActivity.CHATTYPE_GROUP
						&& user != null && user.getType() == Constants.USER_TYPE.PARENT)
						|| message.direct == EMMessage.Direct.SEND) {//教师版或者自己发的
					showContextMenu(R.array.text_revoke_option, message, holder);//可撤回
				} else
					showContextMenu(R.array.text_menu_option, message, holder);
				return true;
			}
		});

		if (message.direct == EMMessage.Direct.SEND) {
			switch (message.status) {
				case SUCCESS: // 发送成功
					holder.pb.setVisibility(View.GONE);
					holder.staus_iv.setVisibility(View.GONE);
					break;
				case FAIL: // 发送失败
					holder.pb.setVisibility(View.GONE);
					holder.staus_iv.setVisibility(View.VISIBLE);
					break;
				case INPROGRESS: // 发送中
					holder.pb.setVisibility(View.VISIBLE);
					holder.staus_iv.setVisibility(View.GONE);
					break;
				default:
					// 发送消息
					sendMsgInBackground(message, holder);
			}
		}
	}



	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return super.getDropDownView(position, convertView, parent);
	}

	/**
	 * 图片消息
	 *
	 * @param message
	 * @param holder
	 * @param position
	 * @param convertView
	 */
	private void handleImageMessage(final EMMessage message, final ViewHolder holder, final int position, View convertView) {
		holder.pb.setTag(position);
		holder.iv.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				User user = CoreService.getInstance().getUserManager().getUserInfo(Long.valueOf(message.getFrom()));
				if ((TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())&&mChatType ==ChatActivity.CHATTYPE_GROUP
						&&user!=null&&user.getType() == Constants.USER_TYPE.PARENT)
						||message.direct == EMMessage.Direct.SEND) {//教师版或者自己发的
					showContextMenu(R.array.text_only_revoke_option, message, holder);//可撤回
				}
				return true;
			}
		});

		// 接收方向的消息
		if (message.direct == EMMessage.Direct.RECEIVE) {
			// "it is receive msg";
			if (message.status == EMMessage.Status.INPROGRESS) {
				// "!!!! back receive";
				holder.iv.setImageResource(R.drawable.default_image);
				showDownloadImageProgress(message, holder);
				// downloadImage(message, holder);
			} else {
				// "!!!! not back receive, show image directly");
				holder.pb.setVisibility(View.GONE);
				holder.tv.setVisibility(View.GONE);
				holder.iv.setImageResource(R.drawable.default_avatar);
				ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
				if (imgBody.getLocalUrl() != null) {
					// String filePath = imgBody.getLocalUrl();
					String remotePath = imgBody.getRemoteUrl();
					String filePath = ImageUtils.getImagePath(remotePath);
					String thumbRemoteUrl = imgBody.getThumbnailUrl();
					String thumbnailPath = ImageUtils.getThumbnailImagePath(thumbRemoteUrl);
					showImageView(thumbnailPath, holder.iv, filePath, imgBody.getRemoteUrl(), message);
				}
			}
			return;
		}

		// 发送的消息
		// process send message
		// send pic, show the pic directly
		ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
		String filePath = imgBody.getLocalUrl();
		if (filePath != null && new File(filePath).exists()) {
			showImageView(ImageUtils.getThumbnailImagePath(filePath), holder.iv, filePath, null, message);
		} else {
			showImageView(ImageUtils.getThumbnailImagePath(filePath), holder.iv, filePath, IMAGE_DIR, message);
		}

		switch (message.status) {
			case SUCCESS:
				holder.pb.setVisibility(View.GONE);
				holder.tv.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.GONE);
				break;
			case FAIL:
				holder.pb.setVisibility(View.GONE);
				holder.tv.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.VISIBLE);
				break;
			case INPROGRESS:
				holder.staus_iv.setVisibility(View.GONE);
				holder.pb.setVisibility(View.VISIBLE);
				holder.tv.setVisibility(View.VISIBLE);
				if (timers.containsKey(message.getMsgId()))
					return;
				// set a timer
				final Timer timer = new Timer();
				timers.put(message.getMsgId(), timer);
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						activity.runOnUiThread(new Runnable() {
							public void run() {
								holder.pb.setVisibility(View.VISIBLE);
								holder.tv.setVisibility(View.VISIBLE);
								holder.tv.setText(message.progress + "%");
								if (message.status == EMMessage.Status.SUCCESS) {
									holder.pb.setVisibility(View.GONE);
									holder.tv.setVisibility(View.GONE);
									// message.setSendingStatus(Message.SENDING_STATUS_SUCCESS);
									timer.cancel();
								} else if (message.status == EMMessage.Status.FAIL) {
									holder.pb.setVisibility(View.GONE);
									holder.tv.setVisibility(View.GONE);
									// message.setSendingStatus(Message.SENDING_STATUS_FAIL);
									// message.setProgress(0);
									holder.staus_iv.setVisibility(View.VISIBLE);
									Toast.makeText(activity,
											activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), Toast.LENGTH_SHORT)
											.show();
									timer.cancel();
								}

							}
						});

					}
				}, 0, 500);
				break;
			default:
				sendPictureMessage(message, holder);
		}
	}
	/**
	 * 语音消息
	 *
	 * @param message
	 * @param holder
	 * @param position
	 * @param convertView
	 */
	private void handleVoiceMessage(final EMMessage message, final ViewHolder holder, final int position, View convertView) {
		VoiceMessageBody voiceBody = (VoiceMessageBody) message.getBody();

		int voiceLen= voiceBody.getLength();
		holder.tv.setText(voiceLen + "\"");
		int vLenInt = 0;
		if(voiceLen >= 60){
			vLenInt = SysConstants.VOICEMAXLENTH;
		}else{
			vLenInt = (SysConstants.VOICEMAXLENTH - SysConstants.VOICEMINLENTH)/60*voiceLen + SysConstants.VOICEMINLENTH;
		}
		vLenInt = dip2px(vLenInt);
		if (message.direct == EMMessage.Direct.RECEIVE) {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.width = vLenInt;
			params.addRule(RelativeLayout.BELOW, R.id.tv_userid);
			params.setMargins(0, 2, 0, 0);
			holder.iv_voice_rl.setLayoutParams(params);
			params.addRule(RelativeLayout.RIGHT_OF, R.id.iv_userhead);
		} else {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.width = vLenInt;
			holder.iv_voice_rl.setLayoutParams(params);
			params.addRule(RelativeLayout.LEFT_OF, R.id.iv_userhead);
		}


		holder.iv_voice_rl.setOnClickListener(new VoicePlayClickListener(message, holder.iv, holder.iv_read_status, this, activity, username));
		holder.iv.setOnClickListener(new VoicePlayClickListener(message, holder.iv, holder.iv_read_status, this, activity, username));
		holder.iv.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				User user = CoreService.getInstance().getUserManager().getUserInfo(Long.valueOf(message.getFrom()));
				if ((TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())&&mChatType ==ChatActivity.CHATTYPE_GROUP
						&&user!=null&&user.getType() == Constants.USER_TYPE.PARENT)
						||message.direct == EMMessage.Direct.SEND) {//教师版或者自己发的
					showContextMenu(R.array.text_only_revoke_option, message, holder);//可撤回
				}
				return true;
			}
		});
		if (((ChatActivity)activity).playMsgId != null
				&& ((ChatActivity)activity).playMsgId.equals(message
				.getMsgId())&& VoicePlayClickListener.isPlaying) {
			AnimationDrawable voiceAnimation;
			if (message.direct == EMMessage.Direct.RECEIVE) {
				holder.iv.setImageResource(R.drawable.voice_from_icon);
			} else {
				holder.iv.setImageResource(R.drawable.voice_to_icon);
			}
			voiceAnimation = (AnimationDrawable) holder.iv.getDrawable();
			voiceAnimation.start();
		} else {
			if (message.direct == EMMessage.Direct.RECEIVE) {
				holder.iv.setImageResource(R.drawable.chatfrom_voice_playing);
			} else {
				holder.iv.setImageResource(R.drawable.chatto_voice_playing);
			}
		}


		if (message.direct == EMMessage.Direct.RECEIVE) {
			if (message.isListened()) {
				// 隐藏语音未听标志
				holder.iv_read_status.setVisibility(View.INVISIBLE);
			} else {
				holder.iv_read_status.setVisibility(View.VISIBLE);
			}
			EMLog.d(TAG, "it is receive msg");
			if (message.status == EMMessage.Status.INPROGRESS) {
				holder.pb.setVisibility(View.VISIBLE);
				EMLog.d(TAG, "!!!! back receive");
				((FileMessageBody) message.getBody()).setDownloadCallback(new EMCallBack() {

					@Override
					public void onSuccess() {
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								holder.pb.setVisibility(View.INVISIBLE);
								notifyDataSetChanged();
							}
						});

					}

					@Override
					public void onProgress(int progress, String status) {
					}

					@Override
					public void onError(int code, String message) {
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								holder.pb.setVisibility(View.INVISIBLE);
							}
						});

					}
				});

			} else {
				holder.pb.setVisibility(View.INVISIBLE);

			}
			return;
		}

		// until here, deal with send voice msg
		switch (message.status) {
			case SUCCESS:
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.GONE);
				break;
			case FAIL:
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.VISIBLE);
				break;
			case INPROGRESS:
				holder.pb.setVisibility(View.VISIBLE);
				holder.staus_iv.setVisibility(View.GONE);
				break;
			default:
				sendMsgInBackground(message, holder);
		}
	}

	/**
	 * 发送消息
	 *
	 * @param message
	 * @param holder
	 */
	public void sendMsgInBackground(final EMMessage message, final ViewHolder holder) {
		holder.staus_iv.setVisibility(View.GONE);
		holder.pb.setVisibility(View.VISIBLE);

		if(!NetworkUtils.isNetWorkAvailable(context)){
			Toast.makeText(context,R.string.network_unavailable,Toast.LENGTH_SHORT).show();
		}else {
			holder.tv_mute.setVisibility(View.GONE);
			final long start = System.currentTimeMillis();
			// 调用sdk发送异步发送方法
			EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

				@Override
				public void onSuccess() {

					updateSendedView(message, holder);
				}

				@Override
				public void onError(int code, String error) {

					updateSendedView(message, holder);
				}

				@Override
				public void onProgress(int progress, String status) {
				}

			});
		}
	}

	/**
	 * 视频消息
	 *
	 * @param message
	 * @param holder
	 * @param position
	 * @param convertView
	 */
	private void handleVideoMessage(final EMMessage message, final ViewHolder holder, final int position, View convertView) {


		holder.iv.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				User user = CoreService.getInstance().getUserManager().getUserInfo(Long.valueOf(message.getFrom()));
				if ((TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion()) && mChatType == ChatActivity.CHATTYPE_GROUP
						&& user != null && user.getType() == Constants.USER_TYPE.PARENT)
						|| message.direct == EMMessage.Direct.SEND) {//教师版或者自己发的
					showContextMenu(R.array.text_only_revoke_option, message, holder);//可撤回
				}
				return true;
			}
		});

		VideoMessageBody videoBody = (VideoMessageBody) message.getBody();
		// final File image=new File(PathUtil.getInstance().getVideoPath(),
		// videoBody.getFileName());
		String localThumb = videoBody.getLocalThumb();

		if (localThumb != null) {

			showVideoThumbView(localThumb, holder.iv, videoBody.getThumbnailUrl(), message);
		}
		if (videoBody.getLength() > 0) {
			String time = DateUtils.toTimeBySecond(videoBody.getLength());
			holder.timeLength.setText(time);
		}
		holder.playBtn.setImageResource(R.drawable.video_download_btn_nor);

		if (message.direct == EMMessage.Direct.RECEIVE) {
			if (videoBody.getVideoFileLength() > 0) {
				String size = TextFormater.getDataSize(videoBody.getVideoFileLength());
				holder.size.setText(size);
			}
		} else {
			if (videoBody.getLocalUrl() != null && new File(videoBody.getLocalUrl()).exists()) {
				String size = TextFormater.getDataSize(new File(videoBody.getLocalUrl()).length());
				holder.size.setText(size);
			}
		}

		if (message.direct == EMMessage.Direct.RECEIVE) {

			// System.err.println("it is receive msg");
			if (message.status == EMMessage.Status.INPROGRESS) {
				// System.err.println("!!!! back receive");
				holder.iv.setImageResource(R.drawable.default_image);
				showDownloadImageProgress(message, holder);

			} else {
				// System.err.println("!!!! not back receive, show image directly");
				holder.iv.setImageResource(R.drawable.default_image);
				if (localThumb != null) {
					showVideoThumbView(localThumb, holder.iv, videoBody.getThumbnailUrl(), message);
				}

			}

			return;
		}
		holder.pb.setTag(position);

		// until here ,deal with send video msg
		switch (message.status) {
			case SUCCESS:
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.GONE);
				holder.tv.setVisibility(View.GONE);
				break;
			case FAIL:
				holder.pb.setVisibility(View.GONE);
				holder.tv.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.VISIBLE);
				break;
			case INPROGRESS:
				if (timers.containsKey(message.getMsgId()))
					return;
				// set a timer
				final Timer timer = new Timer();
				timers.put(message.getMsgId(), timer);
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								holder.pb.setVisibility(View.VISIBLE);
								holder.tv.setVisibility(View.VISIBLE);
								holder.tv.setText(message.progress + "%");
								if (message.status == EMMessage.Status.SUCCESS) {
									holder.pb.setVisibility(View.GONE);
									holder.tv.setVisibility(View.GONE);
									// message.setSendingStatus(Message.SENDING_STATUS_SUCCESS);
									timer.cancel();
								} else if (message.status == EMMessage.Status.FAIL) {
									holder.pb.setVisibility(View.GONE);
									holder.tv.setVisibility(View.GONE);
									// message.setSendingStatus(Message.SENDING_STATUS_FAIL);
									// message.setProgress(0);
									holder.staus_iv.setVisibility(View.VISIBLE);
									Toast.makeText(activity,
											activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), Toast.LENGTH_SHORT)
											.show();
									timer.cancel();
								}

							}
						});

					}
				}, 0, 500);
				break;
			default:
				// sendMsgInBackground(message, holder);
				sendPictureMessage(message, holder);

		}

	}

	/*
	 * chat sdk will automatic download thumbnail image for the image message we
	 * need to register callback show the download progress
	 */
	private void showDownloadImageProgress(final EMMessage message, final ViewHolder holder) {
		EMLog.d(TAG, "!!! show download image progress");
		// final ImageMessageBody msgbody = (ImageMessageBody)
		// message.getBody();
		final FileMessageBody msgbody = (FileMessageBody) message.getBody();
		if(holder.pb!=null)
			holder.pb.setVisibility(View.VISIBLE);
		if(holder.tv!=null)
			holder.tv.setVisibility(View.VISIBLE);

		msgbody.setDownloadCallback(new EMCallBack() {

			@Override
			public void onSuccess() {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// message.setBackReceive(false);
						if (message.getType() == EMMessage.Type.IMAGE) {
							holder.pb.setVisibility(View.GONE);
							holder.tv.setVisibility(View.GONE);
						}
						notifyDataSetChanged();
					}
				});
			}

			@Override
			public void onError(int code, String message) {

			}

			@Override
			public void onProgress(final int progress, String status) {
				if (message.getType() == EMMessage.Type.IMAGE) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							holder.tv.setText(progress + "%");

						}
					});
				}

			}

		});
	}

	/*
	 * send message with new sdk
	 */
	private void sendPictureMessage(final EMMessage message, final ViewHolder holder) {
		try {
			String to = message.getTo();

			// before send, update ui
			holder.staus_iv.setVisibility(View.GONE);
			holder.pb.setVisibility(View.VISIBLE);
			holder.tv.setVisibility(View.VISIBLE);
			holder.tv.setText("0%");

			final long start = System.currentTimeMillis();
			// if (chatType == ChatActivity.CHATTYPE_SINGLE) {
			EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

				@Override
				public void onSuccess() {
					Log.d(TAG, "send image message successfully");
					activity.runOnUiThread(new Runnable() {
						public void run() {
							// send success
							holder.pb.setVisibility(View.GONE);
							holder.tv.setVisibility(View.GONE);
						}
					});
				}

				@Override
				public void onError(int code, String error) {

					activity.runOnUiThread(new Runnable() {
						public void run() {
							holder.pb.setVisibility(View.GONE);
							holder.tv.setVisibility(View.GONE);
							// message.setSendingStatus(Message.SENDING_STATUS_FAIL);
							holder.staus_iv.setVisibility(View.VISIBLE);
							Toast.makeText(activity,
									activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), Toast.LENGTH_SHORT).show();
						}
					});
				}

				@Override
				public void onProgress(final int progress, String status) {
					activity.runOnUiThread(new Runnable() {
						public void run() {
							holder.tv.setText(progress + "%");
						}
					});
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新ui上消息发送状态
	 *
	 * @param message
	 * @param holder
	 */
	private void updateSendedView(final EMMessage message, final ViewHolder holder) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// send success
				if (message.getType() == EMMessage.Type.VIDEO) {
					holder.tv.setVisibility(View.GONE);
				}
				EMLog.d(TAG, "message status : " + message.status);
				if (message.status == EMMessage.Status.SUCCESS) {
					// if (message.getType() == EMMessage.Type.FILE) {
					// holder.pb.setVisibility(View.INVISIBLE);
					// holder.staus_iv.setVisibility(View.INVISIBLE);
					// } else {
					// holder.pb.setVisibility(View.GONE);
					// holder.staus_iv.setVisibility(View.GONE);
					// }

				} else if (message.status == EMMessage.Status.FAIL) {
					// if (message.getType() == EMMessage.Type.FILE) {
					// holder.pb.setVisibility(View.INVISIBLE);
					// } else {
					// holder.pb.setVisibility(View.GONE);
					// }
					// holder.staus_iv.setVisibility(View.VISIBLE);
					Toast.makeText(activity, activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), Toast.LENGTH_SHORT)
							.show();
				}

				notifyDataSetChanged();
			}
		});
	}

	/**
	 * load image into image view
	 *
	 * @param thumbernailPath
	 * @param iv
	 * @return the image exists or not
	 */
	private void showImageView(final String thumbernailPath, final ImageView iv, final String localFullSizePath, String remoteDir,
							   final EMMessage message) {
		Bitmap bitmap = null;
		iv.setClickable(true);
		iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
				if (message.direct == EMMessage.Direct.SEND) {
					NewPicActivity.invoke(context, imgBody.getLocalUrl(), true, paths, true);
				} else {
//					String localPath = SysConstants.FILE_DIR_ROOT + Utils.getImageToken(imgBody.getRemoteUrl())+ "_min";
					NewPicActivity.invoke(context, imgBody.getRemoteUrl(), true, paths, true);
				}

			}
		});
		
			if (message.direct == EMMessage.Direct.SEND) {

				bitmap = ImageCache.getInstance().get(localFullSizePath);
				if(bitmap == null){
					new LoadImageTask(localFullSizePath,iv).execute();
				}else{
					iv.setImageBitmap(bitmap);
				}
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
						params.width = 240;
						params.height = 480;
						iv.setLayoutParams(params);
			} else {
				if (BitmapFactory.decodeFile(thumbernailPath) != null) {
					bitmap = Utils.getSizeBitmap(thumbernailPath, 120);
					if (bitmap != null) {
						iv.setImageBitmap(bitmap);
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
						params.width = 240;
						params.height = bitmap.getHeight()*2;
						iv.setLayoutParams(params);
					}
				} 
		}

	}


	/**
	 * 展示视频缩略图
	 *
	 * @param localThumb
	 *            本地缩略图路径
	 * @param iv
	 * @param thumbnailUrl
	 *            远程缩略图路径
	 * @param message
	 */
	private void showVideoThumbView(final String localThumb, ImageView iv, String thumbnailUrl, final EMMessage message) {
		// first check if the thumbnail image already loaded into cache
		Bitmap bitmap = ImageCache.getInstance().get(localThumb);
		if (bitmap != null) {
			// thumbnail image is already loaded, reuse the drawable
			iv.setImageBitmap(bitmap);
			iv.setClickable(true);
			iv.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					VideoMessageBody videoBody = (VideoMessageBody) message.getBody();
					EMLog.d(TAG, "video view is on click");
					Intent intent = new Intent(activity, VideoPlayerActivity.class);
					intent.putExtra("localpath", videoBody.getLocalUrl());
					intent.putExtra("secret", videoBody.getSecret());
					intent.putExtra("flag", 2);
					intent.putExtra("localThumb",localThumb);
					intent.putExtra("remotepath", videoBody.getRemoteUrl());
					if (message != null && message.direct == EMMessage.Direct.RECEIVE && !message.isAcked
							&& message.getChatType() != ChatType.GroupChat && message.getChatType() != ChatType.ChatRoom) {
						message.isAcked = true;
						try {
							EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					activity.startActivity(intent);

				}
			});

		} else {
			new LoadVideoImageTask().execute(localThumb, thumbnailUrl, iv, activity, message, this);
		}

	}


	public static class ViewHolder {
		ImageView iv;
		TextView tv;
		ProgressBar pb;
		ImageView staus_iv;
		RoundImageView iv_avatar;
		TextView tv_usernick;
		ImageView playBtn;
		TextView timeLength;
		TextView size;
		ImageView iv_read_status;
		RelativeLayout iv_voice_rl;
		LinearLayout container_status_btn;
		TextView tv_mute;

		RelativeLayout rl_share_sent;
		TextView tvshare;
		MyImageView iv_send_pic;
	}
	public void showContextMenu(final int resId,final EMMessage msg, final ViewHolder holder) {
		final CommonDialog dialog = DialogHelper
				.getPinterestDialogCancelable(context);
		dialog.setTitle("选择操作");
		dialog.setNegativeButton("取消", null);
		dialog.setItemsWithoutChk(
				context.getResources().getStringArray(resId),
				new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
											int position, long id) {
						if (position == 0) {
							if(resId == R.array.text_menu_option||resId == R.array.text_revoke_option){//复制消息
								copyTextToBoard(((TextMessageBody) msg.getBody()).getMessage());
							}else if(resId == R.array.text_menu_option_retry){//重试消息
								sendMsgInBackground(msg, holder);
							}else if(resId == R.array.text_only_revoke_option){
								((ChatActivity)activity).sendRevokeMsgTouChuan(msg);//撤回消息
							}
						}else if(position == 1){
							if(resId == R.array.text_revoke_option) {//撤回消息
								((ChatActivity)activity).sendRevokeMsgTouChuan(msg);
							}
						}
						dialog.dismiss();
					}
				});
		dialog.show();
	}
	public void copyTextToBoard(String string) {
		if (TextUtils.isEmpty(string))
			return;
		ClipboardManager clip = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
		clip.setText(string);
		Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show();
	}

	/**
	 * @param event
	 */
	public void onEventMainThread(UserEvent event){
		final User user = event.getUser();
		switch (event.getEvent()) {
			case REQUEST_USER_SUCCESS:
				if(user != null){
					users.put(user.getUserId(), user);
					notifyDataSetChanged();
				}
				break;
			case REQUEST_USER_FAILED:
				break;

		}
	}
	
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public int dip2px(float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

}
