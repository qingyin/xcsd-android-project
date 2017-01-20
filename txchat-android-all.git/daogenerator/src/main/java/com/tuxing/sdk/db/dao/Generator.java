package com.tuxing.sdk.db.dao;

import de.greenrobot.daogenerator.*;

/**
 * Created by Alan on 2015/5/17.
 */
public class Generator {
    private static String entityPath = "com.tuxing.sdk.db.entity";

    public static void main(String[] args) throws Exception {
        int globalDbVersion = 2;

        int userDbVersion = 8;

        int attendanceDbVersion = 1;

        Schema global = new Schema(globalDbVersion, "com.tuxing.sdk.db.dao.global");
        Schema user = new Schema(userDbVersion, "com.tuxing.sdk.db.dao.user");
        Schema upload = new Schema(attendanceDbVersion, "com.tuxing.sdk.db.dao.upload");

        global.enableKeepSectionsByDefault();
        user.enableKeepSectionsByDefault();
        upload.enableActiveEntitiesByDefault();

        addLoginUser(global);

        //addUserRelative(schema);
        addNotice(user);
        addDepartment(user);
        addDepartmentUser(user);
        addUser(user);
        addCheckInRecord(user);
        addSetting(user);
        addComment(user);
        addFeed(user);
        addFeedMedicineTask(user);
        addGardenMail(user);
        addContentItemGroup(user);
        addContentItem(user);
        addRevokedMessage(user);
        addClassPicture(user);
        addActivity(user);
        addLightApp(user);
        //addAttachment(schema);
        addHomeWorkRecord(user);
        addHomeWorkClass(user);
        addHomeWorkMember(user);
        addHomeWorkUserRank(user);
        addHomeWorkGenerate(user);
        addHomeWorkDetail(user);
        addGameLevel(user);
        addAbilityDetail(user);
        addAbilityPoint(user);
        addGameScore(user);
        addScoreShow(user);
        addTestList(user);
        addCourse(user);
        addCourseLesson(user);
        addDataReport(user);

        addAttendanceRecord(upload);

        /*=================================GLOBAL================================*/

        String path = "sdk/src/main/java/";
        new DaoGenerator().generateAll(global, path);
        new DaoGenerator().generateAll(user, path);
        new DaoGenerator().generateAll(upload, path);
    }


    private static void addLoginUser(Schema schema){
        Entity loginUser = schema.addEntity("LoginUser");

        loginUser.implementsSerializable();
        loginUser.setTableName("login_user");
        loginUser.setClassNameDao("LoginUserDao");
        loginUser.setJavaPackage(entityPath);

        loginUser.addIdProperty().autoincrement();
        loginUser.addStringProperty("token").notNull();
        loginUser.addLongProperty("userId").unique().notNull();
        loginUser.addStringProperty("username").unique().notNull();
        loginUser.addStringProperty("password");
        loginUser.addBooleanProperty("active").notNull();
        loginUser.addIntProperty("status").notNull();
        loginUser.addLongProperty("lastLoginTime");

        loginUser.setHasKeepSections(true);
    }

    /*=================================USER================================*/

    private static void addUserRelative(Schema schema){
        Entity relativeType = schema.addEntity("UserRelative");

        relativeType.implementsSerializable();
        relativeType.setTableName("user_relative");
        relativeType.setClassNameDao("UserRelativeDao");
        relativeType.setJavaPackage(entityPath);

        relativeType.addIdProperty().autoincrement();
        relativeType.addLongProperty("userId").notNull();
        relativeType.addStringProperty("relativeId").notNull();
        relativeType.addStringProperty("relativeName").notNull();

        relativeType.setHasKeepSections(true);
    }

    private static void addNotice(Schema schema){
        Entity notice = schema.addEntity("Notice");

        notice.implementsSerializable();
        notice.setTableName("notice");
        notice.setClassNameDao("NoticeDao");
        notice.setJavaPackage(entityPath);

        notice.addIdProperty().autoincrement();
        notice.addLongProperty("noticeId").unique().notNull();
        notice.addLongProperty("senderUserId");
        notice.addStringProperty("senderName");
        notice.addStringProperty("senderAvatar");
        notice.addStringProperty("summary");
        notice.addBooleanProperty("unread");
        notice.addStringProperty("content");
        notice.addStringProperty("attachments");
        notice.addLongProperty("sendTime");
        notice.addIntProperty("mailbox").notNull();

        notice.setHasKeepSections(true);
    }

    private static void addDepartment(Schema schema){
        Entity department = schema.addEntity("Department");

        department.implementsSerializable();
        department.setTableName("department");
        department.setClassNameDao("DepartmentDao");
        department.setJavaPackage(entityPath);

        department.addIdProperty().autoincrement();
        department.addLongProperty("departmentId").unique().notNull();
        department.addStringProperty("name").notNull();
        department.addStringProperty("avatar");
        department.addBooleanProperty("showParents");

        department.addLongProperty("parentId");
        department.addIntProperty("type");
        department.addStringProperty("chatGroupId");
        department.addLongProperty("lastSync");

        department.setHasKeepSections(true);
    }

    private static void addDepartmentUser(Schema schema){
        Entity departmentUser = schema.addEntity("DepartmentUser");

        departmentUser.implementsSerializable();
        departmentUser.setTableName("department_user");
        departmentUser.setClassNameDao("DepartmentUserDao");
        departmentUser.setJavaPackage(entityPath);

        departmentUser.addIdProperty().autoincrement();
        departmentUser.addLongProperty("userId").notNull();
        departmentUser.addLongProperty("departmentId").notNull();
        departmentUser.addIntProperty("role");
        departmentUser.addBooleanProperty("admin");
        departmentUser.addIntProperty("gagSetting");

        departmentUser.setHasKeepSections(true);

    }

    private static void addUser(Schema schema){
        Entity user = schema.addEntity("User");

        user.implementsSerializable();
        user.setTableName("user");
        user.setClassNameDao("UserDao");
        user.setJavaPackage(entityPath);

        user.addIdProperty().autoincrement();
        user.addLongProperty("userId").unique().notNull();
        user.addStringProperty("username").notNull();
        user.addIntProperty("type").notNull();
        user.addStringProperty("nickname");
        user.addStringProperty("realname");
        user.addStringProperty("mobile");
        user.addLongProperty("birthday");
        user.addStringProperty("address");
        user.addIntProperty("gender");
        user.addLongProperty("positionId");
        user.addStringProperty("positionName");
        user.addIntProperty("disturbedFreeSetting");
        user.addStringProperty("avatar");
        user.addLongProperty("childUserId");
        user.addStringProperty("signature");
        user.addStringProperty("className");
        user.addStringProperty("gardenName");
        user.addIntProperty("relativeType");
        user.addLongProperty("gardenId");
        user.addLongProperty("classId");
        user.addStringProperty("guarder");
        user.addBooleanProperty("activated");
        user.addStringProperty("combinedNickname");

        user.setHasKeepSections(true);
    }

    private static void addCheckInRecord(Schema schema){
        Entity checkInRecord = schema.addEntity("CheckInRecord");

        checkInRecord.implementsSerializable();
        checkInRecord.setTableName("check_in_record");
        checkInRecord.setClassNameDao("CheckInRecordDao");
        checkInRecord.setJavaPackage(entityPath);

        checkInRecord.addIdProperty().autoincrement();
        checkInRecord.addLongProperty("checkInRecordId").unique().notNull();
        checkInRecord.addLongProperty("userId").notNull();
        checkInRecord.addLongProperty("gardenId");
        checkInRecord.addStringProperty("userName");
        checkInRecord.addLongProperty("checkInTime");
        checkInRecord.addIntProperty("state");
        checkInRecord.addStringProperty("cardNum");
        checkInRecord.addStringProperty("snapshots");
        checkInRecord.addStringProperty("parentName");
        checkInRecord.addStringProperty("className");

        checkInRecord.setHasKeepSections(true);
    }

    private static void addRevokedMessage(Schema schema){
        Entity revokedMessage = schema.addEntity("RevokedMessage");

        revokedMessage.implementsSerializable();
        revokedMessage.setTableName("revoked_message");
        revokedMessage.setClassNameDao("RevokedMessageDao");
        revokedMessage.setJavaPackage(entityPath);

        revokedMessage.addIdProperty().autoincrement();
        revokedMessage.addStringProperty("msgId").unique();
        revokedMessage.addStringProperty("cmdMsgId");
        revokedMessage.addStringProperty("from");
        revokedMessage.addStringProperty("to");
        revokedMessage.addBooleanProperty("isGroup");

        revokedMessage.setHasKeepSections(true);
    }

    private static void addAttachment(Schema schema){
        Entity attachment = schema.addEntity("Attachment");

        attachment.implementsSerializable();
        attachment.setTableName("attachment");
        attachment.setClassNameDao("AttachmentDao");
        attachment.setJavaPackage(entityPath);

        attachment.addIdProperty().autoincrement();
        attachment.addStringProperty("fileKey").unique().notNull();
        attachment.addStringProperty("localFilePath");
        attachment.addIntProperty("type");
        attachment.addIntProperty("status");
        attachment.addIntProperty("progress");
        attachment.addIntProperty("usage");
        attachment.addIntProperty("belongTo");

        attachment.setHasKeepSections(true);
    }

    private static void addSetting(Schema schema){
        Entity setting = schema.addEntity("Setting");

        setting.implementsSerializable();
        setting.setTableName("setting");
        setting.setClassNameDao("SettingDao");
        setting.setJavaPackage(entityPath);

        setting.addIdProperty().autoincrement();
        setting.addStringProperty("field").unique().notNull();
        setting.addStringProperty("value");

        setting.setHasKeepSections(true);
    }

    private static void addContentItemGroup(Schema schema){
        Entity item = schema.addEntity("ContentItemGroup");

        item.implementsSerializable();
        item.setTableName("content_item_group");
        item.setClassNameDao("ContentItemGroupDao");
        item.setJavaPackage(entityPath);

        item.addIdProperty().autoincrement();
        item.addLongProperty("groupId").unique().notNull();

        item.setHasKeepSections(true);
    }

    private static void addContentItem(Schema schema){
        Entity item = schema.addEntity("ContentItem");

        item.implementsSerializable();
        item.setTableName("content_item");
        item.setClassNameDao("ContentItemDao");
        item.setJavaPackage(entityPath);

        item.addIdProperty().autoincrement();
        item.addLongProperty("itemId").unique().notNull();
        item.addLongProperty("groupId");
        item.addStringProperty("title");
        item.addStringProperty("summary");
        item.addStringProperty("coverImageUrl");
        item.addLongProperty("publishTime");
        item.addIntProperty("contentType");
        item.addStringProperty("postUrl");
        item.addStringProperty("content");

        item.setHasKeepSections(true);
    }

    private static void addGardenMail(Schema schema){
        Entity mail = schema.addEntity("GardenMail");

        mail.implementsSerializable();
        mail.setTableName("garden_mail");
        mail.setClassNameDao("GardenMailDao");
        mail.setJavaPackage(entityPath);

        mail.addIdProperty().autoincrement();
        mail.addLongProperty("mailId").unique().notNull();
        mail.addStringProperty("content");
        mail.addLongProperty("senderId");
        mail.addStringProperty("senderName");
        mail.addStringProperty("senderAvatar");
        mail.addLongProperty("gardenId");
        mail.addStringProperty("gardenName");
        mail.addStringProperty("gardenAvatar");
        mail.addBooleanProperty("anonymous");
        mail.addBooleanProperty("updated");
        mail.addIntProperty("status");
        mail.addLongProperty("sendTime");

        mail.setHasKeepSections(true);
    }

    private static void addFeedMedicineTask(Schema schema){
        Entity task = schema.addEntity("FeedMedicineTask");

        task.implementsSerializable();
        task.setTableName("feed_medicine_task");
        task.setClassNameDao("FeedMedicineTaskDao");
        task.setJavaPackage(entityPath);

        task.addIdProperty().autoincrement();
        task.addLongProperty("taskId").unique().notNull();
        task.addStringProperty("description");
        task.addLongProperty("beginDate");
        task.addStringProperty("attachments");
        task.addLongProperty("senderId");
        task.addStringProperty("senderName");
        task.addStringProperty("senderAvatar");
        task.addLongProperty("classId");
        task.addStringProperty("className");
        task.addStringProperty("classAvatar");
        task.addBooleanProperty("updated");
        task.addIntProperty("status");
        task.addLongProperty("sendTime");

        task.setHasKeepSections(true);

    }

    private static void addFeed(Schema schema){
        Entity feed = schema.addEntity("Feed");

        feed.implementsSerializable();
        feed.setTableName("feed");
        feed.setClassNameDao("FeedDao");
        feed.setJavaPackage(entityPath);

        feed.addIdProperty().autoincrement();
        feed.addLongProperty("feedId").unique().notNull();
        feed.addStringProperty("content");
        feed.addStringProperty("attachments");
        feed.addLongProperty("userId");
        feed.addStringProperty("userName");
        feed.addStringProperty("userAvatar");
        feed.addIntProperty("userType");
        feed.addIntProperty("feedType");
        feed.addLongProperty("publishTime");
        feed.addBooleanProperty("hasMoreComment");

        feed.setHasKeepSections(true);

    }

    private static void addComment(Schema schema){
        Entity comment = schema.addEntity("Comment");

        comment.implementsSerializable();
        comment.setTableName("comment");
        comment.setClassNameDao("CommentDao");
        comment.setJavaPackage(entityPath);

        comment.addIdProperty().autoincrement();
        comment.addLongProperty("commentId").unique().notNull();
        comment.addIntProperty("commentType");
        comment.addLongProperty("topicId").notNull();
        comment.addLongProperty("topicUserId");
        comment.addIntProperty("targetType");
        comment.addLongProperty("replayToUserId");
        comment.addStringProperty("replayToUserName");
        comment.addLongProperty("senderId");
        comment.addStringProperty("senderName");
        comment.addStringProperty("senderAvatar");
        comment.addLongProperty("sendTime");
        comment.addStringProperty("content");

        comment.setHasKeepSections(true);
    }

    private static void addClassPicture(Schema schema){
        Entity classPicture = schema.addEntity("ClassPicture");

        classPicture.implementsSerializable();
        classPicture.setTableName("class_picture");
        classPicture.setClassNameDao("ClassPictureDao");
        classPicture.setJavaPackage(entityPath);

        classPicture.addIdProperty().autoincrement();
        classPicture.addLongProperty("picId").unique().notNull();
        classPicture.addLongProperty("classId");
        classPicture.addStringProperty("picUrl");
        classPicture.addLongProperty("createdOn");

        classPicture.setHasKeepSections(true);

    }

    private static void addActivity(Schema schema){
        Entity activity = schema.addEntity("Activity");

        activity.implementsSerializable();
        activity.setTableName("activity");
        activity.setClassNameDao("ActivityDao");
        activity.setJavaPackage(entityPath);

        activity.addIdProperty().autoincrement();
        activity.addLongProperty("activityId").unique().notNull();
        activity.addStringProperty("title");
        activity.addStringProperty("attachments");
        activity.addStringProperty("senderName");
        activity.addStringProperty("senderAvatar");
        activity.addBooleanProperty("deleted");
        activity.addLongProperty("createdOn");

        activity.setHasKeepSections(true);

    }

    private static void addLightApp(Schema schema){
        Entity activity = schema.addEntity("LightApp");

        activity.implementsSerializable();
        activity.setTableName("light_app");
        activity.setClassNameDao("LightAppDao");
        activity.setJavaPackage(entityPath);

        activity.addIdProperty().autoincrement();
        activity.addStringProperty("name");
        activity.addStringProperty("moduleName");
        activity.addStringProperty("icon");
        activity.addIntProperty("type");
        activity.addStringProperty("url");
        activity.addStringProperty("counterName");
        activity.addIntProperty("counterType");
        activity.addBooleanProperty("isNew");
        activity.addIntProperty("showAt");
        activity.addLongProperty("createdOn");

        activity.setHasKeepSections(true);

    }

    /*====================================Upload======================================*/

    private static void addAttendanceRecord(Schema schema){
        Entity record = schema.addEntity("AttendanceRecord");

        record.implementsSerializable();
        record.setTableName("AttendanceRecord");
        record.setClassNameDao("AttendanceRecordDao");
        record.setJavaPackage(entityPath);

        record.addIdProperty().autoincrement();
        record.addLongProperty("userId");
        record.addStringProperty("userName");
        record.addStringProperty("cardNo");
        record.addIntProperty("status");
        record.addLongProperty("checkInTime");

        record.setHasKeepSections(true);
    }


    /*====================================HomeWork======================================*/

    private static void addHomeWorkRecord(Schema schema){
        Entity record = schema.addEntity("HomeWorkRecord");

        record.implementsSerializable();
        record.setTableName("HomeWorkRecord");
        record.setClassNameDao("HomeWorkRecordDao");
        record.setJavaPackage(entityPath);

        record.addIdProperty().autoincrement();
        record.addLongProperty("hwRecordId");
        record.addLongProperty("memberId");
        record.addStringProperty("title");
        record.addLongProperty("sendUserId");
        record.addStringProperty("senderName");
        record.addStringProperty("senderAvatar");
        record.addStringProperty("targetName");
        record.addIntProperty("status");
        record.addBooleanProperty("hasRead");
        record.addLongProperty("sendTime");

        record.setHasKeepSections(true);
    }


    private static void addHomeWorkClass(Schema schema){
        Entity record = schema.addEntity("HomeWorkClass");

        record.implementsSerializable();
        record.setTableName("HomeWorkClass");
        record.setClassNameDao("HomeWorkClassDao");
        record.setJavaPackage(entityPath);

        record.addIdProperty().autoincrement();
        record.addLongProperty("homeworkId");
        record.addStringProperty("className");
        record.addStringProperty("title");
        record.addIntProperty("type");
        record.addLongProperty("sendTime");
        record.addIntProperty("finishedCount");
        record.addIntProperty("totalCount");

        record.setHasKeepSections(true);
    }

    private static void addHomeWorkUserRank(Schema schema){
        Entity record = schema.addEntity("HomeWorkUserRank");

        record.implementsSerializable();
        record.setTableName("HomeWorkUserRank");
        record.setClassNameDao("HomeWorkUserRankDao");
        record.setJavaPackage(entityPath);

        record.addIdProperty().autoincrement();
        record.addIntProperty("rank");
        record.addLongProperty("userId");
        record.addStringProperty("name");
        record.addStringProperty("avatar");
        record.addIntProperty("score");

        record.setHasKeepSections(true);
    }


    private static void addHomeWorkMember(Schema schema){
        Entity record = schema.addEntity("HomeWorkMember");

        record.implementsSerializable();
        record.setTableName("HomeWorkMember");
        record.setClassNameDao("HomeWorkMemberDao");
        record.setJavaPackage(entityPath);

        record.addIdProperty().autoincrement();
        record.addLongProperty("memberId");
        record.addStringProperty("name");
        record.addStringProperty("avatar");
        record.addIntProperty("status");
        record.addIntProperty("score");
        record.addBooleanProperty("specialAttention");
        record.addLongProperty("homeworkId");

        record.setHasKeepSections(true);
    }


    private static void addHomeWorkGenerate(Schema schema){
        Entity record = schema.addEntity("HomeWorkGenerate");

        record.implementsSerializable();
        record.setTableName("HomeWorkGenerate");
        record.setClassNameDao("HomeWorkGenerateDao");
        record.setJavaPackage(entityPath);

        record.addIdProperty().autoincrement();
        record.addLongProperty("childUserId");
        record.addStringProperty("name");
        record.addStringProperty("avatar");
        record.addIntProperty("generateCount");
        record.addIntProperty("remainMaxCount");
        record.addBooleanProperty("specialAttention");

        record.setHasKeepSections(true);
    }

    private static void addHomeWorkDetail(Schema schema){
        Entity record = schema.addEntity("HomeWorkDetail");

        record.implementsSerializable();
        record.setTableName("HomeWorkDetail");
        record.setClassNameDao("HomeWorkDetailDao");
        record.setJavaPackage(entityPath);

        record.addIdProperty().autoincrement();
        record.addLongProperty("memberId");
        record.addIntProperty("status");
        record.addStringProperty("title");
        record.addStringProperty("description");
        record.addStringProperty("senderName");
        record.addIntProperty("totalScore");
        record.addIntProperty("maxScore");
        record.addLongProperty("sendTime");
        record.addLongProperty("childUserId");

        record.setHasKeepSections(true);
    }


    private static void addGameLevel(Schema schema){
        Entity record = schema.addEntity("GameLevel");

        record.implementsSerializable();
        record.setTableName("GameLevel");
        record.setClassNameDao("GameLevelDao");
        record.setJavaPackage(entityPath);

        record.addIdProperty().autoincrement();
        record.addLongProperty("gameId");
        record.addIntProperty("level");
        record.addStringProperty("gameName");
        record.addStringProperty("abilityName");
        record.addStringProperty("picUrl");
        record.addIntProperty("stars");
        record.addStringProperty("color");
        record.addStringProperty("choicelevel");
        record.addBooleanProperty("hasGuide");

        record.setHasKeepSections(true);
    }

    private static void addAbilityDetail(Schema schema){
        Entity record = schema.addEntity("AbilityDetailList");

        record.implementsSerializable();
        record.setTableName("AbilityDetailList");
        record.setClassNameDao("AbilityDetailListDao");
        record.setJavaPackage(entityPath);

        record.addIdProperty().autoincrement();
        record.addIntProperty("ability");
        record.addIntProperty("level");
        record.addIntProperty("avgLevel");
        record.addDoubleProperty("percentage");
        record.addIntProperty("score");
        record.addIntProperty("maxScore");

        record.setHasKeepSections(true);
    }
    private static void addAbilityPoint(Schema schema){
        Entity record = schema.addEntity("AbilityPoint");

        record.implementsSerializable();
        record.setTableName("AbilityPoint");
        record.setClassNameDao("AbilityPointDao");
        record.setJavaPackage(entityPath);

        record.addIdProperty().autoincrement();
        record.addIntProperty("number");
        record.addIntProperty("score");

        record.setHasKeepSections(true);
    }
    private static void addGameScore(Schema schema){
        Entity record = schema.addEntity("Game_Score");

        record.implementsSerializable();
        record.setTableName("Game_Score");
        record.setClassNameDao("Game_ScoreDao");
        record.setJavaPackage(entityPath);

        record.addIdProperty().autoincrement();
        record.addStringProperty("gameName");
        record.addIntProperty("score");
        record.addIntProperty("bestLevel");
        record.addDoubleProperty("percentage");
        record.addStringProperty("color");

        record.setHasKeepSections(true);
    }
    private static void addScoreShow(Schema schema){
        Entity record = schema.addEntity("ScoreShow");

        record.implementsSerializable();
        record.setTableName("ScoreShow");
        record.setClassNameDao("ScoreShowDao");
        record.setJavaPackage(entityPath);

        record.addIdProperty().autoincrement();
        record.addIntProperty("totalAbilityLevel");
        record.addDoubleProperty("totalAbilityPercentage");
        record.addIntProperty("abilityQuotient");
        record.addIntProperty("maxAbilityQuotient");

        record.setHasKeepSections(true);
    }

    private static void addTestList(Schema schema){
        Entity record = schema.addEntity("TestList");

        record.implementsSerializable();
        record.setTableName("TestList");
        record.setClassNameDao("TestListDao");
        record.setJavaPackage(entityPath);

        record.addIdProperty().autoincrement();
        record.addStringProperty("name");
        record.addStringProperty("description");
        record.addStringProperty("associateTag");
        record.addIntProperty("color");
        record.addStringProperty("colorValue");
        record.addIntProperty("status");
        record.addStringProperty("animalPic");

        record.setHasKeepSections(true);
    }
    private static void addCourse(Schema schema){
        Entity record = schema.addEntity("CourseBean");

        record.implementsSerializable();
        record.setTableName("CourseBean");
        record.setClassNameDao("CourseDao");
        record.setJavaPackage(entityPath);

        record.addIdProperty().autoincrement();
        record.addLongProperty("CourseId");
        record.addLongProperty("createOn");
        record.addLongProperty("updateOn");
        record.addLongProperty("teacherId");
        record.addStringProperty("teacherName");
        record.addLongProperty("labelId");
        record.addStringProperty("labelName");
        record.addIntProperty("type");
        record.addStringProperty("title");
        record.addStringProperty("description");
        record.addStringProperty("cover");
        record.addIntProperty("status");
        record.addLongProperty("hits");
        record.addDoubleProperty("score");
        record.addLongProperty("scoreCnt");
        record.addLongProperty("startOn");
        record.addLongProperty("endOn");
        record.addStringProperty("teacherDesc");
        record.addStringProperty("teacherAvatar");

        record.setHasKeepSections(true);
    }
    private static void addCourseLesson(Schema schema){
        Entity record = schema.addEntity("CourseLessonBean");

        record.implementsSerializable();
        record.setTableName("CourseLessonBean");
        record.setClassNameDao("CourseLessonDao");
        record.setJavaPackage(entityPath);

        record.addIdProperty().autoincrement();
        record.addLongProperty("CourseLessonId");
        record.addLongProperty("createOn");
        record.addLongProperty("updateOn");
        record.addLongProperty("courseId");
        record.addStringProperty("title");
        record.addLongProperty("startOn");
        record.addLongProperty("endOn");
        record.addStringProperty("videoUrl");
        record.addLongProperty("hits");
        record.addLongProperty("liveHits");
        record.addIntProperty("liveStatus");
        record.addStringProperty("pic");
        record.addIntProperty("duration");
        record.addIntProperty("resourceType");

        record.addStringProperty("teacherName");
        record.addStringProperty("teacherAvatar");

        record.setHasKeepSections(true);
    }

    private static void addDataReport(Schema schema){
        Entity dr = schema.addEntity("DataReport");

        dr.implementsSerializable();
        dr.setTableName("tblDataReport");
        dr.setClassNameDao("DataReportDao");
        dr.setJavaPackage(entityPath);

        dr.addIdProperty().autoincrement();
        dr.addLongProperty("userId").notNull();
        dr.addIntProperty("eventType").notNull();
        dr.addStringProperty("bid");
        dr.addLongProperty("timestamp").notNull();
        dr.addBooleanProperty("isSended").notNull();
        dr.addLongProperty("serialNo");
        dr.addStringProperty("extendedInfo");

        dr.setHasKeepSections(true);
    }

}
