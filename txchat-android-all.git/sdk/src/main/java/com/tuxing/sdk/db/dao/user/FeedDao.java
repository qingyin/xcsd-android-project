package com.tuxing.sdk.db.dao.user;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.tuxing.sdk.db.entity.Feed;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table feed.
*/
public class FeedDao extends AbstractDao<Feed, Long> {

    public static final String TABLENAME = "feed";

    /**
     * Properties of entity Feed.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property FeedId = new Property(1, long.class, "feedId", false, "FEED_ID");
        public final static Property Content = new Property(2, String.class, "content", false, "CONTENT");
        public final static Property Attachments = new Property(3, String.class, "attachments", false, "ATTACHMENTS");
        public final static Property UserId = new Property(4, Long.class, "userId", false, "USER_ID");
        public final static Property UserName = new Property(5, String.class, "userName", false, "USER_NAME");
        public final static Property UserAvatar = new Property(6, String.class, "userAvatar", false, "USER_AVATAR");
        public final static Property UserType = new Property(7, Integer.class, "userType", false, "USER_TYPE");
        public final static Property FeedType = new Property(8, Integer.class, "feedType", false, "FEED_TYPE");
        public final static Property PublishTime = new Property(9, Long.class, "publishTime", false, "PUBLISH_TIME");
        public final static Property HasMoreComment = new Property(10, Boolean.class, "hasMoreComment", false, "HAS_MORE_COMMENT");
    };


    public FeedDao(DaoConfig config) {
        super(config);
    }
    
    public FeedDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'feed' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'FEED_ID' INTEGER NOT NULL UNIQUE ," + // 1: feedId
                "'CONTENT' TEXT," + // 2: content
                "'ATTACHMENTS' TEXT," + // 3: attachments
                "'USER_ID' INTEGER," + // 4: userId
                "'USER_NAME' TEXT," + // 5: userName
                "'USER_AVATAR' TEXT," + // 6: userAvatar
                "'USER_TYPE' INTEGER," + // 7: userType
                "'FEED_TYPE' INTEGER," + // 8: feedType
                "'PUBLISH_TIME' INTEGER," + // 9: publishTime
                "'HAS_MORE_COMMENT' INTEGER);"); // 10: hasMoreComment
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'feed'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Feed entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getFeedId());
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(3, content);
        }
 
        String attachments = entity.getAttachments();
        if (attachments != null) {
            stmt.bindString(4, attachments);
        }
 
        Long userId = entity.getUserId();
        if (userId != null) {
            stmt.bindLong(5, userId);
        }
 
        String userName = entity.getUserName();
        if (userName != null) {
            stmt.bindString(6, userName);
        }
 
        String userAvatar = entity.getUserAvatar();
        if (userAvatar != null) {
            stmt.bindString(7, userAvatar);
        }
 
        Integer userType = entity.getUserType();
        if (userType != null) {
            stmt.bindLong(8, userType);
        }
 
        Integer feedType = entity.getFeedType();
        if (feedType != null) {
            stmt.bindLong(9, feedType);
        }
 
        Long publishTime = entity.getPublishTime();
        if (publishTime != null) {
            stmt.bindLong(10, publishTime);
        }
 
        Boolean hasMoreComment = entity.getHasMoreComment();
        if (hasMoreComment != null) {
            stmt.bindLong(11, hasMoreComment ? 1l: 0l);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Feed readEntity(Cursor cursor, int offset) {
        Feed entity = new Feed( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // feedId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // content
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // attachments
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4), // userId
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // userName
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // userAvatar
            cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7), // userType
            cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8), // feedType
            cursor.isNull(offset + 9) ? null : cursor.getLong(offset + 9), // publishTime
            cursor.isNull(offset + 10) ? null : cursor.getShort(offset + 10) != 0 // hasMoreComment
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Feed entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setFeedId(cursor.getLong(offset + 1));
        entity.setContent(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setAttachments(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setUserId(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
        entity.setUserName(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setUserAvatar(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setUserType(cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7));
        entity.setFeedType(cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8));
        entity.setPublishTime(cursor.isNull(offset + 9) ? null : cursor.getLong(offset + 9));
        entity.setHasMoreComment(cursor.isNull(offset + 10) ? null : cursor.getShort(offset + 10) != 0);
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Feed entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Feed entity) {
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
