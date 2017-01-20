package com.tuxing.sdk.manager;

import com.tuxing.sdk.db.entity.Notice;
import com.tuxing.sdk.modle.Attachment;
import com.tuxing.sdk.modle.NoticeDepartmentReceiver;
import com.tuxing.sdk.task.AsyncMethod;

import java.util.List;

/**
 * Created by Alan on 2015/6/25.
 */
public interface NoticeManager extends BaseManager {
    void getLatestNotice(int mailbox);

    @AsyncMethod
    void getLocalCachedNotice(int mailbox);

    void getHistoryNotice(long maxNoticeId, int mailbox);

    void sendNotice(String content, List<Attachment> attachments,
                    List<NoticeDepartmentReceiver> receives);

    void getNoticeReceiverSummary(long noticeId);

    void getNoticeReceiverDetail(long noticeId, long departmentId);

    @AsyncMethod
    void getNotice(long noticeId);

    @AsyncMethod
    void markAsRead(long noticeId);

    Notice getLatestOneFromLocal();

    void clearNotice(long maxNoticeId, int mailbox);

}
