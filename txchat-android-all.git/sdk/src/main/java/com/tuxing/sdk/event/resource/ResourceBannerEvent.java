package com.tuxing.sdk.event.resource;

import com.tuxing.rpc.proto.ResourceBanner;
import com.tuxing.sdk.event.BaseEvent;

import java.util.List;

/**
 * Created by alan on 16/1/6.
 */
public class ResourceBannerEvent extends BaseEvent {
    public enum EventType{
        GET_RESOURCE_BANNER_SUCCESS,
        GET_RESOURCE_BANNER_FAILED
    }

    private EventType event;
    private List<ResourceBanner> banners;

    public ResourceBannerEvent(EventType event, String msg, List<ResourceBanner> banners) {
        super(msg);
        this.event = event;
        this.banners = banners;
    }

    public EventType getEvent() {
        return event;
    }

    public List<ResourceBanner> getBanners() {
        return banners;
    }
}
