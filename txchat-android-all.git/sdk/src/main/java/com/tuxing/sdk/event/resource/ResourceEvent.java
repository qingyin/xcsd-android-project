package com.tuxing.sdk.event.resource;

import com.tuxing.rpc.proto.Resource;
import com.tuxing.sdk.event.BaseEvent;

import java.util.List;

/**
 * Created by alan on 16/1/7.
 */
public class ResourceEvent extends BaseEvent{
    public enum EventType{
        GET_HOT_RESOURCE_SUCCESS,
        GET_HOT_RESOURCE_FAILED,
        GET_LATEST_RESOURCE_SUCCESS,
        GET_LATEST_RESOURCE_FAILED,
        GET_HISTORY_RESOURCE_SUCCESS,
        GET_HISTORY_RESOURCE_FAILED,
        GET_RECOMMEND_RESOURCE_SUCCESS,
        GET_RECOMMEND_RESOURCE_FAILED,
        GET_PLAY_HISTORY_SUCCESS,
        GET_PLAY_HISTORY_FAILED,
        GET_NEXT_RESOURCE_SUCCESS,
        GET_NEXT_RESOURCE_FAILED,
        GET_RESOURCE_BY_ALBUM_SUCCESS,
        GET_RESOURCE_BY_ALBUM_FAILED,
        GET_RESOURCE_BY_ID_SUCCESS,
        GET_RESOURCE_BY_ID_FAILED,
        GET_NEAR_RESOURCE_SUCCESS,
        GET_NEAR_RESOURCE_FAILED
    }

    private EventType event;
    private List<Resource> resources;
    private boolean hasMore;

    public ResourceEvent(EventType event, String msg, List<Resource> resources, boolean hasMore) {
        super(msg);
        this.event = event;
        this.resources = resources;
        this.hasMore = hasMore;
    }

    public EventType getEvent() {
        return event;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public boolean isHasMore() {
        return hasMore;
    }
}
