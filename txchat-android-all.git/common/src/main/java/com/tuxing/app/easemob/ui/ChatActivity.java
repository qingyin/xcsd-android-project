package com.tuxing.app.easemob.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMChatRoomChangeListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMGroupChangeListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatRoom;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VideoMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;
import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.activity.MediaRecorderActivity;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.easemob.HXSDKHelper;
import com.tuxing.app.easemob.adapter.ExpressionAdapter;
import com.tuxing.app.easemob.adapter.ExpressionPagerAdapter;
import com.tuxing.app.easemob.adapter.MessageAdapter;
import com.tuxing.app.easemob.adapter.VoicePlayClickListener;
import com.tuxing.app.easemob.iml.TuxingHXSDKHelper;
import com.tuxing.app.easemob.util.CommonUtils;
import com.tuxing.app.easemob.util.SmileUtils;
import com.tuxing.app.easemob.util.VoiceRecorder;
import com.tuxing.app.easemob.widget.ExpandGridView;
import com.tuxing.app.qzq.util.ParentNoNullUtil;
import com.tuxing.app.util.MyLog;
import com.tuxing.app.util.PreferenceUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.UmengData;
import com.tuxing.app.util.Utils;
import com.tuxing.sdk.db.entity.Department;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.event.DepartmentEvent;
import com.tuxing.sdk.event.StateChangeEvent;
import com.tuxing.sdk.utils.Constants;
import com.tuxing.sdk.utils.IOUtils;
import com.tuxing.sdk.utils.NetworkUtils;
import com.tuxing.sdk.utils.StringUtils;
import com.umeng.analytics.MobclickAgent;
import com.xcsd.rpc.proto.EventType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

/**
 * 聊天页面
 */
public class ChatActivity extends BaseActivity implements OnClickListener, EMEventListener {
    private static final String TAG = "ChatActivity";
    public static final int REQUEST_CODE_TEXT = 5;
    public static final int REQUEST_CODE_VOICE = 6;
    public static final int REQUEST_CODE_PICTURE = 7;
    public static final int REQUEST_CODE_NET_DISK = 9;
    public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
    public static final int REQUEST_CODE_DOWNLOAD_VIDEO = 13;
    public static final int REQUEST_CODE_DOWNLOAD_VOICE = 15;
    public static final int REQUEST_CODE_SELECT_USER_CARD = 16;
    public static final int REQUEST_CODE_SEND_USER_CARD = 17;
    public static final int REQUEST_CODE_CAMERA = 18;
    public static final int REQUEST_CODE_LOCAL = 105;
    public static final int REQUEST_CODE_CLICK_DESTORY_IMG = 20;
    public static final int REQUEST_CODE_SELECT_VIDEO = 23;

    public static final int RESULT_CODE_COPY = 1;
    public static final int RESULT_CODE_DELETE = 2;
    public static final int RESULT_CODE_FORWARD = 3;
    public static final int RESULT_CODE_OPEN = 4;
    public static final int RESULT_CODE_DWONLOAD = 5;
    public static final int RESULT_CODE_TO_CLOUD = 6;
    public static final int RESULT_CODE_EXIT_GROUP = 7;

    public static final int CHATTYPE_SINGLE = 1;
    public static final int CHATTYPE_GROUP = 2;

    public static final String COPY_IMAGE = "EASEMOBIMG";
    private View recordingContainer;
    private ImageView micImage;
    private ImageView iv_record_bg;
    private TextView recordingHint;
    private ListView listView;
    private EditText mEditTextContent;
    private View buttonSetModeKeyboard;
    private View buttonSetModeVoice;
    private View buttonSend;
    private View buttonPressToSpeak;
    // private ViewPager expressionViewpager;
    private LinearLayout emojiIconContainer;
    private LinearLayout btnContainer;
    private LinearLayout page_select;
    //	private ImageView locationImgview;
    private View more;
    private int position;
    private ClipboardManager clipboard;
    private ViewPager expressionViewpager;
    private InputMethodManager manager;
    private List<String> reslist;
    private Drawable[] micImages;
    private int chatType;
    private EMConversation conversation;
    public static ChatActivity activityInstance = null;
    // 给谁发送消息
    private String toChatUsername;
    private VoiceRecorder voiceRecorder;
    private MessageAdapter adapter;
    private File cameraFile;
    static int resendPos;
    private TextView tv_name;
    private TextView tv_back;
    private ImageView iv_people;
    private ImageView iv_people_two;
    private GroupListener groupListener;

    private ImageView iv_emoticons_normal;
    private ImageView iv_emoticons_checked;
    private RelativeLayout edittext_layout;
    private ProgressBar loadmorePB;
    private boolean isloading;
    private final int pagesize = 20;
    private boolean haveMoreData = true;
    private Button btnMore;
    public String playMsgId;
    private User userInfo;
    private String title_name;
    private View autoSendView;
    private boolean isClassManager;
    private String muteGroupIds = "";//禁言群组ids
    RefreshRecivier refreshReceicer;

    private Handler micImageHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            // 切换msg切换图片
            micImage.setImageDrawable(micImages[msg.what]);
            if (msg.arg1 >= voiceRecorder.MAX_DURATION) {
                voiceRecorder.stopRecoding();
                autoSendView.setPressed(false);
                recordingContainer.setVisibility(View.INVISIBLE);
                sendVoice(voiceRecorder.getVoiceFilePath(), voiceRecorder.getVoiceFileName(toChatUsername),
                        Integer.toString(voiceRecorder.MAX_DURATION), false);
            }
        }
    };
    public EMGroup group;
    public EMChatRoom room;
    public Department currentDepartment;
    public List<Department> departmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        activityInstance = this;
        departmentList = getService().getContactManager().getAllDepartment();
        initView();
        getData();

        EventBus.getDefault().post(new StateChangeEvent(
                StateChangeEvent.EventType.STATE_CHANGED,
                null,
                Arrays.asList(Constants.MESSAGE_TAB_INDEX)
        ));
        muteGroupIds = getService().getUserManager().getUserProfile(Constants.SETTING_FIELD.MUTE, "");
        refreshReceicer = new RefreshRecivier();
        registerReceiver(refreshReceicer, new IntentFilter(TuxingApp.packageName + SysConstants.REFRESHMSGLIST));
    }

    @Override
    public void getData() {
        super.getData();
        setUpView();
    }

    /**
     * initView
     */
    protected void initView() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        iv_people = (ImageView) findViewById(R.id.iv_people);
        iv_people_two = (ImageView) findViewById(R.id.iv_people_two);
        if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())) {
            iv_people.setImageResource(R.drawable.ic_group_manager_p);
            iv_people_two.setImageResource(R.drawable.ic_group_manager_p);
            tv_back.setTextColor(getResources().getColor(R.color.text_parent));
            Drawable drawable = null;
            drawable = getResources().getDrawable(R.drawable.ic_back_title_p);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tv_back.setCompoundDrawables(drawable, null, null, null);
        }
        tv_back.setOnClickListener(this);

        tv_name = (TextView) findViewById(R.id.name);
        recordingContainer = findViewById(R.id.recording_container);
        micImage = (ImageView) findViewById(R.id.mic_image);
        recordingHint = (TextView) findViewById(R.id.recording_hint);
        iv_record_bg = (ImageView) findViewById(R.id.iv_record_bg);
        listView = (ListView) findViewById(R.id.list);
        mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
        buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard);
        edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
        buttonSetModeVoice = findViewById(R.id.btn_set_mode_voice);
        buttonSend = findViewById(R.id.btn_send);
        buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
        expressionViewpager = (ViewPager) findViewById(R.id.vPager);
        emojiIconContainer = (LinearLayout) findViewById(R.id.ll_face_container);
        btnContainer = (LinearLayout) findViewById(R.id.ll_btn_container);
        page_select = (LinearLayout) findViewById(R.id.page_select);
//		locationImgview = (ImageView) findViewById(R.id.btn_location);
        iv_emoticons_normal = (ImageView) findViewById(R.id.iv_emoticons_normal);
        iv_emoticons_checked = (ImageView) findViewById(R.id.iv_emoticons_checked);
        loadmorePB = (ProgressBar) findViewById(R.id.pb_load_more);
        btnMore = (Button) findViewById(R.id.btn_more);
        iv_emoticons_normal.setVisibility(View.VISIBLE);
        iv_emoticons_checked.setVisibility(View.INVISIBLE);
        more = findViewById(R.id.more);
//		edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);

        // 动画资源文件,用于录制语音时
        micImages = new Drawable[]{getResources().getDrawable(R.drawable.record_animate_01),
                getResources().getDrawable(R.drawable.record_animate_02),
                getResources().getDrawable(R.drawable.record_animate_03),
                getResources().getDrawable(R.drawable.record_animate_04),
                getResources().getDrawable(R.drawable.record_animate_05),
                getResources().getDrawable(R.drawable.record_animate_06),
                getResources().getDrawable(R.drawable.record_animate_07),
                getResources().getDrawable(R.drawable.record_animate_08)};

        // 表情list
        reslist = getExpressionRes(SmileUtils.emoticons.size());
        // 初始化表情viewpager
        List<View> views = new ArrayList<View>();
        int count = SmileUtils.emoticons.size() % 20;
        if (count != 0)
            count = SmileUtils.emoticons.size() / 20 + 1;
        else
            count = SmileUtils.emoticons.size() / 20;
        for (int i = 0; i < count; i++) {
            View gv = getGridChildView(i, count);
            views.add(gv);
        }
        expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));
        expressionViewpager.setOnPageChangeListener(new GuidePageChangeListener());
        for (int i = 0; i < views.size(); i++) {
            ImageView image = new ImageView(mContext);
            if (i == 0) {
                image.setImageResource(R.drawable.page_focused);
            } else {
                image.setImageResource(R.drawable.page_unfocused);
            }
            image.setPadding(Utils.dip2px(mContext, 10), 0, 0, 40);
            image.setId(i);
            page_select.addView(image);
        }
        edittext_layout.requestFocus();
        voiceRecorder = new VoiceRecorder(micImageHandler);
        buttonPressToSpeak.setOnTouchListener(new PressToSpeakListen());
        mEditTextContent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//				edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
                more.setVisibility(View.GONE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
                emojiIconContainer.setVisibility(View.GONE);
                btnContainer.setVisibility(View.GONE);
            }
        });
        // 监听文字框
        mEditTextContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    btnMore.setVisibility(View.GONE);
                    buttonSend.setVisibility(View.VISIBLE);
                } else {
                    btnMore.setVisibility(View.VISIBLE);
                    buttonSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void setUpView() {
        iv_emoticons_normal.setOnClickListener(this);
        iv_emoticons_checked.setOnClickListener(this);
        // position = getIntent().getIntExtra("position", -1);
        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
        // 判断单聊还是群聊
        chatType = getIntent().getIntExtra("chatType", CHATTYPE_SINGLE);
        title_name = getIntent().getStringExtra("userName");
        boolean isNotifier = getIntent().getBooleanExtra("isNotifier", false);
        if (isNotifier) {
            MobclickAgent.onEvent(mContext, "click_plush", UmengData.click_plush);
        }

        if (chatType == CHATTYPE_SINGLE) { // 单聊
            toChatUsername = String.valueOf(getIntent().getLongExtra("userId", 0));
            if (title_name != null && !"".equals(title_name)) {
                tv_name.setText(title_name);
            } else {
                userInfo = getService().getUserManager().getUserInfo(Long.valueOf(toChatUsername));
                if (null != userInfo)
                    tv_name.setText(userInfo.getNickname());
            }
            if (toChatUsername != null && !"".equals(toChatUsername))
                findViewById(R.id.container_to_group).setVisibility(View.GONE);
        } else {
            // 群聊
            findViewById(R.id.container_to_group).setVisibility(View.VISIBLE);
            findViewById(R.id.container_to_group).setOnClickListener(this);
            findViewById(R.id.container_remove).setVisibility(View.GONE);
            toChatUsername = String.valueOf(getIntent().getLongExtra("groupId", 0));
            getService().getDepartmentManager().getDepartmentByChatGroupId(toChatUsername);
            for (Department dep : departmentList) {
                if (toChatUsername == dep.getChatGroupId()) {
                    currentDepartment = dep;
                }
            }
            if (title_name != null && !"".equals(title_name)) {
                tv_name.setText(title_name);
            }
            if (chatType == CHATTYPE_GROUP) {
                onGroupViewCreation();
            }
        }

        // for chatroom type, we only init conversation and create view adapter on success
        if ((chatType == CHATTYPE_GROUP || chatType == CHATTYPE_SINGLE)) {
            if (EMChat.getInstance().isLoggedIn()) {
                onConversationInit();
                onListViewCreation();
            }
        }
    }

    protected void onConversationInit() {
        if (chatType == CHATTYPE_SINGLE) {
            conversation = EMChatManager.getInstance().getConversationByType(toChatUsername, EMConversationType.Chat);
        } else if (chatType == CHATTYPE_GROUP) {
            conversation = EMChatManager.getInstance().getConversationByType(toChatUsername, EMConversationType.GroupChat);
        }

        // 把此会话的未读数置为0
        conversation.markAllMessagesAsRead();

        // 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
        // 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
        final List<EMMessage> msgs = conversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            if (chatType == CHATTYPE_SINGLE) {
                conversation.loadMoreMsgFromDB(msgId, pagesize);
            } else {
                conversation.loadMoreGroupMsgFromDB(msgId, pagesize);
            }
        }

        EMChatManager.getInstance().addChatRoomChangeListener(new EMChatRoomChangeListener() {

            @Override
            public void onChatRoomDestroyed(String roomId, String roomName) {
                if (roomId.equals(toChatUsername)) {
                    finish();
                }
            }

            @Override
            public void onMemberJoined(String roomId, String participant) {
            }

            @Override
            public void onMemberExited(String roomId, String roomName,
                                       String participant) {

            }

            @Override
            public void onMemberKicked(String roomId, String roomName,
                                       String participant) {
                if (roomId.equals(toChatUsername)) {
                    String curUser = EMChatManager.getInstance().getCurrentUser();
                    if (curUser.equals(participant)) {
                        EMChatManager.getInstance().leaveChatRoom(toChatUsername);
                        finish();
                    }
                }
            }

        });
    }

    protected void onListViewCreation() {
        adapter = new MessageAdapter(ChatActivity.this, toChatUsername, chatType, getService());
        // 显示消息
        listView.setAdapter(adapter);

        listView.setOnScrollListener(new ListScrollListener());
        adapter.refreshSelectLast();

        listView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                more.setVisibility(View.GONE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
                emojiIconContainer.setVisibility(View.GONE);
                btnContainer.setVisibility(View.GONE);
                return false;
            }
        });
    }

    protected void onGroupViewCreation() {
        group = EMGroupManager.getInstance().getGroup(toChatUsername);

        // 监听当前会话的群聊解散被T事件
        groupListener = new GroupListener();
        EMGroupManager.getInstance().addGroupChangeListener(groupListener);
    }

    /**
     * onActivityResult
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CODE_EXIT_GROUP) {
            setResult(RESULT_OK);
            finish();
            return;
        }
        if (resultCode == RESULT_OK) { // 清空消息
            if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
                if (cameraFile != null && cameraFile.exists())
                    sendPicture(cameraFile.getAbsolutePath());
            } else if (requestCode == REQUEST_CODE_LOCAL) { // 发送本地图片
                if (data != null) {
                    final ArrayList<String> phonePath = data.getStringArrayListExtra("file_next_uris");
                    if (phonePath != null && phonePath.size() > 0) {
//						new Thread(new Runnable() {
//							@Override
//							public void run() {
                        for (int i = 0; i < phonePath.size(); i++) {
                            File file = new File(phonePath.get(i));
                            try {
                                if (file != null) {
                                    File fileTo = new File(SysConstants.FILE_DIR_ROOT + file.getName());
                                    IOUtils.copyFile(file, fileTo);
                                    if (fileTo != null) {
                                        sendPicture(SysConstants.FILE_DIR_ROOT + file.getName());
                                    } else
                                        sendPicture(phonePath.get(i));
                                } else
                                    sendPicture(phonePath.get(i));
                            } catch (Exception e) {
                                showAndSaveLog(TAG, "copyFile error", false);
                                e.printStackTrace();
                            }
                        }
//								
//							}
//						}).start();
                    }
//					Uri selectedImage = data.getData();
//					if (selectedImage != null) {
//						sendPicByUri(selectedImage);
//					}
//					sendPicture();
                }
            } else if (requestCode == REQUEST_CODE_TEXT || requestCode == REQUEST_CODE_VOICE
                    || requestCode == REQUEST_CODE_PICTURE) {
                resendMessage();
            } else if (requestCode == REQUEST_CODE_COPY_AND_PASTE) {
                // 粘贴
                if (!TextUtils.isEmpty(clipboard.getText())) {
                    String pasteText = clipboard.getText().toString();
                    if (pasteText.startsWith(COPY_IMAGE)) {
                        // 把图片前缀去掉，还原成正常的path
                        sendPicture(pasteText.replace(COPY_IMAGE, ""));
                    }

                }
            } else if (requestCode == REQUEST_CODE_SELECT_VIDEO) {
                //发送视频
                String videoPath = data.getStringExtra("videoPath");
                int duration = data.getIntExtra("dur", 0);
                if (!TextUtils.isEmpty(videoPath)) {

                    File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
                    Bitmap bitmap = null;
                    FileOutputStream fos = null;
                    try {
                        if (!file.getParentFile().exists()) {
                            file.getParentFile().mkdirs();
                        }
                        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
                        if (bitmap == null) {
                            EMLog.d("chatactivity", "problem load video thumbnail bitmap,use default icon");
                            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_panel_video_icon);
                        }
                        fos = new FileOutputStream(file);

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            fos = null;
                        }
                        if (bitmap != null) {
                            bitmap.recycle();
                            bitmap = null;
                        }

                    }
                    sendVideo(videoPath, file.getAbsolutePath(), duration / 1000);

                }

            } else if (conversation.getMsgCount() > 0) {
                adapter.refresh();
                setResult(RESULT_OK);
            }
        }
    }

    /**
     * 消息图标点击事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        String st1 = getResources().getString(R.string.not_connect_to_server);
        int id = view.getId();
        if (id == R.id.btn_send) {// 点击发送按钮(发文字和表情)
            String s = mEditTextContent.getText().toString();
            if (!checkChatMute()) {
                if (!TextUtils.isEmpty(s.trim()))
                    sendText(s);
            }
        } else if (id == R.id.btn_take_picture) {
            if (!checkChatMute()) {
                selectPicFromCamera();// 点击照相图标
            }
        } else if (id == R.id.btn_picture) {
            if (!checkChatMute()) {
                selectPicFromLocal(); // 点击图片图标
            }
        } else if (id == R.id.btn_video) {
            if (!checkChatMute()) {
                recordVideo(); // 点击视频图标
            }
        } else if (id == R.id.iv_emoticons_normal) { // 点击显示表情框
            if (!checkChatMute()) {
                more.setVisibility(View.VISIBLE);
                iv_emoticons_normal.setVisibility(View.INVISIBLE);
                iv_emoticons_checked.setVisibility(View.VISIBLE);
                page_select.setVisibility(View.VISIBLE);
                btnContainer.setVisibility(View.GONE);
                emojiIconContainer.setVisibility(View.VISIBLE);
            }
            hideKeyboard();
        } else if (id == R.id.iv_emoticons_checked) { // 点击隐藏表情框
            if (!checkChatMute()) {
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
                page_select.setVisibility(View.GONE);
                btnContainer.setVisibility(View.VISIBLE);
                emojiIconContainer.setVisibility(View.GONE);
                more.setVisibility(View.GONE);
            }

        } else if (id == R.id.container_to_group) {
            toGroupDetails();
        } else if (id == R.id.tv_back) {
            finish();
        }
    }

    /**
     * 事件监听
     * <p/>
     * see {@link EMNotifierEvent}
     */
    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage: {
                //获取到message
                EMMessage message = (EMMessage) event.getData();

                String username = null;
                //群组消息
                if (message.getChatType() == ChatType.GroupChat || message.getChatType() == ChatType.ChatRoom) {
                    username = message.getTo();
                } else {
                    //单聊消息
                    username = message.getFrom();
                }

                //如果是当前会话的消息，刷新聊天页面
                if (username.equals(getToChatUsername())) {
                    refreshUIWithNewMessage();
                    //声音和震动提示有新消息
                    HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(message);
                } else {
                    //如果消息不是和当前聊天ID的消息
                    HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
                }
                conversation.markAllMessagesAsRead();

                break;
            }
            case EventDeliveryAck: {
                //获取到message
                EMMessage message = (EMMessage) event.getData();
                refreshUI();
                break;
            }
            case EventReadAck: {
                //获取到message
                EMMessage message = (EMMessage) event.getData();
                refreshUI();
                break;
            }
            case EventOfflineMessage: {
                //a list of offline messages
                //List<EMMessage> offlineMessages = (List<EMMessage>) event.getData();
                refreshUI();
                break;
            }
            default:
                break;
        }

    }


    private void refreshUIWithNewMessage() {
        if (adapter == null) {
            return;
        }

        runOnUiThread(new Runnable() {
            public void run() {
                adapter.refreshSelectLast();
            }
        });
    }

    private void refreshUI() {
        if (adapter == null) {
            return;
        }

        runOnUiThread(new Runnable() {
            public void run() {
                adapter.refresh();
            }
        });
    }

    /**
     * 照相获取图片
     */
    public void selectPicFromCamera() {
        if (!CommonUtils.isExitsSdcard()) {
            String st = getResources().getString(R.string.sd_card_does_not_exist);
            Toast.makeText(getApplicationContext(), st, Toast.LENGTH_SHORT).show();
            return;
        }

        cameraFile = new File(PathUtil.getInstance().getImagePath(), TuxingHXSDKHelper.getInstance().getHXId()
                + System.currentTimeMillis() + ".jpg");
        cameraFile.getParentFile().mkdirs();
        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                REQUEST_CODE_CAMERA);
    }

    /**
     * 从图库获取图片
     */
    public void selectPicFromLocal() {
        //TODO
//		Intent intent;
//		if (Build.VERSION.SDK_INT < 19) {
//			intent = new Intent(Intent.ACTION_GET_CONTENT);
//			intent.setType("image/*");
//
//		} else {
//			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//		}
        Utils.getMultiPhoto(ChatActivity.this, REQUEST_CODE_LOCAL, 9);
//		startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }

    /**
     * 录制视频
     */
    public void recordVideo() {
        Intent intent = new Intent(ChatActivity.this, MediaRecorderActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);


    }

    /**
     * 发送文本消息
     *
     * @param content message content
     *                boolean resend
     */
    private void sendText(String content) {

        if (content.length() > 0) {
            if (content.length() > 500) {
                content = StringUtils.substring(content, 0, 500);
            }
            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
            // 如果是群聊，设置chattype,默认是单聊
            if (chatType == CHATTYPE_GROUP) {
                message.setChatType(ChatType.GroupChat);
            }

            TextMessageBody txtBody = new TextMessageBody(content);
            // 设置消息body
            message.addBody(txtBody);
            // 设置要发给谁,用户username或者群聊groupid
            message.setReceipt(toChatUsername);
            if (user != null && user.getNickname() != null)
                message.setAttribute("name", user.getNickname());
            else
                message.setAttribute("name", "");

            if(toChatUsername.equals("10086")){//客服会话
                //客服
                JSONObject visitorJson = new JSONObject();
                JSONObject weiChat = new JSONObject();
                try {
                    visitorJson.put("trueName", user.getRealname()!=null?user.getRealname():"");
                    visitorJson.put("userNickname", user.getNickname());
                    visitorJson.put("phone", user.getMobile());
//                    visitorJson.put("description", feedBackInfo());
                    visitorJson.put("description", "000000007");
                    weiChat.put("visitor",visitorJson);
                    message.setAttribute("weichat", weiChat);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (conversation != null) {
                // 把messgage加到conversation中
                conversation.addMessage(message);
//			conversation.setExtField("ssss");
                // 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
                adapter.refreshSelectLast();
                mEditTextContent.setText("");

                setResult(RESULT_OK);
                Spannable spannable = Spannable.Factory.getInstance().newSpannable(content);
                boolean isFace = SmileUtils.addChatSmiles(mContext, spannable);

//                SINGLE_CHAT = 11;	//单聊		toPid
//                GROUP_CHAT = 12;	//群聊		groupId
//                TEACHER_GROUP_CHAT = 13;	//教师群聊		groupId
                if (chatType == CHATTYPE_GROUP) {
                    if (title_name.toString().equals("老师")) {
                        getService().getDataReportManager().reportEventBid(EventType.TEACHER_GROUP_CHAT, toChatUsername+"");
//                        showToast("老师群聊");
                    } else {
                        getService().getDataReportManager().reportEventBid(EventType.GROUP_CHAT, toChatUsername+"");
//                        showToast("大家群聊");
                    }
                } else {
                    getService().getDataReportManager().reportEventBid(EventType.SINGLE_CHAT, toChatUsername+"");
//                    showToast("个人单聊");
                }

                if (isFace) {
                    MobclickAgent.onEvent(mContext, "msg_seng_text_face", UmengData.msg_seng_text_face);
                } else {
                    MobclickAgent.onEvent(mContext, "msg_seng_text", UmengData.msg_seng_text);
                }
            }
        }
    }

    /**
     * 发送语音
     *
     * @param filePath
     * @param fileName
     * @param length
     * @param isResend
     */
    private void sendVoice(String filePath, String fileName, String length, boolean isResend) {
        if (!(new File(filePath).exists())) {
            return;
        }
        try {
            final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VOICE);
            // 如果是群聊，设置chattype,默认是单聊
            if (chatType == CHATTYPE_GROUP) {
                message.setChatType(ChatType.GroupChat);
            }
            if (user != null)
                message.setAttribute("name", user.getNickname());
            else
                message.setAttribute("name", "");
            message.setReceipt(toChatUsername);
            int len = Integer.parseInt(length);
            VoiceMessageBody body = new VoiceMessageBody(new File(filePath), len);
            message.addBody(body);

            conversation.addMessage(message);
            adapter.refreshSelectLast();
            setResult(RESULT_OK);
            MobclickAgent.onEvent(mContext, "msg_seng_voice", UmengData.msg_seng_voice);
            // send file
            // sendVoiceSub(filePath, fileName, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送图片
     *
     * @param filePath
     */
    private void sendPicture(final String filePath) {
        String to = toChatUsername;
        // create and add image message in view
        final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
        // 如果是群聊，设置chattype,默认是单聊
        if (chatType == CHATTYPE_GROUP) {
            message.setChatType(ChatType.GroupChat);
        }
        if (user != null)
//            message.setAttribute("name", user.getNickname());
            message.setAttribute("name", ParentNoNullUtil.getNickName(user));
        else
            message.setAttribute("name", "");
        message.setReceipt(to);
        ImageMessageBody body = new ImageMessageBody(new File(filePath));
        // 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
        // body.setSendOriginalImage(true);
        message.addBody(body);
        conversation.addMessage(message);

        if (adapter == null)
            listView.setAdapter(adapter);
        else
            adapter.notifyDataSetChanged();
        adapter.refreshSelectLast();
        setResult(RESULT_OK);
        MobclickAgent.onEvent(mContext, "msg_seng_pic", UmengData.msg_seng_pic);
        // more(more);
    }

    /**
     * 根据图库图片uri发送图片
     *
     * @param selectedImage
     */
    private void sendPicByUri(Uri selectedImage) {
        // String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
        String st8 = getResources().getString(R.string.cant_find_pictures);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex("_data");
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            sendPicture(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }
            sendPicture(file.getAbsolutePath());
        }

    }

    /**
     * 重发消息
     */
    private void resendMessage() {
        EMMessage msg = null;
        msg = conversation.getMessage(resendPos);
        // msg.setBackSend(true);
        msg.status = EMMessage.Status.CREATE;

        adapter.refreshSeekTo(resendPos);
    }

    /**
     * 显示语音图标按钮
     *
     * @param view
     */
    public void setModeVoice(View view) {
        if (!checkChatMute()) {
            hideKeyboard();
            edittext_layout.setVisibility(View.GONE);
            more.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
            buttonSetModeKeyboard.setVisibility(View.VISIBLE);
            buttonSend.setVisibility(View.GONE);
            btnMore.setVisibility(View.VISIBLE);
            buttonPressToSpeak.setVisibility(View.VISIBLE);
            iv_emoticons_normal.setVisibility(View.VISIBLE);
            iv_emoticons_checked.setVisibility(View.INVISIBLE);
            btnContainer.setVisibility(View.VISIBLE);
            emojiIconContainer.setVisibility(View.GONE);
        }

    }

    /**
     * 显示键盘图标
     *
     * @param view
     */
    public void setModeKeyboard(View view) {
        edittext_layout.setVisibility(View.VISIBLE);
        more.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        buttonSetModeVoice.setVisibility(View.VISIBLE);
        // mEditTextContent.setVisibility(View.VISIBLE);
        mEditTextContent.requestFocus();
        mEditTextContent.setSelection(mEditTextContent.getText().toString().length());
        // buttonSend.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.GONE);
        if (TextUtils.isEmpty(mEditTextContent.getText())) {
            btnMore.setVisibility(View.VISIBLE);
            buttonSend.setVisibility(View.GONE);
        } else {
            btnMore.setVisibility(View.GONE);
            buttonSend.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 发送视频消息
     */
    private void sendVideo(final String filePath, final String thumbPath, final int length) {
        final File videoFile = new File(filePath);
        if (!videoFile.exists()) {
            return;
        }
        try {
            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.VIDEO);
            // 如果是群聊，设置chattype,默认是单聊
            if (chatType == CHATTYPE_GROUP) {
                message.setChatType(ChatType.GroupChat);
            }
            String to = toChatUsername;
            message.setReceipt(to);
            VideoMessageBody body = new VideoMessageBody(videoFile, thumbPath, length, videoFile.length());
            message.addBody(body);
            conversation.addMessage(message);
            listView.setAdapter(adapter);
            adapter.refreshSelectLast();
            setResult(RESULT_OK);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 点击进入群组详情
     */
    public void toGroupDetails() {
        if (room == null && group == null) {
            Intent intent = new Intent(TuxingApp.packageName + SysConstants.CLASSMANAGERACTION);
            intent.putExtra("groupId", Long.valueOf(currentDepartment.getDepartmentId()));
            openActivityOrFragment(intent);
            return;
        }
        if (chatType == CHATTYPE_GROUP) {
            Intent intent = new Intent(TuxingApp.packageName + SysConstants.CLASSMANAGERACTION);
            intent.putExtra("groupId", Long.valueOf(currentDepartment.getDepartmentId()));
            openActivityOrFragment(intent);
            return;

//			startActivityForResult((new Intent(this, GroupDetailsActivity.class).putExtra("groupId", toChatUsername)),
//					REQUEST_CODE_GROUP_DETAIL);
        } else {
//			startActivityForResult((new Intent(this, ChatRoomDetailsActivity.class).putExtra("roomId", toChatUsername)),
//					REQUEST_CODE_GROUP_DETAIL);
        }
    }

    /**
     * 显示或隐藏图标按钮页
     *
     * @param view
     */
    public void more(View view) {
        if (more.getVisibility() == View.GONE) {
            EMLog.d(TAG, "more gone");
            hideKeyboard();
            more.setVisibility(View.VISIBLE);
            btnContainer.setVisibility(View.VISIBLE);
            emojiIconContainer.setVisibility(View.GONE);
            page_select.setVisibility(View.GONE);
        } else {
            if (emojiIconContainer.getVisibility() == View.VISIBLE) {
                emojiIconContainer.setVisibility(View.GONE);
                page_select.setVisibility(View.GONE);
                btnContainer.setVisibility(View.VISIBLE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
            } else {
                more.setVisibility(View.GONE);
            }

        }

    }

    /**
     * 点击文字输入框
     *
     * @param v
     */
    public void editClick(View v) {
        listView.setSelection(listView.getCount() - 1);
        if (more.getVisibility() == View.VISIBLE) {
            more.setVisibility(View.GONE);
            iv_emoticons_normal.setVisibility(View.VISIBLE);
            iv_emoticons_checked.setVisibility(View.INVISIBLE);
        }

    }

    private PowerManager.WakeLock wakeLock;

    /**
     * 按住说话listener
     */
    class PressToSpeakListen implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            autoSendView = v;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!CommonUtils.isExitsSdcard()) {
                        String st4 = getResources().getString(R.string.Send_voice_need_sdcard_support);
                        Toast.makeText(ChatActivity.this, st4, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        wakeLock.acquire();
                        if (VoicePlayClickListener.isPlaying)
                            VoicePlayClickListener.currentPlayListener.stopPlayVoice();
                        recordingContainer.setVisibility(View.VISIBLE);
                        recordingHint.setText(getString(R.string.move_up_to_cancel));
                        iv_record_bg.setBackgroundResource(R.drawable.recording_bg);
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                        micImage.setVisibility(View.VISIBLE);
                        voiceRecorder.startRecording(null, toChatUsername, getApplicationContext());
                    } catch (Exception e) {
                        e.printStackTrace();
                        v.setPressed(false);
                        if (wakeLock.isHeld())
                            wakeLock.release();
                        if (voiceRecorder != null)
                            voiceRecorder.discardRecording();
                        recordingContainer.setVisibility(View.INVISIBLE);
                        Toast.makeText(ChatActivity.this, R.string.recoding_fail, Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY() < 0) {
                        micImage.setVisibility(View.GONE);
                        recordingHint.setText(getString(R.string.release_to_cancel));
//						recordingHint.setBackgroundResource(R.drawable.recording_text_hint_bg);
                        iv_record_bg.setBackgroundResource(R.drawable.record_cancel);
                    } else {
                        micImage.setVisibility(View.VISIBLE);
                        recordingHint.setText(getString(R.string.move_up_to_cancel));
                        iv_record_bg.setBackgroundResource(R.drawable.recording_bg);
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    recordingContainer.setVisibility(View.INVISIBLE);
                    if (wakeLock.isHeld())
                        wakeLock.release();
                    if (event.getY() < 0) {
                        // discard the recorded audio.
                        voiceRecorder.discardRecording();

                    } else {
                        // stop recording and send voice file
                        String st1 = getResources().getString(R.string.Recording_without_permission);
                        String st2 = getResources().getString(R.string.The_recording_time_is_too_short);
                        String st3 = getResources().getString(R.string.send_failure_please);
                        try {
                            if (voiceRecorder.totalTime >= voiceRecorder.MAX_DURATION) {
                                return true;
                            }
                            int length = voiceRecorder.stopRecoding();
                            if (length > 0) {
                                sendVoice(voiceRecorder.getVoiceFilePath(), voiceRecorder.getVoiceFileName(toChatUsername),
                                        Integer.toString(length), false);
                            } else if (length == EMError.INVALID_FILE) {
                                Toast.makeText(getApplicationContext(), st1, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), st2, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ChatActivity.this, st3, Toast.LENGTH_SHORT).show();
                        }

                    }
                    return true;
                default:
                    recordingContainer.setVisibility(View.INVISIBLE);
                    if (voiceRecorder != null)
                        voiceRecorder.discardRecording();
                    return false;
            }
        }
    }

    /**
     * 获取表情的gridview的子view
     *
     * @param i
     * @return
     */
    private View getGridChildView(int i, int max) {
        View view = View.inflate(this, R.layout.expression_gridview, null);
        ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
        List<String> list = new ArrayList<String>();
        if (max == i + 1) {
            List<String> list1 = reslist.subList(i * 20, reslist.size());
            list.addAll(list1);
        } else {
            List<String> list1 = reslist.subList(i * 20, (i + 1) * 20);
            list.addAll(list1);
        }
        list.add("delete_expression");
        final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this, 1, list);
        gv.setAdapter(expressionAdapter);
        gv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filename = expressionAdapter.getItem(position);
                try {
                    // 文字输入框可见时，才可输入表情
                    // 按住说话可见，不让输入表情
                    if (buttonSetModeKeyboard.getVisibility() != View.VISIBLE) {

                        if (filename != "delete_expression") { // 不是删除键，显示表情
                            // 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
                            Class clz = Class.forName("com.tuxing.app.easemob.util.SmileUtils");
                            Field field = clz.getField(filename);
                            mEditTextContent.append(SmileUtils.getSmiledText(ChatActivity.this,
                                    (String) field.get(null)));
                        } else { // 删除文字或者表情
                            if (!TextUtils.isEmpty(mEditTextContent.getText())) {

                                int selectionStart = mEditTextContent.getSelectionStart();// 获取光标的位置
                                if (selectionStart > 0) {
                                    String body = mEditTextContent.getText().toString();
                                    EMLog.v("chat", toUNICODE(body));
                                    String tempStr = body.substring(0, selectionStart);
                                    //modified by wangst
                                    if (tempStr.length() >= 2) {
                                        CharSequence cs = tempStr.substring(selectionStart - 2, selectionStart);
                                        if (SmileUtils.containsKey(cs.toString()))
                                            mEditTextContent.getEditableText().delete(selectionStart - 2, selectionStart);
                                        else
                                            mEditTextContent.getEditableText().delete(selectionStart - 1,
                                                    selectionStart);
                                    } else {
                                        mEditTextContent.getEditableText().delete(selectionStart - 1, selectionStart);
                                    }

                                    /**
                                     * 环信删除表情方法
                                     */
//									int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
//									if (i != -1) {
//										CharSequence cs = tempStr.substring(i, selectionStart);
//										if (SmileUtils.containsKey(cs.toString()))
//											mEditTextContent.getEditableText().delete(i, selectionStart);
//										else
//											mEditTextContent.getEditableText().delete(selectionStart - 1,
//													selectionStart);
//									} else {
//										mEditTextContent.getEditableText().delete(selectionStart - 1, selectionStart);
//									}
                                }
                            }

                        }
                    }
                } catch (Exception e) {
                }

            }
        });
        return view;
    }

    public static String toUNICODE(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {

            if (s.charAt(i) < 256)//ASC11表中的字符码值不够4位,补00
            {
                sb.append("\\u00");
            } else {
                sb.append("\\u");
            }
            // System.out.println(Integer.toHexString(s.charAt(i)));
            sb.append(Integer.toHexString(s.charAt(i)));
        }
        return sb.toString();
    }

    private boolean isNotEmoji(String text) {
        boolean flag = false;
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(text);
        if (m.matches()) {
            flag = true;
        }
        p = Pattern.compile("[a-zA-Z]");
        m = p.matcher(text);
        if (m.matches()) {
            flag = true;
        }
        p = Pattern.compile("[\u4e00-\u9fa5]");
        m = p.matcher(text);
        if (m.matches()) {
            flag = true;
        }
        return flag;
    }

    public List<String> getExpressionRes(int getSum) {
        List<String> reslist = new ArrayList<String>();
        for (int x = 1; x <= getSum; x++) {
            String filename = "ee_" + x;

            reslist.add(filename);

        }
        return reslist;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
        if (groupListener != null) {
            EMGroupManager.getInstance().removeGroupChangeListener(groupListener);
        }
//		if(EventBus.getDefault()!=null&&EventBus.getDefault().isRegistered(this))
//			EventBus.getDefault().unregister(this);
        if (refreshReceicer != null) {
            unregisterReceiver(refreshReceicer);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceUtils.setPrefBoolean(mContext, SysConstants.ISCHATACTIVITY, true);
        if (adapter != null) {
            adapter.refresh();
        }
        isClassManager = getIntent().getBooleanExtra("isClassManager", false);
        TuxingHXSDKHelper sdkHelper = TuxingHXSDKHelper.getInstance();
        sdkHelper.pushActivity(this);
        // register the event listener when enter the foreground
        EMChatManager.getInstance().registerEventListener(
                this,
                new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage,
                        EMNotifierEvent.Event.EventOfflineMessage,
                        EMNotifierEvent.Event.EventDeliveryAck,
                        EMNotifierEvent.Event.EventReadAck});
        if (conversation != null) {
            conversation.markAllMessagesAsRead();//全部标为已读
        }
    }

    @Override
    protected void onStop() {
        // unregister this event listener when this activity enters the
        // background
        EMChatManager.getInstance().unregisterEventListener(this);

        TuxingHXSDKHelper sdkHelper = TuxingHXSDKHelper.getInstance();

        // 把此activity 从foreground activity 列表里移除
        sdkHelper.popActivity(this);

        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceUtils.setPrefBoolean(mContext, SysConstants.ISCHATACTIVITY, false);
        if (wakeLock.isHeld())
            wakeLock.release();
        if (VoicePlayClickListener.isPlaying && VoicePlayClickListener.currentPlayListener != null) {
            // 停止语音播放
            VoicePlayClickListener.currentPlayListener.stopPlayVoice();
        }

        try {
            // 停止录音
            if (voiceRecorder.isRecording()) {
                voiceRecorder.discardRecording();
                recordingContainer.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 加入到黑名单
     *
     * @param username
     */
    private void addUserToBlacklist(final String username) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.Is_moved_into_blacklist));
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMContactManager.getInstance().addUserToBlackList(username, false);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), R.string.Move_into_blacklist_success, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (EaseMobException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), R.string.Move_into_blacklist_failure, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 返回
     *
     * @param view
     */
    public void back(View view) {
        //隐藏输入框

        EMChatManager.getInstance().unregisterEventListener(this);
        finish();
    }

    /**
     * 覆盖手机返回键
     */
    @Override
    public void onBackPressed() {
        if (more.getVisibility() == View.VISIBLE) {
            more.setVisibility(View.GONE);
            iv_emoticons_normal.setVisibility(View.VISIBLE);
            iv_emoticons_checked.setVisibility(View.INVISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * listview滑动监听listener
     */
    private class ListScrollListener implements OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case OnScrollListener.SCROLL_STATE_IDLE:
                    if (view.getFirstVisiblePosition() == 0 && !isloading && haveMoreData && conversation.getAllMessages().size() != 0) {
                        isloading = true;
                        loadmorePB.setVisibility(View.VISIBLE);
                        // sdk初始化加载的聊天记录为20条，到顶时去db里获取更多
                        List<EMMessage> messages;
                        EMMessage firstMsg = conversation.getAllMessages().get(0);
                        try {
                            // 获取更多messges，调用此方法的时候从db获取的messages
                            // sdk会自动存入到此conversation中
                            if (chatType == CHATTYPE_SINGLE)
                                messages = conversation.loadMoreMsgFromDB(firstMsg.getMsgId(), pagesize);
                            else
                                messages = conversation.loadMoreGroupMsgFromDB(firstMsg.getMsgId(), pagesize);
                        } catch (Exception e1) {
                            loadmorePB.setVisibility(View.GONE);
                            return;
                        }
                        if (messages.size() != 0) {
                            // 刷新ui
                            if (messages.size() > 0) {
                                adapter.refreshSeekTo(messages.size() - 1);
                            }

                            if (messages.size() != pagesize)
                                haveMoreData = false;
                        } else {
                            haveMoreData = false;
                        }
                        loadmorePB.setVisibility(View.GONE);
                        isloading = false;

                    }
                    break;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        // 点击notification bar进入聊天页面，保证只有一个聊天页面
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }

    /**
     * 监测群组解散或者被T事件
     */
    class GroupListener implements EMGroupChangeListener {

        @Override
        public void onUserRemoved(final String groupId, String groupName) {
            runOnUiThread(new Runnable() {
                String st13 = getResources().getString(R.string.you_are_group);

                public void run() {
                    if (toChatUsername.equals(groupId)) {
                        Toast.makeText(ChatActivity.this, st13, Toast.LENGTH_SHORT).show();
//						if (GroupDetailsActivity.instance != null)
//							GroupDetailsActivity.instance.finish();
//						finish();
                    }
                }
            });
        }

        @Override
        public void onGroupDestroy(final String groupId, String groupName) {
            // 群组解散正好在此页面，提示群组被解散，并finish此页面
            runOnUiThread(new Runnable() {
                String st14 = getResources().getString(R.string.the_current_group);

                public void run() {
                    if (toChatUsername.equals(groupId)) {
                        Toast.makeText(ChatActivity.this, st14, Toast.LENGTH_SHORT).show();
//						if (GroupDetailsActivity.instance != null)
//							GroupDetailsActivity.instance.finish();
//						finish();
                    }
                }
            });
        }

        @Override
        public void onInvitationReceived(String groupId, String groupName,
                                         String inviter, String reason) {
        }

        @Override
        public void onApplicationReceived(String groupId, String groupName,
                                          String applyer, String reason) {
        }

        @Override
        public void onApplicationAccept(String groupId, String groupName,
                                        String accepter) {

        }

        @Override
        public void onApplicationDeclined(String groupId, String groupName,
                                          String decliner, String reason) {
        }

        @Override
        public void onInvitationAccpted(String groupId, String inviter,
                                        String reason) {
        }

        @Override
        public void onInvitationDeclined(String groupId, String invitee,
                                         String reason) {
        }

    }

    public String getToChatUsername() {
        return toChatUsername;
    }

    public ListView getListView() {
        return listView;
    }

    // ** 指引页面改监听器 */
    class GuidePageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            int count = page_select.getChildCount();
            for (int i = 0; i < count; i++) {
                ImageView view = (ImageView) page_select.getChildAt(i);
                if (i == arg0)
                    view.setImageResource(R.drawable.page_focused);
                else {
                    view.setImageResource(R.drawable.page_unfocused);
                }
            }
        }
    }

    public void onEventMainThread(DepartmentEvent event) {
        switch (event.getEvent()) {
            case DEPARTMENT_REQUEST_BY_GROUP_ID_SUCCESS://
                if (event.getDepartment() != null) {
                    currentDepartment = event.getDepartment();
                    tv_name.setText(event.getDepartment().getName());
                }
                break;
            case DEPARTMENT_REQUEST_BY_GROUP_ID_FAILED:
                break;
            default:
                break;
        }
    }

    @Override
    public void finish() {
//        startActivity(new Intent(mContext, MainActivity.class));
        super.finish();
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
        }
        return false;
    }

    /**
     * 如果被禁言
     *
     * @return
     */
    private boolean checkChatMute() {
        muteGroupIds = getService().getUserManager().getUserProfile(Constants.SETTING_FIELD.MUTE, "");
        if (!"".equals(muteGroupIds) && chatType != CHATTYPE_SINGLE && muteGroupIds.contains(toChatUsername)) {//禁言
            showToast(getResources().getString(R.string.hint_mute_text));
            return true;
        }
        return false;
    }

    public void sendRevokeMsgTouChuan(final EMMessage revokeMsg) {
        if (!NetworkUtils.isNetWorkAvailable(mContext)) {
            showToast(getResources().getString(R.string.network_unavailable));
        } else {
            EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
            // 如果是群聊，设置chattype,默认是单聊
            if (chatType == CHATTYPE_GROUP) {
                cmdMsg.setChatType(ChatType.GroupChat);
                cmdMsg.setAttribute("is_group", true);
            } else {
                cmdMsg.setChatType(ChatType.Chat);
                cmdMsg.setAttribute("is_group", false);
            }
            final String action = SysConstants.TOUCHUAN_REVOKEMSG;
            CmdMessageBody cmdBody = new CmdMessageBody(action);
            cmdMsg.setReceipt(toChatUsername);

            //撤回
            cmdMsg.setAttribute("msg_id", revokeMsg.getMsgId());
            cmdMsg.setAttribute("to_user_id", toChatUsername);
            cmdMsg.setAttribute("from_user_id", "" + user.getUserId());
            cmdMsg.addBody(cmdBody);
            EMChatManager.getInstance().sendMessage(cmdMsg, new EMCallBack() {
                @Override
                public void onSuccess() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast("操作成功");
                        }
                    });
                    deletedMsg(revokeMsg, chatType == CHATTYPE_GROUP,
                            revokeMsg.getFrom().equals(String.valueOf(user.getUserId())));
                    refreshUIWithNewMessage();
                    MyLog.getLogger(Utils.class.getSimpleName()).d("发送透传成功 action = " + action);
                }

                @Override
                public void onProgress(int arg0, String arg1) {
                }

                @Override
                public void onError(int arg0, String arg1) {
                    MyLog.getLogger(Utils.class.getSimpleName()).d("发送透传失败  action = " + action);
                }
            });
        }
    }

    /**
     * 删除消息
     *
     * @param message
     * @param isGrupChat
     * @param isSend
     */
    public void deletedMsg(EMMessage message, boolean isGrupChat, boolean isSend) {
        //删除当前会话的某条聊天记录
        String userName = "";
        if (isGrupChat || isSend) {
            userName = message.getTo();
        } else
            userName = message.getFrom();
        EMConversation conversation = EMChatManager.getInstance().getConversation(userName);
        EMMessage delMsg = conversation.getMessage(message.getMsgId());
        if (null != delMsg) {
            conversation.removeMessage(delMsg.getMsgId());
        }
    }

    public class RefreshRecivier extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TuxingApp.packageName + SysConstants.REFRESHMSGLIST)) {
                refreshUI();
            }
        }
    }
}
