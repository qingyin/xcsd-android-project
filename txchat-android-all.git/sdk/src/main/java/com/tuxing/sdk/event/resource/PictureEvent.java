package com.tuxing.sdk.event.resource;

import com.tuxing.sdk.event.BaseEvent;

import java.util.List;

/**
 * Created by alan on 16/1/8.
 */
public class PictureEvent extends BaseEvent{
    public enum EventType{
        GET_RESOURCE_PICTURE_SUCCESS,
        GET_RESOURCE_PICTURE_FAILED
    }

    private EventType event;
    private List<String> picUrls;

    public PictureEvent(EventType event, String msg, List<String> picUrls) {
        super(msg);
        this.event = event;
        this.picUrls = picUrls;
    }

    public EventType getEvent() {
        return event;
    }

    public List<String> getPicUrls() {
        return picUrls;
    }
}
