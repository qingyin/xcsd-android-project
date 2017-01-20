package com.tuxing.sdk.event;

/**
 * Created by Alan on 2015/7/9.
 */
public class DeviceTokenEvent extends BaseEvent {
    public enum EventType{
        UPDATE_DEVICE_TOKEN_SUCCESS,
        UPDATE_DEVICE_TOKEN_FAILED
    }

    private EventType event;

    public DeviceTokenEvent(EventType event, String msg) {
        super(msg);
        this.event = event;
        this.msg = msg;
    }

    public EventType getEvent() {
        return event;
    }
}
