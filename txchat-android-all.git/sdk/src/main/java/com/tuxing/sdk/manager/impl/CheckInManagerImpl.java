package com.tuxing.sdk.manager.impl;

import android.content.Context;
import com.tuxing.rpc.proto.*;
import com.tuxing.sdk.db.entity.CheckInRecord;
import com.tuxing.sdk.db.helper.UserDbHelper;
import com.tuxing.sdk.event.CheckInCardEvent;
import com.tuxing.sdk.event.CheckInEvent;
import com.tuxing.sdk.event.LoginEvent;
import com.tuxing.sdk.http.HttpClient;
import com.tuxing.sdk.http.RequestCallback;
import com.tuxing.sdk.http.RequestUrl;
import com.tuxing.sdk.manager.CheckInManager;
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
import java.util.List;

/**
 * Created by Alan on 2015/6/11.
 */
public class CheckInManagerImpl implements CheckInManager {
    private final static Logger logger = LoggerFactory.getLogger(CheckInManager.class);
    private static CheckInManager instance;

    HttpClient httpClient = HttpClient.getInstance();

    CheckInRecord lastRecord;

    private Context context;

    private CheckInManagerImpl(){

    }

    public synchronized static CheckInManager getInstance(){
        if(instance == null){
            instance = new CheckInManagerImpl();
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
                this.lastRecord = null;
                break;
        }
    }

    @Override public void getLatestCheckInRecords(){
        logger.debug("CheckInManager::getLatestCheckInRecords()");

        FetchCheckinRequest request = new FetchCheckinRequest.Builder()
                .sinceId(0L)
                .maxId(Long.MAX_VALUE)
                .build();

        httpClient.sendRequest(RequestUrl.FETCH_CHECKIN_RECORD, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchCheckinResponse response = SerializeUtils.parseFrom(data, FetchCheckinResponse.class);

                List<CheckInRecord> checkInRecords = new ArrayList<CheckInRecord>();

                for (Checkin pbCheckIn : response.checkins) {

                    CheckInRecord checkInRecord = PbMsgUtils.transObj(pbCheckIn);

                    checkInRecords.add(checkInRecord);
                }

                if(!CollectionUtils.isEmpty(checkInRecords)){
                    lastRecord = checkInRecords.get(0);
                }

                EventBus.getDefault().post(new CheckInEvent(
                        CheckInEvent.EventType.CHECKIN_LATEST_RECORDS_SUCCESS,
                        null,
                        checkInRecords,
                        response.hasMore));

                UserDbHelper.getInstance().updateCheckInRecordList(checkInRecords);

            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new CheckInEvent(
                        CheckInEvent.EventType.CHECKIN_REQUEST_FAILED,
                        cause.getMessage(),
                        null,
                        false));
            }
        });
    }

    @Override public void getLocalCachedCheckInRecords(){
        logger.debug("Call CheckInManager::getLocalCachedCheckInRecords()");

        List<CheckInRecord> checkInRecords = UserDbHelper.getInstance().getLatestCheckInRecord();

        logger.debug("Local checkin list count: {}", checkInRecords.size());

        EventBus.getDefault().post(new CheckInEvent(
                CheckInEvent.EventType.CHECKIN_LOAD_FROM_CACHE,
                null,
                checkInRecords,
                false));
    }

    @Override public void getHistoryCheckInRecords(long maxId){
        logger.debug("CheckInManager::getLocalCachedCheckInRecords(), maxId={}", maxId);

        FetchCheckinRequest request = new FetchCheckinRequest.Builder()
                .sinceId(0L)
                .maxId(maxId)
                .build();


        httpClient.sendRequest(RequestUrl.FETCH_CHECKIN_RECORD, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                FetchCheckinResponse response = SerializeUtils.parseFrom(data, FetchCheckinResponse.class);

                List<CheckInRecord> checkInRecords = new ArrayList<>();

                for (Checkin pbCheckIn : response.checkins){
                    CheckInRecord checkInRecord = PbMsgUtils.transObj(pbCheckIn);

                    checkInRecords.add(checkInRecord);
                }

                EventBus.getDefault().post(new CheckInEvent(
                        CheckInEvent.EventType.CHECKIN_REQUEST_SUCCESS,
                        null,
                        checkInRecords,
                        response.hasMore));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new CheckInEvent(
                        CheckInEvent.EventType.CHECKIN_REQUEST_FAILED,
                        cause.getMessage(),
                        null,
                        false));
            }
        });

    }

    @Override public void getCheckInRecord(long checkInRecordId){
        logger.debug("CheckInManager::getCheckInRecord(), checkInRecordId={}", checkInRecordId);

        List<CheckInRecord> checkInRecords = UserDbHelper.getInstance().getCheckInRecordById(checkInRecordId);

        if(CollectionUtils.isEmpty(checkInRecords)){
            logger.warn("No CheckIn record found by id {}", checkInRecordId);
        }

        EventBus.getDefault().post(new CheckInEvent(
                CheckInEvent.EventType.CHECKIN_QUERY_BY_ID,
                null,
                checkInRecords,
                false));
    }

    @Override public void bindCheckInCard(final String cardNum, final long childUserId){
        logger.debug("CheckInManager::bindCheckInCard(), cardNum={}, childUserId={}", cardNum, childUserId);

        BindCardRequest request = new BindCardRequest.Builder()
                .cardCode(cardNum)
                .userId(childUserId)
                .build();

        httpClient.sendRequest(RequestUrl.BIND_CARD, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                UserDbHelper.getInstance().saveSetting(String.format(
                        Constants.SETTING_FIELD.USER_CARD_NUM, childUserId), cardNum);

                EventBus.getDefault().post(new CheckInCardEvent(
                        CheckInCardEvent.EventType.CARD_BIND_SUCCESS,
                        null,
                        null));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new CheckInCardEvent(
                        CheckInCardEvent.EventType.CARD_BIND_FAILED,
                        cause.getMessage()
                        , null));
            }
        });
    }

    @Override
    public CheckInRecord getLatestOneFromLocal() {
        if(lastRecord == null){
            lastRecord = UserDbHelper.getInstance().getLatestOneCheckInRecord();
        }
        return lastRecord;
    }

    @Override
    public void clearCheckInRecord(long maxCheckInRecordId) {
        logger.debug("CheckInManager::clearCheckInRecord(), maxCheckInRecordId={}", maxCheckInRecordId);

        if(maxCheckInRecordId <= 0){
            UserDbHelper.getInstance().deleteAllCheckInRecords();

            EventBus.getDefault().post(new CheckInEvent(
                    CheckInEvent.EventType.CHECKIN_CLEAR_SUCCESS,
                    null,
                    null,
                    false
            ));

            return;
        }

        ClearCheckInRequest request = new ClearCheckInRequest.Builder()
                .maxId(maxCheckInRecordId)
                .build();

        httpClient.sendRequest(RequestUrl.CLEAR_CHECK_IN, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                UserDbHelper.getInstance().deleteAllCheckInRecords();

                EventBus.getDefault().post(new CheckInEvent(
                        CheckInEvent.EventType.CHECKIN_CLEAR_SUCCESS,
                        null,
                        null,
                        false
                ));

            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new CheckInEvent(
                        CheckInEvent.EventType.CHECKIN_CLEAR_FAILED,
                        cause.getMessage(),
                        null,
                        false
                ));
            }
        });

    }

}
