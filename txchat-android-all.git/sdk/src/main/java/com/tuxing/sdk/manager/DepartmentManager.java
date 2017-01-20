package com.tuxing.sdk.manager;

import com.tuxing.sdk.db.entity.Department;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.task.AsyncMethod;

import java.util.List;

/**
 * Created by Alan on 2015/6/25.
 */
public interface DepartmentManager extends BaseManager {

    @AsyncMethod
    void getDepartmentInfoFromLocal(long departmentId);

    void requestDepartmentInfoFromServer(long departmentId);

    @AsyncMethod
    void getDepartmentByChatGroupId(String chatGroupId);

    Department getDepartment(long departmentId);

    List<User> getDepartmentMembersByGroupId(String chatGroupId);

    /***
     * 禁言用户
     * 操作成功，返回事件MuteEvent.EventType.ADD_TO_MUTED_LIST_SUCCESS
     * 操作失败，返回事件MuteEvent.EventType.ADD_TO_MUTED_LIST_FAILED
     * @param departmentId 班级ID
     * @param userIds      要禁言的用户ID列表
     */
    void addUsersToMutedList(long departmentId, List<Long> userIds, int muteType);

    /***
     * 取消禁言
     * 操作成功，返回事件MuteEvent.EventType.REMOVE_FROM_MUTED_LIST_SUCCESS
     * 操作失败，返回事件MuteEvent.EventType.REMOVE_FROM_MUTED_LIST_FAILED
     * @param departmentId 班级ID
     * @param userId       要解禁的用户ID
     */
    void removeUsersFromMutedList(long departmentId, Long userId, int muteType);

    /***
     * 获取禁言列表
     * 操作成功，返回事件MuteEvent.EventType.FETCH_MUTED_LIST_SUCCESS
     * 操作失败，返回事件MuteEvent.EventType.FETCH_MUTED_LIST_FAILED
     * @param departmentId 班级ID
     */
    void getMutedUserList(long departmentId, int muteType);

}
