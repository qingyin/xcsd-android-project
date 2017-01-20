package com.tuxing.sdk.db.helper;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.tuxing.rpc.proto.CourseLesson;
import com.tuxing.sdk.db.dao.global.LoginUserDao;
import com.tuxing.sdk.db.dao.user.*;
import com.tuxing.sdk.db.entity.*;
import com.tuxing.sdk.utils.CollectionUtils;
import com.tuxing.sdk.utils.Constants;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Created by Alan on 2015/6/4.
 */
public class UserDbHelper {
    private static UserDbHelper instance = new UserDbHelper();

    private DaoSession session;
    private String dbFile;

//    static{
//        QueryBuilder.LOG_SQL = true;
//        QueryBuilder.LOG_VALUES = true;
//    }


    private UserDbHelper() {

    }

    public void init(Context context, long userId) {
        if (context == null) {
            throw new IllegalStateException("Cannot get the service context");
        }

        this.dbFile = String.format(Constants.USER_DB_FILE, userId);

        DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context, dbFile, null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());

        session = daoMaster.newSession();
    }

    public static UserDbHelper getInstance() {
        return instance;
    }

    public String getDbFile() {
        return dbFile;
    }

    /*------------------------------CourseLesson------------------------------------*/
    public void updateCourseLesson(List<CourseLessonBean> CourseLesson) {
        CourseLessonDao courseLessonDao = session.getCourseLessonDao();
        courseLessonDao.deleteAll();
        courseLessonDao.insertOrReplaceInTx(CourseLesson);
    }

    public List<CourseLessonBean> getCourseLesson() {
        CourseLessonDao courseLessonDao = session.getCourseLessonDao();
        return courseLessonDao.queryBuilder()
                .orderDesc(CourseLessonDao.Properties.Id)
                .limit(Constants.DEFAULT_LIST_COUNT)
                .list();
    }

    public void updateCourse(List<CourseBean> Coursebean) {
        CourseDao courseDao = session.getCourseDao();
        courseDao.deleteAll();
        courseDao.insertOrReplaceInTx(Coursebean);
    }

    public List<CourseBean> getCourse(Long id) {
        CourseDao courseDao = session.getCourseDao();
        return courseDao.queryBuilder()
                .where(CourseDao.Properties.CourseId.eq(id))
                .orderDesc(CourseDao.Properties.Id)
                .limit(Constants.DEFAULT_LIST_COUNT)
                .list();
    }

    /*------------------------------HomeWorkRank------------------------------------*/
    public void updateHomeWorkRank(List<HomeWorkUserRank> HomeWorkUserRank) {
        HomeWorkUserRankDao HomeWorkUserRankdao = session.getHomeWorkUserRankDao();
        HomeWorkUserRankdao.deleteAll();
        HomeWorkUserRankdao.insertOrReplaceInTx(HomeWorkUserRank);
    }

    public List<HomeWorkUserRank> getLatestHomeWorkUserRank() {
        HomeWorkUserRankDao HomeWorkUserRankdao = session.getHomeWorkUserRankDao();
        return HomeWorkUserRankdao.queryBuilder()
                .orderDesc(HomeWorkUserRankDao.Properties.Id)
                .limit(Constants.DEFAULT_LIST_COUNT)
                .list();
    }

    /*------------------------------SCORE------------------------------------*/
    public void updateTestList(List<AbilityDetailList> abilityDetailLists, List<AbilityPoint> abilityPoints) {
        AbilityDetailListDao abilityDetailListDao = session.getAbilityDetailListDao();
        abilityDetailListDao.deleteAll();
        abilityDetailListDao.insertOrReplaceInTx(abilityDetailLists);

        AbilityPointDao abilityPointDao = session.getAbilityPointDao();
        abilityPointDao.deleteAll();
        abilityPointDao.insertOrReplaceInTx(abilityPoints);

//        ScoreShowDao scoreShowDao = session.getScoreShowDao();
//        scoreShowDao.deleteAll();
//        scoreShowDao.insertOrReplaceInTx(scoreShow);
    }

    public List<AbilityDetailList> getLatestAbilityDetailList() {
        AbilityDetailListDao abilityDetailListDao = session.getAbilityDetailListDao();
        return abilityDetailListDao.queryBuilder()
                .orderDesc(AbilityDetailListDao.Properties.Id)
                .limit(Constants.DEFAULT_LIST_COUNT)
                .list();
    }

    public List<AbilityPoint> getLatestAbilityPoint() {
        AbilityPointDao abilityPointDao = session.getAbilityPointDao();
        return abilityPointDao.queryBuilder()
                .orderDesc(AbilityPointDao.Properties.Id)
                .limit(Constants.DEFAULT_LIST_COUNT)
                .list();
    }
//    public ScoreShowDao getLatestScoreShowDao(){
//        ScoreShowDao scoreShowDao = session.getScoreShowDao();
//        return scoreShowDao.queryBuilder()
//                .orderDesc(ScoreShowDao.Properties.AbilityQuotient)
//                .limit(1)
//                .unique();
//    }


    /*------------------------------TEST------------------------------------*/
    public void updateTestList(List<TestList> testLists) {
        TestListDao testListDao = session.getTestListDao();
        testListDao.deleteAll();
        testListDao.insertOrReplaceInTx(testLists);
    }

    public List<TestList> getLatestTestList() {
        TestListDao testListDao = session.getTestListDao();
        return testListDao.queryBuilder()
                .orderAsc(TestListDao.Properties.Id)
                .limit(Constants.DEFAULT_LIST_COUNT)
                .list();
    }

    /*------------------------------HOMEWORK------------------------------------*/
    public void updateHomeWorkList(List<HomeWorkRecord> hwRecordList) {
        HomeWorkRecordDao hwRecordDao = session.getHomeWorkRecordDao();
        hwRecordDao.deleteAll();
        hwRecordDao.insertOrReplaceInTx(hwRecordList);
    }


    public List<HomeWorkRecord> getLatestHomeWorkRecordList() {
        HomeWorkRecordDao hwRecordDao = session.getHomeWorkRecordDao();
        return hwRecordDao.queryBuilder()
                .orderDesc(HomeWorkRecordDao.Properties.HwRecordId)
                .limit(Constants.DEFAULT_LIST_COUNT)
                .list();
    }


    public HomeWorkRecord getHomeWorkRecordLastOneFromLocal() {
        HomeWorkRecordDao hwRecordDao = session.getHomeWorkRecordDao();
        return hwRecordDao.queryBuilder()
                .orderDesc(HomeWorkRecordDao.Properties.HwRecordId)
                .limit(1)
                .unique();
    }

    public void setHomeWorkNoticeToReaded(final long homeWorkNoticeId) {
        session.runInTx(new Runnable() {
            @Override
            public void run() {
                List<HomeWorkRecord> record = session.getHomeWorkRecordDao().queryBuilder()
                        .where(HomeWorkRecordDao.Properties.HwRecordId.eq(homeWorkNoticeId))
                        .list();
                for (HomeWorkRecord rc : record) {
                    rc.setHasRead(true);
                }
            }
        });
    }

    public void deleteHomeWorkRecordById(final long homeWorkNoticeId) {
        session.runInTx(new Runnable() {
            @Override
            public void run() {
                List<HomeWorkRecord> record = session.getHomeWorkRecordDao().queryBuilder()
                        .where(HomeWorkRecordDao.Properties.HwRecordId.eq(homeWorkNoticeId))
                        .list();
                for (HomeWorkRecord rc : record) {
                    session.getHomeWorkRecordDao().delete(rc);
                }
            }
        });
    }

    public void updateHomeWorkSendList(List<HomeWorkClass> hwClassList) {
        HomeWorkClassDao hwClassDao = session.getHomeWorkClassDao();
        hwClassDao.deleteAll();
        hwClassDao.insertOrReplaceInTx(hwClassList);
    }

    public List<HomeWorkClass> getLatestHomeWorkSendList() {
        HomeWorkClassDao hwClassDao = session.getHomeWorkClassDao();
        return hwClassDao.queryBuilder()
                .orderDesc(HomeWorkClassDao.Properties.HomeworkId)
                .limit(Constants.DEFAULT_LIST_COUNT)
                .list();
    }


    public void updateHomeWorkMemberList(List<HomeWorkMember> hwMemberList) {
        HomeWorkMemberDao hwMemberDao = session.getHomeWorkMemberDao();
        hwMemberDao.deleteAll();
        hwMemberDao.insertOrReplaceInTx(hwMemberList);
    }

    public List<HomeWorkMember> getLatestHomeWorkMemberList(long homeworkId) {
        HomeWorkMemberDao hwMemberDao = session.getHomeWorkMemberDao();
        return hwMemberDao.queryBuilder()
                .where(HomeWorkMemberDao.Properties.HomeworkId.eq(homeworkId))
                .orderDesc(HomeWorkMemberDao.Properties.Score)
                .list();
    }

    /*----------------------------------LIGHT_APP-------------------------------------*/

    public void saveLightApps(final List<LightApp> lightApps) {
        session.runInTx(new Runnable() {
            @Override
            public void run() {
                session.getLightAppDao().deleteAll();
                session.getLightAppDao().insertOrReplaceInTx(lightApps);
            }
        });
    }

    public List<LightApp> getLightAppsByShown(int showAt) {
        return session.getLightAppDao().queryBuilder()
                .where(LightAppDao.Properties.ShowAt.eq(showAt))
                .list();
    }

    /*------------------------------NOTICE------------------------------------*/

    public Long getMaxNoticeId() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT MAX(NOTICE_ID) ");
        sqlBuilder.append("FROM NOTICE ");

        Cursor cursor = session.getDatabase().rawQuery(sqlBuilder.toString(), new String[]{});
        if (cursor.moveToNext()) {
            return cursor.getLong(0);
        }

        return null;

    }

    public Notice getLatestOneNotice(int mailbox) {
        NoticeDao noticeDao = session.getNoticeDao();

        return noticeDao.queryBuilder()
                .where(NoticeDao.Properties.Mailbox.eq(mailbox))
                .orderDesc(NoticeDao.Properties.NoticeId)
                .limit(1)
                .unique();
    }

    public List<Notice> getLatestNotice(int mailbox) {
        NoticeDao noticeDao = session.getNoticeDao();

        return noticeDao.queryBuilder()
                .where(NoticeDao.Properties.Mailbox.eq(mailbox))
                .orderDesc(NoticeDao.Properties.NoticeId)
                .limit(Constants.DEFAULT_LIST_COUNT)
                .list();
    }

    public void deleteNotice(final int mailbox) {
        session.runInTx(new Runnable() {
            @Override
            public void run() {
                List<Notice> notices = session.getNoticeDao().queryBuilder()
                        .where(NoticeDao.Properties.Mailbox.eq(mailbox))
                        .list();

                for (Notice notice : notices) {
                    //This is clear the cached objects
                    session.getNoticeDao().delete(notice);
                }
            }
        });
    }

    public void updateNoticeList(List<Notice> notices) {
        NoticeDao noticeDao = session.getNoticeDao();
        noticeDao.insertOrReplaceInTx(notices);
    }

    public void updateNotice(Notice notice) {
        NoticeDao noticeDao = session.getNoticeDao();

        noticeDao.insertOrReplaceInTx(notice);
    }

    public List<Notice> getNoticeById(long noticeId) {
        NoticeDao noticeDao = session.getNoticeDao();

        return noticeDao.queryBuilder()
                .where(NoticeDao.Properties.NoticeId.eq(noticeId))
                .list();
    }

    /*-------------------------------------------USER-----------------------------------------------*/
    public void saveAllUsers(List<User> userList) {
        session.getUserDao().insertOrReplaceInTx(userList);
    }

    public List<User> getAllUsers() {
        return session.getUserDao().loadAll();
    }

    public List<User> getAllChildrenAndAllTeacher() {
        return session.getUserDao()
                .queryBuilder()
                .where(UserDao.Properties.Type.in(1,3))
                .list();
    }
    public List<User> getAllParent() {
        return session.getUserDao()
                .queryBuilder()
                .where(UserDao.Properties.Type.in(2))
                .list();
    }

    public void saveUser(User user) {
        session.getUserDao().insertOrReplace(user);
    }

    public User getUserById(long userId) {
        return session.getUserDao()
                .queryBuilder()
                .where(UserDao.Properties.UserId.eq(userId))
                .unique();
    }

    public void deleteUser(final long userId) {
        session.runInTx(new Runnable() {
            @Override
            public void run() {
                User user = session.getUserDao().queryBuilder()
                        .where(UserDao.Properties.UserId.eq(userId))
                        .unique();

                if (user != null) {
                    List<DepartmentUser> departmentUsers = session.getDepartmentUserDao().queryBuilder()
                            .where(DepartmentUserDao.Properties.UserId.eq(userId))
                            .list();

                    for (DepartmentUser departmentUser : departmentUsers) {
                        session.getDepartmentUserDao().delete(departmentUser);
                    }

                    session.getUserDao().delete(user);
                }
            }
        });
    }

    /*----------------------------------------DEPARTMENT-------------------------------------------*/

    public Department getDepartment(long departmentId) {
        return session.getDepartmentDao()
                .queryBuilder()
                .where(DepartmentDao.Properties.DepartmentId.eq(departmentId))
                .unique();
    }

    public List<Department> getAllDepartment() {
        return session.getDepartmentDao().loadAll();
    }

    public Department getDepartmentById(long departmentId) {
        return session.getDepartmentDao()
                .queryBuilder()
                .where(DepartmentDao.Properties.DepartmentId.eq(departmentId))
                .unique();
    }

    public Department getDepartmentByChatGroupId(String chatGroupId) {
        return session.getDepartmentDao()
                .queryBuilder()
                .where(DepartmentDao.Properties.ChatGroupId.eq(chatGroupId))
                .unique();
    }

    public void saveDepartment(Department department) {
        session.getDepartmentDao().insertOrReplace(department);
    }

    public void saveAllDepartments(List<Department> departmentList) {
        session.getDepartmentDao().insertOrReplaceInTx(departmentList);
    }

    public void deleteDepartment(long departmentId) {
        Department department = getDepartment(departmentId);

        if (department != null) {
            session.getDepartmentDao().delete(department);
        }
    }

    /*-------------------------------DEPARTMENT_USER-------------------------------------------*/

    public void deleteDepartmentUser(long departmentId, long userId) {
        DepartmentUser departmentUser = getDepartmentUser(departmentId, userId);

        if (departmentUser != null) {
            session.getDepartmentUserDao().delete(departmentUser);
        }

        long departmentUserCount = session.getDepartmentUserDao().queryBuilder()
                .where(DepartmentUserDao.Properties.UserId.eq(userId))
                .count();

        if (departmentUserCount == 0) {
            //the user is not in any department
            User user = session.getUserDao().queryBuilder()
                    .where(UserDao.Properties.UserId.eq(userId))
                    .unique();

            if (user != null) {
                session.getUserDao().delete(user);
            }
        }
    }

    public void saveDepartmentUsers(final long departmentId, final List<User> userList) {
        session.runInTx(new Runnable() {
            @Override
            public void run() {
                for (User user : userList) {
                    DepartmentUser departmentUser = getDepartmentUser(departmentId, user.getUserId());
                    if (departmentUser == null) {
                        departmentUser = new DepartmentUser();

                    }

                    departmentUser.setUserId(user.getUserId());
                    departmentUser.setDepartmentId(departmentId);

                    session.getUserDao().insertOrReplace(user);
                    session.getDepartmentUserDao().insertOrReplace(departmentUser);
                }
            }
        });
    }

    public DepartmentUser getDepartmentUser(long departmentId, long userId) {
        return session.getDepartmentUserDao()
                .queryBuilder()
                .where(DepartmentUserDao.Properties.DepartmentId.eq(departmentId),
                        DepartmentUserDao.Properties.UserId.eq(userId))
                .unique();
    }

    public List<User> getUsersInDepartmentByType(long departmentId, List<Integer> userType) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("WHERE TYPE IN (");
        sqlBuilder.append(CollectionUtils.join(userType, ",")).append(") ");
        sqlBuilder.append("AND USER_ID IN ");
        sqlBuilder.append("(SELECT USER_ID FROM DEPARTMENT_USER WHERE DEPARTMENT_ID = ?)");

        return session.getUserDao().queryRaw(sqlBuilder.toString(),
                String.valueOf(departmentId));
    }

    public List<User> getUsersInDepartmentByGroupId(String chatGroupId) {
        Department department = getDepartmentByChatGroupId(chatGroupId);
        if (department == null) {
            return new ArrayList<>();
        }

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("WHERE USER_ID IN ");
        sqlBuilder.append("(SELECT USER_ID FROM DEPARTMENT_USER WHERE DEPARTMENT_ID = ?)");

        return session.getUserDao().queryRaw(sqlBuilder.toString(),
                String.valueOf(department.getDepartmentId()));
    }

    public long getDepartmentMemberCountByType(long departmentId, List<Integer> userType) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT COUNT(*) ");
        sqlBuilder.append("FROM USER ");
        sqlBuilder.append("WHERE TYPE IN (");
        sqlBuilder.append(CollectionUtils.join(userType, ",")).append(") ");
        sqlBuilder.append("AND USER_ID IN ");
        sqlBuilder.append("(SELECT USER_ID FROM DEPARTMENT_USER WHERE DEPARTMENT_ID = ?)");

        Cursor cursor = session.getDatabase().rawQuery(sqlBuilder.toString(),
                new String[]{String.valueOf(departmentId)});

        if (cursor.moveToNext()) {
            return cursor.getLong(0);
        }

        return 0;
    }

    /*----------------------------------CHECK IN----------------------------------------------*/

    public Long getMaxCheckInRecordId() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT MAX(CHECK_IN_RECORD_ID) ");
        sqlBuilder.append("FROM CHECK_IN_RECORD ");

        Cursor cursor = session.getDatabase().rawQuery(sqlBuilder.toString(), new String[]{});
        if (cursor.moveToNext()) {
            return cursor.getLong(0);
        }

        return null;
    }

    public void updateCheckInRecordList(List<CheckInRecord> checkInRecords) {
        session.getCheckInRecordDao().deleteAll();
        session.getCheckInRecordDao().insertOrReplaceInTx(checkInRecords);
    }

    public CheckInRecord getLatestOneCheckInRecord() {
        CheckInRecordDao checkInRecordDao = session.getCheckInRecordDao();

        return checkInRecordDao.queryBuilder()
                .orderDesc(CheckInRecordDao.Properties.CheckInRecordId)
                .limit(1)
                .unique();
    }

    public List<CheckInRecord> getLatestCheckInRecord() {
        CheckInRecordDao checkInRecordDao = session.getCheckInRecordDao();

        return checkInRecordDao.queryBuilder()
                .orderDesc(CheckInRecordDao.Properties.CheckInRecordId)
                .limit(Constants.DEFAULT_LIST_COUNT)
                .list();
    }

    public List<CheckInRecord> getCheckInRecordById(long checkInRecordId) {
        CheckInRecordDao checkInRecordDao = session.getCheckInRecordDao();

        return checkInRecordDao.queryBuilder()
                .where(CheckInRecordDao.Properties.CheckInRecordId.eq(checkInRecordId))
                .list();
    }

    public void deleteAllCheckInRecords() {
        session.getCheckInRecordDao().deleteAll();
    }

    /*-----------------------------------------SETTING-------------------------------------------*/

    public void saveSetting(String field, String value) {
        Setting setting = new Setting();

        setting.setField(field);
        setting.setValue(value);

        session.getSettingDao().insertOrReplace(setting);
    }

    public String getSettingValue(String field) {
        Setting setting = session.getSettingDao().queryBuilder()
                .where(SettingDao.Properties.Field.eq(field))
                .unique();

        if (setting != null) {
            return setting.getValue();
        }

        return null;
    }

    public List<Setting> getAllSettings() {
        return session.getSettingDao().loadAll();
    }

    public void saveAllSettings(List<Setting> settings) {
        session.getSettingDao().insertOrReplaceInTx(settings);
    }

    /*------------------------------------------------FEED---------------------------------------------------------*/

    public Long getMaxFeedId() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT MAX(FEED_ID) ");
        sqlBuilder.append("FROM FEED ");

        Cursor cursor = session.getDatabase().rawQuery(sqlBuilder.toString(), new String[]{});
        if (cursor.moveToNext()) {
            return cursor.getLong(0);
        }

        return null;
    }

    public Long getMaxFeedIdByUser(long userId) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT MAX(FEED_ID) ");
        sqlBuilder.append("FROM FEED ");
        sqlBuilder.append("WHERE USER_ID = ? ");

        Cursor cursor = session.getDatabase().rawQuery(sqlBuilder.toString(),
                new String[]{String.valueOf(userId)});
        if (cursor.moveToNext()) {
            return cursor.getLong(0);
        }

        return null;
    }

    public Feed getLatestFeedById(long feedId) {
        Feed feed = session.getFeedDao().queryBuilder()
                .where(FeedDao.Properties.FeedId.eq(feedId))
                .unique();

        if (feed != null) {
            List<Comment> comments = session.getCommentDao().queryBuilder()
                    .where(CommentDao.Properties.TopicId.eq(feed.getFeedId()),
                            CommentDao.Properties.CommentType.eq(Constants.COMMENT_TYPE.REPLY))
                    .orderAsc(CommentDao.Properties.CommentId)
                    .limit(Constants.DEFAULT_LIST_COUNT)
                    .list();

            feed.setComments(comments);

            List<Comment> likes = session.getCommentDao().queryBuilder()
                    .where(CommentDao.Properties.TopicId.eq(feed.getFeedId()),
                            CommentDao.Properties.CommentType.eq(Constants.COMMENT_TYPE.LIKE))
                    .orderDesc(CommentDao.Properties.CommentId)
                    .list();

            feed.setLikes(likes);
        }

        return feed;
    }

    public List<Feed> getLatestFeedByUser(long userId) {
        return session.getFeedDao().queryBuilder()
                .where(FeedDao.Properties.UserId.eq(userId))
                .orderDesc(FeedDao.Properties.FeedId)
                .limit(Constants.DEFAULT_LIST_COUNT)
                .list();
    }

    public void saveFeeds(final List<Feed> feedList) {
        session.runInTx(new Runnable() {
            @Override
            public void run() {
                FeedDao feedDao = session.getFeedDao();
                CommentDao commentDao = session.getCommentDao();

                feedDao.deleteAll();
                commentDao.deleteAll();

                for (Feed feed : feedList) {
                    feedDao.insertOrReplace(feed);
                    if (feed.getComments() != null) {
                        for (Comment comment : feed.getComments()) {
                            commentDao.insertOrReplace(comment);
                        }
                    }

                    if (feed.getLikes() != null) {
                        for (Comment like : feed.getLikes()) {
                            commentDao.insertOrReplace(like);
                        }
                    }
                }
            }
        });
    }

    public void saveFeed(Feed feed) {
        session.getFeedDao().insertOrReplace(feed);
    }

    public List<Feed> getLatestFeed() {
        List<Feed> feeds = session.getFeedDao().queryBuilder()
                .orderDesc(FeedDao.Properties.FeedId)
                .limit(Constants.DEFAULT_LIST_COUNT + 1)
                .list();

        for (Feed feed : feeds) {
            List<Comment> comments = session.getCommentDao().queryBuilder()
                    .where(CommentDao.Properties.TopicId.eq(feed.getFeedId()),
                            CommentDao.Properties.CommentType.eq(Constants.COMMENT_TYPE.REPLY))
                    .orderAsc(CommentDao.Properties.CommentId)
                    .limit(Constants.DEFAULT_LIST_COUNT)
                    .list();

            feed.setComments(comments);

            List<Comment> likes = session.getCommentDao().queryBuilder()
                    .where(CommentDao.Properties.TopicId.eq(feed.getFeedId()),
                            CommentDao.Properties.CommentType.eq(Constants.COMMENT_TYPE.LIKE))
                    .orderDesc(CommentDao.Properties.CommentId)
                    .list();

            feed.setLikes(likes);
        }

        return feeds;
    }

    public void deleteFeed(long feedId) {
        Feed feed = session.getFeedDao().queryBuilder()
                .where(FeedDao.Properties.FeedId.eq(feedId))
                .unique();

        if (feed != null) {
            session.getFeedDao().delete(feed);
        }
    }

    /*----------------------------------------COMMENT------------------------------------------*/

    public void deleteComment(long commentId) {
        Comment comment = session.getCommentDao().queryBuilder()
                .where(CommentDao.Properties.CommentId.eq(commentId))
                .unique();

        if (comment != null) {
            session.getCommentDao().delete(comment);
        }
    }

    public void saveComment(Comment comment) {
        session.getCommentDao().insertOrReplace(comment);
    }

    public void saveComments(List<Comment> comments) {
        session.getCommentDao().insertOrReplaceInTx(comments);
    }

    public Long getMaxCommentId(long userId, int type) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT MAX(COMMENT_ID) ");
        sqlBuilder.append("FROM COMMENT ");
        sqlBuilder.append("WHERE TARGET_TYPE = ? ");
        sqlBuilder.append("AND (TOPIC_USER_ID = ? ");
        sqlBuilder.append("OR REPLY_TO_USER_ID = ?) ");


        Cursor cursor = session.getDatabase().rawQuery(sqlBuilder.toString(),
                new String[]{String.valueOf(type), String.valueOf(userId),
                        String.valueOf(userId)});
        if (cursor.moveToNext()) {
            return cursor.getLong(0);
        }

        return null;
    }

    public List<Comment> getCommentsByUser(long userId, int type) {
        QueryBuilder<Comment> queryBuilder = session.getCommentDao().queryBuilder();
        queryBuilder.where(CommentDao.Properties.TargetType.eq(type),
                queryBuilder.or(CommentDao.Properties.TopicUserId.eq(userId),
                        CommentDao.Properties.ReplayToUserId.eq(userId)));
        return queryBuilder.list();
    }

    public List<Comment> getCommentsByTopic(long topicId, int type) {
        return session.getCommentDao().queryBuilder()
                .where(CommentDao.Properties.TopicId.eq(topicId),
                        CommentDao.Properties.TargetType.eq(type))
                .list();
    }

    /*------------------------------------FEED_MEDICINE_TASK--------------------------------------*/

    public List<FeedMedicineTask> getLatestFeedMedicineTasks() {
        return session.getFeedMedicineTaskDao().queryBuilder()
                .orderDesc(FeedMedicineTaskDao.Properties.TaskId)
                .limit(Constants.DEFAULT_LIST_COUNT)
                .list();
    }

    public void saveFeedMedicineTasks(List<FeedMedicineTask> feedMedicineTaskList) {
        session.getFeedMedicineTaskDao().deleteAll();
        session.getFeedMedicineTaskDao().insertOrReplaceInTx(feedMedicineTaskList);
    }

    public void saveFeedMedicineTask(FeedMedicineTask feedMedicineTask) {
        session.getFeedMedicineTaskDao().insertOrReplace(feedMedicineTask);
    }

    public FeedMedicineTask getFeedMedicineTask(long taskId) {
        return session.getFeedMedicineTaskDao().queryBuilder()
                .where(FeedMedicineTaskDao.Properties.TaskId.eq(taskId))
                .unique();
    }

    /*------------------------------------GARDEN_MAIL--------------------------------------*/

    public List<GardenMail> getLatestGardenMails() {
        return session.getGardenMailDao().queryBuilder()
                .orderDesc(GardenMailDao.Properties.MailId)
                .limit(Constants.DEFAULT_LIST_COUNT)
                .list();
    }

    public void saveGardenMails(List<GardenMail> gardenMailList) {
        session.getGardenMailDao().deleteAll();
        session.getGardenMailDao().insertOrReplaceInTx(gardenMailList);
    }

    public void saveGardenMail(GardenMail gardenMail) {
        session.getGardenMailDao().insertOrReplaceInTx(gardenMail);
    }

    public GardenMail getGardenMail(long mailId) {
        return session.getGardenMailDao().queryBuilder()
                .where(GardenMailDao.Properties.GardenId.eq(mailId))
                .unique();
    }

    /*----------------------------------CONTENT_ITEM_GROUP---------------------------------*/
    public List<ContentItemGroup> getLatestContentItemGroups() {
        List<ContentItemGroup> itemGroups = session.getContentItemGroupDao().queryBuilder()
                .orderAsc(ContentItemGroupDao.Properties.GroupId)
                .limit(Constants.DEFAULT_LIST_COUNT)
                .list();

        for (ContentItemGroup group : itemGroups) {
            List<ContentItem> items = session.getContentItemDao().queryBuilder()
                    .where(ContentItemDao.Properties.GroupId.eq(group.getGroupId()))
                    .orderAsc(ContentItemDao.Properties.PublishTime)
                    .list();

            group.setItems(items);
        }

        return itemGroups;
    }

    public void saveContentItemGroups(final List<ContentItemGroup> itemGroupList) {
        session.runInTx(new Runnable() {
            @Override
            public void run() {
                ContentItemGroupDao itemGroupDao = session.getContentItemGroupDao();
                ContentItemDao itemDao = session.getContentItemDao();

                List<ContentItemGroup> oldItemGroupList = itemGroupDao.loadAll();

                for (ContentItemGroup group : oldItemGroupList) {
                    List<ContentItem> items = itemDao.queryBuilder()
                            .where(ContentItemDao.Properties.GroupId.eq(group.getGroupId()))
                            .list();

                    if (!CollectionUtils.isEmpty(items)) {
                        for (ContentItem item : items) {
                            itemDao.delete(item);
                        }
                    }
                }

                itemGroupDao.deleteAll();

                for (ContentItemGroup group : itemGroupList) {
                    itemGroupDao.insertOrReplace(group);
                    if (group.getItems() != null) {
                        for (ContentItem item : group.getItems()) {
                            itemDao.insertOrReplace(item);
                        }
                    }
                }
            }
        });
    }

    /*-------------------------------------CONTENT_ITEM-----------------------------------------*/

    public ContentItem getLatestOneGroupItem() {
        return session.getContentItemDao().queryBuilder()
                .where(ContentItemDao.Properties.GroupId.isNotNull())
                .orderDesc(ContentItemDao.Properties.PublishTime)
                .limit(1)
                .unique();
    }

    public ContentItem getLatestOneContentItems(int contentType) {
        return session.getContentItemDao().queryBuilder()
                .where(ContentItemDao.Properties.ContentType.eq(contentType))
                .orderDesc(ContentItemDao.Properties.PublishTime)
                .limit(1)
                .unique();
    }

    public List<ContentItem> getLatestContentItems(int contentType) {
        return session.getContentItemDao().queryBuilder()
                .where(ContentItemDao.Properties.ContentType.eq(contentType))
                .orderDesc(ContentItemDao.Properties.ItemId)
                .limit(Constants.DEFAULT_LIST_COUNT)
                .list();
    }

    public void saveContentItems(List<ContentItem> itemList, int contentType) {
        deleteItemsByType(contentType);
        session.getContentItemDao().insertOrReplaceInTx(itemList);
    }

    public void deleteItemsByType(final int contentType) {
        session.runInTx(new Runnable() {
            @Override
            public void run() {
                List<ContentItem> contentItems = session.getContentItemDao().queryBuilder()
                        .where(ContentItemDao.Properties.ContentType.eq(contentType))
                        .list();

                for (ContentItem contentItem : contentItems) {
                    session.getContentItemDao().delete(contentItem);
                }
            }
        });
    }

    /*-------------------------------------REVOKED_MESSAGE-----------------------------------------*/

    public RevokedMessage getRevokedMessageByMsgId(String msgId) {
        return session.getRevokedMessageDao().queryBuilder()
                .where(RevokedMessageDao.Properties.MsgId.eq(msgId))
                .unique();
    }

    public void deleteRevokedMessage(String msgId) {
        RevokedMessage message = getRevokedMessageByMsgId(msgId);

        if (message != null) {
            session.getRevokedMessageDao().delete(message);
        }
    }

    public void saveRevokedMessage(RevokedMessage msg) {
        session.insertOrReplace(msg);
    }

    public List<RevokedMessage> getAllRevokedMessage() {
        return session.getRevokedMessageDao().loadAll();
    }

    /*-------------------------------------CLASS_PICTURE-----------------------------------------*/

    public void saveClassPictures(List<ClassPicture> pictures) {
        session.getClassPictureDao().insertOrReplaceInTx(pictures);
    }

    public void deleteClassPictures(Long classId) {
        final List<ClassPicture> pictures = session.getClassPictureDao().queryBuilder()
                .where(ClassPictureDao.Properties.ClassId.eq(classId))
                .list();

        session.runInTx(new Runnable() {
            @Override
            public void run() {
                for (ClassPicture picture : pictures) {
                    session.getClassPictureDao().delete(picture);
                }
            }
        });
    }

    public List<ClassPicture> getClassPictures(Long classId) {
        return session.getClassPictureDao().queryBuilder()
                .where(ClassPictureDao.Properties.ClassId.eq(classId))
                .orderDesc(ClassPictureDao.Properties.PicId)
                .limit(Constants.DEFAULT_LIST_COUNT)
                .list();
    }

    /*-------------------------------------ACTIVITY-----------------------------------------*/

    public boolean isActivityDeleted(Long activityId) {
        if (activityId == null) {
            return false;
        }

        List<Activity> activities = session.getActivityDao().queryBuilder()
                .where(ActivityDao.Properties.ActivityId.eq(activityId),
                        ActivityDao.Properties.Deleted.eq(true))
                .list();

        return !CollectionUtils.isEmpty(activities);
    }

    public void saveActivity(Activity activity) {
        session.getActivityDao().insertOrReplace(activity);
    }

    public void deleteActivity(Long activityId) {
        final Activity activity = session.getActivityDao().queryBuilder()
                .where(ActivityDao.Properties.ActivityId.eq(activityId))
                .unique();

        if (activity != null) {
            session.runInTx(new Runnable() {
                @Override
                public void run() {
                    List<Feed> activityFeeds = session.getFeedDao().queryBuilder()
                            .where(FeedDao.Properties.FeedType.eq(Constants.FEED_TYPE.ACTIVITY_FEED))
                            .list();

                    for (Feed feed : activityFeeds) {
                        session.getFeedDao().delete(feed);
                    }

                    activity.setDeleted(true);

                    session.getActivityDao().insertOrReplace(activity);
                }
            });
        }
    }

    public void deleteAllActivity() {
        session.runInTx(new Runnable() {

            @Override
            public void run() {
                List<Activity> activityList = session.getActivityDao().queryBuilder()
                        .where(ActivityDao.Properties.Deleted.eq(false))
                        .list();

                for (Activity activity : activityList) {
                    activity.setDeleted(true);
                }

                session.getActivityDao().insertOrReplaceInTx(activityList);
            }
        });
    }

    /*-------------------------------------DATA_REPORT-----------------------------------------*/

    public void insertDataReport(long userId, int eventType, String bid, Long timestamp, String extendedInfo) {
        DataReport dr = new DataReport();
        dr.setUserId(userId);
        dr.setEventType(eventType);
        dr.setBid(bid);
        dr.setTimestamp(timestamp);
        dr.setIsSended(false);
        dr.setExtendedInfo(extendedInfo);
        session.getDataReportDao().insert(dr);
    }

    public void updateDataReportState(final List<Long> listDataReportId, final long serialNo) {
        for (Long id : listDataReportId) {
            final List<DataReport> drList = session.getDataReportDao().queryBuilder()
                    .where(DataReportDao.Properties.Id.eq(id))
                    .list();
            session.runInTx(new Runnable() {
                @Override
                public void run() {
                    for (DataReport dr : drList) {
                        dr.setIsSended(true);
                        dr.setSerialNo(serialNo);
                        session.getDataReportDao().update(dr);
                    }
                }
            });
        }
    }

    public void deleteDataReport(final List<Long> listDataReportId) {
        for (Long id : listDataReportId) {
            final List<DataReport> drList = session.getDataReportDao().queryBuilder()
                    .where(DataReportDao.Properties.Id.eq(id))
                    .list();
            session.runInTx(new Runnable() {
                @Override
                public void run() {
                    for (DataReport dr : drList) {
                        session.getDataReportDao().delete(dr);
                    }
                }
            });
        }
    }


    public void deleteDataReport(final long serialNo) {
        final List<DataReport> drList = session.getDataReportDao().queryBuilder()
                .where(DataReportDao.Properties.SerialNo.eq(serialNo))
                .list();
        session.runInTx(new Runnable() {
            @Override
            public void run() {
                for (DataReport dr : drList) {
                    session.getDataReportDao().delete(dr);
                }
            }
        });
    }

    public List<DataReport> getDataReport() {
        return session.getDataReportDao().queryBuilder()
                .where(DataReportDao.Properties.IsSended.eq(false))
                .limit(Constants.DEFAULT_LIST_COUNT * 4)
                .list();
    }

    public List<DataReport> getDataReportSended() {
        return session.getDataReportDao().queryBuilder()
                .where(DataReportDao.Properties.IsSended.eq(true))
                .list();
    }

    public int getDataReportAllCount() {
        int num = 0;
        List<DataReport> drList = session.getDataReportDao().queryBuilder()
                .where(DataReportDao.Properties.IsSended.eq(false))
                .list();
        if (drList != null) {
            num = drList.size();
        }
        return num;
    }

}
