package com.xcsd.app.teacher.activity;

import android.content.Intent;
import android.net.Uri;
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
import com.tuxing.sdk.utils.Constants;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.UmengData;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.event.UserEvent;
import com.tuxing.sdk.modle.Relative;
import com.tuxing.sdk.utils.Constants.USER_TYPE;
import com.umeng.analytics.MobclickAgent;


public class ChatUserInfoParentActivity extends BaseActivity {

    private RoundImageView userIcon;
    private TextView userSex;
    private TextView userPhone;
    private TextView userSchool;
    private TextView userClass;
    private TextView class_name;
    private TextView six_name;
    private long userId;
    private String TAG = ChatUserInfoParentActivity.class.getSimpleName();
    private boolean isClassManager;
    private LinearLayout bottom_parent_send_msg;
    private TextView tv_call;
    private TextView tv_send_msg;
    private LinearLayout ll_bb_sex;
    private View line;
    private LinearLayout ll_msg;
    private boolean isMine = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.chat_userinfo_parent_layout);
        init();
    }

    private void init() {
        setLeftBack("", true, false);
        setRightNext(false, "", 0);
        userId = getIntent().getLongExtra("userId", 0);
        showProgressDialog(mContext, "", true, null);
        isClassManager = getIntent().getBooleanExtra("isClassManager", false);
        tv_call = (TextView) findViewById(R.id.tv_call);
        tv_call.setOnClickListener(this);
        tv_send_msg = (TextView) findViewById(R.id.tv_send_msg);
        tv_send_msg.setOnClickListener(this);
        userIcon = (RoundImageView) findViewById(R.id.userinfo_parent_icon);
        userSex = (TextView) findViewById(R.id.userinfo_sex);
        userClass = (TextView) findViewById(R.id.userinfo_class);
        userPhone = (TextView) findViewById(R.id.userinfo_phone);
        userSchool = (TextView) findViewById(R.id.userinfo_school);
        class_name = (TextView) findViewById(R.id.class_name);
        six_name = (TextView) findViewById(R.id.six_name);
        ll_bb_sex = (LinearLayout)findViewById(R.id.ll_bb_sex);
        ll_msg = (LinearLayout)findViewById(R.id.ll_msg);
        line = findViewById(R.id.line);
        bottom_parent_send_msg = (LinearLayout) findViewById(R.id.bottom_parent_send_msg);
        if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())){
            bottom_parent_send_msg.setBackground(getResources().getDrawable(R.drawable.straight_corner_p));
        }
        if (user != null && userId == user.getUserId()) {
            isMine = true;
        } else {
           isMine = false;
        }
        getService().getUserManager().requestUserInfoFromServer(userId);
    }

    private void initData(final User user) {
        if(!isMine){
            if(user.getActivated() != null && user.getActivated()){
                bottom_parent_send_msg.setVisibility(View.VISIBLE);
                ll_msg.setVisibility(View.VISIBLE);
                line.setVisibility(View.VISIBLE);
            }else{
                ll_msg.setVisibility(View.GONE);
                line.setVisibility(View.GONE);
        }
        }else{
            bottom_parent_send_msg.setVisibility(View.GONE);
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
            class_name.setText("职位");
            six_name.setText("性别");
            userIcon.setImageUrl(user.getAvatar() + SysConstants.Imgurlsuffix80, R.drawable.default_info_image);
            userSchool.setText(user.getGardenName());
            userPhone.setText(user.getMobile());
            userClass.setText(user.getPositionName());
            if (user.getGender() != null) {
                if (user.getGender() == 1)
                    userSex.setText("女");
                else
                    userSex.setText("男");
            }

        } else {
            ll_bb_sex.setVisibility(View.VISIBLE);
            class_name.setText("班级");
            six_name.setText("孩子性别");
            userIcon.setImageUrl(user.getAvatar() + SysConstants.Imgurlsuffix80, R.drawable.default_avatar);
            userPhone.setText(user.getMobile());
            userSchool.setText(user.getGardenName());
            userClass.setText(user.getClassName());
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
                    showAndSaveLog(TAG,"获取家长信息失败   --" + event.getMsg(),false);
                    break;

            }
        }
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_call:
                if(!TextUtils.isEmpty(user.getMobile())){
                    Intent intentCall = new Intent("android.intent.action.CALL", Uri.parse("tel:" + user.getMobile()));
                    startActivity(intentCall);
                }else{
                	showToast(getString(R.string.phone_msg));
                }
                break;
            case R.id.tv_send_msg:
                // TODO 发送消息 跳转到聊天界面
                if(null!=user){
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.putExtra("userId", user.getUserId());
                    intent.putExtra("userName", user.getNickname());
                    intent.putExtra("isClassManager", isClassManager);
                    startActivity(intent);
                    MobclickAgent.onEvent(mContext, "my_info_sendmsg", UmengData.my_info_sendmsg);
                }
        }
    }
}
