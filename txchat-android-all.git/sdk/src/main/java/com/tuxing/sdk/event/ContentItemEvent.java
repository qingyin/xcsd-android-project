package com.tuxing.sdk.event;

import com.tuxing.sdk.db.entity.ContentItem;

import java.util.List;

/**
 * Created by Alan on 2015/7/5.
 */
public class ContentItemEvent extends BaseEvent {
    public enum EventType{
        FETCH_LATEST_ITEM_SUCCESS,
        FETCH_LATEST_ITEM_FAILED,
        FETCH_HISTORY_ITEM_SUCCESS,
        FETCH_HISTORY_ITEM_FAILED,
        FETCH_ITEM_FROM_LOCAL,
        FETCH_GARDEN_INTRO_SUCCESS,
        FETCH_GARDEN_INTRO_FAILED,
        FETCH_AGREEMENT_SUCCESS,
        FETCH_AGREEMENT_FAILED,
        FETCH_CONTENT_SUCCESS,
        FETCH_CONTENT_FAILED
    }

    private EventType event;
    private List<ContentItem> items;
    private boolean hasMore;

    public ContentItemEvent(EventType event, String msg,
                            List<ContentItem> items, boolean hasMore) {
        super(msg);
        this.event = event;
        this.msg = msg;
        this.items = items;
        this.hasMore = hasMore;
    }

    public EventType getEvent() {
        return event;
    }

    public List<ContentItem> getItems() {
        return items;
    }

    public boolean isHasMore() {
        return hasMore;
    }
}
