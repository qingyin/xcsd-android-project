package com.tuxing.sdk.event;

import com.tuxing.sdk.db.entity.AttendanceRecord;

/**
 * Created by alan on 15/10/25.
 */
public class UploadCheckInEvent extends BaseEvent {
    public enum EventType {
        UPLOAD_CHECK_IN_SUCCESS,
        UPLOAD_CHECK_IN_FAILED
    }

    EventType event;
    AttendanceRecord record;

    public UploadCheckInEvent(EventType event, String msg, AttendanceRecord record) {
        super(msg);
        this.event = event;
        this.record = record;
    }

    public EventType getEvent() {
        return event;
    }

    public AttendanceRecord getRecord() {
        return record;
    }
}
