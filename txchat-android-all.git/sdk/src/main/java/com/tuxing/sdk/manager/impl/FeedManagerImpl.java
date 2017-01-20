package com.tuxing.sdk.manager.impl;

import android.content.Context;

import com.tuxing.rpc.proto.Attach;
import com.tuxing.rpc.proto.CommentType;
import com.tuxing.rpc.proto.DeleteCommnetsRequest;
import com.tuxing.rpc.proto.DeleteFeedRequest;
import com.tuxing.rpc.proto.DepartmentPhoto;
import com.tuxing.rpc.proto.FeedComment;
import com.tuxing.rpc.proto.FetchConcernedCommentRequest;
import com.tuxing.rpc.proto.FetchConcernedCommentResponse;
import com.tuxing.rpc.proto.FetchDepartmentPhotoRequest;
import com.tuxing.rpc.proto.FetchDepartmentPhotoResponse;
import com.tuxing.rpc.proto.FetchFeedMedicineTaskRequest;
import com.tuxing.rpc.proto.FetchFeedMedicineTaskResponse;
import com.tuxing.rpc.proto.FetchFeedRequest;
import com.tuxing.rpc.proto.FetchFeedResponse;
import com.tuxing.rpc.proto.FetchGardenMailRequest;
import com.tuxing.rpc.proto.FetchGardenMailResponse;
import com.tuxing.rpc.proto.FetchUserFeedRequest;
import com.tuxing.rpc.proto.FetchUserFeedResponse;
import com.tuxing.rpc.proto.ReadFeedMedicineRequest;
import com.tuxing.rpc.proto.ReadGardenMailRequest;
import com.tuxing.rpc.proto.SendCommentRequest;
import com.tuxing.rpc.proto.SendCommentResponse;
import com.tuxing.rpc.proto.SendFeedMedicineTaskRequest;
import com.tuxing.rpc.proto.SendFeedMedicineTaskResponse;
import com.tuxing.rpc.proto.SendFeedRequest;
import com.tuxing.rpc.proto.SendFeedResponse;
import com.tuxing.rpc.proto.SendGardenMailRequest;
import com.tuxing.rpc.proto.SendGardenMailResponse;
import com.tuxing.rpc.proto.ShowCommentRequest;
import com.tuxing.rpc.proto.ShowCommentResponse;
import com.tuxing.rpc.proto.TargetType;
import com.tuxing.sdk.db.entity.Activity;
import com.tuxing.sdk.db.entity.ClassPicture;
import com.tuxing.sdk.db.entity.Comment;
import com.tuxing.sdk.db.entity.Feed;
import com.tuxing.sdk.db.entity.FeedMedicineTask;
import com.tuxing.sdk.db.entity.GardenMail;
import com.tuxing.sdk.db.helper.UserDbHelper;
import com.tuxing.sdk.event.ClassPictureEvent;
import com.tuxing.sdk.event.CommentEvent;
import com.tuxing.sdk.event.FeedEvent;
import com.tuxing.sdk.event.FeedMedicineTaskEvent;
import com.tuxing.sdk.event.GardenMailEvent;
import com.tuxing.sdk.http.HttpClient;
import com.tuxing.sdk.http.RequestCallback;
import com.tuxing.sdk.http.RequestUrl;
import com.tuxing.sdk.manager.CounterManager;
import com.tuxing.sdk.manager.FeedManager;
import com.tuxing.sdk.manager.LoginManager;
import com.tuxing.sdk.modle.Attachment;
import com.tuxing.sdk.task.AsyncTaskProxyFactory;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.Constants;
import com.tuxing.sdk.utils.PbMsgUtils;
import com.tuxing.sdk.utils.SerializeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Alan on 2015/6/30.
 */
public class FeedManagerImpl implements FeedManager {
    private final static Logger logger = LoggerFactory.getLogger(FeedManager.class);
    private static FeedManager instance;

    HttpClient httpClient = HttpClient.getInstance();
    LoginManager loginManager = LoginManagerImpl.getInstance();
    CounterManager counterManager = CounterManagerImpl.getInstance();

    private Context context;

    private FeedManagerImpl() {

    }

    public synchronized static FeedManager getInstance() {
        if (instance == null) {
            instance = new FeedManagerImpl();
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
    public void getLatestFeedMedicineTask() {
        logger.debug("FeedManager::getLatestFeedMedicineTask()");


        FetchFeedMedicineTaskRequest request = new FetchFeedMedicineTaskRequest.Builder()
                .maxId(Long.MAX_VALUE)
                .sinceId(0L)
                .build();

        httpClient.sendRequest(RequestUrl.FETCH_FEED_MEDICINE_TASK, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchFeedMedicineTaskResponse response = SerializeUtils.parseFrom(data,
                        FetchFeedMedicineTaskResponse.class);

                List<FeedMedicineTask> feedMedicineTasks = new ArrayList<>();
                for (com.tuxing.rpc.proto.FeedMedicineTask txTask : response.feedMedicineTask) {
                    FeedMedicineTask task = PbMsgUtils.transObj(txTask);
                    feedMedicineTasks.add(task);
                }

                EventBus.getDefault().post(new FeedMedicineTaskEvent(
                        FeedMedicineTaskEvent.EventType.FEED_MEDICINE_LATEST_SUCCESS,
                        null,
                        feedMedicineTasks,
                        response.hasMore));

                UserDbHelper.getInstance().saveFeedMedicineTasks(feedMedicineTasks);
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new FeedMedicineTaskEvent(
                        FeedMedicineTaskEvent.EventType.FEED_MEDICINE_LATEST_FAILED,
                        cause.getMessage(),
                        null,
                        false));
            }
        });
    }

    @Override
    public void getHistoryFeedMedicineTask(long maxTaskId) {
        logger.debug("FeedManager::getHistoryFeedMedicineTask(), maxTaskId={}", maxTaskId);


        FetchFeedMedicineTaskRequest request = new FetchFeedMedicineTaskRequest.Builder()
                .maxId(maxTaskId)
                .sinceId(0L)
                .build();

        httpClient.sendRequest(RequestUrl.FETCH_FEED_MEDICINE_TASK, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchFeedMedicineTaskResponse response = SerializeUtils.parseFrom(data,
                        FetchFeedMedicineTaskResponse.class);

                List<FeedMedicineTask> feedMedicineTasks = new ArrayList<>();
                for (com.tuxing.rpc.proto.FeedMedicineTask txTask : response.feedMedicineTask) {
                    FeedMedicineTask task = PbMsgUtils.transObj(txTask);
                    feedMedicineTasks.add(task);
                }

                EventBus.getDefault().post(new FeedMedicineTaskEvent(
                        FeedMedicineTaskEvent.EventType.FEED_MEDICINE_HISTORY_SUCCESS,
                        null,
                        feedMedicineTasks,
                        response.hasMore));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new FeedMedicineTaskEvent(
                        FeedMedicineTaskEvent.EventType.FEED_MEDICINE_HISTORY_FAILED,
                        cause.getMessage(),
                        null,
                        false));
            }
        });
    }

    @Override
    public void getFeedMedicineTaskFromLocal() {
        logger.debug("FeedManager::getFeedMedicineTaskFromLocal()");


        List<FeedMedicineTask> tasks = UserDbHelper.getInstance().getLatestFeedMedicineTasks();

        EventBus.getDefault().post(new FeedMedicineTaskEvent(
                FeedMedicineTaskEvent.EventType.FEED_MEDICINE_FROM_LOCAL,
                null,
                tasks,
                false
        ));

    }

    @Override
    public void addFeedMedicineTask(final long beginDate, final String description, final List<Attachment> attachments) {
        logger.debug("FeedManager::addFeedMedicineTask(), beginDate={}, description={}", beginDate, description);


        SendFeedMedicineTaskRequest.Builder requestBuilder = new SendFeedMedicineTaskRequest.Builder();

        requestBuilder.beginDate(beginDate);
        requestBuilder.desc(description);

        requestBuilder.attaches(new ArrayList<Attach>());

        if (!CollectionUtils.isEmpty(attachments)) {
            for (Attachment attachment : attachments) {
                logger.debug("Send FeedMedicineTask attachment, file={}, key={}",
                        attachment.getLocalFilePath(),
                        attachment.getFileUrl());

                Attach attach = new Attach.Builder()
                        .fileurl(attachment.getFileUrl())
                        .attachType(PbMsgUtils.transAttachType(attachment.getType()))
                        .build();

                requestBuilder.attaches.add(attach);
            }
        }

        SendFeedMedicineTaskRequest request = requestBuilder.build();

        httpClient.sendRequest(RequestUrl.ADD_FEED_MEDICINE_TASK, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                SendFeedMedicineTaskResponse response = SerializeUtils.parseFrom(data, SendFeedMedicineTaskResponse.class);

                FeedMedicineTask task = new FeedMedicineTask();
                task.setTaskId(response.feedMedicineTaskId);
                task.setSenderId(loginManager.getCurrentUser().getUserId());
                task.setSenderName(loginManager.getCurrentUser().getUsername());
                task.setSenderAvatar(loginManager.getCurrentUser().getAvatar());
                task.setClassId(loginManager.getCurrentUser().getClassId());
                task.setClassName(loginManager.getCurrentUser().getClassName());
                task.setBeginDate(beginDate);
                task.setDescription(description);
                task.setSendTime(System.currentTimeMillis());

                JSONArray jsonAttachments = new JSONArray();
                if (!CollectionUtils.isEmpty(attachments)) {
                    for (Attachment attachment : attachments) {
                        try {
                            JSONObject jsonAttach = new JSONObject();
                            jsonAttach.put("url", Constants.QI_NIU_DOMAIN + attachment.getFileUrl());
                            jsonAttach.put("type", attachment.getType());

                            jsonAttachments.put(jsonAttach);
                        } catch (JSONException e) {
                            logger.error("Json Exception", e);
                        }
                    }
                }
                task.setAttachments(jsonAttachments.toString());

                EventBus.getDefault().post(new FeedMedicineTaskEvent(
                        FeedMedicineTaskEvent.EventType.ADD_FEED_MEDICINE_SUCCESS,
                        null,
                        Arrays.asList(task),
                        false));

                UserDbHelper.getInstance().saveFeedMedicineTask(task);

            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new FeedMedicineTaskEvent(
                        FeedMedicineTaskEvent.EventType.ADD_FEED_MEDICINE_FAILED,
                        cause.getMessage(),
                        null,
                        false));
            }
        });
    }

    @Override
    public void replayFeedMedicineTask(final long taskId, final String content) {
        logger.debug("FeedManager::replayFeedMedicineTask(), taskId={}, content={}", taskId, content);


        SendCommentRequest request = new SendCommentRequest.Builder()
                .content(content)
                .commentType(CommentType.REPLY)
                .targetId(taskId)
                .targetType(TargetType.FEED_MEDICIN_TASK)
                .build();

        httpClient.sendRequest(RequestUrl.SEND_COMMENT, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                SendCommentResponse response = SerializeUtils.parseFrom(data, SendCommentResponse.class);

                Comment comment = new Comment();
                comment.setCommentId(response.commentId);
                comment.setSendTime(System.currentTimeMillis());
                comment.setContent(content);
                comment.setTopicId(taskId);
                comment.setTargetType(Constants.TARGET_TYPE.FEED_MEDICIN_TASK);
                comment.setSenderId(loginManager.getCurrentUser().getUserId());
                comment.setSenderName(loginManager.getCurrentUser().getNickname());
                comment.setSenderAvatar(loginManager.getCurrentUser().getAvatar());

                EventBus.getDefault().post(new CommentEvent(
                        CommentEvent.EventType.REPLAY_FEED_MEDICINE_TASK_SUCCESS,
                        null,
                        Arrays.asList(comment),
                        false
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new CommentEvent(
                        CommentEvent.EventType.REPLAY_FEED_MEDICINE_TASK_FAILED,
                        null,
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void markFeedMedicineTaskAsRead(final long taskId) {
        ReadFeedMedicineRequest request = new ReadFeedMedicineRequest.Builder()
                .id(taskId)
                .build();

        httpClient.sendRequest(RequestUrl.MARK_MEDICINE_TASK_AS_READ, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FeedMedicineTask task = UserDbHelper.getInstance().getFeedMedicineTask(taskId);

                if (task != null) {
                    task.setUpdated(false);
                    UserDbHelper.getInstance().saveFeedMedicineTask(task);
                } else {
                    logger.warn("Cannot find the feed medicine task {}", taskId);
                }
            }

            @Override
            public void onFailure(Throwable cause) {
                logger.error("Cannot mark feed medicine task {} as read", taskId);
            }
        });

        counterManager.decCounter(Constants.COUNTER.MEDICINE);
    }

    @Override
    public void getCommentsForFeedMedicineTask(long taskId) {
        logger.debug("FeedManager::getCommentsForFeedMedicineTask(), taskId={}", taskId);


        ShowCommentRequest request = new ShowCommentRequest.Builder()
                .targetType(TargetType.FEED_MEDICIN_TASK)
                .targetId(taskId)
                .maxId(Long.MAX_VALUE)
                .sinceId(0L)
                .build();

        httpClient.sendRequest(RequestUrl.SHOW_COMMENT, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                ShowCommentResponse response = SerializeUtils.parseFrom(data, ShowCommentResponse.class);

                List<Comment> commentList = new ArrayList<>();
                for (com.tuxing.rpc.proto.Comment txComment : response.comment) {
                    Comment comment = PbMsgUtils.transObj(txComment);
                    commentList.add(comment);
                }

                EventBus.getDefault().post(new CommentEvent(
                        CommentEvent.EventType.GET_FEED_MEDICINE_TASK_COMMENTS_SUCCESS,
                        null,
                        commentList,
                        response.hasMore
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new CommentEvent(
                        CommentEvent.EventType.GET_FEED_MEDICINE_TASK_COMMENTS_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getLatestGardenMails() {
        logger.debug("FeedManager::getLatestGardenMails()");


        final FetchGardenMailRequest request = new FetchGardenMailRequest.Builder()
                .sinceId(0L)
                .maxId(Long.MAX_VALUE)
                .build();

        httpClient.sendRequest(RequestUrl.FETCH_GARDEN_MAIL, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchGardenMailResponse response = SerializeUtils.parseFrom(data, FetchGardenMailResponse.class);

                List<GardenMail> gardenMailList = new ArrayList<>();
                for (com.tuxing.rpc.proto.GardenMail txGardenMail : response.gardenMail) {
                    GardenMail gardenMail = PbMsgUtils.transObj(txGardenMail);
                    gardenMailList.add(gardenMail);
                }

                EventBus.getDefault().post(new GardenMailEvent(
                        GardenMailEvent.EventType.FETCH_LATEST_MAIL_SUCCESS,
                        null,
                        gardenMailList,
                        response.hasMore
                ));

                UserDbHelper.getInstance().saveGardenMails(gardenMailList);
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new GardenMailEvent(
                        GardenMailEvent.EventType.FETCH_LATEST_MAIL_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getHistoryGardenMails(long maxMailId) {
        logger.debug("FeedManager::getHistoryGardenMails(), maxMailId={}", maxMailId);


        FetchGardenMailRequest request = new FetchGardenMailRequest.Builder()
                .sinceId(0L)
                .maxId(maxMailId)
                .build();

        httpClient.sendRequest(RequestUrl.FETCH_GARDEN_MAIL, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchGardenMailResponse response = SerializeUtils.parseFrom(data, FetchGardenMailResponse.class);

                List<GardenMail> gardenMailList = new ArrayList<>();
                for (com.tuxing.rpc.proto.GardenMail txGardenMail : response.gardenMail) {
                    GardenMail gardenMail = PbMsgUtils.transObj(txGardenMail);
                    gardenMailList.add(gardenMail);
                }

                EventBus.getDefault().post(new GardenMailEvent(
                        GardenMailEvent.EventType.FETCH_HISTORY_MAIL_SUCCESS,
                        null,
                        gardenMailList,
                        response.hasMore
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new GardenMailEvent(
                        GardenMailEvent.EventType.FETCH_HISTORY_MAIL_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getGardenMailsFromLocal() {
        logger.debug("FeedManager::getGardenMailsFromLocal()");


        List<GardenMail> mails = UserDbHelper.getInstance().getLatestGardenMails();

        EventBus.getDefault().post(new GardenMailEvent(
                GardenMailEvent.EventType.FETCH_HISTORY_MAIL_FROM_LOCAL,
                null,
                mails,
                false
        ));
    }

    @Override
    public void sendGardenMail(final String content, final boolean anonymous) {
        logger.debug("FeedManager::sendGardenMail(), content={}， anonymous={}", content, anonymous);


        SendGardenMailRequest request = new SendGardenMailRequest.Builder()
                .anonymous(anonymous)
                .content(content)
                .build();

        httpClient.sendRequest(RequestUrl.SEND_GARDEN_MAIL, request.toByteArray(), new RequestCallback() {

            @Override
            public void onResponse(byte[] data) throws IOException {
                SendGardenMailResponse response = SerializeUtils.parseFrom(data, SendGardenMailResponse.class);

                GardenMail mail = new GardenMail();
                mail.setMailId(response.id);
                mail.setSenderId(loginManager.getCurrentUser().getUserId());
                mail.setSenderName(loginManager.getCurrentUser().getUsername());
                mail.setSenderAvatar(loginManager.getCurrentUser().getAvatar());
                mail.setGardenId(loginManager.getCurrentUser().getGardenId());
                mail.setGardenName(loginManager.getCurrentUser().getGardenName());
                mail.setContent(content);
                mail.setAnonymous(anonymous);
                mail.setSendTime(System.currentTimeMillis());

                EventBus.getDefault().post(new GardenMailEvent(
                        GardenMailEvent.EventType.SEND_PRINCIPAL_MAIL_SUCCESS,
                        null,
                        Arrays.asList(mail),
                        false
                ));

                UserDbHelper.getInstance().saveGardenMail(mail);
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new GardenMailEvent(
                        GardenMailEvent.EventType.SEND_PRINCIPAL_MAIL_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void replayGardenMail(final long mailId, final String content) {
        logger.debug("FeedManager::replayGardenMail(), mailId={}, content={}", mailId, content);


        SendCommentRequest request = new SendCommentRequest.Builder()
                .content(content)
                .commentType(CommentType.REPLY)
                .targetId(mailId)
                .targetType(TargetType.GARDEN_MAIL)
                .build();

        httpClient.sendRequest(RequestUrl.SEND_COMMENT, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                SendCommentResponse response = SerializeUtils.parseFrom(data, SendCommentResponse.class);

                Comment comment = new Comment();
                comment.setCommentId(response.commentId);
                comment.setSendTime(System.currentTimeMillis());
                comment.setTargetType(Constants.TARGET_TYPE.GARDEN_MAIL);
                comment.setContent(content);
                comment.setTopicId(mailId);
                comment.setSenderId(loginManager.getCurrentUser().getUserId());
                comment.setSenderName(loginManager.getCurrentUser().getNickname());
                comment.setSenderAvatar(loginManager.getCurrentUser().getAvatar());

                EventBus.getDefault().post(new CommentEvent(
                        CommentEvent.EventType.REPLAY_GARDEN_MAIL_SUCCESS,
                        null,
                        Arrays.asList(comment),
                        false
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new CommentEvent(
                        CommentEvent.EventType.REPLAY_GARDEN_MAIL_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void markGardenMailAsRead(final long mailId) {
        ReadGardenMailRequest request = new ReadGardenMailRequest.Builder()
                .id(mailId)
                .build();

        httpClient.sendRequest(RequestUrl.MARK_GARDEN_MAIL_AS_READ, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                GardenMail mail = UserDbHelper.getInstance().getGardenMail(mailId);

                if (mail != null) {
                    mail.setUpdated(false);
                    UserDbHelper.getInstance().saveGardenMail(mail);
                } else {
                    logger.warn("Cannot find the garden mail {}", mailId);
                }
            }

            @Override
            public void onFailure(Throwable cause) {
                logger.error("Cannot mark garden mail {} as read", mailId);
            }
        });

        counterManager.decCounter(Constants.COUNTER.MAIL);
    }

    @Override
    public void getCommentsForGardenMail(long mailId) {
        logger.debug("FeedManager::getCommentsForGardenMail(), mailId={}", mailId);


        ShowCommentRequest request = new ShowCommentRequest.Builder()
                .targetType(TargetType.GARDEN_MAIL)
                .targetId(mailId)
                .maxId(Long.MAX_VALUE)
                .sinceId(0L)
                .build();

        httpClient.sendRequest(RequestUrl.SHOW_COMMENT, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                ShowCommentResponse response = SerializeUtils.parseFrom(data, ShowCommentResponse.class);

                List<Comment> commentList = new ArrayList<>();
                for (com.tuxing.rpc.proto.Comment txComment : response.comment) {
                    Comment comment = PbMsgUtils.transObj(txComment);
                    commentList.add(comment);
                }

                EventBus.getDefault().post(new CommentEvent(
                        CommentEvent.EventType.GET_GARDEN_MAIL_COMMENTS_SUCCESS,
                        null,
                        commentList,
                        response.hasMore
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new CommentEvent(
                        CommentEvent.EventType.GET_GARDEN_MAIL_COMMENTS_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getLatestFeed(Long departmentId) {
        logger.debug("FeedManager::getLatestFeed(), departmentId={}", departmentId);


        FetchFeedRequest.Builder requestBuilder = new FetchFeedRequest.Builder();
        requestBuilder.isInbox(true);
        requestBuilder.maxId(Long.MAX_VALUE);
        requestBuilder.sinceId(0L);

        if (departmentId != null) {
            requestBuilder.departmentId(departmentId);
        }

        FetchFeedRequest request = requestBuilder.build();

        httpClient.sendRequest(RequestUrl.FETCH_FEED, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchFeedResponse response = SerializeUtils.parseFrom(data, FetchFeedResponse.class);

                List<Feed> feedList = new LinkedList<>();
                for (com.tuxing.rpc.proto.Feed txFeed : response.feeds) {
                    Feed feed = PbMsgUtils.transObj(txFeed);
                    feedList.add(feed);
                }

                if (response.activity != null) {
                    Activity activity = PbMsgUtils.transObj(response.activity);

                    if (!UserDbHelper.getInstance().isActivityDeleted(response.activity.id)) {
                        Feed feed = PbMsgUtils.trans2Feed(response.activity);
                        feedList.add(0, feed);
                    } else {
                        activity.setDeleted(true);
                    }

                    UserDbHelper.getInstance().saveActivity(activity);
                } else {
                    UserDbHelper.getInstance().deleteAllActivity();
                }

                EventBus.getDefault().post(new FeedEvent(
                        FeedEvent.EventType.REQUEST_LATEST_FEED_FROM_SERVER_SUCCESS,
                        null,
                        feedList,
                        response.hasMore
                ));

                UserDbHelper.getInstance().saveFeeds(feedList);
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new FeedEvent(
                        FeedEvent.EventType.REQUEST_LATEST_FEED_FROM_SERVER_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getHistoryFeed(long maxFeedId, Long departmentId) {
        logger.debug("FeedManager::getHistoryFeed(), maxFeedId={}, departmentId={}", maxFeedId, departmentId);


        FetchFeedRequest.Builder requestBuilder = new FetchFeedRequest.Builder();
        requestBuilder.isInbox(true);
        requestBuilder.maxId(maxFeedId);
        requestBuilder.sinceId(0L);

        if (departmentId != null) {
            requestBuilder.departmentId(departmentId);
        }

        FetchFeedRequest request = requestBuilder.build();

        httpClient.sendRequest(RequestUrl.FETCH_FEED, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchFeedResponse response = SerializeUtils.parseFrom(data, FetchFeedResponse.class);

                List<Feed> feedList = new ArrayList<>();
                for (com.tuxing.rpc.proto.Feed txFeed : response.feeds) {
                    Feed feed = PbMsgUtils.transObj(txFeed);
                    feedList.add(feed);
                }

                EventBus.getDefault().post(new FeedEvent(
                        FeedEvent.EventType.REQUEST_HISTORY_FEED_FROM_SERVER_SUCCESS,
                        null,
                        feedList,
                        response.hasMore
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new FeedEvent(
                        FeedEvent.EventType.REQUEST_HISTORY_FEED_FROM_SERVER_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getFeedFromLocalCache() {
        logger.debug("FeedManager::getFeedFromLocalCache()");


        List<Feed> feeds = UserDbHelper.getInstance().getLatestFeed();

        EventBus.getDefault().post(new FeedEvent(
                FeedEvent.EventType.GET_FEED_FROM_LOCAL_CACHE,
                null,
                feeds,
                false
        ));
    }

    @Override
    public void deleteFeed(final long feedId) {
        logger.debug("FeedManager::deleteFeed(), feedId={}", feedId);


        DeleteFeedRequest request = new DeleteFeedRequest.Builder()
                .feedId(feedId)
                .build();

        httpClient.sendRequest(RequestUrl.DELETE_FEED, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {

                UserDbHelper.getInstance().deleteFeed(feedId);

                EventBus.getDefault().post(new FeedEvent(
                        FeedEvent.EventType.DELETE_FEED_SUCCESS,
                        null,
                        null,
                        false
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new FeedEvent(
                        FeedEvent.EventType.DELETE_FEED_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void publishFeed(List<Long> departments, final String content, final List<Attachment> attachments,
                            boolean publishToClassAlbum) {
        logger.debug("FeedManager::publishFeed(), departments={}, content={}, publishToClassAlbum={}",
                departments.toString(), content, publishToClassAlbum);


        SendFeedRequest.Builder requestBuilder = new SendFeedRequest.Builder();

        requestBuilder.content(content);
        requestBuilder.departmentIds(departments);
        requestBuilder.syncDepartmentPhoto = publishToClassAlbum;

        requestBuilder.attaches(new ArrayList<Attach>());

        if (!CollectionUtils.isEmpty(attachments)) {
            for (Attachment attachment : attachments) {
                requestBuilder.attaches.add(new Attach.Builder()
                        .fileurl(attachment.getFileUrl())
                        .attachType(PbMsgUtils.transAttachType(attachment.getType()))
                        .build());
            }
        }

        SendFeedRequest request = requestBuilder.build();

        httpClient.sendRequest(RequestUrl.PUBLISH_FEED, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                SendFeedResponse response = SerializeUtils.parseFrom(data, SendFeedResponse.class);
                Feed feed = new Feed();
                feed.setFeedId(response.feedId);
                feed.setContent(content);
                feed.setPublishTime(System.currentTimeMillis());
                JSONArray jsonAttachments = new JSONArray();
                if (!CollectionUtils.isEmpty(attachments)) {
                    for (Attachment attachment : attachments) {
                        try {
                            JSONObject jsonAttach = new JSONObject();
                            jsonAttach.put("url", Constants.QI_NIU_DOMAIN + attachment.getFileUrl());
                            jsonAttach.put("type", attachment.getType());

                            jsonAttachments.put(jsonAttach);
                        } catch (JSONException e) {
                            logger.error("Json Exception", e);
                        }
                    }
                }
                feed.setAttachments(jsonAttachments.toString());
                feed.setUserId(loginManager.getCurrentUser().getUserId());
                feed.setUserName(loginManager.getCurrentUser().getNickname());
                feed.setUserAvatar(loginManager.getCurrentUser().getAvatar());


                EventBus.getDefault().post(new FeedEvent(
                        FeedEvent.EventType.SEND_FEED_SUCCESS,
                        null,
                        Arrays.asList(feed),
                        false,
                        response.bonus
                ));

                UserDbHelper.getInstance().saveFeed(feed);
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new FeedEvent(
                        FeedEvent.EventType.SEND_FEED_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void deleteFeedComment(final long commentId) {
        logger.debug("FeedManager::deleteFeedComment(), commentId={}", commentId);


        DeleteCommnetsRequest request = new DeleteCommnetsRequest.Builder()
                .commentId(commentId)
                .build();

        httpClient.sendRequest(RequestUrl.DELETE_COMMENT, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {

                UserDbHelper.getInstance().deleteComment(commentId);

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
    public void publishComment(final long feedId, final String content, final Long replayToUserId,
                               final String replayToUserName, final int commentType) {
        logger.debug("FeedManager::publishComment(), feedId={}, content={}, replayToUserId={}, " +
                        "replayToUserName={}, commentType={}", feedId, content, replayToUserId,
                replayToUserName, commentType);


        SendCommentRequest.Builder requestBuilder = new SendCommentRequest.Builder();

        requestBuilder.targetType(TargetType.FEED);
        requestBuilder.targetId(feedId);
        requestBuilder.commentType(PbMsgUtils.transCommentType(commentType));
        if (content != null) {
            requestBuilder.content(content);
        }
        if (replayToUserId != null) {
            requestBuilder.toUserId(replayToUserId);
        }

        SendCommentRequest request = requestBuilder.build();

        httpClient.sendRequest(RequestUrl.SEND_COMMENT, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                SendCommentResponse response = SerializeUtils.parseFrom(data, SendCommentResponse.class);

                Comment comment = new Comment();
                comment.setCommentId(response.commentId);
                comment.setTopicId(feedId);
                comment.setTargetType(Constants.TARGET_TYPE.FEED);
//                comment.setSenderName(loginManager.getCurrentUser().getNickname());

                comment.setSenderName(loginManager.getCurrentUser().getCombinedNickname());
                comment.setSenderId(loginManager.getCurrentUser().getUserId());
                comment.setSenderAvatar(loginManager.getCurrentUser().getAvatar());
                comment.setCommentType(commentType);
                comment.setReplayToUserId(replayToUserId);
                comment.setReplayToUserName(replayToUserName);
                comment.setContent(content);
                comment.setSendTime(System.currentTimeMillis());

                UserDbHelper.getInstance().saveComment(comment);

                EventBus.getDefault().post(new CommentEvent(
                        CommentEvent.EventType.REPLAY_FEED_SUCCESS,
                        null,
                        Arrays.asList(comment),
                        false,
                        response.bonus
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new CommentEvent(
                        CommentEvent.EventType.REPLAY_FEED_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });

    }

    @Override
    public void getLatestTimeLine() {
        logger.debug("FeedManager::getLatestTimeLine()");


        FetchFeedRequest request = new FetchFeedRequest.Builder()
                .sinceId(0L)
                .maxId(Long.MAX_VALUE)
                .isInbox(false)
                .build();

        httpClient.sendRequest(RequestUrl.FETCH_FEED, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchFeedResponse response = SerializeUtils.parseFrom(data, FetchFeedResponse.class);

                List<Feed> feedList = new ArrayList<>();
                for (com.tuxing.rpc.proto.Feed txFeed : response.feeds) {
                    Feed feed = PbMsgUtils.transObj(txFeed);
                    feedList.add(feed);
                }


                EventBus.getDefault().post(new FeedEvent(
                        FeedEvent.EventType.REQUEST_LATEST_TIME_LINE_FROM_SERVER_SUCCESS,
                        null,
                        feedList,
                        response.hasMore
                ));

                UserDbHelper.getInstance().saveFeeds(feedList);
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new FeedEvent(
                        FeedEvent.EventType.REQUEST_LATEST_TIME_LINE_FROM_SERVER_SUCCESS,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getHistoryTimeLine(long maxFeedId) {
        logger.debug("FeedManager::getLatestTimeLine(), maxFeedId={}", maxFeedId);


        FetchFeedRequest request = new FetchFeedRequest.Builder()
                .sinceId(0L)
                .maxId(maxFeedId)
                .isInbox(false)
                .build();

        httpClient.sendRequest(RequestUrl.FETCH_FEED, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchFeedResponse response = SerializeUtils.parseFrom(data, FetchFeedResponse.class);

                List<Feed> feedList = new ArrayList<>();
                for (com.tuxing.rpc.proto.Feed txFeed : response.feeds) {
                    Feed feed = PbMsgUtils.transObj(txFeed);
                    feedList.add(feed);
                }

                EventBus.getDefault().post(new FeedEvent(
                        FeedEvent.EventType.REQUEST_HISTORY_TIME_LINE_FROM_SERVER_SUCCESS,
                        null,
                        feedList,
                        response.hasMore
                ));

                UserDbHelper.getInstance().saveFeeds(feedList);
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new FeedEvent(
                        FeedEvent.EventType.REQUEST_HISTORY_TIME_LINE_FROM_SERVER_SUCCESS,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getHistoryFeedComments(long feedId, long maxCommentId) {
        logger.debug("FeedManager::getHistoryFeedComments(), feedId={}, maxCommentId={}", feedId, maxCommentId);


        ShowCommentRequest request = new ShowCommentRequest.Builder()
                .targetType(TargetType.FEED)
                .targetId(feedId)
                .maxId(maxCommentId)
                .sinceId(0L)
                .build();

        httpClient.sendRequest(RequestUrl.SHOW_COMMENT, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                ShowCommentResponse response = SerializeUtils.parseFrom(data, ShowCommentResponse.class);

                List<Comment> commentList = new ArrayList<>();
                for (com.tuxing.rpc.proto.Comment txComment : response.comment) {
                    Comment comment = PbMsgUtils.transObj(txComment);
                    commentList.add(comment);
                }

                EventBus.getDefault().post(new CommentEvent(
                        CommentEvent.EventType.GET_HISTORY_FEED_COMMENTS_SUCCESS,
                        null,
                        commentList,
                        response.hasMore
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new CommentEvent(
                        CommentEvent.EventType.GET_HISTORY_FEED_COMMENTS_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public Feed getFeedFromLocal(long feedId) {
        return UserDbHelper.getInstance().getLatestFeedById(feedId);
    }

    @Override
    public void getLatestFeedComments(long feedId) {
        logger.debug("FeedManager::getFeedComments(), feedId={}", feedId);


        ShowCommentRequest request = new ShowCommentRequest.Builder()
                .targetType(TargetType.FEED)
                .targetId(feedId)
                .maxId(Long.MAX_VALUE)
                .sinceId(0L)
                .build();

        httpClient.sendRequest(RequestUrl.SHOW_COMMENT, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                ShowCommentResponse response = SerializeUtils.parseFrom(data, ShowCommentResponse.class);

                List<Comment> commentList = new ArrayList<>();
                for (com.tuxing.rpc.proto.Comment txComment : response.comment) {
                    Comment comment = PbMsgUtils.transObj(txComment);
                    commentList.add(comment);
                }

                EventBus.getDefault().post(new CommentEvent(
                        CommentEvent.EventType.GET_LATEST_FEED_COMMENTS_SUCCESS,
                        null,
                        commentList,
                        response.hasMore
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new CommentEvent(
                        CommentEvent.EventType.GET_LATEST_FEED_COMMENTS_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getLatestTimeLineByUser(final long userId) {
        logger.debug("FeedManager::getLatestTimeLineByUser(), userId={}", userId);


        FetchUserFeedRequest request = new FetchUserFeedRequest.Builder()
                .sinceId(0L)
                .maxId(Long.MAX_VALUE)
                .userId(userId)
                .build();

        httpClient.sendRequest(RequestUrl.FETCH_USER_FEED, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchUserFeedResponse response = SerializeUtils.parseFrom(data, FetchUserFeedResponse.class);

                List<Feed> feedList = new ArrayList<>();
                for (com.tuxing.rpc.proto.Feed txFeed : response.feeds) {
                    Feed feed = PbMsgUtils.transObj(txFeed);
                    feedList.add(feed);
                }

                EventBus.getDefault().post(new FeedEvent(
                        FeedEvent.EventType.REQUEST_LATEST_USER_TIME_LINE_FROM_SERVER_SUCCESS,
                        null,
                        feedList,
                        response.hasMore
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new FeedEvent(
                        FeedEvent.EventType.REQUEST_LATEST_USER_TIME_LINE_FROM_SERVER_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getHistoryTimeLineByUser(final long userId, long maxFeedId) {
        logger.debug("FeedManager::getHistoryTimeLineByUser(), userId={}, maxFeedId={}", userId, maxFeedId);


        FetchUserFeedRequest request = new FetchUserFeedRequest.Builder()
                .sinceId(0L)
                .maxId(maxFeedId)
                .userId(userId)
                .build();

        httpClient.sendRequest(RequestUrl.FETCH_USER_FEED, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchUserFeedResponse response = SerializeUtils.parseFrom(data, FetchUserFeedResponse.class);

                List<Feed> feedList = new ArrayList<>();
                for (com.tuxing.rpc.proto.Feed txFeed : response.feeds) {
                    Feed feed = PbMsgUtils.transObj(txFeed);
                    feedList.add(feed);
                }

                EventBus.getDefault().post(new FeedEvent(
                        FeedEvent.EventType.REQUEST_HISTORY_USER_TIME_LINE_FROM_SERVER_SUCCESS,
                        null,
                        feedList,
                        response.hasMore
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new FeedEvent(
                        FeedEvent.EventType.REQUEST_HISTORY_USER_TIME_LINE_FROM_SERVER_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }

    @Override
    public void getLatestMyConcernedComment() {
        logger.debug("FeedManager::getLatestMyConcernedComment()");


        FetchConcernedCommentRequest request = new FetchConcernedCommentRequest.Builder()
                .maxId(Long.MAX_VALUE)
                .sinceId(0L)
                .build();

        httpClient.sendRequest(RequestUrl.FETCH_CONCERNED_COMMENT, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchConcernedCommentResponse response = SerializeUtils.parseFrom(data,
                        FetchConcernedCommentResponse.class);

                List<Comment> concernedCommentList = new ArrayList<>();
                for (FeedComment concernedComment : response.feedComment) {
                    Feed feed = PbMsgUtils.transObj(concernedComment.feed);
                    Comment comment = PbMsgUtils.transObj(concernedComment.comment);

                    comment.setFeed(feed);
                    concernedCommentList.add(comment);
                }

                EventBus.getDefault().post(new CommentEvent(
                        CommentEvent.EventType.GET_LATEST_CONCERNED_COMMENT_SUCCESS,
                        null,
                        concernedCommentList,
                        response.hasMore));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new CommentEvent(
                        CommentEvent.EventType.GET_LATEST_CONCERNED_COMMENT_FAILED,
                        cause.getMessage(),
                        null,
                        false));
            }
        });
    }

    @Override
    public void getHistoryMyConcernedComment(long maxCommentId) {
        logger.debug("FeedManager::getHistoryMyConcernedComment(), maxCommentId={}", maxCommentId);


        FetchConcernedCommentRequest request = new FetchConcernedCommentRequest.Builder()
                .maxId(maxCommentId)
                .sinceId(0L)
                .build();

        httpClient.sendRequest(RequestUrl.FETCH_CONCERNED_COMMENT, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchConcernedCommentResponse response = SerializeUtils.parseFrom(data,
                        FetchConcernedCommentResponse.class);

                List<Comment> concernedCommentList = new ArrayList<>();
                for (FeedComment concernedComment : response.feedComment) {
                    Feed feed = PbMsgUtils.transObj(concernedComment.feed);
                    Comment comment = PbMsgUtils.transObj(concernedComment.comment);

                    comment.setFeed(feed);
                    concernedCommentList.add(comment);
                }

                EventBus.getDefault().post(new CommentEvent(
                        CommentEvent.EventType.GET_HISTORY_CONCERNED_COMMENT_SUCCESS,
                        null,
                        concernedCommentList,
                        response.hasMore));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new CommentEvent(
                        CommentEvent.EventType.GET_HISTORY_CONCERNED_COMMENT_FAILED,
                        cause.getMessage(),
                        null,
                        false));
            }
        });
    }

    @Override
    public void getLatestClassPictures(final Long classId) {
        logger.debug("FeedManager::getLatestClassPictures(), classId={}", classId);


        FetchDepartmentPhotoRequest request = new FetchDepartmentPhotoRequest.Builder()
                .departmentId(classId)
                .sinceId(0L)
                .maxId(Long.MAX_VALUE)
                .build();

        httpClient.sendRequest(RequestUrl.FETCH_CLASS_PICTURE, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchDepartmentPhotoResponse response = SerializeUtils.parseFrom(data,
                        FetchDepartmentPhotoResponse.class);

                List<ClassPicture> picturesList = new ArrayList<>();
                for (DepartmentPhoto departmentPhoto : response.photos) {
                    ClassPicture picture = PbMsgUtils.transObj(departmentPhoto);
                    picture.setClassId(classId);

                    picturesList.add(picture);
                }

                EventBus.getDefault().post(new ClassPictureEvent(
                        ClassPictureEvent.EventType.GET_LATEST_PICTURE_SUCCESS,
                        null,
                        picturesList,
                        response.hasMore,
                        response.totalCnt));

                UserDbHelper.getInstance().deleteClassPictures(classId);
                UserDbHelper.getInstance().saveClassPictures(picturesList);
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new ClassPictureEvent(
                        ClassPictureEvent.EventType.GET_LATEST_PICTURE_FAILED,
                        cause.getMessage(),
                        null,
                        false,
                        0));
            }
        });

    }

    @Override
    public void getHistoryClassPictures(final Long classId, long maxPictureId) {
        logger.debug("FeedManager::getHistoryClassPictures(), classId={}, maxPictureId={}", classId, maxPictureId);


        FetchDepartmentPhotoRequest request = new FetchDepartmentPhotoRequest.Builder()
                .departmentId(classId)
                .sinceId(0L)
                .maxId(maxPictureId)
                .build();

        httpClient.sendRequest(RequestUrl.FETCH_CLASS_PICTURE, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchDepartmentPhotoResponse response = SerializeUtils.parseFrom(data,
                        FetchDepartmentPhotoResponse.class);

                List<ClassPicture> picturesList = new ArrayList<>();
                for (DepartmentPhoto departmentPhoto : response.photos) {
                    ClassPicture picture = PbMsgUtils.transObj(departmentPhoto);
                    picture.setClassId(classId);

                    picturesList.add(picture);
                }

                EventBus.getDefault().post(new ClassPictureEvent(
                        ClassPictureEvent.EventType.GET_HISTORY_PICTURE_SUCCESS,
                        null,
                        picturesList,
                        response.hasMore,
                        response.totalCnt));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new ClassPictureEvent(
                        ClassPictureEvent.EventType.GET_HISTORY_PICTURE_FAILED,
                        cause.getMessage(),
                        null,
                        false,
                        0));
            }
        });
    }

    @Override
    public void getClassPicturesFromLocalCache(Long classId) {
        logger.debug("FeedManager::getClassPicturesFromLocalCache(), classId={}", classId);

        List<ClassPicture> pictures = UserDbHelper.getInstance().getClassPictures(classId);

        EventBus.getDefault().post(new ClassPictureEvent(
                ClassPictureEvent.EventType.GET_LOCAL_PICTURE,
                null,
                pictures,
                false,
                pictures.size()));
    }

    @Override
    public void deleteActivity(Long activityId) {
        logger.debug("FeedManager::deleteActivity(), activityId={}", activityId);

        UserDbHelper.getInstance().deleteActivity(activityId);

        EventBus.getDefault().post(new FeedEvent(
                FeedEvent.EventType.DELETE_FEED_SUCCESS,
                null,
                null,
                false
        ));
    }
}
