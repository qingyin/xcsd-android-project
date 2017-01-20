package com.tuxing.sdk.event.course;

import com.tuxing.rpc.proto.CourseLesson;
import com.tuxing.sdk.db.entity.CourseLessonBean;
import com.tuxing.sdk.event.BaseEvent;

import java.util.List;

/**
 * Created by mingwei on 4/7/16.
 */
public class CourseLessonEvent extends BaseEvent {
    //将CourseLesson改为Comment
    public enum EventType {
        GET_COURSELESSON_SUCCESS,
        GET_COURSELESSON_FAILED,
        GET_COURSELESSON_LIST_SUCCESS,
        GET_COURSELESSON_LIST_FAILED,
    }

    private EventType mEvent;

    private List<CourseLesson> mCourseLesson;
    private List<CourseLessonBean> mCourseLessonBean;

    private boolean hasMore;


    public List<CourseLessonBean> getmCourseLessonBean() {
        return mCourseLessonBean;
    }


    public CourseLessonEvent(EventType eventType, String msg, List<CourseLesson> courseList) {
        super(msg);
        mEvent = eventType;
        mCourseLesson = courseList;
    }

    public CourseLessonEvent(EventType eventType, String msg, List<CourseLesson> courseList, boolean hasmore) {
        super(msg);
        mEvent = eventType;
        mCourseLesson = courseList;
        hasMore = hasmore;
    }

    public CourseLessonEvent(EventType eventType, String msg, List<CourseLessonBean> CourseLessonBean, boolean hasmore,List<CourseLesson> courseList) {
        super(msg);
        mEvent = eventType;
        mCourseLessonBean = CourseLessonBean;
        hasMore = hasmore;
        mCourseLesson = courseList;
    }

    public EventType getEvent() {
        return mEvent;
    }

    public List<CourseLesson> getCourseLesson() {
        return mCourseLesson;
    }

    public boolean hasMore() {
        return hasMore;
    }


}
