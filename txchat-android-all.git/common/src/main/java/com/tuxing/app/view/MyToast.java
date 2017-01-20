package com.tuxing.app.view;

import android.content.Context;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;
import com.tuxing.app.util.DownloadTaskListener;

/**
 * 方角角图片视图
 * 
 * 
 */
public class MyToast extends ImageView implements DownloadTaskListener {
	public MyToast(Context context) {
		super(context);
	}

	private static Toast mToast;
	private static Handler mHandler = new Handler();
	private static Runnable r = new Runnable() {
		public void run() {
			mToast.cancel();
		}
	};

	public static void showToast(Context mContext, String text, int duration) {

		mHandler.removeCallbacks(r);
		if (mToast != null)
			mToast.setText(text);
		else
			mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
		mHandler.postDelayed(r, duration);

		mToast.show();
	}

	public static void showToast(Context mContext, int resId, int duration) {
		showToast(mContext, mContext.getResources().getString(resId), duration);
	}

	@Override
	public void onStartDownload() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProgress(long current, long total) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFinished(int resultCode, String filePath) {
		// TODO Auto-generated method stub

	}

}
