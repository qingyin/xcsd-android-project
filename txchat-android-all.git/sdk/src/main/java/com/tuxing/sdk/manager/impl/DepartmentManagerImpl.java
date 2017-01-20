package com.tuxing.sdk.manager.impl;

import android.content.Context;
import com.tuxing.rpc.proto.*;
import com.tuxing.sdk.db.entity.Department;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.db.helper.UserDbHelper;
import com.tuxing.sdk.event.DepartmentEvent;
import com.tuxing.sdk.event.MuteEvent;
import com.tuxing.sdk.http.HttpClient;
import com.tuxing.sdk.http.RequestCallback;
import com.tuxing.sdk.http.RequestUrl;
import com.tuxing.sdk.manager.DepartmentManager;
import com.tuxing.sdk.task.AsyncTaskProxyFactory;
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
public class DepartmentManagerImpl implements DepartmentManager {
    private final static Logger logger = LoggerFactory.getLogger(DepartmentManager.class);
    private static DepartmentManager instance;

    HttpClient httpClient = HttpClient.getInstance();
    private Context context;

    private DepartmentManagerImpl(){

    }

    public synchronized static DepartmentManager getInstance(){
        if(instance == null){
            instance = new DepartmentManagerImpl();
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

    @Override public void getDepartmentInfoFromLocal(long departmentId){
        logger.debug("DepartmentManager::getDepartmentInfoFromLocal(), departmentId={}", departmentId);


        Department department = UserDbHelper.getInstance().getDepartmentById(departmentId);

        EventBus.getDefault().post(new DepartmentEvent(
                DepartmentEvent.EventType.DEPARTMENT_LOAD_FROM_LOCAL,
                department,
                null));
    }

    @Override public void requestDepartmentInfoFromServer(long departmentId){
        logger.debug("DepartmentManager::requestDepartmentInfoFromServer(), departmentId={}", departmentId);


        FetchDepartmentinfoRequest request = new FetchDepartmentinfoRequest.Builder()
                .departmentId(departmentId)
                .build();

        httpClient.sendRequest(RequestUrl.GET_DEPARTMENT_INFO, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchDepartmentinfoResponse response = SerializeUtils.parseFrom(data, FetchDepartmentinfoResponse.class);
                Department department = PbMsgUtils.transObj(response.department);

                UserDbHelper.getInstance().saveDepartment(department);
                EventBus.getDefault().post(new DepartmentEvent(
                        DepartmentEvent.EventType.DEPARTMENT_REQUEST_SUCCESS,
                        department,
                        null));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new DepartmentEvent(
                        DepartmentEvent.EventType.DEPARTMENT_REQUEST_FAILED,
                        null,
                        cause.getMessage()));
            }
        });
    }

    @Override public void getDepartmentByChatGroupId(String chatGroupId){
        logger.debug("DepartmentManager::getDepartmentByChatGroupId(), chatGroupId={}", chatGroupId);
        Department department = UserDbHelper.getInstance().getDepartmentByChatGroupId(chatGroupId);


        if(department == null){
            FetchDepartmentByGroupIdRequest request = new FetchDepartmentByGroupIdRequest.Builder()
                    .groupId(chatGroupId)
                    .build();

            httpClient.sendRequest(RequestUrl.GET_DEPARTMENT_INFO_BY_GROUP_ID, request.toByteArray(), new RequestCallback() {
                @Override
                public void onResponse(byte[] data) throws IOException {
                    FetchDepartmentByGroupIdResponse response = SerializeUtils.parseFrom(data,
                            FetchDepartmentByGroupIdResponse.class);

                    Department department = PbMsgUtils.transObj(response.department);

                    UserDbHelper.getInstance().saveDepartment(department);
                    EventBus.getDefault().post(new DepartmentEvent(
                                DepartmentEvent.EventType.DEPARTMENT_REQUEST_BY_GROUP_ID_SUCCESS,
                            department,
                            null));
                }

                @Override
                public void onFailure(Throwable cause) {
                    EventBus.getDefault().post(new DepartmentEvent(
                            DepartmentEvent.EventType.DEPARTMENT_REQUEST_BY_GROUP_ID_FAILED,
                            null,
                            cause.getMessage()));
                }
            });
        }else {
            EventBus.getDefault().post(new DepartmentEvent(
                    DepartmentEvent.EventType.DEPARTMENT_REQUEST_BY_GROUP_ID_SUCCESS,
                    department,
                    null));
        }
    }

    @Override
    public Department getDepartment(long departmentId) {
        logger.debug("DepartmentManager::getDepartment(), departmentId={}", departmentId);

        return UserDbHelper.getInstance().getDepartmentById(departmentId);
    }

    @Override
    public List<User> getDepartmentMembersByGroupId(String chatGroupId){
        logger.debug("DepartmentManager::getDepartmentMembersByGroupId(), chatGroupId={}", chatGroupId);

        List<User> userList = UserDbHelper.getInstance().getUsersInDepartmentByGroupId(chatGroupId);

        return userList;
    }

    @Override
    public void addUsersToMutedList(long departmentId, List<Long> userIds, final int muteType) {
        logger.debug("DepartmentManager::addUsersToMutedList(), departmentId={}, muteType={}",
                departmentId, muteType);


        MuteRequest.Builder requestBuilder = new MuteRequest.Builder();

        requestBuilder.departmentId(departmentId);

        requestBuilder.childUserIds = new ArrayList<>();
        for(Long userId : userIds){
            requestBuilder.childUserIds.add(userId);
        }

        requestBuilder.type = PbMsgUtils.transMuteType(muteType);


        MuteRequest request = requestBuilder.build();

        httpClient.sendRequest(RequestUrl.MUTE, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                EventBus.getDefault().post(new MuteEvent(
                        MuteEvent.EventType.ADD_TO_MUTED_LIST_SUCCESS,
                        null,
                        muteType,
                        null
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new MuteEvent(
                        MuteEvent.EventType.ADD_TO_MUTED_LIST_FAILED,
                        cause.getMessage(),
                        muteType,
                        null
                ));
            }
        });
    }

    @Override
    public void removeUsersFromMutedList(long departmentId, Long userId, final int muteType) {
        logger.debug("DepartmentManager::removeUsersFromMutedList(), departmentId={}, userId={}, muteType={}",
                departmentId, userId, muteType);


        UnMuteRequest request = new UnMuteRequest.Builder()
                .departmentId(departmentId)
                .childUserIds(userId)
                .type(PbMsgUtils.transMuteType(muteType))
                .build();

        httpClient.sendRequest(RequestUrl.UNMUTE, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                EventBus.getDefault().post(new MuteEvent(
                        MuteEvent.EventType.REMOVE_FROM_MUTED_LIST_SUCCESS,
                        null,
                        muteType,
                        null
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new MuteEvent(
                        MuteEvent.EventType.REMOVE_FROM_MUTED_LIST_FAILED,
                        cause.getMessage(),
                        muteType,
                        null
                ));
            }
        });
    }

    @Override
    public void getMutedUserList(long departmentId, final int muteType) {
        logger.debug("DepartmentManager::getMutedUserList(), departmentId={}, muteType={}",
                departmentId, muteType);


        FetchMuteRequest request = new FetchMuteRequest.Builder()
                .departmentId(departmentId)
                .type(PbMsgUtils.transMuteType(muteType))
                .build();

        httpClient.sendRequest(RequestUrl.FETCH_MUTED_LIST, request.toByteArray(), new RequestCallback() {

            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchMuteResponse response = SerializeUtils.parseFrom(data, FetchMuteResponse.class);

                List<User> userList = new ArrayList<>();
                for(Long userId : response.childUserIds){
                    User user = UserDbHelper.getInstance().getUserById(userId);
                    if(user == null){
                        user = new User();
                        user.setUserId(userId);
                    }

                    userList.add(user);
                }

                EventBus.getDefault().post(new MuteEvent(
                        MuteEvent.EventType.FETCH_MUTED_LIST_SUCCESS,
                        null,
                        muteType,
                        userList
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new MuteEvent(
                        MuteEvent.EventType.FETCH_MUTED_LIST_FAILED,
                        cause.getMessage(),
                        muteType,
                        null
                ));
            }
        });
    }
}
