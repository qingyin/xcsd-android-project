package com.tuxing.sdk.event.resource;

import com.tuxing.rpc.proto.Provider;
import com.tuxing.rpc.proto.Resource;
import com.tuxing.sdk.event.BaseEvent;

import java.util.List;

/**
 * Created by alan on 16/1/7.
 */
public class HomePageResourceEvent extends BaseEvent {
    public enum EventType{
        GET_HOME_PAGE_RESOURCE_SUCCESS,
        GET_HOME_PAGE_RESOURCE_FAILED
    }

    private EventType event;
    private List<Resource> hotResources;
    private List<Resource> latestResources;
    private List<Resource> recommendedResources;
    private List<Provider> provides;

    public HomePageResourceEvent(EventType event, String msg, List<Resource> hotResources, List<Resource> latestResources,
                                 List<Resource> recommendedResources, List<Provider> provides) {
        super(msg);
        this.event = event;
        this.hotResources = hotResources;
        this.latestResources = latestResources;
        this.recommendedResources = recommendedResources;
        this.provides = provides;
    }

    public EventType getEvent() {
        return event;
    }

    public List<Resource> getHotResources() {
        return hotResources;
    }

    public List<Resource> getLatestResources() {
        return latestResources;
    }

    public List<Resource> getRecommendedResources() {
        return recommendedResources;
    }

    public List<Provider> getProvides() {
        return provides;
    }
}
