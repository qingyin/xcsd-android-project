package com.tuxing.sdk.db.dao.user;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.tuxing.sdk.db.entity.CourseBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table CourseBean.
*/
public class CourseDao extends AbstractDao<CourseBean, Long> {

    public static final String TABLENAME = "CourseBean";

    /**
     * Properties of entity CourseBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property CourseId = new Property(1, Long.class, "CourseId", false, "COURSE_ID");
        public final static Property CreateOn = new Property(2, Long.class, "createOn", false, "CREATE_ON");
        public final static Property UpdateOn = new Property(3, Long.class, "updateOn", false, "UPDATE_ON");
        public final static Property TeacherId = new Property(4, Long.class, "teacherId", false, "TEACHER_ID");
        public final static Property TeacherName = new Property(5, String.class, "teacherName", false, "TEACHER_NAME");
        public final static Property LabelId = new Property(6, Long.class, "labelId", false, "LABEL_ID");
        public final static Property LabelName = new Property(7, String.class, "labelName", false, "LABEL_NAME");
        public final static Property Type = new Property(8, Integer.class, "type", false, "TYPE");
        public final static Property Title = new Property(9, String.class, "title", false, "TITLE");
        public final static Property Description = new Property(10, String.class, "description", false, "DESCRIPTION");
        public final static Property Cover = new Property(11, String.class, "cover", false, "COVER");
        public final static Property Status = new Property(12, Integer.class, "status", false, "STATUS");
        public final static Property Hits = new Property(13, Long.class, "hits", false, "HITS");
        public final static Property Score = new Property(14, Double.class, "score", false, "SCORE");
        public final static Property ScoreCnt = new Property(15, Long.class, "scoreCnt", false, "SCORE_CNT");
        public final static Property StartOn = new Property(16, Long.class, "startOn", false, "START_ON");
        public final static Property EndOn = new Property(17, Long.class, "endOn", false, "END_ON");
        public final static Property TeacherDesc = new Property(18, String.class, "teacherDesc", false, "TEACHER_DESC");
        public final static Property TeacherAvatar = new Property(19, String.class, "teacherAvatar", false, "TEACHER_AVATAR");
    };


    public CourseDao(DaoConfig config) {
        super(config);
    }
    
    public CourseDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'CourseBean' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'COURSE_ID' INTEGER," + // 1: CourseId
                "'CREATE_ON' INTEGER," + // 2: createOn
                "'UPDATE_ON' INTEGER," + // 3: updateOn
                "'TEACHER_ID' INTEGER," + // 4: teacherId
                "'TEACHER_NAME' TEXT," + // 5: teacherName
                "'LABEL_ID' INTEGER," + // 6: labelId
                "'LABEL_NAME' TEXT," + // 7: labelName
                "'TYPE' INTEGER," + // 8: type
                "'TITLE' TEXT," + // 9: title
                "'DESCRIPTION' TEXT," + // 10: description
                "'COVER' TEXT," + // 11: cover
                "'STATUS' INTEGER," + // 12: status
                "'HITS' INTEGER," + // 13: hits
                "'SCORE' REAL," + // 14: score
                "'SCORE_CNT' INTEGER," + // 15: scoreCnt
                "'START_ON' INTEGER," + // 16: startOn
                "'END_ON' INTEGER," + // 17: endOn
                "'TEACHER_DESC' TEXT," + // 18: teacherDesc
                "'TEACHER_AVATAR' TEXT);"); // 19: teacherAvatar
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'CourseBean'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, CourseBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long CourseId = entity.getCourseId();
        if (CourseId != null) {
            stmt.bindLong(2, CourseId);
        }
 
        Long createOn = entity.getCreateOn();
        if (createOn != null) {
            stmt.bindLong(3, createOn);
        }
 
        Long updateOn = entity.getUpdateOn();
        if (updateOn != null) {
            stmt.bindLong(4, updateOn);
        }
 
        Long teacherId = entity.getTeacherId();
        if (teacherId != null) {
            stmt.bindLong(5, teacherId);
        }
 
        String teacherName = entity.getTeacherName();
        if (teacherName != null) {
            stmt.bindString(6, teacherName);
        }
 
        Long labelId = entity.getLabelId();
        if (labelId != null) {
            stmt.bindLong(7, labelId);
        }
 
        String labelName = entity.getLabelName();
        if (labelName != null) {
            stmt.bindString(8, labelName);
        }
 
        Integer type = entity.getType();
        if (type != null) {
            stmt.bindLong(9, type);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(10, title);
        }
 
        String description = entity.getDescription();
        if (description != null) {
            stmt.bindString(11, description);
        }
 
        String cover = entity.getCover();
        if (cover != null) {
            stmt.bindString(12, cover);
        }
 
        Integer status = entity.getStatus();
        if (status != null) {
            stmt.bindLong(13, status);
        }
 
        Long hits = entity.getHits();
        if (hits != null) {
            stmt.bindLong(14, hits);
        }
 
        Double score = entity.getScore();
        if (score != null) {
            stmt.bindDouble(15, score);
        }
 
        Long scoreCnt = entity.getScoreCnt();
        if (scoreCnt != null) {
            stmt.bindLong(16, scoreCnt);
        }
 
        Long startOn = entity.getStartOn();
        if (startOn != null) {
            stmt.bindLong(17, startOn);
        }
 
        Long endOn = entity.getEndOn();
        if (endOn != null) {
            stmt.bindLong(18, endOn);
        }
 
        String teacherDesc = entity.getTeacherDesc();
        if (teacherDesc != null) {
            stmt.bindString(19, teacherDesc);
        }
 
        String teacherAvatar = entity.getTeacherAvatar();
        if (teacherAvatar != null) {
            stmt.bindString(20, teacherAvatar);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public CourseBean readEntity(Cursor cursor, int offset) {
        CourseBean entity = new CourseBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // CourseId
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // createOn
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // updateOn
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4), // teacherId
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // teacherName
            cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6), // labelId
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // labelName
            cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8), // type
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // title
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // description
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // cover
            cursor.isNull(offset + 12) ? null : cursor.getInt(offset + 12), // status
            cursor.isNull(offset + 13) ? null : cursor.getLong(offset + 13), // hits
            cursor.isNull(offset + 14) ? null : cursor.getDouble(offset + 14), // score
            cursor.isNull(offset + 15) ? null : cursor.getLong(offset + 15), // scoreCnt
            cursor.isNull(offset + 16) ? null : cursor.getLong(offset + 16), // startOn
            cursor.isNull(offset + 17) ? null : cursor.getLong(offset + 17), // endOn
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18), // teacherDesc
            cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19) // teacherAvatar
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, CourseBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCourseId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setCreateOn(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setUpdateOn(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setTeacherId(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
        entity.setTeacherName(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setLabelId(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
        entity.setLabelName(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setType(cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8));
        entity.setTitle(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setDescription(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setCover(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setStatus(cursor.isNull(offset + 12) ? null : cursor.getInt(offset + 12));
        entity.setHits(cursor.isNull(offset + 13) ? null : cursor.getLong(offset + 13));
        entity.setScore(cursor.isNull(offset + 14) ? null : cursor.getDouble(offset + 14));
        entity.setScoreCnt(cursor.isNull(offset + 15) ? null : cursor.getLong(offset + 15));
        entity.setStartOn(cursor.isNull(offset + 16) ? null : cursor.getLong(offset + 16));
        entity.setEndOn(cursor.isNull(offset + 17) ? null : cursor.getLong(offset + 17));
        entity.setTeacherDesc(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
        entity.setTeacherAvatar(cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(CourseBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(CourseBean entity) {
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
