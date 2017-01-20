package com.tuxing.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.tuxing.app.MainActivity;
import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.activity.ContactActivity;
import com.tuxing.app.activity.LyceumActivity;
import com.tuxing.app.base.BaseFragment;
import com.tuxing.app.domain.Conversation;
import com.tuxing.app.easemob.adapter.ChatAllHistoryAdapter;
import com.tuxing.app.easemob.db.InviteMessageDao;
import com.tuxing.app.easemob.ui.ChatActivity;
import com.tuxing.app.ui.dialog.CommonDialog;
import com.tuxing.app.ui.dialog.DialogHelper;
import com.tuxing.app.util.PreferenceUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.UmengData;
import com.tuxing.app.util.Utils;
import com.tuxing.sdk.db.entity.CheckInRecord;
import com.tuxing.sdk.db.entity.ContentItem;
import com.tuxing.sdk.db.entity.Department;
import com.tuxing.sdk.db.entity.HomeWorkRecord;
import com.tuxing.sdk.db.entity.Notice;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.db.helper.UserDbHelper;
import com.tuxing.sdk.event.CheckInEvent;
import com.tuxing.sdk.event.ContentItemGroupEvent;
import com.tuxing.sdk.event.HomeworkEvent;
import com.tuxing.sdk.event.NoticeEvent;
import com.tuxing.sdk.event.SyncContactEvent;
import com.tuxing.sdk.event.UserEvent;
import com.tuxing.sdk.manager.QuoraManager;
import com.tuxing.sdk.manager.impl.QuoraManagerImpl;
import com.tuxing.sdk.modle.CommunionMessage;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.Constants;
import com.tuxing.sdk.utils.NetworkUtils;
import com.tuxing.sdk.utils.StringUtils;
import com.umeng.analytics.MobclickAgent;
import com.xcsd.rpc.proto.EventType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.greenrobot.event.EventBus;
import me.maxwin.view.XListView;

/**
 * 显示所有会话记录，比较简单的实现，更好的可能是把陌生人存入本地，这样取到的聊天记录是可控的
 */
public class MessageFragment extends BaseFragment implements OnClickListener, XListView.IXListViewListener {

    //控制Item是否滑动
    public static boolean swipFlag = true;
    private InputMethodManager inputMethodManager;
    private ChatAllHistoryAdapter adapter;
    public RelativeLayout errorItem;
    public TextView errorText;
    private boolean hidden;
    private Map<String, Conversation> conversationMap = new HashMap<String, Conversation>();
    private List<Conversation> conversationList = new ArrayList<Conversation>();
    private Map<String, Department> departments = new ConcurrentHashMap<String, Department>();
    private Map<String, User> users = new ConcurrentHashMap<String, User>();
    public static boolean isNoticeDeleted = false;
    public static boolean isCheckInDeleted = false;
    public static boolean isHomeworkDeleted = false;
    public static boolean ismessageDeleted = false;
    private XListView xListView;
    NetworkErrorReceiver net_receiver;

    private String communionPereferenceKey;
    private LinearLayout lists;

    private BroadcastReceiver newMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == SysConstants.MSGLISTREFRESHACTION) {
                reloadAllConversations();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_message, null);

        initView(view);

        initData();
        if (!EventBus.getDefault().isRegistered(MessageFragment.this)) {
            EventBus.getDefault().register(this);
        }

        return view;
    }

    private void initData() {
        List<Department> departmentList = getService().getContactManager().getAllDepartment();
        communionPereferenceKey = getPreferenceKey(SysConstants.LAST_QUESTION_MESSAGE_TIME);

        if (!CollectionUtils.isEmpty(departmentList)) {
            for (Department department : departmentList) {
                departments.put(department.getChatGroupId(), department);
            }
        }

        if (getService().getContentManager().getLatestGroupItem() == null) {
            getService().getContentManager().getLatestGroupItems();
        }
    }

    private void initView(View view) {

        if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {//教师版
            setLeftBack("", false);
            setRightNext(false, "", R.drawable.ic_contact_manager_t);
        } else if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())) {//家长版
            setRightNext(false, "", 0);
            setLeftBack("", false);
        }
        errorItem = (RelativeLayout) view.findViewById(R.id.rl_error_item);
        errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);
        xListView = (XListView) view.findViewById(R.id.xListView);
        xListView.setXListViewListener(this);
//******
        lists = (LinearLayout) view.findViewById(R.id.lists);
        lists.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        lists.setLongClickable(true);
        xListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MainActivity.instance.detector.onTouchEvent(event);
                return false;
            }
        });
        xListView.setLongClickable(true);
//   *************

//        if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {//教师版
//            setLeftBack(R.drawable.ic_contact_manager);
//            setRightNext(false, "", R.drawable.ic_wipe_manager);
//        } else if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())) {//家长版
//            setRightNext(false, "", 0);
//            setLeftBack("", false);
//        }
//        errorItem = (RelativeLayout) view.findViewById(R.id.rl_error_item);
//        errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);
//        xListView = (XListView) view.findViewById(R.id.xListView);
//        xListView.setXListViewListener(this);
//		xListView.setPullLoadEnable(false);
    }

    @Override
    public void onclickLeftImg() {
        super.onclickLeftImg();
        Intent intent = new Intent(getActivity(), ContactActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MobclickAgent.onEvent(getActivity(), "click_msg", UmengData.click_msg);
        setTitle(getResources().getString(R.string.tab_msg));
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
            return;
        }
        if (!NetworkUtils.isNetWorkAvailable(getActivity()))
            errorItem.setVisibility(View.VISIBLE);
        else
            errorItem.setVisibility(View.GONE);

        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        adapter = new ChatAllHistoryAdapter(getActivity(), 1, conversationList);

        xListView.setAdapter(adapter);
        xListView.setPullLoadEnable(false);
        reloadAllConversations();

        getActivity().registerReceiver(newMessageReceiver, new IntentFilter(SysConstants.MSGLISTREFRESHACTION));
        net_receiver = new NetworkErrorReceiver();
        IntentFilter filter = new IntentFilter(SysConstants.NETWORKERROR);
        getActivity().registerReceiver(net_receiver, filter);

        xListView.setXListViewListener(this);

        xListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (position >= 1) {
                    Conversation conversation = conversationList.get(position - 1);

                    switch (conversation.type) {
                        case LEARN_GARDEN:
                            JSONObject json = new JSONObject();
                            try {
                                json.put("type","message");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            getService().getDataReportManager().reportExtendedInfo(EventType.CHANNEL_IN, "understandchild", json.toString());
                            Intent intent = new Intent(getActivity(), LyceumActivity.class);
                            startActivity(intent);
                            MobclickAgent.onEvent(getActivity(), "click_msg_wxy", UmengData.click_msg_wxy);
                            break;
                        case NOTICE:
                            JSONObject json_notice = new JSONObject();
                            try {
                                json_notice.put("type","message");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            getService().getDataReportManager().reportExtendedInfo(EventType.CHANNEL_IN, "notice", json_notice.toString());
                            openActivity(TuxingApp.packageName + SysConstants.NOTICEACTION);
                            MobclickAgent.onEvent(getActivity(), "click_msg_notice", UmengData.click_msg_notice);
                            break;
                        case HOMEWORK:
                            JSONObject json_homework = new JSONObject();
                            try {
                                json_homework.put("type","message");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            getService().getDataReportManager().reportExtendedInfo(EventType.CHANNEL_IN, "homework", json_homework.toString());
                            openActivity(TuxingApp.packageName + SysConstants.STUDYHOMEWORKACTION);
                            MobclickAgent.onEvent(getActivity(), "click_msg_homework", UmengData.click_msg_homework);
                            break;
                        case CHECK_IN:
                            openActivity(TuxingApp.packageName + SysConstants.SWINGCARDACTION);
                            MobclickAgent.onEvent(getActivity(), "click_msg_checkIn", UmengData.click_msg_checkIn);
                            break;
                        case GROUP_CHAT:
                            intent = new Intent(getActivity(), ChatActivity.class);

                            intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                            intent.putExtra("groupId", conversation.chatId);
                            intent.putExtra("userName", conversation.name);

                            conversation.count = 0;

                            startActivity(intent);
                            MobclickAgent.onEvent(getActivity(), "click_msg_msg", UmengData.click_msg_msg);
                            break;
                        case CHAT:
                            intent = new Intent(getActivity(), ChatActivity.class);

                            intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
                            intent.putExtra("userId", conversation.chatId);
                            intent.putExtra("userName", conversation.name);

                            conversation.count = 0;

                            startActivity(intent);
                            break;

                        case COMMUNION://教师帮
                            intent = new Intent(TuxingApp.packageName + SysConstants.QUESTIONCIRCLEMSGLIST);
                            startActivity(intent);
                            MobclickAgent.onEvent(getActivity(), "click_msg_communion", UmengData.click_msg_communion);
                            break;
                    }
                }
            }
        });

        xListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (position >= 1) {
                    Conversation conversation = conversationList.get(position - 1);

                    if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())) {//家长版
                        if (conversation.type != Conversation.TYPE.LEARN_GARDEN &&
                                conversation.type != Conversation.TYPE.GROUP_CHAT) {
                            showContextMenu(position);
                        }
                    } else {//非家长版
                        if (conversation.type != Conversation.TYPE.LEARN_GARDEN) {
                            showContextMenu(position);
                        }
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (newMessageReceiver != null) {
            getActivity().unregisterReceiver(newMessageReceiver);
        }
        if (net_receiver != null) {
            getActivity().unregisterReceiver(net_receiver);
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


	/*void hideSoftKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getActivity().getCurrentFocus() != null)
				inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}*/


    /**
     * 刷新页面
     */
    public void refreshLoading() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void reloadAllConversations() {
        conversationList.clear();
        conversationMap.clear();
        loadChatConversations();
        loadNoticeConversation();
        loadCheckInConversation();
        loadLearnGardenConversation();
        loadHomeworkConversation();
        loadQuestionCircleConversation();
        conversationList.addAll(conversationMap.values());
        Collections.sort(conversationList);

        refreshLoading();
    }

    private void loadChatConversations() {
        Hashtable<String, EMConversation> conversations = (Hashtable<String, EMConversation>)
                EMChatManager.getInstance().getAllConversations().clone();

        for (EMConversation emConversation : conversations.values()) {
            if (emConversation.getType() == EMConversationType.Chat) {
                long userId = 0;
                try {
                    userId = Long.valueOf(emConversation.getUserName());
                } catch (Exception e) {
                    userId = 0;
                }

                User user = getService().getUserManager().getUserInfo(userId);

                if (user == null&&emConversation.getUserName().equals("10086")) {
                User userchat = new User();
                    userchat.setNickname("乐学堂客服");
                    userchat.setUserId(10086l);
//                user.setAvatar("");
                    user = userchat;
                }

                if (user != null) {
                    users.put(emConversation.getUserName(), user);
                    Conversation conversation = new Conversation(user.getNickname(),
                            Conversation.TYPE.CHAT);
                    EMMessage lastMessage = emConversation.getLastMessage();
                    if (lastMessage != null) {
                        conversation.latestTime = lastMessage.getMsgTime();
                        conversation.showTime = Utils.getDateTime(getActivity(), lastMessage.getMsgTime());
                        conversation.desc = getMessageDigest(lastMessage);
                        if (lastMessage.direct == EMMessage.Direct.SEND) {
                            conversation.msgStatus = lastMessage.status;
                        } else {
                            conversation.msgStatus = EMMessage.Status.SUCCESS;
                        }
                        conversation.chatId = userId;
                        conversation.emConversation = emConversation;
                        conversation.defaultAvatar = R.drawable.default_avatar;
                        conversation.avatar = user.getAvatar();
                        conversation.count = emConversation.getUnreadMsgCount();

                        this.conversationMap.put(emConversation.getUserName(), conversation);
                    } else {
                        conversation.desc = "";
                        conversation.msgStatus = EMMessage.Status.SUCCESS;
                    }
                } else {
                    getService().getUserManager().requestUserInfoFromServer(userId);
                }
            } else {
                Department department = departments.get(emConversation.getUserName());

                if (department != null) {
                    Conversation conversation = new Conversation(department.getName(),
                            Conversation.TYPE.GROUP_CHAT);
                    try {
                        conversation.chatId = Long.valueOf(emConversation.getUserName());
                    } catch (Exception e) {
                        conversation.chatId = 0;
                    }
                    conversation.emConversation = emConversation;
                    EMMessage lastMessage = emConversation.getLastMessage();
                    if (lastMessage != null) {
                        String name = "";
                        User user = getService().getUserManager().getUserInfo(Long.valueOf(lastMessage.getFrom()));
                        if (user != null) {
                            name = user.getNickname();
                        }

                        if (StringUtils.isBlank(name)) {
                            try {
                                if (lastMessage.getStringAttribute("name") != null) {

                                    name = lastMessage.getStringAttribute("name");
                                }
                            } catch (EaseMobException e) {
                                Log.e(this.getClass().getSimpleName(), "Cannot get message attribute", e);
                            }
                        }

                        conversation.latestTime = lastMessage.getMsgTime();
                        conversation.desc = StringUtils.isBlank(name) ? getMessageDigest(lastMessage) :
                                name + ":" + getMessageDigest(lastMessage);
                        conversation.showTime = Utils.getDateTime(getActivity(), lastMessage.getMsgTime());
                        if (lastMessage.direct == EMMessage.Direct.SEND) {
                            conversation.msgStatus = lastMessage.status;
                        } else {
                            conversation.msgStatus = EMMessage.Status.SUCCESS;
                        }

                        try {
                            conversation.chatId = Long.valueOf(emConversation.getUserName());
                        } catch (Exception e) {
                            conversation.chatId = 0;
                        }
                        conversation.defaultAvatar = R.drawable.default_avatar;
                        conversation.avatar = department.getAvatar();
                        conversation.count = emConversation.getUnreadMsgCount();

                        this.conversationMap.put(emConversation.getUserName(), conversation);
                    } else {
                        conversation.desc = SysConstants.CHAT_GROUP_DEFAULT_DESC;
                        conversation.msgStatus = EMMessage.Status.SUCCESS;
                    }

                }
            }
        }

        //教师版不显示
        if (Constants.VERSION.PARENT.getVersion().equals(TuxingApp.VersionType)) {
            for (Department department : departments.values()) {
                if (!conversationMap.containsKey(department.getChatGroupId())) {
                    Conversation conversation = new Conversation(department.getName(),
                            Conversation.TYPE.GROUP_CHAT);
                    conversation.chatId = Long.valueOf(department.getChatGroupId());
                    conversation.defaultAvatar = R.drawable.default_avatar;
                    conversation.avatar = department.getAvatar();
                    conversation.desc = SysConstants.CHAT_GROUP_DEFAULT_DESC;
                    conversation.msgStatus = EMMessage.Status.SUCCESS;
                    this.conversationMap.put(department.getChatGroupId(), conversation);
                }
            }
        }
    }


    private long noticeId = 0;

    private void loadNoticeConversation() {
        int newNoticeCount = getService().getCounterManager().getCounters().get(Constants.COUNTER.NOTICE);
        Notice notice = getService().getNoticeManager().getLatestOneFromLocal();
        noticeId = PreferenceUtils.getPrefLong(getActivity(), "noticeId", noticeId);
        if (notice != null) {
            if (noticeId != notice.getNoticeId()) {
                noticeId = notice.getNoticeId();
                PreferenceUtils.setPrefLong(getActivity(), "noticeId", noticeId);
                isNoticeDeleted = false;


            }

        }
//        if (newNoticeCount > 0) {
//            isNoticeDeleted = false;
//        }
        if (!isNoticeDeleted && notice != null) {
            Conversation noticeConversation = new Conversation(SysConstants.NOTICE_NAME,
                    Conversation.TYPE.NOTICE);
            noticeConversation.latestTime = notice.getSendTime();
            noticeConversation.desc = notice.getSenderName() + ": " + notice.getContent();
            noticeConversation.defaultAvatar = R.drawable.icon_tz;
            noticeConversation.showTime = Utils.getDateTime(getActivity(), notice.getSendTime());
            noticeConversation.msgStatus = EMMessage.Status.SUCCESS;
            noticeConversation.count = newNoticeCount;
            conversationMap.put(SysConstants.NOTICE_NAME, noticeConversation);
        }


    }


    /**
     * 正式版
     * 从shares 中获取last教师成长消息
     */
    //联网方法不对，无法走逻辑
    private void loadQuestionCircleConversation() {
        long lastMessageTime = PreferenceUtils.getPrefLong(getActivity(), communionPereferenceKey, 0);
        int communionCounter = getService().getCounterManager().getCounters().get(Constants.COUNTER.COMMUNION);

        if (lastMessageTime > 0 || communionCounter > 0) {

            if (communionCounter > 0) {
                lastMessageTime = System.currentTimeMillis();
                PreferenceUtils.setPrefLong(getActivity(), communionPereferenceKey, lastMessageTime);
            }

            Conversation questionConversation = new Conversation(SysConstants.QUESTION_CIRCLE_NAME,
                    Conversation.TYPE.COMMUNION);
            questionConversation.defaultAvatar = R.drawable.icon_message_jsb;
            questionConversation.msgStatus = EMMessage.Status.SUCCESS;

            questionConversation.latestTime = lastMessageTime;
            questionConversation.desc = getString(R.string.new_question_circle_msg);
            questionConversation.showTime = Utils.getDateTime(getActivity(), lastMessageTime);
            questionConversation.count = getService().getCounterManager().getCounters().get(Constants.COUNTER.COMMUNION);

            conversationMap.put(SysConstants.QUESTION_LAST_MESSAGE, questionConversation);
        }
    }

    //显示学能作业
//    private long homeworkId = 0;
//    private int homeworkCount = 0;

    private void loadHomeworkConversation() {
        int newHomeworkCount = getService().getCounterManager().getCounters().get(Constants.COUNTER.HOMEWORK);

        long homeworkId = 0;
        int homeworkCount = 0;

        homeworkCount = PreferenceUtils.getPrefInt(getActivity(), "homeworkCount", homeworkCount);
        if (newHomeworkCount > homeworkCount) {
            isHomeworkDeleted = false;
            PreferenceUtils.setPrefInt(getActivity(), "homeworkCount", newHomeworkCount);
        }
        HomeWorkRecord homeWorkRecord = getService().getHomeWorkManager().getHomeWorkRecordLastOneFromLocal();
        homeworkId = PreferenceUtils.getPrefLong(getActivity(), "homeworkId", homeworkId);

        if (homeWorkRecord != null) {
            if (homeworkId != homeWorkRecord.getHwRecordId()) {
                homeworkId = homeWorkRecord.getHwRecordId();
                PreferenceUtils.setPrefLong(getActivity(), "homeworkId", homeworkId);
                isHomeworkDeleted = false;


            }

        }
//        if (newHomeworkCount > 0) {
//            isHomeworkDeleted = false;
//        }
        if (!isHomeworkDeleted && homeWorkRecord != null) {
            Conversation homeworkConversation = new Conversation(SysConstants.HOMEWORK_NAME,
                    Conversation.TYPE.HOMEWORK);

            homeworkConversation.latestTime = homeWorkRecord.getSendTime();
            homeworkConversation.desc = homeWorkRecord.getSenderName() + "布置给" + homeWorkRecord.getTargetName() + "的" + homeWorkRecord.getTitle();
            homeworkConversation.defaultAvatar = R.drawable.icon_tz;
            homeworkConversation.showTime = Utils.getDateTime(getActivity(), homeWorkRecord.getSendTime());
            homeworkConversation.msgStatus = EMMessage.Status.SUCCESS;
            homeworkConversation.count = newHomeworkCount;
            conversationMap.put(SysConstants.HOMEWORK_NAME, homeworkConversation);
        }


    }

    private void loadCheckInConversation() {
        int newCheckInCount = getService().getCounterManager().getCounters().get(Constants.COUNTER.CHECK_IN);
        CheckInRecord checkIn = getService().getCheckInManager().getLatestOneFromLocal();

        if (newCheckInCount > 0) {
            isCheckInDeleted = false;
        }
        if (!isCheckInDeleted && checkIn != null) {
            Conversation checkInConversation = new Conversation(SysConstants.CARD_NAME,
                    Conversation.TYPE.CHECK_IN);
            checkInConversation.latestTime = checkIn.getCheckInTime();
            checkInConversation.desc = SysConstants.CHECK_IN_DESC;
            checkInConversation.defaultAvatar = R.drawable.icon_sk;
            checkInConversation.showTime = Utils.getDateTime(getActivity(), checkIn.getCheckInTime());
            checkInConversation.msgStatus = EMMessage.Status.SUCCESS;
            checkInConversation.count = newCheckInCount;
            conversationMap.put(SysConstants.CARD_NAME, checkInConversation);
        }
    }

    private void loadLearnGardenConversation() {
        Conversation learnGardenConversation = new Conversation(SysConstants.WXY_NAME,
                Conversation.TYPE.LEARN_GARDEN);
        learnGardenConversation.defaultAvatar = R.drawable.msg_wxy;
        learnGardenConversation.msgStatus = EMMessage.Status.SUCCESS;

        ContentItem contentItem = getService().getContentManager().getLatestGroupItem();
        if (contentItem != null) {
            learnGardenConversation.latestTime = contentItem.getPublishTime();
            learnGardenConversation.desc = contentItem.getTitle();
            learnGardenConversation.showTime = Utils.getDateTime(getActivity(), contentItem.getPublishTime());
            learnGardenConversation.count = getService().getCounterManager().getCounters().get(Constants.COUNTER.LEARN_GARDEN);
        }

        conversationMap.put(SysConstants.WXY_NAME, learnGardenConversation);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            reloadAllConversations();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("message fragment", "resume");
        if (!hidden && !((MainActivity) getActivity()).isConflict) {
            reloadAllConversations();
        }
        MobclickAgent.onResume(getActivity());
        MobclickAgent.onPageStart("消息列表界面");
    }

//	@Override
//	public void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//		if (((MainActivity) getActivity()).isConflict) {
//			outState.putBoolean("isConflict", true);
//		} else if (((MainActivity) getActivity()).getCurrentAccountRemoved()) {
//			outState.putBoolean(SysConstants.ACCOUNT_REMOVED, true);
//		}
//	}

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onclickRightImg() {
        super.onclickRightImg();
//        扫码功能
//        openActivity(TuxingApp.packageName + SysConstants.CAPTUREACTION);
//        MobclickAgent.onEvent(getActivity(), "adress", UmengData.adress);
//        跳转通讯录
        Intent intent = new Intent(getActivity(), ContactActivity.class);
        startActivity(intent);
    }


    public void showContextMenu(final int index) {
        final CommonDialog dialog = DialogHelper.getPinterestDialogCancelable(getActivity());
        dialog.setNegativeButton("取消", null);
//        dialog.setPositiveButton("确定"
//        dialog.setTitle("选择操作");
//        dialog.setNegativeButton("取消", null);
//        dialog.setItemsWithoutChk(getResources().getStringArray(R.array.message_menu_option), new OnItemClickListener() {
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            //            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                if (position == 0) {
                    if (index > 0) {
                        Conversation conversation = conversationList.get(index - 1);
                        EMConversation emConversation = conversation.emConversation;
                        // 删除此会话
                        if (conversation.type == Conversation.TYPE.NOTICE) {
                            isNoticeDeleted = true;
                        }
                        if (conversation.type == Conversation.TYPE.HOMEWORK) {
                            isHomeworkDeleted = true;
                        }
                        if (conversation.type == Conversation.TYPE.CHECK_IN) {
                            isCheckInDeleted = true;
                        }
                        if (conversation.type == Conversation.TYPE.COMMUNION) {
                            ismessageDeleted = true;
                            PreferenceUtils.setPrefLong(getActivity(), communionPereferenceKey, 0);
                        }
                        if (emConversation != null) {
                            EMChatManager.getInstance().deleteConversation(emConversation.getUserName(),
                                    emConversation.isGroup(), true);
                            InviteMessageDao inviteMessageDao = new InviteMessageDao(getActivity());
                            inviteMessageDao.deleteMessage(emConversation.getUserName());
                        }
                        adapter.remove(conversation);
                    }
                }

                dialog.dismiss();
            }
        });
        dialog.setMessage("确认删除吗?");
        dialog.show();
    }

    public void onEventMainThread(SyncContactEvent event) {
        switch (event.getEvent()) {
            case SYNC_CONTACT_SUCCESS://
                initData();
                reloadAllConversations();
                refreshLoading();
                break;
        }
    }

    /**
     * @param event
     */
    public void onEventMainThread(UserEvent event) {
        final User user = event.getUser();
        switch (event.getEvent()) {
            case REQUEST_USER_SUCCESS:
                users.put(String.valueOf(user.getUserId()), user);
                refreshLoading();
                break;
            case REQUEST_USER_FAILED:
                break;

        }
    }

    public void onEventMainThread(NoticeEvent event) {
        switch (event.getEvent()) {
            case NOTICE_INBOX_LATEST_NOTICE_SUCCESS:
                loadNoticeConversation();
                conversationList.clear();
                conversationList.addAll(conversationMap.values());
                Collections.sort(conversationList);
                refreshLoading();
                break;
        }
    }

    //学能作业的事件
    public void onEventMainThread(HomeworkEvent event) {
        switch (event.getEvent()) {
            case HOMEWORK_LIST_LATEST_SUCCESS:
                loadHomeworkConversation();
                conversationList.clear();
                conversationList.addAll(conversationMap.values());
                Collections.sort(conversationList);
                refreshLoading();
                break;
        }
    }

    public void onEventMainThread(CheckInEvent event) {
        switch (event.getEvent()) {
            case CHECKIN_LATEST_RECORDS_SUCCESS:
                loadCheckInConversation();
                conversationList.clear();
                conversationList.addAll(conversationMap.values());
                Collections.sort(conversationList);
                refreshLoading();
                break;
        }
    }

    public void onEventMainThread(ContentItemGroupEvent event) {
        switch (event.getEvent()) {
            case FETCH_LATEST_ITEM_GROUP_SUCCESS:
                loadLearnGardenConversation();
                conversationList.clear();
                conversationList.addAll(conversationMap.values());
                Collections.sort(conversationList);
                refreshLoading();
                break;
        }
    }

    @Override
    public void onRefresh() {
        getService().getCounterManager().updateCounters();
        reloadAllConversations();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                xListView.stopRefresh();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {
        xListView.stopLoadMore();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(getActivity());
        MobclickAgent.onPageEnd("消息列表界面");
    }

    private String getMessageDigest(EMMessage message) {
        String digest = "";
        switch (message.getType()) {
            case IMAGE: // 图片消息
                ImageMessageBody imageBody = (ImageMessageBody) message.getBody();
                digest = this.getResources().getString(R.string.picture);
                break;
            case VOICE:// 语音消息

                digest = this.getResources().getString(R.string.voice_call);
                break;
            case VIDEO:// 视频消息

                digest = this.getResources().getString(R.string.video);
                break;
            case TXT: // 文本消息
                if (!message.getBooleanAttribute(SysConstants.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    TextMessageBody txtBody = (TextMessageBody) message.getBody();
                    digest = txtBody.getMessage();
                }
                break;
            default:
                return "";
        }

        return digest;
    }

    private String getPreferenceKey(String template) {
        User user = getService().getLoginManager().getCurrentUser();

        if (user != null) {
            return String.format(template, user.getUserId());
        }

        return template;
    }

    boolean isNetErr = false;


    class NetworkErrorReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SysConstants.NETWORKERROR.equals(intent.getAction())) {

                if (intent.getBooleanExtra("net", false)) {
                    isNetErr = false;
                } else {//网络有问题
                    isNetErr = true;
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isNetErr)
                            errorItem.setVisibility(View.VISIBLE);
                        else
                            errorItem.setVisibility(View.GONE);
                    }
                }, 3000);

                errorItem.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.v("messagefragment", "net");
                        getActivity().startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                    }
                });
            }

        }
    }
}