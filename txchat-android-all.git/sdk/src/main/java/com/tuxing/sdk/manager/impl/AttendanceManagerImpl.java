package com.tuxing.sdk.manager.impl;

import android.content.Context;
import com.tuxing.sdk.db.entity.AttendanceRecord;
import com.tuxing.sdk.db.helper.UploadDbHelper;
import com.tuxing.sdk.event.LoginEvent;
import com.tuxing.sdk.event.UploadCheckInEvent;
import com.tuxing.sdk.http.HttpClient;
import com.tuxing.sdk.manager.AttendanceManager;
import com.tuxing.sdk.manager.LoginManager;
import com.tuxing.sdk.task.AsyncTaskProxyFactory;
import com.tuxing.sdk.upload.UploadCheckInQueue;
import com.tuxing.sdk.utils.Constants;
import de.greenrobot.event.EventBus;

import java.util.List;

/**
 * Created by alan on 15/10/22.
 */
public class AttendanceManagerImpl implements AttendanceManager {
    private static AttendanceManager instance;

    HttpClient httpClient = HttpClient.getInstance();
    LoginManager loginManager = LoginManagerImpl.getInstance();
    UploadCheckInQueue.UploadCallback callback = new UploadCheckInQueue.UploadCallback() {

        @Override
        public void onSuccess(AttendanceRecord record) {
            record.setStatus(Constants.CHECK_IN_STATE.UPLOADED);
            UploadDbHelper.getInstance().saveRecord(record);

            EventBus.getDefault().post(new UploadCheckInEvent(
                    UploadCheckInEvent.EventType.UPLOAD_CHECK_IN_SUCCESS,
                    null,
                    record
            ));
        }

        @Override
        public void onFailed(AttendanceRecord record) {
            record.setStatus(Constants.CHECK_IN_STATE.ERROR);
            UploadDbHelper.getInstance().saveRecord(record);

            EventBus.getDefault().post(new UploadCheckInEvent(
                    UploadCheckInEvent.EventType.UPLOAD_CHECK_IN_FAILED,
                    null,
                    record
            ));
        }
    };


    private Context context;

    private AttendanceManagerImpl(){
    }

    public synchronized static AttendanceManager getInstance(){
        if(instance == null){
            instance = new AttendanceManagerImpl();
            instance = AsyncTaskProxyFactory.getProxy(instance);
        }

        return instance;
    }

    @Override
    public void init(Context context) {
        this.context = context;
        EventBus.getDefault().register(this);

        if(loginManager.getCurrentUser() != null){
            UploadDbHelper.getInstance().init(context, loginManager.getCurrentUser().getUserId());

            submitSavedRecord();
            submitFailedRecord();
        }
    }

    @Override
    public void destroy() {
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    public void onEvent(LoginEvent event){
        switch(event.getEvent()){
            case LOGIN_SUCCESS:
                UploadDbHelper.getInstance().init(context, loginManager.getCurrentUser().getUserId());

                submitSavedRecord();
                submitFailedRecord();
                break;
        }
    }

    @Override
    public List<AttendanceRecord> getRecordList(long maxRecordId) {
        return UploadDbHelper.getInstance().getRecordList(maxRecordId);
    }

    @Override
    public void submitFailedRecord() {
        List<AttendanceRecord> recordList = UploadDbHelper.getInstance()
                .getRecordListByStatus(Constants.CHECK_IN_STATE.ERROR);

        for(AttendanceRecord record : recordList){
            submitRecord(record);
        }
    }

    @Override
    public void clearSuccessRecord() {
        UploadDbHelper.getInstance()
                .deleteRecordsByStatus(Constants.CHECK_IN_STATE.UPLOADED);

    }

    @Override
    public void submitRecord(AttendanceRecord record) {
        record.setStatus(Constants.CHECK_IN_STATE.SAVED);
        long recordId = UploadDbHelper.getInstance().saveRecord(record);
        record.setId(recordId);

        UploadCheckInQueue queue = UploadCheckInQueue.getInstance(callback);

        queue.add(record);
    }

    private void submitSavedRecord(){
        List<AttendanceRecord> recordList = UploadDbHelper.getInstance()
                .getRecordListByStatus(Constants.CHECK_IN_STATE.SAVED);

        for(AttendanceRecord record : recordList){
            submitRecord(record);
        }
    }

}
