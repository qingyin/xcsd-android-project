package com.tuxing.sdk.event;

import com.tuxing.sdk.db.entity.Notice;

import java.util.List;

/**
 * Created by Alan on 2015/6/10.
 */
public class NoticeEvent extends BaseEvent {
    public enum EventType{
        NOTICE_QUERY_BY_ID,
        NOTICE_INBOX_FROM_CACHE,
        NOTICE_OUTBOX_FROM_CACHE,
        NOTICE_INBOX_LATEST_NOTICE_SUCCESS,
        NOTICE_INBOX_REQUEST_SUCCESS,
        NOTICE_INBOX_REQUEST_FAILED,
        NOTICE_OUTBOX_LATEST_NOTICE_SUCCESS,
        NOTICE_OUTBOX_REQUEST_SUCCESS,
        NOTICE_OUTBOX_REQUEST_FAILED,
        NOTICE_SEND_SUCCESS,
        NOTICE_SEND_FAILED,
        NOTICE_CLEAR_SUCCESS,
        NOTICE_CLEAR_FAILED
    }

    private EventType event;
    private List<Notice> notices;
    private boolean hasMore;
    private int bonus;

    public NoticeEvent(EventType event, String msg, List<Notice> notices, boolean hasMore) {
        super(msg);
        this.event = event;
        this.msg = msg;
        this.notices = notices;
        this.hasMore = hasMore;
        this.bonus = 0;
    }

    public NoticeEvent(EventType event, String msg,
                       List<Notice> notices, boolean hasMore,
                       int bonus) {
        this(event, msg, notices, hasMore);
        this.bonus = bonus;
    }


        public EventType getEvent() {
        return event;
    }

    public List<Notice> getNotices() {
        return notices;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public int getBonus() {
        return bonus;
    }
}
