package com.tuxing.sdk.event.quora;

import com.tuxing.sdk.event.BaseEvent;
import com.tuxing.sdk.modle.Question;

import java.util.List;

/**
 * Created by alan on 15/12/2.
 */
public class QuestionEvent extends BaseEvent{
    public enum EventType{
        GET_TOP_HOT_QUESTION_SUCCESS,
        GET_TOP_HOT_QUESTION_FAILED,
        GET_LATEST_QUESTION_SUCCESS,
        GET_LATEST_QUESTION_FAILED,
        GET_HISTORY_QUESTION_SUCCESS,
        GET_HISTORY_QUESTION_FAILED,
        GET_QUESTION_BY_ID_SUCCESS,
        GET_QUESTION_BY_ID_FAILED,
        ASK_QUESTION_SUCCESS,
        ASK_QUESTION_FAILED
    }

    private EventType event;
    private List<Question> questions;
    private Integer page;
    private boolean hasMore;

    public QuestionEvent(EventType event, String msg, List<Question> questions, boolean hasMore) {
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

    public Integer getPage() {
        return page;
    }

    public boolean isHasMore() {
        return hasMore;
    }
}
