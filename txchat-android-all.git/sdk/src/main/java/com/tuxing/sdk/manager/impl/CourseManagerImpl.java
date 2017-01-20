package com.tuxing.sdk.manager.impl;

import android.content.Context;

import com.tuxing.rpc.proto.AddCourseCommentRequest;
import com.tuxing.rpc.proto.AddCourseCommentResponse;
import com.tuxing.rpc.proto.CourseLesson;
import com.tuxing.rpc.proto.EditCourseCommentRequest;
import com.tuxing.rpc.proto.FetchCourseCommentRequest;
import com.tuxing.rpc.proto.FetchCourseCommentResponse;
import com.tuxing.rpc.proto.FetchCourseLessonListRequest;
import com.tuxing.rpc.proto.FetchCourseLessonListResponse;
import com.tuxing.rpc.proto.FetchCourseLessonRequest;
import com.tuxing.rpc.proto.FetchCourseLessonResponse;
import com.tuxing.rpc.proto.FetchCourseListRequest;
import com.tuxing.rpc.proto.FetchCourseListResponse;
import com.tuxing.rpc.proto.FetchCourseRequest;
import com.tuxing.rpc.proto.FetchCourseResponse;
import com.tuxing.rpc.proto.FetchUserCourseCommentRequest;
import com.tuxing.rpc.proto.FetchUserCourseCommentResponse;
import com.tuxing.rpc.proto.PlayCourseLessonRequest;
import com.tuxing.rpc.proto.PlayCourseLessonResponse;
import com.tuxing.sdk.db.entity.CourseLessonBean;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.db.helper.UserDbHelper;
import com.tuxing.sdk.event.course.CourseCommentEvent;
import com.tuxing.sdk.event.course.CourseEvent;
import com.tuxing.sdk.event.course.CourseLessonEvent;
import com.tuxing.sdk.event.course.CourseListEvent;
import com.tuxing.sdk.http.HttpClient;
import com.tuxing.sdk.http.RequestCallback;
import com.tuxing.sdk.http.RequestUrl;
import com.tuxing.sdk.manager.CourseManager;
import com.tuxing.sdk.task.AsyncTaskProxyFactory;
import com.tuxing.sdk.utils.SerializeUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by mingwei on 4/7/16.
 */
public class CourseManagerImpl implements CourseManager {

    private final static Logger logger = LoggerFactory.getLogger(CourseManager.class);

    HttpClient httpClient = HttpClient.getInstance();

    private static CourseManager mIntence;

    public synchronized static CourseManager getInstance() {
        if (mIntence == null) {
            mIntence = new CourseManagerImpl();
            mIntence = AsyncTaskProxyFactory.getProxy(mIntence);
        }
        return mIntence;
    }

    private CourseManagerImpl() {

    }

    @Override
    public void init(Context context) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void getCourseList(int page) {

        logger.debug("CourseManagerImpl::getCourseList(),page={}", page);
        FetchCourseListRequest request = new FetchCourseListRequest.Builder().page(page).build();
        httpClient.sendRequest(RequestUrl.GET_COURSE_LIST, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchCourseListResponse response = SerializeUtils.parseFrom(data, FetchCourseListResponse.class);
                EventBus.getDefault().post(new CourseListEvent(CourseListEvent.EventType.GET_COURSELIST_SUCCESS, null,
                        response.courses, response.hasMore));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new CourseListEvent(CourseListEvent.EventType.GET_COURSELIST_FAILED,
                        cause.getMessage(), null, false));
            }
        });

    }

    @Override
    public void getCourse(long courseId) {
        logger.debug("CourseManagerImpl::getCourse(),courseId={}", courseId);
        final FetchCourseRequest request = new FetchCourseRequest.Builder().courseId(courseId).build();
        httpClient.sendRequest(RequestUrl.GET_COURSE, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchCourseResponse response = SerializeUtils.parseFrom(data, FetchCourseResponse.class);
                EventBus.getDefault().post(new CourseEvent(CourseEvent.EventType.GET_COURSE_SUCCESS, null, response.course));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new CourseEvent(CourseEvent.EventType.GET_COURSE_FAILED, cause.getMessage(), null));
            }
        });
    }

    @Override
    public void getCourseLession(long courseId) {
        logger.debug("CourseManagerImpl::getCourseLession(),courseId={}", courseId);
        FetchCourseLessonRequest request = new FetchCourseLessonRequest.Builder().courseId(courseId).build();
        httpClient.sendRequest(RequestUrl.GET_COURSELESSON, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchCourseLessonResponse response = SerializeUtils.parseFrom(data, FetchCourseLessonResponse.class);
                EventBus.getDefault().post(new CourseLessonEvent(CourseLessonEvent.EventType.GET_COURSELESSON_SUCCESS,
                        null, response.lessons));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new CourseLessonEvent(CourseLessonEvent.EventType.GET_COURSELESSON_FAILED,
                        cause.getMessage(), null));
            }
        });

    }

    @Override
    public void getLatestCourseLessonList(int page) {
        logger.debug("CourseManagerImpl::getLatestCourseLessonList(),page={}", page);
        FetchCourseLessonListRequest request = new FetchCourseLessonListRequest.Builder().page(page).build();
        httpClient.sendRequest(RequestUrl.GET_COURSELESSON_LIST, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {

                FetchCourseLessonListResponse response = SerializeUtils.parseFrom(data, FetchCourseLessonListResponse.class);
//                EventBus.getDefault().post(new CourseLessonEvent(CourseLessonEvent.EventType.GET_COURSELESSON_LIST_SUCCESS, null, response.lessons, response.hasMore));

                List<CourseLesson> lessons = response.lessons;
                List<CourseLessonBean> lesson = new ArrayList<CourseLessonBean>();
                for (int i=0;i<lessons.size();i++){
                    CourseLesson courseLesson = lessons.get(i);
                    CourseLessonBean bean = new CourseLessonBean();
                    bean.setId(courseLesson.id);
                    bean.setCreateOn(courseLesson.createOn);
                    bean.setUpdateOn(courseLesson.updateOn);
                    bean.setCourseId(courseLesson.courseId);
                    bean.setTitle(courseLesson.title);
                    bean.setStartOn(courseLesson.startOn);
                    bean.setEndOn(courseLesson.endOn);
                    bean.setVideoUrl(courseLesson.videoUrl);
                    bean.setHits(courseLesson.hits);
                    bean.setLiveHits(courseLesson.liveHits);
                    bean.setLiveStatus(courseLesson.liveStatus.getValue());

                    bean.setPic(courseLesson.pic);
                    bean.setDuration(courseLesson.duration);
                    bean.setResourceType(courseLesson.resourceType);
//                    xinjia
                    bean.setTeacherAvatar(courseLesson.course.teacherAvatar);
                    bean.setTeacherName(courseLesson.course.teacherName);
                    lesson.add(bean);
                }
                EventBus.getDefault().post(new CourseLessonEvent(CourseLessonEvent.EventType.GET_COURSELESSON_LIST_SUCCESS, null, lesson, response.hasMore,response.lessons));


                UserDbHelper.getInstance().updateCourseLesson(lesson);
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new CourseLessonEvent(CourseLessonEvent.EventType.GET_COURSELESSON_LIST_FAILED, null, null, false));
            }
        });

    }

    @Override
    public void getLatestCourseComment(long courseId) {
        logger.debug("CourseManagerImpl::getCourseComment(),courseId={}", courseId);
        FetchCourseCommentRequest request = new FetchCourseCommentRequest.Builder().courseId(courseId).maxId(Long.MAX_VALUE).build();
        httpClient.sendRequest(RequestUrl.GET_COURSECOMMENT_LIST, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchCourseCommentResponse response = SerializeUtils.parseFrom(data, FetchCourseCommentResponse.class);
                EventBus.getDefault().post(new CourseCommentEvent(CourseCommentEvent.EventType.GET_LATEST_COURSECOMMENT_SUCCESS, null, response.comments, response.hasMore));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new CourseCommentEvent(CourseCommentEvent.EventType.GET_LATEST_COURSECOMMENT_FAILED, cause.getMessage(), null, false));
            }
        });
    }

    @Override
    public void getHistoryCourseComment(long courseId, long maxId) {
        logger.debug("CourseManagerImpl::getCourseComment(),courseId={},maxId={}", courseId, maxId);
        FetchCourseCommentRequest request = new FetchCourseCommentRequest.Builder().courseId(courseId).maxId(maxId).build();
        httpClient.sendRequest(RequestUrl.GET_COURSECOMMENT_LIST, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchCourseCommentResponse response = SerializeUtils.parseFrom(data, FetchCourseCommentResponse.class);
                EventBus.getDefault().post(new CourseCommentEvent(CourseCommentEvent.EventType.GET_HISTORY_COURSECOMMENT_SUCCESS, null, response.comments, response.hasMore));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new CourseCommentEvent(CourseCommentEvent.EventType.GET_HISTORY_COURSECOMMENT_FAILED, cause.getMessage(), null, false));
            }
        });
    }

    @Override
    public void addCourseComment(long courseId, int score, String content) {
        logger.debug("CourseManagerImpl::addCourseComment(),courseId={},score={},content={}", courseId, score, content);
        final AddCourseCommentRequest request = new AddCourseCommentRequest.Builder().courseId(courseId).score(score).content(content).build();
        httpClient.sendRequest(RequestUrl.ADD_COURSECOMMENT, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                AddCourseCommentResponse response = SerializeUtils.parseFrom(data, AddCourseCommentResponse.class);
                EventBus.getDefault().post(new CourseCommentEvent(CourseCommentEvent.EventType.ADD_COURSECOMMENT_SUCCESS, null, null, false));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new CourseCommentEvent(CourseCommentEvent.EventType.ADD_COURSECOMMENT_FAILED, null, null, false));
            }
        });
    }

    @Override
    public void modifyCourseComment(long commentId, int score, String content) {
        logger.debug("CourseManagerImpl::modifyCourseComment(),commentId={},score={},content={}", commentId, score, content);
        EditCourseCommentRequest request = new EditCourseCommentRequest.Builder().commentId(commentId).score(score).content(content).build();
        httpClient.sendRequest(RequestUrl.MODIFY_COURSECOMMENT, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                EventBus.getDefault().post(new CourseCommentEvent(CourseCommentEvent.EventType.MODIFY_COURSECOMMENT_SUCCESS, null, null, false));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new CourseCommentEvent(CourseCommentEvent.EventType.MODIFY_COURSECOMMENT_FAILED, null, null, false));
            }
        });
    }

    @Override
    public void getCourseComment(long courseId) {
        logger.debug("CourseManagerImpl::modifyCourseComment(),commentId={}", courseId);
        FetchUserCourseCommentRequest request = new FetchUserCourseCommentRequest.Builder().courseId(courseId).build();
        httpClient.sendRequest(RequestUrl.GET_COURSECOMMENT, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchUserCourseCommentResponse response = SerializeUtils.parseFrom(data, FetchUserCourseCommentResponse.class);
                EventBus.getDefault().post(new CourseCommentEvent(CourseCommentEvent.EventType.GET_COURSECOMMENT_SUCCESS, null, response.content));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new CourseCommentEvent(CourseCommentEvent.EventType.GET_COURSECOMMENT_FAILED, null, null));
            }
        });
    }

    @Override
    public void playCourseLesson(long courselessonId) {
        logger.debug("CourseManagerImpl::playCourseLesson(),courselessonId={}", courselessonId);
        PlayCourseLessonRequest request = new PlayCourseLessonRequest.Builder().courseLessonId(courselessonId).build();
        httpClient.sendRequest(RequestUrl.PLAY_COURSE, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                PlayCourseLessonResponse response = SerializeUtils.parseFrom(data, PlayCourseLessonResponse.class);
            }

            @Override
            public void onFailure(Throwable cause) {

            }
        });
    }

}
