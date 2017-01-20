package com.tuxing.sdk.modle;

import java.io.Serializable;
import java.util.List;

/**
 * Created by apple on 16/4/20.
 */
public class Answer implements Serializable {
    private static final long serialVersionUID = -2366700832771915514L;

    private Long answerId;
    private Long authorUserId;
    private String authorUserName;
    private String authorTitle;
    private String authorUserAvatar;
    private Boolean isExpert;
    private String content;
    private List<Attachment> attachments;
    private Long thanksCount;
    private Long commentCount;
    private Boolean thanked;
    private Long createOn;
    private Long updateOn;
    private Long questionId;
    private String questionTitle;
    private String questionAuthor;
    private List<Attachment> questionAttaches;
    private Long questionTagId;
    private String questionTagName;

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    public Long getAuthorUserId() {
        return authorUserId;
    }

    public void setAuthorUserId(Long authorUserId) {
        this.authorUserId = authorUserId;
    }

    public String getAuthorUserName() {
        return authorUserName;
    }

    public void setAuthorUserName(String authorUserName) {
        this.authorUserName = authorUserName;
    }

    public String getAuthorTitle() {
        return authorTitle;
    }

    public void setAuthorTitle(String authorTitle) {
        this.authorTitle = authorTitle;
    }

    public String getAuthorUserAvatar() {
        return authorUserAvatar;
    }

    public void setAuthorUserAvatar(String authorUserAvatar) {
        this.authorUserAvatar = authorUserAvatar;
    }

    public Boolean getExpert() {
        return isExpert;
    }

    public void setExpert(Boolean expert) {
        isExpert = expert;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public Long getThanksCount() {
        return thanksCount;
    }

    public void setThanksCount(Long thanksCount) {
        this.thanksCount = thanksCount;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }

    public Boolean getThanked() {
        return thanked;
    }

    public void setThanked(Boolean thanked) {
        this.thanked = thanked;
    }

    public Long getCreateOn() {
        return createOn;
    }

    public void setCreateOn(Long createOn) {
        this.createOn = createOn;
    }

    public Long getUpdateOn() {
        return updateOn;
    }

    public void setUpdateOn(Long updateOn) {
        this.updateOn = updateOn;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getQuestionAuthor() {
        return questionAuthor;
    }

    public void setQuestionAuthor(String questionAuthor) {
        this.questionAuthor = questionAuthor;
    }

    public List<Attachment> getQuestionAttaches() {
        return questionAttaches;
    }

    public void setQuestionAttaches(List<Attachment> questionAttaches) {
        this.questionAttaches = questionAttaches;
    }

    public Long getQuestionTagId() {
        return questionTagId;
    }

    public void setQuestionTagId(Long questionTagId) {
        this.questionTagId = questionTagId;
    }

    public String getQuestionTagName() {
        return questionTagName;
    }

    public void setQuestionTagName(String questionTagName) {
        this.questionTagName = questionTagName;
    }
}
