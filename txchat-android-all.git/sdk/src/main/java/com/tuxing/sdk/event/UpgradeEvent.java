package com.tuxing.sdk.event;

import com.tuxing.sdk.modle.UpgradeInfo;

/**
 * Created by Alan on 2015/8/5.
 */
public class UpgradeEvent extends BaseEvent {
    public enum EventType{
        GET_UPGRADE_SUCCESS,
        GET_UPGRADE_FAILED
    }

    private EventType event;
    private UpgradeInfo info;

    public UpgradeEvent(EventType event, String msg, UpgradeInfo info) {
        super(msg);
        this.event = event;
        this.info = info;
    }

    public EventType getEvent() {
        return event;
    }

    public UpgradeInfo getInfo() {
        return info;
    }
}
