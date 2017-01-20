package com.tuxing.sdk.manager;

import com.tuxing.sdk.db.entity.Department;
import com.tuxing.sdk.task.AsyncMethod;

import java.util.List;

/**
 * Created by Alan on 2015/6/25.
 */
public interface ContactManager extends BaseManager {
    void syncContact();

    @AsyncMethod
    void syncAllDepartment();

    void syncDepartment(long departmentId);

    List<Department> getAllDepartment();

    @AsyncMethod
    void getDepartmentMemberByUserType(long departmentId, int userType);

    @AsyncMethod
    void getDepartmentforshare();

    @AsyncMethod
    void getAllDepartmentMember(long departmentId);

    @AsyncMethod
    void getDepartmentMemberCount(long departmentId);

    Long getDepartmentMemberCountByUserType(long departmentId, int userType);
}
