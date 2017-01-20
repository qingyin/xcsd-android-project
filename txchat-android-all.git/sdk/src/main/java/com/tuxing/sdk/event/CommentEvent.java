package com.tuxing.sdk.event;


import com.tuxing.sdk.db.entity.Comment;

import java.util.List;

/**
 * Created by Alan on 2015/7/2.
 */
public class CommentEvent extends BaseEvent {
    public enum EventType{
        REPLAY_FEED_MEDICINE_TASK_SUCCESS,
        REPLAY_FEED_MEDICINE_TASK_FAILED,
        GET_FEED_MEDICINE_TASK_COMMENTS_SUCCESS,
        GET_FEED_MEDICINE_TASK_COMMENTS_FAILED,
        GET_FEED_MEDICINE_TASK_COMMENTS_FROM_LOCAL,
        REPLAY_GARDEN_MAIL_SUCCESS,
        REPLAY_GARDEN_MAIL_FAILED,
        GET_GARDEN_MAIL_COMMENTS_SUCCESS,
        GET_GARDEN_MAIL_COMMENTS_FAILED,
        GET_GARDEN_MAIL_COMMENTS_FROM_LOCAL,
        REPLAY_FEED_SUCCESS,
        REPLAY_FEED_FAILED,
        DELETE_COMMENT_SUCCESS,
        DELETE_COMMENT_FAILED,
        GET_LATEST_FEED_COMMENTS_SUCCESS,
        GET_LATEST_FEED_COMMENTS_FAILED,
        GET_HISTORY_FEED_COMMENTS_SUCCESS,
        GET_HISTORY_FEED_COMMENTS_FAILED,
        GET_LATEST_COMMENT_TO_ME_SUCCESS,
        GET_LATEST_COMMENT_TO_ME_FAILED,
        GET_HISTORY_COMMENT_TO_ME_SUCCESS,
        GET_HISTORY_COMMENT_TO_ME_FAILED,
        GET_COMMENT_TO_ME_FROM_LOCAL_CACHE,
        GET_LATEST_CONCERNED_COMMENT_SUCCESS,
        GET_LATEST_CONCERNED_COMMENT_FAILED,
        GET_HISTORY_CONCERNED_COMMENT_SUCCESS,
        GET_HISTORY_CONCERNED_COMMENT_FAILED,
        GET_LATEST_ANSWER_COMMENTS_SUCCESS,
        GET_LATEST_ANSWER_COMMENTS_FAILED,
        GET_HISTORY_ANSWER_COMMENTS_SUCCESS,
        GET_HISTORY_ANSWER_COMMENTS_FAILED,
        REPLAY_ANSWER_SUCCESS,
        REPLAY_ANSWER_FAILED,
        GET_LATEST_KNOWLEDGE_COMMENTS_SUCCESS,
        GET_LATEST_KNOWLEDGE_COMMENTS_FAILED,
        GET_HISTORY_KNOWLEDGE_COMMENTS_SUCCESS,
        GET_HISTORY_KNOWLEDGE_COMMENTS_FAILED,
        REPLAY_KNOWLEDGE_SUCCESS,
        REPLAY_KNOWLEDGE_FAILED,
        GET_LATEST_RESOURCE_COMMENTS_SUCCESS,
        GET_LATEST_RESOURCE_COMMENTS_FAILED,
        GET_HISTORY_RESOURCE_COMMENTS_SUCCESS,
        GET_HISTORY_RESOURCE_COMMENTS_FAILED,
        REPLAY_RESOURCE_SUCCESS,
        REPLAY_RESOURCE_FAILED,
    }

    private EventType event;
    private List<Comment> comments;
    private boolean hasMore;
    private int bonus;

    public CommentEvent(EventType event, String msg,
                        List<Comment> comments, boolean hasMore) {
        super(msg);
        this.event = event;
        this.msg = msg;
        this.comments = comments;
        this.hasMore = hasMore;
        this.bonus = 0;
    }

    public CommentEvent(EventType event, String msg,
                        List<Comment> comments, boolean hasMore,
                        int bonus) {
        this(event, msg, comments, hasMore);
        this.bonus = bonus;
    }

    public EventType getEvent() {
        return event;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public int getBonus() {
        return bonus;
    }
}
