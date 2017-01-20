package com.tuxing.sdk.db.entity;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table check_in_record.
 */
public class CheckInRecord implements java.io.Serializable {

    private Long id;
    private long checkInRecordId;
    private long userId;
    private Long gardenId;
    private String userName;
    private Long checkInTime;
    private Integer state;
    private String cardNum;
    private String snapshots;
    private String parentName;
    private String className;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public CheckInRecord() {
    }

    public CheckInRecord(Long id) {
        this.id = id;
    }

    public CheckInRecord(Long id, long checkInRecordId, long userId, Long gardenId, String userName, Long checkInTime, Integer state, String cardNum, String snapshots, String parentName, String className) {
        this.id = id;
        this.checkInRecordId = checkInRecordId;
        this.userId = userId;
        this.gardenId = gardenId;
        this.userName = userName;
        this.checkInTime = checkInTime;
        this.state = state;
        this.cardNum = cardNum;
        this.snapshots = snapshots;
        this.parentName = parentName;
        this.className = className;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getCheckInRecordId() {
        return checkInRecordId;
    }

    public void setCheckInRecordId(long checkInRecordId) {
        this.checkInRecordId = checkInRecordId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Long getGardenId() {
        return gardenId;
    }

    public void setGardenId(Long gardenId) {
        this.gardenId = gardenId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(Long checkInTime) {
        this.checkInTime = checkInTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public String getSnapshots() {
        return snapshots;
    }

    public void setSnapshots(String snapshots) {
        this.snapshots = snapshots;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
