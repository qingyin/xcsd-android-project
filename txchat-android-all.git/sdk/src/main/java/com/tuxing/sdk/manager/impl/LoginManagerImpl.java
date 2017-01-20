package com.tuxing.sdk.manager.impl;

import android.content.Context;

import com.tuxing.rpc.proto.*;
import com.tuxing.sdk.db.entity.LoginUser;
import com.tuxing.sdk.db.entity.Setting;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.db.helper.GlobalDbHelper;
import com.tuxing.sdk.db.helper.UserDbHelper;
import com.tuxing.sdk.event.LoginEvent;
import com.tuxing.sdk.event.UserEvent;
import com.tuxing.sdk.facade.CoreService;
import com.tuxing.sdk.http.HttpClient;
import com.tuxing.sdk.http.RequestCallback;
import com.tuxing.sdk.http.RequestUrl;
import com.tuxing.sdk.manager.LoginManager;
import com.tuxing.sdk.manager.UserManager;
import com.tuxing.sdk.task.AsyncTaskProxyFactory;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.Constants;
import com.tuxing.sdk.utils.PbMsgUtils;
import com.tuxing.sdk.utils.SerializeUtils;
import com.xcsd.rpc.proto.EventType;

import de.greenrobot.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alan on 2015/6/2.
 */
public class LoginManagerImpl implements LoginManager {
    private final static Logger logger = LoggerFactory.getLogger(LoginManagerImpl.class);
    private static LoginManager instance;

    private User currentUser;

    HttpClient httpClient = HttpClient.getInstance();
    UserManager userManager;
    private Context context;

    private LoginManagerImpl(){
        EventBus.getDefault().register(LoginManagerImpl.this, Constants.EVENT_PRIORITY_SERVICE);
    }

    public synchronized static LoginManager getInstance(){
        if(instance == null){
            instance = new LoginManagerImpl();
            instance = AsyncTaskProxyFactory.getProxy(instance);
        }

        return instance;
    }

    @Override
    public void init(Context context) {
        this.context = context;

        userManager = UserManagerImpl.getInstance();

        LoginUser loginUser = GlobalDbHelper.getInstance().getLoginUser();

        if(loginUser != null && loginUser.getActive()){
            doLogin(loginUser, null, null);
        }else{
            logger.debug("No user login, use the last login user to init db");

            LoginUser lastLoginUser = GlobalDbHelper.getInstance().getLastLoginUser();
            if(lastLoginUser != null){
                UserDbHelper.getInstance().init(context, lastLoginUser.getUserId());
            }else{
                logger.debug("No user has login.");
            }

        }

    }

    @Override
    public void destroy() {

    }

    @Override
    public void login(final String username, final String password){
        logger.debug("LoginManager::login(), username={}, password=********", username);


        LoginRequest request = new LoginRequest.Builder()
                .username(username)
                .password(password)
                .build();

        httpClient.sendRequest(RequestUrl.LOGIN, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                LoginResponse response = SerializeUtils.parseFrom(data, LoginResponse.class);

                logger.debug("{} login success, userId={}, token={}",
                        response.user.userName,
                        response.user.userId,
                        response.token);

                User user = PbMsgUtils.transObj(response.user);

                LoginUser loginUser = new LoginUser();
                loginUser.setActive(response.isInit);
                loginUser.setToken(response.token);
                loginUser.setUserId(response.user.userId);
                loginUser.setUsername(username);
                loginUser.setPassword(password);
                loginUser.setStatus(1);

                List<Setting> settings = new ArrayList<>();
                for (UserProfile profile : response.userProfiles) {
                    Setting setting = new Setting();
                    setting.setField(profile.option);
                    setting.setValue(profile.value);

                    settings.add(setting);
                }

                doLogin(loginUser, user, settings);

                EventBus.getDefault().postSticky(new LoginEvent(
                        LoginEvent.EventType.LOGIN_SUCCESS,
                        loginUser,
                        null));

                CoreService.getInstance().getDataReportManager().reportEvent(EventType.APP_LOGIN);
            }

            @Override
            public void onFailure(Throwable cause) {
                logger.debug("{} login failed", username);

                EventBus.getDefault().post(new LoginEvent(
                        LoginEvent.EventType.LOGIN_FAILED_UNKNOWN_REASON,
                        null,
                        cause.getMessage()));

            }
        });
    }

    @Override
    public void logout() {
        logger.debug("LoginManager::logout()");


        GlobalDbHelper.getInstance().logout();

        currentUser = null;

        EventBus.getDefault().post(new LoginEvent(
                LoginEvent.EventType.LOGOUT,
                null,
                null));

        httpClient.sendRequest(RequestUrl.LOGOUT, new byte[]{}, null);
    }


    @Override
    public User getCurrentUser(){
        return currentUser;
    }

    public void onEvent(LoginEvent event){
        switch (event.getEvent()){
            case KICK_OFF:
            case TOKEN_EXPIRED:
                currentUser = null;
                GlobalDbHelper.getInstance().logout();
                break;
        }
    }

    private void doLogin(LoginUser loginUser, User user, List<Setting> settings){
        //1. change user login status
        GlobalDbHelper.getInstance().login(loginUser);

        //2. switch user database
        UserDbHelper.getInstance().init(context, loginUser.getUserId());

        //3. if user is passed save user info
        if(user != null) {
            UserDbHelper.getInstance().saveUser(user);
        }

        //4. if the setting is not null, save it.
        if(!CollectionUtils.isEmpty(settings)){
            UserDbHelper.getInstance().saveAllSettings(settings);
        }

        //4. set currentUser
        currentUser = UserDbHelper.getInstance().getUserById(loginUser.getUserId());
        if(currentUser != null) {
            currentUser.setPassword(loginUser.getPassword());
            currentUser.setActive(loginUser.getActive());
        }
    }

    @Override public void activeUser(String phoneNum, String verifyCode, final String password) {
        logger.debug("UserManager::activeUser(), phoneNum={}, verifyCode={}, password={}",
                phoneNum, verifyCode, password);


        ActiveUserRequest request = new ActiveUserRequest.Builder()
                .mobile(phoneNum)
                .code(verifyCode)
                .password(password)
                .build();

        httpClient.sendRequest(RequestUrl.ACTIVE_USER, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                ActiveUserResponse response = SerializeUtils.parseFrom(data, ActiveUserResponse.class);

                logger.debug("{} active success, userId={}, token={}",
                        response.user.userName,
                        response.user.userId,
                        response.token);

                User user = PbMsgUtils.transObj(response.user);

                LoginUser loginUser = new LoginUser();
                loginUser.setActive(true);
                loginUser.setToken(response.token);
                loginUser.setUserId(response.user.userId);
                loginUser.setUsername(response.user.mobile);
                loginUser.setPassword(password);
                loginUser.setStatus(1);

                List<Setting> settings = new ArrayList<>();

                doLogin(loginUser, user, settings);

                EventBus.getDefault().postSticky(new LoginEvent(
                        LoginEvent.EventType.LOGIN_SUCCESS,
                        loginUser,
                        null));

                EventBus.getDefault().post(new UserEvent(
                        UserEvent.EventType.ACTIVE_USER_SUCCESS,
                        null,
                        user));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new UserEvent(
                        UserEvent.EventType.ACTIVE_USER_FAILED,
                        cause.getMessage(),
                        null));
            }
        });
    }

    @Override
    public String getPassword(String username) {
        LoginUser loginUser = GlobalDbHelper.getInstance().getLoginUserByName(username);

        if(loginUser != null){
            return loginUser.getPassword();
        }

        return null;
    }
}
