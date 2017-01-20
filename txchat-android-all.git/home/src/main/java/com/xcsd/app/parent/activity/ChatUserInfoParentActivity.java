package com.xcsd.app.parent.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tuxing.app.TuxingApp;
import com.tuxing.app.activity.NewPicActivity;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.easemob.ui.ChatActivity;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.UmengData;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.event.UserEvent;
import com.tuxing.sdk.utils.Constants;
import com.tuxing.sdk.utils.Constants.USER_TYPE;
import com.umeng.analytics.MobclickAgent;
import com.xcsd.app.parent.R;


public class ChatUserInfoParentActivity extends BaseActivity {

    private RoundImageView userIcon;
    private TextView userSex;
    private TextView userClass;
    private TextView userSchool;
    private TextView class_tv;
    private TextView school_tv;
    private long userId;
    private String TAG = ChatUserInfoParentActivity.class.getSimpleName();
    private boolean isClassManager;
    private LinearLayout ll_bb_sex;
    private RelativeLayout sendButton;
    private boolean isMine = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.chat_userinfo_parent_layout);
        init();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void init() {
        setLeftBack("", true, false);
        setRightNext(false, "", 0);
        userId = getIntent().getLongExtra("userId", 0);
        showProgressDialog(mContext, "", true, null);
        isClassManager = getIntent().getBooleanExtra("isClassManager", false);
        sendButton = (RelativeLayout) findViewById(R.id.bottom_parent_send_msg);
        sendButton.setOnClickListener(this);
        userIcon = (RoundImageView) findViewById(R.id.userinfo_parent_icon);
        userSex = (TextView) findViewById(R.id.userinfo_parent_sex);
        userClass = (TextView) findViewById(R.id.userinfo_parent_class);
        userSchool = (TextView) findViewById(R.id.userinfo_parent_school);
        class_tv = (TextView) findViewById(R.id.userinfo_class_tv);
        school_tv = (TextView) findViewById(R.id.userinfo_school_tv);
        ll_bb_sex = (LinearLayout) findViewById(R.id.ll_bb_sex);
//        bottom_parent_send_msg = (LinearLayout) findViewById(R.id.bottom_parent_send_msg);
        if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())){
            sendButton.setBackground(getResources().getDrawable(R.drawable.straight_corner_p));
        }



        if (user != null && userId == user.getUserId()) {
            isMine = true;
        } else {
            isMine = false;
        }
        getService().getUserManager().requestUserInfoFromServer(userId);
    }

    private void initData(final User user) {
        if (user.getActivated() != null && user.getActivated() && !isMine) {
            sendButton.setVisibility(View.VISIBLE);
        } else {
            sendButton.setVisibility(View.GONE);
        }

        setTitle(user.getNickname());
        userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(user.getAvatar())) {
                    NewPicActivity.invoke(ChatUserInfoParentActivity.this,
                            user.getAvatar(), true);
                }
            }
        });
        if (user.getType() == USER_TYPE.TEACHER) {
            ll_bb_sex.setVisibility(View.GONE);
            class_tv.setText("职位");
            school_tv.setText("学校");
            userIcon.setImageUrl(user.getAvatar() + SysConstants.Imgurlsuffix80, R.drawable.default_info_image);
            userSchool.setText(user.getGardenName());
            userClass.setText(user.getPositionName());
            if (user.getGender() != null) {
                if (user.getGender() == 1)
                    userSex.setText("女");
                else
                    userSex.setText("男");
            }
        } else {
            ll_bb_sex.setVisibility(View.VISIBLE);
            class_tv.setText("班级");
            school_tv.setText("学校");
            userIcon.setImageUrl(user.getAvatar() + SysConstants.Imgurlsuffix80, R.drawable.default_avatar);
            User childUser = getService().getUserManager().getUserInfo(user.getChildUserId());
            if (childUser.getGender() != null) {
                if (childUser.getGender() == 1)
                    userSex.setText("女");
                else if (childUser.getGender() == 2)
                    userSex.setText("男");
                else
                    userSex.setText("未知");
            } else
                userSex.setText("未知");

            userClass.setText(user.getClassName());
            userSchool.setText(user.getGardenName());
        }
    }

    /**
     * 服务器返回
     *
     * @param event
     */
    public void onEventMainThread(UserEvent event) {
        if (isActivity) {
            disProgressDialog();
            switch (event.getEvent()) {
                case REQUEST_USER_SUCCESS:
                    user = event.getUser();
                    if (user != null) {
                        showAndSaveLog(TAG, "获取网络家长信息成功  --id" + user.getUserId(), false);
                        initData(user);
                    }
                    break;
                case REQUEST_USER_FAILED:
                    showToast(event.getMsg());
                    showAndSaveLog(TAG, "获取家长信息失败   --" + event.getMsg(), false);
                    break;
                case USER_NOT_FOUND:
                    showDialog("", event.getMsg(), "", getString(R.string.btn_ok));
                    break;
            }
        }
    }

    @Override
    public void onConfirm() {
        super.onConfirm();
        finish();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.bottom_parent_send_msg:
                // TODO 发送消息 跳转到聊天界面
                if (null != user) {
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.putExtra("userId", user.getUserId());
                    intent.putExtra("userName", user.getNickname());
                    intent.putExtra("isClassManager", isClassManager);
                    startActivity(intent);
                    MobclickAgent.onEvent(mContext, "my_info_sendmsg", UmengData.my_info_sendmsg);
                }
                break;
        }
    }
}
