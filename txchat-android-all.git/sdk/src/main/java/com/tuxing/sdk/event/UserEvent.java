package com.tuxing.sdk.event;

import com.tuxing.sdk.db.entity.User;

/**
 * Created by Alan on 2015/6/18.
 */
public class UserEvent extends BaseEvent {
    public enum EventType{
        QUERY_USER_BY_ID,
        REQUEST_USER_SUCCESS,
        REQUEST_USER_FAILED,
        USER_NOT_FOUND,
        UPDATE_MOBILE_SUCCESS,
        UPDATE_MOBILE_FAILED,
        UPDATE_MOBILE_VERIFY_CODE_INCORRECT,
        UPDATE_USER_SUCCESS,
        UPDATE_USER_FAILED,
        ACTIVE_USER_SUCCESS,
        ACTIVE_USER_FAILED,
        ACTIVE_USER_VERIFY_CODE_INCORRECT,
        CHANGE_PASSWORD_SUCCESS,
        CHANGE_PASSWORD_FAILED,
        UPDATEUSERPROFILE_SUCCESS
    }

    private EventType event;
    private User user;

    public UserEvent(EventType event, String msg, User user) {
        super(msg);
        this.event = event;
        this.user = user;
    }

    public EventType getEvent() {
        return event;
    }

    public User getUser() {
        return user;
    }
}
