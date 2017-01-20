package com.tuxing.sdk.event;

import com.tuxing.sdk.modle.DepartmentMember;

import java.util.List;

/**
 * Created by Alan on 2015/6/11.
 */
public class GetDepartmentMemberEvent extends BaseEvent {
    public enum EventType{
        GET_ALL,
        QUERY_COUNT,
        QUERY_BY_TYPE,
        GET_FOR_SHARE
    }

    EventType event;
    List<DepartmentMember> departmentMembers;
    long memberCount;
    int userType;

    public GetDepartmentMemberEvent(String msg, EventType event, List<DepartmentMember> departmentMembers, long memberCount, int userType) {
        super( msg);
        this.event = event;
        this.departmentMembers = departmentMembers;
        this.memberCount = memberCount;
        this.userType = userType;
    }

    public List<DepartmentMember> getDepartmentMembers() {
        return departmentMembers;
    }

    public EventType getEvent() {
        return event;
    }

    public long getMemberCount() {
        return memberCount;
    }

    public int getUserType() {
        return userType;
    }
}
