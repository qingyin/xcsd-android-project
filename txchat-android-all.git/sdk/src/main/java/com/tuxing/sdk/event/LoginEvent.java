package com.tuxing.sdk.event;

import com.tuxing.sdk.db.entity.LoginUser;

/**
 * Created by Alan on 2015/5/18.
 */
public class LoginEvent extends BaseEvent {
    public enum EventType{
        LOGIN_SUCCESS,
        LOGIN_NO_SUCH_USER,
        LOGIN_AUTH_FAILED,
        LOGIN_FAILED_UNKNOWN_REASON,
        LOGOUT,
        KICK_OFF,
        TOKEN_EXPIRED,
        REOPRTNOEWLOGOUT
    }

    private EventType event;
    private LoginUser user;

    public LoginEvent(EventType event, LoginUser user, String msg) {
        super(msg);
        this.event = event;
        this.user = user;
        this.msg = msg;
    }

    public EventType getEvent() {
        return event;
    }

    public LoginUser getUser() {
        return user;
    }
}
