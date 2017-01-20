package com.tuxing.sdk.upload;

import com.tuxing.rpc.proto.ScanCodeCheckinRequest;
import com.tuxing.sdk.db.entity.AttendanceRecord;
import com.tuxing.sdk.http.HttpClient;
import com.tuxing.sdk.http.RequestUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by alan on 15/10/12.
 */
public class UploadCheckInQueue extends ConcurrentLinkedQueue<AttendanceRecord> {
    private static final Logger logger = LoggerFactory.getLogger(UploadCheckInQueue.class);

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private HttpClient httpClient = HttpClient.getInstance();

    private static UploadCheckInQueue instance;

    public static synchronized UploadCheckInQueue getInstance(UploadCallback callback){
        if(instance == null){
            instance = new UploadCheckInQueue(callback);
        }

        return instance;
    }

    private UploadCallback callback;

    private volatile boolean running = false;

    private UploadCheckInQueue(UploadCallback callback) {
        this.callback = callback;
    }

    @Override public boolean add(AttendanceRecord record) {
        boolean ret = super.add(record);

        onTaskSubmit();

        return ret;
    }


    private void onTaskSubmit() {
        if(running){
            return;
        }

        running = true;

        executor.execute(new Runnable() {
            @Override
            public void run() {
                AttendanceRecord record = poll();
                while (record != null) {
                    upload(record);
                    record = poll();

                }

                running = false;
            }
        });
    }

    @Override
    public boolean addAll(Collection<? extends AttendanceRecord> records){
        boolean ret = super.addAll(records);

        onTaskSubmit();

        return ret;
    }

    private void upload(AttendanceRecord record){
        ScanCodeCheckinRequest request = new ScanCodeCheckinRequest.Builder()
                .userId(record.getUserId())
                .cardCode(record.getCardNo())
                .checkinTime(System.currentTimeMillis())
                .build();

        try {
            httpClient.sendRequest(RequestUrl.SCAN_CODE_CHECK_IN, request.toByteArray());
            callback.onSuccess(record);
        }catch (Exception e){
            logger.error("Upload check in data failed.", e);
            callback.onFailed(record);
        }
    }

    public interface UploadCallback {
        void onSuccess(AttendanceRecord record);
        void onFailed(AttendanceRecord record);
    }
}
