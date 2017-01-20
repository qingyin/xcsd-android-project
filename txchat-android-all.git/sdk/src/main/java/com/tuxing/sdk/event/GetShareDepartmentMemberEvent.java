package com.tuxing.sdk.event;

import com.tuxing.sdk.modle.DepartmentMember;

import java.util.List;

/**
 * Created by Alan on 2015/6/11.
 */
public class GetShareDepartmentMemberEvent extends BaseEvent {
//    获取分享联系人
    public enum EventType{
        GET_FOR_SHARE
    }

    EventType event;
    List<DepartmentMember> departmentMembers;
    List<DepartmentMember> departments;

    public GetShareDepartmentMemberEvent(String msg, EventType event, List<DepartmentMember> departmentMembers,List<DepartmentMember> departments) {
        super( msg);
        this.event = event;
        this.departmentMembers = departmentMembers;
        this.departments = departments;
    }

    public List<DepartmentMember> getDepartmentMembers() {
        return departmentMembers;
    }
    public List<DepartmentMember> departments() {
        return departments;
    }

    public EventType getEvent() {
        return event;
    }

}
