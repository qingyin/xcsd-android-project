package com.tuxing.sdk.db.entity;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table HomeWorkRecord.
 */
public class HomeWorkRecord implements java.io.Serializable {

    private Long id;
    private Long hwRecordId;
    private Long memberId;
    private String title;
    private Long sendUserId;
    private String senderName;
    private String senderAvatar;
    private String targetName;
    private Integer status;
    private Boolean hasRead;
    private Long sendTime;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public HomeWorkRecord() {
    }

    public HomeWorkRecord(Long id) {
        this.id = id;
    }

    public HomeWorkRecord(Long id, Long hwRecordId, Long memberId, String title, Long sendUserId, String senderName, String senderAvatar, String targetName, Integer status, Boolean hasRead, Long sendTime) {
        this.id = id;
        this.hwRecordId = hwRecordId;
        this.memberId = memberId;
        this.title = title;
        this.sendUserId = sendUserId;
        this.senderName = senderName;
        this.senderAvatar = senderAvatar;
        this.targetName = targetName;
        this.status = status;
        this.hasRead = hasRead;
        this.sendTime = sendTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHwRecordId() {
        return hwRecordId;
    }

    public void setHwRecordId(Long hwRecordId) {
        this.hwRecordId = hwRecordId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(Long sendUserId) {
        this.sendUserId = sendUserId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getHasRead() {
        return hasRead;
    }

    public void setHasRead(Boolean hasRead) {
        this.hasRead = hasRead;
    }

    public Long getSendTime() {
        return sendTime;
    }

    public void setSendTime(Long sendTime) {
        this.sendTime = sendTime;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
