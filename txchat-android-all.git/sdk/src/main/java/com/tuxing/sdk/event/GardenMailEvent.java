package com.tuxing.sdk.event;

import com.tuxing.sdk.db.entity.GardenMail;

import java.util.List;

/**
 * Created by Alan on 2015/6/30.
 */
public class GardenMailEvent extends BaseEvent {
    public enum EventType{
        FETCH_LATEST_MAIL_SUCCESS,
        FETCH_LATEST_MAIL_FAILED,
        FETCH_HISTORY_MAIL_SUCCESS,
        FETCH_HISTORY_MAIL_FAILED,
        FETCH_HISTORY_MAIL_FROM_LOCAL,
        SEND_PRINCIPAL_MAIL_SUCCESS,
        SEND_PRINCIPAL_MAIL_FAILED
    }

    private EventType event;
    private List<GardenMail> mails;
    private boolean hasMore;

    public GardenMailEvent(EventType event, String msg, List<GardenMail> mails, boolean hasMore) {
        super(msg);
        this.event = event;
        this.msg = msg;
        this.mails = mails;
        this.hasMore = hasMore;
    }

    public EventType getEvent() {
        return event;
    }

    public List<GardenMail> getMails() {
        return mails;
    }

    public boolean isHasMore() {
        return hasMore;
    }
}
