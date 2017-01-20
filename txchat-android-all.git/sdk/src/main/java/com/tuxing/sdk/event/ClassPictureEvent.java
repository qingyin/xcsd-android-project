package com.tuxing.sdk.event;

import com.tuxing.sdk.db.entity.ClassPicture;

import java.util.List;

/**
 * Created by alan on 15/9/25.
 */
public class ClassPictureEvent extends BaseEvent {
    public enum EventType{
        GET_LATEST_PICTURE_SUCCESS,
        GET_LATEST_PICTURE_FAILED,
        GET_HISTORY_PICTURE_SUCCESS,
        GET_HISTORY_PICTURE_FAILED,
        GET_LOCAL_PICTURE
    }

    private EventType event;
    private List<ClassPicture> pictures;
    private boolean hasMore;
    private long total;

    public ClassPictureEvent(EventType event, String msg, List<ClassPicture> pictures, boolean hasMore, long total) {
        super(msg);
        this.event = event;
        this.msg = msg;
        this.pictures = pictures;
        this.hasMore = hasMore;
        this.total = total;
    }

    public EventType getEvent() {
        return event;
    }

    public List<ClassPicture> getPictures() {
        return pictures;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public long getTotal() {
        return total;
    }
}
