package com.tuxing.app.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.tuxing.app.R;
import com.tuxing.app.adapter.VideoCutMinuteAdapter;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.qzq.view.MyRecyclerView;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.ScrollListenerView;
import com.tuxing.sdk.db.entity.VideoInfo;
import com.yixia.camera.FFMpegUtils;
import com.yixia.camera.model.MediaObject;
import com.yixia.camera.util.DeviceUtils;
import com.yixia.camera.util.FileUtils;
import com.yixia.camera.util.VideoUtils;
import com.yixia.videoeditor.adapter.UtilityAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class VideoCutSecondActivity extends BaseActivity implements OnClickListener {
    private String TAG = VideoCutSecondActivity.class.getSimpleName();
	private VideoInfo videoInfo;
	/** 横向listview */
	private MyRecyclerView recyclerView;
	/** 封面图按钮 */
	private ImageView img_bg;
	/** 阴影色块left */
	private ImageView img_left;
	/** 阴影色块right */
	private ImageView img_right;
	/** 显示时间 */
	private TextView txt_time;
	private TextView starttime;
	/** 封面容器 */
	private RelativeLayout relative;
	/** 进度条 */
	private RelativeLayout relative1;
	/** 视频播放 */
	private VideoView videoView;
	/** 数据集 */
	private ArrayList<String> list;
	/** 列表适配器 */
	private VideoCutMinuteAdapter adapter;
        /** 屏幕宽度 */
        private int width;

        /** 最少多少秒 */
        public static final int MIN_TIME = 3000;
        /** 最大多少秒 */
        public static final int MAX_TIME = 30000;
        /** 屏幕中1像素占有多少毫秒 */
        private float picture = 0;
        /** 多少秒一帧 */
        private float second_Z;

        /** 是否中断线性 */
        private boolean isThread = false;

        /** 左边拖动按钮 */
        private Button txt_left;
        /** 右边拖动按钮 */
        private Button txt_right;

        /** 按下时X抽坐标 */
        private float DownX;

        /** 拖动条容器 */
        private LayoutParams layoutParams_progress;
        /** 阴影背景容器 */
        private LayoutParams layoutParams_yin;
        /** 拖动条的宽度 */
        private int width_progress = 0;
        /** 拖动条的间距 */
        private int Margin_progress = 0;
        /** 阴影框的宽度 */
        private int width1_progress = 0;

        /** 不能超过右边多少 */
        private int right_margin = 0;
        /** 最少保留的多少秒长度 */
        private int last_length = 0;
        /** 左边啦了多少 */
        private int left_lenth = 0;
        /** 滚动的长度 */
        private int Scroll_lenth = 0;
        private boolean isPlay = false;
        private int seekProgress = 0;
        private TextView seek;
        private TextView tv_total;
        private TextView tv_bottom;
        private MediaMetadataRetriever retriever;
        private String videoPath;
		private int video_width = 0;
		private int video_height = 0;
        private int rotation = -1;//视频的旋转角度
        private VideoUtils videoUtils;
        private MediaObject mMediaObject;
        private Button title_right;
        private TextView tv_video_msg;
        private int compressProgess = 0;
        private HorizontalScrollView scroll_hor;
        private ScrollListenerView scroll;
        private boolean isTranslationY = true;  // true 垂直  false 水平
        private RelativeLayout progressRl;
        private ImageView progressIv;
        private TextView progressTv;

        private Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    adapter.notifyItemInserted(msg.arg1);
                } else if (msg.what == 2) {

				/* 所有图片长度 */
                    int img_widthAll = (int) (msg.arg1 * 1000 / picture);

                    last_length = (int) (MIN_TIME / picture);

                    if (img_widthAll < width) {
                        right_margin = width - img_widthAll;
                        LayoutParams layoutParams_right = (LayoutParams) img_right.getLayoutParams();
                        layoutParams_right.width = width - img_widthAll;
                        img_right.setLayoutParams(layoutParams_right);

                        layoutParams_progress = (LayoutParams) relative1.getLayoutParams();
                        layoutParams_progress.width = img_widthAll;
                        layoutParams_progress.rightMargin = width - img_widthAll;
                        relative1.setLayoutParams(layoutParams_progress);

                        txt_time.setText(Utils.getDuration(msg.arg1 * 1000));
                    } else {
                        layoutParams_progress = (LayoutParams) relative1.getLayoutParams();
                        layoutParams_progress.width = width;
                        relative1.setLayoutParams(layoutParams_progress);

                        txt_time.setText(Utils.getDuration(MAX_TIME));
                    }
                    tv_total.setText(Utils.getDuration(videoInfo.duration));
                    videoView.setVideoPath(videoInfo.videoUrl);
                    videoView.seekTo(200);
                }else if(msg.what == 3){
                    progressTv.setText(getString(R.string.video_progress) + "100");
                    handler.removeCallbacks(runnable2);
                    mMediaObject.delete();
                    Intent intent = new Intent();
                    intent.putExtra("videoPath", videoPath);
                    intent.putExtra("createOn",videoInfo.createOn);
                    setResult(RESULT_OK,intent);
                    stopProgress();
                    finish();
                }else if(msg.what == 4){
                    handler.removeCallbacks(runnable2);
                    stopProgress();
                    title_right.setClickable(true);
                    String str = (String) msg.obj;
                    showToast(str);
                }else if(msg.what == 5){
                    String msgss =  String.valueOf(compressProgess++) + "%";
                    progressTv.setText(getString(R.string.video_progress) + msgss);
                    if(video_width < 1280 && video_height < 720){
                        sendRun2(0);
                    }else if(video_width == 1280 && video_height == 720){
                        sendRun2(1);
                    }else if(video_width > 1280 && video_height > 720){
                        sendRun2(2);
                    }else{
                        handler.postDelayed(runnable2, 300);
                    }

                }
            }
        };

        public void sendRun2(int flag){
            String[] time = txt_time.getText().toString().split(":");
            int intTime = Integer.valueOf(time[1]);
            if(intTime >= 20){
                if(flag == 0){
                    handler.postDelayed(runnable2, 170);
                }else if(flag == 1){
                    handler.postDelayed(runnable2, 430);
                }else if(flag == 2){
                    handler.postDelayed(runnable2, 930);
                }

            }else{
                if(flag == 0){
                    handler.postDelayed(runnable2, 110);
                }else if(flag == 1){
                    handler.postDelayed(runnable2, 130);
                }else if(flag == 2){
                    handler.postDelayed(runnable2, 260);
                }
            }
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            videoUtils = new VideoUtils();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            setContentView(R.layout.activity_video_cur_second);
            videoInfo = (VideoInfo)getIntent().getSerializableExtra("videoInfo");
            retriever = new MediaMetadataRetriever();
			retriever.setDataSource(videoInfo.videoUrl);
            video_width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));//宽
            video_height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));//高
            if (DeviceUtils.hasJellyBeanMr1()) {
                try {
                    rotation = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
                } catch (NumberFormatException e) {
                    rotation = -1;
                }
            } else {
                rotation = UtilityAdapter.VideoGetMetadataRotate(videoInfo.videoUrl);
            }
            findViews();
            widgetListener();
            init();

        }

    @Override
    protected void onResume() {
        super.onResume();
        if(videoInfo != null){
            videoView.setVideoPath(videoInfo.videoUrl);
            videoView.seekTo(200);
            img_bg.setVisibility(View.VISIBLE);
        }
    }

    protected void findViews() {
        recyclerView = (MyRecyclerView) findViewById(R.id.recyclerview_horizontal);
        videoView = (VideoView) findViewById(R.id.video_new_cut_videoview);
        img_bg = (ImageView) findViewById(R.id.video_new_cut_img_bg);
        img_left = (ImageView) findViewById(R.id.video_new_cut_img_left);
        img_right = (ImageView) findViewById(R.id.video_new_cut_img_right);
        txt_time = (TextView) findViewById(R.id.video_new_cut_txt_time);
        starttime = (TextView) findViewById(R.id.video_new_cut_start_time);
        tv_total = (TextView)findViewById(R.id.tv_total);
        tv_bottom = (TextView)findViewById(R.id.tv_bottom);
        tv_video_msg = (TextView) findViewById(R.id.tv_video_msg);
        title_right = (Button) findViewById(R.id.tv_title_right);
        seek = (TextView) findViewById(R.id.seekbar_icon);
        scroll_hor = (HorizontalScrollView) findViewById(R.id.scroll_hor);
        scroll= (ScrollListenerView) findViewById(R.id.scroll);
        relative1 = (RelativeLayout) findViewById(R.id.video_new_cut_relative1);
        txt_left = (Button) findViewById(R.id.video_new_cut_txt_left);
        txt_right = (Button) findViewById(R.id.video_new_cut_txt_right);
        progressTv = (TextView)findViewById(R.id.progress_msg);
        progressIv = (ImageView) findViewById(R.id.progress_iv);
        progressRl = (RelativeLayout) findViewById(R.id.progress_rl);
        RelativeLayout rl_receive = (RelativeLayout) findViewById(R.id.rl_receive);
        title_right.setOnClickListener(this);
        progressRl.setOnClickListener(this);
        tv_bottom.setOnClickListener(this);
        findViewById(R.id.rl_time).setOnClickListener(this);
        findViewById(R.id.tv_title_left).setOnClickListener(this);

        tv_right.setSelected(false);

        width = Utils.getDisplayWidth(getApplicationContext());

        if(rotation > 0){
            if((float)video_height / (float)video_width >(float)4 / (float)3){
                isTranslationY = false;
                tv_video_msg.setText("左右拖动视频调整裁剪区域");
            }else if((float)video_height / (float)video_width <(float)4 / (float)3){
                isTranslationY = true;
                tv_video_msg.setText("上下拖动视频调整裁剪区域");
            }else{
                tv_video_msg.setText("");
            }
        }else{
            if((float)video_width / (float)video_height >(float)4 / (float)3){
                isTranslationY = false;
                tv_video_msg.setText("左右拖动视频调整裁剪区域");
            }else if((float)video_width / (float)video_height <(float)4 / (float)3){
                isTranslationY = true;
                tv_video_msg.setText("上下拖动视频调整裁剪区域");
            }else{
                tv_video_msg.setText("");
            }
        }

        if(isTranslationY){
            LayoutParams scroParams = new LayoutParams(LayoutParams.MATCH_PARENT,width*3/4 + Utils.dip2px(getApplicationContext(), 44));
            scroll.setLayoutParams(scroParams);
            scroll_hor.setVisibility(View.GONE);
            videoView = (VideoView) findViewById(R.id.scroll_video);
            relative = (RelativeLayout) findViewById(R.id.scroll_rl);
        }else{
            scroll.setVisibility(View.GONE);
            videoView = (VideoView) findViewById(R.id.scroll_hor_video);
            relative = (RelativeLayout) findViewById(R.id.scroll_hor_rl);
        }
        relative.setOnClickListener(this);

        initVideoLayout(rotation,isTranslationY);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, Utils.dip2px(getApplicationContext(), 68));
        params.setMargins(0,width*3/4 + Utils.dip2px(getApplicationContext(), 130),0,0);
        rl_receive.setLayoutParams(params);

        LayoutParams params1 = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        params1.setMargins(0,width*3/4 + Utils.dip2px(getApplicationContext(), 80),0,0);
        params1.addRule(RelativeLayout.CENTER_HORIZONTAL);
        tv_video_msg.setLayoutParams(params1);


        LayoutParams params2 = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        params2.setMargins(0,width*3/4 + Utils.dip2px(VideoCutSecondActivity.this, 44),0,0);
        tv_bottom.setLayoutParams(params2);

        LayoutParams params3 = (LayoutParams)img_bg.getLayoutParams();
        params3.height = width*3/4;
        params3.setMargins(0, Utils.dip2px(VideoCutSecondActivity.this, 44),0,0);
        params3.addRule(RelativeLayout.CENTER_IN_PARENT);
        img_bg.setLayoutParams(params3);


        // 创建一个线性布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        list = new ArrayList<>();
        adapter = new VideoCutMinuteAdapter(list);
    }

    protected void init() {
        File file = new File(SysConstants.FILE_TEMP_DIR_ROOT);
        if (!file.exists()) {
            file.mkdirs();
        }
        recyclerView.setAdapter(adapter);

        videoView.setVideoPath(videoInfo.videoUrl);
        videoView.requestFocus();

        /** 一个屏幕1像素是多少毫秒 13.88888 */
        picture = (float) MAX_TIME / (float) width;
        second_Z = (float) MAX_TIME / 1000f / ((float) width / (float) Utils.dip2px(VideoCutSecondActivity.this, 60));

        getBitmapsFromVideo((int) videoInfo.duration);
    }


    // 初始化播放布局
    public void initVideoLayout(int rotation , boolean isY){
        LayoutParams layoutParams = (LayoutParams) videoView.getLayoutParams();
        if(rotation > 0){
            if(isY){
                layoutParams.width = width;
                int h = layoutParams.width * video_width / video_height;
                layoutParams.height = h;
            }else{
                layoutParams.height = width*3/4;
                int w = video_height * layoutParams.height / video_width;
                layoutParams.width = w;
            }
        }else{
            if(isY){
                layoutParams.width = width;
                int h = layoutParams.width * video_height / video_width;
                layoutParams.height = h;
            }else{
                layoutParams.height = width*3/4;
                int w = video_width * layoutParams.height / video_height;
                layoutParams.width = w;
            }
        }
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        videoView.setLayoutParams(layoutParams);
        videoView.getHolder().setFixedSize(layoutParams.width,layoutParams.height);
        relative.setTranslationY(Utils.dip2px(VideoCutSecondActivity.this, 44));

    }



    /**
     * 获取视频帧图片
     */
    public void getBitmapsFromVideo(final int lenth) {
        new Thread(new Runnable() {

            @SuppressLint("NewApi")
            @Override
            public void run() {

                // 取得视频的长度(单位为秒)
                int seconds = lenth / 1000;

                Message message = handler.obtainMessage();
                message.what = 2;
                message.arg1 = seconds;
                handler.sendMessage(message);
                Bitmap bitmap;
                // 得到每一秒时刻的bitmap比如第一秒,第二秒
                int index = 0;
                for (float f = second_Z; f <= (float) seconds; f += second_Z) {

                    if (isThread) {
                        return;
                    }
                    bitmap = retriever.getFrameAtTime((long) (f * 1000 * 1000), MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

                    String path = SysConstants.FILE_TEMP_DIR_ROOT + System.currentTimeMillis() + ".jpg";
                    FileOutputStream fos;
                    try {
                        fos = new FileOutputStream(path);
                        bitmap.compress(CompressFormat.JPEG, 80, fos);
                        fos.close();

                        list.add(path);
                        Message message1 = handler.obtainMessage();
                        message1.what = 1;
                        message1.arg1 = index;
                        handler.sendMessage(message1);
                        index++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    protected void widgetListener() {

		/** 左边拖动按钮 */
		txt_left.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						DownX = event.getRawX();

						layoutParams_progress = (LayoutParams) relative1.getLayoutParams();
						layoutParams_yin = (LayoutParams) img_left.getLayoutParams();

						width_progress = layoutParams_progress.width;
						Margin_progress = layoutParams_progress.leftMargin;
						width1_progress = layoutParams_yin.width;

						break;
					case MotionEvent.ACTION_MOVE:

						LeftMoveLayout(event.getRawX() - DownX);
						sendVideo();
						break;
					case MotionEvent.ACTION_UP:

						sendVideo();

						layoutParams_progress = null;
						layoutParams_yin = null;

						break;
					default:
						break;
				}
				return false;
			}
		});

		/** 右边拖动按钮 */
		txt_right.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						DownX = event.getRawX();

						layoutParams_progress = (LayoutParams) relative1.getLayoutParams();
						layoutParams_yin = (LayoutParams) img_right.getLayoutParams();

						width_progress = layoutParams_progress.width;
						Margin_progress = layoutParams_progress.rightMargin;
						width1_progress = layoutParams_yin.width;

						break;
					case MotionEvent.ACTION_MOVE:

						RightMoveLayout(DownX - event.getRawX());
						sendVideo();
						break;
					case MotionEvent.ACTION_UP:
						layoutParams_progress = null;
						layoutParams_yin = null;
						break;

					default:
						break;
				}
				return false;
			}
		});
		/** 视频播放完回调 */
		videoView.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				isPlay = false;
				img_bg.setVisibility(View.VISIBLE);
				handler.removeCallbacks(runnable);
				handler.removeCallbacks(runnable1);
			}
		});

		/** 滚动监听 */
		recyclerView.setOnItemScrollChangeListener(new MyRecyclerView.OnItemScrollChangeListener() {

			@Override
			public void onChange(View view, int position) {
				Scroll_lenth = position * view.getWidth() - view.getLeft();

				if (Scroll_lenth <= 0) {
					Scroll_lenth = 0;
				}
				sendVideo();
			}

			@Override
			public void onChangeState(int state) {
				if (state == 0) {// 静止情况时候才调用
					sendVideo();
				}
			}
		});
	}



	/**
	 * 向右边啦
	 */
	private void LeftMoveLayout(float MoveX) {
		if (layoutParams_progress != null && layoutParams_yin != null) {
			if (Margin_progress + (int) MoveX > 0 && width_progress - (int) MoveX > last_length) {
				layoutParams_progress.width = width_progress - (int) MoveX;
				layoutParams_progress.leftMargin = Margin_progress + (int) MoveX;
				layoutParams_yin.width = width1_progress + (int) MoveX;

				relative1.setLayoutParams(layoutParams_progress);
				img_left.setLayoutParams(layoutParams_yin);
				long end = (long)(Math.round(layoutParams_progress.width * picture / 1000));
				txt_time.setText(Utils.getDuration(end * 1000));

				left_lenth = layoutParams_yin.width;
				starttime.setText(Utils.getDuration((long) ((Scroll_lenth + left_lenth) * picture)) );
			}
		}
	}

	/**
	 * 向左边拉
	 */
	private void RightMoveLayout(float MoveX) {
		if (layoutParams_progress != null && layoutParams_yin != null) {
			if (Margin_progress + (int) MoveX > right_margin && width_progress - (int) MoveX > last_length) {
				layoutParams_progress.width = width_progress - (int) MoveX;
				layoutParams_progress.rightMargin = Margin_progress + (int) MoveX;
				layoutParams_yin.width = width1_progress + (int) MoveX;
				long end = (long)(Math.round(layoutParams_progress.width * picture / 1000));
				txt_time.setText(Utils.getDuration(end * 1000));
				relative1.setLayoutParams(layoutParams_progress);
				img_right.setLayoutParams(layoutParams_yin);
			}
		}
	}

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {

			if (videoView.isPlaying()) {
				isPlay = false;
				videoView.pause();
                img_bg.setVisibility(View.VISIBLE);
			}
		}
	};

	private Runnable runnable1 = new Runnable() {

		@Override
		public void run() {
			if (videoView.isPlaying() && isPlay) {
				seek.setVisibility(View.VISIBLE);
				handler.postDelayed(runnable1, 1000);
				int cut_time = Integer.valueOf(txt_time.getText().toString().split(":")[1]);
				int aa = (layoutParams_progress.width - 102)/cut_time;
				seekProgress += aa;
				seek.setTranslationX(seekProgress);
			}else{
				seek.setVisibility(View.GONE);
				handler.removeCallbacks(runnable1);
			}
		}
	};

    private Runnable runnable2 = new Runnable() {

        @Override
        public void run() {
            if (compressProgess <= 98) {
               Message msg = new Message();
                msg.what = 5;
                handler.sendMessage(msg);
            }
        }
    };

	long createOn;
	private Runnable runnable3 = new Runnable() {
		@Override
		public void run() {
		    if(new File(SysConstants.FILE_TEMP_DIR_ROOT).exists()){

			try {
				layoutParams_progress = (LayoutParams) relative1.getLayoutParams();
				String start =  "00:"  + Utils.getDuration((long) Math.floor((Scroll_lenth + left_lenth) * picture));
                String lenth ="00:" + txt_time.getText().toString();
                //创建一个视频对象
                createOn = System.currentTimeMillis();
                mMediaObject = videoUtils.setOutputDirectory(Long.toString(createOn), SysConstants.FILE_TEMP_DIR_ROOT+createOn);
                mMediaObject.setData(videoInfo.videoUrl);
//                if(tv_right.isSelected()){ // true 4:3 false 原比例
                    width_new = video_width;
                    height_new = video_height;
                    if(video_width>(video_height*4/3)){
                        height_new = video_height;
                        width_new = video_height*4/3;
                    }else if(video_width<=(video_height*4/3)){
                        height_new = video_width*3/4;
                        width_new = video_width;
                    }

                    boolean isSuccess;
                    if(isTranslationY){
                        isSuccess = FFMpegUtils.importVideo(mMediaObject, video_width, video_height, width_new, height_new, start, lenth, true, 0, scroll.getScrollY());

                    }else{
                        isSuccess = FFMpegUtils.importVideo(mMediaObject, video_width, video_height, width_new, height_new, start, lenth, true, scroll_hor.getScrollX(), 0);
                    }
                    if(isSuccess){
                        boolean isTransSuccess = FFMpegUtils.videoTranscoding(mMediaObject, mMediaObject.getOutputVideoPath());
                        if(isTransSuccess){
                            videoPath = mMediaObject.getOutputVideoPath();
                            Message msg = new Message();
                            msg.what = 3;
                            handler.sendMessage(msg);
                        }else{
                            Message msg = new Message();
                            msg.what = 4;
                            msg.obj = "视频压缩失败";
                            handler.sendMessage(msg);
                            showAndSaveLog(TAG,"视频压缩失败  msg = 视频压缩失败",false) ;
                        }
                    }else{
                        Message msg = new Message();
                        msg.what = 4;
                        msg.obj = "视频裁剪失败";
                        handler.sendMessage(msg);
                        showAndSaveLog(TAG,"视频裁剪失败  msg = 视频裁剪失败",false) ;
                    }
			} catch (Exception e) {
                Message msg = new Message();
                msg.what = 4;
                msg.obj = "视频处理失败";
                handler.sendMessage(msg);
                showAndSaveLog(TAG,"剪裁失败  msg = " + e.toString(),false) ;
			}
            }else{
                Message msg = new Message();
                msg.what = 4;
                msg.obj = "视频处理失败 缓存文件夹不存在或被删除,清理后台运行的应用后再重启微家园";
                handler.sendMessage(msg);
                showAndSaveLog(TAG,"缓存文件夹不存在或被删除",false) ;
            }
		}
	};
    int width_new = 0;
    int height_new = 0;
	/**
	 * 移动起始播放位置
	 */
	private void sendVideo() {
		if (!videoView.isShown()) {
			videoView.setVisibility(View.VISIBLE);
		}
		if (videoView.isPlaying()) {
			isPlay = false;
			videoView.pause();
		}
		if (!img_bg.isShown()) {
			img_bg.setVisibility(View.VISIBLE);
		}
		seek.setVisibility(View.GONE);

		handler.removeCallbacks(runnable);
		starttime.setText(Utils.getDuration((long) ((Scroll_lenth + left_lenth) * picture)));
		videoView.seekTo((int) ((Scroll_lenth + left_lenth) * picture));
	}

	/**
	 * 移动起始播放位置
	 */
	private void sendVideo(int lenth) {
		if (!videoView.isShown()) {
			videoView.setVisibility(View.VISIBLE);
		}
		if (videoView.isPlaying()) {
			isPlay = false;
			videoView.pause();
		}
		if (!img_bg.isShown()) {
			img_bg.setVisibility(View.VISIBLE);
		}
		handler.removeCallbacks(runnable);
		videoView.seekTo((int) (lenth * picture));
	}


	/**
	 * 退出时删除临时文件
	 */
	private void deleteFile() {
        for (String aList : list) {
            File file = new File(aList);
            if (file.exists()) {
                file.delete();
            }
        }
	}

	@Override
	public void onClick(View v) {

        if(v.getId() == R.id.tv_title_left){
            finish();
        }else if(v.getId() == R.id.tv_title_right){
            //检测磁盘空间
            if (FileUtils.showFileAvailable() < 200) {
                Toast.makeText(this, R.string.record_camera_check_available_faild, Toast.LENGTH_SHORT).show();
                return;
            }
            title_right.setClickable(false);
            showProgress();
            compressProgess = 0;
//            progressText = getDialogText();
            handler.post(runnable2);
			new Thread(runnable3).start();
        }else if(v.getId() == R.id.scroll_rl || v.getId() == R.id.scroll_hor_rl){
			if (videoView.isPlaying()) {
				img_bg.setVisibility(View.VISIBLE);
				videoView.pause();
				seek.setVisibility(View.GONE);
				handler.removeCallbacks(runnable);
				handler.removeCallbacks(runnable1);

			} else {
				videoView.setVisibility(View.VISIBLE);
				sendVideo();
				videoView.start();
				isPlay = true;
				img_bg.setVisibility(View.GONE);
				layoutParams_progress = (LayoutParams) relative1.getLayoutParams();
				seekProgress =  img_left.getLayoutParams().width + 44;
				seek.setTranslationX(seekProgress);
				seek.setVisibility(View.VISIBLE);
				// 会误差 200-800毫秒
				handler.postDelayed(runnable, (long) (layoutParams_progress.width * picture));
				handler.postDelayed(runnable1, 1000);
			}
		}
	}
    AnimationDrawable spinner;
	public void showProgress(){
	    progressRl.setVisibility(View.VISIBLE);
         spinner = (AnimationDrawable) progressIv.getBackground();
        spinner.start();
        progressTv.setText(getString(R.string.video_progress) + compressProgess++ + "%");

    }

    private void stopProgress(){
        if(spinner != null){
            spinner.stop();
        }
        progressRl.setVisibility(View.GONE);
    }


	@Override
	public void finish() {
	    if(progressRl.getVisibility() != View.VISIBLE){
            isThread = true;
            isPlay = false;
            deleteFile();
            if(videoView != null){
                videoView.stopPlayback();
            }
            super.finish();
        }
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
                finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
