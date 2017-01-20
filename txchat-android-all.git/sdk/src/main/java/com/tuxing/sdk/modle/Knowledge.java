package com.tuxing.sdk.modle;

import com.tuxing.rpc.proto.KnowledegeContentType;

import java.io.Serializable;
import java.util.List;

/**
 * Created by apple on 16/4/20.
 */
public class Knowledge implements Serializable {

    private static final long serialVersionUID = 7250926879663400880L;

    private long knowledgeId;
    private String title;
    private KnowledegeContentType type;
    private String description;
    private String coverImageUrl;
    private String contentUrl;
    private long authorUserId;
    private String authorTitle;
    private String authorAvatar;
    private long thankCount;
    private long commentCount;
    private long createOn;
    private boolean thanked;
    List<QuestionTag> tags;

    public long getKnowledgeId() {
        return knowledgeId;
    }

    public void setKnowledgeId(long knowledgeId) {
        this.knowledgeId = knowledgeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public KnowledegeContentType getType() {
        return type;
    }

    public void setType(KnowledegeContentType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public long getAuthorUserId() {
        return authorUserId;
    }

    public void setAuthorUserId(long authorUserId) {
        this.authorUserId = authorUserId;
    }

    public String getAuthorTitle() {
        return authorTitle;
    }

    public void setAuthorTitle(String authorTitle) {
        this.authorTitle = authorTitle;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }

    public long getThankCount() {
        return thankCount;
    }

    public void setThankCount(long thankCount) {
        this.thankCount = thankCount;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }

    public long getCreateOn() {
        return createOn;
    }

    public void setCreateOn(long createOn) {
        this.createOn = createOn;
    }

    public boolean isThanked() {
        return thanked;
    }

    public void setThanked(boolean thanked) {
        this.thanked = thanked;
    }

    public List<QuestionTag> getTags() {
        return tags;
    }

    public void setTags(List<QuestionTag> tags) {
        this.tags = tags;
    }
}

