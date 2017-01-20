package com.tuxing.sdk.event;

import com.tuxing.sdk.db.entity.CheckInRecord;

import java.util.List;

/**
 * Created by Alan on 2015/6/11.
 */
public class CheckInEvent extends BaseEvent {
    public enum EventType{
        CHECKIN_QUERY_BY_ID,
        CHECKIN_LOAD_FROM_CACHE,
        CHECKIN_LATEST_RECORDS_SUCCESS,
        CHECKIN_REQUEST_SUCCESS,
        CHECKIN_REQUEST_FAILED,
        CHECKIN_CLEAR_SUCCESS,
        CHECKIN_CLEAR_FAILED
    }

    private EventType event;
    private List<CheckInRecord> checkInRecords;
    private boolean hasMore;

    public CheckInEvent(EventType event, String msg, List<CheckInRecord> checkInRecords, boolean hasMore) {
        super(msg);
        this.event = event;
        this.msg = msg;
        this.checkInRecords = checkInRecords;
        this.hasMore = hasMore;
    }

    public EventType getEvent() {
        return event;
    }

    public List<CheckInRecord> getCheckInRecords() {
        return checkInRecords;
    }

    public boolean isHasMore() {
        return hasMore;
    }
}
