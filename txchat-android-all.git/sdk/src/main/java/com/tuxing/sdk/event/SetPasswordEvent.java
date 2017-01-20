package com.tuxing.sdk.event;

/**
 * Created by Alan on 2015/6/28.
 */
public class SetPasswordEvent extends BaseEvent {
    public enum EventType{
        RESET_PASSWORD_SUCCESS,
        RESET_PASSWORD_FAILED,
        VERIFY_CODE_INCORRECT
    }

    private EventType event;

    public SetPasswordEvent(String msg, EventType event) {
        super(msg);
        this.msg = msg;
        this.event = event;
    }

    public EventType getEvent() {
        return event;
    }
}
