package com.tuxing.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tuxing.app.MainActivity;
import com.tuxing.app.R;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.util.DateTimePickDialogUtil;
import com.tuxing.app.util.PreferenceUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.UmengData;
import com.tuxing.app.util.Utils;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.event.ChildEvent;
import com.tuxing.sdk.modle.Relative;
import com.umeng.analytics.MobclickAgent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//小孩+选择身份页面
public class SelectFolksActivity extends BaseActivity {

	private RelativeLayout rl_select_folks;
	List<Relative> relatives;
	User childUser;
	private  TextView tv_name;
	private  TextView tv_folk_name;
	public static  int REQUESTCODE = 0x001;
	public static  int REQUESTCODE_NAME = 0x002;
	private LinearLayout my_baby_birth_rl;
	private TextView my_baby_birth;
	private LinearLayout my_baby_parent;
	private  TextView tv_baby_parent_name;
	private User user;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.layout_select_folks);
		relatives = new ArrayList<Relative>();
		initView();
		setTitle("选择身份");
		setLeftBack("", true, false);
		setRightNext(true, "确定", 0);
		user = (User)getIntent().getSerializableExtra("user");
		if(null!=user){
			getService().getUserManager().getChild();//获取孩子信息
		}
		getService().getRelativeManager().getRelativeList();
		MobclickAgent.onEvent(mContext,"login_active_select_identity",UmengData.login_active_select_identity);
	}

	private void initView() {
		tv_name = (TextView)findViewById(R.id.tv_name);
		tv_folk_name = (TextView)findViewById(R.id.tv_folk_name);
		tv_folk_name.setText("请选择");
		rl_select_folks = (RelativeLayout) findViewById(R.id.rl_select_folks);
		rl_select_folks.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				showContextMenu(0);
				Intent intent = new Intent(mContext, FolksListActivity.class);
				intent.putExtra("type", 0);
				intent.putExtra("relativeType",relativeType);
				startActivityForResult(intent, REQUESTCODE);
			}
		});
		my_baby_birth_rl = (LinearLayout)findViewById(R.id.my_baby_birth_rl);
		my_baby_birth_rl.setOnClickListener(this);
		my_baby_birth = (TextView)findViewById(R.id.my_baby_birth);
		my_baby_parent = (LinearLayout)findViewById(R.id.my_baby_parent);
		my_baby_parent.setOnClickListener(this);
		tv_baby_parent_name = (TextView)findViewById(R.id.tv_baby_parent_name);
	}
	private int relativeType = -1;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data!=null){
			if(requestCode==REQUESTCODE){
				String name = data.getExtras().getString("name");//得到新Activity 关闭后返回的数据
				relativeType = data.getExtras().getInt("relativeType");//得到新Activity 关闭后返回的数据
				tv_folk_name.setText(name);
			}else if(requestCode==REQUESTCODE_NAME){
				String name = data.getExtras().getString("name");//得到新Activity 关闭后返回的数据
				tv_baby_parent_name.setText(name);
			}
		}
	}

	public void onclickRightNext() {
		MobclickAgent.onEvent(mContext,"login_active_que",UmengData.login_active_que);
	
		if("请选择".equals(tv_folk_name.getText().toString())){
			showToast("请选择身份");
			return;
		}
		if(childUser == null) {
			showToast("获取小孩身份失败");
			return;
		}
		if(TextUtils.isEmpty(my_baby_birth.getText().toString())) {
			showToast("请选择生日");
			return;
		}
		if(TextUtils.isEmpty(tv_baby_parent_name.getText().toString())) {
			showToast("请输入监护人");
			return;
		}
		if(user!=null&&!"请选择".equals(tv_folk_name.getText().toString())&&!TextUtils.isEmpty(my_baby_birth.getText().toString())){
			long timeStemp = 0;
			showProgressDialog(mContext,"",true,null);
			try {
				timeStemp = new SimpleDateFormat("yyyy-MM-dd").parse(my_baby_birth.getText().toString()).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			getService().getRelativeManager().bindChild(childUser.getUserId(), relativeType,timeStemp,tv_baby_parent_name.getText().toString());
		}
	}
	public void onEventMainThread(ChildEvent event) {
		disProgressDialog();
		switch (event.getEvent()) {
			case GET_CHILD_SUCCESS:
				childUser = event.getChild();
				if(null!=childUser){
					tv_name.setText(childUser.getNickname());
					tv_baby_parent_name.setText(childUser.getGuarder());
//					my_baby_birth.setText(Utils.getDateTime(this, childUser.getBirthday()));
					Date date=new Date(childUser.getBirthday());
					SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd");
					String str=format.format(date);
					my_baby_birth.setText(str);
				}else{
					showToast(event.getMsg());
				}
				break;
			case GET_CHILD_FAILED:
				showToast(event.getMsg());
				break;
			case BIND_CHILD_SUCCESS:
				MobclickAgent.onEvent(mContext,"login_active_success",UmengData.login_active_success);
				Intent intent = new Intent(mContext, MainActivity.class);
				intent.putExtra("userId", user.getUserId() + "");
				PreferenceUtils.setPrefString(mContext, SysConstants.userName, user.getMobile());
				startActivity(intent);
				finish();
				break;
			case BIND_CHILD_FAILED:
				showToast(event.getMsg());
				MobclickAgent.onEvent(mContext,"login_active_fail",UmengData.login_active_fail);
				break;
			case CHILD_BIND_BY_OTHERS:
				showToast(event.getMsg());
				break;
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if(v.getId() == R.id.my_baby_birth_rl){
			DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
					mContext, my_baby_birth.getText().toString(),my_baby_birth,true,new MySelectListener());
			dateTimePicKDialog.dateTimePicKDialog();
		}else if(v.getId() == R.id.my_baby_parent){
			Intent intent = new Intent(mContext, EditFolksNameActivity.class);
			intent.putExtra("name", ""+tv_baby_parent_name.getText().toString());
			startActivityForResult(intent, REQUESTCODE_NAME);
		}
	}
	public class MySelectListener implements DateTimePickDialogUtil.SelectListener {
		@Override
		public void updateInfo() {
		}
	}
}
