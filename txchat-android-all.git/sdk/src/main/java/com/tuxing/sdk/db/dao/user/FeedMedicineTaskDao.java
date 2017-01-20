package com.tuxing.sdk.db.dao.user;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.tuxing.sdk.db.entity.FeedMedicineTask;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table feed_medicine_task.
*/
public class FeedMedicineTaskDao extends AbstractDao<FeedMedicineTask, Long> {

    public static final String TABLENAME = "feed_medicine_task";

    /**
     * Properties of entity FeedMedicineTask.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property TaskId = new Property(1, long.class, "taskId", false, "TASK_ID");
        public final static Property Description = new Property(2, String.class, "description", false, "DESCRIPTION");
        public final static Property BeginDate = new Property(3, Long.class, "beginDate", false, "BEGIN_DATE");
        public final static Property Attachments = new Property(4, String.class, "attachments", false, "ATTACHMENTS");
        public final static Property SenderId = new Property(5, Long.class, "senderId", false, "SENDER_ID");
        public final static Property SenderName = new Property(6, String.class, "senderName", false, "SENDER_NAME");
        public final static Property SenderAvatar = new Property(7, String.class, "senderAvatar", false, "SENDER_AVATAR");
        public final static Property ClassId = new Property(8, Long.class, "classId", false, "CLASS_ID");
        public final static Property ClassName = new Property(9, String.class, "className", false, "CLASS_NAME");
        public final static Property ClassAvatar = new Property(10, String.class, "classAvatar", false, "CLASS_AVATAR");
        public final static Property Updated = new Property(11, Boolean.class, "updated", false, "UPDATED");
        public final static Property Status = new Property(12, Integer.class, "status", false, "STATUS");
        public final static Property SendTime = new Property(13, Long.class, "sendTime", false, "SEND_TIME");
    };


    public FeedMedicineTaskDao(DaoConfig config) {
        super(config);
    }
    
    public FeedMedicineTaskDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'feed_medicine_task' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'TASK_ID' INTEGER NOT NULL UNIQUE ," + // 1: taskId
                "'DESCRIPTION' TEXT," + // 2: description
                "'BEGIN_DATE' INTEGER," + // 3: beginDate
                "'ATTACHMENTS' TEXT," + // 4: attachments
                "'SENDER_ID' INTEGER," + // 5: senderId
                "'SENDER_NAME' TEXT," + // 6: senderName
                "'SENDER_AVATAR' TEXT," + // 7: senderAvatar
                "'CLASS_ID' INTEGER," + // 8: classId
                "'CLASS_NAME' TEXT," + // 9: className
                "'CLASS_AVATAR' TEXT," + // 10: classAvatar
                "'UPDATED' INTEGER," + // 11: updated
                "'STATUS' INTEGER," + // 12: status
                "'SEND_TIME' INTEGER);"); // 13: sendTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'feed_medicine_task'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, FeedMedicineTask entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getTaskId());
 
        String description = entity.getDescription();
        if (description != null) {
            stmt.bindString(3, description);
        }
 
        Long beginDate = entity.getBeginDate();
        if (beginDate != null) {
            stmt.bindLong(4, beginDate);
        }
 
        String attachments = entity.getAttachments();
        if (attachments != null) {
            stmt.bindString(5, attachments);
        }
 
        Long senderId = entity.getSenderId();
        if (senderId != null) {
            stmt.bindLong(6, senderId);
        }
 
        String senderName = entity.getSenderName();
        if (senderName != null) {
            stmt.bindString(7, senderName);
        }
 
        String senderAvatar = entity.getSenderAvatar();
        if (senderAvatar != null) {
            stmt.bindString(8, senderAvatar);
        }
 
        Long classId = entity.getClassId();
        if (classId != null) {
            stmt.bindLong(9, classId);
        }
 
        String className = entity.getClassName();
        if (className != null) {
            stmt.bindString(10, className);
        }
 
        String classAvatar = entity.getClassAvatar();
        if (classAvatar != null) {
            stmt.bindString(11, classAvatar);
        }
 
        Boolean updated = entity.getUpdated();
        if (updated != null) {
            stmt.bindLong(12, updated ? 1l: 0l);
        }
 
        Integer status = entity.getStatus();
        if (status != null) {
            stmt.bindLong(13, status);
        }
 
        Long sendTime = entity.getSendTime();
        if (sendTime != null) {
            stmt.bindLong(14, sendTime);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public FeedMedicineTask readEntity(Cursor cursor, int offset) {
        FeedMedicineTask entity = new FeedMedicineTask( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // taskId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // description
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // beginDate
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // attachments
            cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5), // senderId
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // senderName
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // senderAvatar
            cursor.isNull(offset + 8) ? null : cursor.getLong(offset + 8), // classId
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // className
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // classAvatar
            cursor.isNull(offset + 11) ? null : cursor.getShort(offset + 11) != 0, // updated
            cursor.isNull(offset + 12) ? null : cursor.getInt(offset + 12), // status
            cursor.isNull(offset + 13) ? null : cursor.getLong(offset + 13) // sendTime
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, FeedMedicineTask entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTaskId(cursor.getLong(offset + 1));
        entity.setDescription(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setBeginDate(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setAttachments(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setSenderId(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
        entity.setSenderName(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setSenderAvatar(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setClassId(cursor.isNull(offset + 8) ? null : cursor.getLong(offset + 8));
        entity.setClassName(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setClassAvatar(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setUpdated(cursor.isNull(offset + 11) ? null : cursor.getShort(offset + 11) != 0);
        entity.setStatus(cursor.isNull(offset + 12) ? null : cursor.getInt(offset + 12));
        entity.setSendTime(cursor.isNull(offset + 13) ? null : cursor.getLong(offset + 13));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(FeedMedicineTask entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(FeedMedicineTask entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}