package com.tuxing.sdk.db.dao.global;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.tuxing.sdk.db.entity.LoginUser;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table login_user.
*/
public class LoginUserDao extends AbstractDao<LoginUser, Long> {

    public static final String TABLENAME = "login_user";

    /**
     * Properties of entity LoginUser.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Token = new Property(1, String.class, "token", false, "TOKEN");
        public final static Property UserId = new Property(2, long.class, "userId", false, "USER_ID");
        public final static Property Username = new Property(3, String.class, "username", false, "USERNAME");
        public final static Property Password = new Property(4, String.class, "password", false, "PASSWORD");
        public final static Property Active = new Property(5, boolean.class, "active", false, "ACTIVE");
        public final static Property Status = new Property(6, int.class, "status", false, "STATUS");
        public final static Property LastLoginTime = new Property(7, Long.class, "lastLoginTime", false, "LAST_LOGIN_TIME");
    };


    public LoginUserDao(DaoConfig config) {
        super(config);
    }
    
    public LoginUserDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'login_user' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'TOKEN' TEXT NOT NULL ," + // 1: token
                "'USER_ID' INTEGER NOT NULL UNIQUE ," + // 2: userId
                "'USERNAME' TEXT NOT NULL UNIQUE ," + // 3: username
                "'PASSWORD' TEXT," + // 4: password
                "'ACTIVE' INTEGER NOT NULL ," + // 5: active
                "'STATUS' INTEGER NOT NULL ," + // 6: status
                "'LAST_LOGIN_TIME' INTEGER);"); // 7: lastLoginTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'login_user'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, LoginUser entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getToken());
        stmt.bindLong(3, entity.getUserId());
        stmt.bindString(4, entity.getUsername());
 
        String password = entity.getPassword();
        if (password != null) {
            stmt.bindString(5, password);
        }
        stmt.bindLong(6, entity.getActive() ? 1l: 0l);
        stmt.bindLong(7, entity.getStatus());
 
        Long lastLoginTime = entity.getLastLoginTime();
        if (lastLoginTime != null) {
            stmt.bindLong(8, lastLoginTime);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public LoginUser readEntity(Cursor cursor, int offset) {
        LoginUser entity = new LoginUser( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // token
            cursor.getLong(offset + 2), // userId
            cursor.getString(offset + 3), // username
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // password
            cursor.getShort(offset + 5) != 0, // active
            cursor.getInt(offset + 6), // status
            cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7) // lastLoginTime
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, LoginUser entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setToken(cursor.getString(offset + 1));
        entity.setUserId(cursor.getLong(offset + 2));
        entity.setUsername(cursor.getString(offset + 3));
        entity.setPassword(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setActive(cursor.getShort(offset + 5) != 0);
        entity.setStatus(cursor.getInt(offset + 6));
        entity.setLastLoginTime(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(LoginUser entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(LoginUser entity) {
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