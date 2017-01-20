package com.tuxing.sdk.manager.impl;

import android.content.Context;
import android.os.Environment;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.*;
import com.tuxing.rpc.proto.CollectLogRequest;
import com.tuxing.rpc.proto.GetUploadinfoRequest;
import com.tuxing.rpc.proto.GetUploadinfoResponse;
import com.tuxing.sdk.db.helper.GlobalDbHelper;
import com.tuxing.sdk.db.helper.UserDbHelper;
import com.tuxing.sdk.event.DebugFileEvent;
import com.tuxing.sdk.event.UploadFileEvent;
import com.tuxing.sdk.http.HttpClient;
import com.tuxing.sdk.http.RequestCallback;
import com.tuxing.sdk.http.RequestUrl;
import com.tuxing.sdk.manager.FileManager;
import com.tuxing.sdk.manager.LoginManager;
import com.tuxing.sdk.modle.Attachment;
import com.tuxing.sdk.task.AsyncTaskProxyFactory;
import com.tuxing.sdk.utils.Constants;
import com.tuxing.sdk.utils.IOUtils;
import com.tuxing.sdk.utils.SerializeUtils;
import com.tuxing.sdk.utils.StringUtils;
import de.greenrobot.event.EventBus;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Alan on 2015/6/12.
 */
public class FileManagerImpl implements FileManager {
    private final static Logger logger = LoggerFactory.getLogger(FileManager.class);
    private static FileManager instance;

    HttpClient httpClient = HttpClient.getInstance();
    LoginManager loginManager = LoginManagerImpl.getInstance();

    UploadManager uploadManager;
    String storageToken = null;
    AtomicBoolean updatingToken = new AtomicBoolean(false);
    Queue<UploadTask> uploadTaskQueue = new ConcurrentLinkedQueue<>();
    private Context context;

    private FileManagerImpl(){
        Configuration configuration = new Configuration.Builder()
                .connectTimeout(60)
                .responseTimeout(60)
                .putThreshhold(1 * 1024 * 1024)
                .build();

        uploadManager = new UploadManager(configuration);
    }

    public synchronized static FileManager getInstance(){
        if(instance == null){
            instance = new FileManagerImpl();
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
    public Attachment uploadFile(File file, int fileType){
        logger.debug("FileManager::uploadFile(), file={}, fileType={}", file.getAbsoluteFile(), fileType);

        String fileKey = UUID.randomUUID().toString();
        logger.debug("fileKey={}", fileKey);

        final Attachment attachment = new Attachment();
        attachment.setFileUrl(fileKey);
        attachment.setType(fileType);
        attachment.setLocalFilePath(file.getAbsolutePath());
        attachment.setStatus(Constants.ATTACHMENT_STATUS.NOT_UPLOAD);
        attachment.setProgress(0);
        attachment.setIsUploadCancel(false);

        uploadFile(attachment);

        return attachment;
    }

    @Override public void uploadFile(final Attachment attachment) {
        logger.debug("FileManager::uploadFile(), filePath={}, fileKey={}",
                attachment.getLocalFilePath(),
                attachment.getFileUrl());

        if (updatingToken.get()) {
            uploadTaskQueue.offer(new UploadTask(attachment));
            return;
        }

        if (StringUtils.isBlank(storageToken)) {
            uploadTaskQueue.offer(new UploadTask(attachment));
            getUploadToken();
            return;
        }

        doUpload(attachment);
    }

    private void doUpload(final Attachment attachment){
        File file = new File(attachment.getLocalFilePath());

        if(!file.exists() || file.length() <= 0){
            attachment.setStatus(Constants.ATTACHMENT_STATUS.UPLOAD_FAILED);

            EventBus.getDefault().post(new UploadFileEvent(
                    UploadFileEvent.EventType.UPLOAD_FAILED,
                    "图片路径不存在",
                    attachment));
        }

        uploadManager.put(file, attachment.getFileUrl(), storageToken, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if(info.isOK()){
                    //Upload successful
                    attachment.setProgress(100);
                    attachment.setStatus(Constants.ATTACHMENT_STATUS.UPLOAD_COMPLETED);

                    EventBus.getDefault().post(new UploadFileEvent(
                            UploadFileEvent.EventType.UPLOAD_COMPETED,
                            null,
                            attachment));
                }else if(info.statusCode == ResponseInfo.InvalidToken || info.statusCode == 401){
                    //request storageToken
                    uploadTaskQueue.offer(new UploadTask(attachment));
                    getUploadToken();
                }else{
                    attachment.setStatus(Constants.ATTACHMENT_STATUS.UPLOAD_FAILED);
                    EventBus.getDefault().post(new UploadFileEvent(
                            UploadFileEvent.EventType.UPLOAD_FAILED,
                            info.error,
                            attachment));
                }

            }
        }, new UploadOptions(null, null, true, new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
                attachment.setStatus(Constants.ATTACHMENT_STATUS.UPLOAD_IN_PROGRESS);
                attachment.setProgress((int)Math.round(percent * 100));
                EventBus.getDefault().post(new UploadFileEvent(
                        UploadFileEvent.EventType.UPLOAD_PROGRESS_UPDATED,
                        null,
                        attachment));
            }
        }, new UpCancellationSignal() {
            @Override
            public boolean isCancelled() {
                return attachment.getIsUploadCancel();
            }
        }));
    }

    @Override
    public void collectDebugFile() {
        logger.debug("FileManager::collectDebugFile()");


        //1. mkdir
        File debugDir = new File(Constants.APP_ROOT_DIR,
                String.valueOf(loginManager.getCurrentUser().getUserId())
                        + "_" + String.valueOf(System.currentTimeMillis()));
        try {
            if (!debugDir.exists()) {
                debugDir.mkdirs();
            }

            logger.debug("Make debug dir {}", debugDir.getAbsoluteFile());

            //2. collect user db file
            File userDb = context.getDatabasePath(UserDbHelper.getInstance().getDbFile());
            IOUtils.copyFile(userDb, new File(debugDir, UserDbHelper.getInstance().getDbFile()));

            File globalDb = context.getDatabasePath(GlobalDbHelper.getInstance().getDbFile());
            IOUtils.copyFile(globalDb, new File(debugDir, GlobalDbHelper.getInstance().getDbFile()));

            if(loginManager.getCurrentUser() != null) {
                File dbEmmsg = context.getDatabasePath(loginManager.getCurrentUser().getUserId() + "_emmsg.db");
                if(dbEmmsg.exists()) {
                    IOUtils.copyFile(dbEmmsg, new File(debugDir, loginManager.getCurrentUser().getUserId() + "_emmsg.db"));
                }
            }

            //3. collect log files
            File logFile = new File(Constants.APP_ROOT_DIR, "logs");
            IOUtils.copyDir(logFile, new File(debugDir, "logs"));

            File emLogFile = new File(Environment.getExternalStorageDirectory(),
                    "Android/data/" + context.getPackageName() + "/tuxing#weijiayuan2/log");
            if(emLogFile.exists()){
                IOUtils.copyDir(emLogFile, new File(debugDir, "emlogs"));
            }


            //4. compress the debug file
            File zippedFile = new File(Constants.APP_ROOT_DIR, debugDir.getName() + ".zip");
            IOUtils.zipFile(debugDir, zippedFile);

            EventBus.getDefault().post(new DebugFileEvent(
                        DebugFileEvent.EventType.COLLECT_EVENT_SUCCESS,
                    null,
                    zippedFile.getPath()
            ));
        }catch (Exception e){
            logger.error("Collect debug file error.", e);

            EventBus.getDefault().post(new DebugFileEvent(
                        DebugFileEvent.EventType.COLLECT_EVENT_FAILED,
                    e.getMessage(),
                    null
            ));
        }finally {
            if(debugDir.exists()){
                //5. delete the temp file
                IOUtils.deleteFile(debugDir);
            }
        }
    }

    private void getUploadToken(){
        logger.debug("FileManager::getUploadToken()");

        if(!updatingToken.compareAndSet(false, true)){
            return;
        }

        GetUploadinfoRequest request = new GetUploadinfoRequest();

        httpClient.sendRequest(RequestUrl.GET_CLOUD_STORAGE_TOKEN, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                GetUploadinfoResponse response = SerializeUtils.parseFrom(data, GetUploadinfoResponse.class);

                storageToken = response.token;

                logger.debug("Get upload file storageToken {}", storageToken);

                updatingToken.set(false);

                Iterator<UploadTask> iterator = uploadTaskQueue.iterator();
                while(iterator.hasNext()){
                    iterator.next().upload();
                    iterator.remove();
                }
            }

            @Override
            public void onFailure(Throwable cause) {
                updatingToken.set(false);

                EventBus.getDefault().post(new UploadFileEvent(
                        UploadFileEvent.EventType.GET_UPLOAD_TOKEN_FAILED,
                        cause.getMessage(),
                        null));
            }
        });
    }

    @Override
    public void uploadDebugFileInfo(String fileKey){
        logger.debug("FileManager::uploadDebugFileInfo(), fileKey={}", fileKey);

        CollectLogRequest request = new CollectLogRequest.Builder()
                .content(fileKey)
                .build();

        httpClient.sendRequest(RequestUrl.UPLOAD_DEBUG_FILE_INFO, request.toByteArray(), new RequestCallback() {
            @Override
            public void onResponse(byte[] data) throws IOException {
                EventBus.getDefault().post(new DebugFileEvent(
                        DebugFileEvent.EventType.UPLOAD_INFO_SUCCESS,
                        null,
                        null
                ));
            }

            @Override
            public void onFailure(Throwable cause) {
                EventBus.getDefault().post(new DebugFileEvent(
                        DebugFileEvent.EventType.UPLOAD_INFO_FAILED,
                        cause.getMessage(),
                        null
                ));
            }
        });
    }

    class UploadTask{
        Attachment attachment;

        public UploadTask(Attachment attachment) {
            this.attachment = attachment;
        }

        public void upload(){
            uploadFile(attachment);
        }
    }
}
