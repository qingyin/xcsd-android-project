package com.tuxing.sdk.event;

import com.tuxing.sdk.db.entity.User;

import java.util.List;

/**
 * Created by Alan on 2015/7/27.
 */
public class MuteEvent extends BaseEvent {
    public enum EventType{
        ADD_TO_MUTED_LIST_SUCCESS,
        ADD_TO_MUTED_LIST_FAILED,
        REMOVE_FROM_MUTED_LIST_SUCCESS,
        REMOVE_FROM_MUTED_LIST_FAILED,
        FETCH_MUTED_LIST_SUCCESS,
        FETCH_MUTED_LIST_FAILED
    }

    private EventType event;
    private int muteType;
    private List<User> userList;

    public MuteEvent(EventType event, String msg, int muteType, List<User> userList) {
        super(msg);
        this.event = event;
        this.muteType = muteType;
        this.userList = userList;
    }

    public EventType getEvent() {
        return event;
    }

    public int getMuteType() {
        return muteType;
    }

    public List<User> getUserList() {
        return userList;
    }
}
