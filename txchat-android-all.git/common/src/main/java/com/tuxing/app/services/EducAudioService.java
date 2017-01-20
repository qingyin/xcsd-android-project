package com.tuxing.app.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.tuxing.app.MainActivity;
import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.activity.EducAudioPlayerActivity;
import com.tuxing.app.util.EducAudioPlayListener;
import com.tuxing.app.util.ImageUtils;
import com.tuxing.app.util.MyLog;
import com.tuxing.app.util.PhoneUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.Utils;
import com.tuxing.app.view.BubblePlayView;
import com.tuxing.rpc.proto.Resource;
import com.tuxing.sdk.event.resource.ResourceEvent;
import com.tuxing.sdk.facade.CoreService;
import com.tuxing.sdk.utils.CollectionUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.vov.vitamio.widget.MediaPlayerControl;


public class EducAudioService extends Service implements MediaPlayerControl {
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;
    public static int mCurrentState = STATE_IDLE;
    private int mTargetState = STATE_IDLE;
    private String mUri;
    private EducAudioPlayListener mMediaController;
    private long mDuration;
    private long mSeekWhenPrepared;
    private int mCurrentBufferPercentage;
    private static final int sDefaultTimeout = 1000;
    private final IBinder mBinder = new LocalBinder();
    private MediaPlayer mediaPlayer;
    private final static String TAG = EducAudioService.class.getSimpleName();
    public ButtonBroadcastReceiver bReceiver;
    public AudioShowReceiver audioReceiver;
    public NetWorkReceiver netWorkReceiver;
    public PauseReceiver pauseReceiver;
    public final static String ACTION_BUTTON = "com.tuixng.app.ButtonClick";
    public NotificationManager mNotificationManager;
    private Notification notify;
    public final static String INTENT_BUTTONID_TAG = "ButtonId";
    public final static int BUTTON_CLOSE_ID = 1;
    public final static int BUTTON_PALY_ID = 2;
    public final static int BUTTON_NEXT_ID = 3;
    public final static int NET_CHANGE = 4;

    private static Logger logger = LoggerFactory.getLogger(EducAudioService.class);
    private boolean mReflectFlg = false;
    private static final int NOTIFICATION_ID = 101; // 如果id设置为0,会导致不能设置为前台service
    private static final Class<?>[] mSetForegroundSignature = new Class[]{boolean.class};
    private static final Class<?>[] mStartForegroundSignature = new Class[]{int.class, Notification.class};
    private static final Class<?>[] mStopForegroundSignature = new Class[]{boolean.class};
    private Method mSetForeground;
    private Method mStartForeground;
    private Method mStopForeground;
    private Object[] mSetForegroundArgs = new Object[1];
    private Object[] mStartForegroundArgs = new Object[2];
    private Object[] mStopForegroundArgs = new Object[1];
    private Resource currentResource;
    private boolean isPlayHistory = false;
    private ImageView icon;
    private ImageView icon_bg;
    private RemoteViews mRemoteViews;
    private BubblePlayView playAudioView;
    Handler handler=new Handler();
    private boolean isShowNotify = false;
    private boolean isPrepared = false;
    private Bitmap bitmap = null;
    private Bitmap mBoxBlurFilterBitmap = null;
    private final int UPDATE_TITLE_BG = 4;
    private List<Resource> datas = new ArrayList<>();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TITLE_BG:
                    if(mRemoteViews != null && mBoxBlurFilterBitmap != null){
                        mRemoteViews.setImageViewBitmap(R.id.custom_icon_bg, mBoxBlurFilterBitmap);
                    }
                    if(isShowNotify){
                        mNotificationManager.notify(NOTIFICATION_ID, notify);
                    }
                    break;
            }
        }
    };
    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
            mCurrentState = STATE_PREPARED;
            long seekToPosition = mSeekWhenPrepared;
            isPrepared = true;
            if (seekToPosition != 0)
                seekTo(seekToPosition);
            if (mMediaController != null) {
                mMediaController.onPrepared(mp);
            }
        }
    };

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mp) {
            mCurrentState = STATE_PLAYBACK_COMPLETED;
            mTargetState = STATE_PLAYBACK_COMPLETED;
            if(playAudioView != null){
                playAudioView.stopRotation();
            }
            if (PhoneUtils.isNetworkAvailable(getApplicationContext())) {
                playNexe(true);
            }
            if(mRemoteViews != null && isShowNotify){
                mRemoteViews.setImageViewResource(R.id.custom_play, R.drawable.mediacontroller_play);
                mNotificationManager.notify(NOTIFICATION_ID, notify);
            }
            if (mMediaController != null){
                mMediaController.onCompletion(mp);
            }
        }
    };
    private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
        public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            if (mMediaController != null){
                mMediaController.onError(mp);
            }

            return true;
        }
    };
    private OnBufferingUpdateListener mBufferingUpdateListener = new OnBufferingUpdateListener() {
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            mCurrentBufferPercentage = percent;
            if (mMediaController != null){
                mMediaController.onBufferingUpdate(percent);
            }
        }
    };
    private MediaPlayer.OnInfoListener mInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {


          if (mediaPlayer != null) {
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    mediaPlayer.pause();
                    if (mMediaController != null)
                        mMediaController.bufferingStart();
                } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    mediaPlayer.start();

                    if (mMediaController != null)
                        mMediaController.bufferingEnd();
                }else if(what == MediaPlayer.MEDIA_INFO_METADATA_UPDATE){
                    if (mMediaController != null)
                        mMediaController.bufferingChange(extra);
                }
            }
            return true;
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        try {
            mStartForeground = EducAudioService.class.getMethod("startForeground", mStartForegroundSignature);
            mStopForeground = EducAudioService.class.getMethod("stopForeground", mStopForegroundSignature);
        } catch (NoSuchMethodException e) {
            mStartForeground = mStopForeground = null;
        }
        EventBus.getDefault().register(this);

        try {
            mSetForeground = getClass().getMethod("setForeground",
                    mSetForegroundSignature);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("");
        }
        showAudioView();
        openVideo();

        initButtonReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("debug", "onStartCommnad");
        if(intent != null){
            currentResource = (Resource) intent.getSerializableExtra("resource");
            isPlayHistory = intent.getBooleanExtra("isPlayHistory", false);
        }
        showButtonNotify();
        if(playAudioView != null){
            playAudioView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intnet = new Intent(getApplicationContext(), EducAudioPlayerActivity.class);
                    intnet.putExtra("resource", currentResource);
                    intnet.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intnet);
                }
            });
        }
        return START_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }


    private void openVideo() {
    if(TextUtils.isEmpty(mUri)){
        return;
    }
        release(false);
        try {
            mDuration = -1;
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnPreparedListener(mPreparedListener);
            mediaPlayer.setOnCompletionListener(mCompletionListener);
            mediaPlayer.setOnErrorListener(mErrorListener);
            mediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mediaPlayer.setOnInfoListener(mInfoListener);
            mediaPlayer.setDataSource(mUri);
            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.prepareAsync();
            mCurrentState = STATE_PREPARING;
        } catch (IOException ex) {
            io.vov.vitamio.utils.Log.e("Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mediaPlayer, io.vov.vitamio.MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            return;
        } catch (IllegalArgumentException ex) {
            io.vov.vitamio.utils.Log.e("Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mediaPlayer, io.vov.vitamio.MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            return;
        }
    }
    public void setAudioPlay(EducAudioPlayListener mMediaController){
        this.mMediaController = mMediaController;
        mMediaController.setMediaPlayerControl(this);
    }

    @Override
    public void start() {
        if (isInPlaybackState()) {
            mediaPlayer.start();
            mCurrentState = STATE_PLAYING;
        }
        if(playAudioView != null && isShowNotify){
            playAudioView.startRotation();
        }
        if(mRemoteViews != null && isShowNotify){
            mRemoteViews.setImageViewResource(R.id.custom_play, R.drawable.mediacontroller_pause);
            mNotificationManager.notify(NOTIFICATION_ID,notify);
        }

        mTargetState = STATE_PLAYING;
    }
    @Override
    public void pause() {
        if (isInPlaybackState()) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                mCurrentState = STATE_PAUSED;
            }
        }
        if(mRemoteViews != null && isShowNotify){
            mRemoteViews.setImageViewResource(R.id.custom_play, R.drawable.mediacontroller_play);
            mNotificationManager.notify(NOTIFICATION_ID,notify);
        }
        if(playAudioView != null && isShowNotify){
            playAudioView.stopRotation();
        }
        mTargetState = STATE_PAUSED;
    }

    @Override
    public long getDuration() {
        if (isInPlaybackState() && isPrepared) {
            if (mDuration > 0)
                return mDuration;
            mDuration = mediaPlayer.getDuration();
            return mDuration;
        }
        mDuration = 0;
        return mDuration;
    }

    @Override
    public long getCurrentPosition() {
        if (isInPlaybackState())
            return mediaPlayer.getCurrentPosition();
        return 0;
    }

    @Override
    public void seekTo(long msec) {
        if (isInPlaybackState()) {
            mediaPlayer.seekTo((int)msec);
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = msec;
        }
    }

    @Override
    public boolean isPlaying() {
        if(mediaPlayer == null){
            return false;
        }
        return  mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        if (mediaPlayer != null)
            return mCurrentBufferPercentage;
        return 0;
    }


    public void setAudioUri(Resource mResoutce,boolean isPlayHistory) {
        this.mUri = mResoutce.url;
        isPrepared = false;
        this.isPlayHistory = isPlayHistory;
        this.currentResource = mResoutce;
        mSeekWhenPrepared = 0;
        openVideo();
    }

    public String getAduioUri(){
        return  this.mUri;
    }

    public void playNexe(boolean isNext){
        if(currentResource != null){
            CoreService.getInstance().getResourceManager().playNext(currentResource.id,isPlayHistory);
        }
    }


    public void stopPlay(){
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            mCurrentState = STATE_IDLE;
            mTargetState = STATE_IDLE;
        }
    }

    public void setDatas(List<Resource> mDatas){
        this.datas = mDatas;
    }

   
    private void release(boolean cleartargetstate) {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            mCurrentState = STATE_IDLE;
            if (cleartargetstate)
                mTargetState = STATE_IDLE;
        }
    }



    public void onDestroy() {
        canceNotifi();
        stopPlay();
        EventBus.getDefault().unregister(this);
        if (bReceiver != null) {
            unregisterReceiver(bReceiver);
        }
        if (audioReceiver != null) {
            unregisterReceiver(audioReceiver);
        }
        if (netWorkReceiver != null) {
            unregisterReceiver(netWorkReceiver);
        }
        if (pauseReceiver != null) {
            unregisterReceiver(pauseReceiver);
        }
        if(playAudioView != null){
            playAudioView.dismiss();
        }

    }

    protected boolean isInPlaybackState() {
        return (mediaPlayer != null && mCurrentState != STATE_ERROR && mCurrentState != STATE_IDLE);
    }

    public void showNotifi(){
        isShowNotify = true;
        playAudioView.show();
        playAudioView.startRotation();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                playAudioView.setProgressPercent((float)getCurrentPosition()/((float)getDuration()));
                handler.postDelayed(this, sDefaultTimeout);
            }
        }, sDefaultTimeout);
        setNotifiData();
        startForegroundCompat(NOTIFICATION_ID, notify);
    }

    public void canceNotifi(){
        isShowNotify = false;
        playAudioView.dismiss();
        stopForegroundCompat(NOTIFICATION_ID);
    }
    public void startForegroundCompat(int id, Notification notification) {
        if (mReflectFlg) {
            if (mStartForeground != null) {
                mStartForegroundArgs[0] = Integer.valueOf(id);
                mStartForegroundArgs[1] = notification;
                invokeMethod(mStartForeground, mStartForegroundArgs);
                return;
            }
            mSetForegroundArgs[0] = Boolean.TRUE;
            invokeMethod(mSetForeground, mSetForegroundArgs);
            mNotificationManager.notify(id, notification);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                startForeground(id, notification);
            } else {
                mNotificationManager.notify(0, notification);
            }
        }
    }

    public void stopForegroundCompat(int id) {
        if (mReflectFlg) {
            if (mStopForeground != null) {
                mStopForegroundArgs[0] = Boolean.TRUE;
                invokeMethod(mStopForeground, mStopForegroundArgs);
                return;
            }
            mNotificationManager.cancel(id);
            mSetForegroundArgs[0] = Boolean.FALSE;
            invokeMethod(mSetForeground, mSetForegroundArgs);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                stopForeground(true);
            } else {
                mNotificationManager.cancel(0);
            }
        }
    }

    void invokeMethod(Method method, Object[] args) {
        try {
            method.invoke(this, args);
        } catch (InvocationTargetException e) {
            logger.error("Unable to invoke method", e);
        } catch (IllegalAccessException e) {
            logger.error("Unable to invoke method", e);
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    public class LocalBinder extends Binder {
        public EducAudioService getService() {
            return EducAudioService.this;
        }

    }

    public void showButtonNotify(){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_audio_notification_layout, null);
         icon = (ImageView) view.findViewById(R.id.custom_icon);
         icon_bg = (ImageView) view.findViewById(R.id.custom_icon_bg);
         mRemoteViews = new RemoteViews(getPackageName(), R.layout.activity_audio_notification_layout);
        if(currentResource != null){
            if(!TextUtils.isEmpty(currentResource.coverImage)){
                new Thread(new LoadBoxBlurFilterBitmap(true)).start();
            }else{
                new Thread(new LoadBoxBlurFilterBitmap(false)).start();
            }
            mRemoteViews.setTextViewText(R.id.custom_title, currentResource.name);
            ImageLoader.getInstance().displayImage(currentResource.coverImage,icon, ImageUtils.DIO_MEDIA_ICON,new SimpleImageLoadingListener(){
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    mRemoteViews.setImageViewBitmap(R.id.custom_icon, loadedImage);
                }
            });
        }
        Intent buttonIntent_close = new Intent(ACTION_BUTTON);
        buttonIntent_close.putExtra(INTENT_BUTTONID_TAG, BUTTON_CLOSE_ID);
        PendingIntent intent_close = PendingIntent.getBroadcast(this, 1, buttonIntent_close, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.custom_close, intent_close);

        Intent buttonIntent_play = new Intent(ACTION_BUTTON);
        buttonIntent_play.putExtra(INTENT_BUTTONID_TAG, BUTTON_PALY_ID);
        PendingIntent intent_plsy = PendingIntent.getBroadcast(this, 2, buttonIntent_play, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.custom_play, intent_plsy);

        Intent buttonIntent_next = new Intent(ACTION_BUTTON);
        buttonIntent_next.putExtra(INTENT_BUTTONID_TAG, BUTTON_NEXT_ID);
        PendingIntent intent_next = PendingIntent.getBroadcast(this, 3, buttonIntent_next, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.custom_next, intent_next);

        Intent intent_main = new Intent(ACTION_BUTTON);
        intent_main.putExtra(INTENT_BUTTONID_TAG, NET_CHANGE);
        PendingIntent intent_start = PendingIntent.getBroadcast(this, 4, intent_main, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContent(mRemoteViews)
                    .setContentIntent(intent_start)
                    .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                    .setTicker("正在播放")
                    .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                    .setOngoing(true);

//        if(TuxingApp.VersionType == 0){
//            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
//                mBuilder.setSmallIcon(R.drawable.app_logo_n);
//            } else {
//                mBuilder.setSmallIcon(R.drawable.app_logo_p);
//            }
//        } else if (TuxingApp.VersionType == 1) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                mBuilder.setSmallIcon(R.drawable.app_logo_n);
//            } else {
//                mBuilder.setSmallIcon(R.drawable.app_logo_t);
//            }
//        } else if (TuxingApp.VersionType == 2) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                mBuilder.setSmallIcon(R.drawable.app_logo_n);
//            } else {
//                mBuilder.setSmallIcon(R.drawable.app_logo_k);
//            }
//        }
        notify = mBuilder.build();
        notify.flags = Notification.FLAG_ONGOING_EVENT;

    }

    public void setNotifiData(){
        if(currentResource != null && isShowNotify){
            if(!TextUtils.isEmpty(currentResource.coverImage)){
                new Thread(new LoadBoxBlurFilterBitmap(true)).start();
            }else{
                new Thread(new LoadBoxBlurFilterBitmap(false)).start();
            }

            mRemoteViews.setTextViewText(R.id.custom_title, currentResource.name);
            playAudioView.setDisplayImage(currentResource.coverImage);
            ImageLoader.getInstance().displayImage(currentResource.coverImage, icon, ImageUtils.DIO_MEDIA_ICON, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    mRemoteViews.setImageViewBitmap(R.id.custom_icon, loadedImage);
                    mNotificationManager.notify(NOTIFICATION_ID, notify);

                }
            });

        }
    }
    public void initButtonReceiver(){
        bReceiver = new ButtonBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_BUTTON);
        registerReceiver(bReceiver, intentFilter);

        audioReceiver = new AudioShowReceiver();
        registerReceiver(audioReceiver, new IntentFilter(SysConstants.HIDEANDSHOWAUDIO));

        netWorkReceiver = new NetWorkReceiver();
        registerReceiver(netWorkReceiver, new IntentFilter(SysConstants.NETWORKCHANGE));

        pauseReceiver = new PauseReceiver();
        registerReceiver(pauseReceiver, new IntentFilter(SysConstants.PAUSAUDIO));
    }

    public class ButtonBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if(action.equals(ACTION_BUTTON)){
                //通过传递过来的ID判断按钮点击属性或者通过getResultCode()获得相应点击事件
                int buttonId = intent.getIntExtra(INTENT_BUTTONID_TAG, 0);
                switch (buttonId) {
                    case BUTTON_CLOSE_ID:
                        Intent intentFinish = new Intent(SysConstants.PLAYNEXT);
                        intentFinish.putExtra("isPlayNext",false);
                        sendBroadcast(intentFinish);
                        stopSelf();
                        break;
                    case BUTTON_PALY_ID:
                        if(isPlaying()){
                           pause();
                        }else{
                            start();
                        }
                        break;
                    case BUTTON_NEXT_ID:
                        playNexe(true);
                        break;
                    case NET_CHANGE:
                        if(Utils.isAppOnForeground(getApplicationContext())){
                            Intent intnet = new Intent(getApplicationContext(), EducAudioPlayerActivity.class);
                            intnet.putExtra("resource", currentResource);
                            intnet.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intnet);
                        }else{
                            Intent mainIntent_net = new Intent(getApplicationContext(), MainActivity.class);
                            mainIntent_net.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Intent audioIntent_net = new Intent(getApplicationContext(), EducAudioPlayerActivity.class);
                            audioIntent_net.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            audioIntent_net.putExtra("resource", currentResource);
                            Intent[] intents_net = new Intent[]{mainIntent_net,audioIntent_net};
                            startActivities(intents_net);
                        }
                        break;
                }
            }
        }
    }

    class NetWorkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SysConstants.NETWORKCHANGE)) {
                if(!getTopActivity().contains(EducAudioPlayerActivity.class.getSimpleName())){
                    pause();
                    Intent netIntent = new Intent(ACTION_BUTTON);
                    netIntent.putExtra(INTENT_BUTTONID_TAG, NET_CHANGE);
                    context.sendBroadcast(netIntent);
                }
            }
        }
    }
    class PauseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SysConstants.PAUSAUDIO)) {
                if(mediaPlayer != null){
                if(intent.getBooleanExtra("isStop",false)){
                    pause();
                }else{
                    start();
                }
            }}
        }
    }

    public class AudioShowReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(SysConstants.HIDEANDSHOWAUDIO)){
                boolean hideFlag = intent.getBooleanExtra("hide",false);
                if(hideFlag && playAudioView != null){
                        playAudioView.dismiss();
                    }else if(playAudioView != null && isShowNotify) {
                        playAudioView.show();
                    }
            }
        }
    }

    public void onEventMainThread(ResourceEvent event) {
            switch (event.getEvent()) {
                case GET_NEXT_RESOURCE_SUCCESS:
                    Intent intent = new Intent(SysConstants.PLAYNEXT);
                    if(!CollectionUtils.isEmpty(event.getResources())){
                        currentResource = event.getResources().get(0);
                        setAudioUri(currentResource, isPlayHistory);
                        CoreService.getInstance().getResourceManager().play(currentResource.id);
                        intent.putExtra("resource",currentResource);
                        intent.putExtra("isPlayNext",true);
                        sendBroadcast(intent);
                    }

                    setNotifiData();
                    MyLog.getLogger(TAG).d("播放下一首成功");
                    break;
                case GET_NEXT_RESOURCE_FAILED:
                    Toast.makeText(getApplicationContext(),event.getMsg(),Toast.LENGTH_SHORT).show();
                    MyLog.getLogger(TAG).d("播放下一首失败");
                    break;
        }
    }

    public void showAudioView(){
        playAudioView = new BubblePlayView(this);
        playAudioView.setOffsetOuter(6);
        playAudioView.setArcWidth(4);
        playAudioView.setOffsetInner(2);
        playAudioView.setArcColor(getResources().getColor(android.R.color.holo_blue_light));
        playAudioView.setCDImg(R.drawable.defal_bubble_icon);
    }
    private class LoadBoxBlurFilterBitmap implements Runnable {
        boolean isCoverImage = true;
        LoadBoxBlurFilterBitmap(boolean isCoverImage){
            this.isCoverImage = isCoverImage;
        }
        @Override
        public void run() {
            if(isCoverImage){
                bitmap = ImageUtils.getBitmap(currentResource.coverImage + SysConstants.Imgurlsuffix134, 134, 134);
                mBoxBlurFilterBitmap = ImageUtils.BoxBlurFilterBitmap(bitmap, 3, 3, 3);
                mHandler.sendEmptyMessage(UPDATE_TITLE_BG);
            }else{
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.resource_detail_icon);
                mBoxBlurFilterBitmap = ImageUtils.BoxBlurFilterBitmap(bitmap, 3, 3, 3);
                mHandler.sendEmptyMessage(UPDATE_TITLE_BG);
            }
        }
    }

     public String getTopActivity(){
        ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        if(runningTaskInfos != null)
            return (runningTaskInfos.get(0).topActivity).toString();
        else
            return null ;
    }
}
