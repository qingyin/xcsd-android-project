package com.tuxing.sdk.event.course;

import com.tuxing.rpc.proto.Course;
import com.tuxing.sdk.event.BaseEvent;


/**
 * Created by mingwei on 4/7/16.
 */
public class CourseEvent extends BaseEvent {
    //将Course改为Comment
    public enum EventType {
        GET_COURSE_SUCCESS,
        GET_COURSE_FAILED,
    }

    private EventType mEvent;

    private Course mCourse;

    public CourseEvent(EventType eventType, String msg, Course course) {
        super(msg);
        mEvent = eventType;
        mCourse = course;
    }

    public EventType getEvent() {
        return mEvent;
    }

    public Course getCourse() {
        return mCourse;
    }
}
