package com.tuxing.app.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tuxing.app.R;
import com.tuxing.app.adapter.ClassRoomCatalogAdapter;
import com.tuxing.app.adapter.ClassRoomCommentAdapter;
import com.tuxing.app.adapter.ViewPagerAdapter;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.services.EducAudioService;
import com.tuxing.app.util.ImageUtils;
import com.tuxing.app.util.MyLog;
import com.tuxing.app.util.PhoneUtils;
import com.tuxing.app.util.PreferenceUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.UmengData;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.CustomDialog;
import com.tuxing.app.view.ExpandTextView;
import com.tuxing.app.view.IndicatorLayout;
import com.tuxing.app.view.MyImageView;
import com.tuxing.rpc.proto.Comment;
import com.tuxing.rpc.proto.Course;
import com.tuxing.rpc.proto.CourseComment;
import com.tuxing.rpc.proto.CourseLesson;
import com.tuxing.sdk.db.entity.HomeWorkRecord;
import com.tuxing.sdk.event.DataReportEvent;
import com.tuxing.sdk.event.course.CourseCommentEvent;
import com.tuxing.sdk.event.course.CourseEvent;
import com.tuxing.sdk.event.course.CourseLessonEvent;
import com.tuxing.sdk.utils.CollectionUtils;
import com.umeng.analytics.MobclickAgent;
import com.xcsd.rpc.proto.EventType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.utils.StringUtils;
import io.vov.vitamio.widget.MediaControllerListener;
import io.vov.vitamio.widget.MediaPlayerControl;
import io.vov.vitamio.widget.VideoView;
import me.maxwin.view.XListView;

/**
 * 课程资源播放
 */
public class ClassRoomPlayerActivity extends BaseActivity implements MediaControllerListener,
        XListView.IXListViewListener, View.OnTouchListener, AdapterView.OnItemClickListener, IndicatorLayout.OnIndicatorChangeListener {

//    private String vidioUrl = "http://7xpklk.media1.z0.glb.clouddn.com/UAA-4hndfVc5V6DJX0EvslAUBBI=/lrtOjdwzdZ8rPq_1hlF93uGK3JX3";
    private TextView title;
    private TextView title_back;
    private XListView catalogListView;
    private XListView commentListView;
    private RelativeLayout rl_title;
    private RelativeLayout ll_bottom_view;
    private ClassRoomCatalogAdapter catalogAdapter;
    private ClassRoomCommentAdapter commentAdapter;
    private boolean hasMore = false;
    private boolean commentHasMore = false;
    private CourseLesson currentResource;
    //    private HomeWorkRecord currentResource;
    private int currentType = 0; // 0 简介  1 目录 2 评论
    private int MyComment = 0;
    public boolean pausePlaying = false; // onPause是是否正在播放

    List<CourseLesson> catalogDatas = new ArrayList();
    //    List<HomeWorkRecord> catalogDatas = new ArrayList();
    private int playIndex = 0;
    private int catalogCurrentPage = 1;

    // 简介
    private ImageView summaryAuthorIcon;
    private ImageView summaryXing_1;
    private ImageView summaryXing_2;
    private ImageView summaryXing_3;
    private ImageView summaryXing_4;
    private ImageView summaryXing_5;
    private ImageView summary_more;
    private TextView summaryTitle;
    private TextView summaryLearn_count;
    private TextView summaryClassView;
    private TextView summaryAutorView;
    private TextView summaryScore;
    private TextView summaryAuthor;
    private TextView summaryAuthorSummary;
    private ExpandTextView class_summary;
    private List<ImageView> summaryXingList = new ArrayList<>();

    // 评论
    private View headView;
    private TextView commentScore;
    private TextView commentCount;
    private ImageView commentXing_1;
    private ImageView commentXing_2;
    private ImageView commentXing_3;
    private ImageView commentXing_4;
    private ImageView commentXing_5;
    private ImageView userXing_1;
    private ImageView userXing_2;
    private ImageView userXing_3;
    private ImageView userXing_4;
    private ImageView userXing_5;
    private List<ImageView> userXingList = new ArrayList<>();
    private List<ImageView> sorceXingList = new ArrayList<>();
    private List<CourseComment> commentDatas = new ArrayList<>();
//    private List<Comment> commentDatas = new ArrayList<>();
private android.media.MediaPlayer mMediaPlayer;


    // dialog
    private CustomDialog commentdialog;
    private TextView dialog_sorce;
    private EditText dialog_et;
    private Button confirmBtn;
    private ImageView dialogXing1;
    private ImageView dialogXing2;
    private ImageView dialogXing3;
    private ImageView dialogXing4;
    private ImageView dialogXing5;
    private List<ImageView> dialogXingList = new ArrayList<>();

    /**
     * 播放控件
     */
    public String TAG = ClassRoomPlayerActivity.class.getSimpleName();
    private VideoView playView;
    private static final int sDefaultTimeout = 3000;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private TextView mTotalTime;
    private TextView mCurrentTime;
    private ImageButton mPauseButton;
    private ImageButton screen_on;
    private SeekBar mProgress;
    private RelativeLayout playRl;
    private RelativeLayout playProress;
    private ImageView imageProgress;
    private TextView tv_speed;
    private RelativeLayout rl_finish;
    private boolean mShowing;
    private boolean mDragging;
    private boolean fullscreen = false;
    private long mDuration;
    MediaPlayerControl mPlayer;
    private boolean isNetWork = true;
    long currentPress;
    long curentPosition;
    private boolean isShowDialog = false;
    public NetWorkReceiver netWorkReceiver;
    AnimationDrawable spinner;
    private boolean isPlayLastFisish = false;
    private boolean isPlayNext = false;

    /**
     * viewpager
     */
    private ViewPager mViewPager;
    private IndicatorLayout mIndicator;
    private List<View> mViews = new ArrayList<>();
    private ViewPagerAdapter mAdapter;
    private String mTabs[] = new String[]{"简介", "目录", "评价"};
    View viewSummary;

    private ImageView ImageView_show;
    //资源类型 1音频 2视频
    boolean isOnlineDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Vitamio.isInitialized(this);
        } catch (Throwable a) {
            MyLog.getLogger(TAG).d("加载视频.so失败  msg" + a.getMessage());
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.classroom_player);
        currentResource = (CourseLesson) getIntent().getSerializableExtra("courseLesson");
//        currentResource = (HomeWorkRecord) getIntent().getSerializableExtra("courseLesson");
        init();
        initViewPager();
        initData();
        currentType = 1;
        mViewPager.setCurrentItem(currentType);
        netWorkReceiver = new NetWorkReceiver();
        registerReceiver(netWorkReceiver, new IntentFilter(SysConstants.NETWORKCHANGE));

        getService().getDataReportManager().reportEventBid(EventType.LESSON_IN, currentResource.courseId+"");
    }

    private void init() {
        playView = (VideoView) findViewById(R.id.playView);
        rl_finish = (RelativeLayout) findViewById(R.id.rl_finish);
        ViewGroup.LayoutParams lp = playView.getLayoutParams();
        lp.width = Utils.getDisplayWidth(mContext);
        lp.height = lp.width * 9 / 16;
        playView.setLayoutParams(lp);
        playView.setRecyvle(false);
        rl_finish.setLayoutParams(lp);
        playProress = (RelativeLayout) findViewById(R.id.rl_play_proess);
        imageProgress = (ImageView) findViewById(R.id.spinnerImageView);
        spinner = (AnimationDrawable) imageProgress.getBackground();
        mTotalTime = (TextView) findViewById(R.id.mediacontroller_time_total);
        mCurrentTime = (TextView) findViewById(R.id.mediacontroller_time_current);
        mPauseButton = (ImageButton) findViewById(R.id.mediacontroller_play_pause);
        ll_bottom_view = (RelativeLayout) findViewById(R.id.ll_bottom_view);
        screen_on = (ImageButton) findViewById(R.id.mediacontroller_screen);
        screen_on.setSelected(false);
        playRl = (RelativeLayout) findViewById(R.id.rl_mediacontroller);

        mProgress = (SeekBar) findViewById(R.id.mediacontroller_seekbar);
        rl_title = (RelativeLayout) findViewById(R.id.rl_title_left);

        title_back = (TextView) findViewById(R.id.title_back);
        title = (TextView) findViewById(R.id.video_title);
        tv_speed = (TextView) findViewById(R.id.tv_speed);

        ImageView_show = (ImageView) findViewById(R.id.ImageView_show);
        ImageView_show.setOnClickListener(this);

        mPauseButton.setOnClickListener(this);
        playProress.setOnClickListener(this);
        screen_on.setOnClickListener(this);
        screen_on.setOnTouchListener(this);
        mPauseButton.setOnTouchListener(this);
        findViewById(R.id.video_left).setOnClickListener(this);
        spinner.start();
        mProgress.setMax(1000);
        if (mProgress instanceof SeekBar) {
            SeekBar seeker = (SeekBar) mProgress;
            seeker.setOnSeekBarChangeListener(mSeekListener);
        }

        mMediaPlayer = new android.media.MediaPlayer();

    }

    private void initData() {
        if (currentResource != null) {
            getService().getCourseManager().getCourseComment(currentResource.courseId);
            getService().getCourseManager().getCourseLession(currentResource.courseId);
            getService().getCourseManager().getLatestCourseComment(currentResource.courseId);
            getService().getCourseManager().getCourse(currentResource.courseId);
        }
    }


    public void initViewPager() {
        viewSummary = LayoutInflater.from(mContext).inflate(R.layout.classroom_summary_layout, null);
        initSummary();
        mViews.add(viewSummary);

        View viewCatalog = LayoutInflater.from(mContext).inflate(R.layout.classroom_catalog_layout, null);
        catalogListView = (XListView) viewCatalog.findViewById(R.id.classroom_list);
        catalogListView.setXListViewListener(this);
        catalogListView.setOnItemClickListener(this);
        catalogAdapter = new ClassRoomCatalogAdapter(mContext, catalogDatas, 0);
        catalogListView.setAdapter(catalogAdapter);
        mViews.add(viewCatalog);

        View viewComment = LayoutInflater.from(mContext).inflate(R.layout.classroom_comment_layout, null);
        commentListView = (XListView) viewComment.findViewById(R.id.classroom_list);
        headView = LayoutInflater.from(mContext).inflate(R.layout.classroom_comment_header_layout, null);
        initComment();
        commentListView.setXListViewListener(this);
        commentListView.addHeaderView(headView);
        commentAdapter = new ClassRoomCommentAdapter(mContext, commentDatas);
        commentListView.setAdapter(commentAdapter);
        mViews.add(viewComment);

        mViewPager = (ViewPager) findViewById(R.id.classroom_viewpager);
        mIndicator = (IndicatorLayout) findViewById(R.id.classroom_indicator);
        mAdapter = new ViewPagerAdapter(mViews);
        mIndicator.setVisiableTabCount(mTabs.length);
        mIndicator.setTabs(mTabs);
        mIndicator.setViewPager(mViewPager);
        mIndicator.setOnIndicatorChangeListener(this);
        mViewPager.setAdapter(mAdapter);
    }

    public void initSummary() {
        summaryAuthorIcon = (ImageView) viewSummary.findViewById(R.id.classroom_austor_icon);
        summaryXing_1 = (ImageView) viewSummary.findViewById(R.id.summary_xing_1);
        summaryXing_2 = (ImageView) viewSummary.findViewById(R.id.summary_xing_2);
        summaryXing_3 = (ImageView) viewSummary.findViewById(R.id.summary_xing_3);
        summaryXing_4 = (ImageView) viewSummary.findViewById(R.id.summary_xing_4);
        summaryXing_5 = (ImageView) viewSummary.findViewById(R.id.summary_xing_5);
        summary_more = (ImageView) viewSummary.findViewById(R.id.summary_more);
        summaryTitle = (TextView) viewSummary.findViewById(R.id.classroom_summary_title);
        summaryLearn_count = (TextView) viewSummary.findViewById(R.id.classroom_learn_count);
        summaryScore = (TextView) viewSummary.findViewById(R.id.classroom_score);
        summaryClassView = (TextView) viewSummary.findViewById(R.id.classroom_view);
        summaryAutorView = (TextView) viewSummary.findViewById(R.id.author_view);
        summaryAuthor = (TextView) viewSummary.findViewById(R.id.classroom_austor);
        summaryAuthorSummary = (TextView) viewSummary.findViewById(R.id.classroom_austor_summary);
        summaryTitle = (TextView) viewSummary.findViewById(R.id.classroom_summary_title);
        class_summary = (ExpandTextView) viewSummary.findViewById(R.id.summary_class_summary);
        class_summary.setTextColor(getResources().getColor(R.color.gray));
        class_summary.setTextLineSpacingExtra(7);
        class_summary.setTextSize(13);
        class_summary.setTextColor(getResources().getColor(R.color.question_time));
        class_summary.setTextMaxLine(3);
        summaryXingList.add(summaryXing_1);
        summaryXingList.add(summaryXing_2);
        summaryXingList.add(summaryXing_3);
        summaryXingList.add(summaryXing_4);
        summaryXingList.add(summaryXing_5);

        summary_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                class_summary.switchs();
                if (class_summary.isExpand()) {
                    summary_more.setImageResource(R.drawable.classromm_msg_down);
                } else {
                    summary_more.setImageResource(R.drawable.classromm_msg_more);
                }
            }
        });
    }


    public void initComment() {
        userXing_1 = (ImageView) headView.findViewById(R.id.user_comment_xing_1);
        userXing_2 = (ImageView) headView.findViewById(R.id.user_comment_xing_2);
        userXing_3 = (ImageView) headView.findViewById(R.id.user_comment_xing_3);
        userXing_4 = (ImageView) headView.findViewById(R.id.user_comment_xing_4);
        userXing_5 = (ImageView) headView.findViewById(R.id.user_comment_xing_5);
        commentXing_1 = (ImageView) headView.findViewById(R.id.score_xing_1);
        commentXing_2 = (ImageView) headView.findViewById(R.id.score_xing_2);
        commentXing_3 = (ImageView) headView.findViewById(R.id.score_xing_3);
        commentXing_4 = (ImageView) headView.findViewById(R.id.score_xing_4);
        commentXing_5 = (ImageView) headView.findViewById(R.id.score_xing_5);
        userXingList.add(userXing_1);
        userXingList.add(userXing_2);
        userXingList.add(userXing_3);
        userXingList.add(userXing_4);
        userXingList.add(userXing_5);
        sorceXingList.add(commentXing_1);
        sorceXingList.add(commentXing_2);
        sorceXingList.add(commentXing_3);
        sorceXingList.add(commentXing_4);
        sorceXingList.add(commentXing_5);
        commentScore = (TextView) headView.findViewById(R.id.classroom_comment_header_score);
        commentCount = (TextView) headView.findViewById(R.id.classroom_comment_header_comment);
        headView.findViewById(R.id.comment_xing_ll).setOnClickListener(this);
        headView.findViewById(R.id.classroom_comment_header_tv).setOnClickListener(this);
    }

    //将Course改为Comment
    public void showSummaryData(Course data) {
        if (data != null) {
            summaryTitle.setText(data.title);
//            summaryTitle.setText(data.userTitle);
            summaryLearn_count.setText(String.valueOf(data.hits) + "人学过");
            summaryScore.setText(String.valueOf(data.score) + "分");
            class_summary.setText(data.description);
            summaryAuthor.setText(data.teacherName);
            summaryAuthorSummary.setText(data.teacherDesc);
            double sorce = data.score;
            int intSorce = (int) sorce;
            for (int i = 0; i < summaryXingList.size(); i++) {
                if (i < intSorce) {
                    summaryXingList.get(i).setSelected(true);
                    sorceXingList.get(i).setSelected(true);
                } else {
                    summaryXingList.get(i).setSelected(false);
                    sorceXingList.get(i).setSelected(false);
                }
            }

            if ((sorce - intSorce) > 0) {
                summaryXingList.get(intSorce).setImageResource(R.drawable.xing_m_half);
                sorceXingList.get(intSorce).setImageResource(R.drawable.xing_s_half);
            }

            commentScore.setText("综合评分: " + String.valueOf(data.score));
            commentCount.setText(String.valueOf(data.scoreCnt) + "人评价");
            class_summary.post(new Runnable() {
                @Override
                public void run() {
                    if (class_summary.expandText().getLineCount() <= class_summary.line()) {
                        summary_more.setVisibility(View.GONE);
                    } else {
                        summary_more.setVisibility(View.VISIBLE);
                    }
                }
            });

            ImageLoader.getInstance().displayImage(data.teacherAvatar + SysConstants.Imgurlsuffix80, summaryAuthorIcon, ImageUtils.DIO_USER_ICON);
//            ImageLoader.getInstance().displayImage(data.userAvatarUrl + SysConstants.Imgurlsuffix80, summaryAuthorIcon, ImageUtils.DIO_USER_ICON);
        }

    }

    //将CourseLesson换为Comment
    public void showCatalogData(List<CourseLesson> datas) {
        boolean flag = false;
        if (!CollectionUtils.isEmpty(datas)) {
            if (CollectionUtils.isEmpty(catalogDatas)) {
                for (int i = 0; i < datas.size(); i++) {
                    if (datas.get(i).id.equals(currentResource.id)) {
                        flag = true;
                        playIndex = i;
                    }
                }
                if (flag) {
                    play(currentResource.videoUrl,currentResource.resourceType);
//                    play(vidioUrl);
                } else {
                    currentResource = datas.get(0);
                    play(currentResource.videoUrl,currentResource.resourceType);
//                    play(vidioUrl);
                }
            }
            catalogDatas.clear();
            catalogDatas.addAll(datas);
            catalogAdapter.setData(currentResource.id);
        }
        catalogListView.stopRefresh();
        catalogListView.stopLoadMore();
        if (hasMore) {
            catalogListView.setPullLoadEnable(true);
        } else {
            catalogListView.setPullLoadEnable(false);
        }

    }

    //将CourseComment改为Comment
    public void showCommentData(List<CourseComment> mDatas) {
        if (!CollectionUtils.isEmpty(mDatas)) {
            commentDatas.addAll(mDatas);
            commentAdapter.notifyDataSetChanged();
        }

        for (int j = 0; j < userXingList.size(); j++) {
            if (j < MyComment) {
                userXingList.get(j).setSelected(true);
            } else {
                userXingList.get(j).setSelected(false);
            }
        }
        commentListView.stopRefresh();
        commentListView.stopLoadMore();

        if (commentHasMore) {
            commentListView.setPullLoadEnable(true);
        } else {
            commentListView.setPullLoadEnable(false);
        }
    }


    public void play(String videoPath,int type) {
        try {
            if (isActivity) {
                int netInt = PhoneUtils.getNetWorkType(mContext);
                if (netInt == PhoneUtils.NETWORKTYPE_WIFI) {
                    playVideo(videoPath,type);
                } else if (netInt == PhoneUtils.NETWORKTYPE_MOBILE) {
                    if (PreferenceUtils.getPrefBoolean(mContext, SysConstants.media_folw, false)) {
                        playVideo(videoPath,type);
                    } else {
                        showCenterBtnDialog();
                    }
                } else if (netInt == PhoneUtils.NETWORKTYPE_INVALID) {
                    if (mPlayer != null) {
                        mPlayer.pause();
                    }
                    showToast("当前无网络");
                } else {
                    if (mPlayer != null) {
                        mPlayer.pause();
                    }
                    showToast("当前未知网络");
                }
            }
        } catch (Throwable a) {
            MyLog.getLogger(TAG).d("Vitamio播放课程视频失败");
//            MyLog.getLogger(TAG).d("Vitamio播放课程视频失败"+a);
        }

    }
    //资源类型 1音频 2视频
    public void playVideo(String mVideoPath,int type) {
        if (!TextUtils.isEmpty(mVideoPath)) {
            if (type==2){
                if (mMediaPlayer != null){
                    mMediaPlayer.pause();
                }
                ImageView_show.setVisibility(View.GONE);
                playView.setOnErrorListener(new MediaErroy());
                playView.setVideoPath(mVideoPath);
                playView.setMediaController(this);
                playView.requestFocus();
                playView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.setPlaybackSpeed(1.0f);
                    }
                });
                getService().getCourseManager().playCourseLesson(currentResource.id);
            }else {
                spinner.stop();
//                imageProgress.setVisibility(View.GONE);
                playProress.setVisibility(View.GONE);
                tv_speed.setVisibility(View.VISIBLE);
                show();
                ImageView_show.setVisibility(View.VISIBLE);
//                ImageView_show.setImageDrawable(Drawable.createFromPath(currentResource.pic));
                ImageLoader.getInstance().displayImage(currentResource.pic,ImageView_show);
//                mMediaPlayer = new android.media.MediaPlayer();
                try {
                    mMediaPlayer.setDataSource(mVideoPath);
                    mMediaPlayer.setOnPreparedListener(new android.media.MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(android.media.MediaPlayer mp) {
                            mMediaPlayer.start();
                        }
                    });
                    mMediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            MyLog.getLogger(TAG).d("未找到该课程视频");
            showToast("未找到课程该视频");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        isActivity = true;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (pausePlaying && playView != null && mPlayer != null) {
                    mPlayer.start();
                    pausePlaying = false;
                    updatePausePlay();
                }
            }
        }, 1000);
//        play(vidioUrl);

        if (mRotationObserver != null) {
            mRotationObserver.startObserver();
        }
       stopService(new Intent(mContext, EducAudioService.class));
    }


    @Override
    public void onRefresh() {
        if (currentType == 1) {
            if (currentResource != null) {
                catalogCurrentPage = 1;
                getService().getCourseManager().getCourse(currentResource.courseId);
                getService().getCourseManager().getCourseLession(currentResource.courseId);
            }
        } else if (currentType == 2) {
            if (currentResource != null) {
                getService().getCourseManager().getCourse(currentResource.courseId);
                getService().getCourseManager().getLatestCourseComment(currentResource.courseId);
            }
        }
    }


    @Override
    public void onLoadMore() {
        if (currentType == 1) {
//            getService().getCourseManager().getCourseLession(catalogCurrentPage++);
        } else if (currentType == 2) {
            if (!CollectionUtils.isEmpty(commentDatas) && currentResource != null) {
                long lastId = commentDatas.get(commentDatas.size() - 1).id;
                getService().getCourseManager().getHistoryCourseComment(currentResource.courseId, lastId);
            }
        }
    }

    @Override
    public void finish() {
        if (playView != null) {
            playView.stopPlayback();
            playView = null;
        }
        if (mRotationObserver != null) {
            mRotationObserver.stopObserver();
        }
        //        音频播放销毁
        if (mMediaPlayer!= null){
            mMediaPlayer.pause();
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }


        getService().getDataReportManager().reportEventBid(EventType.LESSON_OUT, currentResource.courseId + "");
        super.finish();
    }

    @Override
    protected void onDestroy() {
        if (playView != null) {
            playView.stopPlayback();
            playView = null;
        }

        if (netWorkReceiver != null) {
            unregisterReceiver(netWorkReceiver);
        }
        //        音频播放销毁
        if (mMediaPlayer!= null){
            mMediaPlayer.pause();
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPlayer != null && playView != null && playView.isPlaying()) {
            pausePlaying = true;
            mPlayer.pause();
            setProgress();
            updatePausePlay();
        }
        if (mMediaPlayer != null  && mMediaPlayer.isPlaying()) {
            pausePlaying = true;
            mMediaPlayer.pause();
            setProgress();
            updatePausePlay();
        }

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
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.mediacontroller_play_pause) {
            if(currentResource.resourceType==1){
                int netInt = PhoneUtils.getNetWorkType(mContext);
                if (netInt == PhoneUtils.NETWORKTYPE_WIFI) {
                    doPauseResume();
                } else if (netInt == PhoneUtils.NETWORKTYPE_MOBILE) {
                    doPauseResume();
                } else if (netInt == PhoneUtils.NETWORKTYPE_INVALID) {
                    showToast("当前无网络");
                } else {
                    showToast("当前未知网络");
                }
            }else{
                int netInt = PhoneUtils.getNetWorkType(mContext);
                if (netInt == PhoneUtils.NETWORKTYPE_WIFI) {
                    if (isPlayLastFisish) {
                        play(currentResource.videoUrl,currentResource.resourceType);
//                    play(vidioUrl);
                        isPlayLastFisish = false;
                    } else {
                        doPauseResume();
                    }
                } else if (netInt == PhoneUtils.NETWORKTYPE_MOBILE) {
                    if (PreferenceUtils.getPrefBoolean(mContext, SysConstants.media_folw, false)) {
                        if (isPlayLastFisish) {
                            play(currentResource.videoUrl,currentResource.resourceType);
//                        play(vidioUrl);
                            isPlayLastFisish = false;
                        } else {
                            doPauseResume();
                        }
                    } else {
                        if (!isShowDialog) {
                            isShowDialog = true;
                            showCenterBtnDialog();
                        }
                    }
                } else if (netInt == PhoneUtils.NETWORKTYPE_INVALID) {
                    showToast("当前无网络");
                } else {
                    showToast("当前未知网络");
                }
            }

        } else if (view.getId() == R.id.mediacontroller_screen) {
            Configuration mConfiguration = this.getResources().getConfiguration();
            int ori = mConfiguration.orientation;
            if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {//横屏
                fullscreen = false;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {//竖屏
                fullscreen = true;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                MobclickAgent.onEvent(this, "media_resource_fullscreen", UmengData.media_resource_fullscreen);//切换为全屏视频播放
            }
        } else if (view.getId() == R.id.video_left) {
            if (fullscreen) {//横屏
                fullscreen = false;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                finish();
                MobclickAgent.onEvent(this, "media_resource_clickBack", UmengData.media_resource_clickBack);
            }
        } else if (view.getId() == R.id.comment_xing_ll || view.getId() == R.id.classroom_comment_header_tv) {
            if (MyComment == 0) {
                if (mPlayer != null && playView.isPlaying()) {
                    pausePlaying = true;
                    mPlayer.pause();
                    updatePausePlay();
                }
                showDialog();
            } else {
                showToast("您已评价过该课程");
            }

        } else if (view.getId() == R.id.confirm_btn) {
            if (isOnlineDialog){
                return;
            }
            for (int i = 0; i < dialogXingList.size(); i++) {
                if (dialogXingList.get(i).isSelected()) {
                    MyComment++;
                }
            }
            commentdialog.dismiss();
            showProgressDialog(mContext, "", true, null);
            getService().getCourseManager().addCourseComment(currentResource.course.id, MyComment, dialog_et.getText().toString().trim());
        } else if (view.getId() == R.id.comment_dialog_xing_1 || view.getId() == R.id.comment_dialog_xing_2 || view.getId() == R.id.comment_dialog_xing_3 ||
                view.getId() == R.id.comment_dialog_xing_4 || view.getId() == R.id.comment_dialog_xing_5) {
            int tag = (Integer) view.getTag();
            int selecCount = 0;
            boolean select = dialogXingList.get(tag).isSelected();
            for (int i = 0; i < dialogXingList.size(); i++) {
                if (select) {
                    if (i > tag) {
                        selecCount++;
                        dialogXingList.get(i).setSelected(false);
                    }
                } else {
                    if (tag >= i) {
                        selecCount++;
                        dialogXingList.get(i).setSelected(true);
                    }
                }
            }
            if (select) {
                selecCount = dialogXingList.size() - selecCount;
            }
            if (selecCount == 1) {
                dialog_sorce.setText("较差");
            } else if (selecCount == 2) {
                dialog_sorce.setText("一般");
            } else if (selecCount == 3) {
                dialog_sorce.setText("良好");
            } else if (selecCount == 4) {
                dialog_sorce.setText("推荐");
            } else if (selecCount == 5) {
                dialog_sorce.setText("极佳");
            }
        } else if (view.getId() == R.id.rl_play_proess) {
            show();
        } else if (view.getId() == R.id.ImageView_show) {
            show();
        }

    }

    /**
     * 屏幕旋转时调用此方法
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //现在是竖屏
            fullscreen = false;
            screen_on.setSelected(false);
            WindowManager.LayoutParams attr = getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attr);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            ViewGroup.LayoutParams lp = playView.getLayoutParams();
            lp.width = Utils.getDisplayWidth(mContext);
            lp.height = lp.width * 9 / 16;
            playView.setLayoutParams(lp);

            playView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
            ll_bottom_view.setVisibility(View.VISIBLE);
        }

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //现在是横屏
            fullscreen = true;
            screen_on.setSelected(true);
            WindowManager.LayoutParams lpw = getWindow().getAttributes();
            lpw.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lpw);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            ViewGroup.LayoutParams lp = playView.getLayoutParams();
            lp.width = Utils.getDisplayWidth(mContext);
            lp.height = Utils.getDisplayHeight(mContext);
            playView.setLayoutParams(lp);
            playView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0);
            ll_bottom_view.setVisibility(View.GONE);
        }
        if (Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                }
            }, 500);
        }
    }

    @Override
    public void screenOrientAtion() {
        super.screenOrientAtion();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    public void onEventMainThread(CourseLessonEvent event) {
        disProgressDialog();
        if (isActivity) {
            switch (event.getEvent()) {
                case GET_COURSELESSON_SUCCESS:
                    showCatalogData(event.getCourseLesson());
                    showAndSaveLog(TAG, "获得课程视频列表成功 ", false);
                    break;
                case GET_COURSELESSON_FAILED:
                    showToast(event.getMsg());
                    catalogListView.stopLoadMore();
                    catalogListView.stopRefresh();
                    showAndSaveLog(TAG, "获得课程视频列表失败", false);
                    break;
            }
        }
    }

    public void onEventMainThread(CourseEvent event) {
        disProgressDialog();
        if (isActivity) {
            switch (event.getEvent()) {
                case GET_COURSE_SUCCESS:
                    showSummaryData(event.getCourse());
                    showAndSaveLog(TAG, "获得课程成功 ", false);
                    break;
                case GET_COURSE_FAILED:
                    showToast(event.getMsg());
                    catalogListView.stopLoadMore();
                    catalogListView.stopRefresh();
                    showAndSaveLog(TAG, "获得课程失败", false);
                    break;
            }
        }
    }


    public void onEventMainThread(CourseCommentEvent event) throws JSONException {
        disProgressDialog();
        if (isActivity) {
            switch (event.getEvent()) {
                case GET_LATEST_COURSECOMMENT_SUCCESS:
                    if (!CollectionUtils.isEmpty(event.getCourseComments())) {
                        commentDatas.clear();
                    }
                    showCommentData(event.getCourseComments());
                    commentHasMore = event.hasMore();
                    showAndSaveLog(TAG, "获得课程最新视频评论成功 ", false);
                    break;
                case GET_LATEST_COURSECOMMENT_FAILED:
                    showToast(event.getMsg());
                    commentListView.stopRefresh();
                    showAndSaveLog(TAG, "获得课程最新视频评论失败", false);
                    break;
                case GET_HISTORY_COURSECOMMENT_SUCCESS:

                    showCommentData(event.getCourseComments());
                    commentHasMore = event.hasMore();
                    showAndSaveLog(TAG, "获得课程历史视频评论成功 ", false);
                    break;
                case GET_HISTORY_COURSECOMMENT_FAILED:
                    showToast(event.getMsg());
                    commentListView.stopLoadMore();
                    showAndSaveLog(TAG, "获得课程历史视频评论失败", false);
                    break;
                case GET_COURSECOMMENT_SUCCESS:
                    if (event.getCourseComment() != null) {
                        MyComment = event.getCourseComment().score;
                        showCommentData(null);
                    }
                    showAndSaveLog(TAG, "获取我的评论成功 ", false);
                    break;
                case GET_COURSECOMMENT_FAILED:
                    showToast(event.getMsg());
                    showAndSaveLog(TAG, "获取我的评论失败", false);
                    break;
                case ADD_COURSECOMMENT_SUCCESS:
                    disProgressDialog();
                    getService().getCourseManager().getLatestCourseComment(currentResource.courseId);
                    getService().getCourseManager().getCourse(currentResource.courseId);
                    JSONObject json = new JSONObject();
                    json.put("score",MyComment);

                    getService().getDataReportManager().reportExtendedInfo(EventType.LESSON_SCORE,currentResource.course.id+"", json.toString());
                    showAndSaveLog(TAG, "添加视频评论成功 ", false);
                    break;
                case ADD_COURSECOMMENT_FAILED:
                    disProgressDialog();
                    showToast(event.getMsg());
                    showAndSaveLog(TAG, "添加视屏评论失败", false);
                    break;

            }
        }
    }

    /*************************************************************************/

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            long pos;
            switch (msg.what) {
                case FADE_OUT:
                    hide();
                    break;
                case SHOW_PROGRESS:
                    pos = setProgress();
                    if (!mDragging && mShowing) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                        updatePausePlay();
                    }
                    break;
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        public void onStartTrackingTouch(SeekBar bar) {
            mDragging = true;
//            show(3600000);
            mHandler.removeMessages(SHOW_PROGRESS);
        }

        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser)
                return;

            long newposition = (mDuration * progress) / 1000;
            String time = StringUtils.generateTime(newposition);
            if (currentResource.resourceType==1){
                mMediaPlayer.seekTo((Integer.parseInt(newposition+"")));
            }else{
                mPlayer.seekTo(newposition);
            }
            if (mCurrentTime != null)
                mCurrentTime.setText(time);
        }

        public void onStopTrackingTouch(SeekBar bar) {
            if (currentResource.resourceType==1){
                mMediaPlayer.seekTo(Integer.parseInt((mDuration * bar.getProgress()) / 1000+""));
            }else{
                mPlayer.seekTo((mDuration * bar.getProgress()) / 1000);
            }
            show(sDefaultTimeout);
            mHandler.removeMessages(SHOW_PROGRESS);
            mDragging = false;
            mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
        }
    };


    private long setProgress() {
        if (currentResource.resourceType==1){
            if (mMediaPlayer == null || mDragging)
                return 0;
            if (PhoneUtils.isNetworkAvailable(mContext) && isNetWork) {
                curentPosition = mMediaPlayer.getCurrentPosition();
                mDuration = mMediaPlayer.getDuration();
            }
            if (mProgress != null) {
                if (mDuration > 0) {
                    currentPress = 1000L * curentPosition / mDuration;
                    mProgress.setProgress((int) currentPress);
                }
//                int percent = mMediaPlayer.getBufferPercentage();
//                mProgress.setSecondaryProgress(percent * 10);
            }
            if (mCurrentTime != null)
                mCurrentTime.setText(StringUtils.generateTime(curentPosition));
            if (mTotalTime != null)
                mTotalTime.setText(StringUtils.generateTime(mDuration));
        }else{
            if (mPlayer == null || mDragging)
                return 0;
            if (PhoneUtils.isNetworkAvailable(mContext) && isNetWork) {
                curentPosition = mPlayer.getCurrentPosition();
                mDuration = mPlayer.getDuration();
            }
            if (mProgress != null) {
                if (mDuration > 0) {
                    currentPress = 1000L * curentPosition / mDuration;
                    mProgress.setProgress((int) currentPress);
                }
                int percent = mPlayer.getBufferPercentage();
                mProgress.setSecondaryProgress(percent * 10);
            }
            if (mCurrentTime != null)
                mCurrentTime.setText(StringUtils.generateTime(curentPosition));
            if (mTotalTime != null)
                mTotalTime.setText(StringUtils.generateTime(mDuration));
        }
        return curentPosition;
    }

    private void updatePausePlay() {

        if (currentResource.resourceType == 2) {
            if (mPlayer != null) {
                if (mPlayer.isPlaying())
                    mPauseButton.setImageResource(getResources().getIdentifier("mediacontroller_pause", "drawable", getPackageName()));
//                mPauseButton.setImageResource(R.drawable.mediacontroller_pause);
                else
                    mPauseButton.setImageResource(getResources().getIdentifier("mediacontroller_play", "drawable", getPackageName()));
//                mPauseButton.setImageResource(getResources().getIdentifier("mediacontroller_play", "drawable", getPackageName()));
//                mPauseButton.setImageResource(R.drawable.mediacontroller_play);
            }
        }else{
            if (mMediaPlayer != null) {
                if (mMediaPlayer.isPlaying())
                    mPauseButton.setImageResource(getResources().getIdentifier("mediacontroller_pause", "drawable", getPackageName()));
                else
                    mPauseButton.setImageResource(getResources().getIdentifier("mediacontroller_play", "drawable", getPackageName()));
            }
//                mPauseButton.setImageResource(getResources().getIdentifier("mediacontroller_play", "drawable", getPackageName()));
//                mPauseButton.setImageResource(R.drawable.mediacontroller_play);
        }
    }

    private void doPauseResume() {
        if (currentResource.resourceType==1){
            if (mMediaPlayer.isPlaying()) {//暂停
                mMediaPlayer.pause();
            } else {//播放
                mMediaPlayer.start();
            }
        }else{
            if (mPlayer.isPlaying()) {//暂停
                mPlayer.pause();
                MobclickAgent.onEvent(this, "media_resource_videoPause", UmengData.media_resource_videoPause);
            } else {//播放
                mPlayer.start();
                MobclickAgent.onEvent(this, "media_resource_videoPlay", UmengData.media_resource_videoPlay);
            }
        }
        updatePausePlay();
    }


    @Override
    public void hide() {
        if (mShowing) {
            try {
                playRl.startAnimation(AnimationUtils.loadAnimation(ClassRoomPlayerActivity.this, R.anim.edu_pic_bottom_hide));
                playRl.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        playRl.setVisibility(View.GONE);
                    }
                }, 180);
                rl_title.startAnimation(AnimationUtils.loadAnimation(ClassRoomPlayerActivity.this, R.anim.edu_pic_title_hide));
                rl_title.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rl_title.setVisibility(View.GONE);
                    }
                }, 180);
                mHandler.removeMessages(SHOW_PROGRESS);
            } catch (IllegalArgumentException ex) {
                MyLog.getLogger(TAG).d("MediaController already removed");
            }
            mShowing = false;
        }
    }

    @Override
    public void show() {
        show(sDefaultTimeout);
    }

    @Override
    public void show(int timeout) {
        timeout = sDefaultTimeout;
        if (!mShowing) {
            if (mPauseButton != null)
                mPauseButton.requestFocus();
            playRl.setVisibility(View.VISIBLE);
            playRl.startAnimation(AnimationUtils.loadAnimation(ClassRoomPlayerActivity.this, R.anim.edu_pic_bottom_show));
            rl_title.setVisibility(View.VISIBLE);
            rl_title.startAnimation(AnimationUtils.loadAnimation(ClassRoomPlayerActivity.this, R.anim.edu_pic_title_show));
            title.setText(currentResource.title);
//            title.setText(currentResource.getTargetName());
            mShowing = true;
        }
        updatePausePlay();
        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT), timeout);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //播放完毕
        mPauseButton.setImageResource(getResources().getIdentifier("mediacontroller_play", "drawable", getPackageName()));
        if (!PhoneUtils.isNetworkAvailable(mContext)) {
            currentPress = mProgress.getProgress();
            curentPosition = mDuration * currentPress / 1000L;
            tv_speed.setVisibility(View.GONE);
            isNetWork = false;
            showToast("当前无网络");
        } else {
            if (isPlayLast()) {
                rl_finish.setVisibility(View.VISIBLE);
                isPlayLastFisish = true;
                currentPress = 0;
                curentPosition = 0;
            } else {
                if (!CollectionUtils.isEmpty(catalogDatas)) {
                    playIndex = playIndex + 1;
                    currentResource = catalogDatas.get(playIndex);
                    play(currentResource.videoUrl,currentResource.resourceType);
//                    play(vidioUrl);
                    catalogAdapter.setData(currentResource.id);
                }
            }
        }
    }

    @Override
    public void setEnabled(boolean isInPlaybackState) {
        if (mPauseButton != null)
            mPauseButton.setEnabled(isInPlaybackState);
        if (mProgress != null)
            mProgress.setEnabled(isInPlaybackState);
    }

    @Override
    public void setMediaPlayerControl(MediaPlayerControl playerControl) {
        this.mPlayer = playerControl;
        updatePausePlay();
    }

    @Override
    public void setFileName(String name) {
    }

    @Override
    public void bufferingStart() {
        playProress.setVisibility(View.GONE);
        spinner.stop();
        show();
        if (!PhoneUtils.isNetworkAvailable(mContext)) {
            currentPress = mProgress.getProgress();
            curentPosition = mDuration * currentPress / 1000L;
            tv_speed.setVisibility(View.GONE);
            isNetWork = false;
            showToast("当前无网络");
        } else {
            tv_speed.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void bufferingEnd() {
        rl_finish.setVisibility(View.GONE);
        tv_speed.setVisibility(View.GONE);
        isNetWork = true;
    }

    @Override
    public void onPrepared() {
//        show();
        disProgressDialog();
    }

    @Override
    public void bufferingChange(int percent) {
        tv_speed.setText(percent + "kb/s");
    }

    @Override
    public boolean isShowing() {
        return mShowing;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (fullscreen) {//横屏
                fullscreen = false;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean isPlayLast() {
        if (!CollectionUtils.isEmpty(catalogDatas) && currentResource != null) {
            for (int i = 0; i < catalogDatas.size(); i++) {
                if (catalogDatas.get(i).id.equals(currentResource.id)) {
                    if (i == catalogDatas.size() - 1) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            ImageUtils.changeImgAlpha((ImageButton) view, 0);
        } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            ImageUtils.changeImgAlpha((ImageButton) view, -80);
        } else if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
            ImageUtils.changeImgAlpha((ImageButton) view, 0);
        }
        return false;
    }

    @Override
    public void onclickFlowAll() {
//        super.onclickFlowAll();
        isOnlineDialog = true;
        isShowDialog = false;
        PreferenceUtils.setPrefBoolean(mContext, SysConstants.media_folw, true);
        showDialog("", getResources().getString(R.string.media_flow_all_msg), null, getResources().getString(R.string.media_flow_all_msg_que), false);
    }

    @Override
    public void onclickflowCancle() {
        super.onclickflowCancle();
        isShowDialog = false;
        finish();
    }

//longlongago
    @Override
    public void onConfirm() {
        super.onConfirm();
        if (!isPlayNext) {
            if (currentPress != 0 && mPlayer != null) {
                mPlayer.seekTo(mPlayer.getCurrentPosition());
            } else {
                play(currentResource.videoUrl,currentResource.resourceType);
//                play(vidioUrl);
            }
        }

    }

    @Override
    public void onCancel() {
        super.onCancel();
        isShowDialog = false;
        isPlayNext = false;
        if (spinner != null) {
            spinner.stop();
        }
        playProress.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        position = position - 1;
        if (position >= 0) {
            playIndex = position;
            if (!catalogDatas.get(position).id.equals(currentResource.id)) {
                currentResource = catalogDatas.get(position);
                currentPress = 0;
                playView.stopPlayback();
                playProress.setVisibility(View.VISIBLE);
                play(currentResource.videoUrl,currentResource.resourceType);
//                play(vidioUrl);
                catalogAdapter.setData(currentResource.id);
            }


            currentResource = catalogDatas.get(position);
            currentPress = 0;
            playView.stopPlayback();
            playProress.setVisibility(View.VISIBLE);
                play(currentResource.videoUrl,currentResource.resourceType);
//            play(vidioUrl);、


        }
    }

    @Override
    public void onChangeed(int index) {
        currentType = index;
    }

    class NetWorkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isActivity) {
                if (intent.getAction().equals(SysConstants.NETWORKCHANGE)) {

                    int netInt = PhoneUtils.getNetWorkType(mContext);
                    if (netInt == PhoneUtils.NETWORKTYPE_WIFI) {
                        if (mPlayer != null && mPlayer.isPlaying()) {
                            if (currentPress == 0) {
                                play(currentResource.videoUrl,currentResource.resourceType);
//                                play(vidioUrl);
                            }
                        }
                    } else if (netInt == PhoneUtils.NETWORKTYPE_MOBILE) {
                        if (PreferenceUtils.getPrefBoolean(mContext, SysConstants.media_folw, false)) {
                            if (mPlayer != null && mPlayer.isPlaying()) {
                                if (currentPress == 0) {
                                    play(currentResource.videoUrl,currentResource.resourceType);
//                                    play(vidioUrl);
                                }
                            }
                        } else {
                            if (mPlayer != null) {
                                mPlayer.pause();
                                updatePausePlay();
                            }
                            if (!isShowDialog) {
                                isShowDialog = true;
                                showCenterBtnDialog();
                            }
                        }
                    } else if (netInt == PhoneUtils.NETWORKTYPE_INVALID) {
                        if (mPlayer != null) {
                            mPlayer.pause();
                        }
                        showToast("当前无网络");
                    } else {
                        if (mPlayer != null) {
                            mPlayer.pause();
                        }
                        showToast("当前未知网络");
                    }
                }
            }
        }
    }

    public void showDialog() {
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.classroom_comment_dialog, null);
        dialogXingList.clear();
        commentdialog = new CustomDialog(mContext, R.style.dialog_alert_style, 0);
        // 根据id在布局中找到控件对象
        dialog_sorce = (TextView) view.findViewById(R.id.comment_dialog_sorce);
        dialog_et = (EditText) view.findViewById(R.id.comment_dialog_et);
        confirmBtn = (Button) view.findViewById(R.id.confirm_btn);
        dialogXing1 = (ImageView) view.findViewById(R.id.comment_dialog_xing_1);
        dialogXing2 = (ImageView) view.findViewById(R.id.comment_dialog_xing_2);
        dialogXing3 = (ImageView) view.findViewById(R.id.comment_dialog_xing_3);
        dialogXing4 = (ImageView) view.findViewById(R.id.comment_dialog_xing_4);
        dialogXing5 = (ImageView) view.findViewById(R.id.comment_dialog_xing_5);
        dialog_et.addTextChangedListener(new MaxLengthWatcher(500, dialog_et));
        dialog_sorce.setText("极佳");
        dialogXing1.setTag(0);
        dialogXing2.setTag(1);
        dialogXing3.setTag(2);
        dialogXing4.setTag(3);
        dialogXing5.setTag(4);
        dialogXing1.setSelected(true);
        dialogXing2.setSelected(true);
        dialogXing3.setSelected(true);
        dialogXing4.setSelected(true);
        dialogXing5.setSelected(true);
        dialogXingList.add(dialogXing1);
        dialogXingList.add(dialogXing2);
        dialogXingList.add(dialogXing3);
        dialogXingList.add(dialogXing4);
        dialogXingList.add(dialogXing5);
        confirmBtn.setOnClickListener(this);
        dialogXing1.setOnClickListener(this);
        dialogXing2.setOnClickListener(this);
        dialogXing3.setOnClickListener(this);
        dialogXing4.setOnClickListener(this);
        dialogXing5.setOnClickListener(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = Utils.getDisplayWidth(mContext) - Utils.dip2px(mContext, 20) * 2;
        commentdialog.setContentView(view, params);
        commentdialog.setCanceledOnTouchOutside(false);
        commentdialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        if (pausePlaying && playView != null && mPlayer != null) {
                            mPlayer.start();
                            pausePlaying = false;
                            updatePausePlay();
                        }
                    }
                }, 1000);
            }
        });
        commentdialog.show();
    }

    public class MaxLengthWatcher implements TextWatcher {
        private int maxLen = 0;
        private EditText editText = null;

        public MaxLengthWatcher(int maxLen, EditText editText) {
            this.maxLen = maxLen;
            this.editText = editText;
        }

        public void afterTextChanged(Editable arg0) {
        }

        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            Editable editable = editText.getText();
            int len = editable.length();
            if (len >= maxLen) {
                showToast(getResources().getString(R.string.edit_number_count));
            }
        }
    }

    public class MediaErroy implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            String erroy = "";
            switch (what) {
                case -1004:
                    erroy = "MEDIA_ERROR_IO";
                    break;
                case -1007:
                    erroy = "MEDIA_ERROR_MALFORMED";
                    break;
                case 200:
                    erroy = "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK";
                    break;
                case 100:
                    erroy = "MEDIA_ERROR_SERVER_DIED";
                    break;
                case -110:
                    erroy = "MEDIA_ERROR_TIMED_OUT";
                    break;
                case 1:
                    erroy = "MEDIA_ERROR_UNKNOWN";
                    break;
                case -1010:
                    erroy = "MEDIA_ERROR_UNSUPPORTED";
                    break;
            }
            switch (extra) {
                case 800:
                    erroy = "MEDIA_INFO_BAD_INTERLEAVING";
                    break;
                case 702:
                    erroy = "MEDIA_INFO_BUFFERING_END";
                    break;
                case 701:
                    Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE");
                    break;
                case 802:
                    erroy = "MEDIA_INFO_METADATA_UPDATE";
                    break;
                case 801:
                    erroy = "MEDIA_INFO_NOT_SEEKABLE";
                    break;
                case 1:
                    erroy = "MEDIA_INFO_UNKNOWN";
                    break;
                case 3:
                    erroy = "MEDIA_INFO_VIDEO_RENDERING_START";
                    break;
                case 700:
                    erroy = "MEDIA_INFO_VIDEO_TRACK_LAGGING";
                    break;
            }
            MyLog.getLogger(TAG).d("Vitamio播放课程视频失败 msg = " + erroy);
            return false;
        }
    }


}
