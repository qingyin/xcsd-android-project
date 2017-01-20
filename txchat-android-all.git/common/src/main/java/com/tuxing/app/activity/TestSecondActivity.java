package com.tuxing.app.activity;

import java.util.ArrayList;
import java.util.List;



import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.tuxing.app.R;
import com.tuxing.app.adapter.TestSecondAdapter;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.bean.TestSecondBean;
import com.tuxing.app.helper.NetManager;
import com.tuxing.app.helper.TestHelper;
import com.tuxing.app.util.PixUtil;
import com.tuxing.app.util.PreferenceUtil;
import com.tuxing.app.util.StringUtil;

public class TestSecondActivity extends BaseActivity {

	private static final String TAG = TestSecondActivity.class.getName();
	public static final String TEST_ID = "Test_Id";
	public static final String CURRENT_TEST_NAME = "Current_Test_Name";
	private TextView test_first_back_txt;
	private TextView test_first_title;
	private GridView test_second_grid;
	private TestSecondAdapter testAdapter;
	private List<TestSecondBean> listItem = new ArrayList<TestSecondBean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.test_first_activity);

		test_first_back_txt = (TextView) findViewById(R.id.test_first_back_txt);
		test_first_back_txt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		test_first_title = (TextView) findViewById(R.id.test_first_title);
//		String first_name = "测试";
		String first_name = getIntent().getStringExtra(
				TestFirstActivity.TEST_FIRST_NAME);
		if (!StringUtil.isNullOrEmpty(first_name)) {
			test_first_title.setText(first_name);
		}

		test_second_grid = (GridView) findViewById(R.id.test_first_grid);
		testAdapter = new TestSecondAdapter(TestSecondActivity.this, listItem);
		test_second_grid.setAdapter(testAdapter);
		int item_width = (getWindowManager().getDefaultDisplay().getWidth() - PixUtil
				.convertDpToPixel(42, TestSecondActivity.this)) / 2;
		testAdapter.setItemWidth(item_width);
		test_second_grid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				final TestSecondBean test = listItem.get(arg2);
				PreferenceUtil.putDefStr(TestSecondActivity.this,
						CURRENT_TEST_NAME, test.name);
				if (test.status == 1) {
					// 已完成 跳至答题结果
//					int childID = GuideHelper.getCurrentBabyId(TestSecondActivity.this);
					int childID = 9;
					TestHelper.getEvaluation(TestSecondActivity.this,
							test.testID, childID, new NetManager.MonitorInterface() {
								@Override
								public void getContentFromNet(Object content) {
									Intent intent = new Intent(
											TestSecondActivity.this,
											TestEvaluationActivity.class);
//									intent.putExtra(TEST_ID, test.testID);
									startActivity(intent);
								}
							});
				} else {
//					 未完成 跳至做题
					Intent intent = new Intent(TestSecondActivity.this,
							TestDespActivity.class);
					intent.putExtra(TEST_ID, test.testID);
					startActivity(intent);
				}

			}
		});

//		CacheHelper.clearSecondTest(TestSecondActivity.this);
		int cf = getIntent().getIntExtra(TestFirstActivity.TEST_FIRST_ID, 0);
//		int cf = 0;
		TestHelper.getSecondTests(TestSecondActivity.this, cf,
				new NetManager.MonitorInterface() {
					@Override
					public void getContentFromNet(Object content) {
						listItem = (List<TestSecondBean>) content;
						testAdapter.setListItem(listItem);
					}
				});
	}

	@Override
	protected void onResume() {
		super.onResume();
//		List<TestSecondBean> list = CacheHelper
//				.getSecondTestCache(TestSecondActivity.this);
//		if(list.size()>0){
//			Log.i(TAG, "GetFromCache");
//			listItem.clear();
//			listItem.addAll(list);
//			testAdapter.setListItem(listItem);
//		}
	}

}
