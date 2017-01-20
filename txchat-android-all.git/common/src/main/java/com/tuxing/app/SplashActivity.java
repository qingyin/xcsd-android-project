package com.tuxing.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.sdk.utils.Constants;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

/**
 */
public class SplashActivity extends BaseActivity {


	public static final String TAG = SplashActivity.class.getSimpleName();

	private AlphaAnimation startAnima;
	View view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = View.inflate(this, R.layout.layout_splash, null);
		setContentView(view);
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(mContext);
		AnalyticsConfig.enableEncrypt(true);
		initData();
	}

	private void initData() {
		startAnima = new AlphaAnimation(0.3f, 1.0f);
		startAnima.setDuration(500);
		view.startAnimation(startAnima);
		startAnima.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
//				redirectTo();
				new AsyncTask<Void, Void, Void>() {
					protected void onPostExecute(Void result) {
						super.onPostExecute(result);
						redirectTo();
					}

					@Override
					protected Void doInBackground(Void... params) {
						return null;
					}
				}.execute();
			}
		});
	}

	private void redirectTo() {
		//todo
		if (user != null) {//自动登录

			//user.type  1 小孩 2 家长 3 教师  Constants.USER_TYPE.CHILD
			if ((user.getType() == Constants.USER_TYPE.CHILD ||
					user.getType()== Constants.USER_TYPE.PARENT)
					&& user.getRelativeType() == null) {
				//需要绑定小孩
				startActivity(new Intent(SplashActivity.this, LoginActivity.class));
				finish();
			} else {
				startActivity(new Intent(SplashActivity.this, MainActivity.class));
				finish();
			}
		} else {
			startActivity(new Intent(SplashActivity.this, LoginActivity.class));
			finish();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
