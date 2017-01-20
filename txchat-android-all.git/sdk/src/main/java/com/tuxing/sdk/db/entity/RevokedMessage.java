package com.tuxing.sdk.db.entity;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table revoked_message.
 */
public class RevokedMessage implements java.io.Serializable {

    private Long id;
    private String msgId;
    private String cmdMsgId;
    private String from;
    private String to;
    private Boolean isGroup;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public RevokedMessage() {
    }

    public RevokedMessage(Long id) {
        this.id = id;
    }

    public RevokedMessage(Long id, String msgId, String cmdMsgId, String from, String to, Boolean isGroup) {
        this.id = id;
        this.msgId = msgId;
        this.cmdMsgId = cmdMsgId;
        this.from = from;
        this.to = to;
        this.isGroup = isGroup;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getCmdMsgId() {
        return cmdMsgId;
    }

    public void setCmdMsgId(String cmdMsgId) {
        this.cmdMsgId = cmdMsgId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Boolean getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(Boolean isGroup) {
        this.isGroup = isGroup;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}