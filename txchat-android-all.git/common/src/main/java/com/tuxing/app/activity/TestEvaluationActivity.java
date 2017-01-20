package com.tuxing.app.activity;

import java.text.NumberFormat;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.bean.EvaluationBean;
import com.tuxing.app.helper.TestHelper;
import com.tuxing.app.util.CircleBitmapDisplayer;
import com.tuxing.app.util.ShapeUtil;
import com.tuxing.app.util.StringUtil;
import com.tuxing.app.util.SysConstants;
import com.tuxing.sdk.utils.Constants;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TestEvaluationActivity extends BaseActivity {

	private static final String TAG = TestEvaluationActivity.class.getName();
	private TextView test_evaluation_img;
	private TextView test_evaluation_back_txt;
	private Button repeat_test;
	private TextView test_evaluation_desp;
	private RelativeLayout share_test_layout, share_result_layout;
	private EvaluationBean evaluation;
	private int test_id;
//	private SharePopupWindow shareWindow;
	private ImageView share_result_img, share_test_img;
	private TextView share_result_txt, share_test_txt;
	private ImageView rank_baby_img;
	private TextView rank_baby_name, rank_baby_num, rank_txt;
	private ImageLoader mImageLoader;
	private DisplayImageOptions mOption;
	private boolean is_from_community = false;
	private String child_name;
	private Button related_article, related_task;
	private String associateTag;

	private LinearLayout test_evaluation_header;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_evaluation_activity);

		test_evaluation_header = (LinearLayout) findViewById(R.id.test_evaluation_header);
		if (TuxingApp.VersionType.equals(Constants.VERSION.TEACHER.getVersion())) {
//			test_evaluation_header.setBackgroundColor(getResources().getColor(R.color.teacher_pre_title));
		}

//		associateTag = getIntent().getStringExtra(
//				RelatedTaskActivity.ASSOCIATE_TAG);
		test_id = getIntent().getIntExtra(SubjectActivity.TEST_ID, 0);
//		is_from_community = getIntent().getBooleanExtra(
//				GuideAdapter.IS_FROM_COMMUNITY, false);
//		child_name = getIntent().getStringExtra(GuideAdapter.CHILD_NAME);
		if (StringUtil.isNullOrEmpty(child_name)) {
			child_name = SysConstants.childname;
			// 日记
//			child_name = GuideHelper
//					.getCurrentBaby(TestEvaluationActivity.this);
		}
		evaluation = TestHelper.getEvaluation(TestEvaluationActivity.this);
		test_evaluation_img = (TextView) findViewById(R.id.test_evaluation_img);
		test_evaluation_img.setBackgroundDrawable(ShapeUtil
				.getGradientTopDrawable(TestEvaluationActivity.this,
						R.color.edu_color_2));

		test_evaluation_back_txt = (TextView) findViewById(R.id.test_evaluation_back_txt);
		test_evaluation_back_txt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		repeat_test = (Button) findViewById(R.id.repeat_test);
		repeat_test.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TestHelper.clearAnswers(TestEvaluationActivity.this, test_id);
				Intent intent = new Intent(TestEvaluationActivity.this,
						TestDespActivity.class);
				intent.putExtra(TestSecondActivity.TEST_ID, test_id);
				startActivity(intent);
				finish();
			}
		});

		test_evaluation_desp = (TextView) findViewById(R.id.test_evaluation_desp);
		test_evaluation_desp.setText(evaluation.description);

		share_test_layout = (RelativeLayout) findViewById(R.id.share_test_layout);
		share_test_layout.setVisibility(View.GONE);
//		share_test_layout.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				final String content = ShareHelper.getShareTestContent(
//						TestEvaluationActivity.this, evaluation);
//				final String get_down_url = ShareHelper.getTestDespUrl(
//						TestEvaluationActivity.this, test_id);
//				share(content, get_down_url, true);
//			}
//		});
		share_result_layout = (RelativeLayout) findViewById(R.id.share_result_layout);
		share_result_layout.setVisibility(View.GONE);
//		share_result_layout.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				final String content = ShareHelper.getShareTestResultContent(
//						TestEvaluationActivity.this, evaluation);
//				final String get_down_url = ShareHelper.getTestUrl(
//						TestEvaluationActivity.this, test_id);
//				share(content, get_down_url, false);
//			}
//		});
		share_result_img = (ImageView) findViewById(R.id.share_result_img);
		share_result_txt = (TextView) findViewById(R.id.share_result_txt);
		share_test_img = (ImageView) findViewById(R.id.share_test_img);
		share_test_txt = (TextView) findViewById(R.id.share_test_txt);

		initRank();

		related_article = (Button) findViewById(R.id.related_article);
		related_article.setVisibility(View.GONE);
//		related_article.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(TestEvaluationActivity.this,
//						RelatedArticleActivity.class);
//				intent.putExtra(RelatedTaskActivity.ASSOCIATE_TAG, associateTag);
//				startActivity(intent);
//			}
//		});
		related_task = (Button) findViewById(R.id.related_task);
		related_task.setVisibility(View.GONE);
//		related_task.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(TestEvaluationActivity.this,
//						RelatedTaskActivity.class);
//				intent.putExtra(RelatedTaskActivity.ASSOCIATE_TAG, associateTag);
//				startActivity(intent);
//			}
//		});

		//evaluation.
	}

	private void disableOperate() {
		related_article.setBackgroundColor(getResources().getColor(R.color.grey18));
		related_article.setEnabled(false);
		related_task.setBackgroundColor(getResources().getColor(R.color.grey18));
		related_task.setEnabled(false);
		repeat_test.setVisibility(View.GONE);
		share_result_img.setImageResource(R.drawable.share_result_unclick);
		share_result_txt.setTextColor(getResources().getColor(R.color.grey19));
		share_result_layout.setEnabled(false);
		share_test_img.setImageResource(R.drawable.share_result_unclick);
		share_test_txt.setTextColor(getResources().getColor(R.color.grey19));
		share_test_layout.setEnabled(false);
	}

	private void initRank() {
		rank_baby_name = (TextView) findViewById(R.id.rank_baby_name);
		rank_baby_img = (ImageView) findViewById(R.id.rank_baby_img);
		rank_baby_name.setText(child_name);
		mImageLoader = ImageLoader.getInstance();
		mOption = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.ARGB_8888)
				.showImageForEmptyUri(R.drawable.default_baby_img)
				.showImageOnFail(R.drawable.default_baby_img)
				.displayer(new CircleBitmapDisplayer()).build();
		mImageLoader.displayImage(evaluation.childPic, rank_baby_img, mOption);
		rank_baby_num = (TextView) findViewById(R.id.rank_baby_num);
		rank_txt = (TextView) findViewById(R.id.rank_txt);
		String _txt = child_name + "是第" + evaluation.testSerialNumber
				+ "名参加此测试的用户喔～";
		rank_txt.setText(_txt);
		rank_baby_num.setText(evaluation.testSerialNumber + "");
	}

	private String getRankStr(int total, int rank) {
		float result = 1 - (float) rank / (float) total;
		NumberFormat nt = NumberFormat.getPercentInstance();
		nt.setMinimumFractionDigits(1);
		return nt.format(result);
	}

	private void share(final String content, final String get_down_url,
			final boolean is_new_ver) {
//		shareWindow = new SharePopupWindow(TestEvaluationActivity.this,
//				new View.OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						switch (v.getId()) {
//						case R.id.share_wechat_btn:
//							String w_url = ShareHelper.getNewShareUrl(
//									get_down_url, ShareHelper.WECHAT_CH,
//									is_new_ver);
//							ShareHelper.snsShare(TestEvaluationActivity.this,
//									ShareHelper.WECHAT_SHARE, content, null,
//									w_url, new NetManager.MonitorInterface() {
//										@Override
//										public void getContentFromNet(
//												Object content) {
//											shareWindow.dismiss();
//										}
//									});
//							break;
//						case R.id.share_weibo_btn:
//							String s_url = ShareHelper.getNewShareUrl(
//									get_down_url, ShareHelper.WEIBO_CH,
//									is_new_ver);
//							Log.i(TAG, s_url);
//							ShareHelper.snsShare(TestEvaluationActivity.this,
//									ShareHelper.WEIBO_SHARE, content, null,
//									s_url, new NetManager.MonitorInterface() {
//										@Override
//										public void getContentFromNet(
//												Object content) {
//											shareWindow.dismiss();
//										}
//									});
//							break;
//						case R.id.share_friends_btn:
//							String f_url = ShareHelper.getNewShareUrl(
//									get_down_url, ShareHelper.FRIENDS_CH,
//									is_new_ver);
//							ShareHelper.snsShare(TestEvaluationActivity.this,
//									ShareHelper.FRIENDS_SHARE, content, null,
//									f_url, new NetManager.MonitorInterface() {
//										@Override
//										public void getContentFromNet(
//												Object content) {
//											shareWindow.dismiss();
//										}
//									});
//							break;
//
//						default:
//							break;
//						}
//					}
//				});
//		shareWindow.showAtLocation(TestEvaluationActivity.this
//				.findViewById(R.id.test_evaluation_activity_layout),
//				Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
	}

}
