package com.tuxing.sdk.event;

import com.tuxing.sdk.db.entity.Department;

import java.util.List;

/**
 * Created by Alan on 2015/6/11.
 */
public class GetContactEvent extends BaseEvent {
    List<Department> departments;

    public GetContactEvent(String msg, List<Department> departments) {
        super(msg);
        this.departments = departments;
    }

    public List<Department> getDepartments() {
        return departments;
    }
}
