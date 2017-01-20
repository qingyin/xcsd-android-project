package com.tuxing.sdk.event;

import com.tuxing.sdk.db.entity.Department;

/**
 * Created by Alan on 2015/6/11.
 */
public class DepartmentEvent extends BaseEvent {
    public enum EventType{
        DEPARTMENT_LOAD_FROM_LOCAL,
        DEPARTMENT_REQUEST_SUCCESS,
        DEPARTMENT_REQUEST_FAILED,
        DEPARTMENT_REQUEST_BY_GROUP_ID_SUCCESS,
        DEPARTMENT_REQUEST_BY_GROUP_ID_FAILED
    }

    private EventType event;
    private Department department;

    public DepartmentEvent(EventType event, Department department, String msg) {
        super(msg);
        this.event = event;
        this.department = department;
        this.msg = msg;
    }

    public EventType getEvent() {
        return event;
    }

    public Department getDepartment() {
        return department;
    }
}
