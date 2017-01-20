package com.xcsd.app.teacher.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tuxing.app.activity.ExplainActivity;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.sdk.event.DataReportEvent;
import com.xcsd.app.teacher.fragment.ScoreShowFragment;
import com.xcsd.app.teacher.fragment.WorkShowFragment;
import com.xcsd.app.teacher.view.ContactParentPopupwindow;
import com.xcsd.app.teacher.R;
import com.tuxing.app.ui.dialog.DialogHelper;
import com.tuxing.app.ui.dialog.PopupWindowDialog;
import com.tuxing.app.util.PhoneUtils;
import com.tuxing.sdk.db.entity.Department;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.Constants;
import com.xcsd.rpc.proto.EventType;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


public class WorkScoreShowActivity extends BaseActivity implements View.OnClickListener {
    private RadioButton button_work;
    private RadioButton button_score;
    private RadioGroup button_group;
    private LinearLayout btnTitleLeft;
    private TextView tvTitle;
    private TextView bt_right;
    private FrameLayout framelayout;
    private ScoreShowFragment scoreShowFragment;
    private WorkShowFragment workShowFragment;
    private ContactParentPopupwindow Popupwindow;
    private Fragment[] fragment_array;
    private RadioButton[] bt_array;
    private int index;
    private int currentindex;

    private List<Department> info = null;
    private List<Department> departmentList;
    private List<Department> departmentListClass;
    int selectedPopup = 0;
    int slectedtype = 0;
    long classid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_score_show);
        init();

        getService().getDataReportManager().reportEventBid(EventType.CHANNEL_IN, "achievement");
    }

    public void init() {
        button_work = (RadioButton) findViewById(R.id.button_work);
        button_score = (RadioButton) findViewById(R.id.button_score);
        button_group = (RadioGroup) findViewById(R.id.button_group);
        framelayout = (FrameLayout) findViewById(R.id.framelayout);
        btnTitleLeft = (LinearLayout) findViewById(R.id.btnTitleLeft);
        bt_right = (TextView) findViewById(R.id.bt_right);
//        bt_right.setVisibility(View.GONE);
        tvTitle = (TextView) findViewById(R.id.tvTitle);


        departmentList = this.getService().getContactManager().getAllDepartment();
        departmentListClass = new ArrayList<Department>();
        if (departmentList == null || departmentList.size() == 0) {
            showToast("班级数据获取失败");
        } else {
            //      初始化数据
            Department department = departmentList.get(0);
            tvTitle.setText(department.getName());
            classid = department.getDepartmentId();
        }

        ////      初始化数据,区分班级
        for (int i = 0; i < departmentList.size(); i++) {
            Department department1 = new Department();
            department1 = departmentList.get(i);
            if (department1.getType() == 2) {
                departmentListClass.add(department1);
            }
        }

        Department department_NAME = departmentListClass.get(0);
        tvTitle.setText(department_NAME.getName());
        classid = department_NAME.getDepartmentId();

        departmentList = departmentListClass;

//        Department test = new Department();
//        Department test2 = new Department();
//        Department test3 = new Department();
//        test.setName("三年二班");
//        test2.setName("四年三班");
//        test3.setName("一期五班");
//        info = new ArrayList<Department>();
//        info.add(test);
//        info.add(test2);
//        info.add(test3);
//        departmentList = info;

        scoreShowFragment = new ScoreShowFragment();
        workShowFragment = new WorkShowFragment();
        fragment_array = new Fragment[]{workShowFragment, scoreShowFragment};
        bt_array = new RadioButton[]{button_work, button_score};

        btnTitleLeft.setOnClickListener(this);
        tvTitle.setOnClickListener(this);
        bt_right.setOnClickListener(this);

        workShowFragment.getclassid(classid);
        scoreShowFragment.getclassid(classid);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.framelayout, scoreShowFragment)
                .add(R.id.framelayout, workShowFragment)
                .hide(scoreShowFragment).show(workShowFragment)
                .commit();

        bt_array[0].setChecked(true);
        bt_array[1].setChecked(false);


        button_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == button_score.getId()) {
                    index = 0;
                    bt_array[0].setChecked(true);
                    bt_array[1].setChecked(false);
//                    button_score.setTextColor(Color.WHITE);
//                    button_score.setBackgroundColor(Color.BLUE);


                } else if (checkedId == button_work.getId()) {
                    index = 1;
                    bt_array[1].setChecked(true);
                    bt_array[0].setChecked(false);

                }
                if (currentindex != index) {
                    getSupportFragmentManager().beginTransaction()
                            .hide(fragment_array[currentindex]).show(fragment_array[index])
                            .commit();
                    bt_array[index].setTextColor(Color.WHITE);
                    bt_array[currentindex].setTextColor(getResources().getColor(R.color.text_teacher));
                    bt_array[index].setBackgroundColor(getResources().getColor(R.color.text_teacher));
                    bt_array[currentindex].setBackgroundColor(Color.WHITE);
                    currentindex = index;
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getPopWindow();
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (event.getKeyCode()==KeyEvent.KEYCODE_BACK){
////            showToast("退出啦");
//        }
//        return super.onKeyDown(keyCode, event);
//    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTitleLeft:
                finish();
                break;
//            case R.id.tvTitle:
//                break;
            case R.id.bt_right:
//                Intent intent = new Intent(this,TeacherHelpMainActivity.class);
//                startActivity(intent);
                Intent intent = new Intent(this, ExplainActivity.class);
                startActivity(intent);

                break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        getService().getDataReportManager().reportEventBid(EventType.CHANNEL_OUT, "achievement");
        super.finish();
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
                                classid = department.getDepartmentId();
                                workShowFragment.ReData(classid);
                                scoreShowFragment.ReData(classid);

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
            lp.height = PhoneUtils.dip2px(this, 85);
            ; // 动态高度
        } else if (departs.length >= 5) {
            lp.height = PhoneUtils.dip2px(this, 208);
            ; // 动态高度
        } else {
            lp.height = PhoneUtils.dip2px(this, departs.length * 45) - PhoneUtils.dip2px(this, 8);
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
