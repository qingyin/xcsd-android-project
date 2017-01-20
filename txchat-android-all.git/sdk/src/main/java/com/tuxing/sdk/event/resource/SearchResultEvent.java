package com.tuxing.sdk.event.resource;

import com.tuxing.rpc.proto.Album;
import com.tuxing.rpc.proto.Provider;
import com.tuxing.rpc.proto.Resource;
import com.tuxing.rpc.proto.SearchResultCategory;
import com.tuxing.sdk.event.BaseEvent;

import java.util.List;

/**
 * Created by alan on 16/1/8.
 */
public class SearchResultEvent extends BaseEvent {
    public enum EventType{
        GET_SEARCH_RESULT_SUCCESS,
        GET_SEARCH_RESULT_FAILED
    }

    private EventType event;
    private List<Resource> resources;
    private List<Album> albums;
    private List<Provider> providers;
    private SearchResultCategory category;

    public SearchResultEvent(EventType event, String msg, List<Resource> resources, List<Album> albums,
                             List<Provider> providers, SearchResultCategory category) {
        super(msg);
        this.event = event;
        this.resources = resources;
        this.albums = albums;
        this.providers = providers;
        this.category = category;
    }

    public EventType getEvent() {
        return event;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public List<Provider> getProviders() {
        return providers;
    }

    public SearchResultCategory getCategory() {
        return category;
    }
}
