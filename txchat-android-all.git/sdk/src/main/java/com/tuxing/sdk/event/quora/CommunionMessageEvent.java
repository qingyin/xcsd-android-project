package com.tuxing.sdk.event.quora;

import com.tuxing.sdk.event.BaseEvent;
import com.tuxing.sdk.modle.CommunionMessage;

import java.util.List;

/**
 * Created by alan on 15/12/10.
 */
public class CommunionMessageEvent extends BaseEvent{
    public enum EventType{
        FETCH_LATEST_MESSAGE_SUCCESS,
        FETCH_LATEST_MESSAGE_FAILED,
        FETCH_HISTORY_MESSAGE_SUCCESS,
        FETCH_HISTORY_MESSAGE_FAILED
    }

    private EventType event;
    private List<CommunionMessage> messages;
    private boolean hasMore;

    public CommunionMessageEvent(EventType event, String msg, List<CommunionMessage> messages, boolean hasMore) {
        super(msg);
        this.event = event;
        this.messages = messages;
        this.hasMore = hasMore;
    }

    public EventType getEvent() {
        return event;
    }

    public List<CommunionMessage> getMessages() {
        return messages;
    }

    public boolean isHasMore() {
        return hasMore;
    }
}
