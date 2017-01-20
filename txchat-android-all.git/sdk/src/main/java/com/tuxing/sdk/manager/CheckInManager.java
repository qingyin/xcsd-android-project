package com.tuxing.sdk.manager;

import com.tuxing.sdk.db.entity.CheckInRecord;
import com.tuxing.sdk.task.AsyncMethod;

/**
 * Created by Alan on 2015/6/25.
 */
public interface CheckInManager extends BaseManager {
    void getLatestCheckInRecords();

    @AsyncMethod
    void getLocalCachedCheckInRecords();

    @AsyncMethod
    void getHistoryCheckInRecords(long maxCheckInRecordId);

    @AsyncMethod
    void getCheckInRecord(long checkInRecordId);

    void bindCheckInCard(String cardNum, long childUserId);

    CheckInRecord getLatestOneFromLocal();

    void clearCheckInRecord(long maxCheckInRecordId);
}
