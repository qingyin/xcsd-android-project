package com.tuxing.app.activity;

import java.util.ArrayList;
import java.util.List;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tuxing.app.R;
import com.tuxing.app.adapter.TestFirstAdapter;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.bean.TestFirstBean;
import com.tuxing.app.helper.NetManager;
import com.tuxing.app.helper.TestHelper;
import com.tuxing.app.util.PixUtil;

public class TestFirstActivity extends BaseActivity {

	public static final String TEST_FIRST_ID = "Test_First_Id";
	public static final String TEST_FIRST_NAME = "Test_First_Name";
	private RelativeLayout test_first_back_img_layout;
	private TextView test_first_back_txt;
	private GridView test_first_grid;
	private TestFirstAdapter testAdapter;
	private List<TestFirstBean> listItem = new ArrayList<TestFirstBean>();
	private TextView test_first_title;
	private LinearLayout test_first_empty_layout;
	private ImageView test_first_empty_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_first_activity);

		test_first_back_img_layout = (RelativeLayout) findViewById(R.id.test_first_back_img_layout);
		test_first_back_img_layout.setVisibility(View.GONE);
		test_first_back_txt = (TextView) findViewById(R.id.test_first_back_txt);
		test_first_back_txt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		test_first_back_txt.setVisibility(View.GONE);
		test_first_title = (TextView) findViewById(R.id.test_first_title);
		test_first_title.setText(R.string.topic);

		test_first_grid = (GridView) findViewById(R.id.test_first_grid);
		testAdapter = new TestFirstAdapter(TestFirstActivity.this, listItem);
		test_first_grid.setAdapter(testAdapter);
		int item_width = (getWindowManager().getDefaultDisplay().getWidth() - PixUtil
				.convertDpToPixel(42, TestFirstActivity.this)) / 2;
		testAdapter.setItemWidth(item_width);
		test_first_grid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(TestFirstActivity.this,
						TestSecondActivity.class);
				intent.putExtra(TEST_FIRST_ID, listItem.get(arg2).id);
				intent.putExtra(TEST_FIRST_NAME, listItem.get(arg2).name);
				startActivity(intent);
			}
		});

		test_first_empty_layout = (LinearLayout) findViewById(R.id.test_first_empty_layout);
		test_first_empty_btn = (ImageView) findViewById(R.id.test_first_empty_btn);
		test_first_empty_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getData();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		getData();
	}

	public void getData() {
		TestHelper.getFirstTests(TestFirstActivity.this,
				new NetManager.MonitorInterface() {
					@Override
					public void getContentFromNet(Object content) {
						if (content != null) {
							test_first_grid.setVisibility(View.VISIBLE);
							test_first_empty_layout.setVisibility(View.GONE);
							listItem = (List<TestFirstBean>) content;
							testAdapter.setListItem(listItem);
						} else {
							// 加载空态页面
							test_first_grid.setVisibility(View.GONE);
							test_first_empty_layout.setVisibility(View.VISIBLE);
							listItem.clear();
							testAdapter.setListItem(listItem);
						}
					}
				});
	}

}
