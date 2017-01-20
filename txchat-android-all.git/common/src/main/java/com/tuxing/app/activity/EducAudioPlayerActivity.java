package com.tuxing.app.activity;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.adapter.EducInfoAdapter;
import com.tuxing.app.base.BaseActivity;
import com.tuxing.app.services.EducAudioService;
import com.tuxing.app.util.EducAudioPlayListener;
import com.tuxing.app.util.ImageUtils;
import com.tuxing.app.util.MyLog;
import com.tuxing.app.util.PhoneUtils;
import com.tuxing.app.util.PreferenceUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.UmengData;
import com.tuxing.app.util.Utils;
import com.tuxing.rpc.proto.CommentType;
import com.tuxing.rpc.proto.Resource;
import com.tuxing.rpc.proto.TargetType;
import com.tuxing.sdk.event.CommentEvent;
import com.tuxing.sdk.event.resource.AlbumEvent;
import com.tuxing.sdk.event.resource.ResourceEvent;
import com.tuxing.sdk.utils.CollectionUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import io.vov.vitamio.utils.StringUtils;
import io.vov.vitamio.widget.MediaPlayerControl;
import me.maxwin.view.XListView;

/**
 * 通用播放音频界面
 */
public class EducAudioPlayerActivity extends BaseActivity implements EducAudioPlayListener,
        XListView.IXListViewListener, View.OnTouchListener, AdapterView.OnItemClickListener{


    private TextView title;
    private TextView videoType;
    private TextView videoAlbumName;
    private TextView videoSouse;
    private TextView videoInfo;
    private TextView educ_list_name;
    private TextView educ_list_tv;
    private TextView comment_number;
    private TextView zan_number;
    private TextView title_back;
    private CheckBox zanCB;
    private XListView listView;
    private LinearLayout ll_info;
    private RelativeLayout rl_title;
    private RelativeLayout rl_audio;
    private RelativeLayout comment_views_ll;
    private EducInfoAdapter videoAdapter;
    private boolean hasMore = false;
    private List<Resource> datas = new ArrayList<>();
    private Resource currentResource;
    private boolean isPlayHistory = false;
    private boolean isZan = false;
    private RelativeLayout playProress;
    private ImageView imageProgress;
    AnimationDrawable spinner;
    /**
     * 播放控件
     */
    public String TAG = EducAudioPlayerActivity.class.getSimpleName();
    private static final int sDefaultTimeout = 3000;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private final int UPDATE_TITLE_BG = 3;
    private TextView mTotalTime;
    private TextView mCurrentTime;
    private ImageView audioPlay_bg;
    private ImageView audioIcon;
    private ImageButton mPauseButton;
    private ImageButton play_next;
    private SeekBar mProgress;
    private RelativeLayout playRl;
    private TextView tv_speed;
    String playPath = "";
    private boolean mShowing = false;
    private boolean mDragging;
    private boolean mInstantSeeking = false;
    private boolean isForward = false;
    private boolean isNetReceiver = false;
    private boolean isPlayLastFisish = false;
    private long mDuration;
    MediaPlayerControl mPlayer;
    private boolean isNetWork = true;
    long currentPress;
    long curentPosition;
    private Bitmap bitmap = null;
    private Bitmap mBoxBlurFilterBitmap = null;
    boolean isLastForwad = true;
    boolean isShowDialog = false;
    private String albumName;
    private View headView;

    private EducAudioService audioService;
    private PlayNextReceiver playNextReceiver;
    public NetWorkReceiver netWorkReceiver;
    public boolean isPlayNext = false;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            EducAudioService.LocalBinder binder = (EducAudioService.LocalBinder) service;
            audioService = binder.getService();
            audioService.setAudioPlay(EducAudioPlayerActivity.this);
            if (currentResource != null) {
                playPath = currentResource.url;
                play(playPath);
                show();
                audioService.canceNotifi();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 防止锁屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.educ_audio_player);
        currentResource = (Resource) getIntent().getSerializableExtra("resource");
        isPlayHistory = getIntent().getBooleanExtra("isPlayHistory", false);

        albumName = getIntent().getStringExtra("albumName");
        init();
        Intent intent = new Intent(this, EducAudioService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        Intent intentService = new Intent(mContext, EducAudioService.class);
        intentService.putExtra("resource", currentResource);
        startService(intentService);
        getData();
        if (TextUtils.isEmpty(albumName) && currentResource != null && currentResource.albumId != null) {
            getService().getResourceManager().getAlbumById(currentResource.albumId);
        }
    }

    private void init() {
        audioPlay_bg = (ImageView) findViewById(R.id.audio_icon_bg);
        mTotalTime = (TextView) findViewById(R.id.mediacontroller_time_total);
        mCurrentTime = (TextView) findViewById(R.id.mediacontroller_time_current);
        mPauseButton = (ImageButton) findViewById(R.id.mediacontroller_play_pause);
        rl_audio = (RelativeLayout) findViewById(R.id.rl_audio_icon_bg);
        play_next = (ImageButton) findViewById(R.id.mediacontroller_play_next);
        comment_views_ll = (RelativeLayout) findViewById(R.id.comment_views_ll);
        playRl = (RelativeLayout) findViewById(R.id.rl_mediacontroller);
        mProgress = (SeekBar) findViewById(R.id.mediacontroller_seekbar);
        comment_number = (TextView) findViewById(R.id.comment_number);
        playProress = (RelativeLayout) findViewById(R.id.rl_play_proess);
        imageProgress = (ImageView) findViewById(R.id.spinnerImageView);
        zan_number = (TextView) findViewById(R.id.zan_number);
        title_back = (TextView) findViewById(R.id.title_back);
        ll_info = (LinearLayout) findViewById(R.id.ll_info);
        rl_title = (RelativeLayout) findViewById(R.id.rl_title_left);
        audioIcon = (ImageView) findViewById(R.id.audio_icon);
        title = (TextView) findViewById(R.id.video_title);
        tv_speed = (TextView) findViewById(R.id.tv_speed);
        zanCB = (CheckBox) findViewById(R.id.video_zan);
        listView = (XListView) findViewById(R.id.video_list);
        spinner = (AnimationDrawable) imageProgress.getBackground();
        headView = getLayoutInflater().inflate(R.layout.educ_video_header,null);
        listView.setXListViewListener(this);
        listView.setOnItemClickListener(this);
        initHeadView();
        showHeadData();
        spinner.start();
        playProress.setVisibility(View.VISIBLE);
        updateAdapter();
        mProgress.setMax(1000);

        zanCB.setOnClickListener(this);
        mPauseButton.setOnTouchListener(this);
        mPauseButton.setOnClickListener(this);
        play_next.setOnClickListener(this);
        findViewById(R.id.rl_video_comment).setOnClickListener(this);
        findViewById(R.id.rl_video_zan).setOnClickListener(this);
        findViewById(R.id.video_left).setOnClickListener(this);
        findViewById(R.id.video_share).setOnClickListener(this);
        findViewById(R.id.comment_et).setOnClickListener(this);

        playNextReceiver = new PlayNextReceiver();
        registerReceiver(playNextReceiver, new IntentFilter(SysConstants.PLAYNEXT));

        netWorkReceiver = new NetWorkReceiver();
        registerReceiver(netWorkReceiver, new IntentFilter(SysConstants.NETWORKCHANGE));

        mProgress.setOnSeekBarChangeListener(mSeekListener);
        play_next.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (!isPlayLast(isZan)) {
                        ImageUtils.changeImgAlpha((ImageButton) view, 0);
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!isPlayLast(isZan)) {
                        ImageUtils.changeImgAlpha((ImageButton) view, -80);
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                    if (!isPlayLast(isZan)) {
                        ImageUtils.changeImgAlpha((ImageButton) view, 0);
                    }
                }
                return false;
            }
        });
        rl_audio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (rl_title.getVisibility() == View.VISIBLE) {
                    hide();
                } else {
                    show();
                }
                return false;
            }
        });
    }

    private void initHeadView() {
        videoType = (TextView) headView.findViewById(R.id.video_type);
        videoAlbumName = (TextView) headView.findViewById(R.id.video_album_name);
        videoSouse = (TextView) headView.findViewById(R.id.video_soure);
        educ_list_name = (TextView) headView.findViewById(R.id.educ_list_name);
        educ_list_tv = (TextView) headView.findViewById(R.id.educ_list_tv);
//        if (TuxingApp.VersionType == 1) {
//            educ_list_tv.setBackgroundColor(getResources().getColor(R.color.circle_pink));
//        } else if (TuxingApp.VersionType == 0){
//            educ_list_tv.setBackgroundColor(getResources().getColor(R.color.login_text_blue));
//        }else if (TuxingApp.VersionType == 2){
//            educ_list_tv.setBackgroundColor(getResources().getColor(R.color.skin_text_red));
//        }
        headView.findViewById(R.id.soure_info).setOnClickListener(this);
    }

    public void play(String videoPath) {
        try {
            int netInt = PhoneUtils.getNetWorkType(mContext);
            if(netInt == PhoneUtils.NETWORKTYPE_WIFI){
                playAudio(videoPath);
            }else if(netInt == PhoneUtils.NETWORKTYPE_MOBILE){
                if(PreferenceUtils.getPrefBoolean(mContext, SysConstants.media_folw, false)){
                    playAudio(videoPath);
                }else{
                    if(!isShowDialog){
                        isShowDialog = true;
                        showCenterBtnDialog();
                    }
                }
            }else if(netInt == PhoneUtils.NETWORKTYPE_INVALID){
                if(mPlayer != null){
                    mPlayer.pause();
                }
                showToast("当前无网络");
            }else{
                if(mPlayer != null){
                    mPlayer.pause();
                }
                showToast("当前未知网络");
            }
        } catch (Throwable a) {
            MyLog.getLogger(TAG).d("Vitamio播放音频失败   mUrl =" + playPath);
        }
    }

    public void playAudio(String mAudioPath){
        if (!TextUtils.isEmpty(mAudioPath)) {
            // 判断audio是否播放同一个资源
            if (!mAudioPath.equals(audioService.getAduioUri()) || isPlayLastFisish) {
                isPlayLastFisish = false;
                audioService.setAudioUri(currentResource, isPlayHistory);
                getService().getResourceManager().play(currentResource.id);
            }else if(isNetReceiver && mPlayer != null){
                spinner.stop();
                playProress.setVisibility(View.GONE);
                isNetReceiver = false;
                mPlayer.start();
            } else{
                spinner.stop();
                playProress.setVisibility(View.GONE);
            }
        } else {
            spinner.stop();
            playProress.setVisibility(View.GONE);
            MyLog.getLogger(TAG).d("未找到该音频");
            showToast("未找到该音频");
        }
        updatePausePlay();
    }

    @Override
    public void getData() {
        if (currentResource != null) {
            playPath = currentResource.url;
            getService().getResourceManager().getResourceById(currentResource.id);
            getService().getResourceManager().getNearResource(currentResource.id, 20, isForward);
        }

    }

    public void showHeadData() {
        if (currentResource != null) {
            ViewGroup.LayoutParams lp = audioPlay_bg.getLayoutParams();
            lp.width = Utils.getDisplayWidth(mContext);
            lp.height = lp.width * 9 / 16;
            audioPlay_bg.setLayoutParams(lp);
            ImageLoader.getInstance().displayImage(currentResource.coverImage + SysConstants.Imgurlsuffix80, audioIcon, ImageUtils.DIO_MEDIA_ICON);
            title.setText(currentResource.name);
            if (!TextUtils.isEmpty(albumName)) {
                videoAlbumName.setText(albumName);
            }
            videoSouse.setText("by " + currentResource.providerName);
            if (!TextUtils.isEmpty(currentResource.coverImage)) {
                new Thread(new LoadBoxBlurFilterBitmap(true)).start();
            } else {
                new Thread(new LoadBoxBlurFilterBitmap(false)).start();
            }
            if (!TextUtils.isEmpty(currentResource.category)) {
                videoType.setVisibility(View.VISIBLE);
                videoType.setText(currentResource.category);
            } else {
                videoType.setVisibility(View.INVISIBLE);
            }
            setCount();
            if (currentResource.liked) {
                zanCB.setSelected(true);
            } else {
                zanCB.setSelected(false);
            }
        }
    }

    public void setCount(){
        if (currentResource.commentCount != null && currentResource.commentCount > 0) {
            comment_number.setVisibility(View.VISIBLE);
            if(currentResource.commentCount > 99){
                comment_number.setBackgroundResource(R.drawable.educ_unread_msg_shape);
                comment_number.setText(" 99+ ");
            }else{
                comment_number.setBackgroundResource(R.drawable.unread_count_bg);
                comment_number.setText(String.valueOf(currentResource.commentCount));
            }
        } else {
            comment_number.setVisibility(View.GONE);
        }
        if (currentResource.likedCount != null && currentResource.likedCount > 0) {
            zan_number.setVisibility(View.VISIBLE);
            if(currentResource.likedCount > 99){
                zan_number.setBackgroundResource(R.drawable.educ_unread_msg_shape);
                zan_number.setText(" 99+ ");
            }else{
                zan_number.setBackgroundResource(R.drawable.unread_count_bg);
                zan_number.setText(String.valueOf(currentResource.likedCount));
            }
        } else {
            zan_number.setVisibility(View.GONE);
        }
    }

    private void getResresh(List<Resource> mDatas) {
        if (!CollectionUtils.isEmpty(mDatas)) {
            if (datas.size() > 0) {
                datas.addAll(0, mDatas);
            } else {
                datas.addAll(mDatas);
                datas.add(currentResource);
            }
        } else if (CollectionUtils.isEmpty(datas)) {
            datas.add(currentResource);
        }
        updateAdapter();
        listView.stopRefresh();
    }

    private void getLoad(List<Resource> mDatas) {
        if (!CollectionUtils.isEmpty(mDatas)) {
            if (datas.size() > 0) {
                datas.addAll(mDatas);
            } else {
                datas.add(currentResource);
                datas.addAll(mDatas);
            }
        } else if (isLastForwad) {
            isForward = true;
            getService().getResourceManager().getNearResource(currentResource.id, 3, isForward);
        }
        isLastForwad = false;
        updateAdapter();
        listView.stopLoadMore();
    }

    @Override
    public void onRefresh() {
        isForward = true;
        if(!CollectionUtils.isEmpty(datas)){
            long firstId = datas.get(0).id;
            getService().getResourceManager().getNearResource(firstId, 20, isForward);
        }
    }

    @Override
    public void onLoadMore() {
        isForward = false;
        long lastId = datas.get(datas.size() - 1).id;
        getService().getResourceManager().getNearResource(lastId, 20, isForward);
    }

    public void updateAdapter() {
        isPlayLast(isZan);
        if (videoAdapter == null) {
            listView.addHeaderView(headView);
            videoAdapter = new EducInfoAdapter(mContext, datas, currentResource.id);
            listView.setAdapter(videoAdapter);
        } else {
            videoAdapter.setData(currentResource.id);
        }
        showFooterView();
    }

    public void showFooterView() {
        if (hasMore) {
            listView.setPullLoadEnable(true);
        } else {
            listView.setPullLoadEnable(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (audioService != null) {
            audioService.canceNotifi();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPlayer != null && audioService != null) {
            if (!mPlayer.isPlaying()) {
                audioService.canceNotifi();
            } else {
                audioService.showNotifi();
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        try {
            if (playNextReceiver != null) {
                unregisterReceiver(playNextReceiver);
            }
            if (netWorkReceiver != null) {
                unregisterReceiver(netWorkReceiver);
            }
            if(conn != null){
                unbindService(conn);
            }
            if (mPlayer != null && audioService != null) {
                if (!mPlayer.isPlaying()) {
                    stopService(new Intent(getApplicationContext(),EducAudioService.class));
                }
            }
        } catch (IllegalArgumentException e) {
           showAndSaveLog(TAG,"注销广播 error = " + e.toString(),false);
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

            int netInt = PhoneUtils.getNetWorkType(mContext);
            if(netInt == PhoneUtils.NETWORKTYPE_WIFI){
                if(isPlayLastFisish){
                     play(playPath);

                }else{
                    doPauseResume();
                }
            }else if(netInt == PhoneUtils.NETWORKTYPE_MOBILE){
                if(PreferenceUtils.getPrefBoolean(mContext, SysConstants.media_folw, false)) {
                    if(isPlayLastFisish){
                        play(playPath);

                    }else{
                        doPauseResume();
                    }
                }else{
                    if(!isShowDialog){
                        isShowDialog = true;
                        showCenterBtnDialog();
                    }
                }
            }else if(netInt == PhoneUtils.NETWORKTYPE_INVALID){
                showToast("当前无网络");
            }else{
                showToast("当前未知网络");
            }

        } else if (view.getId() == R.id.mediacontroller_play_next) {
            int netInt = PhoneUtils.getNetWorkType(mContext);
            if(netInt == PhoneUtils.NETWORKTYPE_WIFI){
                if (audioService != null) {
                    audioService.playNexe(true);
                } else {
                    showToast("当前无播放资源");
                }
            }else if(netInt == PhoneUtils.NETWORKTYPE_MOBILE){
                if(PreferenceUtils.getPrefBoolean(mContext, SysConstants.media_folw, false)) {
                    if (audioService != null) {
                        audioService.playNexe(true);
                    } else {
                        showToast("当前无播放资源");
                    }
                }else{
                    if(!isShowDialog){
                        isShowDialog = true;
                        isPlayNext = true;
                        showCenterBtnDialog();
                    }
                }
            }else if(netInt == PhoneUtils.NETWORKTYPE_INVALID){
                showToast("当前无网络");
            }else{
                showToast("当前未知网络");
            }

        } else if (view.getId() == R.id.video_left) {
            finish();
            MobclickAgent.onEvent(this, "media_resource_clickBack", UmengData.media_resource_clickBack);
        } else if (view.getId() == R.id.soure_info) {
            showToast("功能未完善");
//            Intent intent = new Intent(this, ResourceListDetailActivity.class);
//            intent.putExtra("type", SysConstants.RESOURCES_TYPE.PROVIDER);
//            intent.putExtra("providerId", currentResource.providerId);
//            startActivity(intent);
        } else if (view.getId() == R.id.rl_video_comment || view.getId() == R.id.comment_et) {
            showToast("功能未完善");
//            if (currentResource != null) {
//                Intent intent = new Intent(mContext, EducCommentActivity.class);
//                intent.putExtra("resource", currentResource);
//                startActivityForResult(intent, 100);
//            }
        } else if (view.getId() == R.id.video_zan || view.getId() == R.id.rl_video_zan) {
            if (currentResource != null) {
                if (currentResource.liked) {
                    showToast(getResources().getString(R.string.media_prompt));
                } else {
                    showProgressDialog(mContext, null, true, null);
                    showPopupWindow(view);
                    getService().getCommentManager().publishComment(currentResource.id, TargetType.RESOURCE, null, null, null, CommentType.LIKE);
                    MobclickAgent.onEvent(this, "media_resource_like", UmengData.media_resource_like);
                }
            } else {
                showToast("当前无播放资源");
            }
        } else if (view.getId() == R.id.video_share) {
            showToast("video_share");
        }

    }

    public void onEventMainThread(ResourceEvent event) {
        disProgressDialog();
        if (isActivity) {
            switch (event.getEvent()) {
                /*case GET_RESOURCE_BY_ALBUM_SUCCESS:
					getResresh(event.getResources());
					hasMore = event.isHasMore();
					showAndSaveLog(TAG, "获得资源列表成功 ", false);
					break;
				case GET_RESOURCE_BY_ALBUM_FAILED:
					showToast(event.getMsg());
					showAndSaveLog(TAG, "获得资源列表失败", false);
					break;*/
                case GET_NEAR_RESOURCE_SUCCESS:
                    if (isForward) {
                        getResresh(event.getResources());
                    } else {
                        getLoad(event.getResources());
                    }
                    hasMore = event.isHasMore();
                    showAndSaveLog(TAG, "获得资源列表成功 ", false);
                    break;
                case GET_NEAR_RESOURCE_FAILED:
                    showToast(event.getMsg());
                    showAndSaveLog(TAG, "获得资源列表失败", false);
                    break;
                case GET_RESOURCE_BY_ID_SUCCESS:
                    if (CollectionUtils.isEmpty(event.getResources())) {
                        return;
                    }
                    currentResource = event.getResources().get(0);
                    setCount();
                    if (currentResource.liked) {
                        zanCB.setSelected(true);
                    } else {
                        zanCB.setSelected(false);
                    }
                    isZan = true;
                    updateAdapter();
                    showAndSaveLog(TAG, "获得资源成功", false);
                    break;
                case GET_RESOURCE_BY_ID_FAILED:
                    showToast(event.getMsg());
                    showAndSaveLog(TAG, "获得资源失败", false);
                    break;
            }
        }
    }

    public void onEventMainThread(CommentEvent event) {
        if (isActivity) {
            switch (event.getEvent()) {
                case REPLAY_RESOURCE_SUCCESS:
                    getService().getResourceManager().getResourceById(currentResource.id);
                    if (currentResource != null) {
                        sedBroadcase(currentResource.id);
                    }
                    showAndSaveLog(TAG, "audio赞成功 resourecId = " + currentResource.id, false);
                    break;
                case REPLAY_RESOURCE_FAILED:
                    disProgressDialog();
                    showToast(event.getMsg());
                    showAndSaveLog(TAG, "audio赞失败 resourecId = " + currentResource.id, false);
                    break;
            }
        }
    }
    public void onEventMainThread(AlbumEvent event) {
        if (isActivity) {
            switch (event.getEvent()) {
                case GET_ALBUM_BY_ID_SUCCESS:
                     if(!CollectionUtils.isEmpty(event.getAlbums()) && !TextUtils.isEmpty(event.getAlbums().get(0).name)){
                           videoAlbumName.setText(event.getAlbums().get(0).name);
                     }
                    break;
                case GET_ALBUM_BY_ID_FAILED:
                    showToast(event.getMsg());
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data.getBooleanExtra("commentSuccess", false)) {
                isActivity = true;
                getService().getResourceManager().getResourceById(currentResource.id);

            }
        }
    }

    public void sedBroadcase(long id) {
        Intent intent = new Intent(SysConstants.UPDATEMEDIACOMMENT);
        intent.putExtra("resourceId", id);
        sendBroadcast(intent);
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
                case UPDATE_TITLE_BG:
                    if (mBoxBlurFilterBitmap != null) {
                        Animation alphaAnimation = AnimationUtils.loadAnimation(
                                EducAudioPlayerActivity.this, R.anim.show_blur_img);
                        audioPlay_bg.setAnimation(alphaAnimation);
                        audioPlay_bg.setImageBitmap(mBoxBlurFilterBitmap);
                    } else {
                        new Thread(new LoadBoxBlurFilterBitmap(false)).start();
                    }
                    break;
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        public void onStartTrackingTouch(SeekBar bar) {
            mDragging = true;
            show(3600000);
        }

        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser)
                return;

            long newposition = (mDuration * progress) / 1000;
            String time = StringUtils.generateTime(newposition);
            if (mInstantSeeking)
                mPlayer.seekTo(newposition);
            if (mCurrentTime != null)
                mCurrentTime.setText(time);
        }

        public void onStopTrackingTouch(SeekBar bar) {
            if (!mInstantSeeking)
                mPlayer.seekTo((mDuration * bar.getProgress()) / 1000);
            show(sDefaultTimeout);
            mDragging = false;
        }
    };


    private long setProgress() {
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

        }

        if (mCurrentTime != null)
            mCurrentTime.setText(StringUtils.generateTime(curentPosition));
        if (mTotalTime != null)
            mTotalTime.setText(StringUtils.generateTime(mDuration));
        return curentPosition;
    }

    private void updatePausePlay() {
        if(mPlayer != null){
            if (mPlayer.isPlaying())
                mPauseButton.setImageResource(getResources().getIdentifier("mediacontroller_pause", "drawable", getPackageName()));
            else
                mPauseButton.setImageResource(getResources().getIdentifier("mediacontroller_play", "drawable", getPackageName()));
        }

    }

    private void doPauseResume() {
        if (mPlayer.isPlaying()){
            mPlayer.pause();
//            MobclickAgent.onEvent(this, "media_resource_audioPause", UmengData.media_resource_audioPause);
        }
        else{
            mPlayer.start();
//            MobclickAgent.onEvent(this, "media_resource_audioPlay", UmengData.media_resource_audioPlay);
        }

        updatePausePlay();
    }

    public void hide() {
        if (mShowing) {
            try {
//                rl_title.startAnimation(AnimationUtils.loadAnimation(EducAudioPlayerActivity.this,
//                        R.anim.edu_pic_title_hide));
//                rl_title.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        rl_title.setVisibility(View.GONE);
//                    }
//                }, 180);

            } catch (IllegalArgumentException ex) {
                MyLog.getLogger(TAG).d("MediaController already removed");
            }
            mShowing = false;
        }
    }

    public void show() {
        show(sDefaultTimeout);
    }

    public void show(int timeout) {
        if (!TextUtils.isEmpty(playPath)) {
            if (!mShowing) {
                if (mPauseButton != null)
                    mPauseButton.requestFocus();
                if (playRl.getVisibility() == View.GONE) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                           setProgress();
                            mHandler.postDelayed(this, 1000);
                        }
                    }, 1000);
                    playRl.setVisibility(View.VISIBLE);
                    playRl.startAnimation(AnimationUtils.loadAnimation(EducAudioPlayerActivity.this,
                            R.anim.edu_pic_bottom_show));
                    rl_title.setVisibility(View.VISIBLE);
                    rl_title.startAnimation(AnimationUtils.loadAnimation(EducAudioPlayerActivity.this,
                            R.anim.edu_pic_title_show));
                }
                mShowing = true;
            }
            updatePausePlay();


            if (timeout != 0) {
                mHandler.removeMessages(FADE_OUT);
                mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT), timeout);
            }
        }
    }

    @Override
    public void onCompletion(android.media.MediaPlayer mp) {
//播放完毕
        mPauseButton.setImageResource(getResources().getIdentifier("mediacontroller_play", "drawable", getPackageName()));
        if (!PhoneUtils.isNetworkAvailable(mContext)) {
            currentPress = mProgress.getProgress();
            curentPosition = mDuration * currentPress / 1000L;
            tv_speed.setVisibility(View.GONE);
            isNetWork = false;
        } else {
            if (isPlayLast(isZan)) {
                isPlayLastFisish = true;
                currentPress = 0;
                curentPosition = 0;
            } else {
//                if (audioService != null) {
//                    audioService.playNexe(true);
//                } else {
//                    showToast("当前无播放资源");
//                }
                if (isActivity) {
                    showProgressDialog(mContext, null, true, null);
                }
            }
        }
    }

    @Override
    public void onPrepared(android.media.MediaPlayer mp) {
        playProress.setVisibility(View.GONE);
        spinner.stop();
        mPlayer.start();
        show();
        disProgressDialog();
    }

    @Override
    public void onError(android.media.MediaPlayer mp) {
        hide();
    }

    @Override
    public void onBufferingUpdate(int percent) {
        mProgress.setSecondaryProgress(percent * 10);
    }

    @Override
    public void bufferingStart() {
        if (!PhoneUtils.isNetworkAvailable(mContext)) {
            currentPress = mProgress.getProgress();
            curentPosition = mDuration * currentPress / 1000L;
            tv_speed.setVisibility(View.GONE);
            isNetWork = false;
        } else {
            tv_speed.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void bufferingEnd() {
        tv_speed.setVisibility(View.GONE);
        isNetWork = true;
    }

    @Override
    public void bufferingChange(int percent) {
        tv_speed.setText(percent + "kb/s");
    }

    @Override
    public void setMediaPlayerControl(MediaPlayerControl mMediaPlayerControl) {
        this.mPlayer = mMediaPlayerControl;
    }

    @Override
    public boolean isShowing() {
        return mShowing;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
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

    public boolean isPlayLast(boolean mIsZan) {
        if (!CollectionUtils.isEmpty(datas) && currentResource != null) {
            for (int i = 0; i < datas.size(); i++) {
                if (datas.get(i).id.equals(currentResource.id)) {
                    if (mIsZan) {
                        isZan = false;
                        datas.remove(i);
                        datas.add(i, currentResource);
                    }
                    if (i == datas.size() - 1) {
                        ImageUtils.changeImgAlpha(play_next, -80);
                        return true;
                    } else {
                        ImageUtils.changeImgAlpha(play_next, 0);
                        return false;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onclickFlowAll() {
        super.onclickFlowAll();
        isShowDialog = false;
        PreferenceUtils.setPrefBoolean(mContext, SysConstants.media_folw, true);
        showDialog("", getResources().getString(R.string.media_flow_all_msg), null, getResources().getString(R.string.media_flow_all_msg_que),false);
    }



    @Override
    public void onConfirm() {
        super.onConfirm();
        if(!isPlayNext){
            if(currentPress != 0 && mPlayer != null){
                mPlayer.start();
                updatePausePlay();
            }else{
                play(playPath);
            }
        }else{
            isPlayNext = false;
            if (audioService != null) {
                audioService.playNexe(true);
            } else {
                showToast("当前无播放资源");
            }
        }

    }

    @Override
    public void onCancel() {
        super.onCancel();
        isShowDialog = false;
        isPlayNext = false;
        if(spinner != null){
            spinner.stop();
        }
        playProress.setVisibility(View.GONE);
    }

    class PlayNextReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SysConstants.PLAYNEXT)) {
                if(intent.getBooleanExtra("isPlayNext",false)){
                    currentResource = (Resource) intent.getSerializableExtra("resource");
                    showHeadData();
                    updateAdapter();
                }else{
                    finish();
                }

            }
        }
    }

    class NetWorkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(isActivity){
                if (intent.getAction().equals(SysConstants.NETWORKCHANGE)) {
                    if(mPlayer != null){
                        mPlayer.pause();
                        isNetReceiver = true;
                        updatePausePlay();
                    }
                    play(currentResource.url);
                }
            }
        }
    }



    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        position = position - 2;
        if (!CollectionUtils.isEmpty(datas) && position >= 0) {
            if (!datas.get(position).id.equals(currentResource.id)) {
                currentResource = datas.get(position);
                showHeadData();
                hide();
                play(currentResource.url);
                updateAdapter();
//                MobclickAgent.onEvent(this, "media_resource_clickResource", UmengData.media_resource_clickResource);
            }
        }
    }

    private class LoadBoxBlurFilterBitmap implements Runnable {
        boolean isCoverImage = true;

        LoadBoxBlurFilterBitmap(boolean isCoverImage) {
            this.isCoverImage = isCoverImage;
        }

        @Override
        public void run() {
            if (isCoverImage) {
                bitmap = ImageUtils.getBitmap(currentResource.coverImage + SysConstants.Imgurlsuffix134, 134, 134);
                mBoxBlurFilterBitmap = ImageUtils.BoxBlurFilterBitmap(bitmap, 3, 3, 3);
                mHandler.sendEmptyMessage(UPDATE_TITLE_BG);
            } else {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.resource_detail_icon);
                mBoxBlurFilterBitmap = ImageUtils.BoxBlurFilterBitmap(bitmap, 3, 3, 3);
                mHandler.sendEmptyMessage(UPDATE_TITLE_BG);
            }
        }
    }
}
