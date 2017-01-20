package com.tuxing.sdk.manager;

import com.tuxing.sdk.db.entity.AttendanceRecord;
import com.tuxing.sdk.task.AsyncMethod;

import java.util.List;

/**
 * Created by alan on 15/10/22.
 */
public interface AttendanceManager extends BaseManager{

    /***
     * 获取扫码记录的列表，每次最多20条
     * @param maxRecordId 最大ID，返回的记录都会比这个ID小
     * @return 扫码记录的列表
     */
    List<AttendanceRecord> getRecordList(long maxRecordId);

    /***
     * 重新上传失败的记录
     */
    @AsyncMethod
    void submitFailedRecord();

    /***
     * 清除已经上传成功的记录
     */
    @AsyncMethod
    void clearSuccessRecord();

    /***
     * 提交一个扫码记录
     * @param record 扫码记录
     */
    void submitRecord(AttendanceRecord record);
}
