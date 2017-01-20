package com.xcsd.app.parent.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.tuxing.app.TuxingApp;
import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.parent.R;
import com.xcsd.app.parent.fragment.StudyHomeWorkCheckedFragment;
import com.xcsd.app.parent.fragment.StudyHomeworkRankFragment;
import com.xcsd.app.parent.util.SysUtil;
import com.tuxing.app.ui.dialog.DialogHelper;
import com.tuxing.app.ui.dialog.PopupWindowDialog;
import com.tuxing.app.util.PhoneUtils;
import com.tuxing.sdk.db.entity.HomeWorkRecord;
import com.tuxing.sdk.db.entity.HomeWorkUserRank;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.event.HomeworkEvent;
import com.tuxing.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shan on 2016/4/9.
 */
public class StudyRecordActivity extends BaseActivity implements View.OnClickListener {
    private RadioGroup rgHomeRecord;
    private RadioButton rbRank, rbCheck;
    private RadioButton[] arrRadioButton;
    private FragmentManager fragmentManager = null;
    private List<Fragment> list = new ArrayList<>();
    private int currentTabIndex = 0;

    private List<HomeWorkRecord> popupWinList;
    //private List<HomeworkProto.Homework> popupWinList;
    private TextView tv_Back;
    private LinearLayout ll_StudyHomePopu;
    private List<User> userList;
    int selectedPopup = 0;
    private List<String> departs;
    private TextView tvHomeTitle;
    private Fragment currentFragment;
    private Fragment toFragment;
    List<HomeWorkUserRank> userRankList;
    List<Integer> userFishList;
    List<Integer> userUnFinishList;
    StudyHomeworkRankFragment studyHomeworkRankFragment;
    StudyHomeWorkCheckedFragment studyHomeWorkCheckedFragment;
    private boolean isFirst = true;
    private int index;
    private int currentIndex;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_studyrecord_layout);
        fragmentManager = getSupportFragmentManager();

        initView();

        initPopupList();


    }

    private void showFragment() {
        studyHomeworkRankFragment = new StudyHomeworkRankFragment();
        studyHomeWorkCheckedFragment = new StudyHomeWorkCheckedFragment();
        list.add(studyHomeworkRankFragment);
        list.add(studyHomeWorkCheckedFragment);
        if (isFirst) {
            fragmentManager.beginTransaction().add(R.id.home_layout_container, studyHomeworkRankFragment).commit();
            isFirst = false;
        }
        //switchFragment(0);
        if (rbRank.isChecked()) {
            rbRank.setTextColor(Color.WHITE);
            rbCheck.setTextColor(getResources().getColor(R.color.rb_frg_blue_bg));

            rgHomeRecord.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {


                    if (rbCheck.getId() == checkedId) {
                        rbRank.setTextColor(getResources().getColor(R.color.rb_frg_blue_bg));
                        rbCheck.setTextColor(Color.WHITE);

                        switchFragment(1);

                    } else {
                        rbRank.setTextColor(Color.WHITE);
                        rbCheck.setTextColor(getResources().getColor(R.color.rb_frg_blue_bg));

                        switchFragment(0);

                    }
                }
            });
        } else {
            rbRank.setTextColor(getResources().getColor(R.color.rb_frg_blue_bg));
            rbCheck.setTextColor(Color.WHITE);

            rgHomeRecord.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {


                    if (rbCheck.getId() == checkedId) {
                        rbRank.setTextColor(getResources().getColor(R.color.rb_frg_blue_bg));
                        rbCheck.setTextColor(Color.WHITE);

                        switchFragment(1);

                    } else {
                        rbRank.setTextColor(Color.WHITE);
                        rbCheck.setTextColor(getResources().getColor(R.color.rb_frg_blue_bg));

                        switchFragment(0);

                    }
                }
            });
        }

    }

    private void initPopupList() {
        userList = new ArrayList<User>();
        User userFirst = getService().getUserManager().getUserInfo(user.getChildUserId());
        SysUtil.setUserId(userFirst.getUserId());
        userList.add(userFirst);
    }


    private void switchFragment(int toTabindex) {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        currentFragment = list.get(currentTabIndex);
        // 即将希望现显示的碎片
        toFragment = list.get(toTabindex);
        // 判断即将显示的碎片是否已经被添加过了。

        if (!toFragment.isAdded()) {
            fragmentTransaction.hide(currentFragment).add(R.id.home_layout_container,
                    toFragment).commit();
            // fragmentTransaction.hide(currentFragment).show(toFragment);
        } else {
            fragmentTransaction.hide(currentFragment).show(toFragment).commit();
        }

//        fragmentTransaction.commit();
        currentTabIndex = toTabindex;
    }

    private void initView() {
        ll_StudyHomePopu = (LinearLayout) findViewById(R.id.ll_home_study_popup);
        tvHomeTitle = (TextView) findViewById(R.id.tv_home_title);
        tv_Back = (TextView) findViewById(R.id.tv_left);
        rgHomeRecord = (RadioGroup) findViewById(R.id.rg_home_study_record);
        rbCheck = (RadioButton) rgHomeRecord.getChildAt(1);
        rbRank = (RadioButton) rgHomeRecord.getChildAt(0);
        arrRadioButton = new RadioButton[rgHomeRecord.getChildCount()];

        tv_Back.setOnClickListener(this);
        ll_StudyHomePopu.setOnClickListener(this);

        rbRank.setTextColor(getResources().getColor(R.color.rb_frg_blue_bg));
        rbCheck.setTextColor(Color.WHITE);


    }

    public void onEventMainThread(HomeworkEvent event) {
        userRankList = new ArrayList<>();
        userFishList = new ArrayList<>();
        userUnFinishList = new ArrayList<>();

        if (isActivity) {
            switch (event.getEvent()) {
                case HOMEWORK_RANKING_SUCCESS:
                    disProgressDialog();
                    userRankList = event.getHomeWorkUserRankList();

                    break;
                case HOMEWORK_CALENDAR_SUCCESS:
                    //可以调到数据
                    disProgressDialog();
                    userFishList = event.getFinishedList();
                    userUnFinishList = event.getUnfinishedList();


                    break;


            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        getPopWindow();
        showFragment();

    }

    public void getreFresh() {
        getService().getHomeWorkManager().HomeworkCalendarRequest(SysUtil.getUserId());

        getService().getHomeWorkManager().getHomeworkRankingFromParent(SysUtil.getUserId());

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ll_home_study_popup:

                break;
            case R.id.tv_left:
                finish();
                break;
        }
    }

    private void getPopWindow() {

        if ("parent".equals(TuxingApp.VersionType)) {//家长版没有全部
            if (!CollectionUtils.isEmpty(userList) && userList.size() >= 1) {//教师版
                User userPopWin = userList.get(selectedPopup);
                if (userPopWin != null) {
                    tvHomeTitle.setText(userPopWin.getNickname());
                }
//                arrow_show(false);
                ll_StudyHomePopu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<String> departs = new ArrayList<String>();
                        for (User userp : userList) {
                            departs.add(userp.getNickname());
                        }
//                        arrow_show(true);
                        //弹出窗口
//                        showContextMenu(departs.toArray(new CharSequence[departs.size()]));
                    }
                });
            } else {
                //  tv_title.setText(getResources().getString(com.tuxing.app.R.string.tab_circle));
            }
        } else {
            if (!CollectionUtils.isEmpty(userList) && userList.size() >= 1) {//教师版
                if (selectedPopup == 0) {
                    tvHomeTitle.setText("全部");
                } else {
                    User userPopup = userList.get(selectedPopup - 1);
                    if (userPopup != null) {
                        tvHomeTitle.setText(userPopup.getUsername());
                    }
                }
                arrow_show(false);
                ll_StudyHomePopu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<String> departs = new ArrayList<String>();
                        departs.add("全部");
                        for (User department : userList) {
                            departs.add(department.getNickname() );
                        }
                        arrow_show(true);
                        showContextMenu(departs.toArray(new CharSequence[departs.size()]));
                    }
                });
            } else {
                //  tv_title.setText(getResources().getString(com.tuxing.app.R.string.tab_circle));
            }
        }
    }

    private void arrow_show(boolean isUp) {
        Drawable drawable = null;
        if (isUp) {
            drawable = getResources().getDrawable(com.tuxing.app.R.drawable.ic_arrow_up);
        } else {
            drawable = getResources().getDrawable(com.tuxing.app.R.drawable.ic_arrow_down);
        }
        /// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth() + 5, drawable.getMinimumHeight());
        //  tv_title.setCompoundDrawables(null, null, null, drawable);
        tvHomeTitle.setCompoundDrawables(null, null, null, drawable);
    }


    public void showContextMenu(final CharSequence[] departs) {
        final PopupWindowDialog dialog = DialogHelper.getPopDialogCancelable(this);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                arrow_show(false);
            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                arrow_show(true);
            }
        });
        dialog.setItemsWithoutChk(departs,
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        dialog.dismiss();
                        tvHomeTitle.setText(departs[position]);
                        arrow_show(false);
                        if (position == 0) {
                            showProgressDialog(mContext, "", true, null);
                            if ("parent".equals(TuxingApp.VersionType)) {
                                User department = userList.get(selectedPopup);
                                if (department != null) {
                                    SysUtil.setUserId(department.getUserId());


                                    getService().getHomeWorkManager().HomeworkCalendarRequest(department.getUserId());

                                    getService().getHomeWorkManager().getHomeworkRankingFromParent(department.getUserId());

                                    // showFragment();
                                }
                            } else {
                                // getService().getFeedManager().getLatestFeed(null);
                            }
                        } else {
                            User department = userList.get(position - 1);
                            if (department != null) {
                                showProgressDialog(mContext, "", true, null);
                                // getService().getFeedManager().getLatestFeed(department.getDepartmentId());
                            }
                        }
                        dialog.setSelectIndex(selectedPopup);
                        selectedPopup = position;
                    }
                }, selectedPopup);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        lp.y = PhoneUtils.dip2px(this, 48); // 新位置Y坐标
        lp.width = PhoneUtils.dip2px(this, 200); // 宽度
        if (departs.length < 3) {
            lp.height = PhoneUtils.dip2px(mContext, 85);
            ; // 动态高度
        } else if (departs.length >= 5) {
            lp.height = PhoneUtils.dip2px(mContext, 250);
            ; // 动态高度
        } else {
            lp.height = PhoneUtils.dip2px(mContext, departs.length * 48) - PhoneUtils.dip2px(mContext, 10);
            ; // 动态高度
        }

        // 当Window的Attributes改变时系统会调用此函数,可以直接调用以应用上面对窗口参数的更改,也可以用setAttributes
        // dialog.onWindowAttributesChanged(lp);
        dialogWindow.setAttributes(lp);
        dialog.show();
    }
}
