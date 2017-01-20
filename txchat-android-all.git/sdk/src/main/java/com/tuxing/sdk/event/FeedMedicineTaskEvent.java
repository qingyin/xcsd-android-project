package com.tuxing.sdk.event;

import com.tuxing.sdk.db.entity.FeedMedicineTask;

import java.util.List;

/**
 * Created by Alan on 2015/6/30.
 */
public class FeedMedicineTaskEvent extends BaseEvent {
    public enum EventType{
        FEED_MEDICINE_LATEST_SUCCESS,
        FEED_MEDICINE_LATEST_FAILED,
        FEED_MEDICINE_HISTORY_SUCCESS,
        FEED_MEDICINE_HISTORY_FAILED,
        FEED_MEDICINE_FROM_LOCAL,
        ADD_FEED_MEDICINE_SUCCESS,
        ADD_FEED_MEDICINE_FAILED
    }

    private EventType event;
    private List<FeedMedicineTask> feedMedicineTasks;
    private boolean hasMore;

    public FeedMedicineTaskEvent(EventType event, String msg,
                                 List<FeedMedicineTask> feedMedicineTasks, boolean hasMore) {
        super(msg);
        this.event = event;
        this.msg = msg;
        this.feedMedicineTasks = feedMedicineTasks;
        this.hasMore = hasMore;
    }

    public EventType getEvent() {
        return event;
    }

    public List<FeedMedicineTask> getFeedMedicineTasks() {
        return feedMedicineTasks;
    }

    public boolean isHasMore() {
        return hasMore;
    }
}
