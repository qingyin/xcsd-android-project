package com.xcsd.app.teacher.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.activity.GameWebSubUrlActivity;
import com.tuxing.app.activity.WebSubDataActivity;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.util.SysConstants;
import com.xcsd.app.teacher.adapter.SetClassWorkAdapter;
import com.xcsd.app.teacher.adapter.TrainAdapter;
import com.xcsd.app.teacher.adapter.WorkSocreAdapter;
import com.xcsd.app.teacher.view.ContactParentPopupwindow;
import com.xcsd.app.teacher.R;
import com.tuxing.app.ui.dialog.DialogHelper;
import com.tuxing.app.ui.dialog.PopupWindowDialog;
import com.tuxing.app.util.PhoneUtils;
import com.tuxing.sdk.db.entity.ClassHomeworkInfo;
import com.tuxing.sdk.db.entity.ContentItem;
import com.tuxing.sdk.db.entity.Department;
import com.tuxing.sdk.db.entity.LoginUser;
import com.tuxing.sdk.db.helper.GlobalDbHelper;
import com.tuxing.sdk.event.ContentItemEvent;
import com.tuxing.sdk.event.HomeworkEvent;
import com.tuxing.sdk.event.SyncContactEvent;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import me.maxwin.view.XListView;

public class SetClassWorkActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout btnTitleLeft;
    private RelativeLayout rl_done;
    private RelativeLayout rl_set;
    private TextView tvTitle;
    private TextView item_tips_down;
    private TextView item_tips_down_name;
    private ImageView item_arrow;
    private ImageView item_arrows;
    private ContactParentPopupwindow Popupwindow;
    private RelativeLayout activity_bg;
    private boolean hasMore = false;
    ArrayList<String> list;

    private List<Department> info = null;
    Department test;
    private List<Department> departmentList;
    private List<Department> departmentList1;
    private List<Department> departmentListClass;
    int selectedPopup = 0;
    private Long classid = 0L;
    private int class_id;

    public static SetClassWorkActivity instance = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_class_work);
        instance = this;
        init();
        getData();
    }

    private void init() {

        TAG = SetClassWorkActivity.class.getSimpleName();
        btnTitleLeft = (LinearLayout) findViewById(R.id.btnTitleLeft);
        tvTitle = (TextView) findViewById(R.id.tvTitle);

        item_tips_down_name = (TextView) findViewById(R.id.item_tips_down_name);
        item_tips_down = (TextView) findViewById(R.id.item_tips_down);
        rl_done = (RelativeLayout) findViewById(R.id.rl_done);
        rl_set = (RelativeLayout) findViewById(R.id.rl_set);
        item_arrow = (ImageView) findViewById(R.id.item_arrow);
        item_arrows = (ImageView) findViewById(R.id.item_arrows);

        rl_set.setOnClickListener(this);
        rl_done.setOnClickListener(this);
        rl_set.setClickable(true);
        rl_done.setClickable(true);

        activity_bg = (RelativeLayout) findViewById(R.id.activity_bg);
        btnTitleLeft.setOnClickListener(this);

        departmentList1 = getService().getContactManager().getAllDepartment();
        departmentListClass = new ArrayList<Department>();
////      初始化数据,区分班级
        for (int i = 0; i < departmentList1.size(); i++) {
            Department department1 = new Department();
            department1 = departmentList1.get(i);
            if (department1.getType() == 2) {
                departmentListClass.add(department1);
            }
        }

        if(departmentListClass.size() > 0){
            Department department_NAME = departmentListClass.get(0);
            tvTitle.setText(department_NAME.getName());
            classid = department_NAME.getDepartmentId();
        }
        departmentList = departmentListClass;
    }

    @Override
    protected void onResume() {
        isActivity = true;
        super.onResume();
        getPopWindow();
    }

    private void initData() {
        // TODO 获取服务器数据
        getService().getHomeWorkManager().HomeworkRemainingCountRequest(classid);
        showProgressDialog(this, "", true, null);
    }


    public void onEventMainThread(HomeworkEvent event) {
        if (isActivity) {
            disProgressDialog();
            switch (event.getEvent()) {
                case HOMEWORK_REMAINING_COUNT_SUCCESS:
                    if (!event.isCustomizedStatus()) {
                        rl_set.setClickable(false);
                        item_tips_down_name.setVisibility(View.VISIBLE);
                        item_arrow.setVisibility(View.GONE);
                        rl_set.setBackgroundColor(getResources().getColor(R.color.gray_normal));

                    }else{
                        rl_set.setClickable(true);
                        item_tips_down_name.setVisibility(View.GONE);
                        item_arrow.setVisibility(View.VISIBLE);
                        rl_set.setBackgroundColor(getResources().getColor(R.color.white));
                    }
                    if (event.getUnifiedCount() == 0) {
                        rl_done.setClickable(false);
                        item_tips_down.setVisibility(View.VISIBLE);
                        item_arrows.setVisibility(View.GONE);
                        rl_done.setBackgroundColor(getResources().getColor(R.color.gray_normal));
                    }else{
                        SysConstants.GAMEmax=event.getUnifiedCount();
                        rl_done.setClickable(true);
                        item_tips_down.setVisibility(View.GONE);
                        item_arrows.setVisibility(View.VISIBLE);
                        rl_done.setBackgroundColor(getResources().getColor(R.color.white));
                    }
                    break;
                case HOMEWORK_REMAINING_COUNT_FAILED:
                    showToast(event.getMsg());

                    break;
            }
        }
    }



    @Override
    public void getData() {
//        if (user != null)
//            classid = user.getClassId();
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTitleLeft:
//            点击退出界面
                this.finish();
                break;
            case R.id.rl_set:
//
                Intent intent = new Intent(this, SetWorkActivity.class).putExtra("classid", classid);
                startActivity(intent);
                break;
            case R.id.rl_done:

                String token = null;
                LoginUser loginUser = GlobalDbHelper.getInstance().getLoginUser();
                if (loginUser != null) {
                    token = loginUser.getToken();

                }

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(SysConstants.H5_GAME_HOST_URL);
                stringBuilder.append("?");
                stringBuilder.append("action=10002&");
//                stringBuilder.append("memberId=" + classid + "&");
                stringBuilder.append("memberId=" + classid + "&");
                stringBuilder.append("token=" + token + "&");
                stringBuilder.append("isTaskState=" + "2");
//                showAndSaveLog(TAG, "TOKEN数据=======================+" + stringBuilder.toString(), false);
//                ShowWebSubUrlActivity.invoke(mContext, stringBuilder.toString(),
//                        getString(com.tuxing.app.R.string.activity));
                Intent it =  new Intent(this,SetUniteWorkActivity.class).putExtra("classid", classid);
                startActivity(it);

//
                break;
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
        tvTitle.setCompoundDrawables(null, null, drawable, null);

    }

    private void getPopWindow() {
        arrow_show(false);
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> departs = new ArrayList<String>();
                for (Department department : departmentList) {
                    departs.add(department.getName());
                }
                if (departmentList.size()>1){
                    arrow_show(true);
                    showContextMenu(departs.toArray(new CharSequence[departs.size()]));
                }else{
                    showToast("没有能选择的班级");
                }
            }
        });
    }

    public void showContextMenu(final CharSequence[] departs) {
        final PopupWindowDialog dialog = DialogHelper.getPopDialogCancelable(this);
        dialog.setItemsWithoutChk(departs,
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        dialog.dismiss();
                        tvTitle.setText(departs[position]);
                        arrow_show(false);
                        if (!CollectionUtils.isEmpty(departmentList)) {
                            Department department = departmentList.get(position);
                            if (department != null) {
//                                showToast(department.getName());
                                classid = department.getDepartmentId();
                                initData();
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
            lp.height = PhoneUtils.dip2px(mContext, 208);
            ; // 动态高度
        } else {
            lp.height = PhoneUtils.dip2px(mContext, departs.length * 45) - PhoneUtils.dip2px(mContext, 8);
            ; // 动态高度
        }

        // 当Window的Attributes改变时系统会调用此函数,可以直接调用以应用上面对窗口参数的更改,也可以用setAttributes
        // dialog.onWindowAttributesChanged(lp);
        dialogWindow.setAttributes(lp);
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                arrow_show(false);
            }
        });
    }


}
