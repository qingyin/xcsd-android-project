package com.tuxing.sdk.event.resource;

import com.tuxing.rpc.proto.ResourceCategory;
import com.tuxing.sdk.event.BaseEvent;

import java.util.List;

/**
 * Created by alan on 16/1/7.
 */
public class ResourceCategoryEvent extends BaseEvent {
    public enum EventType{
        GET_RESOURCE_CATEGORY_SUCCESS,
        GET_RESOURCE_CATEGORY_FAILED
    }

    private EventType event;
    private List<ResourceCategory> categories;

    public ResourceCategoryEvent(EventType event, String msg, List<ResourceCategory> categories) {
        super(msg);
        this.event = event;
        this.categories = categories;
    }

    public EventType getEvent() {
        return event;
    }

    public List<ResourceCategory> getCategories() {
        return categories;
    }
}
