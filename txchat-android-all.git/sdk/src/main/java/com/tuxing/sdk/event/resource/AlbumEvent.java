package com.tuxing.sdk.event.resource;

import com.tuxing.rpc.proto.Album;
import com.tuxing.sdk.event.BaseEvent;

import java.util.List;

/**
 * Created by alan on 16/1/8.
 */
public class AlbumEvent extends BaseEvent {
    public enum EventType{
        GET_LATEST_ALBUM_BY_CATEGORY_SUCCESS,
        GET_LATEST_ALBUM_BY_CATEGORY_FAILED,
        GET_HISTORY_ALBUM_BY_CATEGORY_SUCCESS,
        GET_HISTORY_ALBUM_BY_CATEGORY_FAILED,
        GET_LATEST_ALBUM_BY_PROVIDER_SUCCESS,
        GET_LATEST_ALBUM_BY_PROVIDER_FAILED,
        GET_HISTORY_ALBUM_BY_PROVIDER_SUCCESS,
        GET_HISTORY_ALBUM_BY_PROVIDER_FAILED,
        GET_ALBUM_BY_ID_SUCCESS,
        GET_ALBUM_BY_ID_FAILED
    }

    private EventType event;
    private List<Album> albums;
    private boolean hasMore;

    public AlbumEvent(EventType event, String msg, List<Album> albums, boolean hasMore) {
        super(msg);
        this.event = event;
        this.albums = albums;
        this.hasMore = hasMore;
    }

    public EventType getEvent() {
        return event;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public boolean isHasMore() {
        return hasMore;
    }
}
