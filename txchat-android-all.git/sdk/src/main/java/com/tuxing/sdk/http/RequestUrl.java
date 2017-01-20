package com.tuxing.sdk.http;

/**
 * Created by Alan on 2015/6/10.
 */
public class RequestUrl {
    public static final String LOGIN = "/login";

    public static final String LOGOUT = "/logout";

    public static final String BIND_CHILD = "/bind_child";

    public static final String FETCH_NOTICE = "/fetch_notice";

    public static final String BIND_CARD = "/bind_card";

    public static final String SEND_SMS_CODE = "/send_sms_code";

    public static final String SEND_NOTICE = "/send_notice";

    public static final String GET_COURSE_LIST = "/fetch_course_list";
    public static final String GET_COURSE = "/fetch_course";
    public static final String GET_COURSELESSON = "/fetch_course_lesson";
    public static final String ADD_COURSECOMMENT = "/add_course_comment";
    public static final String MODIFY_COURSECOMMENT = "/edit_course_comment";
    public static final String PLAY_COURSE = "/play_course_lesson";
    public static final String GET_COURSECOMMENT_LIST = "/fetch_course_comment";
    public static final String GET_COURSECOMMENT = "/fetch_user_course_comment";
    public static final String GET_COURSELESSON_LIST = "/fetch_course_lesson_list";


    public static final String HOMEWORK_DETAILS = "/homework/detail";

    public static final String HOMEWORK_GENERATE_DETAILS = "/homework/generate_detail";

    public static final String HOMEWORK_GAME_LIST = "/game/list";

    public static final String HOMEWORK_GAME_LIST_SEND = "/homework/send_unified";

    public static final String GET_NOTICE_RECEIVER_LIST = "/fetch_notice_departments";

    public static final String GET_NOTICE_RECEIVER_STATUS = "/fetch_notice_members";

    public static final String SYNC_CONTACT = "/fetch_contacts";

    public static final String SYNC_DEPARTMENT_MEMBERS = "/fetch_department_members";

    public static final String GET_DEPARTMENT_INFO = "/fetch_departmentinfo";

    public static final String GET_DEPARTMENT_INFO_BY_GROUP_ID = "/fetch_department_by_groupId";

    public static final String GET_USER_INFO = "/fetch_userinfo";

    public static final String FETCH_CHECKIN_RECORD = "/fetch_checkin";

    public static final String GET_CLOUD_STORAGE_TOKEN = "/get_uploadinfo";

    public static final String GET_CHECK_IN_STORAGE_TOKEN = "/get_checkin_upload_token";

    public static final String GET_BIND_CARD = "/fetch_user_card";

    public static final String REPORT_LOST_CARD = "/report_loss_card";

    public static final String MARK_NOTICE_READ = "/read_notice";

    public static final String GET_CHILD = "/fetch_child";

    public static final String ACTIVE_USER = "/active_user";

    public static final String SET_USER_PROFILE = "/save_user_profile";

    public static final String FETCH_USER_PROFILE = "/fetch_user_profile";

    public static final String RESET_PASSWORD = "/set_password";

    public static final String UPDATE_MOBILE = "/update_mobile";

    public static final String UPDATE_BINDING = "/update_bind";

    public static final String UPDATE_USER_INFO = "/update_user_info";

    public static final String CHANGE_PASSWORD = "/update_password";

    public static final String ADD_RELATIVE = "/active_invite_user";

    public static final String REMOVE_RELATIVE = "/relieve_bind";

    public static final String GET_BIND_RELATIVE = "/get_binding_parent_list";

    public static final String FETCH_FEED_MEDICINE_TASK = "/fetch_feed_medicine_task";

    public static final String ADD_FEED_MEDICINE_TASK = "/send_feed_medicine_task";

    public static final String FETCH_GARDEN_MAIL = "/fetch_garden_mail";

    public static final String SEND_GARDEN_MAIL = "/send_garden_mail";

    public static final String SEND_COMMENT = "/send_comment";

    public static final String SHOW_COMMENT = "/show_comments";

    public static final String FETCH_FEED = "/fetch_feed";

    public static final String FETCH_USER_FEED = "/fetch_user_feed";

    public static final String DELETE_FEED = "/delete_feed";

    public static final String PUBLISH_FEED = "/send_feed";

    public static final String DELETE_COMMENT = "/delete_comment";

    public static final String FETCH_COMMENT_TO_ME = "/fetch_comments_to_me";

    public static final String FETCH_POST = "/fetch_post";

    public static final String FETCH_POST_GROUP = "/fetch_postgroup";

    public static final String FETCH_POST_DETAIL = "/fetch_post_detail";

    public static final String FETCH_GARDEN_INTRO = "/fetch_garden_intro";

    public static final String FETCH_AGREEMENT = "/fetch_agreement";

    public static final String UPDATE_COUNTER = "/fetch_counter";

    public static final String UPDATE_DEVICE_TOKEN = "/update_device_token";

    public static final String REGISTER_CHECK_IN_MACHINE = "/register_checkin_machine";

    public static final String SYNC_CARD = "/fetch_card";

    public static final String CHECK_IN = "/checkin";

    public static final String MARK_MEDICINE_TASK_AS_READ = "/read_feed_medicine";

    public static final String MARK_GARDEN_MAIL_AS_READ = "/read_garden_mail";

    public static final String FETCH_CONCERNED_COMMENT = "/fetch_concerned_comment";

    public static final String UPLOAD_DEBUG_FILE_INFO = "/collect_log";

    public static final String MUTE = "/mute";

    public static final String UNMUTE = "/unmute";

    public static final String FETCH_MUTED_LIST = "/fetch_mute";

    public static final String UPGRADE = "/upgrade";

    public static final String UPGRADE_MACHINE = "/card_machine_upgrade";

    public static final String CLEAR_NOTICE = "/clear_notice";

    public static final String CLEAR_CHECK_IN = "/clear_check_in";

    public static final String FETCH_CLASS_PICTURE = "/fetch_department_photo";

    public static final String USER_CHECK_IN = "/user_check_in";

    public static final String SCAN_CODE_CHECK_IN = "/scan_code_checkin";

    public static final String HOMEWORK_LIST = "/homework/list";
    public static final String HOMEWORK_READ_NOTICE = "/homework/read_notice";
    public static final String HOMEWORK_DELETE_NOTICE = "/homework/delete_notice";
    public static final String HOMEWORK_RANKING = "/homework/ranking";
    public static final String HOMEWORK_CALENDAR = "/homework/calendar";

    public static final String HOMEWORK_SENT_LIST = "/homework/sent_list";
    public static final String HOMEWORK_MEMBERS = "/homework/members";
    public static final String HOMEWORK_GENERATE = "/homework/generate";
    public static final String HOMEWORK_SEND = "/homework/send";
    public static final String HOMEWORK_REMAINING_COUNT = "/homework/remaining_count";

    public static final String LEARNING_ABILITY_RANKING = "/learning_ability/ranking";
    public static final String LEARNING_ABILITY = "/learning_ability";
    public static final String GAME_TEST = "/game_test";
    public static final String GAME_SCORE = "/learning_ability/game_stat";


    public static final String FETCH_TOP_HOT_QUESTION = "/fetch_hot_questions";
    public static final String FETCH_QUESTION = "/fetch_questions";
    public static final String ASK_QUESTION = "/ask_question";
    public static final String GET_QUESTION_TAGS = "/fetch_tags";
    public static final String ANSWER_QUESTION = "/answer_question";
    public static final String GET_ANSWERS = "/fetch_question_answers";
    public static final String DELETE_ANSWERS = "/delete_question_answer";
    public static final String GET_QUESTION_DETAIL = "/fetch_question_detail";
    public static final String GET_TOP_HOT_KNOWLEDGE = "/fetch_hot_knowledges";
    public static final String GET_KNOWLEDGE = "/fetch_knowledges";
    public static final String GET_KNOWLEDGE_DETAIL = "/fetch_knowledge_detail";
    public static final String GET_TOP_HOT_EXPORTS = "/fetch_recommend_experts";
    public static final String GET_EXPORTS = "/fetch_experts";
    public static final String GET_EXPORT_DETAIL = "/fetch_expert";
    public static final String GET_COMMUNION_MESSAGE = "/fetch_communion_message";
    public static final String SEARCH_REQUESTION = "/search_question";

    public static final String GET_RESOURCE_BANNER = "/get_resource_banners";

    public static final String GET_RESOURCE_CATEGORY = "/get_resource_category";

    public static final String GET_HOME_PAGE_RESOURCE = "/fetch_home_page_resources";

    public static final String GET_HOT_RESOURCE = "/fetch_hot_resource";

    public static final String GET_RESOURCE = "/fetch_resource";

    public static final String GET_RECOMMENDED_RESOURCE = "/fetch_recommended_resource";

    public static final String GET_PROVIDER_BY_UPDATE_TIME = "/fetch_provider_by_update";

    public static final String GET_PLAY_HISTORY = "/fetch_play_history";

    public static final String GET_ALBUM_BY_CATEGORY = "/fetch_album_by_category";

    public static final String GET_ALBUM_BY_PROVIDER = "/fetch_album_by_provider";

    public static final String PLAY_NEXT = "/play_next";

    public static final String PLAY_RESOURCE = "/play_resource";

    public static final String GET_SEARCH_KEYWORD = "/fetch_search_keywords";

    public static final String GET_SEARCH_RESULT = "/search_resource";

    public static final String GET_RESOURCE_BY_ALBUM = "/fetch_resource_by_album";

    public static final String GET_RESOURCE_PICTURE = "/fetch_resource_pictures";

    public static final String GET_RESOURCE_BY_ID = "/fetch_resource_by_id";

    public static final String GET_PROVIDER_BY_ID = "/fetch_provider_by_id";

    public static final String GET_ALBUM_BY_ID = "/fetch_album_by_id";

    public static final String GET_NEAR_RESOURCE = "/fetch_near_resources";

    public static final String GET_LIGHT_APPS = "/fetch_app";
    public static final String DATA_EVENT = "/data/event";
}
