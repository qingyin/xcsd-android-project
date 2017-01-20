package com.tuxing.sdk.db.entity;


public class UploadFile implements java.io.Serializable {

    public String url;
    public Long createOn;
    public Boolean isAsynced;
    public Long photoId;
    public Long photoTime;
    public int type = 0;// 0 图片 1 视频

    public UploadFile() {
    }

    public UploadFile(String url) {
        this.url = url;
    }

    public UploadFile(String url, Long createOn) {
        this.url = url;
        this.createOn = createOn;
    }


    public UploadFile(String url, Long createOn, Boolean isAsynced, Long photoId, Long photoTime, int type) {
        this.url = url;
        this.createOn = createOn;
        this.isAsynced = isAsynced;
        this.photoId = photoId;
        this.photoTime = photoTime;
        this.type = type;
    }

    public UploadFile(String url, Long createOn, Boolean isAsynced, Long photoId, Long photoTime) {
        this.url = url;
        this.createOn = createOn;
        this.isAsynced = isAsynced;
        this.photoId = photoId;
        this.photoTime = photoTime;
    }



}
