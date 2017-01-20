package com.tuxing.sdk.event;

import com.tuxing.sdk.modle.NoticeReceiver;

import java.util.List;

/**
 * Created by Alan on 2015/6/10.
 */
public class NoticeReceiverEvent extends BaseEvent{
    public enum EventType{
        NOTICE_RECEIVER_GET_SUMMARY_SUCCESS,
        NOTICE_RECEIVER_GET_DETAIL_SUCCESS,
        NOTICE_RECEIVER_FAILED
    }

    private EventType event;
    private List<? extends NoticeReceiver> receivers;

    public NoticeReceiverEvent(EventType event, String msg, List<? extends NoticeReceiver> receivers) {
        super(msg);
        this.event = event;
        this.msg = msg;
        this.receivers = receivers;
    }

    public EventType getEvent() {
        return event;
    }

    public List<? extends NoticeReceiver> getReceivers() {
        return receivers;
    }
}
