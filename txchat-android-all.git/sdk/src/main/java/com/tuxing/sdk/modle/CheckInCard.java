package com.tuxing.sdk.modle;

import java.io.Serializable;

/**
 * Created by Alan on 2015/6/19.
 */
public class CheckInCard implements Serializable {
    private static final long serialVersionUID = -5334734505353071729L;

    private String userName;
    private String cardNum;
    private int relativeType;
    private Long userId;
    private String avatar;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public int getRelativeType() {
        return relativeType;
    }

    public void setRelativeType(int relativeType) {
        this.relativeType = relativeType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
