package com.tuxing.app.util;

import android.app.Activity;

import com.tuxing.app.R;
import com.tuxing.app.TuxingApp;
import com.tuxing.app.bean.TestListBean;
import com.tuxing.app.domain.HomeMenuItem;
import com.tuxing.sdk.db.entity.GameLevel;
import com.tuxing.sdk.modle.Attachment;
import com.tuxing.sdk.modle.DepartmentMember;
import com.tuxing.sdk.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 常量类
 */
public class SysConstants {

    public static final boolean isTest = true;

    public static final String H5_HOST_URL = "http://h5.tx2010.com/";

    public static final String wuyouhome = H5_HOST_URL + "insurance.do?intro";
    public static final String wuyouorder = H5_HOST_URL + "insurance.do?order";

//	public static final String HUODONGZQ = "http://h5.tx2010.com/activity.do?token=?";
//	public static final String CHOUJIANGJL = "http://h5.tx2010.com/activity.do?recordDraw&token=";
//	public static final String SHOP = "http://h5.tx2010.com/shop.do";


    public static final String GARDENURL = H5_HOST_URL + "cms/article.do?gardenIntro&gardenId=";
    public static final String AGREEMENTURL = H5_HOST_URL + "cms/article.do?agreement";

    public static String AGREEMENTURL_XCSD = "http://121.41.101.14:8080/cms/article.do?agreement";//服务协议
    public static final String AGREEMENTURL_XCSD_DEV_TEST = "http://121.41.101.14:8080/cms/article.do?agreement";
    public static final String AGREEMENTURL_XCSD_DIS = "http://service.xcsdedu.com/cms/article.do?agreement";


    public static String KURL_FEEDBACK = "http://121.41.101.14:8080/cms/article.do?feedback";//帮助与反馈
    public static final String KURL_FEEDBACK_DEV_TEST = "http://121.41.101.14:8080/cms/article.do?feedback";
    public static final String KURL_FEEDBACK_DIS = "http://service.xcsdedu.com/cms/article.do?feedback";


    public static final String HUODONGZQ = H5_HOST_URL + "activity.do?token=";
    public static final String CHOUJIANGJL = H5_HOST_URL + "activity.do?recordDraw&token=";
    public static final String SHOP = H5_HOST_URL + "shop.do";
    public static final String ACTIVITY_URL = H5_HOST_URL + "lottrery.do?activityId=%d&token=";
//	public static final String wuyouhome = "http://123.57.43.111:8080/insurance.do?intro";
//	public static final String wuyouorder = "http://123.57.43.111:8080/insurance.do?order";

    public static final String CHECK_IN_QD = "wjyteacher://check_in_with_user_id?user_id=%s&user_name=%s";//&user_type=%s&user_cardnumber=%s";
    public static final String UpdateJson = "update_json";
    public static final String DownloadId = "DownloadId";
    public static final int VOICEMAXLENTH = 160;//320
    public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
    public static final int VOICEMINLENTH = 80;//160
    public static final String GROUP_USERNAME = "item_groups";
    public static final String CHAT_ROOM = "item_chatroom";
    public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
    public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";
    public static final String ACCOUNT_REMOVED = "account_removed";
    public static final String NOTICE_NAME = "通知";
    public static final String HOMEWORK_NAME = "学能作业";
    public static final String WXY_NAME = "理解孩子";
    public static final String CARD_NAME = "云卫士刷卡";
    public static final String Imgurlsuffix80 = "?imageView2/1/w/160/h/160/format/jpg";
    public static final String Imgurlsuffix90 = "?imageView2/1/w/180/h/180/format/jpg";
    public static final String Imgurlsuffix90_png = "?imageView2/1/w/180/h/180/format/png";
    public static final String Imgurlsuffix120 = "?imageView2/1/w/160/h/120/format/jpg";
    public static final String Imgurlsuffix134 = "?imageView2/1/w/134/h/134/format/jpg";
    public static final String Imgurlsuffix306 = "?imageView2/1/w/230/h/306/format/jpg";
    public static final String Imgurlsuffix320 = "?imageView2/1/w/320/h/320/format/jpg";
    public static final String ImgurlJpg = "?imageView2/1/format/jpg";
    public static final String VIDEOSUFFIX80 = "?vframe/jpg/offset/0/w/160/h/160";
    public static final String VIDEOSUFFIX306 = "?vframe/jpg/offset/0/w/320/h/240";
    public static final String CHECK_IN_DESC = "您有一条新的刷卡信息";
    public static final String CHAT_GROUP_DEFAULT_DESC = "赶快向全班的家长老师打个招呼吧!";

    public static final String MEIQIAKEY = "558cff894eae35532b000003";
    public static final String STU_BUGAPPID = "900027875";
    public static final String TEA_BUGAPPID = "900028340";

    public static final String STU_XINGE_ACCESSID = "2100130671";
    public static final String STU_XINGE_ACCESSKEY = "A648ACAQ7A4T";

    public static final String TEA_XINGE_ACCESSID = "2100139125";
    public static final String TEA_XINGE_ACCESSKEY = "A7H7I4Y6AK5C";


//    public static final String STU_YOUMENG_APPKEY = "55daadda67e58e76840013dd";
    public static final String STU_YOUMENG_APPKEY = "5721cd3b67e58efb720011bf";
    public static final String TEA_YOUMENG_APPKEY = "5721cd0ee0f55ab3cf0013f4";

    public static final String LINK_WEB_REGEX  = "(((^https?:(?:\\/\\/)?)(?:[-;:&=\\+\\$,\\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\\+\\$,\\w]+@)[A-Za-z0-9.-]+)((?:\\/[\\+~%\\/.\\w-_]*)?\\??(?:[-\\+=&;%@.\\w_]*)#?(?:[\\w]*))?)$";
    public static final String LINK_PHONE_REGEX = "((\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$)";

    public static final String PHONEREG = "^13[0-9]{9}$|^14[0-9]{9}$|^15[0-9]{9}$|^18[0-9]{9}$|^17[0-9]{9}$|^400[0-9]{7}-?([1-9]{1}[0-9]{0,4})?$";
    public static final int CHILD = 1;
    public static final int PARENT = 2;
    public static final int TEACHER = 3;
    public static final int PARTNER = 4;

    public static final int MsgType = 1;

    public static final int commentType_1 = 1;
    public static final int commentType_2 = 2;
    /**
     * action
     */
    public static final String ANNOUNCEMENTACTION = ".announcementActivityAction";
    public static final String ACTIVITYACTION = ".activityActivityAction";
    public static final String RECIPEACTION = ".recipeActivityAction";
    public static final String TEST = ".TestActivityAction";
    public static final String CLASSROOM = ".ClassRoomListActivityAction";
    //以下的action要做分包处理
    public static final String COMMUNICATIONSACTION = ".communicationsActivityAction";
    public static final String TEACHERCLUBACTION = ".teacherhelpaction";
    public static final String STUDYHOMEWORKACTION = ".studyHomeWorkActivityAction";
    public static final String SWINGCARDACTION = ".swingCardActivityAction";
    public static final String NOTICEACTION = ".noticeActivityAction";
    public static final String HEYACTION = ".heyActivityAction";
    public static final String MAILBOXACTION = ".LeaderMailBoxActivityAction";
    public static final String CLOUDCARDACTION = ".cloudCardActivityAction";
    public static final String MYUSERINFOACTION = ".myUserInfoActivityAction";
    public static final String CLASSMANAGERACTION = ".classManagerActivityAction";
    public static final String PARENTACTION = ".userInfoParentActivityAction";
    public static final String TEAACTION = ".userInfoTeaActivityAction";
    public static final String CONTACTALLTEACHER = ".contactAllteacher";
    public static final String CONTACTALLPARENT = ".contactAllParent";
    public static final String CAPTUREACTION = ".CaptureAction";
    public static final String TRAIN = ".TrainActivityAction";
    public static final String StartTest = ".StartTestActivityAction";
    public static final String SCORESHOW = ".WorkScoreShowActivityAction";
    public static final String QUESTIONCIRCLEMSGLIST = ".questionCircleMsgListAction";
    /*end*/
    public static final String MSGLISTREFRESHACTION = "msglistRefreshaction";
    public static final String HOMEREFRESHACTION = "homeRefreshaction";
    public static final String BANNERREFRESHACTION = "bannerRefreshaction";
    public static final String FINISH_LOGIN = "finish_login";
    public static final String FINISH_ACTIVE = "finish_active";
    public static final String FINISH_MAIN = "finish_main";
    public static final String NETWORKERROR = "networkerror";
    public static final String LOGINHX = "loginhx";


    /**
     * BroadcastReceiver action
     */
    public static final String READNOTICEACTION = "read_notice_action";
    public static final String DELREFRESHACTION = "del_refresh_action";
    public static final String UPDATECIRCLELIST = "update_circle_list";
    public static final String UPDATENEWCOMMENT = "update_new_comment";
    public static final String UPDATENEWEXPLORE = "update_new_explore";
    public static final String TOUCHUANACTION = "com.tuxing.touchuan";
    public static final String REFRESHMSGLIST = "refresh_action";
    public static final String NETWORKCHANGE = "net_work_change";
    public static final String UPDATEMEDIACOMMENT = "update_media_comment";

    public static final String UPDATEQUESTION = "update_new_question";
    public static final String UPDATBAODIANLIST = "update_baodian_list";
    public static final String HIDEANDSHOWAUDIO = "hide_show_audio";
    public static final String PAUSAUDIO = "paus_audio";
    public static final String PLAYNEXT = "play_next_audio";

    public static final int VERIFICATION_CODE_RESET_PASSWORD = 0;
    public static final int VERIFICATION_CODE_CHANGE_MOBILE = 1;
    public static final int VERIFICATION_CODE_ACTIVATE_USER = 2;


    /**
     * touchuan action
     */

    public static final String TOUCHUAN_UNBINDUSER = "unbindUser";
    public static final String TOUCHUAN_PROFILECHANGE = "profileChange";
    public static final String TOUCHUAN_GAGUSER = "gagUser";
    public static final String TOUCHUAN_REVOKEMSG = "revokeMsg";


    /**
     * 图片缩放参照尺寸大小
     */
    public static final int IMAGEIMPLESIZE_256 = 512;


    /**
     * 日志存储目录
     */
    public static final String DATA_DIR_ROOT = Utils.savePath() + "/tuxing";
    /**
     * 图片存储目录
     */
    public static final String FILE_DIR_ROOT = DATA_DIR_ROOT + "/.files/";
    public static final String FILE_DIR_VIDEO = DATA_DIR_ROOT + "/video/";
    public static final String FILE_DIR_TEMP = DATA_DIR_ROOT + "/.temp/";
    public static final String FILE_upload_ROOT = DATA_DIR_ROOT + "/.uploads/";
    public static final String FILE_BIG_ROOT = DATA_DIR_ROOT + "/.bigfiles/";

    public static final String FILE_TEMP_DIR_ROOT = DATA_DIR_ROOT + "/image/";

    /**
     * 大图存储目录
     */
    public static final String DCIM_DIR_ROOT = DATA_DIR_ROOT + "/weijiayuan_img/";

    public static final String FILE_DCIM = Utils.savePath() + "/DCIM/Camera/";
    public static final String FILE_PICTURES = Utils.savePath() + "/Pictures/";


    /*************************
     * PreferenceUtils key
     ***************************/
    public static String currentUser = "currentUser";
    public static String userName = "userName";
    public static String passWord = "passWord";
    public static String userId = "userId";
    public static String remind_disturb = "msg_remind_disturb";
    public static String voice_remind = "msg_voice_remind";
    public static String shake_remind = "msg_shake_remind";
    public static String departmentdi_sturb = "msg_department_disturb";
    public static String ISCHATACTIVITY = "ischatActivity";
    public static String ISACTIVITYACTIVITY = "isActivityActivity";
    public static String XINGETOKEN = "xingetoken";
    public static String LAST_QUESTION_MESSAGE_TIME = "latest_question_msg_time_%d";
    public static String media_folw = "all_media_flow";

    /****************************************************/

    public static String WEICHAT_APPID = "";
    public static String WEICHAT_SECRET = "";

    public static String QQ_APPID = "";
    public static String QQ_APPKEY = "";

    /***********************************************************/
    public static final String MENU_ITEM_ACTIVITY = "activity";
    public static final String MENU_ITEM_ANNOUNCEMENT = "announcement";
    public static final String MENU_ITEM_NOTICE = "notice";
    public static final String MENU_ITEM_CHECKIN = "checkIn";
    public static final String MENU_ITEM_MEDICINE = "medicine";
    public static final String MENU_ITEM_MAILBOX = "mail";
    public static final String MENU_ITEM_INSURANCE = "insurance";
    public static final String MENU_ITEM_RECIPE = "recipes";
    //	add by mey
    public static final String MENU_ITEM_GAME = "game";
    public static final String MENU_ITEM_HOMEWORK = "homework";
    public static final String MENU_ITEM_ACHIEVEMENT = "achievement";
    public static final String MENU_ITEM_STUDENTSAFETY = "studententsefaty";
    public static final String MENU_ITEM_TEACHERCLUB = "teachercommunity";
    public static final String MENU_ITEM_COMMUNICATIONS = "addressList";
    public static final String MENU_ITEM_TEST = "themeTest";
    public static final String MENU_ITEM_CLASSROOM = "course";

    public static Map<String, HomeMenuItem> HOME_MENU_ITEMS = new HashMap<String, HomeMenuItem>();

    static {
        HOME_MENU_ITEMS.put(MENU_ITEM_ACTIVITY, new HomeMenuItem(
                R.drawable.jy_hd_p,
                "activity",
                Constants.COUNTER.ACTIVITY,
                TuxingApp.packageName + SysConstants.ACTIVITYACTION,
                false,""));
        HOME_MENU_ITEMS.put(MENU_ITEM_ANNOUNCEMENT, new HomeMenuItem(
                R.drawable.jy_gg_p,
                "announcement",
                Constants.COUNTER.ANNOUNCEMENT,
                TuxingApp.packageName + SysConstants.ANNOUNCEMENTACTION,
                false,""));
        HOME_MENU_ITEMS.put(MENU_ITEM_NOTICE, new HomeMenuItem(
                R.drawable.jy_tz_p,
                "notice",
                Constants.COUNTER.NOTICE,
                TuxingApp.packageName + SysConstants.NOTICEACTION,
                false,""));
        HOME_MENU_ITEMS.put(MENU_ITEM_CHECKIN, new HomeMenuItem(
                R.drawable.icon_sk,
                TuxingApp.getInstance().getString(R.string.swiping_card),
                Constants.COUNTER.CHECK_IN,
                TuxingApp.packageName + SysConstants.SWINGCARDACTION,
                false,""));
        HOME_MENU_ITEMS.put(MENU_ITEM_MEDICINE, new HomeMenuItem(
                R.drawable.icon_wy,
                TuxingApp.getInstance().getString(R.string.eat_drug),
                Constants.COUNTER.MEDICINE,
                TuxingApp.packageName + SysConstants.HEYACTION,
                true,""));
        HOME_MENU_ITEMS.put(MENU_ITEM_MAILBOX, new HomeMenuItem(
                R.drawable.icon_yzxx,
                TuxingApp.getInstance().getString(R.string.mailbox),
                Constants.COUNTER.MAIL,
                TuxingApp.packageName + SysConstants.MAILBOXACTION,
                true,""));
        HOME_MENU_ITEMS.put(MENU_ITEM_INSURANCE, new HomeMenuItem(
                R.drawable.icon_wuyouu,
                TuxingApp.getInstance().getString(R.string.zywyou),
                null,
                null,
                false,""));
        HOME_MENU_ITEMS.put(MENU_ITEM_RECIPE, new HomeMenuItem(
                R.drawable.icon_sp,
                TuxingApp.getInstance().getString(R.string.recipe),
                null,
                TuxingApp.packageName + RECIPEACTION,
                false,""));
        HOME_MENU_ITEMS.put(MENU_ITEM_GAME, new HomeMenuItem(
                R.drawable.jy_game_p,
                "game",
                null,
                null,
                false,""));
        HOME_MENU_ITEMS.put(MENU_ITEM_ACHIEVEMENT, new HomeMenuItem(
                R.drawable.jy_achievement_p,
                "achievement",
                null,
                null,
                false,""));
        HOME_MENU_ITEMS.put(MENU_ITEM_HOMEWORK, new HomeMenuItem(
                R.drawable.jy_homework_p,
                "homework",
                Constants.COUNTER.HOMEWORK,
                TuxingApp.packageName + SysConstants.STUDYHOMEWORKACTION,
                false,""));
        HOME_MENU_ITEMS.put(MENU_ITEM_STUDENTSAFETY, new HomeMenuItem(
                R.drawable.jy_childsafety_p,
                "studententsefaty",
                null,
                null,
                false,""));
        HOME_MENU_ITEMS.put(MENU_ITEM_TEACHERCLUB, new HomeMenuItem(
                R.drawable.jy_community_t,
                "teachercommunity",
                Constants.COUNTER.COMMUNION,
                TuxingApp.packageName + SysConstants.TEACHERCLUBACTION,
                false,""));
        HOME_MENU_ITEMS.put(MENU_ITEM_COMMUNICATIONS, new HomeMenuItem(
                R.drawable.jy_txl_t,
                "addressList",
                Constants.COUNTER.COMMUNICATIONS,
                TuxingApp.packageName + SysConstants.COMMUNICATIONSACTION,
                true,""));
        HOME_MENU_ITEMS.put(MENU_ITEM_TEST, new HomeMenuItem(
                R.drawable.jy_test_p,
                "themeTest",
                Constants.COUNTER.TEST,
                TuxingApp.packageName + SysConstants.TEST,
                true,""));
        HOME_MENU_ITEMS.put(MENU_ITEM_CLASSROOM, new HomeMenuItem(
                R.drawable.jy_classroom_p,
                "course",
                Constants.COUNTER.CLASSROOM,
                TuxingApp.packageName + SysConstants.CLASSROOM,
                true,""));

    }

    public static final String DEFAULT_HOME_MENU_ITEMS = "homework,game,achievement,course,teachercommunity,addressList,notice,announcement,activity";
    public static final String DEFAULT_HOME_MENU_ITEMS_P = "homework,game,achievement,themeTest,course,notice,announcement,activity";
    public static final String DEFAULT_HOME_MENU_teacher_ITEMS = "homeWork,game,achievement,announcement,notice,activity";
    public static final String DESCRIPTOR = "com.tuxing.app.share";

    public static final String QUESTION_CIRCLE_NAME = "教师成长";
    public static final String QUESTION_LAST_MESSAGE = "_QUESTION_LAST_MESSAGE";
    public static final String ASSESS_TEST_URL = "https://www.baidu.com";

    public static List<Attachment> attachments = new ArrayList<Attachment>();

    public static String H5_GAME_HOST_URL = "http://h5.xcsdedu.com/1.0/";

    //正式用的
    public static final String H5_GAME_HOST_URL_DIS = "http://h5.xcsdedu.com/1.0/";
    //开发和测试用的
    public static final String H5_GAME_HOST_URL_DEV_TEST = "http://121.41.101.14/html5/";

    public static final String HX_APPKEY_DIS="xcsdedu#lexuetangdis";//环信appkey正式
    public static final String HX_APPKEY_DEV="xcsdedu#lexuetangdev";//环信appkey开发
    public static final String HX_APPKEY_TEST="xcsdedu#lexuetangdev";//环信appkey测试


    public static String childID = "1";//本地测试用
    public static String childname = "";//本地测试用
    public static int GAMELEVEL = 0;//本地用控制游戏关卡不超过
    public static int GAMEmax = 0;//本地用控制游戏关卡不超过多少个
    public static List<GameLevel> game_Level = new ArrayList<GameLevel>();//本地用
    public static List<TestListBean> test_data_location = new ArrayList<TestListBean>();//本地用
    public static String HomeWorkTitle = "";//本地title用
    public static String TestTitle = "";//本地测试用
    public static boolean isIsTest = false;//本地测试用

    public static boolean isZan = false;//本地教师社区用
    public static String answerId = "";//教师社区用

    public static boolean ishome =true;//退出登录判断
    public static ArrayList<Activity> listActivitys = new ArrayList<Activity>();
    public static String titlename = "";//分享title
    public static String shareimage = "";//分享图片
    public static boolean messagein = false;//分享图片

    public static List<DepartmentMember> members = new ArrayList<>();
    public static List<DepartmentMember> memberteacher = new ArrayList<>();

    public class RESOURCES_TYPE {
        public static final String HOT = "HOT";
        public static final String LATEST = "LATEST";
        public static final String RECOMMEND = "RECOMMEND";
        public static final String PROVIDER = "PROVIDER";
        public static final String HISTORY = "HISTORY";
        public static final String RESOURCE = "RESOURCE";
        public static final String ALBUM = "ALBUM";
    }


    public static String KURL_THEME_TEST = "http://121.41.101.14:8082/service/service/";
    public static final String KURL_THEME_TEST_DEV_TEST = "http://121.41.101.14:8082/service/service/";//测评模块地址（开发，测试）
    public static final String KURL_THEME_TEST_DIS = "http://101.201.37.122:8090/service/";//测评模块地址（正式）

    public static String JSHostUrl = "http://121.41.101.14:8080/gamedata.py"; //测试服
    public static final String JSHostUrlTest = "http://121.41.101.14:8080/gamedata.py"; //测试服
    public static final String JSHostUrlDev = "http://121.40.16.212:8080/gamedata.py"; //开发服
    public static final String JSHostUrlDis = "http://service.xcsdedu.com/gamedata.py"; //线上服

    public class GameArgsKey {
        public class ActionEnum{
            public static final String gameLobby = "10001";
            public static final String gameTest = "10003";
            public static final String homeWork = "10004";
        }
        public static final String ACTION = "action";
        public static final String TOKEN = "token";
        public static final String MEMBER_ID = "memberId";
        public static final String GAME_LIST = "gameList";
        public static final String TASK_LIST = "taskList";
        public static final String IS_FIRST_TEST = "isFirstTest";
        public static final String TEST_ID = "testId";
        public static final String CHILD_USERID = "childUserId";
        public static final String Ability = "ability";
        public static final String ISTEACHER = "isTeacherApp";
    }
}
