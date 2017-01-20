package com.tuxing.sdk.http;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.squareup.okhttp.*;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.internal.DiskLruCache;
import com.squareup.wire.Wire;
import com.tuxing.rpc.proto.*;
import com.tuxing.sdk.db.entity.LoginUser;
import com.tuxing.sdk.db.helper.GlobalDbHelper;
import com.tuxing.sdk.event.LoginEvent;
import com.tuxing.sdk.event.NetworkEvent;
import com.tuxing.sdk.exception.ResponseError;
import com.tuxing.sdk.utils.Constants;
import com.tuxing.sdk.utils.NetworkUtils;
import com.tuxing.sdk.utils.SerializeUtils;
import com.tuxing.sdk.utils.StringUtils;
import de.greenrobot.event.EventBus;
import okio.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Alan on 2015/5/19.
 */
public class HttpClient {
    private final static Logger logger = LoggerFactory.getLogger(HttpClient.class);
    //private final static String DEFAULT_APP_SERVER_DOMAIN_SERVER = "192.168.10.115";
    //private final static String DEFAULT_APP_SERVER_DOMAIN_SERVER = "123.57.43.111";
    //private final static String DEFAULT_APP_SERVER_DOMAIN_SERVER = "api.tx2010.com";
    private final static String HTTP_DNS_URL = "http://119.29.29.29/d?dn=";
    //private final static int DEFAULT_APP_PORT = 80;
    private final static String CONTENT_TYPE = "application/x-protobuf; charset=utf-8";

    private static final String ACTION_RESOLVE = "RESOLVE_DOMAIN_NAME";

    private static final int INIT_RESOLVE_INTERVAL = 60000;
    private static final int RESOLVE_INTERVAL_INC = 60000;
    private static final int MAX_RESOLVE_INTERVAL_SECONDS = 600000;

    private static final int HTTP_STATUS_OK = 200;

    private static final int CONNECT_TIMEOUT = 15;
    private static final int READ_TIMEOUT = 15;
    private static final int WRITE_TIMEOUT = 15;

    private static HttpClient instance;

    private OkHttpClient client;
    private String hostUrl;
    private String host;
    private int port;
    private volatile String token;

    private PendingIntent pendingIntent;
    private BroadcastReceiver receiver;
    private int reconnectInterval = INIT_RESOLVE_INTERVAL;
    private Context context;
    private String apiVersion;
    private String osVersion;

    public synchronized static HttpClient getInstance(){
        if (instance == null){
            instance = new HttpClient();
            
        }

        return instance;
    }

    private HttpClient(){
        client = new OkHttpClient();
        client.setConnectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        client.setReadTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        client.setWriteTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);

        //setHostAddress(DEFAULT_APP_SERVER_DOMAIN_SERVER, DEFAULT_APP_PORT);
        EventBus.getDefault().register(HttpClient.this);
    }

    public void init(Context context, String version) {
        this.context = context;
        /*receiver = new Receiver();
        pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_RESOLVE),
                PendingIntent.FLAG_UPDATE_CURRENT);
        context.registerReceiver(receiver, new IntentFilter(ACTION_RESOLVE));*/

        //resolveDomainName();
        apiVersion = version;

        LoginUser user = GlobalDbHelper.getInstance().getLoginUser();
        if(user != null){
            token = user.getToken();
        }
        osVersion = android.os.Build.VERSION.SDK;// + Build.VERSION.RELEASE;
    }

    public void onDestroy(){
        /*try{
            context.unregisterReceiver(receiver);
        }catch(IllegalArgumentException e){
            //Ignore unregister errors.
        }*/
    }

    public void setHostAddress(String host, int port){
        this.host = host;
        this.port = port;
        hostUrl = String.format("http://%s:%d/http_invoke", host, port);
    }

    private void resolveDomainName(){
        logger.debug("Resolve domainName {} via HTTP DNS", host);
        if (!NetworkUtils.isNetWorkAvailable(context)){
            logger.debug("Network is not available, schedule next try.");
            scheduleNextResolve();
        }

        final Request httpRequest = new Request.Builder()
                .url(HTTP_DNS_URL + host)
                .get()
                .build();

        client.newCall(httpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Request httpRequest, IOException e) {
                logger.error("Request HTTP DNS failed.", e);
                setHostAddress(host, port);

                scheduleNextResolve();
            }

            @Override
            public void onResponse(Response httpResponse) throws IOException {
                String hostAddress = httpResponse.body().string();
                logger.debug("Resolve host ip: {}", hostAddress);
                if(!StringUtils.isBlank(hostAddress)) {
                    setHostAddress(hostAddress, port);
                    reconnectInterval = INIT_RESOLVE_INTERVAL;
                }else{
                    scheduleNextResolve();
                }
            }
        });
    }

    private void scheduleNextResolve(){
        logger.debug("Resolve host in next {} milliseconds", reconnectInterval);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Service.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, reconnectInterval, pendingIntent);

        reconnectInterval += RESOLVE_INTERVAL_INC;
        if (reconnectInterval > MAX_RESOLVE_INTERVAL_SECONDS){
            reconnectInterval = MAX_RESOLVE_INTERVAL_SECONDS;
        }
    }

    public com.tuxing.rpc.proto.Request buildPbRequest(String url, byte[] data){
        return  new com.tuxing.rpc.proto.Request.Builder()
                .url(url)
                .token(token == null ? "" : token)
                .version(apiVersion)
                .osVersion(osVersion)
                .osName("android")
                .body(ByteString.of(data))
                .build();
    }

    public void sendRequest(final String url, byte[] data, final RequestCallback callback){
        logger.debug("Send request: {}", url);
        final com.tuxing.rpc.proto.Request request = buildPbRequest(url, data);

        final Request httpRequest = new Request.Builder()
                .url(hostUrl)
                .post(RequestBody.create(
                        MediaType.parse(CONTENT_TYPE),
                        request.toByteArray()))
                .build();

        client.newCall(httpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Request httpRequest, IOException e) {
                logger.error("Send request failed!", e);

                ResponseError error;

                if(e instanceof InterruptedIOException || e instanceof SocketException){
                    if(!NetworkUtils.isNetWorkAvailable(context)){
                        EventBus.getDefault().post(new NetworkEvent(
                                NetworkEvent.EventType.NETWORK_UNAVAILABLE,
                                null
                        ));
                    }

                }

                error = new ResponseError(url, 500, Constants.MSG_CANNOT_CONNECT_TO_SERVER);

                if (callback != null) {
                    callback.onFailure(error);
                }
            }

            @Override
            public void onResponse(Response httpResponse) throws IOException {
                logger.debug("Url: {}, Response status: {}, message: {}", url, httpResponse.code(), httpResponse.message());
                if(httpResponse.code() != HTTP_STATUS_OK){
                    if(callback != null) {
                        callback.onFailure(new ResponseError(url, httpResponse.code(),
                                "服务器错误，请稍后再试"));
                    }
                }else{
                    try {
                        com.tuxing.rpc.proto.Response response = SerializeUtils.parseFrom(httpResponse.body().bytes(),
                                com.tuxing.rpc.proto.Response.class);

                        logger.debug("Request url: {}, Response status: {}, msg: {}", url,
                                response.status,
                                response.statusTxt);

                        if (callback != null) {
                            if (response.status == Constants.RESPONSE_STATUS.OK) {
                                try {
                                    callback.onResponse(Wire.get(response.body, response.DEFAULT_BODY).toByteArray());
                                } catch (Exception e) {
                                    logger.error("Error when handle callback", e);
                                }
                            } else if (response.status == Constants.RESPONSE_STATUS.NOT_ALLOWED) {
                                logger.debug("User login on another deceive.");

                                EventBus.getDefault().post(new LoginEvent(
                                        LoginEvent.EventType.TOKEN_EXPIRED,
                                        null,
                                        response.statusTxt
                                ));
                            } else {
                                callback.onFailure(new ResponseError(url, response.status,
                                        response.statusTxt));
                            }
                        }
                    }catch (Exception e){
                        logger.error("Process http response error", e);

                        if(callback != null){
                            callback.onFailure(new ResponseError(url, 500,
                                    Constants.MSG_CANNOT_CONNECT_TO_SERVER));
                        }
                    }
                }

            }
        });
    }

    public byte[] sendRequest(String url, byte[] data) throws IOException, ResponseError {
        logger.debug("Send request: {}", url);
        com.tuxing.rpc.proto.Request request = buildPbRequest(url, data);

        final Request httpRequest = new Request.Builder()
                .url(hostUrl)
                .post(RequestBody.create(
                        MediaType.parse(CONTENT_TYPE),
                        request.toByteArray()))
                .build();

        Response httpResponse = client.newCall(httpRequest).execute();

        logger.debug("Url: {}, Response status: {}, message: {}",
                url,
                httpResponse.code(),
                httpResponse.message());

        if(httpResponse.code() != HTTP_STATUS_OK){
            throw new ResponseError(url, httpResponse.code(), httpResponse.toString());
        }

        com.tuxing.rpc.proto.Response response = SerializeUtils.parseFrom(httpResponse.body().bytes(),
                com.tuxing.rpc.proto.Response.class);

        logger.debug("Request url: {}, Response status: {}, msg: {}",
                url,
                response.status,
                response.statusTxt);

        if(response.status == Constants.RESPONSE_STATUS.OK) {
            return Wire.get(response.body, response.DEFAULT_BODY).toByteArray();
        }else if(response.status == Constants.RESPONSE_STATUS.KICK_OFF){
            logger.debug("User login on another deceive.");
            token = null;
            EventBus.getDefault().post(new LoginEvent(
                    LoginEvent.EventType.KICK_OFF,
                    null,
                    null
            ));
        }else if(response.status == Constants.RESPONSE_STATUS.TOKEN_EXPIRED) {
            logger.debug("User token is expired.");
            token = null;
            EventBus.getDefault().post(new LoginEvent(
                    LoginEvent.EventType.TOKEN_EXPIRED,
                    null,
                    null
            ));
        }

        return null;
    }

    public void onEvent(LoginEvent event){
        switch (event.getEvent()){
            case LOGOUT:
                token = null;
                break;
            case LOGIN_SUCCESS:
                token = event.getUser().getToken();
        }
    }

    class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            resolveDomainName();
        }
    }

    public String getToken(){
        return token;
    }




    public void mySendRequest(final String hostUrl,final String url, byte[] data, final MyRequestCallback callback){
        logger.debug("Send request: {}", url);
        final com.tuxing.rpc.proto.Request request = buildPbRequest(url, data);

        final Request httpRequest = new Request.Builder()
                .url(hostUrl)
                .post(RequestBody.create(
                        MediaType.parse("application/json; charset=utf-8"),
                        data))
                .build();


        client.newCall(httpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Request httpRequest, IOException e) {
                logger.error("Send request failed!", e);

                ResponseError error;

                if(e instanceof InterruptedIOException || e instanceof SocketException){
                    if(!NetworkUtils.isNetWorkAvailable(context)){
                        EventBus.getDefault().post(new NetworkEvent(
                                NetworkEvent.EventType.NETWORK_UNAVAILABLE,
                                null
                        ));
                    }

                }

                error = new ResponseError(url, 500, Constants.MSG_CANNOT_CONNECT_TO_SERVER);

                if (callback != null) {
                    callback.onFailure(error);
                }
            }

            @Override
            public void onResponse(Response httpResponse) throws IOException {
                logger.debug("Url: {}, Response status: {}, message: {}", url, httpResponse.code(), httpResponse.message());
                if(httpResponse.code() != HTTP_STATUS_OK){
                    if(callback != null) {
                        callback.onFailure(new ResponseError(url, httpResponse.code(),
                                "服务器错误，请稍后再试"));
                    }
                }else{
                    try {
                        String str = httpResponse.body().string();
                        if( callback != null){
                            try {
                                //logger.debug("---callback data-----{},====",str);
                                callback.onResponse(str);
                            } catch (Exception e) {
                                logger.error("Error when handle callback", e);
                            }
                        }else{

                        }

//                        com.tuxing.rpc.proto.Response response = SerializeUtils.parseFrom(httpResponse.body().bytes(),
//                                com.tuxing.rpc.proto.Response.class);
//
//                        logger.debug("Request url: {}, Response status: {}, msg: {}", url,
//                                response.status,
//                                response.statusTxt);
//
//                        if (callback != null) {
//                            if (response.status == Constants.RESPONSE_STATUS.OK) {
//                                try {
//                                    callback.onResponse(Wire.get(response.body, response.DEFAULT_BODY).toByteArray());
//                                } catch (Exception e) {
//                                    logger.error("Error when handle callback", e);
//                                }
//                            } else if (response.status == Constants.RESPONSE_STATUS.NOT_ALLOWED) {
//                                logger.debug("User login on another deceive.");
//
//                                EventBus.getDefault().post(new LoginEvent(
//                                        LoginEvent.EventType.TOKEN_EXPIRED,
//                                        null,
//                                        response.statusTxt
//                                ));
//                            } else {
//                                callback.onFailure(new ResponseError(url, response.status,
//                                        response.statusTxt));
//                            }
//                        }
                    }catch (Exception e){
                        logger.error("Process http response error", e);

                        if(callback != null){
                            callback.onFailure(new ResponseError(url, 500,
                                    Constants.MSG_CANNOT_CONNECT_TO_SERVER));
                        }
                    }
                }

            }
        });
    }
}
