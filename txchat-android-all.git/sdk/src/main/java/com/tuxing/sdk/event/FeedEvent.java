package com.tuxing.sdk.event;

import com.tuxing.sdk.db.entity.Feed;

import java.util.List;

/**
 * Created by Alan on 2015/7/3.
 */
public class FeedEvent extends BaseEvent {
    public enum EventType{
        GET_FEED_FROM_LOCAL_CACHE,
        REQUEST_LATEST_FEED_FROM_SERVER_SUCCESS,
        REQUEST_LATEST_FEED_FROM_SERVER_FAILED,
        REQUEST_HISTORY_FEED_FROM_SERVER_SUCCESS,
        REQUEST_HISTORY_FEED_FROM_SERVER_FAILED,
        GET_TIME_LINE_FROM_LOCAL_CACHE,
        REQUEST_LATEST_TIME_LINE_FROM_SERVER_SUCCESS,
        REQUEST_LATEST_TIME_LINE_FROM_SERVER_FAILED,
        REQUEST_HISTORY_TIME_LINE_FROM_SERVER_SUCCESS,
        REQUEST_HISTORY_TIME_LINE_FROM_SERVER_FAILED,
        REQUEST_LATEST_USER_TIME_LINE_FROM_SERVER_SUCCESS,
        REQUEST_LATEST_USER_TIME_LINE_FROM_SERVER_FAILED,
        REQUEST_HISTORY_USER_TIME_LINE_FROM_SERVER_SUCCESS,
        REQUEST_HISTORY_USER_TIME_LINE_FROM_SERVER_FAILED,
        QUERY_FEED_BY_ID,
        DELETE_FEED_SUCCESS,
        DELETE_FEED_FAILED,
        SEND_FEED_SUCCESS,
        SEND_FEED_FAILED
    }

    private EventType event;
    private List<Feed> feeds;
    private boolean hasMore;
    private int bonus;

    public FeedEvent(EventType event, String msg,
                     List<Feed> feeds, boolean hasMore) {
        super(msg);
        this.event = event;
        this.msg = msg;
        this.feeds = feeds;
        this.hasMore = hasMore;
        this.bonus = 0;
    }

    public FeedEvent(EventType event, String msg,
                     List<Feed> feeds, boolean hasMore,
                     int bonus) {
        this(event, msg, feeds, hasMore);
        this.bonus = bonus;
    }

    public EventType getEvent() {
        return event;
    }

    public List<Feed> getFeeds() {
        return feeds;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public int getBonus() {
        return bonus;
    }
}
