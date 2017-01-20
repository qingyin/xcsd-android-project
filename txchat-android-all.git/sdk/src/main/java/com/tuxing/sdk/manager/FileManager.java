package com.tuxing.sdk.manager;

import com.tuxing.sdk.modle.Attachment;
import com.tuxing.sdk.task.AsyncMethod;

import java.io.File;

/**
 * Created by Alan on 2015/6/25.
 */
public interface FileManager extends BaseManager {
    /***
     * 上传文件
     * 上传成功，触发事件UploadFileEvent.EventType.UPLOAD_COMPETED
     * 上传失败，触发事件UploadFileEvent.EventType.UPLOAD_FAILED
     * @param file        文件对象
     * @param fileType    文件类型，参考Constants.ATTACHMENT_TYPE
     */
    Attachment uploadFile(File file, int fileType);

    /***
     * 上传文件
     * 上传成功，触发事件UploadFileEvent.EventType.UPLOAD_COMPETED
     * 上传失败，触发事件UploadFileEvent.EventType.UPLOAD_FAILED
     * @param attachment 要长传的附件
     */
    void uploadFile(Attachment attachment);

    /***
     * 收集诊断文件
     * 完成后触发事件DebugFileEvent.EventType.COLLECT_EVENT_SUCCESS
     * 失败触发事件DebugFileEvent.EventType.COLLECT_EVENT_FAILED
     */

    @AsyncMethod
    void collectDebugFile();

    /***
     * 向服务器上报debug文件的key
     * 上报成功，触发事件DebugFileEvent.EventType.UPLOAD_INFO_SUCCESS
     * 上报失败，触发事件DebugFileEvent.EventType.UPLOAD_INFO_FAILED
     *
     * @param fileKey 文件key
     */
    public void uploadDebugFileInfo(String fileKey);
}
