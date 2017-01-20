package com.tuxing.sdk.event;

/**
 * Created by alan on 15/10/18.
 */
public class UserCheckInEvent extends BaseEvent{
    public enum EventType{
        USER_CHECK_IN_SUCCESS
    }

    private EventType event;
    private int bonus;

    public UserCheckInEvent(EventType event, String msg, int bonus) {
        super(msg);
        this.event = event;
        this.bonus = bonus;
    }

    public EventType getEvent() {
        return event;
    }

    public int getBonus() {
        return bonus;
    }
}
