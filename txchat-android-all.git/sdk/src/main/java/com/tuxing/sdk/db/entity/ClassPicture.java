package com.tuxing.sdk.db.entity;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table class_picture.
 */
public class ClassPicture implements java.io.Serializable {

    private Long id;
    private long picId;
    private Long classId;
    private String picUrl;
    private Long createdOn;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public ClassPicture() {
    }

    public ClassPicture(Long id) {
        this.id = id;
    }

    public ClassPicture(Long id, long picId, Long classId, String picUrl, Long createdOn) {
        this.id = id;
        this.picId = picId;
        this.classId = classId;
        this.picUrl = picUrl;
        this.createdOn = createdOn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getPicId() {
        return picId;
    }

    public void setPicId(long picId) {
        this.picId = picId;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
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
