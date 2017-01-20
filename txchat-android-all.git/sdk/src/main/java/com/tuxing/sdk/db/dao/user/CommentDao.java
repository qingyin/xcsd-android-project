package com.tuxing.sdk.db.dao.user;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.tuxing.sdk.db.entity.Comment;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table comment.
*/
public class CommentDao extends AbstractDao<Comment, Long> {

    public static final String TABLENAME = "comment";

    /**
     * Properties of entity Comment.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property CommentId = new Property(1, long.class, "commentId", false, "COMMENT_ID");
        public final static Property CommentType = new Property(2, Integer.class, "commentType", false, "COMMENT_TYPE");
        public final static Property TopicId = new Property(3, long.class, "topicId", false, "TOPIC_ID");
        public final static Property TopicUserId = new Property(4, Long.class, "topicUserId", false, "TOPIC_USER_ID");
        public final static Property TargetType = new Property(5, Integer.class, "targetType", false, "TARGET_TYPE");
        public final static Property ReplayToUserId = new Property(6, Long.class, "replayToUserId", false, "REPLAY_TO_USER_ID");
        public final static Property ReplayToUserName = new Property(7, String.class, "replayToUserName", false, "REPLAY_TO_USER_NAME");
        public final static Property SenderId = new Property(8, Long.class, "senderId", false, "SENDER_ID");
        public final static Property SenderName = new Property(9, String.class, "senderName", false, "SENDER_NAME");
        public final static Property SenderAvatar = new Property(10, String.class, "senderAvatar", false, "SENDER_AVATAR");
        public final static Property SendTime = new Property(11, Long.class, "sendTime", false, "SEND_TIME");
        public final static Property Content = new Property(12, String.class, "content", false, "CONTENT");
    };


    public CommentDao(DaoConfig config) {
        super(config);
    }
    
    public CommentDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'comment' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'COMMENT_ID' INTEGER NOT NULL UNIQUE ," + // 1: commentId
                "'COMMENT_TYPE' INTEGER," + // 2: commentType
                "'TOPIC_ID' INTEGER NOT NULL ," + // 3: topicId
                "'TOPIC_USER_ID' INTEGER," + // 4: topicUserId
                "'TARGET_TYPE' INTEGER," + // 5: targetType
                "'REPLAY_TO_USER_ID' INTEGER," + // 6: replayToUserId
                "'REPLAY_TO_USER_NAME' TEXT," + // 7: replayToUserName
                "'SENDER_ID' INTEGER," + // 8: senderId
                "'SENDER_NAME' TEXT," + // 9: senderName
                "'SENDER_AVATAR' TEXT," + // 10: senderAvatar
                "'SEND_TIME' INTEGER," + // 11: sendTime
                "'CONTENT' TEXT);"); // 12: content
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'comment'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Comment entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getCommentId());
 
        Integer commentType = entity.getCommentType();
        if (commentType != null) {
            stmt.bindLong(3, commentType);
        }
        stmt.bindLong(4, entity.getTopicId());
 
        Long topicUserId = entity.getTopicUserId();
        if (topicUserId != null) {
            stmt.bindLong(5, topicUserId);
        }
 
        Integer targetType = entity.getTargetType();
        if (targetType != null) {
            stmt.bindLong(6, targetType);
        }
 
        Long replayToUserId = entity.getReplayToUserId();
        if (replayToUserId != null) {
            stmt.bindLong(7, replayToUserId);
        }
 
        String replayToUserName = entity.getReplayToUserName();
        if (replayToUserName != null) {
            stmt.bindString(8, replayToUserName);
        }
 
        Long senderId = entity.getSenderId();
        if (senderId != null) {
            stmt.bindLong(9, senderId);
        }
 
        String senderName = entity.getSenderName();
        if (senderName != null) {
            stmt.bindString(10, senderName);
        }
 
        String senderAvatar = entity.getSenderAvatar();
        if (senderAvatar != null) {
            stmt.bindString(11, senderAvatar);
        }
 
        Long sendTime = entity.getSendTime();
        if (sendTime != null) {
            stmt.bindLong(12, sendTime);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(13, content);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Comment readEntity(Cursor cursor, int offset) {
        Comment entity = new Comment( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // commentId
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // commentType
            cursor.getLong(offset + 3), // topicId
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4), // topicUserId
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // targetType
            cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6), // replayToUserId
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // replayToUserName
            cursor.isNull(offset + 8) ? null : cursor.getLong(offset + 8), // senderId
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // senderName
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // senderAvatar
            cursor.isNull(offset + 11) ? null : cursor.getLong(offset + 11), // sendTime
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12) // content
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Comment entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCommentId(cursor.getLong(offset + 1));
        entity.setCommentType(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setTopicId(cursor.getLong(offset + 3));
        entity.setTopicUserId(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
        entity.setTargetType(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setReplayToUserId(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
        entity.setReplayToUserName(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setSenderId(cursor.isNull(offset + 8) ? null : cursor.getLong(offset + 8));
        entity.setSenderName(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setSenderAvatar(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setSendTime(cursor.isNull(offset + 11) ? null : cursor.getLong(offset + 11));
        entity.setContent(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Comment entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Comment entity) {
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
