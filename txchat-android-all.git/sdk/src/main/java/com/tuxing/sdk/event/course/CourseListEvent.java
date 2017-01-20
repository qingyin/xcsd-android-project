package com.tuxing.sdk.event.course;

import com.tuxing.rpc.proto.Course;
import com.tuxing.sdk.event.BaseEvent;

import java.util.List;

/**
 * Created by mingwei on 4/7/16.
 */
public class CourseListEvent extends BaseEvent {
    //将Course改为Comment
    public enum EventType {
        GET_COURSELIST_SUCCESS,
        GET_COURSELIST_FAILED,
    }

    private EventType mEvent;

   private List<Course> mCourseList;

    private boolean mHasMore;

    public CourseListEvent(EventType eventType, String msg, List<Course> courseList, boolean hasmore) {
        super(msg);
        mEvent = eventType;
        mCourseList = courseList;
        mHasMore = hasmore;
    }

    public EventType getEvent() {
        return mEvent;
    }

    public List<Course> getCourseList() {
        return mCourseList;
    }

    public boolean hasMore() {
        return mHasMore;
    }
}
