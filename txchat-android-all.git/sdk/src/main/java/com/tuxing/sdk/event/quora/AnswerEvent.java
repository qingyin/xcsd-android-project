package com.tuxing.sdk.event.quora;

import com.tuxing.sdk.event.BaseEvent;
import com.tuxing.sdk.modle.Answer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alan on 15/12/2.
 */
public class AnswerEvent extends BaseEvent {
    public enum EventType{
        GET_LATEST_ANSWER_SUCCESS,
        GET_LATEST_ANSWER_FAILED,
        GET_HISTORY_ANSWER_SUCCESS,
        GET_HISTORY_ANSWER_FAILED,
        ANSWER_QUESTION_SUCCESS,
        ANSWER_QUESTION_FAILED,
        DELETE_ANSWER_SUCCESS,
        DELETE_ANSWER_FAILED
    }

    private EventType event;
    private List<Answer> expertAnswers;
    private List<Answer> answers;
    private boolean hasMore;

    public AnswerEvent(EventType event, String msg, List<Answer> expertAnswers, List<Answer> answers, boolean hasMore) {
        super(msg);
        this.event = event;
        this.expertAnswers = expertAnswers;
        this.answers = answers;
        this.hasMore = hasMore;
    }

    public AnswerEvent(EventType event, String msg, List<Answer> answers, boolean hasMore) {
        this(event, msg, new ArrayList<Answer>(), answers, hasMore);
    }


    public EventType getEvent() {
        return event;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public List<Answer> getExpertAnswers() {
        return expertAnswers;
    }

    public boolean isHasMore() {
        return hasMore;
    }
}
