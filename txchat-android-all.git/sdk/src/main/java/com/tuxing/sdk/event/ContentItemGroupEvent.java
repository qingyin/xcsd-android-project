package com.tuxing.sdk.event;

import com.tuxing.sdk.db.entity.ContentItemGroup;

import java.util.List;

/**
 * Created by Alan on 2015/7/5.
 */
public class ContentItemGroupEvent extends BaseEvent {
    public enum EventType{
        FETCH_LATEST_ITEM_GROUP_SUCCESS,
        FETCH_LATEST_ITEM_GROUP_FAILED,
        FETCH_ITEM_GROUP_FROM_LOCAL,
        FETCH_HISTORY_ITEM_GROUP_SUCCESS,
        FETCH_HISTORY_ITEM_GROUP_FAILED,
    }

    private EventType event;
    private List<ContentItemGroup> itemGroups;
    private boolean hasMore;

    public ContentItemGroupEvent(EventType event, String msg,
                            List<ContentItemGroup> itemGroups, boolean hasMore) {
        super(msg);
        this.event = event;
        this.msg = msg;
        this.itemGroups = itemGroups;
        this.hasMore = hasMore;
    }

    public EventType getEvent() {
        return event;
    }

    public List<ContentItemGroup> getItemGroups() {
        return itemGroups;
    }

    public boolean isHasMore() {
        return hasMore;
    }
}
