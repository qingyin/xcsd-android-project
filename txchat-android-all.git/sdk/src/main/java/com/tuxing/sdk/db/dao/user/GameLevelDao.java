package com.tuxing.sdk.db.dao.user;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.tuxing.sdk.db.entity.GameLevel;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table GameLevel.
*/
public class GameLevelDao extends AbstractDao<GameLevel, Long> {

    public static final String TABLENAME = "GameLevel";

    /**
     * Properties of entity GameLevel.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property GameId = new Property(1, Long.class, "gameId", false, "GAME_ID");
        public final static Property Level = new Property(2, Integer.class, "level", false, "LEVEL");
        public final static Property GameName = new Property(3, String.class, "gameName", false, "GAME_NAME");
        public final static Property AbilityName = new Property(4, String.class, "abilityName", false, "ABILITY_NAME");
        public final static Property PicUrl = new Property(5, String.class, "picUrl", false, "PIC_URL");
        public final static Property Stars = new Property(6, Integer.class, "stars", false, "STARS");
        public final static Property Color = new Property(7, String.class, "color", false, "COLOR");
        public final static Property Choicelevel = new Property(8, String.class, "choicelevel", false, "CHOICELEVEL");
        public final static Property HasGuide = new Property(9, Boolean.class, "hasGuide", false, "HAS_GUIDE");
    };


    public GameLevelDao(DaoConfig config) {
        super(config);
    }
    
    public GameLevelDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'GameLevel' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'GAME_ID' INTEGER," + // 1: gameId
                "'LEVEL' INTEGER," + // 2: level
                "'GAME_NAME' TEXT," + // 3: gameName
                "'ABILITY_NAME' TEXT," + // 4: abilityName
                "'PIC_URL' TEXT," + // 5: picUrl
                "'STARS' INTEGER," + // 6: stars
                "'COLOR' TEXT," + // 7: color
                "'CHOICELEVEL' TEXT," + // 8: choicelevel
                "'HAS_GUIDE' INTEGER);"); // 9: hasGuide
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'GameLevel'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, GameLevel entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long gameId = entity.getGameId();
        if (gameId != null) {
            stmt.bindLong(2, gameId);
        }
 
        Integer level = entity.getLevel();
        if (level != null) {
            stmt.bindLong(3, level);
        }
 
        String gameName = entity.getGameName();
        if (gameName != null) {
            stmt.bindString(4, gameName);
        }
 
        String abilityName = entity.getAbilityName();
        if (abilityName != null) {
            stmt.bindString(5, abilityName);
        }
 
        String picUrl = entity.getPicUrl();
        if (picUrl != null) {
            stmt.bindString(6, picUrl);
        }
 
        Integer stars = entity.getStars();
        if (stars != null) {
            stmt.bindLong(7, stars);
        }
 
        String color = entity.getColor();
        if (color != null) {
            stmt.bindString(8, color);
        }
 
        String choicelevel = entity.getChoicelevel();
        if (choicelevel != null) {
            stmt.bindString(9, choicelevel);
        }
 
        Boolean hasGuide = entity.getHasGuide();
        if (hasGuide != null) {
            stmt.bindLong(10, hasGuide ? 1l: 0l);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public GameLevel readEntity(Cursor cursor, int offset) {
        GameLevel entity = new GameLevel( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // gameId
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // level
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // gameName
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // abilityName
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // picUrl
            cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6), // stars
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // color
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // choicelevel
            cursor.isNull(offset + 9) ? null : cursor.getShort(offset + 9) != 0 // hasGuide
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, GameLevel entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setGameId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setLevel(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setGameName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setAbilityName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setPicUrl(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setStars(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
        entity.setColor(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setChoicelevel(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setHasGuide(cursor.isNull(offset + 9) ? null : cursor.getShort(offset + 9) != 0);
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(GameLevel entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(GameLevel entity) {
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