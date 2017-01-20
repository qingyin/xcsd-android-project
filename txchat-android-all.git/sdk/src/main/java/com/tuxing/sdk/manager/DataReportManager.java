package com.tuxing.sdk.manager;

import com.tuxing.sdk.task.AsyncMethod;
import com.xcsd.rpc.proto.EventType;

/**
 * Created by apple on 16/10/24.
 */
public interface DataReportManager extends BaseManager {

    @AsyncMethod
    void reportNow();

    @AsyncMethod
    void reportEvent(EventType eventType);

    @AsyncMethod
    void reportEventBid(EventType eventType,String bid);

    @AsyncMethod
    void reportGameData(EventType eventType,String bid,Long userId);

    @AsyncMethod
    void reportExtendedInfo(EventType eventType,String bid,String extendedInfo);


    @AsyncMethod
    void reportExtendedInfo(EventType eventType,String bid,String extendedInfo,Long userId);


    @AsyncMethod
    void noticeFinishLogin();


}
