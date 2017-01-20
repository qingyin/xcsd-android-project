package com.tuxing.sdk.event.quora;

import com.tuxing.sdk.event.BaseEvent;
import com.tuxing.sdk.modle.Knowledge;

import java.util.List;

/**
 * Created by alan on 15/12/6.
 */
public class KnowledgeEvent extends BaseEvent {
    public enum EventType{
        GET_TOP_HOT_KNOWLEDGE_SUCCESS,
        GET_TOP_HOT_KNOWLEDGE_FAILED,
        GET_LATEST_KNOWLEDGE_SUCCESS,
        GET_LATEST_KNOWLEDGE_FAILED,
        GET_HISTORY_KNOWLEDGE_SUCCESS,
        GET_HISTORY_KNOWLEDGE_FAILED,
        GET_KNOWLEDGE_BY_ID_SUCCESS,
        GET_KNOWLEDGE_BY_ID_FAILED
    }

    private EventType event;
    private List<Knowledge> knowledges;
    private Boolean hasMore;

    public KnowledgeEvent(EventType event, String msg, List<Knowledge> knowledges, Boolean hasMore) {
        super(msg);
        this.event = event;
        this.knowledges = knowledges;
        this.hasMore = hasMore;
    }

    public EventType getEvent() {
        return event;
    }

    public List<Knowledge> getKnowledges() {
        return knowledges;
    }

    public Boolean getHasMore() {
        return hasMore;
    }
}
