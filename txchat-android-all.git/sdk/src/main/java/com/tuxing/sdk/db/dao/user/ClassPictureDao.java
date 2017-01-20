package com.tuxing.sdk.db.dao.user;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.tuxing.sdk.db.entity.ClassPicture;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table class_picture.
*/
public class ClassPictureDao extends AbstractDao<ClassPicture, Long> {

    public static final String TABLENAME = "class_picture";

    /**
     * Properties of entity ClassPicture.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property PicId = new Property(1, long.class, "picId", false, "PIC_ID");
        public final static Property ClassId = new Property(2, Long.class, "classId", false, "CLASS_ID");
        public final static Property PicUrl = new Property(3, String.class, "picUrl", false, "PIC_URL");
        public final static Property CreatedOn = new Property(4, Long.class, "createdOn", false, "CREATED_ON");
    };


    public ClassPictureDao(DaoConfig config) {
        super(config);
    }
    
    public ClassPictureDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'class_picture' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'PIC_ID' INTEGER NOT NULL UNIQUE ," + // 1: picId
                "'CLASS_ID' INTEGER," + // 2: classId
                "'PIC_URL' TEXT," + // 3: picUrl
                "'CREATED_ON' INTEGER);"); // 4: createdOn
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'class_picture'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ClassPicture entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getPicId());
 
        Long classId = entity.getClassId();
        if (classId != null) {
            stmt.bindLong(3, classId);
        }
 
        String picUrl = entity.getPicUrl();
        if (picUrl != null) {
            stmt.bindString(4, picUrl);
        }
 
        Long createdOn = entity.getCreatedOn();
        if (createdOn != null) {
            stmt.bindLong(5, createdOn);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public ClassPicture readEntity(Cursor cursor, int offset) {
        ClassPicture entity = new ClassPicture( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // picId
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // classId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // picUrl
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4) // createdOn
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ClassPicture entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPicId(cursor.getLong(offset + 1));
        entity.setClassId(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setPicUrl(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setCreatedOn(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(ClassPicture entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(ClassPicture entity) {
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
