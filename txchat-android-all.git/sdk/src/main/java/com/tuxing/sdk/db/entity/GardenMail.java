package com.tuxing.sdk.db.entity;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table garden_mail.
 */
public class GardenMail implements java.io.Serializable {

    private Long id;
    private long mailId;
    private String content;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private Long gardenId;
    private String gardenName;
    private String gardenAvatar;
    private Boolean anonymous;
    private Boolean updated;
    private Integer status;
    private Long sendTime;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public GardenMail() {
    }

    public GardenMail(Long id) {
        this.id = id;
    }

    public GardenMail(Long id, long mailId, String content, Long senderId, String senderName, String senderAvatar, Long gardenId, String gardenName, String gardenAvatar, Boolean anonymous, Boolean updated, Integer status, Long sendTime) {
        this.id = id;
        this.mailId = mailId;
        this.content = content;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderAvatar = senderAvatar;
        this.gardenId = gardenId;
        this.gardenName = gardenName;
        this.gardenAvatar = gardenAvatar;
        this.anonymous = anonymous;
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

    public long getMailId() {
        return mailId;
    }

    public void setMailId(long mailId) {
        this.mailId = mailId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public Long getGardenId() {
        return gardenId;
    }

    public void setGardenId(Long gardenId) {
        this.gardenId = gardenId;
    }

    public String getGardenName() {
        return gardenName;
    }

    public void setGardenName(String gardenName) {
        this.gardenName = gardenName;
    }

    public String getGardenAvatar() {
        return gardenAvatar;
    }

    public void setGardenAvatar(String gardenAvatar) {
        this.gardenAvatar = gardenAvatar;
    }

    public Boolean getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        this.anonymous = anonymous;
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
