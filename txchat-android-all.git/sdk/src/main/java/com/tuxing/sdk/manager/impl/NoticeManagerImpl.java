package com.tuxing.sdk.manager.impl;

import android.content.Context;

import com.tuxing.rpc.proto.*;
import com.tuxing.sdk.db.entity.Department;
import com.tuxing.sdk.db.entity.Notice;
import com.tuxing.sdk.db.helper.UserDbHelper;
import com.tuxing.sdk.event.LoginEvent;
import com.tuxing.sdk.event.NoticeEvent;
import com.tuxing.sdk.event.NoticeReceiverEvent;
import com.tuxing.sdk.http.HttpClient;
import com.tuxing.sdk.http.RequestCallback;
import com.tuxing.sdk.http.RequestUrl;
import com.tuxing.sdk.manager.CounterManager;
import com.tuxing.sdk.manager.LoginManager;
import com.tuxing.sdk.manager.NoticeManager;
import com.tuxing.sdk.modle.Attachment;
import com.tuxing.sdk.modle.NoticeDepartmentReceiver;
import com.tuxing.sdk.modle.NoticeUserReceiver;
import com.tuxing.sdk.task.AsyncTaskProxyFactory;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.Constants;
import com.tuxing.sdk.utils.PbMsgUtils;
import com.tuxing.sdk.utils.SerializeUtils;
import de.greenrobot.event.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alan on 2015/5/21.
 */
public class NoticeManagerImpl implements NoticeManager {
    private final static Logger logger = LoggerFactory.getLogger(NoticeManager.class);
    private static NoticeManager instance;

    HttpClient httpClient = HttpClient.getInstance();
    LoginManager loginManager = LoginManagerImpl.getInstance();
    CounterManager counterManager = CounterManagerImpl.getInstance();

    Notice latestNotice;

    private Context context;

    private NoticeManagerImpl(){

    }

    public synchronized static NoticeManager getInstance(){
        if(instance == null){
            instance = new NoticeManagerImpl();
            instance = AsyncTaskProxyFactory.getProxy(instance);
        }

        return instance;
    }

    @Override
    public void init(Context context) {
        this.context = context;
        EventBus.getDefault().register(this);
    }

    @Override
    public void destroy() {
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    public void onEvent(LoginEvent event){
        switch(event.getEvent()){
            case KICK_OFF:
            case LOGOUT:
            case TOKEN_EXPIRED:
                this.latestNotice = null;
                break;
        }
    }

    @Override public void getLatestNotice(final int mailbox){
        logger.debug("NoticeManager::getLatestNotice(), mailbox={}", mailbox);


        FetchNoticeRequest request = new FetchNoticeRequest.Builder()
                .sinceId(0L)
                .maxId(Long.MAX_VALUE)
                .isInbox(mailbox == Constants.MAILBOX_INBOX)
                .build();

        httpClient.sendRequest(RequestUrl.FETCH_NOTICE, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchNoticeResponse response = SerializeUtils.parseFrom(data, FetchNoticeResponse.class);

                List<Notice> notices = new ArrayList<>();

                for (com.tuxing.rpc.proto.Notice pbNotice : response.notices){
                    Notice notice = PbMsgUtils.transObj(pbNotice);
                    notice.setMailbox(mailbox);
                    notices.add(notice);
                }

                if(mailbox == Constants.MAILBOX_INBOX) {

                    if(!CollectionUtils.isEmpty(notices)) {
                        latestNotice = notices.get(0);
                    }

                    EventBus.getDefault().post(new NoticeEvent(
                                NoticeEvent.EventType.NOTICE_INBOX_LATEST_NOTICE_SUCCESS,
                            null,
                            notices,
                            response.hasMore));
                }else{
                    EventBus.getDefault().post(new NoticeEvent(
                                NoticeEvent.EventType.NOTICE_OUTBOX_LATEST_NOTICE_SUCCESS,
                            null,
                            notices,
                            response.hasMore));
                }

                UserDbHelper.getInstance().deleteNotice(mailbox);
                UserDbHelper.getInstance().updateNoticeList(notices);
            }

            @Override
            public void onFailure(Throwable cause) {
                if(mailbox == Constants.MAILBOX_INBOX) {
                    EventBus.getDefault().post(new NoticeEvent(
                                NoticeEvent.EventType.NOTICE_INBOX_REQUEST_FAILED,
                            cause.getMessage(),
                            null,
                            false));
                }else{
                    EventBus.getDefault().post(new NoticeEvent(
                                NoticeEvent.EventType.NOTICE_OUTBOX_REQUEST_FAILED,
                            cause.getMessage(),
                            null,
                            false));
                }
            }
        });
    }

    @Override public void getLocalCachedNotice(int mailbox){
        logger.debug("NoticeManager::getLocalCachedNotice(), mailbox={}", mailbox);


        List<Notice> notices = UserDbHelper.getInstance().getLatestNotice(mailbox);

        logger.debug("Local notice list count: {}", notices.size());

        if(mailbox == Constants.MAILBOX_INBOX) {
            EventBus.getDefault().post(new NoticeEvent(
                        NoticeEvent.EventType.NOTICE_INBOX_FROM_CACHE,
                    null,
                    notices,
                    false));
        }else{
            EventBus.getDefault().post(new NoticeEvent(
                        NoticeEvent.EventType.NOTICE_OUTBOX_FROM_CACHE,
                    null,
                    notices,
                    false));
        }
    }

    @Override public void getHistoryNotice(long maxId, final int mailbox){
        logger.debug("NoticeManager::getHistoryNotice(), maxId={}, mailbox={}", maxId, mailbox);


        final FetchNoticeRequest request = new FetchNoticeRequest.Builder()
                .sinceId(0L)
                .maxId(maxId)
                .isInbox(mailbox == Constants.MAILBOX_INBOX)
                .build();


        httpClient.sendRequest(RequestUrl.FETCH_NOTICE, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchNoticeResponse response = SerializeUtils.parseFrom(data, FetchNoticeResponse.class);

                List<Notice> notices = new ArrayList<>();

                for (com.tuxing.rpc.proto.Notice pbNotice : response.notices){
                    Notice notice = PbMsgUtils.transObj(pbNotice);
                    notice.setMailbox(mailbox);
                    notices.add(notice);
                }

                if(mailbox == Constants.MAILBOX_INBOX) {
                    EventBus.getDefault().post(new NoticeEvent(
                                NoticeEvent.EventType.NOTICE_INBOX_REQUEST_SUCCESS,
                            null,
                            notices,
                            response.hasMore));
                }else{
                    EventBus.getDefault().post(new NoticeEvent(
                                NoticeEvent.EventType.NOTICE_OUTBOX_REQUEST_SUCCESS,
                            null,
                            notices,
                            response.hasMore));
                }
            }

            @Override
            public void onFailure(Throwable cause) {
                if(mailbox == Constants.MAILBOX_INBOX) {
                    EventBus.getDefault().post(new NoticeEvent(
                                NoticeEvent.EventType.NOTICE_INBOX_REQUEST_FAILED,
                            cause.getMessage(),
                            null,
                            false));
                }else{
                    EventBus.getDefault().post(new NoticeEvent(
                                NoticeEvent.EventType.NOTICE_OUTBOX_REQUEST_FAILED,
                            cause.getMessage(),
                            null,
                            false));
                }
            }
        });

    }

    @Override public void sendNotice(final String content, final List<Attachment> attachments,
                           final List<NoticeDepartmentReceiver> receives){
        logger.debug("NoticeManager::sendNotice(), content={}", content);


        SendNoticeRequest.Builder requestBuilder = new SendNoticeRequest.Builder();
        requestBuilder.content(content);

        requestBuilder.attches(new ArrayList<Attach>());
        if(!CollectionUtils.isEmpty(attachments)) {
            for (Attachment attachment : attachments) {
                logger.debug("Send notice attachment, file={}, key={}",
                        attachment.getLocalFilePath(),
                        attachment.getFileUrl());

                Attach attach = new Attach.Builder()
                                .fileurl(attachment.getFileUrl())
                                .attachType(PbMsgUtils.transAttachType(attachment.getType()))
                        .build();

                requestBuilder.attches.add(attach);
            }
        }

        requestBuilder.noticeDepartments(new ArrayList<NoticeDepartment>());

        if(!CollectionUtils.isEmpty(receives)){
            for (NoticeDepartmentReceiver receiver : receives){
                logger.debug("Notice receiver, departmentId={}, userIds={}, all={}",
                        receiver.getDepartmentId(),
                        receiver.getMemberUserIds(),
                        receiver.isAll());

                NoticeDepartment department = new NoticeDepartment.Builder()
                        .departmentId(receiver.getDepartmentId())
                        .memberUserIds(receiver.getMemberUserIds())
                        .all(receiver.isAll())
                        .build();

                requestBuilder.noticeDepartments.add(department);
            }
        }

        SendNoticeRequest request = requestBuilder.build();

        httpClient.sendRequest(RequestUrl.SEND_NOTICE, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                SendNoticeResponse response = SerializeUtils.parseFrom(data, SendNoticeResponse.class);

                logger.debug("Send notice success, server return id: {}", response.noticeId);

                Notice notice = new Notice();
                notice.setContent(content);
                notice.setNoticeId(response.noticeId);
                if(loginManager.getCurrentUser() != null) {
                    notice.setSenderUserId(loginManager.getCurrentUser().getUserId());
                    notice.setSenderName(loginManager.getCurrentUser().getUsername());
                }

                JSONArray jsonAttachments = new JSONArray();
                if(!CollectionUtils.isEmpty(attachments)) {
                    for (Attachment attachment : attachments) {
                        try {
                            JSONObject jsonAttach = new JSONObject();
                            jsonAttach.put("url", attachment.getFileUrl());
                            jsonAttach.put("type", attachment.getType());

                            jsonAttachments.put(jsonAttach);
                        } catch (JSONException e) {
                            logger.error("Json Exception", e);
                        }
                    }
                }
                notice.setAttachments(jsonAttachments.toString());

                notice.setSummary(content.length() > 10 ? content.substring(0, 10) : content);
                notice.setMailbox(Constants.MAILBOX_OUTBOX);

                EventBus.getDefault().post(new NoticeEvent(
                        NoticeEvent.EventType.NOTICE_SEND_SUCCESS,
                        null,
                        null,
                        false,
                        response.bonus));

                UserDbHelper.getInstance().updateNotice(notice);
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new NoticeEvent(
                        NoticeEvent.EventType.NOTICE_SEND_FAILED,
                        cause.getMessage(),
                        null,
                        false));
            }
        });
    }

    @Override public void getNoticeReceiverSummary(long noticeId){
        logger.debug("NoticeManager::getNoticeReceiverSummary(), noticeId={}", noticeId);


        FetchNoticeDepartmentsRequest request = new FetchNoticeDepartmentsRequest.Builder()
                .noticeId(noticeId)
                .build();

        httpClient.sendRequest(RequestUrl.GET_NOTICE_RECEIVER_LIST, request.toByteArray(), new RequestCallback() {

            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchNoticeDepartmentsResponse response = SerializeUtils.parseFrom(data,
                        FetchNoticeDepartmentsResponse.class);

                List<NoticeDepartmentReceiver> receivers = new ArrayList<>();

                for(NoticeDepartment noticeDepartment : response.noticeDepartments){
                    logger.debug("Department Id: {}", noticeDepartment.departmentId);
                    logger.debug("status: {}/{}", noticeDepartment.readedCount,
                            noticeDepartment.memberCount);

                    NoticeDepartmentReceiver receiver = new NoticeDepartmentReceiver();
                    Department department = UserDbHelper.getInstance().getDepartment(
                            noticeDepartment.departmentId);

                    receiver.setDepartmentId(noticeDepartment.departmentId);
                    receiver.setMemberCount(noticeDepartment.memberCount);
                    receiver.setReadCount(noticeDepartment.readedCount);
                    receiver.setDepartmentName(department == null ? "" : department.getName());

                    receivers.add(receiver);
                }

                EventBus.getDefault().post(new NoticeReceiverEvent(
                        NoticeReceiverEvent.EventType.NOTICE_RECEIVER_GET_SUMMARY_SUCCESS,
                        null,
                        receivers));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new NoticeReceiverEvent(
                        NoticeReceiverEvent.EventType.NOTICE_RECEIVER_FAILED,
                        cause.getMessage(),
                        null));
            }
        });
    }

    @Override public void getNoticeReceiverDetail(final long noticeId, long departmentId){
        logger.debug("NoticeManager::getNoticeReceiverDetail(), noticeId={}, departmentId={}", noticeId, departmentId);


        FetchNoticeMembersRequest request = new FetchNoticeMembersRequest.Builder()
                .noticeId(noticeId)
                .departmentId(departmentId)
                .build();

        httpClient.sendRequest(RequestUrl.GET_NOTICE_RECEIVER_STATUS, request.toByteArray(), new RequestCallback() {

            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchNoticeMembersResponse response = SerializeUtils.parseFrom(data,
                        FetchNoticeMembersResponse.class);

                List<NoticeUserReceiver> receivers = new ArrayList<>();

                for(NoticeMember noticeMember : response.noticeMembers){
                    NoticeUserReceiver receiver = new NoticeUserReceiver();

                    receiver.setUserId(noticeMember.userId);
                    receiver.setUserName(noticeMember.nickname);
                    receiver.setRead(noticeMember.isRead);
                    receiver.setAvatar(noticeMember.avatar);

                    receivers.add(receiver);
                }

                EventBus.getDefault().post(new NoticeReceiverEvent(
                        NoticeReceiverEvent.EventType.NOTICE_RECEIVER_GET_DETAIL_SUCCESS,
                        null,
                        receivers));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new NoticeReceiverEvent(
                        NoticeReceiverEvent.EventType.NOTICE_RECEIVER_FAILED,
                        cause.getMessage(),
                        null));
            }
        });
    }

    @Override public void getNotice(long noticeId){
        logger.debug("NoticeManager::getNotice(), noticeId={}", noticeId);


        List<Notice> notices = UserDbHelper.getInstance().getNoticeById(noticeId);

        if(CollectionUtils.isEmpty(notices)){
            logger.warn("No notice found by Id {}", noticeId);
        }

        EventBus.getDefault().post(new NoticeEvent(
                NoticeEvent.EventType.NOTICE_QUERY_BY_ID,
                null,
                notices,
                false));
    }

    @Override public void markAsRead(final long noticeId){
        logger.debug("NoticeManager::markAsRead(), noticeId={}", noticeId);

        ReadNoticeRequest request = new ReadNoticeRequest.Builder()
                .noticeId(noticeId)
                .build();

        httpClient.sendRequest(RequestUrl.MARK_NOTICE_READ, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                logger.debug("Mark notice {} read successful.", noticeId);

                List<Notice> noticeList = UserDbHelper.getInstance().getNoticeById(noticeId);

                if(noticeList.size() > 0){
                    Notice notice = noticeList.get(0);
                    notice.setUnread(false);
                    UserDbHelper.getInstance().updateNotice(notice);
                }else{
                    logger.warn("Cannot find notice {} in local database.", noticeId);
                }
            }

            @Override
            public void onFailure(Throwable cause) {
                logger.error("Mark notice {} read failed.", noticeId);
            }
        });

        counterManager.decCounter(Constants.COUNTER.NOTICE);
    }

    @Override
    public Notice getLatestOneFromLocal() {
        if(latestNotice == null){
            latestNotice = UserDbHelper.getInstance().getLatestOneNotice(Constants.MAILBOX_INBOX);
        }
        return latestNotice;
    }

    @Override
    public void clearNotice(long maxNoticeId, final int mailbox) {
        logger.debug("NoticeManager::clearNotice(), maxNoticeId={}, mailbox={}", maxNoticeId, mailbox);


        if(maxNoticeId <= 0){
            UserDbHelper.getInstance().deleteNotice(mailbox);

            EventBus.getDefault().post(new NoticeEvent(
                        NoticeEvent.EventType.NOTICE_CLEAR_SUCCESS,
                    null,
                    null,
                    false
            ));

            return;
        }

        ClearNoticeRequest request = new ClearNoticeRequest.Builder()
                .maxId(maxNoticeId)
                .isInbox(mailbox == Constants.MAILBOX_INBOX)
                .build();

        httpClient.sendRequest(RequestUrl.CLEAR_NOTICE, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws  IOException {
                UserDbHelper.getInstance().deleteNotice(mailbox);

                EventBus.getDefault().post(new NoticeEvent(
                        NoticeEvent.EventType.NOTICE_CLEAR_SUCCESS,
                        null,
                        null,
                        false
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new NoticeEvent(
                        NoticeEvent.EventType.NOTICE_CLEAR_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });
    }
}
