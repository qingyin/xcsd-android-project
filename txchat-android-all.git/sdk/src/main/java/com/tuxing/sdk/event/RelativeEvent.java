package com.tuxing.sdk.event;

import com.tuxing.sdk.modle.Relative;

import java.util.List;

/**
 * Created by Alan on 2015/6/29.
 */
public class RelativeEvent extends BaseEvent {
    public enum EventType{
        GET_RELATIVE_SUCCESS,
        GET_RELATIVE_FAILED,
        ADD_RELATIVE_SUCCESS,
        ADD_RELATIVE_FAILED,
        ADD_RELATIVE_VERIFY_CODE_INCORRECT,
        ADD_RELATIVE_BIND_BY_OTHER,
        REMOVE_RELATIVE_SUCCESS,
        REMOVE_RELATIVE_FAILED,
        UPDATE_RELATIVE_SUCCESS,
        UPDATE_RELATIVE_FAILED
    }

    private EventType event;
    private List<Relative> relatives;

    public RelativeEvent(EventType event, String msg, List<Relative> relatives) {
        super(msg);
        this.event = event;
        this.msg = msg;
        this.relatives = relatives;
    }

    public EventType getEvent() {
        return event;
    }

    public List<Relative> getRelatives() {
        return relatives;
    }
}
