package com.tuxing.sdk.utils;

import android.os.Environment;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alan on 2015/5/17.
 */
public class Constants {

    /***
     * DB FILE
     */
//    public static final String QI_NIU_DOMAIN = "http://s.tx2010.com/";
    public static String QI_NIU_DOMAIN = "http://o7m2r7gaj.bkt.clouddn.com/";
    public static final String QI_NIU_DOMAIN_DEV_TEST = "http://o7m2r7gaj.bkt.clouddn.com/";
    public static final String QI_NIU_DOMAIN_DIS = "http://resource.xcsdedu.com/";

    public static final String APP_ROOT_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "tuxing";
    public static final String GLOBAL_DB_FILE = "global.dat";
    public static final String UPLOAD_DB_FILE = "upload_%d.dat";
    public static final String USER_DB_FILE = "%d.dat";
    public static final String app_prefix = "wjy";

    public static final int EVENT_PRIORITY_SERVICE = 10;
    public static final int EVENT_PRIORITY_MANAGER = 9;


    public static final String MSG_CANNOT_CONNECT_TO_SERVER = "无法连接服务器";

    /***
     * QUERY LIMIT
     */
    public static final int DEFAULT_LIST_COUNT = 20;

    public static final int CHECK_IN_UPLOAD_IMAGE_LIST_COUNT = 10;

    public static final int CHECK_IN_UPLOAD_DATA_LIST_COUNT = 50;

    /***
     * MAILBOX
     */
    public static final int MAILBOX_INBOX = 0;
    public static final int MAILBOX_OUTBOX = 1;


    /***
     * INTERVAL
     */
    public static final int ALARM_REPEAT_INTERVAL = 60;


    /***
     * SETTING FILED
     */
    public class SETTING_FIELD{
        public static final String CONTACT_LAST_SYNC = "CONTACT_LAST_SYNC";
        public static final String USER_CARD_NUM = "CARD_%d";
        public static final String GARDEN_ID = "GARDEN_ID";
        public static final String MACHINE_ID = "MACHINE_ID";
        public static final String GARDEN_NAME = "GARDEN_NAME";
        public static final String MUTE = "mute";
        public static final String HOME_MENU = "home_menu";
        public static final String HOME_NAME = "home_menu_name";
        public static final String FEED_MUTE = "feed_mute";
        public static final String GARDEN_IMAGE = "garden_image";
        public static final String USER_GRADE = "grade";
        public static final String USER_GRADE_NAME = "grade_name";
        public static final String RANK_NAME = "ranking_name";
        public static final String HOME_BANNERS = "home_banners";
    }

    public class RESPONSE_STATUS{
        public static final int OK = 200;
        public static final int NOT_FOUND = 404;
        public static final int NOT_ALLOWED = 403;
        public static final int ERROR = 500;

        public static final int KICK_OFF = 1001; //被T
        public static final int TOKEN_EXPIRED = 1002;//TOKEN过期
    }

    public class CHECK_IN_STATE{
        public static final int SAVED = 1;
        public static final int UPLOADED = 2;
        public static final int ERROR = 3;
    }

    public class ATTACHMENT_TYPE{
        public static final int IMAGE = 1;
        public static final int AUDIO = 2;
        public static final int VIDEO = 3;
        public static final int OTHER = 4;
    }

    public class MUTE_TYPE{
        public static final int MUTE_CHAT = 1;
        public static final int MUTE_FEED = 2;
    }

    public class ATTACHMENT_CATEGORY{
        public static final int NOTICE = 1;
        public static final int CHECK_IN = 2;
    }

    public class ATTACHMENT_STATUS{
        public static final int NOT_UPLOAD = 1;
        public static final int UPLOAD_IN_PROGRESS = 3;
        public static final int UPLOAD_COMPLETED = 5;
        public static final int UPLOAD_FAILED = 7;

    }

    public class USER_TYPE{
        public static final int CHILD = 1;
        public static final int PARENT = 2;
        public static final int TEACHER = 3;
        public static final int PARTNER = 4;
    }

    public class RELATIVE_TYPE{
        public static final int FATHER = 1;
        public static final int MOTHER = 2;
        public static final int PATERNAL_GRANDFATHER = 3;
        public static final int PATERNAL_GRANDMOTHER = 4;
        public static final int MATERNAL_GRANDFATHER = 5;
        public static final int MATERNAL_GRANDMOTHER = 6;
        public static final int OTHER = 100;
    }

    public static final int[] RELATIVE_TYPE_ARRAY = {
            RELATIVE_TYPE.FATHER,
            RELATIVE_TYPE.MOTHER,
            RELATIVE_TYPE.MATERNAL_GRANDFATHER,
            RELATIVE_TYPE.MATERNAL_GRANDMOTHER,
            RELATIVE_TYPE.PATERNAL_GRANDFATHER,
            RELATIVE_TYPE.PATERNAL_GRANDMOTHER,
            RELATIVE_TYPE.OTHER
    };

    public class LIGHT_APP_SHOW_AT{
        public static final int HOME = 1; // 家园
    }


    public class DEPARTMENT_TYPE{
        public static final int GARDEN = 1;
        public static final int CLASS = 2;
        public static final int OTHER = 3;
        public static final int SCHOOL = 11;
        public static final int GRADE = 12;
    }

    public class DISBURBED_FREE_SETTING{
        public static final int CLOSE = 1;//关闭
        public static final int NIGHT = 2;//夜间
        public static final int ALL = 3;//全天
    }

    //性别类型
    public class GENDER{
        public static final int FEMALE = 1;//女
        public static final int MALE = 2;//男
    }

    //禁言类型
    public class GAG_SETTING{
        public static final int FORBIDDEN = 1;//关闭
        public static final int OPEN = 2;//开启
    }

    //职位类型
    public class POSITION_TYPE{
        public static final int LEADER = 1;//园长
    }

    public class COMMENT_TYPE{
        public static final int LIKE = 1;//点赞
        public static final int REPLY = 2;//回复

    }

    public class  TARGET_TYPE {
        public static final int FEED = 1; // 亲子圈
        public static final int GARDEN_MAIL = 2; //园长信箱
        public static final int FEED_MEDICIN_TASK = 3; //喂药
    }

    public class FEED_TYPE {
        public static final int USER_FEED = 1; // 亲子圈
        public static final int ACTIVITY_FEED = 2; // 活动
    }

    public class CONTENT_TYPE{
        public static final int ACTIVITY = 1; // 活动
        public static final int ANNOUNCEMENT = 2; //公告
        public static final int LEARNGARDEN = 3; //微学院
        public static final int INTRO = 4; // 园介绍
        public static final int RECIPES = 5; //食谱
    }

    public class COUNTER{
        public static final String MAIL = "mail";    //园长信箱
        public static final String NOTICE = "notice";  //通知
        public static final String MEDICINE = "medicine";  //喂药
        public static final String CHECK_IN = "checkIn";  //刷卡
        public static final String ACTIVITY = "activity";  //活动
        public static final String ANNOUNCEMENT = "announcement";  //公告
        public static final String FEED = "feed";   //家园
        public static final String LEARN_GARDEN = "learn_garden";   //家园
        public static final String COMMENT = "comment";   //家园
        public static final String HOMEWORK = "homework"; //homeWork
        public static final String COMMUNION = "communion"; //教师帮
        public static  final String TEACHERCLUB="teacherclub";//教师社区
        public  static final String STUDENTSAFETY="studentsafety";//学生安全
        public static final  String COMMUNICATIONS="communications";//通讯录
        public static final  String TEST="test";//测评
        public static final  String CLASSROOM="course";//微课堂
    }

    public enum VERSION{
        PARENT("parent"),
        TEACHER("teacher"),
        MACHINE("machine");

        String version;
        VERSION(String version){
            this.version = version;
        }

        public String getVersion() {
            return version;
        }
    }

    public class VERIFY_CODE_TYPE{
        public static final int FORGET_PASSWORD = 1; //忘记密码
        public static final int ACTIVATE = 2; //激活
        public static final int INVITATION_ACTIVATE = 3; //邀请激活
        public static final int UPDATE_MOBILE = 4; //修改手机号
    }

    public static final int MESSAGE_TAB_INDEX = 0;
    public static final int HOME_TAB_INDEX = 1;
    public static final int EXPLORE_TAB_INDEX = 2;
    public static final int SETTING_TAB_INDEX = 3;


    public static final Map<String, List<Integer>> COUNTER_TAB_MAP = new HashMap<>();

    public static final Map<Integer, List<String>> TAB_COUNTER_MAP = new HashMap<>();

    static{
        COUNTER_TAB_MAP.put(COUNTER.ACTIVITY, CollectionUtils.asList(HOME_TAB_INDEX));
        COUNTER_TAB_MAP.put(COUNTER.ANNOUNCEMENT, CollectionUtils.asList(HOME_TAB_INDEX));
        COUNTER_TAB_MAP.put(COUNTER.MEDICINE, CollectionUtils.asList(HOME_TAB_INDEX));
        COUNTER_TAB_MAP.put(COUNTER.MAIL, CollectionUtils.asList(HOME_TAB_INDEX));
        COUNTER_TAB_MAP.put(COUNTER.NOTICE, CollectionUtils.asList(MESSAGE_TAB_INDEX, HOME_TAB_INDEX));
        COUNTER_TAB_MAP.put(COUNTER.CHECK_IN, CollectionUtils.asList(MESSAGE_TAB_INDEX, HOME_TAB_INDEX));
        COUNTER_TAB_MAP.put(COUNTER.FEED, CollectionUtils.asList(EXPLORE_TAB_INDEX));
        COUNTER_TAB_MAP.put(COUNTER.LEARN_GARDEN, CollectionUtils.asList(MESSAGE_TAB_INDEX));
        COUNTER_TAB_MAP.put(COUNTER.COMMENT, CollectionUtils.asList(EXPLORE_TAB_INDEX));
        COUNTER_TAB_MAP.put(COUNTER.HOMEWORK, CollectionUtils.asList(MESSAGE_TAB_INDEX, HOME_TAB_INDEX));
        COUNTER_TAB_MAP.put(COUNTER.COMMUNION, CollectionUtils.asList(MESSAGE_TAB_INDEX, EXPLORE_TAB_INDEX));

        TAB_COUNTER_MAP.put(MESSAGE_TAB_INDEX, CollectionUtils.asList(COUNTER.NOTICE, COUNTER.LEARN_GARDEN, COUNTER.CHECK_IN,COUNTER.HOMEWORK,COUNTER.COMMUNION));
        TAB_COUNTER_MAP.put(HOME_TAB_INDEX, CollectionUtils.asList(COUNTER.NOTICE, COUNTER.ACTIVITY, COUNTER.CHECK_IN,
                COUNTER.ANNOUNCEMENT, COUNTER.MEDICINE, COUNTER.MAIL,COUNTER.COMMUNION,COUNTER.HOMEWORK));
        TAB_COUNTER_MAP.put(EXPLORE_TAB_INDEX, CollectionUtils.asList(COUNTER.FEED, COUNTER.COMMENT));
        TAB_COUNTER_MAP.put(EXPLORE_TAB_INDEX, CollectionUtils.asList(COUNTER.FEED, COUNTER.COMMENT, COUNTER.COMMUNION));
    }


    public class HOMEWORK_STATUS{
        public static final int UNFINISHED = 0;
        public static final int FINISHED = 1;
    }
    public class HOMEWORK_TYPE{
        public static final int CUSTOMIZED = 1;		//定制作业
        public static final int UNIFIED = 2;		//统一作业
    }
    public class STUDENT_SCOPE{ //定制作业 选择学生的范围
        public static final int ALL = 1;
        public static final int NORMAL = 2;
        public static final int SPECIAL = 3;
    }

}
