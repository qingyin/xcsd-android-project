package com.tuxing.sdk.event;

/**
 * Created by Alan on 2015/7/7.
 */
public class BaseEvent {
    protected String msg;

    public BaseEvent(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
