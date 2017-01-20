package com.tuxing.sdk.event;

import com.tuxing.sdk.modle.NotificationSetting;

/**
 * Created by Alan on 2015/7/2.
 */
public class NotificationSettingEvent extends BaseEvent {
    public enum EventType{
        GET_SETTING_SUCCESS,
        GET_SETTING_FAILED,
        CHANGE_SETTING_SUCCESS,
        CHANGE_SETTING_FAILED
    }

    private EventType event;
    private NotificationSetting setting;

    public NotificationSettingEvent(EventType event, String msg, NotificationSetting setting) {
        super(msg);
        this.event = event;
        this.msg = msg;
        this.setting = setting;
    }

    public EventType getEvent() {
        return event;
    }

    public NotificationSetting getSetting() {
        return setting;
    }
}
