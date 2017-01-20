package com.tuxing.sdk.event;

import com.tuxing.sdk.db.entity.User;

/**
 * Created by Alan on 2015/6/15.
 */
public class ChildEvent extends BaseEvent {
    public enum EventType{
        BIND_CHILD_SUCCESS,
        BIND_CHILD_FAILED,
        CHILD_BIND_BY_OTHERS,
        GET_CHILD_SUCCESS,
        GET_CHILD_FAILED,
    }

    public EventType event;
    public User child;

    public ChildEvent(EventType event, String msg, User child) {
        super(msg);
        this.event = event;
        this.msg = msg;
        this.child = child;
    }

    public EventType getEvent() {
        return event;
    }

    public User getChild() {
        return child;
    }
}
