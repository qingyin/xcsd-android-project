package com.tuxing.app.activity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.bean.TestBean;
import com.tuxing.app.helper.NetManager;
import com.tuxing.app.helper.TestHelper;
import com.tuxing.app.util.ShapeUtil;
import com.tuxing.sdk.utils.Constants;

public class TestDespActivity extends BaseActivity {

	public static final String TEST = "Test";
	private TextView test_desp_back_txt;
	private ImageButton go_main;
	private TextView test_desp_img;
	private Button begin_test;
	private TextView test_desp;
	private TestBean test;
	private RelativeLayout subject_count_layout;
	private TextView test_subject_count;
	private String associateTag;
	private LinearLayout test_desp_header;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_desp_activity);

		test_desp_header = (LinearLayout) findViewById(R.id.test_desp_header);
		if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {
//			test_desp_header.setBackgroundColor(getResources().getColor(R.color.teacher_pre_title));
		}

//		associateTag = getIntent().getStringExtra(RelatedTaskActivity.ASSOCIATE_TAG);
		test_desp_back_txt = (TextView) findViewById(R.id.test_desp_back_txt);
		test_desp_back_txt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		go_main = (ImageButton) findViewById(R.id.go_main);
		go_main.setVisibility(View.GONE);
		go_main.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent _intent = new Intent(TestDespActivity.this,
//						MainTabActivity.class);
//				startActivity(_intent);
			}
		});

		test_desp_img = (TextView) findViewById(R.id.test_desp_img);
		test_desp_img.setBackgroundDrawable(ShapeUtil.getGradientTopDrawable(
				TestDespActivity.this, R.color.edu_color_2));

		begin_test = (Button) findViewById(R.id.begin_test);
		begin_test.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TestDespActivity.this,
						SubjectActivity.class);
				intent.putExtra(TEST, test);
				TestHelper.clearAnswers(TestDespActivity.this, test.id);
//				intent.putExtra(RelatedTaskActivity.ASSOCIATE_TAG, associateTag);
				startActivity(intent);
				finish();
			}
		});

		test_desp = (TextView) findViewById(R.id.test_desp);
		int test_id = getIntent().getIntExtra(TestSecondActivity.TEST_ID, 0);
		TestHelper.getTests(TestDespActivity.this, test_id,
				new NetManager.MonitorInterface() {
					@Override
					public void getContentFromNet(Object content) {
						if (content != null) {
							test = (TestBean) content;
							test_desp.setText(test.description);
							subject_count_layout.setVisibility(View.VISIBLE);
							int size = 0;
							if (test.subjests != null) {
								size = test.subjests.size();
							}
							test_subject_count.setText("本测试共有" + size + "道题");

							if(size == 0){
								begin_test.setVisibility(View.GONE);
							}
						}
					}
				});
		subject_count_layout = (RelativeLayout) findViewById(R.id.subject_count_layout);
		test_subject_count = (TextView) findViewById(R.id.test_subject_count);
	}

}
