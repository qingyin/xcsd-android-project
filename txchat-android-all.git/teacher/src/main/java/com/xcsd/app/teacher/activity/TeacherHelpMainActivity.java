package com.xcsd.app.teacher.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.tuxing.app.base.BaseActivity;
import com.tuxing.sdk.event.DataReportEvent;
import com.xcsd.app.teacher.fragment.ExpertFragment;
import com.xcsd.app.teacher.fragment.PersonalFragment;
import com.xcsd.app.teacher.fragment.QuestionFragment;
import com.xcsd.app.teacher.R;
import com.xcsd.rpc.proto.EventType;

import de.greenrobot.event.EventBus;


public class TeacherHelpMainActivity extends BaseActivity {

	//定义数组来存放按钮图片
	private int mImageViewArray[] = {
			com.tuxing.app.R.drawable.teacher_help_question_selector,
			/*com.tuxing.app.R.drawable.teacher_help_baodian_selector,*/
			com.tuxing.app.R.drawable.teacher_help_expert_selector,
			com.tuxing.app.R.drawable.teacher_help_personal_selector};

	//定义数组来存放Fragment界面
	private Class<?> fragmentArray[] = {
            QuestionFragment.class,
           /* BaoDianFragment.class,*/
            ExpertFragment.class,
            PersonalFragment.class};

    private String mTextviewArray[] = {"问题", /*"幼教宝典",*/ "专家", "个人"};

	//定义一个布局
	private LayoutInflater layoutInflater;

	//定义FragmentTabHost对象
	private FragmentTabHost mTabHost;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main_teacher_help);
		initView();

		getService().getDataReportManager().reportEventBid(EventType.CHANNEL_IN, "teachercommunity");

	}




	private void initView() {
		//实例化布局对象
		layoutInflater = LayoutInflater.from(this);
		//实例化TabHost对象，得到TabHost
		mTabHost = (FragmentTabHost)findViewById(R.id.main_tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.main_tabcontent);
		mTabHost.getTabWidget().setDividerDrawable(R.color.white);
		//得到fragment的个数
		int count = fragmentArray.length;

		for(int i = 0; i < count; i++){
			//为每一个Tab按钮设置图标、文字和内容
			TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
			//将Tab按钮添加进Tab选项卡中
			mTabHost.addTab(tabSpec, fragmentArray[i], null);
//			//设置Tab按钮的背景
//			mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
		}

		mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String s) {
//				if (mTabHost.getCurrentTab() == 1) {}
			}
		});
	}

	@Override
	public void onClick(View v) {

	}


	/**
	 * 给Tab按钮设置图标和文字
	 */
	private View getTabItemView(int index){
		View view = layoutInflater.inflate(R.layout.layout_teacher_help_button, null);

		ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
		imageView.setImageResource(mImageViewArray[index]);

		TextView textView = (TextView) view.findViewById(R.id.textview);
		textView.setText(mTextviewArray[index]);
		if (index == 0) {
			textView.setSelected(true);
		}

		return view;
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
		getService().getDataReportManager().reportEventBid(EventType.CHANNEL_OUT, "teachercommunity");
		super.finish();
	}

}
