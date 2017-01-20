package com.tuxing.sdk.manager.impl;

import android.content.Context;
import com.tuxing.rpc.proto.*;
import com.tuxing.sdk.db.entity.Comment;
import com.tuxing.sdk.event.CommentEvent;
import com.tuxing.sdk.http.HttpClient;
import com.tuxing.sdk.http.RequestCallback;
import com.tuxing.sdk.http.RequestUrl;
import com.tuxing.sdk.manager.CommentManager;
import com.tuxing.sdk.manager.LoginManager;
import com.tuxing.sdk.task.AsyncTaskProxyFactory;
import com.tuxing.sdk.utils.PbMsgUtils;
import com.tuxing.sdk.utils.SerializeUtils;
import de.greenrobot.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alan on 16/1/10.
 */
public class CommentManagerImpl implements CommentManager {
    private final static Logger logger = LoggerFactory.getLogger(CommentManager.class);
    private static CommentManager instance = null;

    HttpClient httpClient = HttpClient.getInstance();
    LoginManager loginManager = LoginManagerImpl.getInstance();

    private Context context;

    private CommentManagerImpl(){

    }

    public synchronized static CommentManager getInstance(){
        if(instance == null){
            instance = new CommentManagerImpl();
            instance = AsyncTaskProxyFactory.getProxy(instance);
        }

        return instance;
    }

    @Override
    public void init(Context context) {
        this.context = context;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void getLatestComment(long targetId, final TargetType targetType) {
        logger.debug("CommentManager::getLatestAnswerComment(), targetId={}, targetType={}", targetId, targetType);

        ShowCommentRequest request = new ShowCommentRequest.Builder()
                .targetType(targetType)
                .targetId(targetId)
                .maxId(Long.MAX_VALUE)
                .sinceId(0L)
                .build();

        httpClient.sendRequest(RequestUrl.SHOW_COMMENT, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                ShowCommentResponse response = SerializeUtils.parseFrom(data, ShowCommentResponse.class);

                List<Comment> commentList = new ArrayList<>();
                for(com.tuxing.rpc.proto.Comment txComment : response.comment){
                    com.tuxing.sdk.db.entity.Comment comment = PbMsgUtils.transObj(txComment);
                    if(txComment.commentType == CommentType.REPLY) {
                        commentList.add(comment);
                    }
                }

                CommentEvent.EventType event = CommentEvent.EventType.valueOf("GET_LATEST_" +
                        targetType.name() + "_COMMENTS_SUCCESS");

                EventBus.getDefault().post(new CommentEvent(
                        event,
                        null,
                        commentList,
                        response.hasMore
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                CommentEvent.EventType event = CommentEvent.EventType.valueOf("GET_LATEST_" +
                        targetType.name() + "_COMMENTS_FAILED");

                EventBus.getDefault().post(new CommentEvent(
                        event,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getHistoryComment(long targetId, final TargetType targetType, long maxCommentId) {
        logger.debug("CommentManager::getLatestAnswerComment(), targetId={}, targetType={}, maxCommentId={}",
                targetId, targetType);

        ShowCommentRequest request = new ShowCommentRequest.Builder()
                .targetType(targetType)
                .targetId(targetId)
                .maxId(maxCommentId)
                .sinceId(0L)
                .build();

        httpClient.sendRequest(RequestUrl.SHOW_COMMENT, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                ShowCommentResponse response = SerializeUtils.parseFrom(data, ShowCommentResponse.class);

                List<Comment> commentList = new ArrayList<>();
                for(com.tuxing.rpc.proto.Comment txComment : response.comment){
                    com.tuxing.sdk.db.entity.Comment comment = PbMsgUtils.transObj(txComment);
                    if(txComment.commentType == CommentType.REPLY) {
                        commentList.add(comment);
                    }
                }

                CommentEvent.EventType event = CommentEvent.EventType.valueOf("GET_HISTORY_" +
                        targetType.name() + "_COMMENTS_SUCCESS");

                EventBus.getDefault().post(new CommentEvent(
                        event,
                        null,
                        commentList,
                        response.hasMore
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                CommentEvent.EventType event = CommentEvent.EventType.valueOf("GET_HISTORY_" +
                        targetType.name() + "_COMMENTS_FAILED");

                EventBus.getDefault().post(new CommentEvent(
                        event,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void deleteComment(long commentId) {
        logger.debug("CommentManager::deleteComment(), commentId={}", commentId);

        DeleteCommnetsRequest request = new DeleteCommnetsRequest.Builder()
                .commentId(commentId)
                .build();

        httpClient.sendRequest(RequestUrl.DELETE_COMMENT, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {

                EventBus.getDefault().post(new CommentEvent(
                        CommentEvent.EventType.DELETE_COMMENT_SUCCESS,
                        null,
                        null,
                        false
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new CommentEvent(
                        CommentEvent.EventType.DELETE_COMMENT_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void publishComment(final long targetId, final TargetType targetType, final String content,
                               final Long replayToUserId, final String replayToUserName,
                               final CommentType commentType) {
        logger.debug("FeedManager::publishComment(), answerId={}, content={}, replayToUserId={}, " +
                        "replayToUserName={}, commentType={}, targetType={}", targetId, content, replayToUserId,
                replayToUserName, commentType, targetType);

        SendCommentRequest.Builder requestBuilder = new SendCommentRequest.Builder();

        requestBuilder.targetType(targetType);
        requestBuilder.targetId(targetId);
        requestBuilder.commentType(commentType);
        if(content != null) {
            requestBuilder.content(content);
        }else{
            requestBuilder.content("");
        }

        if(replayToUserId != null) {
            requestBuilder.toUserId(replayToUserId);
        }

        SendCommentRequest request = requestBuilder.build();

        httpClient.sendRequest(RequestUrl.SEND_COMMENT, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                SendCommentResponse response = SerializeUtils.parseFrom(data, SendCommentResponse.class);

                CommentEvent.EventType event = CommentEvent.EventType.valueOf("REPLAY_" +
                        targetType.name() + "_SUCCESS");

                Comment comment = new Comment();
                comment.setCommentId(response.commentId);
                comment.setTopicId(targetId);
                comment.setTargetType(commentType.getValue());
                comment.setSenderName(loginManager.getCurrentUser().getNickname());
                comment.setSenderId(loginManager.getCurrentUser().getUserId());
                comment.setSenderAvatar(loginManager.getCurrentUser().getAvatar());
                comment.setCommentType(commentType.getValue());
                comment.setReplayToUserId(replayToUserId);
                comment.setReplayToUserName(replayToUserName);
                comment.setContent(content);
                comment.setUserType(UserType.TEACHER);
                comment.setSendTime(System.currentTimeMillis());

                EventBus.getDefault().post(new CommentEvent(
                        event,
                        null,
                        Arrays.asList(comment),
                        false
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                CommentEvent.EventType event = CommentEvent.EventType.valueOf("REPLAY_" +
                        targetType.name() + "_FAILED");

                EventBus.getDefault().post(new CommentEvent(
                        event,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }
}
