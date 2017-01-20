package com.tuxing.app.activity;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.VideoView;

import com.tuxing.app.R;
import com.tuxing.app.adapter.SelectVideoAdapter;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.qzq.view.MyRecyclerView;
import com.tuxing.app.util.Utils;
import com.tuxing.sdk.db.entity.VideoInfo;
import com.tuxing.sdk.utils.CollectionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SelectVideoActivity extends BaseActivity implements
		OnClickListener, SelectVideoAdapter.MyItemClickListener{

	private VideoInfo videoInfo;
	private VideoView playView;
	private TextView tv_desc;
	private MyRecyclerView listView;
	private RelativeLayout rl_pause;
	private SelectVideoAdapter mAdapter;
    public Button titleight;
	private List<VideoInfo> listItems = new ArrayList<>();
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
            disProgressDialog();
			if (mAdapter == null) {
				mAdapter = new SelectVideoAdapter(SelectVideoActivity.this,listItems);
				listView.setAdapter(mAdapter);
			} else {
				mAdapter.notifyDataSetChanged();
			}
			tv_desc.setText((listItems.size() - 1) + "个视频  (仅支持少于5分钟的视频)");
            if(!CollectionUtils.isEmpty(listItems) && listItems.size() >= 2){
                videoInfo = listItems.get(1);
                playView.setVideoPath(videoInfo.videoUrl);
                playView.seekTo(200);
                rl_pause.setVisibility(View.VISIBLE);
            }else{
                titleight.setVisibility(View.GONE);
            }
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_select_play);
		listView = (MyRecyclerView) findViewById(R.id.horlist);
		playView = (VideoView) findViewById(R.id.playView);
		tv_desc = (TextView)findViewById(R.id.tv_desc);
		RelativeLayout rl_video = (RelativeLayout) findViewById(R.id.rl_video);
		rl_pause = (RelativeLayout) findViewById(R.id.rl_pause);
        titleight = (Button) findViewById(R.id.tv_title_right);
		rl_pause.setOnClickListener(this);
		rl_video.setOnClickListener(this);
        findViewById(R.id.tv_title_left).setOnClickListener(this);
        titleight.setOnClickListener(this);
		LayoutParams layoutParams = (LayoutParams) rl_video.getLayoutParams();
		layoutParams.width = Utils.getDisplayWidth(getApplicationContext());
		layoutParams.height = Utils.getDisplayWidth(getApplicationContext())*3/4;
		rl_video.setLayoutParams(layoutParams);
		LinearLayoutManager layoutManager = new LinearLayoutManager(SelectVideoActivity.this);
		layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		listView.setLayoutManager(layoutManager);
        listItems.add(new VideoInfo("","",0,0));
		mAdapter = new SelectVideoAdapter(SelectVideoActivity.this,listItems);
		listView.setAdapter(mAdapter);

		mAdapter.setOnClickListener(this);

		playView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
				return true;
			}
		});
		playView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				rl_pause.setVisibility(View.VISIBLE);

			}
		});
		getNoThumbnailImages();
	}

    @Override
    protected void onResume() {
        super.onResume();
        if(videoInfo != null){
            playView.setVideoPath(videoInfo.videoUrl);
            playView.seekTo(200);
            rl_pause.setVisibility(View.VISIBLE);
        }
    }

    public void play(final String videoPath) {
		if (!TextUtils.isEmpty(videoPath) && playView != null) {
			playView.setVideoPath(videoPath);
			playView.start();
			rl_pause.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
        if(v.getId() == R.id.tv_title_left){
            finish();
        }else if(v.getId() == R.id.tv_title_right){
            Intent intent = new Intent(this,VideoCutSecondActivity.class);
            intent.putExtra("videoInfo", videoInfo);
            startActivityForResult(intent,0);
            if(playView != null){
                playView.stopPlayback();
            }
        }else if(v.getId() == R.id.rl_pause){
			if(playView.isPlaying()){
				rl_pause.setVisibility(View.VISIBLE);
				playView.pause();
			}else{
				play(videoInfo.videoUrl);
			}
		}else if(v.getId() == R.id.rl_video){
			if(playView.isPlaying()){
				rl_pause.setVisibility(View.VISIBLE);
				playView.pause();
			}else{
			    if(videoInfo != null){
                    play(videoInfo.videoUrl);
                }else{
                    showToast("数据异常");
                }

			}
		}
	}


	@Override
	public void onItemClick(View view, int position) {
		if(position == 0){
            Intent intent = new Intent(this,PicListActivity.class);
             intent.putExtra("isVideo",true);
			startActivityForResult(intent,0);
		}else{
			if(playView != null && mAdapter != null && mAdapter.clickPositon != position){
				videoInfo = listItems.get(position);
				rl_pause.setVisibility(View.GONE);
				playView.stopPlayback();
				playView.setVisibility(View.GONE);
				play(videoInfo.videoUrl);
				playView.setVisibility(View.VISIBLE);
				mAdapter.clickPositon = position;
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(playView != null && playView.isPlaying()){
			playView.pause();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			setResult(RESULT_OK,data);
			finish();
		}
	}


    /**
	 * 获取手机相册图片
	 */
	private void getNoThumbnailImages() {
        showProgressDialog(mContext,null,true,null);
		new Thread(new Runnable() {

			@Override
			public void run() {
				String sortOrder = MediaStore.Video.Media.DATE_ADDED + " desc";

				Cursor cursor = getContentResolver().query(
						MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null,
						null, null, sortOrder);
				if (cursor != null) {
					if (cursor.moveToFirst()) {
						do {
							String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
							String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
							String img_date = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
							long duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
							/** 取得扩展名 */
							String end = path.substring(path.lastIndexOf(".") + 1, path.length()).toLowerCase();
							if (end.equals("mp4") && duration <= 1000 * 60 * 5 && duration >= 1000*3) {
								if (new File(path).exists()) {
									VideoInfo videoInfo = new VideoInfo(title,path, Long.valueOf(img_date + "000"),duration);
									listItems.add(videoInfo);
								}
							}
						} while (cursor.moveToNext());
					}
					cursor.close();
					mHandler.sendEmptyMessage(0);
				}
			}
		}).start();

	}



}