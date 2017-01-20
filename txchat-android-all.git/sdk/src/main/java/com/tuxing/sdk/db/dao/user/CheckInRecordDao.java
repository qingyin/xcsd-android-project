package com.tuxing.sdk.db.dao.user;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.tuxing.sdk.db.entity.CheckInRecord;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table check_in_record.
*/
public class CheckInRecordDao extends AbstractDao<CheckInRecord, Long> {

    public static final String TABLENAME = "check_in_record";

    /**
     * Properties of entity CheckInRecord.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property CheckInRecordId = new Property(1, long.class, "checkInRecordId", false, "CHECK_IN_RECORD_ID");
        public final static Property UserId = new Property(2, long.class, "userId", false, "USER_ID");
        public final static Property GardenId = new Property(3, Long.class, "gardenId", false, "GARDEN_ID");
        public final static Property UserName = new Property(4, String.class, "userName", false, "USER_NAME");
        public final static Property CheckInTime = new Property(5, Long.class, "checkInTime", false, "CHECK_IN_TIME");
        public final static Property State = new Property(6, Integer.class, "state", false, "STATE");
        public final static Property CardNum = new Property(7, String.class, "cardNum", false, "CARD_NUM");
        public final static Property Snapshots = new Property(8, String.class, "snapshots", false, "SNAPSHOTS");
        public final static Property ParentName = new Property(9, String.class, "parentName", false, "PARENT_NAME");
        public final static Property ClassName = new Property(10, String.class, "className", false, "CLASS_NAME");
    };


    public CheckInRecordDao(DaoConfig config) {
        super(config);
    }
    
    public CheckInRecordDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'check_in_record' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'CHECK_IN_RECORD_ID' INTEGER NOT NULL UNIQUE ," + // 1: checkInRecordId
                "'USER_ID' INTEGER NOT NULL ," + // 2: userId
                "'GARDEN_ID' INTEGER," + // 3: gardenId
                "'USER_NAME' TEXT," + // 4: userName
                "'CHECK_IN_TIME' INTEGER," + // 5: checkInTime
                "'STATE' INTEGER," + // 6: state
                "'CARD_NUM' TEXT," + // 7: cardNum
                "'SNAPSHOTS' TEXT," + // 8: snapshots
                "'PARENT_NAME' TEXT," + // 9: parentName
                "'CLASS_NAME' TEXT);"); // 10: className
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'check_in_record'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, CheckInRecord entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getCheckInRecordId());
        stmt.bindLong(3, entity.getUserId());
 
        Long gardenId = entity.getGardenId();
        if (gardenId != null) {
            stmt.bindLong(4, gardenId);
        }
 
        String userName = entity.getUserName();
        if (userName != null) {
            stmt.bindString(5, userName);
        }
 
        Long checkInTime = entity.getCheckInTime();
        if (checkInTime != null) {
            stmt.bindLong(6, checkInTime);
        }
 
        Integer state = entity.getState();
        if (state != null) {
            stmt.bindLong(7, state);
        }
 
        String cardNum = entity.getCardNum();
        if (cardNum != null) {
            stmt.bindString(8, cardNum);
        }
 
        String snapshots = entity.getSnapshots();
        if (snapshots != null) {
            stmt.bindString(9, snapshots);
        }
 
        String parentName = entity.getParentName();
        if (parentName != null) {
            stmt.bindString(10, parentName);
        }
 
        String className = entity.getClassName();
        if (className != null) {
            stmt.bindString(11, className);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public CheckInRecord readEntity(Cursor cursor, int offset) {
        CheckInRecord entity = new CheckInRecord( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // checkInRecordId
            cursor.getLong(offset + 2), // userId
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // gardenId
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // userName
            cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5), // checkInTime
            cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6), // state
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // cardNum
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // snapshots
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // parentName
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10) // className
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, CheckInRecord entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCheckInRecordId(cursor.getLong(offset + 1));
        entity.setUserId(cursor.getLong(offset + 2));
        entity.setGardenId(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setUserName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setCheckInTime(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
        entity.setState(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
        entity.setCardNum(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setSnapshots(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setParentName(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setClassName(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(CheckInRecord entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(CheckInRecord entity) {
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