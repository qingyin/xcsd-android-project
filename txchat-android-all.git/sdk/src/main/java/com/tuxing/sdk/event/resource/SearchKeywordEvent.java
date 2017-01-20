package com.tuxing.sdk.event.resource;

import com.tuxing.sdk.event.BaseEvent;

import java.util.List;

/**
 * Created by alan on 16/1/8.
 */
public class SearchKeywordEvent extends BaseEvent {
    public enum EventType{
        GET_SEARCH_KEYWORD_SUCCESS,
        GET_SEARCH_KEYWORD_FAILED
    }

    private EventType event;
    private List<String> hotKeywords;
    private List<String> recommendedKeywords;

    public SearchKeywordEvent(EventType event, String msg, List<String> hotKeywords, List<String> recommendedKeywords) {
        super(msg);
        this.event = event;
        this.hotKeywords = hotKeywords;
        this.recommendedKeywords = recommendedKeywords;
    }

    public EventType getEvent() {
        return event;
    }

    public void setEvent(EventType event) {
        this.event = event;
    }

    public List<String> getHotKeywords() {
        return hotKeywords;
    }

    public List<String> getRecommendedKeywords() {
        return recommendedKeywords;
    }
}
