package com.tuxing.sdk.event.quora;

import com.tuxing.sdk.event.BaseEvent;
import com.tuxing.sdk.modle.Expert;

import java.util.List;

/**
 * Created by alan on 15/12/6.
 */
public class ExpertEvent extends BaseEvent {
    public enum EventType{
        GET_TOP_HOT_EXPERT_SUCCESS,
        GET_TOP_HOT_EXPERT_FAILED,
        GET_EXPERT_SUCCESS,
        GET_EXPERT_FAILED,
        GET_EXPERT_DETAIL_SUCCESS,
        GET_EXPERT_DETAIL_FAILED
    }

    private EventType event;
    private List<Expert> experts;
    private boolean hasMore;

    public ExpertEvent(EventType event, String msg, List<Expert> experts, boolean hasMore) {
        super(msg);
        this.event = event;
        this.experts = experts;
        this.hasMore = hasMore;
    }

    public EventType getEvent() {
        return event;
    }

    public List<Expert> getExperts() {
        return experts;
    }

    public boolean isHasMore() {
        return hasMore;
    }
}
