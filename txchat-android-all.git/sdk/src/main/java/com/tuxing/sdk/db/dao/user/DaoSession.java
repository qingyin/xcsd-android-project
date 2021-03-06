package com.tuxing.sdk.db.dao.user;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.tuxing.sdk.db.entity.Notice;
import com.tuxing.sdk.db.entity.Department;
import com.tuxing.sdk.db.entity.DepartmentUser;
import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.db.entity.CheckInRecord;
import com.tuxing.sdk.db.entity.Setting;
import com.tuxing.sdk.db.entity.Comment;
import com.tuxing.sdk.db.entity.Feed;
import com.tuxing.sdk.db.entity.FeedMedicineTask;
import com.tuxing.sdk.db.entity.GardenMail;
import com.tuxing.sdk.db.entity.ContentItemGroup;
import com.tuxing.sdk.db.entity.ContentItem;
import com.tuxing.sdk.db.entity.RevokedMessage;
import com.tuxing.sdk.db.entity.ClassPicture;
import com.tuxing.sdk.db.entity.Activity;
import com.tuxing.sdk.db.entity.LightApp;
import com.tuxing.sdk.db.entity.HomeWorkRecord;
import com.tuxing.sdk.db.entity.HomeWorkClass;
import com.tuxing.sdk.db.entity.HomeWorkMember;
import com.tuxing.sdk.db.entity.HomeWorkUserRank;
import com.tuxing.sdk.db.entity.HomeWorkGenerate;
import com.tuxing.sdk.db.entity.HomeWorkDetail;
import com.tuxing.sdk.db.entity.GameLevel;
import com.tuxing.sdk.db.entity.AbilityDetailList;
import com.tuxing.sdk.db.entity.AbilityPoint;
import com.tuxing.sdk.db.entity.Game_Score;
import com.tuxing.sdk.db.entity.ScoreShow;
import com.tuxing.sdk.db.entity.TestList;
import com.tuxing.sdk.db.entity.CourseBean;
import com.tuxing.sdk.db.entity.CourseLessonBean;
import com.tuxing.sdk.db.entity.DataReport;

import com.tuxing.sdk.db.dao.user.NoticeDao;
import com.tuxing.sdk.db.dao.user.DepartmentDao;
import com.tuxing.sdk.db.dao.user.DepartmentUserDao;
import com.tuxing.sdk.db.dao.user.UserDao;
import com.tuxing.sdk.db.dao.user.CheckInRecordDao;
import com.tuxing.sdk.db.dao.user.SettingDao;
import com.tuxing.sdk.db.dao.user.CommentDao;
import com.tuxing.sdk.db.dao.user.FeedDao;
import com.tuxing.sdk.db.dao.user.FeedMedicineTaskDao;
import com.tuxing.sdk.db.dao.user.GardenMailDao;
import com.tuxing.sdk.db.dao.user.ContentItemGroupDao;
import com.tuxing.sdk.db.dao.user.ContentItemDao;
import com.tuxing.sdk.db.dao.user.RevokedMessageDao;
import com.tuxing.sdk.db.dao.user.ClassPictureDao;
import com.tuxing.sdk.db.dao.user.ActivityDao;
import com.tuxing.sdk.db.dao.user.LightAppDao;
import com.tuxing.sdk.db.dao.user.HomeWorkRecordDao;
import com.tuxing.sdk.db.dao.user.HomeWorkClassDao;
import com.tuxing.sdk.db.dao.user.HomeWorkMemberDao;
import com.tuxing.sdk.db.dao.user.HomeWorkUserRankDao;
import com.tuxing.sdk.db.dao.user.HomeWorkGenerateDao;
import com.tuxing.sdk.db.dao.user.HomeWorkDetailDao;
import com.tuxing.sdk.db.dao.user.GameLevelDao;
import com.tuxing.sdk.db.dao.user.AbilityDetailListDao;
import com.tuxing.sdk.db.dao.user.AbilityPointDao;
import com.tuxing.sdk.db.dao.user.Game_ScoreDao;
import com.tuxing.sdk.db.dao.user.ScoreShowDao;
import com.tuxing.sdk.db.dao.user.TestListDao;
import com.tuxing.sdk.db.dao.user.CourseDao;
import com.tuxing.sdk.db.dao.user.CourseLessonDao;
import com.tuxing.sdk.db.dao.user.DataReportDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig noticeDaoConfig;
    private final DaoConfig departmentDaoConfig;
    private final DaoConfig departmentUserDaoConfig;
    private final DaoConfig userDaoConfig;
    private final DaoConfig checkInRecordDaoConfig;
    private final DaoConfig settingDaoConfig;
    private final DaoConfig commentDaoConfig;
    private final DaoConfig feedDaoConfig;
    private final DaoConfig feedMedicineTaskDaoConfig;
    private final DaoConfig gardenMailDaoConfig;
    private final DaoConfig contentItemGroupDaoConfig;
    private final DaoConfig contentItemDaoConfig;
    private final DaoConfig revokedMessageDaoConfig;
    private final DaoConfig classPictureDaoConfig;
    private final DaoConfig activityDaoConfig;
    private final DaoConfig lightAppDaoConfig;
    private final DaoConfig homeWorkRecordDaoConfig;
    private final DaoConfig homeWorkClassDaoConfig;
    private final DaoConfig homeWorkMemberDaoConfig;
    private final DaoConfig homeWorkUserRankDaoConfig;
    private final DaoConfig homeWorkGenerateDaoConfig;
    private final DaoConfig homeWorkDetailDaoConfig;
    private final DaoConfig gameLevelDaoConfig;
    private final DaoConfig abilityDetailListDaoConfig;
    private final DaoConfig abilityPointDaoConfig;
    private final DaoConfig game_ScoreDaoConfig;
    private final DaoConfig scoreShowDaoConfig;
    private final DaoConfig testListDaoConfig;
    private final DaoConfig courseDaoConfig;
    private final DaoConfig courseLessonDaoConfig;
    private final DaoConfig dataReportDaoConfig;

    private final NoticeDao noticeDao;
    private final DepartmentDao departmentDao;
    private final DepartmentUserDao departmentUserDao;
    private final UserDao userDao;
    private final CheckInRecordDao checkInRecordDao;
    private final SettingDao settingDao;
    private final CommentDao commentDao;
    private final FeedDao feedDao;
    private final FeedMedicineTaskDao feedMedicineTaskDao;
    private final GardenMailDao gardenMailDao;
    private final ContentItemGroupDao contentItemGroupDao;
    private final ContentItemDao contentItemDao;
    private final RevokedMessageDao revokedMessageDao;
    private final ClassPictureDao classPictureDao;
    private final ActivityDao activityDao;
    private final LightAppDao lightAppDao;
    private final HomeWorkRecordDao homeWorkRecordDao;
    private final HomeWorkClassDao homeWorkClassDao;
    private final HomeWorkMemberDao homeWorkMemberDao;
    private final HomeWorkUserRankDao homeWorkUserRankDao;
    private final HomeWorkGenerateDao homeWorkGenerateDao;
    private final HomeWorkDetailDao homeWorkDetailDao;
    private final GameLevelDao gameLevelDao;
    private final AbilityDetailListDao abilityDetailListDao;
    private final AbilityPointDao abilityPointDao;
    private final Game_ScoreDao game_ScoreDao;
    private final ScoreShowDao scoreShowDao;
    private final TestListDao testListDao;
    private final CourseDao courseDao;
    private final CourseLessonDao courseLessonDao;
    private final DataReportDao dataReportDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        noticeDaoConfig = daoConfigMap.get(NoticeDao.class).clone();
        noticeDaoConfig.initIdentityScope(type);

        departmentDaoConfig = daoConfigMap.get(DepartmentDao.class).clone();
        departmentDaoConfig.initIdentityScope(type);

        departmentUserDaoConfig = daoConfigMap.get(DepartmentUserDao.class).clone();
        departmentUserDaoConfig.initIdentityScope(type);

        userDaoConfig = daoConfigMap.get(UserDao.class).clone();
        userDaoConfig.initIdentityScope(type);

        checkInRecordDaoConfig = daoConfigMap.get(CheckInRecordDao.class).clone();
        checkInRecordDaoConfig.initIdentityScope(type);

        settingDaoConfig = daoConfigMap.get(SettingDao.class).clone();
        settingDaoConfig.initIdentityScope(type);

        commentDaoConfig = daoConfigMap.get(CommentDao.class).clone();
        commentDaoConfig.initIdentityScope(type);

        feedDaoConfig = daoConfigMap.get(FeedDao.class).clone();
        feedDaoConfig.initIdentityScope(type);

        feedMedicineTaskDaoConfig = daoConfigMap.get(FeedMedicineTaskDao.class).clone();
        feedMedicineTaskDaoConfig.initIdentityScope(type);

        gardenMailDaoConfig = daoConfigMap.get(GardenMailDao.class).clone();
        gardenMailDaoConfig.initIdentityScope(type);

        contentItemGroupDaoConfig = daoConfigMap.get(ContentItemGroupDao.class).clone();
        contentItemGroupDaoConfig.initIdentityScope(type);

        contentItemDaoConfig = daoConfigMap.get(ContentItemDao.class).clone();
        contentItemDaoConfig.initIdentityScope(type);

        revokedMessageDaoConfig = daoConfigMap.get(RevokedMessageDao.class).clone();
        revokedMessageDaoConfig.initIdentityScope(type);

        classPictureDaoConfig = daoConfigMap.get(ClassPictureDao.class).clone();
        classPictureDaoConfig.initIdentityScope(type);

        activityDaoConfig = daoConfigMap.get(ActivityDao.class).clone();
        activityDaoConfig.initIdentityScope(type);

        lightAppDaoConfig = daoConfigMap.get(LightAppDao.class).clone();
        lightAppDaoConfig.initIdentityScope(type);

        homeWorkRecordDaoConfig = daoConfigMap.get(HomeWorkRecordDao.class).clone();
        homeWorkRecordDaoConfig.initIdentityScope(type);

        homeWorkClassDaoConfig = daoConfigMap.get(HomeWorkClassDao.class).clone();
        homeWorkClassDaoConfig.initIdentityScope(type);

        homeWorkMemberDaoConfig = daoConfigMap.get(HomeWorkMemberDao.class).clone();
        homeWorkMemberDaoConfig.initIdentityScope(type);

        homeWorkUserRankDaoConfig = daoConfigMap.get(HomeWorkUserRankDao.class).clone();
        homeWorkUserRankDaoConfig.initIdentityScope(type);

        homeWorkGenerateDaoConfig = daoConfigMap.get(HomeWorkGenerateDao.class).clone();
        homeWorkGenerateDaoConfig.initIdentityScope(type);

        homeWorkDetailDaoConfig = daoConfigMap.get(HomeWorkDetailDao.class).clone();
        homeWorkDetailDaoConfig.initIdentityScope(type);

        gameLevelDaoConfig = daoConfigMap.get(GameLevelDao.class).clone();
        gameLevelDaoConfig.initIdentityScope(type);

        abilityDetailListDaoConfig = daoConfigMap.get(AbilityDetailListDao.class).clone();
        abilityDetailListDaoConfig.initIdentityScope(type);

        abilityPointDaoConfig = daoConfigMap.get(AbilityPointDao.class).clone();
        abilityPointDaoConfig.initIdentityScope(type);

        game_ScoreDaoConfig = daoConfigMap.get(Game_ScoreDao.class).clone();
        game_ScoreDaoConfig.initIdentityScope(type);

        scoreShowDaoConfig = daoConfigMap.get(ScoreShowDao.class).clone();
        scoreShowDaoConfig.initIdentityScope(type);

        testListDaoConfig = daoConfigMap.get(TestListDao.class).clone();
        testListDaoConfig.initIdentityScope(type);

        courseDaoConfig = daoConfigMap.get(CourseDao.class).clone();
        courseDaoConfig.initIdentityScope(type);

        courseLessonDaoConfig = daoConfigMap.get(CourseLessonDao.class).clone();
        courseLessonDaoConfig.initIdentityScope(type);

        dataReportDaoConfig = daoConfigMap.get(DataReportDao.class).clone();
        dataReportDaoConfig.initIdentityScope(type);

        noticeDao = new NoticeDao(noticeDaoConfig, this);
        departmentDao = new DepartmentDao(departmentDaoConfig, this);
        departmentUserDao = new DepartmentUserDao(departmentUserDaoConfig, this);
        userDao = new UserDao(userDaoConfig, this);
        checkInRecordDao = new CheckInRecordDao(checkInRecordDaoConfig, this);
        settingDao = new SettingDao(settingDaoConfig, this);
        commentDao = new CommentDao(commentDaoConfig, this);
        feedDao = new FeedDao(feedDaoConfig, this);
        feedMedicineTaskDao = new FeedMedicineTaskDao(feedMedicineTaskDaoConfig, this);
        gardenMailDao = new GardenMailDao(gardenMailDaoConfig, this);
        contentItemGroupDao = new ContentItemGroupDao(contentItemGroupDaoConfig, this);
        contentItemDao = new ContentItemDao(contentItemDaoConfig, this);
        revokedMessageDao = new RevokedMessageDao(revokedMessageDaoConfig, this);
        classPictureDao = new ClassPictureDao(classPictureDaoConfig, this);
        activityDao = new ActivityDao(activityDaoConfig, this);
        lightAppDao = new LightAppDao(lightAppDaoConfig, this);
        homeWorkRecordDao = new HomeWorkRecordDao(homeWorkRecordDaoConfig, this);
        homeWorkClassDao = new HomeWorkClassDao(homeWorkClassDaoConfig, this);
        homeWorkMemberDao = new HomeWorkMemberDao(homeWorkMemberDaoConfig, this);
        homeWorkUserRankDao = new HomeWorkUserRankDao(homeWorkUserRankDaoConfig, this);
        homeWorkGenerateDao = new HomeWorkGenerateDao(homeWorkGenerateDaoConfig, this);
        homeWorkDetailDao = new HomeWorkDetailDao(homeWorkDetailDaoConfig, this);
        gameLevelDao = new GameLevelDao(gameLevelDaoConfig, this);
        abilityDetailListDao = new AbilityDetailListDao(abilityDetailListDaoConfig, this);
        abilityPointDao = new AbilityPointDao(abilityPointDaoConfig, this);
        game_ScoreDao = new Game_ScoreDao(game_ScoreDaoConfig, this);
        scoreShowDao = new ScoreShowDao(scoreShowDaoConfig, this);
        testListDao = new TestListDao(testListDaoConfig, this);
        courseDao = new CourseDao(courseDaoConfig, this);
        courseLessonDao = new CourseLessonDao(courseLessonDaoConfig, this);
        dataReportDao = new DataReportDao(dataReportDaoConfig, this);

        registerDao(Notice.class, noticeDao);
        registerDao(Department.class, departmentDao);
        registerDao(DepartmentUser.class, departmentUserDao);
        registerDao(User.class, userDao);
        registerDao(CheckInRecord.class, checkInRecordDao);
        registerDao(Setting.class, settingDao);
        registerDao(Comment.class, commentDao);
        registerDao(Feed.class, feedDao);
        registerDao(FeedMedicineTask.class, feedMedicineTaskDao);
        registerDao(GardenMail.class, gardenMailDao);
        registerDao(ContentItemGroup.class, contentItemGroupDao);
        registerDao(ContentItem.class, contentItemDao);
        registerDao(RevokedMessage.class, revokedMessageDao);
        registerDao(ClassPicture.class, classPictureDao);
        registerDao(Activity.class, activityDao);
        registerDao(LightApp.class, lightAppDao);
        registerDao(HomeWorkRecord.class, homeWorkRecordDao);
        registerDao(HomeWorkClass.class, homeWorkClassDao);
        registerDao(HomeWorkMember.class, homeWorkMemberDao);
        registerDao(HomeWorkUserRank.class, homeWorkUserRankDao);
        registerDao(HomeWorkGenerate.class, homeWorkGenerateDao);
        registerDao(HomeWorkDetail.class, homeWorkDetailDao);
        registerDao(GameLevel.class, gameLevelDao);
        registerDao(AbilityDetailList.class, abilityDetailListDao);
        registerDao(AbilityPoint.class, abilityPointDao);
        registerDao(Game_Score.class, game_ScoreDao);
        registerDao(ScoreShow.class, scoreShowDao);
        registerDao(TestList.class, testListDao);
        registerDao(CourseBean.class, courseDao);
        registerDao(CourseLessonBean.class, courseLessonDao);
        registerDao(DataReport.class, dataReportDao);
    }
    
    public void clear() {
        noticeDaoConfig.getIdentityScope().clear();
        departmentDaoConfig.getIdentityScope().clear();
        departmentUserDaoConfig.getIdentityScope().clear();
        userDaoConfig.getIdentityScope().clear();
        checkInRecordDaoConfig.getIdentityScope().clear();
        settingDaoConfig.getIdentityScope().clear();
        commentDaoConfig.getIdentityScope().clear();
        feedDaoConfig.getIdentityScope().clear();
        feedMedicineTaskDaoConfig.getIdentityScope().clear();
        gardenMailDaoConfig.getIdentityScope().clear();
        contentItemGroupDaoConfig.getIdentityScope().clear();
        contentItemDaoConfig.getIdentityScope().clear();
        revokedMessageDaoConfig.getIdentityScope().clear();
        classPictureDaoConfig.getIdentityScope().clear();
        activityDaoConfig.getIdentityScope().clear();
        lightAppDaoConfig.getIdentityScope().clear();
        homeWorkRecordDaoConfig.getIdentityScope().clear();
        homeWorkClassDaoConfig.getIdentityScope().clear();
        homeWorkMemberDaoConfig.getIdentityScope().clear();
        homeWorkUserRankDaoConfig.getIdentityScope().clear();
        homeWorkGenerateDaoConfig.getIdentityScope().clear();
        homeWorkDetailDaoConfig.getIdentityScope().clear();
        gameLevelDaoConfig.getIdentityScope().clear();
        abilityDetailListDaoConfig.getIdentityScope().clear();
        abilityPointDaoConfig.getIdentityScope().clear();
        game_ScoreDaoConfig.getIdentityScope().clear();
        scoreShowDaoConfig.getIdentityScope().clear();
        testListDaoConfig.getIdentityScope().clear();
        courseDaoConfig.getIdentityScope().clear();
        courseLessonDaoConfig.getIdentityScope().clear();
        dataReportDaoConfig.getIdentityScope().clear();
    }

    public NoticeDao getNoticeDao() {
        return noticeDao;
    }

    public DepartmentDao getDepartmentDao() {
        return departmentDao;
    }

    public DepartmentUserDao getDepartmentUserDao() {
        return departmentUserDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public CheckInRecordDao getCheckInRecordDao() {
        return checkInRecordDao;
    }

    public SettingDao getSettingDao() {
        return settingDao;
    }

    public CommentDao getCommentDao() {
        return commentDao;
    }

    public FeedDao getFeedDao() {
        return feedDao;
    }

    public FeedMedicineTaskDao getFeedMedicineTaskDao() {
        return feedMedicineTaskDao;
    }

    public GardenMailDao getGardenMailDao() {
        return gardenMailDao;
    }

    public ContentItemGroupDao getContentItemGroupDao() {
        return contentItemGroupDao;
    }

    public ContentItemDao getContentItemDao() {
        return contentItemDao;
    }

    public RevokedMessageDao getRevokedMessageDao() {
        return revokedMessageDao;
    }

    public ClassPictureDao getClassPictureDao() {
        return classPictureDao;
    }

    public ActivityDao getActivityDao() {
        return activityDao;
    }

    public LightAppDao getLightAppDao() {
        return lightAppDao;
    }

    public HomeWorkRecordDao getHomeWorkRecordDao() {
        return homeWorkRecordDao;
    }

    public HomeWorkClassDao getHomeWorkClassDao() {
        return homeWorkClassDao;
    }

    public HomeWorkMemberDao getHomeWorkMemberDao() {
        return homeWorkMemberDao;
    }

    public HomeWorkUserRankDao getHomeWorkUserRankDao() {
        return homeWorkUserRankDao;
    }

    public HomeWorkGenerateDao getHomeWorkGenerateDao() {
        return homeWorkGenerateDao;
    }

    public HomeWorkDetailDao getHomeWorkDetailDao() {
        return homeWorkDetailDao;
    }

    public GameLevelDao getGameLevelDao() {
        return gameLevelDao;
    }

    public AbilityDetailListDao getAbilityDetailListDao() {
        return abilityDetailListDao;
    }

    public AbilityPointDao getAbilityPointDao() {
        return abilityPointDao;
    }

    public Game_ScoreDao getGame_ScoreDao() {
        return game_ScoreDao;
    }

    public ScoreShowDao getScoreShowDao() {
        return scoreShowDao;
    }

    public TestListDao getTestListDao() {
        return testListDao;
    }

    public CourseDao getCourseDao() {
        return courseDao;
    }

    public CourseLessonDao getCourseLessonDao() {
        return courseLessonDao;
    }

    public DataReportDao getDataReportDao() {
        return dataReportDao;
    }

}
