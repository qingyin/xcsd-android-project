package com.xcsd.app.parent.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.easemob.chat.EMChatManager;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.sdk.utils.Constants;
import com.xcsd.app.parent.R;
import com.tuxing.app.util.PreferenceUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.view.RoundImageView;
import com.tuxing.sdk.db.entity.Department;
import com.tuxing.sdk.event.DepartmentEvent;
import com.tuxing.sdk.event.GetDepartmentMemberEvent;
import com.tuxing.sdk.modle.DepartmentMember;

import java.util.ArrayList;
import java.util.List;


public class ClassManagerActivity extends BaseActivity {

    private RoundImageView classIcon;
    private RelativeLayout teachRl;
    private RelativeLayout studentRl;
    private RelativeLayout clearHistoryMsg;
    private RoundImageView teacherIcon0;
    private RoundImageView teacherIcon1;
    private RoundImageView teacherIcon2;
    private RoundImageView teacherIcon3;
    private RoundImageView studentIcon0;
    private RoundImageView studentIcon1;
    private RoundImageView studentIcon2;
    private RoundImageView studentIcon3;
    private TextView schoolName;
    private TextView className;
    private ToggleButton distrub;
    private RoundImageView teacherHeadAvtar[];
    private RoundImageView studentHeadAvtar[];
    private String classId;
    private Department department;
    private List<DepartmentMember> teaDepartmentMembers;
    List<String> tIconUrl;
    List<String> sIconUrl;
    private List<DepartmentMember> stuDepartmentMembers;
    private long departmentId;
    private String TAG = ClassManagerActivity.class.getSimpleName();
    List<String> idList;
    private Handler scrollHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    // TODO 教师
                    if (teaDepartmentMembers != null) {
                        tIconUrl.clear();

                        for (int t = 0; t < teaDepartmentMembers.size(); t++) {
                            String tUrl = teaDepartmentMembers.get(t).getUser().getAvatar();
                            if (!"".equals(tUrl) && tUrl != null) {
                                tIconUrl.add(0, tUrl);
                            } else {
                                tIconUrl.add(tUrl);
                            }
                        }
                        if (tIconUrl.size() > 0) {
                            int teacherNum = tIconUrl.size();
                            teacherNum = teacherNum >= 4 ? 4 : teacherNum;
                            int i;
                            for (i = 0; i < teacherNum; i++) {
                                teacherHeadAvtar[i].setImageUrl(tIconUrl.get(i) + SysConstants.Imgurlsuffix80, R.drawable.default_avatar);
                            }
                        }
                    }
                    break;
                case 2:
                    // TODO 学生权限
                    if (stuDepartmentMembers != null) {
                        sIconUrl.clear();

                        for (int s = 0; s < stuDepartmentMembers.size(); s++) {
                            if (stuDepartmentMembers.get(s).getRelatives() != null && stuDepartmentMembers.get(s).getRelatives().size() > 0) {
                                String sUrl = stuDepartmentMembers.get(s).getRelatives().get(0).getAvatar();
                                if (!"".equals(sUrl) && sUrl != null) {
                                    sIconUrl.add(0, sUrl);
                                } else {
                                    sIconUrl.add(sUrl);

                                }
                            }
                        }
                        int stuNum = sIconUrl.size();
                        if (stuNum > 0) {
                            stuNum = stuNum >= 4 ? 4 : stuNum;
                            for (int k = 0; k < stuNum; k++) {
                                studentHeadAvtar[k].setImageUrl(sIconUrl.get(k) + SysConstants.Imgurlsuffix80, R.drawable.default_avatar);
                            }
                        }
                    }
                    break;

            }

        }

        ;
    };
    private String schoolData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.class_manage_layout);
        isActivity = true;
        init();
        initData();
    }

    private void init() {

        setTitle(getString(R.string.groupTitleName));
        setLeftBack("", true, false);
        setRightNext(false, "", 0);
        departmentId = getIntent().getLongExtra("groupId", 0);
        getService().getContactManager().syncDepartment(departmentId);
        clearHistoryMsg = (RelativeLayout) findViewById(R.id.manager_clear_msg);
        className = (TextView) findViewById(R.id.manager_class_name);
        schoolName = (TextView) findViewById(R.id.manager_school_name);
        classIcon = (RoundImageView) findViewById(R.id.manager_class_icon);

        teachRl = (RelativeLayout) findViewById(R.id.manager_teacher_rl);
        teacherIcon0 = (RoundImageView) findViewById(R.id.manager_teachHead0);
        teacherIcon1 = (RoundImageView) findViewById(R.id.manager_teachHead1);
        teacherIcon2 = (RoundImageView) findViewById(R.id.manager_teachHead2);
        teacherIcon3 = (RoundImageView) findViewById(R.id.manager_teachHead3);

        studentRl = (RelativeLayout) findViewById(R.id.manager_student_rl);
        studentIcon0 = (RoundImageView) findViewById(R.id.manager_studentHead0);
        studentIcon1 = (RoundImageView) findViewById(R.id.manager_studentHead1);
        studentIcon2 = (RoundImageView) findViewById(R.id.manager_studentHead2);
        studentIcon3 = (RoundImageView) findViewById(R.id.manager_studentHead3);
        distrub = (ToggleButton) findViewById(R.id.manager_no_distrub_button);
        if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())) {
            distrub.setBackground(getResources().getDrawable(com.tuxing.app.R.drawable.ios7_btn_p));
        }
        clearHistoryMsg.setOnClickListener(this);
        teacherHeadAvtar = new RoundImageView[]{teacherIcon0, teacherIcon1, teacherIcon2, teacherIcon3};
        studentHeadAvtar = new RoundImageView[]{studentIcon0, studentIcon1, studentIcon2, studentIcon3};
        classId = getIntent().getStringExtra("classId");
        teachRl.setOnClickListener(this);
        studentRl.setOnClickListener(this);
        boolean isDistrub = PreferenceUtils.getPrefBoolean(mContext, SysConstants.departmentdi_sturb, false);
        distrub.setChecked(isDistrub);
        distrub.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                distrub.setChecked(isChecked);
                idList.clear();
                if (isChecked) {
                    if (department != null) {
                        idList.add(department.getChatGroupId());
                        EMChatManager.getInstance().getChatOptions().setReceiveNotNoifyGroup(idList);
                        distrub.setChecked(isChecked);
                        PreferenceUtils.setPrefBoolean(mContext, SysConstants.departmentdi_sturb, true);
                    } else {
                        showToast("设置失败");
                    }
                } else {
                    distrub.setChecked(isChecked);
                    EMChatManager.getInstance().getChatOptions().setReceiveNotNoifyGroup(null);
                    PreferenceUtils.setPrefBoolean(mContext, SysConstants.departmentdi_sturb, isChecked);

                }

            }
        });
        tIconUrl = new ArrayList<String>();
        sIconUrl = new ArrayList<String>();
        idList = new ArrayList<String>();
    }


    private void initData() {
        showProgressDialog(mContext, "", true, null);
        if (user != null)
            schoolData = user.getGardenName();
        // 获取本地班级详情
        getService().getDepartmentManager().getDepartmentInfoFromLocal(departmentId);
        getService().getContactManager().getDepartmentMemberByUserType(departmentId, SysConstants.TEACHER);
    }

    public void onEventMainThread(DepartmentEvent event) {
        if (isActivity) {
            switch (event.getEvent()) {
                case DEPARTMENT_LOAD_FROM_LOCAL:
                    department = event.getDepartment();
                    if (department != null) {
                        showData(department);
                        showAndSaveLog(TAG, "获取本地的班级详情成功" + department.getDepartmentId(), false);
                        disProgressDialog();
                    } else {
                        showAndSaveLog(TAG, "获取本地的班级详情为空", false);
                        getService().getDepartmentManager().requestDepartmentInfoFromServer(departmentId);
                    }
                    // 获取班级成员
                    getService().getContactManager().getDepartmentMemberByUserType(departmentId, SysConstants.TEACHER);
                    break;
                case DEPARTMENT_REQUEST_SUCCESS:
                    if (department != null) {
                        department = event.getDepartment();
                        showData(department);
                        showAndSaveLog(TAG, "获取网络的班级详情成功" + department.getDepartmentId(), false);
                    }
                    disProgressDialog();
                    break;
                case DEPARTMENT_REQUEST_FAILED:
                    // 返回班级详情成功
                    disProgressDialog();
                    showAndSaveLog(TAG, "获取群资料信息失败   --" + event.getMsg(), false);
                    break;

            }
        }
    }

    public void onEventMainThread(GetDepartmentMemberEvent event) {
        //返回班级成员

        if (isActivity) {
            switch (event.getUserType()) {
                case SysConstants.TEACHER:
                    teaDepartmentMembers = event.getDepartmentMembers();
                    if (teaDepartmentMembers != null) {
                        showAndSaveLog(TAG, "获取本地的教师成员" + teaDepartmentMembers.size(), false);
                        scrollHandler.sendEmptyMessage(1);
                    }
                    disProgressDialog();
                    break;
                case SysConstants.CHILD:
                    stuDepartmentMembers = event.getDepartmentMembers();
                    if (stuDepartmentMembers != null) {
                        showAndSaveLog(TAG, "获取本地的班级学生成员" + stuDepartmentMembers.size(), false);
                        scrollHandler.sendEmptyMessage(2);
                    }
                    disProgressDialog();
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.manager_teacher_rl:
                //TODO 跳转到教师群
                Intent intentTea = new Intent(mContext, ClassManagerTeaActivity.class);
                intentTea.putExtra("departmentId", departmentId);
                openActivityOrFragment(intentTea);

                break;
            case R.id.manager_student_rl:
                //TODO 跳转到学生群(权限)
                Intent intentParent = new Intent(mContext, ClassManagerParentActivity.class);
                intentParent.putExtra("departmentId", departmentId);
                openActivityOrFragment(intentParent);

                break;
            case R.id.manager_clear_msg:
                //TODO 清除聊天记录
                showBtnDialog(new String[]{getString(R.string.btn_clear),
                        getString(R.string.btn_cancel)});
                break;

        }
    }

    public void showData(Department data) {
        schoolName.setText(schoolData);
        classIcon.setImageUrl(data.getAvatar() + SysConstants.Imgurlsuffix80, R.drawable.ic_class);
        className.setText(data.getName());
        if (department.getShowParents()) {
            getService().getContactManager().getDepartmentMemberByUserType(departmentId, SysConstants.CHILD);
            findViewById(R.id.stu_line).setVisibility(View.VISIBLE);
            studentRl.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onclickBtn1() {
        EMChatManager.getInstance().clearConversation(department.getChatGroupId());
        showToast("清除聊天记录成功");
        super.onclickBtn1();
    }


}
