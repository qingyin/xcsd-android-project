/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tuxing.app.easemob.iml;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;
import com.easemob.EMCallBack;
import com.easemob.EMChatRoomChangeListener;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.*;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.EasyUtils;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.easemob.HXNotifier;
import com.tuxing.app.easemob.HXNotifier.HXNotificationInfoProvider;
import com.tuxing.app.easemob.HXSDKHelper;
import com.tuxing.app.easemob.HXSDKModel;
import com.tuxing.app.easemob.domain.User;
import com.tuxing.app.easemob.receiver.CallReceiver;
import com.tuxing.app.easemob.ui.ChatActivity;
import com.tuxing.app.easemob.util.CommonUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.sdk.db.entity.RevokedMessage;
import com.tuxing.sdk.facade.CoreService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Demo UI HX SDK helper class which subclass HXSDKHelper
 *
 * @author easemob
 */
public class TuxingHXSDKHelper extends HXSDKHelper {

    private static final String TAG = "DemoHXSDKHelper";
    private static TuxingHXSDKHelper instance = new TuxingHXSDKHelper();
    private String msgId = "";
    public static TuxingHXSDKHelper getInstance() {
        return instance;
    }

    /**
     * EMEventListener
     */
    protected EMEventListener eventListener = null;

    /**
     * contact list in cache
     */
    private Map<String, User> contactList;
    private CallReceiver callReceiver;

    /**
     * 用来记录foreground Activity
     */
    private List<Activity> activityList = new ArrayList<Activity>();

    public void pushActivity(Activity activity) {
        if (!activityList.contains(activity)) {
            activityList.add(0, activity);
        }
    }

    public void popActivity(Activity activity) {
        activityList.remove(activity);
    }

    @Override
    protected void initHXOptions() {
        super.initHXOptions();

        // you can also get EMChatOptions to set related SDK options
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        options.allowChatroomOwnerLeave(getModel().isChatroomOwnerLeaveAllowed());
    }

    @Override
    protected void initListener() {
        super.initListener();
        IntentFilter callFilter = new IntentFilter(EMChatManager.getInstance().getIncomingCallBroadcastAction());
        if (callReceiver == null) {
            callReceiver = new CallReceiver();
        }

        //注册通话广播接收者
        appContext.registerReceiver(callReceiver, callFilter);
        //注册消息事件监听
        initEventListener();
    }

    /**
     * 全局事件监听
     * 因为可能会有UI页面先处理到这个消息，所以一般如果UI页面已经处理，这里就不需要再次处理
     * activityList.size() <= 0 意味着所有页面都已经在后台运行，或者已经离开Activity Stack
     */
    protected void initEventListener() {
        eventListener = new EMEventListener() {
            private BroadcastReceiver broadCastReceiver = null;

            @Override
            public void onEvent(EMNotifierEvent event) {
                EMMessage message = null;
                if (event.getData() instanceof EMMessage) {
                    message = (EMMessage) event.getData();
                }

                switch (event.getEvent()) {
                    case EventNewMessage:
                        //应用在后台，不需要刷新UI,通知栏提示新消息
                        if (activityList.size() <= 0) {
                            HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
                        }
                        break;
                    case EventOfflineMessage:
                        if (activityList.size() <= 0) {
                            EMLog.d(TAG, "received offline messages");
                            List<EMMessage> messages = (List<EMMessage>) event.getData();
                            HXSDKHelper.getInstance().getNotifier().onNewMesg(messages);
                        }
                        break;
                    // below is just giving a example to show a cmd toast, the app should not follow this
                    // so be careful of this
                    case EventNewCMDMessage: {
                        CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
                        String action = cmdMsgBody.action;//获取自定义action
                        if(action.equals(SysConstants.TOUCHUAN_REVOKEMSG)){//撤回
                            try {
                                String from_user_id = message.getStringAttribute("from_user_id");
                                String to_user_id = message.getStringAttribute("to_user_id");
                                String delId = message.getStringAttribute("msg_id");
                                boolean is_group = message.getBooleanAttribute("is_group",false);
                                //删除当前会话的某条聊天记录
                                String from = "";
                                if(is_group){
                                    from = to_user_id;
                                }else
                                    from = from_user_id;
                                EMConversation conversation = EMChatManager.getInstance().getConversation(from);
                                EMMessage delMsg = conversation.getMessage(delId);
                                if(null!=delMsg){
                                    conversation.removeMessage(delId);
                                }else{//存入数据库
                                    RevokedMessage revokedMessage = new RevokedMessage(null,delId,"",from_user_id,to_user_id,is_group);
                                    CoreService.getInstance().getMessageManager().saveRevokedMessage(revokedMessage);
                                }
                                Intent intent = new Intent(TuxingApp.packageName + SysConstants.REFRESHMSGLIST);
                                appContext.sendBroadcast(intent);
                            } catch (EaseMobException e) {
                                e.printStackTrace();
                            }
                        }else{
                            Intent broadcastIntent = new Intent(SysConstants.TOUCHUANACTION);
                            broadcastIntent.putExtra("cmd_value", action);
                            appContext.sendBroadcast(broadcastIntent, null);
                        }

                        //获取扩展属性 此处省略
                        //message.getStringAttribute("");
//                        EMLog.d(TAG, String.format("透传消息：action:%s,message:%s", action, message.toString()));
//                        final String str = appContext.getString(R.string.receive_the_passthrough);
//
//                        final String CMD_TOAST_BROADCAST = "easemob.demo.cmd.toast";
//                        IntentFilter cmdFilter = new IntentFilter(CMD_TOAST_BROADCAST);
//
//                        if (broadCastReceiver == null) {
//                            broadCastReceiver = new BroadcastReceiver() {
//
//                                @Override
//                                public void onReceive(Context context, Intent intent) {
//                                    // TODO Auto-generated method stub
////                                    Toast.makeText(appContext, intent.getStringExtra("cmd_value"), Toast.LENGTH_SHORT).show();
//                                }
//                            };
//
//                            //注册通话广播接收者
//                            appContext.registerReceiver(broadCastReceiver, cmdFilter);
//                        }
                        break;
                    }
                    case EventDeliveryAck:
                        message.setDelivered(true);
                        break;
                    case EventReadAck:
                        message.setAcked(true);
                        break;
                    // add other events in case you are interested in
                    default:
                        break;
                }

            }
        };

        EMChatManager.getInstance().registerEventListener(eventListener);

        EMChatManager.getInstance().addChatRoomChangeListener(new EMChatRoomChangeListener() {
            private final static String ROOM_CHANGE_BROADCAST = "easemob.demo.chatroom.changeevent.toast";
            private final IntentFilter filter = new IntentFilter(ROOM_CHANGE_BROADCAST);
            private boolean registered = false;

            private void showToast(String value) {
                if (!registered) {
                    //注册通话广播接收者
                    appContext.registerReceiver(new BroadcastReceiver() {

                        @Override
                        public void onReceive(Context context, Intent intent) {
                            Toast.makeText(appContext, intent.getStringExtra("value"), Toast.LENGTH_SHORT).show();
                        }

                    }, filter);

                    registered = true;
                }

                Intent broadcastIntent = new Intent(ROOM_CHANGE_BROADCAST);
                broadcastIntent.putExtra("value", value);
                appContext.sendBroadcast(broadcastIntent, null);
            }

            @Override
            public void onChatRoomDestroyed(String roomId, String roomName) {
                showToast(" room : " + roomId + " with room name : " + roomName + " was destroyed");
                Log.i("info", "onChatRoomDestroyed=" + roomName);
            }

            @Override
            public void onMemberJoined(String roomId, String participant) {
                showToast("member : " + participant + " join the room : " + roomId);
                Log.i("info", "onmemberjoined=" + participant);

            }

            @Override
            public void onMemberExited(String roomId, String roomName,
                                       String participant) {
                showToast("member : " + participant + " leave the room : " + roomId + " room name : " + roomName);
                Log.i("info", "onMemberExited=" + participant);

            }

            @Override
            public void onMemberKicked(String roomId, String roomName,
                                       String participant) {
                showToast("member : " + participant + " was kicked from the room : " + roomId + " room name : " + roomName);
                Log.i("info", "onMemberKicked=" + participant);

            }

        });
    }

    /**
     * 自定义通知栏提示内容
     *
     * @return
     */
    @Override
    protected HXNotificationInfoProvider getNotificationListener() {
        //可以覆盖默认的设置
        return new HXNotificationInfoProvider() {

            @Override
            public String getTitle(EMMessage message) {
                //修改标题,这里使用默认
                return null;
            }

            @Override
            public int getSmallIcon(EMMessage message) {
                //设置小图标，这里为默认
                return 0;
            }

            @Override
            public String getDisplayedText(EMMessage message) {
                // 设置状态栏的消息提示，可以根据message的类型做相应提示
                String ticker = CommonUtils.getMessageDigest(message, appContext);
                if (message.getType() == Type.TXT) {
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                }

                return message.getFrom() + ": " + ticker;
            }

            @Override
            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
                return null;
                // return fromUsersNum + "个基友，发来了" + messageNum + "条消息";
            }

            @Override
            public Intent getLaunchIntent(EMMessage message) {
                //设置点击通知栏跳转事件
                Intent intent = new Intent(appContext, ChatActivity.class);
                ChatType chatType = message.getChatType();
                if (chatType == ChatType.Chat) { // 单聊信息
                    intent.putExtra("userId", message.getFrom());
                    intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
                } else { // 群聊信息
                    // message.getTo()为群聊id
                    intent.putExtra("groupId", message.getTo());
                    if (chatType == ChatType.GroupChat) {
                        intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                    }
                }
                return intent;
            }
        };
    }


    @Override
    protected void onConnectionConflict() {

    }

    @Override
    protected void onCurrentAccountRemoved() {

    }


    @Override
    protected HXSDKModel createModel() {
        return new TuxingHXSDKModel(appContext);
    }

    @Override
    public HXNotifier createNotifier() {
        return new HXNotifier() {
            public synchronized void onNewMsg(final EMMessage message) {
                if (EMChatManager.getInstance().isSlientMessage(message)) {
                    return;
                }
                HXSDKModel model = HXSDKHelper.getInstance().getModel();
                List<String> conversions = EMChatManager.getInstance().getChatOptions().getGroupsOfNotificationDisabled();
                if(conversions != null){
                    for(int i = 0; i < conversions.size(); i++){
                        if(conversions.get(i).equals( message.getTo())){
                            return;
                        }
                    }
                }

                if(!model.getSettingMsgNotification()){
                    return;
                }
                
                if(msgId.equals(message.getMsgId())){
                	return;
                }
            	msgId = message.getMsgId();

                String chatUsename = null;
                List<String> notNotifyIds = null;
                // 获取设置的不提示新消息的用户或者群组ids
                if (message.getChatType() == ChatType.Chat) {
                    chatUsename = message.getFrom();
                    notNotifyIds = ((TuxingHXSDKModel) hxModel).getDisabledGroups();
                } else {
                    chatUsename = message.getTo();
                    notNotifyIds = ((TuxingHXSDKModel) hxModel).getDisabledIds();
                }

                if (notNotifyIds == null || !notNotifyIds.contains(chatUsename)) {
                    // 判断app是否在后台
                    if (!EasyUtils.isAppRunningForeground(appContext)) {
                        EMLog.d(TAG, "app is running in backgroud");
                        sendNotification(message, false);
                    } else {
                        sendNotification(message, true);

                    }

                    viberateAndPlayTone(message);
                }
            }
        };
    }

    /**
     * get demo HX SDK Model
     */
    public TuxingHXSDKModel getModel() {
        return (TuxingHXSDKModel) hxModel;
    }

    /**
     * 获取内存中好友user list
     *
     * @return
     */
    public Map<String, User> getContactList() {
        if (getHXId() != null && contactList == null) {
            contactList = ((TuxingHXSDKModel) getModel()).getContactList();
        }

        return contactList;
    }

    /**
     * 设置好友user list到内存中
     *
     * @param contactList
     */
    public void setContactList(Map<String, User> contactList) {
        this.contactList = contactList;
    }

    @Override
    public void logout(final EMCallBack callback) {
        endCall();
        super.logout(new EMCallBack() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                setContactList(null);
                getModel().closeDB();
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub
                if (callback != null) {
                    callback.onProgress(progress, status);
                }
            }

        });
    }

    void endCall() {
        try {
            EMChatManager.getInstance().endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
