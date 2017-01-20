package com.tuxing.sdk.modle;

import com.tuxing.rpc.proto.CommunionAction;
import com.tuxing.rpc.proto.CommunionObjType;
import com.tuxing.rpc.proto.UserType;

import java.io.Serializable;

/**
 * Created by apple on 16/4/20.
 */
public class CommunionMessage implements Serializable {
    private static final long serialVersionUID = -457480212036900499L;

    private long messageId;
    private long targetId;
    private CommunionObjType type;
    private CommunionAction action;
    private UserType userType;
    private String userName;
    private String userTitle;
    private String userAvatar;
    private String title;
    private String content;
    private Long createOn;
    private Question question;
    private Answer answer;

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public CommunionObjType getType() {
        return type;
    }

    public void setType(CommunionObjType type) {
        this.type = type;
    }

    public CommunionAction getAction() {
        return action;
    }

    public void setAction(CommunionAction action) {
        this.action = action;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserTitle() {
        return userTitle;
    }

    public void setUserTitle(String userTitle) {
        this.userTitle = userTitle;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCreateOn() {
        return createOn;
    }

    public void setCreateOn(Long createOn) {
        this.createOn = createOn;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }
}
