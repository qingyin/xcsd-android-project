package com.tuxing.sdk.manager.impl;

import android.content.Context;
import com.tuxing.rpc.proto.*;
import com.tuxing.sdk.db.entity.Department;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.db.helper.UserDbHelper;
import com.tuxing.sdk.event.BaseEvent;
import com.tuxing.sdk.event.GetDepartmentMemberEvent;
import com.tuxing.sdk.event.GetShareDepartmentMemberEvent;
import com.tuxing.sdk.event.LoginEvent;
import com.tuxing.sdk.event.SyncContactEvent;
import com.tuxing.sdk.http.HttpClient;
import com.tuxing.sdk.http.RequestCallback;
import com.tuxing.sdk.http.RequestUrl;
import com.tuxing.sdk.manager.ContactManager;
import com.tuxing.sdk.manager.LoginManager;
import com.tuxing.sdk.modle.DepartmentMember;
import com.tuxing.sdk.task.AsyncTaskProxyFactory;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.Constants;
import com.tuxing.sdk.utils.PbMsgUtils;
import com.tuxing.sdk.utils.SerializeUtils;
import de.greenrobot.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Created by Alan on 2015/5/21.
 */
public class ContactManagerImpl implements ContactManager {
    private final static Logger logger = LoggerFactory.getLogger(ContactManager.class);
    private static ContactManager instance;

    private List<Department> departments;

    LoginManager loginManager = LoginManagerImpl.getInstance();
    HttpClient httpClient = HttpClient.getInstance();
    private Context context;

    private ContactManagerImpl() {
        EventBus.getDefault().register(ContactManagerImpl.this, Constants.EVENT_PRIORITY_MANAGER);
    }

    public synchronized static ContactManager getInstance() {
        if (instance == null) {
            instance = new ContactManagerImpl();
            instance = AsyncTaskProxyFactory.getProxy(instance);
        }

        return instance;
    }

    @Override
    public void init(Context context) {
        this.context = context;

        if(loginManager.getCurrentUser() != null){
            departments = UserDbHelper.getInstance().getAllDepartment();
        }else{
            departments = new ArrayList<>();
        }

        if(EventBus.getDefault().isRegistered(this) == false) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void destroy() {
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override public void syncContact() {
        logger.debug("ContactManager::syncContact()");
        
        String lastSync = UserDbHelper.getInstance().getSettingValue(Constants.SETTING_FIELD.CONTACT_LAST_SYNC);

        final FetchContactsRequest request = new FetchContactsRequest.Builder()
                .lastModifiedSince(lastSync != null && !lastSync.equals("") ? Long.parseLong(lastSync) : 0)
                .build();

        RequestCallback callback = new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchContactsResponse response = SerializeUtils.parseFrom(data, FetchContactsResponse.class);

                //save to db
                List<Department> departmentList = new ArrayList<>();
                for (com.tuxing.rpc.proto.Department pbDepartment : response.departments) {
                    if (pbDepartment.actionType == ActionType.DELETE) {
                        UserDbHelper.getInstance().deleteDepartment(pbDepartment.id);
                    } else {
                        Department department = PbMsgUtils.transObj(pbDepartment);
                        departmentList.add(department);
                    }
                }

                if (!CollectionUtils.isEmpty(departmentList)) {
                    UserDbHelper.getInstance().saveAllDepartments(departmentList);
                }

                UserDbHelper.getInstance().saveSetting(Constants.SETTING_FIELD.CONTACT_LAST_SYNC,
                        String.valueOf(response.fetchTime));

                EventBus.getDefault().post(new SyncContactEvent(
                        SyncContactEvent.EventType.SYNC_CONTACT_SUCCESS,
                        null));

                departments = UserDbHelper.getInstance().getAllDepartment();

                syncAllDepartment();
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new SyncContactEvent(
                        SyncContactEvent.EventType.SYNC_CONTACT_FAILED,
                        cause.getMessage()));
            }
        };

        httpClient.sendRequest(RequestUrl.SYNC_CONTACT, request.toByteArray(), callback);
    }

    @Override public void syncAllDepartment() {
        logger.debug("ContactManager::syncAllDepartment()");

        List<Department> departments = UserDbHelper.getInstance().getAllDepartment();

        for (Department department : departments) {
            syncDepartment(department.getDepartmentId());
        }
    }

    private void syncDepartment(final Department department, final FetchDepartmentMembersRequest request) {
        httpClient.sendRequest(RequestUrl.SYNC_DEPARTMENT_MEMBERS, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchDepartmentMembersResponse response = SerializeUtils.parseFrom(data, FetchDepartmentMembersResponse.class);

                List<User> userList = new ArrayList<>();
                for (com.tuxing.rpc.proto.User pbUser : response.members) {
                    if (pbUser.actionType == ActionType.DELETE) {
                        UserDbHelper.getInstance().deleteDepartmentUser(department.getDepartmentId(),
                                pbUser.userId);
                    } else {
                        User user = PbMsgUtils.transObj(pbUser);
                        userList.add(user);
                    }
                }

                //we need to insert the user and department_user in a transaction.
                if (!CollectionUtils.isEmpty(userList)) {
                    UserDbHelper.getInstance().saveDepartmentUsers(department.getDepartmentId(), userList);
                }

                department.setLastSync(response.lastFetchTime);
                UserDbHelper.getInstance().saveDepartment(department);

                if (response.hasMore) {
                    final FetchDepartmentMembersRequest nextRequest = new FetchDepartmentMembersRequest.Builder()
                            .departmentId(department.getDepartmentId())
                            .lastFetchTime(response.lastFetchTime)
                            .build();

                    syncDepartment(department, nextRequest);
                }
            }

            @Override
            public void onFailure(Throwable cause) {

            }
        });
    }

    @Override public void syncDepartment(long departmentId) {
        logger.debug("ContactManager::syncDepartment(), departmentId={}", departmentId);
        
        final Department department = UserDbHelper.getInstance().getDepartment(departmentId);
        final FetchDepartmentMembersRequest request = new FetchDepartmentMembersRequest.Builder()
                .departmentId(departmentId)
                .lastFetchTime(department == null || department.getLastSync() == null ? 0 : department.getLastSync())
                .build();

        syncDepartment(department, request);
    }

    @Override public List<Department> getAllDepartment() {
        logger.debug("ContactManager::getAllDepartment()");

        return departments;
    }

    @Override public void getDepartmentMemberByUserType(long departmentId, int userType) {
        logger.debug("ContactManager::getDepartmentMember(), departmentId={}, userType={}", departmentId, userType);
        
        List<DepartmentMember> members = new ArrayList<>();

        List<User> userList = UserDbHelper.getInstance().getUsersInDepartmentByType(
                departmentId, Arrays.asList(userType));

        logger.debug("Get department member from db, count: {}", userList.size());

        if (userType == Constants.USER_TYPE.CHILD) {
            List<User> parents = UserDbHelper.getInstance().getUsersInDepartmentByType(
                    departmentId, Arrays.asList(Constants.USER_TYPE.PARENT));

            Map<Long, DepartmentMember> memberMap = new HashMap<>();
            for (User child : userList) {
                DepartmentMember member = new DepartmentMember();
                member.setUser(child);
                member.setRelatives(new ArrayList<User>());

                memberMap.put(child.getUserId(), member);
                members.add(member);
            }

            for (User parent : parents) {
                if (parent.getChildUserId() != null) {
                    DepartmentMember member = memberMap.get(parent.getChildUserId());
                    if (member != null) {
                        member.getRelatives().add(parent);
                    }
                }
            }
        } else {
            for (User user : userList) {
                DepartmentMember member = new DepartmentMember();
                member.setUser(user);

                members.add(member);
            }
        }

        EventBus.getDefault().post(new GetDepartmentMemberEvent(
                null,
                GetDepartmentMemberEvent.EventType.QUERY_BY_TYPE,
                members,
                members.size(),
                userType));
    }
    private class shareEvent{ }
    public void onEventAsync(shareEvent event){
        List<DepartmentMember> members = new ArrayList<>();
        List<DepartmentMember> memberteacher = new ArrayList<>();

        List<Department> Department = new ArrayList<>();
        Department =getAllDepartment();

        Department department = new Department();
        for (int i = 0; i < Department.size(); i++) {
            department = Department.get(i);
            DepartmentMember departmentMember = new DepartmentMember();
            User user = new User();
            user.setAvatar(department.getAvatar() + "");
            user.setNickname(department.getName() + "");
            user.setUserId(Long.parseLong(department.getChatGroupId()));
            user.setType(120);
            departmentMember.setUser(user);
            memberteacher.add(departmentMember);
        }

        List<User> parents = UserDbHelper.getInstance().getAllParent();
        Map<Long, DepartmentMember> memberMap = new HashMap<>();

        List<User> userList = UserDbHelper.getInstance().getAllChildrenAndAllTeacher();
        for (User user : userList) {
            DepartmentMember member = new DepartmentMember();
            member.setUser(user);
            if (user.getType()==1){
                member.setRelatives(new ArrayList<User>());
                memberMap.put(user.getUserId(), member);
            }
            members.add(member);
        }

        for (User parent : parents) {
            if (parent.getChildUserId() != null) {
                DepartmentMember member = memberMap.get(parent.getChildUserId());
                if (member != null) {
                    member.getRelatives().add(parent);
                }
            }
        }

        EventBus.getDefault().post(new GetShareDepartmentMemberEvent(
                null,
                GetShareDepartmentMemberEvent.EventType.GET_FOR_SHARE,
                members,
                memberteacher
        ));
    }
    @Override
    public void getDepartmentforshare() {
        if(EventBus.getDefault().isRegistered(this) == false) {
            EventBus.getDefault().register(this);
        }
        EventBus.getDefault().post(new shareEvent());
    }

    @Override public void getAllDepartmentMember(long departmentId) {
        logger.debug("ContactManager::getAllDepartmentMember(), departmentId={}", departmentId);

        List<DepartmentMember> members = new ArrayList<>();

        List<User> users = UserDbHelper.getInstance().getUsersInDepartmentByType(
                departmentId, Arrays.asList(Constants.USER_TYPE.TEACHER, Constants.USER_TYPE.CHILD));

        logger.debug("Department {} has {} members", departmentId, users.size());

        for (User user : users) {
            DepartmentMember member = new DepartmentMember();
            member.setUser(user);

            members.add(member);
        }

        EventBus.getDefault().post(new GetDepartmentMemberEvent(
                null,
                GetDepartmentMemberEvent.EventType.GET_ALL,
                members,
                members.size(),
                0));
    }

    @Override public void getDepartmentMemberCount(long departmentId) {
        logger.debug("ContactManager::getDepartmentMemberCount(), departmentId={}", departmentId);


        Long memberCount = UserDbHelper.getInstance().getDepartmentMemberCountByType(
                departmentId, Arrays.asList(Constants.USER_TYPE.TEACHER, Constants.USER_TYPE.CHILD));

        logger.debug("Department {} has {} members", departmentId, memberCount);

        EventBus.getDefault().post(new GetDepartmentMemberEvent(
                null,
                GetDepartmentMemberEvent.EventType.QUERY_COUNT,
                null,
                memberCount,
                0));
    }

    public Long getDepartmentMemberCountByUserType(long departmentId, int userType) {
        logger.debug("ContactManager::getDepartmentMemberCountByUserType(), departmentId={}, userType={}",
                departmentId, userType);


        Long memberCount = UserDbHelper.getInstance().getDepartmentMemberCountByType(
                departmentId, Arrays.asList(userType));

        return memberCount;
    }

    public void onEvent(LoginEvent event){
        switch (event.getEvent()){
            case LOGIN_SUCCESS:
                departments = UserDbHelper.getInstance().getAllDepartment();
        }
    }
}
