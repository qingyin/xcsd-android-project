package com.tuxing.sdk.event;

/**
 * Created by Alan on 2015/7/15.
 */
public class RegisterMachineEvent extends BaseEvent {
    public enum EventType{
        REGISTER_SUCCESS,
        REGISTER_FAILED
    }

    private EventType event;

    public RegisterMachineEvent(EventType event, String msg) {
        super(msg);
        this.event = event;
        this.msg = msg;
    }

    public EventType getEvent() {
        return event;
    }
}
