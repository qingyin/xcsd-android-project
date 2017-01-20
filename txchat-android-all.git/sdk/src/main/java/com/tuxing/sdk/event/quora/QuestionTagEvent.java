package com.tuxing.sdk.event.quora;

import com.tuxing.sdk.event.BaseEvent;
import com.tuxing.sdk.modle.QuestionTag;

import java.util.List;

/**
 * Created by alan on 15/12/2.
 */
public class QuestionTagEvent extends BaseEvent {
    public enum EventType {
        FETCH_TAGS_SUCCESS,
        FETCH_TAGS_FAILED
    }

    private EventType event;
    private List<QuestionTag> tags;

    public QuestionTagEvent(EventType event, String msg, List<QuestionTag> tags) {
        super(msg);
        this.event = event;
        this.tags = tags;
    }

    public EventType getEvent() {
        return event;
    }

    public List<QuestionTag> getTags() {
        return tags;
    }
}
