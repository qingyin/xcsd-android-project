package com.xcsd.app.parent.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.tuxing.app.base.BaseActivity;
import com.xcsd.app.parent.R;
import com.xcsd.app.parent.adapter.TeacherAdapter;
import com.xcsd.app.parent.util.SideBar;
import com.xcsd.app.parent.util.SideBar.OnTouchingLetterChangedListener;
import com.tuxing.app.util.PinyinComparator;
import com.tuxing.app.util.SysConstants;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.event.GetDepartmentMemberEvent;
import com.tuxing.sdk.modle.DepartmentMember;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ClassManagerTeaActivity extends BaseActivity implements OnItemClickListener{

	private ListView teaListView;
	private SideBar sideBar;
	public TeacherAdapter adapter;
	private long departmentId;
	private List<DepartmentMember> departmentMembers;
	private String Tag = ClassManagerTeaActivity.class.getSimpleName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.class_manager_tea_layout);
		init();
		initData();
		showProgressDialog(mContext,"",true,null);
	}
	
	private void initData() {
		// TODO 获取成员
		getService().getContactManager().getDepartmentMemberByUserType(departmentId, SysConstants.TEACHER);
	}
	
	 public void onEventMainThread(GetDepartmentMemberEvent event){
		 //班级成员
		 if(isActivity){
			 disProgressDialog();
		 departmentMembers = event.getDepartmentMembers();
		 if(departmentMembers != null){
			 Collections.sort(departmentMembers, new PinyinComparator());
			 updateAdapter(departmentMembers);
		 }
		 }
	 }

	

	private void init() {
		setTitle(getString(R.string.chat_teacher));
		setLeftBack("", true,false);
		setRightNext(false, "", 0);
		departmentId = getIntent().getLongExtra("departmentId", 0);
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		teaListView = (ListView) findViewById(R.id.manager_tea_listview);
		
		//设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				 int position = adapter.getPositionForSection(s.charAt(0));
				 if(position != -1){
					 teaListView.setSelection(position);
				 }
			}
		});
		
		teaListView.setOnItemClickListener(this);
		departmentMembers = new ArrayList<DepartmentMember>();
		
	}
	
	/**
	 * 显示数据
	 * @param list
	 */
	public void updateAdapter(List<DepartmentMember> list){
		if(adapter == null){
			adapter = new TeacherAdapter(mContext, list);
			teaListView.setAdapter(adapter);
		}else{
			adapter.setTeacherList(list);
			adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO 
		User user = departmentMembers.get(position).getUser();
		Intent intent = new Intent(mContext,ChatUserInfoParentActivity.class);
		intent.putExtra("userId", user.getUserId());
		intent.putExtra("isClassManager", true);
		openActivityOrFragment(intent);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
}
