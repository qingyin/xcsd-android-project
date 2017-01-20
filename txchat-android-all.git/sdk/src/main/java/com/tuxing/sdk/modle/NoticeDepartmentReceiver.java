package com.tuxing.sdk.modle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alan on 2015/6/10.
 */
public class NoticeDepartmentReceiver implements NoticeReceiver, Serializable{
    private static final long serialVersionUID = -2695816746235693370L;

    private Long departmentId;
    private String departmentName;
    private Long noticeId;
    private Integer memberCount;
    private Integer readCount;
    private List<Long> memberUserIds;
    private boolean all;

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Long getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(Long noticeId) {
        this.noticeId = noticeId;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    public Integer getReadCount() {
        return readCount;
    }

    public void setReadCount(Integer readCount) {
        this.readCount = readCount;
    }

    public List<Long> getMemberUserIds() {
        if(memberUserIds == null){
            memberUserIds = new ArrayList<>();
        }

        return memberUserIds;
    }

    public void setMemberUserIds(List<Long> memberUserIds) {
        this.memberUserIds = memberUserIds;
    }

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean all) {
        this.all = all;
    }


}
