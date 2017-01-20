package com.tuxing.sdk.db.helper;

import android.content.Context;
import com.tuxing.sdk.db.dao.upload.AttendanceRecordDao;
import com.tuxing.sdk.db.dao.upload.DaoMaster;
import com.tuxing.sdk.db.dao.upload.DaoSession;
import com.tuxing.sdk.db.entity.AttendanceRecord;
import com.tuxing.sdk.utils.Constants;

import java.util.List;

/**
 * Created by alan on 15/10/21.
 */
public class UploadDbHelper {
    private static UploadDbHelper instance = new UploadDbHelper();

    private DaoSession session;
    private String dbFile;

//    static{
//        QueryBuilder.LOG_SQL = true;
//        QueryBuilder.LOG_VALUES = true;
//    }


    private UploadDbHelper() {

    }

    public void init(Context context, long userId){
        if(context == null){
            throw new IllegalStateException("Cannot get the service context");
        }

        this.dbFile = String.format(Constants.UPLOAD_DB_FILE, userId);

        DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context, dbFile, null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());

        session = daoMaster.newSession();
    }

    public static UploadDbHelper getInstance(){
        return instance;
    }

    public String getDbFile() {
        return dbFile;
    }

    public List<AttendanceRecord> getRecordListByStatus(int status){
        return session.getAttendanceRecordDao().queryBuilder()
                .where(AttendanceRecordDao.Properties.Status.eq(status))
                .list();
    }

    public long saveRecord(AttendanceRecord record){
        return session.getAttendanceRecordDao().insertOrReplace(record);
    }

    public List<AttendanceRecord> getRecordList(long maxRecordId){
        return session.getAttendanceRecordDao().queryBuilder()
                .where(AttendanceRecordDao.Properties.Id.lt(maxRecordId))
                .limit(Constants.DEFAULT_LIST_COUNT + 1)
                .orderDesc(AttendanceRecordDao.Properties.Id)
                .list();

    }

    public void deleteRecordsByStatus(final int status){
        session.runInTx(new Runnable() {
            @Override
            public void run() {
                List<AttendanceRecord> recordList = session.getAttendanceRecordDao().queryBuilder()
                        .where(AttendanceRecordDao.Properties.Status.eq(status))
                        .list();

                for(AttendanceRecord record : recordList){
                    session.getAttendanceRecordDao().delete(record);
                }
            }
        });
    }

}
