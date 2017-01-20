package com.tuxing.sdk.manager.impl;


import android.content.Context;

import com.tuxing.rpc.proto.*;
import com.tuxing.sdk.db.entity.*;
import com.tuxing.sdk.db.entity.Comment;
//import com.tuxing.sdk.event.CommentEvent;
import com.tuxing.sdk.event.quora.*;
import com.tuxing.sdk.http.HttpClient;
import com.tuxing.sdk.http.RequestCallback;
import com.tuxing.sdk.http.RequestUrl;
import com.tuxing.sdk.manager.FeedManager;
import com.tuxing.sdk.manager.LoginManager;
import com.tuxing.sdk.manager.QuoraManager;
import com.tuxing.sdk.modle.*;
import com.tuxing.sdk.modle.CommunionMessage;
import com.tuxing.sdk.modle.Expert;
import com.tuxing.sdk.modle.Question;
import com.tuxing.sdk.modle.Knowledge;
import com.tuxing.sdk.task.AsyncTaskProxyFactory;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.Constants;
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
 * Created by apple on 16/4/20.
 */
public class QuoraManagerImpl implements QuoraManager {
    private final static Logger logger = LoggerFactory.getLogger(QuoraManager.class);
    private static QuoraManager instance;

    HttpClient httpClient = HttpClient.getInstance();
    LoginManager loginManager = LoginManagerImpl.getInstance();

    private Context context;

    private QuoraManagerImpl() {

    }

    public synchronized static QuoraManager getInstance() {
        if (instance == null) {
            instance = new QuoraManagerImpl();
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
    public void getTopHotQuestions(int page) {
        logger.debug("QuaraManager::getTopHotQuestions(), page = {}", page);
        FetchHotQuestionsRequest request = new FetchHotQuestionsRequest(page);

        httpClient.sendRequest(RequestUrl.FETCH_TOP_HOT_QUESTION, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchHotQuestionsResponse response = SerializeUtils.parseFrom(data, FetchHotQuestionsResponse.class);

                List<Question> questions = new ArrayList<>();
                for (com.tuxing.rpc.proto.Question question : response.questions) {
                    questions.add(PbMsgUtils.transObj(question));
                }

                EventBus.getDefault().post(new QuestionEvent(
                        QuestionEvent.EventType.GET_TOP_HOT_QUESTION_SUCCESS,
                        null,
                        questions,
                        response.hasMore
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new QuestionEvent(
                        QuestionEvent.EventType.GET_TOP_HOT_QUESTION_FAILED,
                        cause.getMessage(),
                        null,
                        true
                ));
            }
        });
    }

    @Override
    public void getLatestQuestions(Long userId) {
        logger.debug("QuaraManager::getLatestQuestions(), userId = {}", userId);
        FetchQuestionsRequest request = new FetchQuestionsRequest.Builder()
                .authorId(userId)
                .maxId(Long.MAX_VALUE)
                .sinceId(0L)
                .build();

        httpClient.sendRequest(RequestUrl.FETCH_QUESTION, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchQuestionsResponse response = SerializeUtils.parseFrom(data, FetchQuestionsResponse.class);

                List<Question> questions = new ArrayList<>();
                for (com.tuxing.rpc.proto.Question question : response.questions) {
                    questions.add(PbMsgUtils.transObj(question));
                }

                EventBus.getDefault().post(new QuestionEvent(
                        QuestionEvent.EventType.GET_LATEST_QUESTION_SUCCESS,
                        null,
                        questions,
                        response.hasMore
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new QuestionEvent(
                        QuestionEvent.EventType.GET_LATEST_QUESTION_FAILED,
                        cause.getMessage(),
                        null,
                        true
                ));
            }
        });
    }

    @Override
    public void getHistoryQuestions(Long userId, long maxQuestionId) {
        logger.debug("QuaraManager::getHistoryQuestions(), userId = {}, maxQuestionId = {}", userId, maxQuestionId);
        FetchQuestionsRequest request = new FetchQuestionsRequest.Builder()
                .authorId(userId)
                .maxId(maxQuestionId)
                .sinceId(0L)
                .build();

        httpClient.sendRequest(RequestUrl.FETCH_QUESTION, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchQuestionsResponse response = SerializeUtils.parseFrom(data, FetchQuestionsResponse.class);

                List<Question> questions = new ArrayList<>();
                for (com.tuxing.rpc.proto.Question question : response.questions) {
                    questions.add(PbMsgUtils.transObj(question));
                }

                EventBus.getDefault().post(new QuestionEvent(
                        QuestionEvent.EventType.GET_HISTORY_QUESTION_SUCCESS,
                        null,
                        questions,
                        response.hasMore
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new QuestionEvent(
                        QuestionEvent.EventType.GET_HISTORY_QUESTION_FAILED,
                        cause.getMessage(),
                        null,
                        true
                ));
            }
        });
    }

    @Override
    public void getQuestionById(long questionId) {
        FetchQuestionDetailRequest request = new FetchQuestionDetailRequest(questionId);

        httpClient.sendRequest(RequestUrl.GET_QUESTION_DETAIL, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchQuestionDetailResponse response = SerializeUtils.parseFrom(data, FetchQuestionDetailResponse.class);

                Question question = PbMsgUtils.transObj(response.question);

                EventBus.getDefault().post(new QuestionEvent(
                        QuestionEvent.EventType.GET_QUESTION_BY_ID_SUCCESS,
                        null,
                        CollectionUtils.asList(question),
                        false
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new QuestionEvent(
                        QuestionEvent.EventType.GET_QUESTION_BY_ID_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getQuestionTags(TagType tagType) {
        logger.debug("QuaraManager::getQuestionTags()");

        FetchTagsRequest request = new FetchTagsRequest(tagType);

        httpClient.sendRequest(RequestUrl.GET_QUESTION_TAGS, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchTagsResponse response = SerializeUtils.parseFrom(data, FetchTagsResponse.class);

                List<QuestionTag> tags = new ArrayList<>();
                for (Tag tag : response.tags) {
                    tags.add(PbMsgUtils.transObj(tag));
                }

                EventBus.getDefault().post(new QuestionTagEvent(
                        QuestionTagEvent.EventType.FETCH_TAGS_SUCCESS,
                        null,
                        tags
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new QuestionTagEvent(
                        QuestionTagEvent.EventType.FETCH_TAGS_FAILED,
                        cause.getMessage(),
                        null
                ));
            }
        });
    }

    @Override
    public void askQuestion(Long expertId, Long tagId, String title, final String content, List<Attachment> attachments) {
        logger.debug("QuaraManager::askQuestion(), expertId = {}, tagId = {}, title = {}, content = {}",
                expertId, tagId, title, content);

        List<Attach> attaches = new ArrayList<>();

        if (!CollectionUtils.isEmpty(attachments)) {
            for (Attachment attachment : attachments) {
                Attach attach = new Attach(attachment.getFileUrl(),
                        PbMsgUtils.transAttachType(attachment.getType()));
                attaches.add(attach);
            }
        }

        AskQuestionRequest request = new AskQuestionRequest.Builder()
                .expertId(expertId)
                .title(title)
                .content(content)
                .tagId(tagId)
                .attaches(attaches)
                .build();

        httpClient.sendRequest(RequestUrl.ASK_QUESTION, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                EventBus.getDefault().post(new QuestionEvent(
                        QuestionEvent.EventType.ASK_QUESTION_SUCCESS,
                        null,
                        null,
                        false
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new QuestionEvent(
                        QuestionEvent.EventType.ASK_QUESTION_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void answerQuestion(long questionId, String content, List<Attachment> attachments) {
        logger.debug("QuaraManager::askQuestion(), questionId = {}, content = {}", questionId, content);

        List<Attach> attaches = new ArrayList<>();

        if (!CollectionUtils.isEmpty(attachments)) {
            for (Attachment attachment : attachments) {
                Attach attach = new Attach(attachment.getFileUrl(),
                        PbMsgUtils.transAttachType(attachment.getType()));
                attaches.add(attach);
            }
        }

        AnswerQuestionRequest request = new AnswerQuestionRequest.Builder()
                .questionId(questionId)
                .content(content)
                .attaches(attaches)
                .build();

        httpClient.sendRequest(RequestUrl.ANSWER_QUESTION, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                EventBus.getDefault().post(new AnswerEvent(
                        AnswerEvent.EventType.ANSWER_QUESTION_SUCCESS,
                        null,
                        null,
                        false
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new AnswerEvent(
                        AnswerEvent.EventType.ANSWER_QUESTION_FAILED,
                        null,
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getLatestAnswers(Long questionId, Long userId, UserType userType) {
        logger.debug("QuaraManager::getLatestAnswers(), questionId = {}, userId = {}, userType = {}",
                questionId, userId, userType);

        FetchQuestionAnswersRequest request = new FetchQuestionAnswersRequest.Builder()
                .questionId(questionId)
                .maxId(Long.MAX_VALUE)
                .authorId(userId)
                .userType(userId == null ? null : userType)
                .sinceId(0L)
                .build();

        httpClient.sendRequest(RequestUrl.GET_ANSWERS, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchQuestionAnswersResponse response = SerializeUtils.parseFrom(data, FetchQuestionAnswersResponse.class);

                List<Answer> expertAnswers = new ArrayList<>();
                List<Answer> answers = new ArrayList<>();

                for (QuestionAnswer answer : response.expertAnswers) {
                    expertAnswers.add(PbMsgUtils.transObj(answer));
                }

                for (QuestionAnswer answer : response.answers) {
                    answers.add(PbMsgUtils.transObj(answer));
                }

                EventBus.getDefault().post(new AnswerEvent(
                        AnswerEvent.EventType.GET_LATEST_ANSWER_SUCCESS,
                        null,
                        expertAnswers,
                        answers,
                        response.hasMore
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new AnswerEvent(
                        AnswerEvent.EventType.GET_LATEST_ANSWER_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getHistoryAnswers(Long questionId, Long userId, UserType userType, long maxAnswerId) {
        logger.debug("QuaraManager::getHistoryAnswers(), questionId = {}, userId = {}, userType = {}, maxAnswerId={}",
                questionId, userId, userType, maxAnswerId);
        FetchQuestionAnswersRequest request = new FetchQuestionAnswersRequest.Builder()
                .questionId(questionId)
                .authorId(userId)
                .userType(userId == null ? null : userType)
                .maxId(maxAnswerId)
                .sinceId(0L)
                .build();

        httpClient.sendRequest(RequestUrl.GET_ANSWERS, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchQuestionAnswersResponse response = SerializeUtils.parseFrom(data, FetchQuestionAnswersResponse.class);

                List<Answer> answers = new ArrayList<>();
                for (QuestionAnswer answer : response.answers) {
                    answers.add(PbMsgUtils.transObj(answer));
                }

                EventBus.getDefault().post(new AnswerEvent(
                        AnswerEvent.EventType.GET_HISTORY_ANSWER_SUCCESS,
                        null,
                        answers,
                        response.hasMore
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new AnswerEvent(
                        AnswerEvent.EventType.GET_HISTORY_ANSWER_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void deleteAnswer(long answerId) {
        logger.debug("QuaraManager::deleteAnswer(), answerId = {}");

        DeleteQuestionAnswerRequest request = new DeleteQuestionAnswerRequest(answerId);

        httpClient.sendRequest(RequestUrl.DELETE_ANSWERS, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                EventBus.getDefault().post(new AnswerEvent(
                        AnswerEvent.EventType.DELETE_ANSWER_SUCCESS,
                        null,
                        null,
                        false
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new AnswerEvent(
                        AnswerEvent.EventType.DELETE_ANSWER_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    /*@Override
    public void getLatestComment(final long targetId, final TargetType targetType) {
        logger.debug("QuoraManager::getLatestAnswerComment(), targetId={}, targetType={}", targetId, targetType);

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

                List<com.tuxing.sdk.db.entity.Comment> commentList = new ArrayList<>();
                for(com.tuxing.rpc.proto.Comment txComment : response.comment){
                    com.tuxing.sdk.db.entity.Comment comment = PbMsgUtils.transObj(txComment);
                    if(txComment.commentType == CommentType.REPLY) {
                        commentList.add(comment);
                    }
                }

                CommentEvent.EventType event = CommentEvent.EventType.GET_LATEST_ANSWER_COMMENTS_SUCCESS;

                if(targetType == TargetType.KNOWLEDGE) {
                    event = CommentEvent.EventType.GET_LATEST_KNOWLEDGE_COMMENTS_SUCCESS;
                }

                EventBus.getDefault().post(new CommentEvent(
                        event,
                        null,
                        commentList,
                        response.hasMore
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                CommentEvent.EventType event = CommentEvent.EventType.GET_LATEST_ANSWER_COMMENTS_FAILED;

                if(targetType == TargetType.KNOWLEDGE) {
                    event = CommentEvent.EventType.GET_LATEST_KNOWLEDGE_COMMENTS_FAILED;
                }
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
        logger.debug("QuoraManager::getHistoryComment(), targetId = {}, targetType={}, maxCommentId = {}",
                targetId, targetType, maxCommentId);

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

                List<com.tuxing.sdk.db.entity.Comment> commentList = new ArrayList<>();
                for(com.tuxing.rpc.proto.Comment txComment : response.comment){
                    com.tuxing.sdk.db.entity.Comment comment = PbMsgUtils.transObj(txComment);
                    if(txComment.commentType == CommentType.REPLY) {
                        commentList.add(comment);
                    }
                }

                CommentEvent.EventType event = CommentEvent.EventType.GET_HISTORY_ANSWER_COMMENTS_SUCCESS;

                if(targetType == TargetType.KNOWLEDGE) {
                    event = CommentEvent.EventType.GET_HISTORY_KNOWLEDGE_COMMENTS_SUCCESS;
                }

                EventBus.getDefault().post(new CommentEvent(
                        event,
                        null,
                        commentList,
                        response.hasMore
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                CommentEvent.EventType event = CommentEvent.EventType.GET_HISTORY_ANSWER_COMMENTS_FAILED;

                if(targetType == TargetType.KNOWLEDGE) {
                    event = CommentEvent.EventType.GET_HISTORY_KNOWLEDGE_COMMENTS_FAILED;
                }
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
        logger.debug("QuoraManager::deleteComment(), answerCommentId={}", commentId);

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
    public void publishComment(final long targetId, final String content, final Long replayToUserId,
                               final String replayToUserName, final CommentType commentType,
                               final TargetType targetType) {
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

                CommentEvent.EventType event = CommentEvent.EventType.REPLAY_ANSWER_SUCCESS;

                if(targetType == TargetType.KNOWLEDGE) {
                    event = CommentEvent.EventType.REPLAY_KNOWLEDGE_SUCCESS;
                }

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
                CommentEvent.EventType event = CommentEvent.EventType.REPLAY_ANSWER_FAILED;

                if(targetType == TargetType.KNOWLEDGE) {
                    event = CommentEvent.EventType.REPLAY_KNOWLEDGE_FAILED;
                }
                EventBus.getDefault().post(new CommentEvent(
                        event,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }*/

    @Override
    public void getTopHotKnowledge(int page) {
        logger.debug("QuaraManager::getTopHotKnowledge(), page = {}", page);
        FetchHotKnowledgesRequest request = new FetchHotKnowledgesRequest(page);

        httpClient.sendRequest(RequestUrl.GET_TOP_HOT_KNOWLEDGE, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchHotKnowlegdesResponse response = SerializeUtils.parseFrom(data, FetchHotKnowlegdesResponse.class);

                List<Knowledge> knowledgeList = new ArrayList<>();
                for (com.tuxing.rpc.proto.Knowledge knowledge : response.knowledges) {
                    knowledgeList.add(PbMsgUtils.transObj(knowledge));
                }

                EventBus.getDefault().post(new KnowledgeEvent(
                        KnowledgeEvent.EventType.GET_TOP_HOT_KNOWLEDGE_SUCCESS,
                        null,
                        knowledgeList,
                        response.hasMore
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new KnowledgeEvent(
                        KnowledgeEvent.EventType.GET_TOP_HOT_KNOWLEDGE_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getLatestKnowledge(Long tagId, Long userId) {
        logger.debug("QuaraManager::getLatestKnowledge(), tagId = {}, userId = {}",
                tagId, userId);

        FetchKnowledgesRequest request = new FetchKnowledgesRequest.Builder()
                .authorId(userId)
                .tagId(tagId)
                .maxId(Long.MAX_VALUE)
                .sinceId(0L)
                .build();

        httpClient.sendRequest(RequestUrl.GET_KNOWLEDGE, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchHotKnowlegdesResponse response = SerializeUtils.parseFrom(data, FetchHotKnowlegdesResponse.class);

                List<Knowledge> knowledgeList = new ArrayList<>();
                for (com.tuxing.rpc.proto.Knowledge knowledge : response.knowledges) {
                    knowledgeList.add(PbMsgUtils.transObj(knowledge));
                }

                EventBus.getDefault().post(new KnowledgeEvent(
                        KnowledgeEvent.EventType.GET_LATEST_KNOWLEDGE_SUCCESS,
                        null,
                        knowledgeList,
                        response.hasMore
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new KnowledgeEvent(
                        KnowledgeEvent.EventType.GET_LATEST_KNOWLEDGE_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getHistoryKnowledge(Long tagId, Long userId, long maxKnowledgeId) {
        logger.debug("QuaraManager::getHistoryKnowledge(), tagId = {}, userId = {}, maxKnowledgeId = {}",
                tagId, userId, maxKnowledgeId);

        FetchKnowledgesRequest request = new FetchKnowledgesRequest.Builder()
                .authorId(userId)
                .tagId(tagId)
                .maxId(maxKnowledgeId)
                .sinceId(0L)
                .build();

        httpClient.sendRequest(RequestUrl.GET_KNOWLEDGE, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchHotKnowlegdesResponse response = SerializeUtils.parseFrom(data, FetchHotKnowlegdesResponse.class);

                List<Knowledge> knowledgeList = new ArrayList<>();
                for (com.tuxing.rpc.proto.Knowledge knowledge : response.knowledges) {
                    knowledgeList.add(PbMsgUtils.transObj(knowledge));
                }

                EventBus.getDefault().post(new KnowledgeEvent(
                        KnowledgeEvent.EventType.GET_HISTORY_KNOWLEDGE_SUCCESS,
                        null,
                        knowledgeList,
                        response.hasMore
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new KnowledgeEvent(
                        KnowledgeEvent.EventType.GET_HISTORY_KNOWLEDGE_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getKnowledgeById(long knowledgeId) {
        logger.debug("QuaraManager::getKnowledgeById(), knowledgeId = {}", knowledgeId);

        final FetchKnowledgeDetailRequest request = new FetchKnowledgeDetailRequest(knowledgeId);

        httpClient.sendRequest(RequestUrl.GET_KNOWLEDGE_DETAIL, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchKnowledgeDetailResponse response = SerializeUtils.parseFrom(data, FetchKnowledgeDetailResponse.class);

                Knowledge knowledge = PbMsgUtils.transObj(response.knowledge);

                EventBus.getDefault().post(new KnowledgeEvent(
                        KnowledgeEvent.EventType.GET_KNOWLEDGE_BY_ID_SUCCESS,
                        null,
                        Arrays.asList(knowledge),
                        false
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new KnowledgeEvent(
                        KnowledgeEvent.EventType.GET_KNOWLEDGE_BY_ID_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getTopHotExperts() {
        logger.debug("QuaraManager::getTopHotExperts()");

        FetchRecommendExpertsRequest request = new FetchRecommendExpertsRequest();

        httpClient.sendRequest(RequestUrl.GET_TOP_HOT_EXPORTS, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchRecommendExpertsResponse response = SerializeUtils.parseFrom(data, FetchRecommendExpertsResponse.class);

                List<Expert> expertList = new ArrayList<>();
                for (com.tuxing.rpc.proto.Expert expert : response.experts) {
                    expertList.add(PbMsgUtils.transObj(expert));
                }

                EventBus.getDefault().post(new ExpertEvent(
                        ExpertEvent.EventType.GET_TOP_HOT_EXPERT_SUCCESS,
                        null,
                        expertList,
                        false
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new ExpertEvent(
                        ExpertEvent.EventType.GET_TOP_HOT_EXPERT_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getExperts(int page) {
        logger.debug("QuaraManager::getExperts(), page = {}", page);

        FetchExpertsRequest request = new FetchExpertsRequest(page);

        httpClient.sendRequest(RequestUrl.GET_EXPORTS, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchExpertsResponse response = SerializeUtils.parseFrom(data, FetchExpertsResponse.class);

                List<Expert> expertList = new ArrayList<>();
                for (com.tuxing.rpc.proto.Expert expert : response.experts) {
                    expertList.add(PbMsgUtils.transObj(expert));
                }

                EventBus.getDefault().post(new ExpertEvent(
                        ExpertEvent.EventType.GET_EXPERT_SUCCESS,
                        null,
                        expertList,
                        response.hasMore
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new ExpertEvent(
                        ExpertEvent.EventType.GET_EXPERT_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });

    }

    @Override
    public void getExpertDetail(long expertId) {
        logger.debug("QuaraManager::getExpertDetail(), expertId = {}", expertId);

        final FetchExpertRequest request = new FetchExpertRequest(expertId);

        httpClient.sendRequest(RequestUrl.GET_EXPORT_DETAIL, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchExpertResponse response = SerializeUtils.parseFrom(data, FetchExpertResponse.class);

                Expert expert = PbMsgUtils.transObj(response.expert);

                expert.setHasMoreAnswer(response.hasMoreAnswer);
                expert.setHasMoreKnowledge(response.hasMoreKnowledges);

                if (!CollectionUtils.isEmpty(response.answers)) {
                    expert.setAnswers(new ArrayList<Answer>());

                    for (QuestionAnswer answer : response.answers) {
                        expert.getAnswers().add(PbMsgUtils.transObj(answer));
                    }
                }

                if (!CollectionUtils.isEmpty(response.knowledges)) {
                    expert.setKnowledges(new ArrayList<Knowledge>());

                    for (com.tuxing.rpc.proto.Knowledge knowledge : response.knowledges) {
                        expert.getKnowledges().add(PbMsgUtils.transObj(knowledge));
                    }
                }

                EventBus.getDefault().post(new ExpertEvent(
                        ExpertEvent.EventType.GET_EXPERT_DETAIL_SUCCESS,
                        null,
                        Arrays.asList(expert),
                        false
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new ExpertEvent(
                        ExpertEvent.EventType.GET_EXPERT_DETAIL_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getLatestMessages() {
        logger.debug("QuaraManager::getLatestMessages()");

        FetchCommunionMessageRequest request = new FetchCommunionMessageRequest(Long.MAX_VALUE, 0L, null);

        httpClient.sendRequest(RequestUrl.GET_COMMUNION_MESSAGE, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchCommunionMessageResponse response = SerializeUtils.parseFrom(data, FetchCommunionMessageResponse.class);

                List<CommunionMessage> messageList = new ArrayList<>();
                for (com.tuxing.rpc.proto.CommunionMessage message : response.msgs) {
                    messageList.add(PbMsgUtils.transObj(message));
                }

                EventBus.getDefault().post(new CommunionMessageEvent(
                        CommunionMessageEvent.EventType.FETCH_LATEST_MESSAGE_SUCCESS,
                        null,
                        messageList,
                        response.hasMore
                ));

            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new CommunionMessageEvent(
                        CommunionMessageEvent.EventType.FETCH_LATEST_MESSAGE_SUCCESS,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

//    @Override
//    CommunionMessage getMessageLastOneFromLocal(){
//        logger.debug("QuaraManager::getMessageLastOneFromLocal()");
//
//        CommunionMessage message = UserDbHelper.getInstance().getHomeWorkRecordLastOneFromLocal();
//        return message;
//    }

    @Override
    public void getHistoryMessage(long maxMessageId) {
        logger.debug("QuaraManager::getHistoryMessage(), maxMessageId = {}", maxMessageId);
        FetchCommunionMessageRequest request = new FetchCommunionMessageRequest(maxMessageId, 0L, null);

        httpClient.sendRequest(RequestUrl.GET_COMMUNION_MESSAGE, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchCommunionMessageResponse response = SerializeUtils.parseFrom(data, FetchCommunionMessageResponse.class);

                List<CommunionMessage> messageList = new ArrayList<>();
                for (com.tuxing.rpc.proto.CommunionMessage message : response.msgs) {
                    messageList.add(PbMsgUtils.transObj(message));
                }

                EventBus.getDefault().post(new CommunionMessageEvent(
                        CommunionMessageEvent.EventType.FETCH_HISTORY_MESSAGE_SUCCESS,
                        null,
                        messageList,
                        response.hasMore
                ));

            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new CommunionMessageEvent(
                        CommunionMessageEvent.EventType.FETCH_HISTORY_MESSAGE_SUCCESS,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void searchQuestion(String keywords, int pagenum) {
        logger.debug("QuaraManager::searchQuestion(), keywords = {},pagenum={}", keywords, pagenum);
        SearchQuestionRequest request = new SearchQuestionRequest(keywords, pagenum);
        httpClient.sendRequest(RequestUrl.SEARCH_REQUESTION, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                SearchQuestionResponse response = SerializeUtils.parseFrom(data, SearchQuestionResponse.class);
                List<Question> questionList = new ArrayList<Question>();
                for (com.tuxing.rpc.proto.Question question : response.questions) {
                    questionList.add(PbMsgUtils.transObj(question));
                }
                EventBus.getDefault().post(new SearchQuestionEvent(SearchQuestionEvent.EventType.
                        SEARCH_QUESTION_SUCCESS, null, questionList, response.hasMore));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new SearchQuestionEvent(SearchQuestionEvent.EventType.SEARCH_QUESTION_FAILED,
                        cause.getMessage(), null, false));
            }
        });
    }

}
