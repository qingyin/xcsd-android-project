package com.xcsd.app.teacher.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.teacher.adapter.ParentAdapter;
import com.xcsd.app.teacher.util.SideBar;
import com.xcsd.app.teacher.util.SideBar.OnTouchingLetterChangedListener;
import com.xcsd.app.teacher.R;
import com.tuxing.app.util.PinyinComparator;
import com.tuxing.app.util.SysConstants;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.event.GetDepartmentMemberEvent;
import com.tuxing.sdk.modle.DepartmentMember;
import com.tuxing.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ClassManagerParentActivity extends BaseActivity implements OnChildClickListener{

	private List<DepartmentMember> departmentMembers;
	private long departmentId;
	private ExpandableListView expandableView;
	private ParentAdapter adapter;
	private String title;
	public String TAG = ClassManagerParentActivity.class.getSimpleName();
	  private SideBar sideBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.class_manager_parent_layout);
		init();
		initData();
		showProgressDialog(mContext, "", true, null);
	}
	
	
	private void init() {
		title = getIntent().getStringExtra("title");
		setTitle(title);
		setLeftBack("", true, false);
		setRightNext(false, "", 0);
		
		departmentId = getIntent().getLongExtra("departmentId", 0);
		expandableView = (ExpandableListView) findViewById(R.id.manager_parent_listview);
		expandableView.setOnChildClickListener(this);
		expandableView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) { //

				return true;

			}

		});
		departmentMembers = new ArrayList<DepartmentMember>();
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
	
	private void initData() {
		// TODO 获取成员
		getService().getContactManager().getDepartmentMemberByUserType(departmentId, SysConstants.CHILD);
		
	}
	
	 public void onEventMainThread(GetDepartmentMemberEvent event){
		 //班级成员
		 if(isActivity){
			 disProgressDialog();
			 departmentMembers = event.getDepartmentMembers();
			 if(!CollectionUtils.isEmpty(departmentMembers)){
				 Collections.sort(departmentMembers, new PinyinComparator());
				 showData(departmentMembers);
				 showAndSaveLog(TAG, "获取本地家长成员   数量 = " + departmentMembers.size(), false);
			 }
		 }
	 }
	
	
	private void showData(List<DepartmentMember> list) {
		// TODO
		if (adapter == null) {
			adapter = new ParentAdapter(mContext, list);
			expandableView.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
		for (int i = 0; i < adapter.getGroupCount(); i++) {
			expandableView.expandGroup(i);
		}

	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		// TODO 
		User user = departmentMembers.get(groupPosition).getRelatives().get(childPosition);
		Intent intent = new Intent(mContext,ChatUserInfoParentActivity.class);
		intent.putExtra("userId", user.getUserId());
		intent.putExtra("isClassManager", true);
		openActivityOrFragment(intent);
		return false;
	}

}
