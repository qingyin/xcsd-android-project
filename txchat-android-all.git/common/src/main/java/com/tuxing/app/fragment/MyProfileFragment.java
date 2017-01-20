package com.tuxing.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.activity.*;
import com.tuxing.app.base.BaseFragment;
import com.tuxing.app.easemob.ui.ChatActivity;
import com.tuxing.app.qzq.util.ParentNoNullUtil;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.UmengData;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.db.helper.UserDbHelper;
import com.tuxing.sdk.facade.CoreService;
import com.tuxing.sdk.utils.Constants;
import com.umeng.analytics.MobclickAgent;

public class MyProfileFragment extends BaseFragment implements OnClickListener {

    private TextView my_user_name;
    private TextView my_user_account;
    private RoundImageView icon;
    private View insuranceView;
    private ImageView ivArrowSetting;
    private ImageView ivInvatation_man;
    private ImageView ivMy_baby_wuyou_icon;
    private ImageView ivMy_favorite_icon;
    private ImageView ivMy_account_asfety_icon;
    private ImageView ivMy_help_icon;
    private ImageView ivMy_set_icon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setRightNext(false, "", 0);
        View view = inflater.inflate(R.layout.fragment_myprofile, null);
        ivArrowSetting = (ImageView) view.findViewById(R.id.ivArrowSetting);
        ivInvatation_man = (ImageView) view.findViewById(R.id.iv_invatation_man);
        ivMy_baby_wuyou_icon = (ImageView) view.findViewById(R.id.iv_my_baby_wuyou_icon);
        ivMy_favorite_icon = (ImageView) view.findViewById(R.id.iv_my_favorite_icon);
        ivMy_account_asfety_icon = (ImageView) view.findViewById(R.id.iv_my_account_asfety_icon);
        ivMy_help_icon = (ImageView) view.findViewById(R.id.iv_my_help_icon);
        ivMy_set_icon = (ImageView) view.findViewById(R.id.iv_my_set_icon);
//		if(TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())){
//			ivArrowSetting.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right_p));
//			ivInvatation_man.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right_p));
//			ivMy_baby_wuyou_icon.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right_p));
//			ivMy_favorite_icon.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right_p));
//			ivMy_account_asfety_icon.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right_p));
//			ivMy_help_icon.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right_p));
//			ivMy_set_icon.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right_p));
//		}else if(TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())){
//			ivArrowSetting.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right_t));
//			ivInvatation_man.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right_t));
//			ivMy_baby_wuyou_icon.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right_t));
//			ivMy_favorite_icon.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right_t));
//			ivMy_account_asfety_icon.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right_t));
//			ivMy_help_icon.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right_t));
//			ivMy_set_icon.setImageDrawable(getResources().getDrawable(R.drawable.arrow_right_t));
//
//		}
//		view.findViewById(R.id.my_cloudcard_number).setOnClickListener(this);
        view.findViewById(R.id.my_rlUserInfo).setOnClickListener(this);
        view.findViewById(R.id.my_set).setOnClickListener(this);
        view.findViewById(R.id.my_help).setOnClickListener(this);
        view.findViewById(R.id.my_account_asfety).setOnClickListener(this);
        view.findViewById(R.id.my_baby_wuyou).setOnClickListener(this);
        if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {
            view.findViewById(R.id.my_invatation).setVisibility(View.GONE);
            view.findViewById(R.id.my_baby_wuyou).setVisibility(View.GONE);
            view.findViewById(R.id.iv_wipe).setVisibility(View.GONE);
            view.findViewById(R.id.ll_my_parent).setVisibility(View.GONE);

        } else if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())) {
            view.findViewById(R.id.my_invatation).setVisibility(View.VISIBLE);
            view.findViewById(R.id.my_baby_wuyou).setVisibility(View.VISIBLE);
//			view.findViewById(R.id.iv_wipe).setVisibility(View.VISIBLE);
        }
        view.findViewById(R.id.my_invatation).setOnClickListener(this);
        insuranceView = view.findViewById(R.id.my_baby_wuyou);

        String strHomeMenu = getService().getUserManager().getUserProfile(
                Constants.SETTING_FIELD.HOME_MENU, SysConstants.DEFAULT_HOME_MENU_ITEMS);
        if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion()) && strHomeMenu.contains(SysConstants.MENU_ITEM_INSURANCE)) {
            insuranceView.setVisibility(View.VISIBLE);
        } else {
            insuranceView.setVisibility(View.GONE);
        }

        initView(view);
        ll_left_img.setVisibility(View.GONE);
        return view;
    }

    private void initView(View view) {
        icon = (RoundImageView) view.findViewById(R.id.my_user_head);
        my_user_name = (TextView) view.findViewById(R.id.my_user_name);
        my_user_account = (TextView) view.findViewById(R.id.my_user_account);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MobclickAgent.onEvent(getActivity(), "click_my", UmengData.click_my);
        setTitle(getResources().getString(R.string.tab_my));
    }

    @Override
    public void onclickRightNext() {
        super.onclickRightNext();
    }

    @Override
    public void getData() {
        if (getService() != null && getService().getLoginManager().getCurrentUser() != null) {
            currentUser = getService().getUserManager().getUserInfo(getService().getLoginManager().getCurrentUser().getUserId());
//            icon.setImageUrl(currentUser.getAvatar() + SysConstants.Imgurlsuffix80, R.drawable.default_avatar);
            icon.setImageUrl(currentUser.getAvatar(), R.drawable.default_avatar);
            User user = CoreService.getInstance().getLoginManager().getCurrentUser();
//			my_user_name.setText(currentUser.getNickname());
            my_user_name.setText(ParentNoNullUtil.getNickName(currentUser));
            my_user_account.setText("帐号 : " + currentUser.getMobile());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(getActivity());
        MobclickAgent.onPageStart("我的界面");
        getData();
    }

    @Override
    public void onClick(View v) {
//		if(v.getId() == R.id.my_cloudcard_number){
//			openActivity(TuxingApp.packageName+ SysConstants.CLOUDCARDACTION);
        if (v.getId() == R.id.my_rlUserInfo) {
            openActivity(TuxingApp.packageName + SysConstants.MYUSERINFOACTION);
        } else if (v.getId() == R.id.my_set) {
            Intent intent = new Intent(getActivity(), SettingActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.my_help) {
//            WebSubDataActivity.invoke(getActivity(), "http://121.41.101.14:8080/cms/article.do?feedback",
//                    getString(R.string.help_feedback));

//            WebSubDataActivity.invoke(getActivity(), SysConstants.KURL_FEEDBACK,
//                    getString(R.string.help_feedback));

//			Intent intent = new Intent(getActivity(),HelpAndFBActivity.class);
//			startActivity(intent);

//            Intent intent = new MQIntentBuilder(getActivity()).build();
//            startActivity(intent);
//            User users = getService().getUserManager().getUserInfo(10086l);
//            if (users == null) {
//                User user = new User();
//                user.setNickname("乐学堂客服");
//                user.setUserId(10086l);
////                user.setAvatar("");
//                UserDbHelper.getInstance().saveUser(user);
//            }

            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
            intent.putExtra("userId", 10086l);
            intent.putExtra("userName", "乐学堂客服");
            startActivity(intent);
        } else if (v.getId() == R.id.my_account_asfety) {
            Intent intent = new Intent(getActivity(), MyRoleActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.my_invatation) {
            Intent intent = new Intent(getActivity(), InviteFolksListActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.my_baby_wuyou) {
            WebSubDataActivity.invoke(getActivity(), true, SysConstants.wuyouorder, getResources().getString(R.string.baby_wuyou));
            MobclickAgent.onEvent(getActivity(), "my_baoxian", UmengData.my_baoxian);
        }

    }
}
