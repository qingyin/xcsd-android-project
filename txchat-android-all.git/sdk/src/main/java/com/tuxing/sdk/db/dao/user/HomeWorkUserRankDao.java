package com.tuxing.sdk.db.dao.user;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.tuxing.sdk.db.entity.HomeWorkUserRank;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table HomeWorkUserRank.
*/
public class HomeWorkUserRankDao extends AbstractDao<HomeWorkUserRank, Long> {

    public static final String TABLENAME = "HomeWorkUserRank";

    /**
     * Properties of entity HomeWorkUserRank.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Rank = new Property(1, Integer.class, "rank", false, "RANK");
        public final static Property UserId = new Property(2, Long.class, "userId", false, "USER_ID");
        public final static Property Name = new Property(3, String.class, "name", false, "NAME");
        public final static Property Avatar = new Property(4, String.class, "avatar", false, "AVATAR");
        public final static Property Score = new Property(5, Integer.class, "score", false, "SCORE");
    };


    public HomeWorkUserRankDao(DaoConfig config) {
        super(config);
    }
    
    public HomeWorkUserRankDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'HomeWorkUserRank' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'RANK' INTEGER," + // 1: rank
                "'USER_ID' INTEGER," + // 2: userId
                "'NAME' TEXT," + // 3: name
                "'AVATAR' TEXT," + // 4: avatar
                "'SCORE' INTEGER);"); // 5: score
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'HomeWorkUserRank'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, HomeWorkUserRank entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Integer rank = entity.getRank();
        if (rank != null) {
            stmt.bindLong(2, rank);
        }
 
        Long userId = entity.getUserId();
        if (userId != null) {
            stmt.bindLong(3, userId);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(4, name);
        }
 
        String avatar = entity.getAvatar();
        if (avatar != null) {
            stmt.bindString(5, avatar);
        }
 
        Integer score = entity.getScore();
        if (score != null) {
            stmt.bindLong(6, score);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public HomeWorkUserRank readEntity(Cursor cursor, int offset) {
        HomeWorkUserRank entity = new HomeWorkUserRank( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // rank
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // userId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // name
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // avatar
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5) // score
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, HomeWorkUserRank entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setRank(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setUserId(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setAvatar(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setScore(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(HomeWorkUserRank entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(HomeWorkUserRank entity) {
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