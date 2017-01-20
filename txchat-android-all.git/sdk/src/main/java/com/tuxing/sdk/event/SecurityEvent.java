package com.tuxing.sdk.event;

/**
 * Created by Alan on 2015/7/24.
 */
public class SecurityEvent extends BaseEvent {
    public enum EventType{
        SEND_VERIFY_CODE_SUCCESS,
        SEND_VERIFY_CODE_FAILED
    }

    private EventType event;

    public SecurityEvent(EventType event, String msg) {
        super(msg);
        this.event = event;
    }

    public EventType getEvent() {
        return event;
    }
}
