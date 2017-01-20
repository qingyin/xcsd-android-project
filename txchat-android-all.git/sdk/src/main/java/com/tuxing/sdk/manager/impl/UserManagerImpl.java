package com.tuxing.sdk.manager.impl;

import android.content.Context;
import com.tuxing.rpc.proto.*;
import com.tuxing.sdk.db.entity.Setting;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.db.helper.GlobalDbHelper;
import com.tuxing.sdk.db.helper.UserDbHelper;
import com.tuxing.sdk.event.*;
import com.tuxing.sdk.exception.ResponseError;
import com.tuxing.sdk.http.HttpClient;
import com.tuxing.sdk.http.RequestCallback;
import com.tuxing.sdk.http.RequestUrl;
import com.tuxing.sdk.manager.LoginManager;
import com.tuxing.sdk.manager.UserManager;
import com.tuxing.sdk.modle.CheckInCard;
import com.tuxing.sdk.modle.NotificationSetting;
import com.tuxing.sdk.task.AsyncTaskProxyFactory;
import com.tuxing.sdk.utils.Constants;
import com.tuxing.sdk.utils.PbMsgUtils;
import com.tuxing.sdk.utils.SerializeUtils;
import de.greenrobot.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alan on 2015/5/21.
 */
public class UserManagerImpl implements UserManager {
    private final static Logger logger = LoggerFactory.getLogger(UserManager.class);
    private static UserManager instance;

    HttpClient httpClient = HttpClient.getInstance();
    LoginManager loginManager = LoginManagerImpl.getInstance();

    private Context context;

    private UserManagerImpl(){

    }

    public synchronized static UserManager getInstance(){
        if(instance == null){
            instance = new UserManagerImpl();
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

    @Override public void getChild(){
        logger.debug("UserManager::getChild()");


        FetchChildRequest request = new FetchChildRequest();

        httpClient.sendRequest(RequestUrl.GET_CHILD, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchChildResponse response = SerializeUtils.parseFrom(data, FetchChildResponse.class);

                User user = null;
                if(response.children != null) {
                    user = PbMsgUtils.transObj(response.children);

                    UserDbHelper.getInstance().saveUser(user);

                    logger.debug("Get child, userId={}, username={}", user.getId(), user.getUsername());
                }else{
                    logger.debug("current user is not a parent?");
                }

                EventBus.getDefault().post(new ChildEvent(
                        ChildEvent.EventType.GET_CHILD_SUCCESS,
                        null,
                        user
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new ChildEvent(
                        ChildEvent.EventType.GET_CHILD_FAILED,
                        cause.getMessage(),
                        null
                ));
            }
        });
    }

    @Override public void requestUserInfoFromServer(long userId){
        logger.debug("UserManager::requestUserInfoFromServer(), userId={}", userId);


        User user = UserDbHelper.getInstance().getUserById(userId);

        if(user == null) {

            FetchUserinfoRequest request = new FetchUserinfoRequest.Builder()
                    .uid(userId)
                    .build();

            httpClient.sendRequest(RequestUrl.GET_USER_INFO, request.toByteArray(), new RequestCallback() {

                @Override
                public void onResponse(byte[] data) throws IOException {
                    FetchUserinfoResponse response = SerializeUtils.parseFrom(data, FetchUserinfoResponse.class);

                    User user = PbMsgUtils.transObj(response.user);

                    UserDbHelper.getInstance().saveUser(user);

                    EventBus.getDefault().post(new UserEvent(
                                UserEvent.EventType.REQUEST_USER_SUCCESS,
                            null,
                            user));
                }

                @Override
                public void onFailure(Throwable cause) {
                    UserEvent.EventType event = UserEvent.EventType.REQUEST_USER_FAILED;

                    if(cause instanceof ResponseError){
                        ResponseError error = (ResponseError) cause;
                        if(error.getStatus() == Constants.RESPONSE_STATUS.NOT_FOUND){
                            event = UserEvent.EventType.USER_NOT_FOUND;
                        }
                    }
                    EventBus.getDefault().post(new UserEvent(
                                event,
                            cause.getMessage(),
                            null));
                }
            });
        }else{
            EventBus.getDefault().post(new UserEvent(
                    UserEvent.EventType.REQUEST_USER_SUCCESS,
                    null,
                    user));
        }
    }

    @Override public void getUserInfoFromLocal(long userId){
        logger.debug("UserManager::getUserInfoFromLocal(), userId={}", userId);


        User user = UserDbHelper.getInstance().getUserById(userId);

        if(user == null){
            logger.warn("Cannot find user by Id {}", userId);
        }

        EventBus.getDefault().post(new UserEvent(
                UserEvent.EventType.QUERY_USER_BY_ID,
                null,
                user));
    }


    @Override public void getBindCard() {
        logger.debug("UserManager::getBindCard()");


        httpClient.sendRequest(RequestUrl.GET_BIND_CARD, new byte[]{}, new RequestCallback() {

            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchUserCardResponse response = SerializeUtils.parseFrom(data, FetchUserCardResponse.class);

                List<CheckInCard> cardList = new ArrayList<>();

                for (BindCardInfo pbCard : response.biandCardInfo) {
                    CheckInCard card = PbMsgUtils.transObj(pbCard);
                    cardList.add(card);
                }

                EventBus.getDefault().post(new CheckInCardEvent(
                        CheckInCardEvent.EventType.CARD_REQUEST_SUCCESS,
                        null,
                        cardList
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new CheckInCardEvent(
                        CheckInCardEvent.EventType.CARD_REQUEST_FAILED,
                        cause.getMessage(),
                        null
                ));
            }
        });
    }

    @Override
    public User getUserInfo(long userId) {
        logger.debug("UserManager::getUserInfo(), userId={}", userId);
        return UserDbHelper.getInstance().getUserById(userId);
    }

    @Override
    public void updatePhoneNum(final String phoneNum, String verifyCode) {
        logger.debug("UserManager::updatePhoneNum(), phoneNum={}, verifyCode={}", phoneNum, verifyCode);


        UpdateMobileRequest request = new UpdateMobileRequest.Builder()
                .mobile(phoneNum)
                .code(verifyCode)
                .build();

        httpClient.sendRequest(RequestUrl.UPDATE_MOBILE, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                loginManager.getCurrentUser().setMobile(phoneNum);
                loginManager.getCurrentUser().setUsername(phoneNum);

                UserDbHelper.getInstance().saveUser(loginManager.getCurrentUser());

                GlobalDbHelper.getInstance().changeLoginName(loginManager.getCurrentUser().getUserId(), phoneNum);

                GlobalDbHelper.getInstance().activeUser(loginManager.getCurrentUser().getUserId());

                EventBus.getDefault().post(new UserEvent(
                        UserEvent.EventType.UPDATE_MOBILE_SUCCESS,
                        null,
                        null
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                //TODO: UPDATE_MOBILE_VERIFY_CODE_INCORRECT
                EventBus.getDefault().post(new UserEvent(
                        UserEvent.EventType.UPDATE_MOBILE_FAILED,
                        cause.getMessage(),
                        null
                ));
            }
        });
    }

    @Override
    public void updateUserInfo(final User user) {
        logger.debug("UserManager::updateUserInfo(), user={}", user.getUserId());


        com.tuxing.rpc.proto.User pbUser = PbMsgUtils.transPbMsg(user);
        UpdateUserInfoRequest request = new UpdateUserInfoRequest.Builder()
                .user(pbUser)
                .build();

        httpClient.sendRequest(RequestUrl.UPDATE_USER_INFO, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                final FetchUserinfoRequest request = new FetchUserinfoRequest.Builder()
                        .uid(user.getUserId())
                        .build();

                httpClient.sendRequest(RequestUrl.GET_USER_INFO, request.toByteArray(), new RequestCallback() {

                    @Override
                    public void onResponse(byte[] data) throws IOException {
                        FetchUserinfoResponse response = SerializeUtils.parseFrom(data, FetchUserinfoResponse.class);

                        User newUser = PbMsgUtils.transObj(response.user);
                        UserDbHelper.getInstance().saveUser(newUser);

                        if (user.getUserId() == loginManager.getCurrentUser().getUserId()) {
                            loginManager.getCurrentUser().setNickname(newUser.getNickname());
                            loginManager.getCurrentUser().setAvatar(newUser.getAvatar());
                            loginManager.getCurrentUser().setRealname(newUser.getRealname());
                        }

                        EventBus.getDefault().post(new UserEvent(
                                UserEvent.EventType.UPDATE_USER_SUCCESS,
                                null,
                                newUser));
                    }

                    @Override
                    public void onFailure(Throwable cause) {
                        EventBus.getDefault().post(new UserEvent(
                                UserEvent.EventType.UPDATE_USER_FAILED,
                                cause.getMessage(),
                                null));
                    }
                });
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new UserEvent(
                        UserEvent.EventType.UPDATE_USER_FAILED,
                        cause.getMessage(),
                        null));
            }
        });
    }

    @Override
    public void unbindCheckInCard(String codeNum) {
        logger.debug("UserManager::unbindCheckInCard()");

        ReportLossCardRequest request = new ReportLossCardRequest.Builder()
                .cardCode(codeNum)
                .build();


        httpClient.sendRequest(RequestUrl.REPORT_LOST_CARD, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                EventBus.getDefault().post(new CheckInCardEvent(
                        CheckInCardEvent.EventType.CARD_UNBIND_SUCCESS,
                        null,
                        null
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new CheckInCardEvent(
                        CheckInCardEvent.EventType.CARD_UNBIND_FAILED,
                        cause.getMessage(),
                        null
                ));
            }
        });
    }

    @Override
    public void changeNotificationSetting(NotificationSetting notificationSetting) {
        logger.debug("UserManager::changeNotificationSetting(), " +
                        "open_sound={}, open_vibration={}, exempt_disturb={}",
                notificationSetting.getEnableSound(), notificationSetting.getEnableVibration(),
                notificationSetting.getDisturbFreeSetting());


        SaveUserProfileRequest.Builder requestBuilder = new SaveUserProfileRequest.Builder();

        List<UserProfile> userProfileList = new ArrayList<>();
        final List<Setting> settings = new ArrayList<>();

        if(notificationSetting.getEnableSound() != null){
            Setting setting = new Setting();
            setting.setField("open_sound");
            setting.setValue(notificationSetting.getEnableSound().toString());
            settings.add(setting);

            UserProfile profile = new UserProfile.Builder()
                    .option("open_sound")
                    .value(notificationSetting.getEnableSound().toString())
                    .build();

            userProfileList.add(profile);
        }

        if(notificationSetting.getEnableVibration() != null){
            Setting setting = new Setting();
            setting.setField("open_vibration");
            setting.setValue(notificationSetting.getEnableVibration().toString());
            settings.add(setting);

            UserProfile profile = new UserProfile.Builder()
                    .option("open_vibration")
                    .value(notificationSetting.getEnableVibration().toString())
                    .build();

            userProfileList.add(profile);
        }

        if(notificationSetting.getDisturbFreeSetting() != null){
            Setting setting = new Setting();
            setting.setField("exempt_disturb");
            setting.setValue(notificationSetting.getDisturbFreeSetting().toString());
            settings.add(setting);

            UserProfile profile = new UserProfile.Builder()
                    .option("exempt_disturb")
                    .value(notificationSetting.getDisturbFreeSetting().toString())
                    .build();

            userProfileList.add(profile);
        }

        SaveUserProfileRequest request = new SaveUserProfileRequest.Builder().userProfiles(userProfileList).build();

        httpClient.sendRequest(RequestUrl.SET_USER_PROFILE, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                UserDbHelper.getInstance().saveAllSettings(settings);

                EventBus.getDefault().post(new NotificationSettingEvent(
                        NotificationSettingEvent.EventType.CHANGE_SETTING_SUCCESS,
                        null,
                        null
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new NotificationSettingEvent(
                        NotificationSettingEvent.EventType.CHANGE_SETTING_FAILED,
                        cause.getMessage(),
                        null
                ));
            }
        });

    }

    @Override
    public void getNotificationSetting() {
        logger.debug("UserManager::getNotificationSetting()");


        List<Setting> settings = UserDbHelper.getInstance().getAllSettings();

        NotificationSetting notificationSetting = new NotificationSetting();
        notificationSetting.setDisturbFreeSetting(0);
        notificationSetting.setEnableSound(1);
        notificationSetting.setEnableVibration(1);

        for(Setting setting : settings){
            if("exempt_disturb".equalsIgnoreCase(setting.getField())){
                notificationSetting.setDisturbFreeSetting(Integer.valueOf(setting.getValue()));
            }else if("open_vibration".equalsIgnoreCase(setting.getField())){
                notificationSetting.setEnableVibration(Integer.valueOf(setting.getValue()));
            }else if("open_sound".equalsIgnoreCase(setting.getField())){
                notificationSetting.setEnableSound(Integer.valueOf(setting.getValue()));
            }
        }

        EventBus.getDefault().post(new NotificationSettingEvent(
                NotificationSettingEvent.EventType.GET_SETTING_SUCCESS,
                null,
                notificationSetting
        ));
    }

    @Override
    public void updateUserProfile() {
        logger.debug("UserManager::updateUserProfile()");

        httpClient.sendRequest(RequestUrl.FETCH_USER_PROFILE, new byte[]{}, new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchUserProfileResponse response = SerializeUtils.parseFrom(data, FetchUserProfileResponse.class);

                List<Setting> settingList = new ArrayList<>();

                for(UserProfile txProfile : response.userProfiles){
                    Setting setting = new Setting();

                    setting.setField(txProfile.option);
                    setting.setValue(txProfile.value);

                    settingList.add(setting);
                }

                UserDbHelper.getInstance().saveAllSettings(settingList);

                if (settingList.size() > 0) {
                    EventBus.getDefault().post(new UserEvent(
                            UserEvent.EventType.UPDATEUSERPROFILE_SUCCESS,
                            null,
                            null
                    ));
                }

            }

            @Override
            public void onFailure(Throwable cause) {
                logger.error("Cannot get user profile", cause);
            }
        });
    }

    @Override
    public String getUserProfile(String field, String defaultVal) {
        logger.debug("UserManager::getUserProfile(), field={}, defaultVal={}", field, defaultVal);

        String value = UserDbHelper.getInstance().getSettingValue(field);

        if(value != null){
            return value;
        }

        return defaultVal;
    }

    @Override
    public void changePassword(String oldPassword, final String newPassword) {
        logger.debug("UserManager::changePassword(), oldPassword=**********, newPassword=********");


        UpdatePasswordRequest request = new UpdatePasswordRequest.Builder()
                .oldPassword(oldPassword)
                .newPassword(newPassword)
                .build();

        httpClient.sendRequest(RequestUrl.CHANGE_PASSWORD, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                GlobalDbHelper.getInstance().resetPassword(loginManager.getCurrentUser().getUserId(), newPassword);
                loginManager.getCurrentUser().setPassword(newPassword);

                EventBus.getDefault().post(new UserEvent(
                        UserEvent.EventType.CHANGE_PASSWORD_SUCCESS,
                        null,
                        null
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new UserEvent(
                        UserEvent.EventType.CHANGE_PASSWORD_FAILED,
                        cause.getMessage(),
                        null
                ));
            }
        });
    }

    @Override
    public void clearUserCache() {
        logger.debug("UserManager::clearUserCache()");

        //1. delete notice cache

        //2. delete checkIn cache

        //3. delete image cache

        //4. call
    }

    @Override
    public void checkIn() {
        logger.debug("UserManager::checkIn()");

        UserCheckInRequest request = new UserCheckInRequest();

        httpClient.sendRequest(RequestUrl.USER_CHECK_IN, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                UserCheckInResponse response = SerializeUtils.parseFrom(data, UserCheckInResponse.class);

                EventBus.getDefault().post(new UserCheckInEvent(
                        UserCheckInEvent.EventType.USER_CHECK_IN_SUCCESS,
                        null,
                        response.bonus));
            }

            @Override
            public void onFailure(Throwable cause) {
                logger.error("user check in return error.", cause);
            }
        });
    }
}
