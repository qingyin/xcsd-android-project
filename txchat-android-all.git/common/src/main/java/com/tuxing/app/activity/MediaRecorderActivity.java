package com.tuxing.app.activity;

import java.io.File;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.util.MyLog;
import com.tuxing.app.util.PhoneUtils;
import com.tuxing.app.util.RoundProgressBar;
import com.tuxing.app.util.Utils;
import com.yixia.camera.MediaRecorderBase;
import com.yixia.camera.MediaRecorderBase.OnErrorListener;
import com.yixia.camera.MediaRecorderBase.OnPreparedListener;
import com.yixia.camera.MediaRecorderNative;
import com.yixia.camera.VCamera;
import com.yixia.camera.model.MediaObject;
import com.yixia.camera.util.DeviceUtils;
import com.yixia.camera.util.FileUtils;
import com.yixia.videoeditor.adapter.UtilityAdapter;

/**
 * 视频录制
 * 
 */
public class MediaRecorderActivity extends BaseActivity implements
		OnErrorListener,  OnPreparedListener,
		MediaRecorderBase.OnEncodeListener {

	/** 录制最长时间 */
	public final static int RECORD_TIME_MAX = 10 * 1000;
	/** 录制最小时间 */
	public final static int RECORD_TIME_MIN = 2 * 1000;
	/** 刷新进度条 */
	private static final int HANDLE_INVALIDATE_PROGRESS = 0;
	/** 延迟拍摄停止 */
	private static final int HANDLE_STOP_RECORD = 1;
	/** 对焦 */
	private static final int HANDLE_HIDE_RECORD_FOCUS = 2;

	/** 对焦图标-带动画效果 */
	private ImageView mFocusImage;
	/** 对焦图片宽度 */
	private int mFocusWidth;
	/** 屏幕宽度 */
	private int mWindowWidth;
	/** 拍摄按钮 */
	private Button mRecordController;

    private TextView mBack;

	/** 底部条 */
	private RelativeLayout mBottomLayout;
	/** 摄像头数据显示画布 */
	private SurfaceView mSurfaceView;
	/** 录制进度 */
	private RoundProgressBar mProgressView;
	/** 对焦动画 */
	private Animation mFocusAnimation;

	/** SDK视频录制对象 */
	private MediaRecorderBase mMediaRecorder;
	/** 视频信息 */
	private MediaObject mMediaObject;
	/** 是否是点击状态 */
	private volatile boolean mPressedStatus;
	/** 是否已经释放 */
	private volatile boolean mReleased;
	private boolean mCreated;
	/** 底部背景色 */
	private int mBackgroundColorNormal, mBackgroundColorPress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCreated = false;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 防止锁屏
		if(TuxingApp.videoErroy){
			Toast.makeText(mContext,R.string.video_erroy,Toast.LENGTH_SHORT).show();
			setResult(RESULT_OK,null);
			finish();
		}else{
            loadIntent();
            loadViews();

			mCreated = true;
		}

	}

	/** 加载传入的参数 */
	private void loadIntent() {
		mWindowWidth = DeviceUtils.getScreenWidth(this);
		mFocusWidth = Utils.dipToPX(this, 64);
		mBackgroundColorNormal = getResources().getColor(R.color.video_black);// camera_bottom_bg
		mBackgroundColorPress = getResources().getColor(
				R.color.video_black);
	}

	/** 加载视图 */
	private void loadViews() {
		setContentView(R.layout.activity_media_recorder);
		mSurfaceView = (SurfaceView) findViewById(R.id.record_preview);
		mProgressView = (RoundProgressBar) findViewById(R.id.record_progress);
		mRecordController = (Button) findViewById(R.id.record_controller);
		mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
		mFocusImage = (ImageView) findViewById(R.id.record_focusing);
		mRecordController.setOnTouchListener(mOnVideoControllerTouchListener);
        mBack = (TextView) findViewById(R.id.recorder_left);
        mBack.setOnClickListener(this);


		// ~~~ 绑定事件
		if (DeviceUtils.hasICS())
			mSurfaceView.setOnTouchListener(mOnSurfaveViewTouchListener);
		
		try {
			mFocusImage.setImageResource(R.drawable.video_focus);
		} catch (OutOfMemoryError e) {
			
		}
		mProgressView.setMax(RECORD_TIME_MAX);
		initSurfaceView();
	}

	/** 初始化画布 */
	private void initSurfaceView() {
		final int w = DeviceUtils.getScreenWidth(this);

		((RelativeLayout.LayoutParams) mBottomLayout.getLayoutParams()).topMargin = w * 3 / 4 +
                DeviceUtils.dipToPX(this, 45);
		int width = w;
		int height = w * 4 / 3;
		//
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mSurfaceView
				.getLayoutParams();
		lp.width = width;
		lp.height = height;
		mSurfaceView.setLayoutParams(lp);
	}

	/** 初始化拍摄SDK */
	private void initMediaRecorder() {
		mMediaRecorder = new MediaRecorderNative();

		mMediaRecorder.setOnErrorListener(this);
		mMediaRecorder.setOnEncodeListener(this);
		File f = new File(VCamera.getVideoCachePath());
		if (!FileUtils.checkFile(f)) {
			f.mkdirs();
		}
		String key = String.valueOf(System.currentTimeMillis());
		mMediaObject = mMediaRecorder.setOutputDirectory(key,
				VCamera.getVideoCachePath() + key);
		mMediaRecorder.setSurfaceHolder(mSurfaceView.getHolder());
		mMediaRecorder.prepare();
	}

	/** 点击屏幕录制 */
	private View.OnTouchListener mOnSurfaveViewTouchListener = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (mMediaRecorder == null || !mCreated) {
				return false;
			}

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// 检测是否手动对焦
				if (checkCameraFocus(event))
					return true;
				break;
			}
			return true;
		}

	};
	
	/** 点击屏幕录制 */
	private View.OnTouchListener mOnVideoControllerTouchListener = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (mMediaRecorder == null) {
				return false;
			}

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (mMediaObject.getDuration() >= RECORD_TIME_MAX || mPressedStatus) {
					return true;
				}
				startRecord();
				break;

			case MotionEvent.ACTION_UP:
				// 暂停
				if (mPressedStatus) {
					stopRecord();
					// 检测是否已经完成
					if (mMediaObject.getDuration() >= RECORD_TIME_MIN) {
						mMediaRecorder.release();
						mMediaRecorder.startEncoding();
					}
				}
				break;
			}
			return true;
		}

	};

	@Override
	public void onResume() {
		super.onResume();

		try {
			UtilityAdapter.freeFilterParser();
			UtilityAdapter.initFilterParser();

			if (mMediaRecorder == null) {
				initMediaRecorder();
			} else {
				File f = new File(VCamera.getVideoCachePath());
				if (!FileUtils.checkFile(f)) {
					f.mkdirs();
				}
				mMediaRecorder.prepare();
				mProgressView.setProgress(0);
			}
		}catch (Throwable t){
			Toast.makeText(mContext,R.string.video_erroy,Toast.LENGTH_SHORT).show();
			MyLog.getLogger(TAG).d("录制视频加载失败" + PhoneUtils.getPhoneType());
		}

	}

	@Override
	public void onPause() {
		super.onPause();

		try {
			stopRecord();
			UtilityAdapter.freeFilterParser();
			if (!mReleased) {
				if (mMediaRecorder != null)
					mMediaRecorder.release();
			}
			mReleased = false;
		}catch (Throwable e){
			Toast.makeText(mContext,R.string.video_erroy,Toast.LENGTH_SHORT).show();
			MyLog.getLogger(TAG).d("录制视频加载失败" + PhoneUtils.getPhoneType());
		}

	}

	/** 手动对焦 */
	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private boolean checkCameraFocus(MotionEvent event) {
		mFocusImage.setVisibility(View.GONE);
		float x = event.getX();
		float y = event.getY();
		float touchMajor = event.getTouchMajor();
		float touchMinor = event.getTouchMinor();

		Rect touchRect = new Rect((int) (x - touchMajor / 2),
				(int) (y - touchMinor / 2), (int) (x + touchMajor / 2),
				(int) (y + touchMinor / 2));
		if (touchRect.right > 1000)
			touchRect.right = 1000;
		if (touchRect.bottom > 1000)
			touchRect.bottom = 1000;
		if (touchRect.left < 0)
			touchRect.left = 0;
		if (touchRect.right < 0)
			touchRect.right = 0;

		if (touchRect.left >= touchRect.right
				|| touchRect.top >= touchRect.bottom)
			return false;

		ArrayList<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
		focusAreas.add(new Camera.Area(touchRect, 1000));
		if (!mMediaRecorder.manualFocus(new Camera.AutoFocusCallback() {

			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				// if (success) {
				mFocusImage.setVisibility(View.GONE);
				// }
			}
		}, focusAreas)) {
			mFocusImage.setVisibility(View.GONE);
		}

		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mFocusImage
				.getLayoutParams();
		int left = touchRect.left - (mFocusWidth / 2);// (int) x -
														// (focusingImage.getWidth()
														// / 2);
		int top = touchRect.top - (mFocusWidth / 2);// (int) y -
													// (focusingImage.getHeight()
													// / 2);
		if (left < 0)
			left = 0;
		else if (left + mFocusWidth > mWindowWidth)
			left = mWindowWidth - mFocusWidth;
		if (top + mFocusWidth > mWindowWidth)
			top = mWindowWidth - mFocusWidth;

		lp.leftMargin = left;
		lp.topMargin = top;
		mFocusImage.setLayoutParams(lp);
		mFocusImage.setVisibility(View.VISIBLE);

		if (mFocusAnimation == null)
			mFocusAnimation = AnimationUtils.loadAnimation(this,
					R.anim.record_focus);

		mFocusImage.startAnimation(mFocusAnimation);

		mHandler.sendEmptyMessageDelayed(HANDLE_HIDE_RECORD_FOCUS, 3500);// 最多3.5秒也要消失
		return true;
	}

	/** 开始录制 */
	private void startRecord() {
		try {
			if (mMediaRecorder != null) {
				MediaObject.MediaPart part = mMediaRecorder.startRecord();
				if (part == null) {
					return;
				}
				mProgressView.setProgress(mMediaObject.getDuration());
			}

			mPressedStatus = true;
			mBottomLayout.setBackgroundColor(mBackgroundColorPress);

			if (mHandler != null) {
				mHandler.removeMessages(HANDLE_INVALIDATE_PROGRESS);
				mHandler.sendEmptyMessage(HANDLE_INVALIDATE_PROGRESS);

				mHandler.removeMessages(HANDLE_STOP_RECORD);
				mHandler.sendEmptyMessageDelayed(HANDLE_STOP_RECORD,
						RECORD_TIME_MAX - mMediaObject.getDuration());
			}

		}catch (Throwable e){
			Toast.makeText(mContext,R.string.video_erroy,Toast.LENGTH_SHORT).show();
			MyLog.getLogger(TAG).d("录制视频加载失败" + PhoneUtils.getPhoneType());
		}

	}

	@Override
	public void onBackPressed() {

		if (mMediaObject != null)
			mMediaObject.delete();
		finish();
	}

	/** 停止录制 */
	private void stopRecord() {
		try {
			mPressedStatus = false;
			mBottomLayout.setBackgroundColor(mBackgroundColorNormal);

			checkStatus();
			if (mMediaRecorder != null) {
				mMediaRecorder.stopRecord();
			}

			mProgressView.setProgress(mMediaObject.getDuration());
			mHandler.removeMessages(HANDLE_STOP_RECORD);
		}catch (Throwable e){
			Toast.makeText(mContext,R.string.video_erroy,Toast.LENGTH_SHORT).show();
			MyLog.getLogger(TAG).d("录制视频加载失败" + PhoneUtils.getPhoneType());
		}

	}



	/** 检查录制时间，显示/隐藏下一步按钮 */
	private int checkStatus() {
		int duration = 0;
		if (!isFinishing() && mMediaObject != null) {
			duration = mMediaObject.getDuration();
			if (duration < RECORD_TIME_MIN) {
				// 视频必须大于2秒
				Toast.makeText(this, "录制时间太短", Toast.LENGTH_SHORT).show();
			} 
		}
		return duration;
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLE_INVALIDATE_PROGRESS:
				if (mMediaRecorder != null && !isFinishing()) {
					if (mProgressView != null)
						mProgressView.setProgress(mMediaObject.getDuration());
					if(mMediaObject.getDuration() >= 10*1000 && mPressedStatus){
							stopRecord();
							mMediaRecorder.release();
							mMediaRecorder.startEncoding();
					}
					if (mPressedStatus)
						sendEmptyMessageDelayed(0, 30);
				}
				break;
			}
		}
	};

	@Override
	public void onEncodeStart() {
		showProgressDialog(mContext,"处理中...",true,null);
	}

	@Override
	public void onEncodeProgress(int progress) {
	}

	/** 转码完成 */
	@Override
	public void onEncodeComplete() {
		 Intent intent = new Intent();
         	intent.putExtra("videoPath",mMediaObject.getOutputTempVideoPath());
		setResult(RESULT_OK,intent);
		disProgressDialog();
         finish();
	}

	/**
	 * 转码失败 检查sdcard是否可用，检查分块是否存在
	 */
	@Override
	public void onEncodeError() {
		Toast.makeText(this, "onEncodeError",
				Toast.LENGTH_SHORT).show();
		disProgressDialog();
		setResult(RESULT_OK, null);
		finish();
	}

	@Override
	public void onConfirm() {
		super.onConfirm();
		setResult(RESULT_OK, null);
		finish();
	}

	@Override
	public void onVideoError(int what, int extra) {
		showDialog(null, getResources().getString(R.string.authority_msg), "", "确定");
	}

	@Override
	public void onAudioError(int what, String message) {
	}

	@Override
	public void onPrepared() {

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		System.out.println("tet");
		if (keyCode == KeyEvent. KEYCODE_BACK ) {
			setResult(RESULT_OK,null);
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		setResult(RESULT_OK,null);
		finish();
		super.onClick(v);
	}
}
