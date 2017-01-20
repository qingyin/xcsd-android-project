package com.tuxing.sdk.manager;

import com.tuxing.rpc.proto.CommentType;
import com.tuxing.rpc.proto.TargetType;

/**
 * Created by alan on 16/1/8.
 */
public interface CommentManager extends BaseManager {
    /***
     * 获取最新的评论
     * 请求成功, 返回CommentEvent.GET_LATEST_XXX_COMMENTS_SUCCESS, 包括有Comment的列表
     * 请求失败, 返回CommentEvent.GET_LATEST_XXX_COMMENTS_FAILED, 包括有错误信息
     *
     * @param targetId 评论目标的ID
     * @param targetType 评论目标的类型
     */
    void getLatestComment(long targetId, TargetType targetType);

    /***
     * 获取历史评论
     * 请求成功, 返回CommentEvent.EventType.GET_HISTORY_XXX_COMMENTS_SUCCESS, 包括有Comment的列表
     * 请求失败, 返回CommentEvent.EventType.GET_HISTORY_XXX_COMMENTS_FAILED, 包括有错误信息
     *
     * @param targetId 资源ID
     * @param targetType 评论目标的类型
     * @param maxCommentId 上次返回的最大的评论ID
     */
    void getHistoryComment(long targetId, TargetType targetType, long maxCommentId);

    /***
     * 删除评论
     * 删除成功, 返回CommentEvent.EventType.DELETE_COMMENT_SUCCESS
     * 删除失败, 返回CommentEvent.EventType.DELETE_COMMENT_FAILED
     *
     * @param commentId 评论ID
     */
    void deleteComment(long commentId);

    /***
     * 发布一个评论
     * 发布成功，触发事件CommentEvent.EventType.REPLAY_XXX_SUCCESS
     * 发布失败，触发事件CommentEvent.EventType.REPLAY_XXX_FAILED
     * @param targetId  回答ID或是宝典文章ID
     * @param targetType 评论的是回答还是文章
     * @param content   评论的内容
     * @param replayToUserId  评论的回复人ID，可以不填写
     * @param replayToUserName 评论的回复人名，可以不填写
     * @param commentType 评论类型，1是点赞，2是文字
     */
    void publishComment(long targetId,  TargetType targetType, String content, Long replayToUserId,
                        String replayToUserName, CommentType commentType);
}
