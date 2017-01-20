package com.tuxing.sdk.db.dao.user;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.tuxing.sdk.db.entity.User;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table user.
*/
public class UserDao extends AbstractDao<User, Long> {

    public static final String TABLENAME = "user";

    /**
     * Properties of entity User.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserId = new Property(1, long.class, "userId", false, "USER_ID");
        public final static Property Username = new Property(2, String.class, "username", false, "USERNAME");
        public final static Property Type = new Property(3, int.class, "type", false, "TYPE");
        public final static Property Nickname = new Property(4, String.class, "nickname", false, "NICKNAME");
        public final static Property Realname = new Property(5, String.class, "realname", false, "REALNAME");
        public final static Property Mobile = new Property(6, String.class, "mobile", false, "MOBILE");
        public final static Property Birthday = new Property(7, Long.class, "birthday", false, "BIRTHDAY");
        public final static Property Address = new Property(8, String.class, "address", false, "ADDRESS");
        public final static Property Gender = new Property(9, Integer.class, "gender", false, "GENDER");
        public final static Property PositionId = new Property(10, Long.class, "positionId", false, "POSITION_ID");
        public final static Property PositionName = new Property(11, String.class, "positionName", false, "POSITION_NAME");
        public final static Property DisturbedFreeSetting = new Property(12, Integer.class, "disturbedFreeSetting", false, "DISTURBED_FREE_SETTING");
        public final static Property Avatar = new Property(13, String.class, "avatar", false, "AVATAR");
        public final static Property ChildUserId = new Property(14, Long.class, "childUserId", false, "CHILD_USER_ID");
        public final static Property Signature = new Property(15, String.class, "signature", false, "SIGNATURE");
        public final static Property ClassName = new Property(16, String.class, "className", false, "CLASS_NAME");
        public final static Property GardenName = new Property(17, String.class, "gardenName", false, "GARDEN_NAME");
        public final static Property RelativeType = new Property(18, Integer.class, "relativeType", false, "RELATIVE_TYPE");
        public final static Property GardenId = new Property(19, Long.class, "gardenId", false, "GARDEN_ID");
        public final static Property ClassId = new Property(20, Long.class, "classId", false, "CLASS_ID");
        public final static Property Guarder = new Property(21, String.class, "guarder", false, "GUARDER");
        public final static Property Activated = new Property(22, Boolean.class, "activated", false, "ACTIVATED");
        public final static Property CombinedNickname = new Property(23, String.class, "combinedNickname", false, "COMBINED_NICKNAME");
    };


    public UserDao(DaoConfig config) {
        super(config);
    }
    
    public UserDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'user' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'USER_ID' INTEGER NOT NULL UNIQUE ," + // 1: userId
                "'USERNAME' TEXT NOT NULL ," + // 2: username
                "'TYPE' INTEGER NOT NULL ," + // 3: type
                "'NICKNAME' TEXT," + // 4: nickname
                "'REALNAME' TEXT," + // 5: realname
                "'MOBILE' TEXT," + // 6: mobile
                "'BIRTHDAY' INTEGER," + // 7: birthday
                "'ADDRESS' TEXT," + // 8: address
                "'GENDER' INTEGER," + // 9: gender
                "'POSITION_ID' INTEGER," + // 10: positionId
                "'POSITION_NAME' TEXT," + // 11: positionName
                "'DISTURBED_FREE_SETTING' INTEGER," + // 12: disturbedFreeSetting
                "'AVATAR' TEXT," + // 13: avatar
                "'CHILD_USER_ID' INTEGER," + // 14: childUserId
                "'SIGNATURE' TEXT," + // 15: signature
                "'CLASS_NAME' TEXT," + // 16: className
                "'GARDEN_NAME' TEXT," + // 17: gardenName
                "'RELATIVE_TYPE' INTEGER," + // 18: relativeType
                "'GARDEN_ID' INTEGER," + // 19: gardenId
                "'CLASS_ID' INTEGER," + // 20: classId
                "'GUARDER' TEXT," + // 21: guarder
                "'ACTIVATED' INTEGER," + // 22: activated
                "'COMBINED_NICKNAME' TEXT);"); // 23: combinedNickname
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'user'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, User entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getUserId());
        stmt.bindString(3, entity.getUsername());
        stmt.bindLong(4, entity.getType());
 
        String nickname = entity.getNickname();
        if (nickname != null) {
            stmt.bindString(5, nickname);
        }
 
        String realname = entity.getRealname();
        if (realname != null) {
            stmt.bindString(6, realname);
        }
 
        String mobile = entity.getMobile();
        if (mobile != null) {
            stmt.bindString(7, mobile);
        }
 
        Long birthday = entity.getBirthday();
        if (birthday != null) {
            stmt.bindLong(8, birthday);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(9, address);
        }
 
        Integer gender = entity.getGender();
        if (gender != null) {
            stmt.bindLong(10, gender);
        }
 
        Long positionId = entity.getPositionId();
        if (positionId != null) {
            stmt.bindLong(11, positionId);
        }
 
        String positionName = entity.getPositionName();
        if (positionName != null) {
            stmt.bindString(12, positionName);
        }
 
        Integer disturbedFreeSetting = entity.getDisturbedFreeSetting();
        if (disturbedFreeSetting != null) {
            stmt.bindLong(13, disturbedFreeSetting);
        }
 
        String avatar = entity.getAvatar();
        if (avatar != null) {
            stmt.bindString(14, avatar);
        }
 
        Long childUserId = entity.getChildUserId();
        if (childUserId != null) {
            stmt.bindLong(15, childUserId);
        }
 
        String signature = entity.getSignature();
        if (signature != null) {
            stmt.bindString(16, signature);
        }
 
        String className = entity.getClassName();
        if (className != null) {
            stmt.bindString(17, className);
        }
 
        String gardenName = entity.getGardenName();
        if (gardenName != null) {
            stmt.bindString(18, gardenName);
        }
 
        Integer relativeType = entity.getRelativeType();
        if (relativeType != null) {
            stmt.bindLong(19, relativeType);
        }
 
        Long gardenId = entity.getGardenId();
        if (gardenId != null) {
            stmt.bindLong(20, gardenId);
        }
 
        Long classId = entity.getClassId();
        if (classId != null) {
            stmt.bindLong(21, classId);
        }
 
        String guarder = entity.getGuarder();
        if (guarder != null) {
            stmt.bindString(22, guarder);
        }
 
        Boolean activated = entity.getActivated();
        if (activated != null) {
            stmt.bindLong(23, activated ? 1l: 0l);
        }
 
        String combinedNickname = entity.getCombinedNickname();
        if (combinedNickname != null) {
            stmt.bindString(24, combinedNickname);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public User readEntity(Cursor cursor, int offset) {
        User entity = new User( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // userId
            cursor.getString(offset + 2), // username
            cursor.getInt(offset + 3), // type
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // nickname
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // realname
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // mobile
            cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7), // birthday
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // address
            cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9), // gender
            cursor.isNull(offset + 10) ? null : cursor.getLong(offset + 10), // positionId
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // positionName
            cursor.isNull(offset + 12) ? null : cursor.getInt(offset + 12), // disturbedFreeSetting
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // avatar
            cursor.isNull(offset + 14) ? null : cursor.getLong(offset + 14), // childUserId
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // signature
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), // className
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17), // gardenName
            cursor.isNull(offset + 18) ? null : cursor.getInt(offset + 18), // relativeType
            cursor.isNull(offset + 19) ? null : cursor.getLong(offset + 19), // gardenId
            cursor.isNull(offset + 20) ? null : cursor.getLong(offset + 20), // classId
            cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21), // guarder
            cursor.isNull(offset + 22) ? null : cursor.getShort(offset + 22) != 0, // activated
            cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23) // combinedNickname
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, User entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserId(cursor.getLong(offset + 1));
        entity.setUsername(cursor.getString(offset + 2));
        entity.setType(cursor.getInt(offset + 3));
        entity.setNickname(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setRealname(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setMobile(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setBirthday(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
        entity.setAddress(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setGender(cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9));
        entity.setPositionId(cursor.isNull(offset + 10) ? null : cursor.getLong(offset + 10));
        entity.setPositionName(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setDisturbedFreeSetting(cursor.isNull(offset + 12) ? null : cursor.getInt(offset + 12));
        entity.setAvatar(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setChildUserId(cursor.isNull(offset + 14) ? null : cursor.getLong(offset + 14));
        entity.setSignature(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setClassName(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setGardenName(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
        entity.setRelativeType(cursor.isNull(offset + 18) ? null : cursor.getInt(offset + 18));
        entity.setGardenId(cursor.isNull(offset + 19) ? null : cursor.getLong(offset + 19));
        entity.setClassId(cursor.isNull(offset + 20) ? null : cursor.getLong(offset + 20));
        entity.setGuarder(cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21));
        entity.setActivated(cursor.isNull(offset + 22) ? null : cursor.getShort(offset + 22) != 0);
        entity.setCombinedNickname(cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(User entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(User entity) {
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
