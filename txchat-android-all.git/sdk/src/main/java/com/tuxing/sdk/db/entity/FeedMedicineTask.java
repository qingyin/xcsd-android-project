package com.tuxing.sdk.db.entity;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table feed_medicine_task.
 */
public class FeedMedicineTask implements java.io.Serializable {

    private Long id;
    private long taskId;
    private String description;
    private Long beginDate;
    private String attachments;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private Long classId;
    private String className;
    private String classAvatar;
    private Boolean updated;
    private Integer status;
    private Long sendTime;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public FeedMedicineTask() {
    }

    public FeedMedicineTask(Long id) {
        this.id = id;
    }

    public FeedMedicineTask(Long id, long taskId, String description, Long beginDate, String attachments, Long senderId, String senderName, String senderAvatar, Long classId, String className, String classAvatar, Boolean updated, Integer status, Long sendTime) {
        this.id = id;
        this.taskId = taskId;
        this.description = description;
        this.beginDate = beginDate;
        this.attachments = attachments;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderAvatar = senderAvatar;
        this.classId = classId;
        this.className = className;
        this.classAvatar = classAvatar;
        this.updated = updated;
        this.status = status;
        this.sendTime = sendTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Long beginDate) {
        this.beginDate = beginDate;
    }

    public String getAttachments() {
        return attachments;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
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

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassAvatar() {
        return classAvatar;
    }

    public void setClassAvatar(String classAvatar) {
        this.classAvatar = classAvatar;
    }

    public Boolean getUpdated() {
        return updated;
    }

    public void setUpdated(Boolean updated) {
        this.updated = updated;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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