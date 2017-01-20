package com.tuxing.sdk.event;

/**
 * Created by Alan on 2015/6/11.
 */
public class SyncContactEvent extends BaseEvent {
    public enum EventType {
        SYNC_CONTACT_SUCCESS,
        SYNC_CONTACT_FAILED,
        SYNC_DEPARTMENT_SUCCESS,
        SYNC_DEPARTMENT_FAILED
    }

    private EventType event;

    public SyncContactEvent(EventType event, String msg) {
        super(msg);
        this.event = event;
        this.msg = msg;
    }

    public EventType getEvent() {
        return event;
    }
}
