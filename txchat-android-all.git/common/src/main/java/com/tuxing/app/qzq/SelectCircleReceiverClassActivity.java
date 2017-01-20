package com.tuxing.app.qzq;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.tuxing.app.R;
import com.tuxing.app.activity.ContactActivity;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.qzq.adapter.CircleReceiverClassAdapter;
import com.tuxing.app.qzq.adapter.CircleReceiverClassAdapter.ShowNumber;
import com.tuxing.sdk.db.entity.Department;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectCircleReceiverClassActivity extends BaseActivity implements OnItemClickListener {
	private ListView classListView;
	private CircleReceiverClassAdapter classAdapter;
	public String TAG = ContactActivity.class.getSimpleName();
	private List<Department> departments;
	private Map<Integer,Boolean> selectCb;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.select_receiver_class_layout);
		init();
		initData();

	}

	private void init() {
		setTitle(getString(R.string.tab_circle));
		setLeftBack("取消", false, false);
		classListView = (ListView) findViewById(R.id.select_receiver_class_listview);
		classListView.setOnItemClickListener(this);
	}
	@Override
	protected void onResume() {
		super.onResume();
	}

	private void initData() {
		List<Department> deps = (List<Department>) getIntent().getSerializableExtra("deptTempList");
		selectCb = new HashMap<Integer,Boolean>();
		departments = getService().getContactManager().getAllDepartment();
		for(int i =0;i<departments.size();i++){
			boolean flag = false;
			for(Department dep:deps){
				if(dep.getDepartmentId()==departments.get(i).getDepartmentId()){
					flag = true;
				}
			}
			if(flag){
				selectCb.put(i,true);
			}else{
				selectCb.put(i,false);
			}
		}
		setRightNext(true, "选择" + "(" + deps.size() + ")", 0);
		updateAdapter(departments);
	}

	@Override
	public void onclickRightNext() {
		super.onclickRightNext();
		Intent intent =new Intent(mContext,CircleReleaseActivity.class);
		List<Department> departList = new ArrayList<Department>();
		for(int i=0;i<departments.size();i++){
			if(classAdapter.getMap().size()>0&&classAdapter.getMap().get(i)){
				departList.add(departments.get(i));
			}
		}
		if(departList.size()<=0){
			showToast("请选择班级部门");
			return;
		}
		intent.putExtra("depts", (Serializable) departList);
		setResult(Activity.RESULT_OK,intent);
		finish();
	}
	/**
	 * 显示数据
	 */
	public void updateAdapter(List<Department> list){
		if(classAdapter == null){
			classAdapter = new CircleReceiverClassAdapter(mContext, list,new changeSelectNumber(),selectCb);
			classListView.setAdapter(classAdapter);
		}else{
			classAdapter.setData(selectCb);
			classAdapter.notifyDataSetChanged();
		}
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
//		Boolean isChecket = classAdapter.getMap().get(position);
//		Intent intent = new Intent(mContext,SelectReceiverActivity.class);
//		intent.putExtra("departmentId", departments.get(position).getDepartmentId());
//		intent.putExtra("departmentName", departments.get(position).getName());
//		intent.putExtra("isChecket", isChecket);
//		startActivityForResult(intent, 100);

	}
	/**
	 * 显示选择的数量
	 */
	public class changeSelectNumber implements ShowNumber{
		@Override
		public void refeshNumber(int number) {
			setRightNext(true, "选择" + "(" + number + ")", 0);
		}
	}
}
