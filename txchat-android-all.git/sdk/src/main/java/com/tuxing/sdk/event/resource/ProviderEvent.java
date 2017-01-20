package com.tuxing.sdk.event.resource;

import com.tuxing.rpc.proto.Provider;
import com.tuxing.sdk.event.BaseEvent;

import java.util.List;

/**
 * Created by alan on 16/1/7.
 */
public class ProviderEvent extends BaseEvent {
    public enum EventType{
        GET_LATEST_UPDATE_PROVIDER_SUCCESS,
        GET_LATEST_UPDATE_PROVIDER_FAILED,
        GET_PROVIDER_BY_ID_SUCCESS,
        GET_PROVIDER_BY_ID_FAILED
    }

    private EventType event;
    private List<Provider> providers;
    private boolean hasMore;

    public ProviderEvent(EventType event, String msg, List<Provider> providers, boolean hasMore) {
        super(msg);
        this.event = event;
        this.providers = providers;
        this.hasMore = hasMore;
    }

    public EventType getEvent() {
        return event;
    }

    public List<Provider> getProviders() {
        return providers;
    }

    public boolean isHasMore() {
        return hasMore;
    }
}
