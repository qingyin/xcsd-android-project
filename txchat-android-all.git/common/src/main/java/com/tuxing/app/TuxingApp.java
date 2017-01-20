
package com.tuxing.app;


import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.easemob.chat.EMChatConfig;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.bugly.crashreport.CrashReport.UserStrategy;
import com.tuxing.app.easemob.iml.TuxingHXSDKHelper;
import com.tuxing.app.easemob.ui.ChatActivity;
import com.tuxing.app.services.MyPushIntentService;
import com.tuxing.app.util.MyLog;
import com.tuxing.app.util.PhoneUtils;
import com.tuxing.app.util.SysConstants;
import com.tuxing.app.util.UmengData;
import com.tuxing.app.util.Utils;
import com.tuxing.sdk.facade.CoreService;
import com.tuxing.sdk.utils.Constants;
import com.umeng.analytics.MobclickAgent;
import com.umeng.common.message.UmengMessageDeviceConfig;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.yixia.camera.VCamera;
import com.yixia.camera.util.Log;

import java.io.File;

public class TuxingApp extends MultiDexApplication {

    public static Context applicationContext;
    private static TuxingApp instance;
    public static String VersionType = "";
    public static String packageName = "";
    public static String VersionName = "";
    // login user name
    private String TAG = TuxingApp.class.getSimpleName();
    public static PushAgent mPushAgent;
    public static boolean videoErroy = false;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        instance = this;
        initPackageConfig();
        initPush();
        startIMService();
        initVideoRecorde();
        initBugly();
        initImageLoader(this.getApplicationContext());
        initShareKey();//初始化umeng share key
        TuxingHXSDKHelper.getInstance().onInit(applicationContext);
        MyLog.getLogger(TAG).d("hxSDKHelper");

    }

    private void initPush() {
        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setDebugMode(true);

        mPushAgent.setPushCheck(true);

        UmengMessageHandler messageHandler = new UmengMessageHandler(){
            /**
             * 参考集成文档的1.6.3
             * http://dev.umeng.com/push/android/integration#1_6_3
             * */
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                new Handler().post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        // 对自定义消息的处理方式，点击或者忽略
                        Toast.makeText(context,msg+"",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            /**
             * 参考集成文档的1.6.4
             * http://dev.umeng.com/push/android/integration#1_6_4
             * */
//            @Override
//            public Notification getNotification(Context context,
//                                                UMessage msg) {
//                switch (msg.builder_id) {
//                    case 1:
//                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//                        RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.notification_view);
//                        myNotificationView.setTextViewText(R.id.notification_title, msg.title);
//                        myNotificationView.setTextViewText(R.id.notification_text, msg.text);
//                        myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
//                        myNotificationView.setImageViewResource(R.id.notification_small_icon, getSmallIconId(context, msg));
//                        builder.setContent(myNotificationView)
//                                .setSmallIcon(getSmallIconId(context, msg))
//                                .setTicker(msg.ticker)
//                                .setAutoCancel(true);
//
//                        return builder.build();
//
//                    default:
//                        //默认为0，若填写的builder_id并不存在，也使用默认。
//                        return super.getNotification(context, msg);
//                }
//            }
        };

        mPushAgent.setMessageHandler(messageHandler);

        /**
         * 该Handler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * 参考集成文档的1.6.2
         * http://dev.umeng.com/push/android/integration#1_6_2
         * */
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler(){
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
            }

            @Override
            public void handleMessage(Context context, UMessage uMessage) {
                if (uMessage.extra.get("pushType").toString().equals("30")){
                    if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())) {
                        Intent intent = new Intent(TuxingApp.packageName + SysConstants.STUDYHOMEWORKACTION);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
                super.handleMessage(context, uMessage);
            }
        };

        //使用自定义的NotificationHandler，来结合友盟统计处理消息通知
        //参考http://bbs.umeng.com/thread-11112-1-1.html
        //CustomNotificationHandler notificationClickHandler = new CustomNotificationHandler();
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
    }

//    private void initPush() {
//        mPushAgent = PushAgent.getInstance(this);
//        mPushAgent.setDebugMode(false);
//
//        /**
//         * 该Handler是在IntentService中被调用，故
//         * 1. 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
//         * 2. IntentService里的onHandleIntent方法是并不处于主线程中，因此，如果需调用到主线程，需如下所示;
//         * 	      或者可以直接启动Service
//         * */
//        UmengMessageHandler messageHandler = new UmengMessageHandler() {
//            @Override
//            public void dealWithCustomMessage(final Context context, final UMessage msg) {
//                new Handler(getMainLooper()).post(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        // TODO Auto-generated method stub
////						Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
//                        UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
//                    }
//                });
//            }
//
//            @Override
//            public Notification getNotification(Context context,
//                                                UMessage msg) {
//                Intent intent = new Intent(TuxingApp.packageName + SysConstants.DELREFRESHACTION);
//                sendBroadcast(intent);
//                MobclickAgent.onEvent(context, "plush_success", UmengData.plush_success);
//                switch (msg.builder_id) {
//                    case 1:
//                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//                        RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.notification_view);
//                        myNotificationView.setTextViewText(R.id.notification_title, msg.title);
//                        myNotificationView.setTextViewText(R.id.notification_text, msg.text);
//                        myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
//                        myNotificationView.setImageViewResource(R.id.notification_small_icon, getSmallIconId(context, msg));
//                        builder.setContent(myNotificationView);
//                        builder.setAutoCancel(true);
//                        Notification mNotification = builder.build();
//                        //由于Android v4包的bug，在2.3及以下系统，Builder创建出来的Notification，并没有设置RemoteView，故需要添加此代码
//                        mNotification.contentView = myNotificationView;
//                        return mNotification;
//                    default:
//                        //默认为0，若填写的builder_id并不存在，也使用默认。
//                        return super.getNotification(context, msg);
//                }
//            }
//        };
//        mPushAgent.setMessageHandler(messageHandler);
//
//        /**
//         * 该Handler是在BroadcastReceiver中被调用，故
//         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
//         * */
//        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
//            @Override
//            public void dealWithCustomAction(Context context, UMessage msg) {
////				Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
//            }
//        };
//        mPushAgent.setNotificationClickHandler(notificationClickHandler);
//    }


    private void initPackageConfig() {
        ApplicationInfo appInfo = null;
        try {
            VersionName = Utils.getVersionName(this);
            appInfo = this.getPackageManager()
                    .getApplicationInfo(getPackageName(),
                            PackageManager.GET_META_DATA);
            PackageInfo info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            packageName = info.packageName;
            VersionType = appInfo.metaData.getString("version_name");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void startIMService() {
        if (SysConstants.isTest) {//测试
            EMChatConfig.getInstance().APPKEY = SysConstants.HX_APPKEY_DEV;
            SysConstants.H5_GAME_HOST_URL = SysConstants.H5_GAME_HOST_URL_DEV_TEST;
            SysConstants.AGREEMENTURL_XCSD = SysConstants.AGREEMENTURL_XCSD_DEV_TEST;
            SysConstants.KURL_FEEDBACK = SysConstants.KURL_FEEDBACK_DEV_TEST;
            SysConstants.KURL_THEME_TEST = SysConstants.KURL_THEME_TEST_DEV_TEST;
            SysConstants.JSHostUrl = SysConstants.JSHostUrlTest;
            Constants.QI_NIU_DOMAIN = Constants.QI_NIU_DOMAIN_DEV_TEST;
            //开发服
            CoreService.getInstance().start(this.getBaseContext(), VersionType + "_" + VersionName, "121.40.16.212", 8080);
            //测试服
//		CoreService.getInstance().start(this.getBaseContext(), VersionType+"_"+VersionName,"121.41.101.14",8080);
        } else {

            EMChatConfig.getInstance().APPKEY = SysConstants.HX_APPKEY_DIS;
            SysConstants.H5_GAME_HOST_URL = SysConstants.H5_GAME_HOST_URL_DIS;
            SysConstants.AGREEMENTURL_XCSD = SysConstants.AGREEMENTURL_XCSD_DIS;
            SysConstants.KURL_FEEDBACK = SysConstants.KURL_FEEDBACK_DIS;
            SysConstants.KURL_THEME_TEST = SysConstants.KURL_THEME_TEST_DIS;
            SysConstants.JSHostUrl = SysConstants.JSHostUrlDis;
            Constants.QI_NIU_DOMAIN = Constants.QI_NIU_DOMAIN_DIS;

            //正式服
            CoreService.getInstance().start(this.getBaseContext(), VersionType + "_" + VersionName, "service.xcsdedu.com", 80);
        }
    }


    public static TuxingApp getInstance() {
        return instance;
    }


    private void initBugly() {
        UserStrategy strategy = new UserStrategy(getApplicationContext()); //App的策略Bean
        strategy.setAppVersion(Utils.getVersionName(getApplicationContext()));      //App的版本
        strategy.setAppReportDelay(2000);  //设置SDK处理延时，毫秒
        if (VersionType.equals("parent")) {
            CrashReport.initCrashReport(getApplicationContext(), SysConstants.STU_BUGAPPID, false, strategy);  //初始化SDK
        } else if (VersionType.equals("teacher")) {
            CrashReport.initCrashReport(getApplicationContext(), SysConstants.TEA_BUGAPPID, false, strategy);  //初始化SDK
        }

    }

    private void initShareKey() {
        SysConstants.WEICHAT_APPID = getMetaDateString("WEICHAT_APPID");
        SysConstants.WEICHAT_SECRET = getMetaDateString("WEICHAT_SECRET");
//		SysConstants.QQ_APPID = getMetaDateString("QQ_APPID");
        if (VersionType.equals("parent")) {
//			SysConstants.QQ_APPID = "1104755971";
            SysConstants.QQ_APPID = "1105366460";
        } else if (VersionType.equals("teacher")) {
            SysConstants.QQ_APPID = "1105366436";
        }
        SysConstants.QQ_APPKEY = getMetaDateString("QQ_APPKEY");
    }

    private String getMetaDateString(String key) {
        ApplicationInfo appInfo = null;
        try {
            appInfo = this.getPackageManager()
                    .getApplicationInfo(getPackageName(),
                            PackageManager.GET_META_DATA);
            return appInfo.metaData.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void initImageLoader(Context context) {

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.diskCache(new UnlimitedDiscCache(new File(SysConstants.DATA_DIR_ROOT, ".images")));

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    public void initVideoRecorde() {
        try {
            videoErroy = false;
            // 设置拍摄视频缓存路径
            VCamera.setVideoCachePath(SysConstants.FILE_DIR_VIDEO);
            // 初始化拍摄SDK，必须
            VCamera.initialize(this);
        } catch (Throwable t) {
            videoErroy = true;
            MyLog.getLogger(TAG).d("视频_加载.so失败" + PhoneUtils.getPhoneType());
        }

    }



}
