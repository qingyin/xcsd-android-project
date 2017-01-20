package com.tuxing.sdk.manager;

/**
 * Created by mingwei on 4/7/16.
 */
public interface CourseManager extends BaseManager {

    /**
     * 获取课程列表
     * 获取成功
     * CourseListEvent.EventType.GET_COURSELIST_SUCCESS
     * 获取失败
     * CourseListEvent.EventType.GET_COURSELIST_FAILED
     *
     * @param page 页数
     */
    public void getCourseList(int page);

    /**
     * 获取课程
     * 获取成功
     * CourseEvent.EventType.GET_COURSE_SUCCESS
     * 获取失败
     * CourseEvent.EventType.GET_COURSE_FAILED
     *
     * @param courseId
     */
    public void getCourse(long courseId);

    /**
     * 获取课程
     * 获取成功
     * CourseLessonEvent.EventType.GET_COURSELESSON_SUCCESS
     * 获取失败
     * CourseLessonEvent.EventType.GET_COURSELESSON_FAILED
     *
     * @param courseId 课程ID
     */
    public void getCourseLession(long courseId);

    /**
     * 获取课时列表
     * 获取成功
     * CourseLessonEvent.EventType.GET_COURSELESSON_LIST_SUCCESS
     * 获取失败
     * CourseLessonEvent.EventType.GET_COURSELESSON_LIST_FAILED
     * 获取失败
     *
     * @param page
     */
    public void getLatestCourseLessonList(int page);

    /**
     * 获取课程的最新评论
     * 获取成功
     * CourseCommentEvent.EventType.GET_LATEST_COURSECOMMENT_SUCCESS
     * 获取失败
     * CourseCommentEvent.EventType.GET_LATEST_COURSECOMMENT_FAILED
     *
     * @param courseId
     */

    public void getLatestCourseComment(long courseId);

    /**
     * 获取课程历史评论
     * 获取成功
     * CourseCommentEvent.EventType.GET_HISTORY_COURSECOMMENT_SUCCESS
     * 获取失败
     * CourseCommentEvent.EventType.GET_HISTORY_COURSECOMMENT_FAILED
     *
     * @param courseId 课程ID
     * @param maxId    最后一条评论的ID
     */
    public void getHistoryCourseComment(long courseId, long maxId);

    /**
     * 添加一条评论
     * 添加成功
     * CourseCommentEvent.EventType.ADD_COURSECOMMENT_SUCCESS
     * 添加失败
     * CourseCommentEvent.EventType.ADD_COURSECOMMENT_FAILED
     *
     * @param courseId 课程ID
     * @param score    评分
     * @param content  评论内容
     */
    public void addCourseComment(long courseId, int score, String content);

    /**
     * 修改一条评论
     * 修改成功
     * CourseCommentEvent.EventType.MODIFY_COURSECOMMENT_SUCCESS
     * 修改失败
     * CourseCommentEvent.EventType.MODIFY_COURSECOMMENT_FAILED
     *
     * @param commentId 课程ID
     * @param score     评分
     * @param content   评论内容
     */
    public void modifyCourseComment(long commentId, int score, String content);

    /**
     * 获取当前用户对该课时的评论
     * 获取成功
     * CourseCommentEvent.EventType.GET_COURSECOMMENT_SUCCESS
     * 获取失败
     * CourseCommentEvent.EventType.GET_COURSECOMMENT_FAILED
     *
     * @param courseId
     */
    public void getCourseComment(long courseId);

    /**
     * 播放统计
     *
     * @param courselessonId 课程ID
     */
    public void playCourseLesson(long courselessonId);

}
