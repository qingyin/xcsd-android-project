package com.tuxing.sdk.manager.impl;

import android.content.Context;
import com.tuxing.rpc.proto.*;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.db.helper.UserDbHelper;
import com.tuxing.sdk.event.ChildEvent;
import com.tuxing.sdk.event.RelativeEvent;
import com.tuxing.sdk.http.HttpClient;
import com.tuxing.sdk.http.RequestCallback;
import com.tuxing.sdk.http.RequestUrl;
import com.tuxing.sdk.manager.LoginManager;
import com.tuxing.sdk.manager.RelativeManager;
import com.tuxing.sdk.modle.Relative;
import com.tuxing.sdk.task.AsyncTaskProxyFactory;
import com.tuxing.sdk.utils.Constants;
import com.tuxing.sdk.utils.PbMsgUtils;
import com.tuxing.sdk.utils.SerializeUtils;
import de.greenrobot.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Alan on 2015/6/28.
 */
public class RelativeManagerImpl implements RelativeManager {
    private final static Logger logger = LoggerFactory.getLogger(RelativeManager.class);
    private static RelativeManager instance;

    HttpClient httpClient = HttpClient.getInstance();
    LoginManager loginManager = LoginManagerImpl.getInstance();
    private Context context;

    private RelativeManagerImpl(){

    }

    public synchronized static RelativeManager getInstance(){
        if(instance == null){
            instance = new RelativeManagerImpl();
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

    @Override
    public void bindChild(final long childUserId, final int relativeType, long birthday, String guarder){
        logger.debug("RelativeManager::bindChild(), childUserId={}, relativeType={}, " +
                "birthday={}, guarder={}", childUserId, relativeType, birthday, guarder);


        BindChildRequest request = new BindChildRequest.Builder()
                .childUserId(childUserId)
                .parentType(PbMsgUtils.transParentType(relativeType))
                .birthday(birthday)
                .guarder(guarder)
                .build();

        httpClient.sendRequest(RequestUrl.BIND_CHILD, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                BindChildResponse response = SerializeUtils.parseFrom(data, BindChildResponse.class);

                logger.debug("bind child {} successful", childUserId);

                loginManager.getCurrentUser().setRelativeType(relativeType);
                loginManager.getCurrentUser().setChildUserId(childUserId);
                loginManager.getCurrentUser().setNickname(response.parent.nickname);
                UserDbHelper.getInstance().saveUser(loginManager.getCurrentUser());

                User child = PbMsgUtils.transObj(response.child);
                UserDbHelper.getInstance().saveUser(child);

                EventBus.getDefault().post(new ChildEvent(
                        ChildEvent.EventType.BIND_CHILD_SUCCESS,
                        null,
                        null
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                //TODO: CHILD_BIND_BY_OTHERS
                EventBus.getDefault().post(new ChildEvent(
                        ChildEvent.EventType.BIND_CHILD_FAILED,
                        cause.getMessage(),
                        null
                ));
            }
        });
    }

    @Override
    public void getRelativeList() {
        logger.debug("RelativeManager::getRelativeList()");


        httpClient.sendRequest(RequestUrl.GET_BIND_RELATIVE, new byte[]{}, new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                GetBindingParentListResponse response = SerializeUtils.parseFrom(data, GetBindingParentListResponse.class);

                SortedMap<Integer, Relative> relativeMap = new TreeMap<>();
                for(Integer relativeType : Constants.RELATIVE_TYPE_ARRAY){
                    Relative relative = new Relative();
                    relative.setRelativeType(relativeType);

                    relativeMap.put(relativeType, relative);
                }

                for(BindingParentInfo pbRelative : response.bindParents){
                    Relative relative = relativeMap.get(pbRelative.parentType.getValue());

                    if(relative != null) {
                        relative.setUser(PbMsgUtils.transObj(pbRelative.user));
                        relative.setMaster(pbRelative.isMaster);
                    }
                }

                EventBus.getDefault().post(new RelativeEvent(
                        RelativeEvent.EventType.GET_RELATIVE_SUCCESS,
                        null,
                        new ArrayList<>(relativeMap.values())));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new RelativeEvent(
                        RelativeEvent.EventType.GET_RELATIVE_FAILED,
                        cause.getMessage(),
                        null));
            }
        });
    }

    @Override
    public void changeRelative(long userId, final int relativeType) {
        logger.debug("RelativeManager::changeRelative(), userId={}, relativeType={}", userId, relativeType);


        UpdateBindRequest request = new UpdateBindRequest.Builder()
                .parentId(userId)
                .parentType(PbMsgUtils.transParentType(relativeType))
                .build();

        httpClient.sendRequest(RequestUrl.UPDATE_BINDING, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                UpdateBindResponse response = SerializeUtils.parseFrom(data, UpdateBindResponse.class);

                if(response.user.userId == loginManager.getCurrentUser().getUserId()) {
                    loginManager.getCurrentUser().setRelativeType(relativeType);
                    loginManager.getCurrentUser().setNickname(response.user.nickname);

                    UserDbHelper.getInstance().saveUser(loginManager.getCurrentUser());
                }

                EventBus.getDefault().post(new RelativeEvent(
                        RelativeEvent.EventType.UPDATE_RELATIVE_SUCCESS,
                        null,
                        null));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new RelativeEvent(
                        RelativeEvent.EventType.UPDATE_RELATIVE_FAILED,
                        cause.getMessage(),
                        null));
            }
        });
    }

    @Override
    public void removeRelative(final long userId) {
        logger.debug("RelativeManager::removeRelative(), userId={}", userId);


        RelieveBindRequest request = new RelieveBindRequest.Builder()
                .parentId(userId)
                .build();

        httpClient.sendRequest(RequestUrl.REMOVE_RELATIVE, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                //the user is removed
                UserDbHelper.getInstance().deleteUser(userId);

                EventBus.getDefault().post(new RelativeEvent(
                        RelativeEvent.EventType.REMOVE_RELATIVE_SUCCESS,
                        null,
                        null));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new RelativeEvent(
                        RelativeEvent.EventType.REMOVE_RELATIVE_FAILED,
                        cause.getMessage(),
                        null));
            }
        });
    }

    @Override
    public void addRelative(String phoneNum, String verifyCode, int relativeType, String password) {
        logger.debug("RelativeManager::addRelative(), phoneNum={}， verifyCode={}， " +
                "relativeType={}, password=********", phoneNum, verifyCode, relativeType);


        ActiveInviteUserRequest request = new ActiveInviteUserRequest.Builder()
                .parentType(PbMsgUtils.transParentType(relativeType))
                .mobile(phoneNum)
                .password(password)
                .code(verifyCode)
                .build();

        httpClient.sendRequest(RequestUrl.ADD_RELATIVE, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                ActiveInviteUserResponse response = SerializeUtils.parseFrom(data, ActiveInviteUserResponse.class);

                User user = UserDbHelper.getInstance().getUserById(response.user.userId);

                if(user != null){
                    user.setNickname(response.user.nickname);
                    UserDbHelper.getInstance().saveUser(user);
                }

                EventBus.getDefault().post(new RelativeEvent(
                        RelativeEvent.EventType.ADD_RELATIVE_SUCCESS,
                        null,
                        null));
            }

            @Override
            public void onFailure(Throwable cause) {
                //TODO: ADD_RELATIVE_VERIFY_CODE_INCORRECT, ADD_RELATIVE_BIND_BY_OTHER
                EventBus.getDefault().post(new RelativeEvent(
                        RelativeEvent.EventType.ADD_RELATIVE_FAILED,
                        cause.getMessage(),
                        null));
            }
        });
    }
}
