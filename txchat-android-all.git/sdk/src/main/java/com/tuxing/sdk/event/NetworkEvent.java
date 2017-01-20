package com.tuxing.sdk.event;

/**
 * Created by Alan on 2015/6/9.
 */
public class NetworkEvent extends BaseEvent {
    public enum EventType {
        NETWORK_AVAILABLE,
        NETWORK_UNAVAILABLE,
        NETWORK_REQUEST_TIMEOUT
    }

    private EventType event;

    public NetworkEvent(EventType event, String msg) {
        super(msg);
        this.event = event;
        this.msg = msg;
    }

    public EventType getEvent() {
        return event;
    }
}
