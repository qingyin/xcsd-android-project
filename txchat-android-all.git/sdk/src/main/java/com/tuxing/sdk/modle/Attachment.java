package com.tuxing.sdk.modle;

import java.io.Serializable;

/**
 * Created by Alan on 2015/6/24.
 */
public class Attachment implements Serializable{
    private static final long serialVersionUID = 6860651379530055675L;

    private String fileUrl;
    private String localFilePath;
    private Integer type;
    private Integer status;
    private Integer progress;
    private Boolean isUploadCancel;

    private long creatOn;

    public long getCreatOn() {
        return creatOn;
    }

    public void setCreatOn(long creatOn) {
        this.creatOn = creatOn;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Boolean getIsUploadCancel() {
        return isUploadCancel;
    }

    public void setIsUploadCancel(Boolean isUploadCancel) {
        this.isUploadCancel = isUploadCancel;
    }
}
