package com.tuxing.sdk.db.entity;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table activity.
 */
public class Activity implements java.io.Serializable {

    private Long id;
    private long activityId;
    private String title;
    private String attachments;
    private String senderName;
    private String senderAvatar;
    private Boolean deleted;
    private Long createdOn;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Activity() {
    }

    public Activity(Long id) {
        this.id = id;
    }

    public Activity(Long id, long activityId, String title, String attachments, String senderName, String senderAvatar, Boolean deleted, Long createdOn) {
        this.id = id;
        this.activityId = activityId;
        this.title = title;
        this.attachments = attachments;
        this.senderName = senderName;
        this.senderAvatar = senderAvatar;
        this.deleted = deleted;
        this.createdOn = createdOn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getActivityId() {
        return activityId;
    }

    public void setActivityId(long activityId) {
        this.activityId = activityId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAttachments() {
        return attachments;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
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

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Long createdOn) {
        this.createdOn = createdOn;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}