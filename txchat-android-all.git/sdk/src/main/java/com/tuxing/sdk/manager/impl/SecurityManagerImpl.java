package com.tuxing.sdk.manager.impl;

import android.content.Context;

import com.tuxing.rpc.proto.PlatformType;
import com.tuxing.rpc.proto.SendSmsCodeRequest;
import com.tuxing.rpc.proto.SetPasswordRequest;
import com.tuxing.rpc.proto.UpdateDeviceTokenRequest;
import com.tuxing.sdk.event.DeviceTokenEvent;
import com.tuxing.sdk.event.SecurityEvent;
import com.tuxing.sdk.event.SetPasswordEvent;
import com.tuxing.sdk.http.HttpClient;
import com.tuxing.sdk.http.RequestCallback;
import com.tuxing.sdk.http.RequestUrl;
import com.tuxing.sdk.manager.SecurityManager;
import com.tuxing.sdk.task.AsyncTaskProxyFactory;
import com.tuxing.sdk.utils.PbMsgUtils;
import de.greenrobot.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by Alan on 2015/6/9.
 */
public class SecurityManagerImpl implements SecurityManager {
    private final static Logger logger = LoggerFactory.getLogger(SecurityManager.class);
    private static SecurityManager instance;

    HttpClient httpClient = HttpClient.getInstance();

    private Context context;

    private SecurityManagerImpl(){

    }

    public synchronized static SecurityManager getInstance(){
        if(instance == null){
            instance = new SecurityManagerImpl();
            instance = AsyncTaskProxyFactory.getProxy(instance);
        }

        return instance;
    }

    @Override
    public void init(Context context) {
        this.context = context;
    }

    @Override
    public void destroy() {

    }

    @Override public void sendVerifyCode(String phoneNum, int type, boolean isVoice){
        logger.debug("SecurityManager::sendVerifyCode(), phoneNum={}", phoneNum);


        SendSmsCodeRequest request = new SendSmsCodeRequest.Builder()
                .mobile(phoneNum)
                .sendSmsCodeType(PbMsgUtils.transSendSmsCodeType(type))
                .isVoice(isVoice)
                .build();

        httpClient.sendRequest(RequestUrl.SEND_SMS_CODE, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                EventBus.getDefault().post(new SecurityEvent(
                        SecurityEvent.EventType.SEND_VERIFY_CODE_SUCCESS,
                        null
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new SecurityEvent(
                        SecurityEvent.EventType.SEND_VERIFY_CODE_FAILED,
                        cause.getMessage()
                ));
            }
        });
    }


    @Override
    public void setPassword(String phoneNum, String verifyCode, final String password) {
        logger.debug("SecurityManager::setPassword(), verifyCode={}. phoneNum={}, password=********", verifyCode, phoneNum);


        SetPasswordRequest request = new SetPasswordRequest.Builder()
                .code(verifyCode)
                .mobile(phoneNum)
                .password(password)
                .build();


        httpClient.sendRequest(RequestUrl.RESET_PASSWORD, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                EventBus.getDefault().post(new SetPasswordEvent(
                        null,
                        SetPasswordEvent.EventType.RESET_PASSWORD_SUCCESS));
            }

            @Override
            public void onFailure(Throwable cause) {
                //TODO： verify code is not correct
                EventBus.getDefault().post(new SetPasswordEvent(
                        cause.getMessage(),
                        SetPasswordEvent.EventType.RESET_PASSWORD_FAILED));

            }
        });
    }

    @Override
    public void updateDeviceToken(String deviceToken, String deviceId, String osVersion, String model) {
        logger.debug("SecurityManager::updateDeviceToken(), deviceToken={}", deviceToken);


        UpdateDeviceTokenRequest request = new UpdateDeviceTokenRequest.Builder()
                .deviceToken(deviceToken)
                .platformType(PlatformType.ANDROID)
                .mobileVersion(model)
                .deviceId(deviceId)
                .osVersion(osVersion)
                .build();

        httpClient.sendRequest(RequestUrl.UPDATE_DEVICE_TOKEN, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {

                EventBus.getDefault().post(new DeviceTokenEvent(
                        DeviceTokenEvent.EventType.UPDATE_DEVICE_TOKEN_SUCCESS,
                        null
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new DeviceTokenEvent(
                        DeviceTokenEvent.EventType.UPDATE_DEVICE_TOKEN_FAILED,
                        cause.getMessage()
                ));
            }
        });

    }
}
