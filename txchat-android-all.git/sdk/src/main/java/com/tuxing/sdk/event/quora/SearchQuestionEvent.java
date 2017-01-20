package com.tuxing.sdk.event.quora;

import com.tuxing.sdk.event.BaseEvent;
import com.tuxing.sdk.modle.Question;

import java.util.List;

/**
 * Created by mingwei on 3/1/16.
 */
public class SearchQuestionEvent extends BaseEvent {
    public enum EventType {
        SEARCH_QUESTION_SUCCESS,
        SEARCH_QUESTION_FAILED
    }

    private EventType event;
    private List<Question> questions;
    private boolean hasMore;

    public SearchQuestionEvent(EventType event, String msg, List<Question> questions, boolean hasMore) {
        super(msg);
        this.event = event;
        this.questions = questions;
        this.hasMore = hasMore;
    }

    public EventType getEvent() {
        return event;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public boolean ishasMore() {
        return hasMore;
    }
}
