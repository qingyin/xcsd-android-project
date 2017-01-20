package com.tuxing.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.tuxing.app.TuxingApp;
import com.tuxing.app.util.SysConstants;
import com.tuxing.sdk.db.entity.LoginUser;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.db.helper.GlobalDbHelper;
import com.tuxing.sdk.event.DataReportEvent;
import com.tuxing.sdk.facade.CoreService;
import com.tuxing.sdk.http.HttpClient;
import com.tuxing.sdk.http.MyRequestCallback;
import com.tuxing.sdk.utils.Constants;
import com.xcsd.rpc.proto.EventType;

import org.cocos2dx.lib.Cocos2dxActivity;

import java.io.IOException;

import de.greenrobot.event.EventBus;


/**
 * Created by apple on 16/7/8.
 */
public class CocosJSActivity extends Cocos2dxActivity {
    private static User user;
    private static String token = null;
    private static String userStr = "";
    private static String isTeacherApp = "0";
    private static Boolean isGameEnter = false;

    private static native void nativeEnd();

    private static native void nativeEnterCocosScene(final String args);

    private static native void nativeSetContext(final Context pContext);

    private static native void nativeJSHttpCallBack(final String cbData);

    public static Context preContext = null;

    public static CocosJSActivity appCocosActivity = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCocosActivity = this;
        resetData();
        if(isGameEnter){
            CoreService.getInstance().getDataReportManager().reportEventBid(EventType.CHANNEL_IN,"game");
        }
    }

    @Override
    public void finish() {
        android.util.Log.d("finish", "finish");
        nativeEnd();
        if(isGameEnter){
            CoreService.getInstance().getDataReportManager().reportEventBid(EventType.CHANNEL_OUT,"game");
        }
        super.finish();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    static public void setClassLoaderFrom(Context cxt) {
        preContext = cxt;
        nativeSetContext(cxt);
    }

    public static void loadNativeLibraries() {
        try {
            String libName = "cocos2djs";//bundle.getString("android.app.lib_name");
            System.loadLibrary(libName);
//            System.load(System.getProperty("java.library.path")+"/"+"cocos2djs");
//            System.load("/system/lib/libcocos2djs.so");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void nativeHttpClient(String hostUrl, String url, String bodyData, String callback) {
        android.util.Log.d("=====-nativeHttpClient", hostUrl + "\n url=" + url + "\n bodyData=" + bodyData + "\n realHostUrl=" + SysConstants.JSHostUrl);
        HttpClient.getInstance().mySendRequest(SysConstants.JSHostUrl, url, bodyData.getBytes(), new
                MyRequestCallback() {
                    @Override
                    public void onResponse(final String data) throws IOException {
                        android.util.Log.d("CocosJSActivity-nativeHttpClient", data);
                        appCocosActivity.runOnGLThread(new Runnable() {
                            @Override
                            public void run() {
                                nativeJSHttpCallBack(data);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable cause) {
                        android.util.Log.d("nativeHttpClient---onFailure---", cause.toString());
                        appCocosActivity.runOnGLThread(new Runnable() {
                            @Override
                            public void run() {
                                nativeJSHttpCallBack("{\"result\":400}");
                            }
                        });
                    }
                });
    }

    public static void reportGameDataNative(int gameId, int eventType) {
        android.util.Log.d("CocosJSActivity", "reportGameDataNative data:gameId=" +
                Integer.toString(gameId) + " eventType=" + Integer.toString(eventType)
                +" isGameEnter="+Boolean.toString(isGameEnter));
        EventType eType = EventType.APP_LOGIN;
        for (EventType et : EventType.values()) {
            if (et.getValue() == eventType) {
                eType = et;
                break;
            }
        }
        if (eType == EventType.GAME_IN || eType == EventType.GAME_OUT) {
            if(isGameEnter){
                CoreService.getInstance().getDataReportManager().reportGameData(eType,gameId+"",Long.parseLong(userStr));
            }
            android.util.Log.d("CocosJSActivity", "22222reportGameDataNative data:gameId=" +
                    Integer.toString(gameId) + " eventType=" + Integer.toString(eventType)
                    +" isGameEnter="+Boolean.toString(isGameEnter));
        } else {
            android.util.Log.d("CocosJSActivity", "reportGameDataNative error data:gameId=" +
                    Integer.toString(gameId) + " eventType=" + Integer.toString(eventType));
        }
    }


    public static void reportFinishHomeworkNative(String memberId,String scoreJson) {
        android.util.Log.d("CocosJSActivity", "reportFinishHomeworkNative scoreJson=" +scoreJson+
                " memberId="+memberId);
        CoreService.getInstance().getDataReportManager().reportExtendedInfo(
                EventType.FINISH_HOMEWORK, memberId, scoreJson, Long.parseLong(userStr));
    }


    public static void reportGameTestNative(String testId) {
        android.util.Log.d("CocosJSActivity", "reportGameTestNative testId=" +testId);
        CoreService.getInstance().getDataReportManager().reportGameData(EventType.GAME_TEST, testId, Long.parseLong(userStr));
    }

    public static void nativeActionCallBack() {
        android.util.Log.d("CocosJSActivity", "nativeActionCallBack");
        Intent intent = new Intent(preContext, CocosJSActivity.class);
        preContext.startActivity(intent);
    }

    public static void backToNativeApp() {
        appCocosActivity.getGLSurfaceView().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Intent intent = new Intent(appCocosActivity.getContext(), MainActivity.class);
                Intent intent = new Intent(appCocosActivity.getContext(), preContext.getClass());
                //Intent intent = new Intent(MainActivity., preContext.getClass());

                appCocosActivity.getContext().startActivity(intent);
                appCocosActivity.finish();
                //setClassLoaderFrom(HomeFragment.instance.getActivity());
//                Log.i("---goTo---backToNativeApp--",preContext.toString());
            }
        }, 80);
    }

    public static void goToGameLobby() {
        //loadNativeLibraries();
        checkTokenUserId();
        if (token == null || userStr == ""){
            if (token == null){
                android.util.Log.d("CocosJSActivity", "goToGameLobby---token == null");
            }
            if (userStr == ""){
                android.util.Log.d("CocosJSActivity", "goToGameLobby---userStr == ''");
            }
            return;
        }
        isGameEnter = true;
        StringBuilder strArgs = new StringBuilder();
        strArgs.append(SysConstants.GameArgsKey.ACTION + "=" + SysConstants.GameArgsKey.ActionEnum.gameLobby + "&");
        strArgs.append(SysConstants.GameArgsKey.TOKEN + "=" + token + "&");
        strArgs.append(SysConstants.GameArgsKey.MEMBER_ID + "=" + userStr);
//        Log.i("---goTo---goToGameLobby--", strArgs.toString());
        nativeEnterCocosScene(strArgs.toString());
    }

    public static void goToGameTest(final String gamelist, final boolean isFirstTest, final Integer testId) {//,final List<Integer> ability_arry){
        //loadNativeLibraries();
        checkTokenUserId();
        isGameEnter = false;
        StringBuilder strArgs = new StringBuilder();
        strArgs.append(SysConstants.GameArgsKey.ACTION + "=" + SysConstants.GameArgsKey.ActionEnum.gameTest + "&");
        strArgs.append(SysConstants.GameArgsKey.TOKEN + "=" + token + "&");
        strArgs.append(SysConstants.GameArgsKey.MEMBER_ID + "=" + userStr + "&");
        strArgs.append(SysConstants.GameArgsKey.GAME_LIST + "=" + formatJsonString(gamelist) + "&");
        strArgs.append(SysConstants.GameArgsKey.IS_FIRST_TEST + "=" + isFirstTest + "&");
        strArgs.append(SysConstants.GameArgsKey.TEST_ID + "=" + testId.toString());
        nativeEnterCocosScene(strArgs.toString());
    }

    public static void goToGameHomeWork(final String gamelist, final Long memberId, final Long childUserId) {
        //loadNativeLibraries();
        checkTokenUserId();
        isGameEnter = false;
        StringBuilder strArgs = new StringBuilder();
        strArgs.append(SysConstants.GameArgsKey.ACTION + "=" + SysConstants.GameArgsKey.ActionEnum.homeWork + "&");
        strArgs.append(SysConstants.GameArgsKey.TOKEN + "=" + token + "&");
        strArgs.append(SysConstants.GameArgsKey.MEMBER_ID + "=" + memberId.toString() + "&");
        strArgs.append(SysConstants.GameArgsKey.TASK_LIST + "=" + formatJsonString(gamelist) + "&");
        strArgs.append(SysConstants.GameArgsKey.CHILD_USERID + "=" + childUserId.toString());
        nativeEnterCocosScene(strArgs.toString());
    }

    //gameList:"1#2$5_true;3#7$9_true"--->1->gameId;2->level;
    //[{\"gameId\":1,\"level\":2,\"abilityId\":5,\"hasGuide\":\"true\"},{\"gameId\":1,\"level\":2}]
    private static String formatJsonString(final String gameList) {
        if (gameList.isEmpty()) {
            return "[]";
        }

        String source = gameList;
        if (gameList.substring(gameList.length() - 1).compareTo(";") == 0) {
            source = gameList.substring(0, gameList.length() - 1);
        }
        final String search1 = "#";
        final String search2 = "$";
        final String search3 = "_";
        final String search4 = ";";

        final String replace1 = ",\"level\":";
        final String replace2 = ",\"abilityId\":";
        final String replace3 = ",\"hasGuide\":\"";
        final String replace4 = "\"},{\"gameId\":";
        String temp1 = source.replace(search1, replace1);
        String temp2 = temp1.replace(search2, replace2);
        String temp3 = temp2.replace(search3, replace3);
        String temp4 = temp3.replace(search4, replace4);

        StringBuilder jsonStr = new StringBuilder();
        jsonStr.append("[{\"gameId\":");
        jsonStr.append(temp4);
        jsonStr.append("\"}]");

        //Log.i("---formatJsonString-----", jsonStr.toString());
        return jsonStr.toString();
    }

    public static void resetData() {
        User user = CoreService.getInstance().getLoginManager().getCurrentUser();
        LoginUser loginUser = GlobalDbHelper.getInstance().getLoginUser();
        if (loginUser != null) {
            token = loginUser.getToken();
        }

        if (TuxingApp.VersionType.equals(Constants.VERSION.PARENT.getVersion())) {
            userStr = user.getChildUserId() + "";
        } else {
            userStr = user.getUserId() + "";
        }
    }
    public static void checkTokenUserId() {
        if (token == null || userStr == ""){
            resetData();
        }
    }
}


