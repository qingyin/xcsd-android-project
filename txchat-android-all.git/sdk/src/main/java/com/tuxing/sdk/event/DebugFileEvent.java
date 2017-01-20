package com.tuxing.sdk.event;

/**
 * Created by Alan on 2015/7/9.
 */
public class DebugFileEvent extends BaseEvent {
    public enum EventType{
        COLLECT_EVENT_SUCCESS,
        COLLECT_EVENT_FAILED,
        UPLOAD_INFO_SUCCESS,
        UPLOAD_INFO_FAILED
    }

    private EventType event;
    private String filePath;

    public DebugFileEvent(EventType event, String msg, String filePath) {
        super(msg);
        this.event = event;
        this.msg = msg;
        this.filePath = filePath;
    }

    public EventType getEvent() {
        return event;
    }

    public String getFilePath() {
        return filePath;
    }
}
