package com.tuxing.sdk.event;


import com.tuxing.sdk.modle.Attachment;

/**
 * Created by Alan on 2015/6/12.
 */
public class UploadFileEvent extends BaseEvent {
    public enum EventType{
        UPLOAD_COMPETED,
        UPLOAD_FAILED,
        UPLOAD_PROGRESS_UPDATED,
        UPLOAD_CANCELED,
        GET_UPLOAD_TOKEN_FAILED
    }

    private EventType event;
    private Attachment attachment;


    public UploadFileEvent(EventType event, String msg, Attachment attachment) {
        super(msg);
        this.event = event;
        this.msg = msg;
        this.attachment = attachment;
    }

    public EventType getEvent() {
        return event;
    }

    public Attachment getAttachment() {
        return attachment;
    }
}
