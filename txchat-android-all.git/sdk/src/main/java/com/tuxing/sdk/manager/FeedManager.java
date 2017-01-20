package com.tuxing.sdk.manager;

import com.tuxing.sdk.db.entity.Feed;
import com.tuxing.sdk.modle.Attachment;
import com.tuxing.sdk.task.AsyncMethod;

import java.util.List;

/**
 * Created by Alan on 2015/6/30.
 */
public interface FeedManager extends BaseManager {

    /***
     * 请求最新的喂药记录，最多20条
     * 请求成功，返回FeedMedicineTaskEvent.EventType.FEED_MEDICINE_LATEST_SUCCESS，其中包括有FeedMedicineTask的List
     * 如果返回FeedMedicineTaskEvent中的hasMore是true，则表示下面还有数据，可以下拉请求数据
     * 请求失败，返回FeedMedicineTaskEvent.EventType.FEED_MEDICINE_LATEST_FAILED
     */
    void getLatestFeedMedicineTask();

    /***
     * 请求的喂药历史记录，最多20条
     * 请求成功，返回FeedMedicineTaskEvent.EventType.FEED_MEDICINE_HISTORY_SUCCESS，其中包括有FeedMedicineTask的List
     * 如果返回FeedMedicineTaskEvent中的hasMore是true，则表示下面还有数据，可以下拉请求数据
     * 请求失败，返回FeedMedicineTaskEvent.EventType.FEED_MEDICINE_HISTORY_FAILED
     *
     * @param maxTaskId 上次查询结果中最小的taskId
     */
    void getHistoryFeedMedicineTask(long maxTaskId);

    /***
     * 从本地缓存里获取喂药记录，最多20条
     * 返回FeedMedicineTaskEvent.EventType.FEED_MEDICINE_FROM_LOCAL，其中包括有FeedMedicineTask的List
     */
    @AsyncMethod
    void getFeedMedicineTaskFromLocal();

    /***
     * 发布一个喂药记录
     * 发布成功，返回FeedMedicineTaskEvent.EventType.ADD_FEED_MEDICINE_SUCCESS，其中包括有发布的FeedMedicineTask对象
     * 发布失败，返回FeedMedicineTaskEvent.EventType.ADD_FEED_MEDICINE_FAILED
     * @param beginDate    喂药开始日期，时间戳格式
     * @param description  喂药说明
     * @param attachments  附件
     */
    void addFeedMedicineTask(long beginDate, String description, List<Attachment> attachments);

    /***
     * 回复喂药任务
     * 回复成功，触发事件CommentEvent.EventType.REPLAY_FEED_MEDICINE_TASK_SUCCESS
     * 回复失败，触发事件CommentEvent.EventType.REPLAY_FEED_MEDICINE_TASK_FAILED
     * @param taskId   任务ID
     * @param content  回复内容
     */
    void replayFeedMedicineTask(long taskId, String content);

    /***
     * 标记喂药任务为已读
     *
     * @param taskId 任务ID
     */
    void markFeedMedicineTaskAsRead(long taskId);

    /***
     * 获取喂药任务的评论
     * 获取评论成功，触发事件CommentEvent.EventType.GET_FEED_MEDICINE_TASK_COMMENTS_SUCCESS
     * 获取评论失败，触发事件CommentEvent.EventType.GET_FEED_MEDICINE_TASK_COMMENTS_FAILED
     * @param taskId 任务ID
     */
    void getCommentsForFeedMedicineTask(long taskId);

    /***
     * 请求最新的幼儿园信箱信件，最多20条
     * 请求成功，返回GardenMailEvent.EventType.FETCH_LATEST_MAIL_SUCCESS
     * 如果返回返回GardenMailEvent中的hasMore是true，则表示下面还有数据，可以下拉请求数据
     * 请求失败，返回GardenMailEvent.EventType.FETCH_LATEST_MAIL_FAILED
     */
    void getLatestGardenMails();

    /***
     * 请求的幼儿园信箱信件历史记录，最多20条
     * 请求成功，返回GardenMailEvent.EventType.FETCH_HISTORY_MAIL_SUCCESS，其中包括有GardenMail的List
     * 如果返回返回GardenMailEvent中的hasMore是true，则表示下面还有数据，可以下拉请求数据
     * 请求失败，返回GardenMailEvent.EventType.FETCH_HISTORY_MAIL_FAILED
     *
     * @param maxMailId 上次查询结果中最小的mailId
     */
    void getHistoryGardenMails(long maxMailId);

    /***
     * 从本地缓存里获取幼儿园信箱信件，最多20条
     * 返回GardenMailEvent.EventType.FETCH_HISTORY_MAIL_FROM_LOCAL，其中包括有GardenMail的List
     */
    @AsyncMethod
    void getGardenMailsFromLocal();

    /***
     * 给幼儿园信箱发邮件
     * 发送成功，返回GardenMailEvent.EventType.SEND_PRINCIPAL_MAIL_SUCCESS
     * 发送失败，返回GardenMailEvent.EventType.SEND_PRINCIPAL_MAIL_FAILED
     * @param content     邮件内容
     * @param anonymous   是否匿名
     */
    void sendGardenMail(String content, boolean anonymous);

    /***
     * 回复园长信箱邮件
     * 回复成功，触发事件CommentEvent.EventType.REPLAY_GARDEN_MAIL_SUCCESS
     * 回复失败，触发事件CommentEvent.EventType.REPLAY_GARDEN_MAIL_FAILED
     * @param mailId   邮件ID
     * @param content  回复内容
     */
    void replayGardenMail(long mailId, String content);

    /***
     * 标记园长信箱邮件为已读
     *
     * @param mailId 邮件ID
     */
    void markGardenMailAsRead(long mailId);

    /***
     * 获取园长信箱的评论
     * 获取评论成功，触发事件CommentEvent.EventType.GET_GARDEN_MAIL_COMMENTS_SUCCESS
     * 获取评论失败，触发事件CommentEvent.EventType.GET_GARDEN_MAIL_COMMENTS_FAILED
     * @param mailId 信件ID
     */
    void getCommentsForGardenMail(long mailId);

    /***
     * 请求最新的亲子圈消息流，最多20条
     * 请求成功，触发事件FeedEvent.EventType.REQUEST_LATEST_FEED_FROM_SERVER_SUCCESS，其中包括有Feed对象的列表
     * 请求失败，触发事件FeedEvent.EventType.REQUEST_LATEST_FEED_FROM_SERVER_SUCCESS
     * @param departmentId 班级ID，如果班级ID是NULL，就是取所有的feed
     */
    void getLatestFeed(Long departmentId);

    /***
     * 请求亲子圈消息流的历史记录，最多20条
     * 请求成功，触发事件FeedEvent.EventType.REQUEST_HISTORY_FEED_FROM_SERVER_SUCCESS，其中包括有Feed对象的列表
     * 请求失败，触发事件FeedEvent.EventType.REQUEST_HISTORY_FEED_FROM_SERVER_FAILED
     * @param maxFeedId 上次查询结果中最小的feedId
     * @param departmentId 班级ID，如果班级ID是NULL，就是取所有的feed
     */
    void getHistoryFeed(long maxFeedId, Long departmentId);

    /***
     * 从本地缓存中请求亲子圈消息，最多20条
     * 请求成功，触发事件FeedEvent.EventType.GET_FEED_FROM_LOCAL_CACHE，其中包括有Feed对象的列表
     */
    @AsyncMethod
    void getFeedFromLocalCache();

    /***
     * 删除一条自己发的feed
     * 删除成功，触发事件FeedEvent.EventType.DELETE_FEED_SUCCESS
     * 删除失败，触发事件FeedEvent.EventType.DELETE_FEED_FAILED
     * @param feedId feedId
     */
    void deleteFeed(long feedId);

    /***
     * 从本地数据库中加载一个Feed
     * @param feedId feedID
     * @return Feed详情
     */
    Feed getFeedFromLocal(long feedId);

    /***
     * 发布一条亲子圈消息流
     * 发布成功，触发事件FeedEvent.EventType.SEND_FEED_SUCCESS
     * 发布失败，触发事件FeedEvent.EventType.SEND_FEED_FAILED
     * @param departments  亲子圈发布到的班级
     * @param content      内容
     * @param attachments  图片/视频
     * @param publishToClassAlbum 是否发布到班级相册中
     */
    void publishFeed(List<Long> departments, String content, List<Attachment> attachments, boolean publishToClassAlbum);

    /***
     * 删除一条自己发的评论
     * 删除成功，触发事件CommentEvent.EventType.DELETE_COMMENT_SUCCESS
     * 删除失败，触发事件CommentEvent.EventType.DELETE_COMMENT_FAILED
     * @param commentId 评论ID
     */
    void deleteFeedComment(long commentId);
    /***
     * 发布一个评论
     * 发布成功，触发事件CommentEvent.EventType.REPLAY_FEED_SUCCESS
     * 发布失败，触发事件CommentEvent.EventType.REPLAY_FEED_FAILED
     * @param feedId    评论的feedId
     * @param content   评论的内容
     * @param replayToUserId  评论的回复人ID，可以不填写
     * @param replayToUserName 评论的回复人名，可以不填写
     * @param commentType 评论类型，1是点赞，2是文字
     */
    void publishComment(long feedId, String content, Long replayToUserId, String replayToUserName, int commentType);


    /***
     * 获取最新的timeline，最多20条feed
     * 请求成功，触发事件FeedEvent.EventType.REQUEST_LATEST_TIME_LINE_FROM_SERVER_SUCCESS，其中包括有Feed对象的列表
     * 请求失败，触发事件FeedEvent.EventType.REQUEST_LATEST_TIME_LINE_FROM_SERVER_FAILED
     */
    void getLatestTimeLine();

    /***
     * 获取最近的timeline记录，最多20条feed
     * 请求成功，触发事件FeedEvent.EventType.REQUEST_HISTORY_TIME_LINE_FROM_SERVER_SUCCESS，其中包括有Feed对象的列表
     * 请求失败，触发事件FeedEvent.EventType.REQUEST_HISTORY_TIME_LINE_FROM_SERVER_SUCCESS
     *
     * @param maxFeedId 上次查询结果中最小的feedId
     */
    void getHistoryTimeLine(long maxFeedId);

    /***
     * 获取Feed的最近评论，最多20条
     * 获取评论成功，触发事件CommentEvent.EventType.GET_LATEST_FEED_COMMENTS_SUCCESS
     * 获取评论失败，触发事件CommentEvent.EventType.GET_LATEST_FEED_COMMENTS_FAILED
     * @param feedId feedId
     */
     void getLatestFeedComments(long feedId);

    /***
     * 获取Feed的历史评论，最多20条
     * 获取评论成功，触发事件CommentEvent.EventType.GET_HISTORY_FEED_COMMENTS_SUCCESS
     * 获取评论失败，触发事件CommentEvent.EventType.GET_HISTORY_FEED_COMMENTS_FAILED
     * @param feedId feedId
     * @param maxCommentId 上次返回的评论列表中最小的评论ID
     */
    void getHistoryFeedComments(long feedId, long maxCommentId);

    /***
     * 获取某个人最新的TimeLine，最多20条
     * 请求成功，触发事件FeedEvent.EventType.REQUEST_LATEST_USER_TIME_LINE_FROM_SERVER_SUCCESS，其中包括有Feed对象的列表
     * 请求失败，触发事件FeedEvent.EventType.REQUEST_LATEST_USER_TIME_LINE_FROM_SERVER_FAILED
     * @param userId 用户ID
     */
    void getLatestTimeLineByUser(long userId);

    /***
     * 获取某个人历史的TimeLine，最多20条
     * 请求成功，触发事件FeedEvent.EventType.REQUEST_HISTORY_USER_TIME_LINE_FROM_SERVER_SUCCESS，其中包括有Feed对象的列表
     * 请求失败，触发事件FeedEvent.EventType.REQUEST_HISTORY_USER_TIME_LINE_FROM_SERVER_FAILED
     *
     * @param userId 用户ID
     * @param maxFeedId 上次查询结果中最小的feedId
     */
    void getHistoryTimeLineByUser(long userId, long maxFeedId);

    /***
     * 获取对我参与的feed的最新评论，最多20条
     * 请求成功，触发事件CommentEvent.EventType.GET_LATEST_CONCERNED_COMMENT_SUCCESS
     * 请求失败，触发事件CommentEvent.EventType.GET_LATEST_CONCERNED_COMMENT_FAILED
     */
    void getLatestMyConcernedComment();

    /***
     * 获取对我参与的feed的历史评论，最多20条
     * 请求成功，触发事件CommentEvent.EventType.GET_HISTORY_CONCERNED_COMMENT_SUCCESS
     * 请求失败，触发事件CommentEvent.EventType.GET_HISTORY_CONCERNED_COMMENT_FAILED
     *
     * @param maxCommentId 上次查询结果中最小的评论ID
     */
    void getHistoryMyConcernedComment(long maxCommentId);


    /***
     * 获取最新的班级相册图片，最多20条
     * @param classId 班级的deptId
     * 请求成功，触发事件ClassPictureEvent.EventType.GET_LATEST_PICTURE_SUCCESS
     * 请求失败，触发事件ClassPictureEvent.EventType.GET_LATEST_PICTURE_FAILED
     *
     */
    void getLatestClassPictures(Long classId);

    /***
     * 获取最新的班级相册图片，最多20条
     * @param classId 班级的deptId
     * @param maxPictureId 上次查询结果中最小的图片Id
     * 请求成功，触发事件ClassPictureEvent.EventType.GET_HISTORY_PICTURE_SUCCESS
     * 请求失败，触发事件ClassPictureEvent.EventType.GET_HISTORY_PICTURE_FAILED
     *
     */
    void getHistoryClassPictures(Long classId, long maxPictureId);


    /***
     * 从本地缓存中请求班级相册消息，最多20条
     * 请求成功，触发事件ClassPictureEvent.EventType.GET_LOCAL_PICTURE
     */
    @AsyncMethod
    void getClassPicturesFromLocalCache(Long classId);

    /***
     * 本地删除一个活动，删除后的活动，在亲子圈上不再显示
     * @param activityId 活动的ID
     */
    @AsyncMethod
    void deleteActivity(Long activityId);
}
