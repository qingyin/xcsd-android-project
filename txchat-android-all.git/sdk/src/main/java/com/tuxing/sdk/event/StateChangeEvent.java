package com.tuxing.sdk.event;

import java.util.List;

/**
 * Created by Alan on 2015/7/14.
 */
public class StateChangeEvent extends BaseEvent {
    public enum EventType{
        STATE_CHANGED,
    }

    private EventType event;
    private List<Integer> changeList;

    public StateChangeEvent(EventType event, String msg, List<Integer> changeList) {
        super(msg);
        this.event = event;
        this.changeList = changeList;

    }

    public EventType getEvent() {
        return event;
    }

    public List<Integer> getChangeList() {
        return changeList;
    }
}
