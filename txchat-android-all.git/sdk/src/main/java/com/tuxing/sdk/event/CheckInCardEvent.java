package com.tuxing.sdk.event;

import com.tuxing.sdk.modle.CheckInCard;

import java.util.List;

/**
 * Created by Alan on 2015/6/14.
 */
public class CheckInCardEvent extends BaseEvent{
    public enum EventType{
        CARD_BIND_SUCCESS,
        CARD_BIND_FAILED,
        CARD_REQUEST_SUCCESS,
        CARD_REQUEST_FAILED,
        CARD_UNBIND_SUCCESS,
        CARD_UNBIND_FAILED,
    }

    public EventType event;
    public List<CheckInCard> cards;

    public CheckInCardEvent(EventType event, String msg, List<CheckInCard> cards) {
        super(msg);
        this.event = event;
        this.msg = msg;
        this.cards = cards;
    }

    public EventType getEvent() {
        return event;
    }

    public List<CheckInCard> getCards() {
        return cards;
    }
}
