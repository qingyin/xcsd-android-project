package com.xcsd.app.teacher.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.teacher.adapter.ParentAdapter;
import com.xcsd.app.teacher.util.SideBar;
import com.xcsd.app.teacher.util.SideBar.OnTouchingLetterChangedListener;
import com.xcsd.app.teacher.view.ContactParentPopupwindow;
import com.xcsd.app.teacher.R;
import com.tuxing.app.ui.dialog.DialogHelper;
import com.tuxing.app.ui.dialog.PopupWindowDialog;
import com.tuxing.app.util.PhoneUtils;
import com.tuxing.app.util.PinyinComparator;
import com.tuxing.sdk.db.entity.Department;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.event.GetDepartmentMemberEvent;
import com.tuxing.sdk.modle.DepartmentMember;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.Constants;

import java.text.Collator;
import java.util.*;


public class ClassContactAllParentActivity extends BaseActivity implements OnChildClickListener {

    private List<DepartmentMember> realDepartmentMembers;
    private ExpandableListView expandableView;
    private ParentAdapter adapter;
    public String TAG = ClassContactAllParentActivity.class.getSimpleName();
    private List<Department> departments;
    private List<Department> showDepartments;
    private Map<String, List<DepartmentMember>> departmentListMap;
    private ContactParentPopupwindow popupwindow;
    private SideBar sideBar;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.class_manager_parent_layout);
        init();
        showProgressDialog(mContext, "", true, null);
        showDepartments = new ArrayList<Department>();
        departments = getService().getContactManager().getAllDepartment();
        departmentListMap = new HashMap<String, List<DepartmentMember>>();
        initData();
    }


    private void init() {
        title = getIntent().getStringExtra("title");
        setTitle(title);
        setLeftBack("", true, false);
        setRightNext(false, "", 0);

        expandableView = (ExpandableListView) findViewById(R.id.manager_parent_listview);
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        expandableView.setOnChildClickListener(this);
        expandableView.setOnGroupClickListener(new OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) { //
                return true;
            }
        });
        realDepartmentMembers = new ArrayList<DepartmentMember>();
        sideBar = (SideBar) findViewById(R.id.sidrbar);
    	//设置右侧触摸监听
    			sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

    				@Override
    				public void onTouchingLetterChanged(String s) {
    					// 该字母首次出现的位置
    					 int position = adapter.getPositionForSection(s.charAt(0));
    					 if(position != -1){
    						 expandableView.setSelectedGroup(position);    					 }
    				}
    			});
    }

    int count = 0;
    int countEnd = 0;

    private void initData() {
        for (Department depart : departments) {
            if (depart.getType() == Constants.DEPARTMENT_TYPE.CLASS) {
                count++;
                showDepartments.add(depart);
                getService().getContactManager().getDepartmentMemberByUserType(depart.getDepartmentId(), Constants.USER_TYPE.CHILD);
            }
        }

    }

    public void onEventMainThread(GetDepartmentMemberEvent event) {
        final Comparator comparator = Collator.getInstance(Locale.CHINA);
        //班级成员
        if (isActivity) {
            switch (event.getEvent()) {
                case QUERY_BY_TYPE:
                    countEnd++;
                    List<DepartmentMember>  departmentMembers  = event.getDepartmentMembers();
                    if (!CollectionUtils.isEmpty(departmentMembers)) {
                        Collections.sort(departmentMembers, new PinyinComparator());
                        departmentListMap.put(departmentMembers.get(0).getUser().getClassName(), departmentMembers);
                    }
                    if (countEnd >= count) {//显示
                        disProgressDialog();
//						 Department department = new Department();
//						 department.setName("全部");
//						 department.setDepartmentId(Long.MAX_VALUE);
//						 departments.add(0, department);
                        arrow_show(false);
                        getPopWindow();
                        List<DepartmentMember> departmentMemberAll = new ArrayList<DepartmentMember>();
                        for (List<DepartmentMember> departmentMembersList : departmentListMap.values()) {
                            for(DepartmentMember departmentMember:departmentMembersList){
                                if(departmentMember.getRelatives()!=null&&departmentMember.getRelatives().size()>0){
                                    departmentMemberAll.add(departmentMember);
                                }
                            }
                        }
                        realDepartmentMembers.clear();
                        realDepartmentMembers.addAll(departmentMemberAll);
                        showData(departmentMemberAll);
                    }
                    break;
            }
        }
    }

    int selectedPopup = 0;

    private void getPopWindow() {
        tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> departs = new ArrayList<String>();
                for(Department department:showDepartments){
                    departs.add(department.getName());
                }
                arrow_show(true);
                showContextMenu(departs.toArray(new CharSequence[departs.size()]));
            }
        });
    }


    private void showData(List<DepartmentMember> list) {
        // TODO
        if (adapter == null) {
            adapter = new ParentAdapter(mContext, list);
            expandableView.setAdapter(adapter);
        } else {
            adapter.setData(list);
        }
        if (adapter != null && adapter.getGroupCount() > 0) {
            for (int i = 0; i < adapter.getGroupCount(); i++) {
                expandableView.expandGroup(i);
            }
        }
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
                                int groupPosition, int childPosition, long id) {
        // TODO
        User user = realDepartmentMembers.get(groupPosition).getRelatives().get(childPosition);
        Intent intent = new Intent(mContext, ChatUserInfoParentActivity.class);
        intent.putExtra("userId", user.getUserId());
        intent.putExtra("isClassManager", true);
        openActivityOrFragment(intent);
        return false;
    }


    public void showContextMenu(final CharSequence[] departs) {
        final PopupWindowDialog dialog = DialogHelper.getPopDialogCancelable(mContext);
        dialog.setItemsWithoutChk(departs,
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        dialog.dismiss();
                        arrow_show(false);
                        List<DepartmentMember> departmentMembers = departmentListMap.get(departs[position]);
                                if (!CollectionUtils.isEmpty(departmentMembers)) {
                                    List<DepartmentMember> modDepartment = new ArrayList<DepartmentMember>();
                                    for(DepartmentMember departmentMember:departmentMembers){
                                        if(departmentMember.getRelatives()!=null&&departmentMember.getRelatives().size()>0){
                                            modDepartment.add(departmentMember);
                                        }
                                    }
                                    realDepartmentMembers.clear();
                                    realDepartmentMembers.addAll(modDepartment);
                                    showData(modDepartment);
                                } else {
                                    showData(new ArrayList<DepartmentMember>());
                                }
                        dialog.setSelectIndex(selectedPopup);
                        selectedPopup = position;
                    }
                },selectedPopup);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        lp.y =  PhoneUtils.dip2px(mContext, 48); // 新位置Y坐标
        lp.width = PhoneUtils.dip2px(mContext, 200); // 宽度
        if(departs.length<3){
            lp.height = PhoneUtils.dip2px(mContext, 100); ; // 动态高度
        }else if(departs.length>=5){
            lp.height = PhoneUtils.dip2px(mContext, 250); ; // 动态高度
        }else{
            lp.height = PhoneUtils.dip2px(mContext, departs.length*45)-PhoneUtils.dip2px(mContext, 8); ; // 动态高度
        }

        // 当Window的Attributes改变时系统会调用此函数,可以直接调用以应用上面对窗口参数的更改,也可以用setAttributes
        // dialog.onWindowAttributesChanged(lp);
        dialogWindow.setAttributes(lp);
        dialog.show();
    }

    private void arrow_show(boolean isUp){
        Drawable drawable = null;
        if(isUp){
            drawable= getResources().getDrawable(com.tuxing.app.R.drawable.ic_arrow_up);
        }else{
            drawable= getResources().getDrawable(com.tuxing.app.R.drawable.ic_arrow_down);
        }
        /// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth() + 5, drawable.getMinimumHeight());
        tv_title.setCompoundDrawables(null, null, null, drawable);

    }
}
