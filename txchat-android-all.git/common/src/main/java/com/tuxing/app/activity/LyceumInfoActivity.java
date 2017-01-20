package com.tuxing.app.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.tuxing.app.R;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.view.RoundImageView;

public class LyceumInfoActivity extends BaseActivity {
	public static final String LOG_TAG = LyceumInfoActivity.class.getSimpleName();
	
	private TextView title;
	private TextView sum;
	private TextView suoShu;
	private RoundImageView icon;
	private ToggleButton button;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.lyceuminfo_layout);
		init();
		
	}

	private void init() {
		// TODO 
		setTitle(getString(R.string.home_name));
		setLeftBack("", true,false);
		setRightNext(true, "", 0);
		
		icon = (RoundImageView) findViewById(R.id.lyceuminfo_icon);
		title = (TextView) findViewById(R.id.lyceuminfo_title);
		sum = (TextView) findViewById(R.id.lyceuminfo_sum);
		suoShu = (TextView) findViewById(R.id.lyceuminfo_suoshu);
		button = (ToggleButton) findViewById(R.id.lyceuminfo__button);
		findViewById(R.id.lyceuminfo_clean_msg).setOnClickListener(this);
		findViewById(R.id.lyceuminfo_query_msg).setOnClickListener(this);
		initData();
	}
	
	private void initData() {
		// TODO 
		
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		int id = v.getId();
		if (id == R.id.lyceuminfo_clean_msg) {
			showToast("清空消息");
		} else if (id == R.id.lyceuminfo_query_msg) {
			showToast("查看历史消息");
		}
	}
	
}
