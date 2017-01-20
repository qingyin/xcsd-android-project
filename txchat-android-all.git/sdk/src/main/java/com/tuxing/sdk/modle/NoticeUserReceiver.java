package com.tuxing.sdk.modle;

import java.io.Serializable;

/**
 * Created by Alan on 2015/6/10.
 */
public class NoticeUserReceiver implements NoticeReceiver, Serializable{
    private static final long serialVersionUID = 4740722702364949492L;

    private Long userId;
    private String userName;
    private String avatar;
    private boolean read;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
