package com.tuxing.app.activity;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.view.ViewGroup;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.*;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.tuxing.app.R;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.easemob.util.ImageCache;
import com.tuxing.app.util.*;
import com.tuxing.app.view.MyImageView;
import android.widget.VideoView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用播放界面
 * 
 *
 */
public class VideoPlayerActivity extends BaseActivity implements
		OnClickListener {

	/** 播放控件 */
	private VideoView playView;
	private RelativeLayout loadingLayout;
	private TextView progressBar;
	private String localFilePath;
	/** 是否需要回复播放 */
	private boolean mNeedResume;
	private int flag; // 1 qzq   2 msg
	private String filePath;
	private MyImageView image;
	DownloadVideoTask downToask;
	int screenWidth;
	int screenHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 防止锁屏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.activity_video_player);
		playView = (VideoView) findViewById(R.id.playView);
		findViewById(R.id.root).setOnClickListener(this);

		 screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		 screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		loadingLayout = (RelativeLayout) findViewById(R.id.loading_layout);
		image = (MyImageView)findViewById(R.id.iv_back);
		progressBar = (TextView) findViewById(R.id.progressBar);
		localFilePath = getIntent().getStringExtra("localpath");
		String remotepath = getIntent().getStringExtra("remotepath");
		String secret = getIntent().getStringExtra("secret");
		String localThumb = getIntent().getStringExtra("localThumb");

		flag = getIntent().getIntExtra("flag", 0);
		if (localFilePath != null && new File(localFilePath).exists()) {
			play(localFilePath);
		} else if (!TextUtils.isEmpty(remotepath) && !remotepath.equals("null")) {

			if(flag == 2){
				// 消息视频
				Bitmap bitmap = ImageCache.getInstance().get(localThumb);
				if (bitmap != null){
					RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,screenWidth*3/4);
					lp.setMargins(0, 0, 0, 0);
					image.setLayoutParams(lp);
					image.setImageBitmap(bitmap);
				}
				Map<String, String> maps = new HashMap<String, String>();
				if (!TextUtils.isEmpty(secret)) {
					maps.put("share-secret", secret);
				}
				downloadVideo(remotepath, maps);
			}else{
				// qzq 视频
				downVideo(remotepath);
			}

		}

	}

	public  void play(final String videoPath){
		if(!TextUtils.isEmpty(videoPath)){
			playView.setVideoPath(videoPath);
			playView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
					MyLog.getLogger(TAG).d("播放视频失败  fiePath =" + videoPath);
					return false;
				}
			});
			playView.start();
			playView.setOnCompletionListener(new android.media.MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(android.media.MediaPlayer mediaPlayer) {
					playView.start();
				}
			});
		}else{
			MyLog.getLogger(TAG).d("未找到该视频");
			showToast("未找到该视频");
		}



	}


	@Override
	public void finish() {
		if (playView != null) {
			playView.stopPlayback();
			playView = null;
		}
		super.finish();
	}

	@Override
	protected void onDestroy() {
		if (playView != null) {
			playView.stopPlayback();
			playView = null;
		}
		super.onDestroy();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		switch (event.getKeyCode()) {// 跟随系统音量走
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_VOLUME_UP:
			break;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onClick(View v) {
			if(v.getId() == R.id.root){
				finish();
			}else if(v.getId() == R.id.playView){
				finish();
			}

	}


	/**
	 * 下载视频文件
	 */
	private void downloadVideo(final String remoteUrl,
							   final Map<String, String> header) {


		localFilePath =  SysConstants.FILE_DIR_VIDEO + Utils.getVideoToken(remoteUrl);;

		if (new File(localFilePath).exists()) {
			play(localFilePath);
			return;
		}
		loadingLayout.setVisibility(View.VISIBLE);

		EMCallBack callback = new EMCallBack() {

			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						loadingLayout.setVisibility(View.GONE);
						progressBar.setText("0%");
						play(localFilePath);
					}
				});
			}

			@Override
			public void onProgress(final int progress,String status) {
				Log.d("ease", "video progress:" + progress);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						progressBar.setText(progress + "%");
					}
				});

			}

			@Override
			public void onError(int error, String msg) {
				Log.e("###", "offline file transfer error:" + msg);
				File file = new File(localFilePath);
				if (file.exists()) {
					file.delete();
				}
			}
		};

		EMChatManager.getInstance().downloadFile(remoteUrl, localFilePath, header, callback);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ( keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public void downVideo(String remotepath){
		filePath = SysConstants.FILE_DIR_VIDEO + Utils.getVideoToken(remotepath);
		if (new File(filePath).exists()) {
			play(filePath);
			return;
		}
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,screenWidth*3/4);
		lp.setMargins(0, 0, 0, 0);
		image.setLayoutParams(lp);
		image.setImageUrl(remotepath + SysConstants.VIDEOSUFFIX306, 0, true);
		beginDownload(remotepath, filePath);
	}

	private void beginDownload( final String key, final String filePath) {
		// TODO 去网络加载视频
		 downToask = (DownloadVideoTask) new DownloadVideoTask(key, filePath,
				new DownloadTaskListener() {

					@Override
					public void onStartDownload() {
						// TODO Auto-generated method stub
						loadingLayout.setVisibility(View.VISIBLE);
					}

					@Override
					public void onProgress(long current, long total) {
						progressBar.setText((current * 100 / total) + "%");

					}

					@Override
					public void onFinished(int resultCode, String filePath) {
						if (resultCode == 1) {
							final File file = new File(filePath);
							if (file.exists()) {
								play(filePath);
							}
							MyLog.getLogger(TAG).d(
									"下载视频成功  filePath= " + filePath);
							loadingLayout.setVisibility(View.GONE);
						} else {
							Toast.makeText(VideoPlayerActivity.this,
									R.string.net_slowly, Toast.LENGTH_SHORT)
									.show();
							loadingLayout.setVisibility(View.GONE);
							MyLog.getLogger(TAG).d("下载视频失败  filePath= " + filePath);


						}

					}
				}).execute();
	}

	@Override
	protected void onPause() {

		if (downToask != null) {
			downToask.cancel(true);
		}
		super.onPause();
	}


}
