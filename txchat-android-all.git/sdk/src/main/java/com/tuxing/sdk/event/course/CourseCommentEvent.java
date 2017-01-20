package com.tuxing.sdk.event.course;

import com.tuxing.rpc.proto.CourseComment;
import com.tuxing.sdk.event.BaseEvent;

import java.util.List;

/**
 * Created by mingwei on 4/7/16.
 */
public class CourseCommentEvent extends BaseEvent {
    //将CourseComment改为Comment
    public enum EventType {
        GET_LATEST_COURSECOMMENT_SUCCESS,
        GET_LATEST_COURSECOMMENT_FAILED,
        GET_HISTORY_COURSECOMMENT_SUCCESS,
        GET_HISTORY_COURSECOMMENT_FAILED,
        ADD_COURSECOMMENT_SUCCESS,
        ADD_COURSECOMMENT_FAILED,
        MODIFY_COURSECOMMENT_SUCCESS,
        MODIFY_COURSECOMMENT_FAILED,
        GET_COURSECOMMENT_SUCCESS,
        GET_COURSECOMMENT_FAILED
    }

    private EventType mEvent;

    private List<CourseComment> mCourseComments;

    private CourseComment mCourseComment;

    private boolean mHasMore;

    public CourseCommentEvent(EventType eventType, String msg, List<CourseComment> coursecomment, boolean hasMore) {
        super(msg);
        mEvent = eventType;
        mCourseComments = coursecomment;
        mHasMore = hasMore;
    }

    public CourseCommentEvent(EventType eventType, String msg, CourseComment coursecomment) {
        super(msg);
        mEvent = eventType;
        mCourseComment = coursecomment;
    }

    public EventType getEvent() {
        return mEvent;
    }

    public List<CourseComment> getCourseComments() {
        return mCourseComments;
    }

    public CourseComment getCourseComment() {
        return mCourseComment;
    }

    public boolean hasMore() {
        return mHasMore;
    }

}
