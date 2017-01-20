package com.tuxing.sdk.facade;

import android.content.Context;
import com.tuxing.sdk.db.helper.GlobalDbHelper;
import com.tuxing.sdk.db.helper.UserDbHelper;
import com.tuxing.sdk.event.LoginEvent;
import com.tuxing.sdk.http.HttpClient;
import com.tuxing.sdk.manager.*;
import com.tuxing.sdk.manager.SecurityManager;
import com.tuxing.sdk.manager.impl.*;
import com.tuxing.sdk.utils.Constants;
import com.tuxing.sdk.utils.LogUtils;
import de.greenrobot.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alan on 2015/5/16.
 */
public class CoreService {
    private final static Logger logger = LoggerFactory.getLogger(CoreService.class);
    private static CoreService instance = new CoreService();

    private Context context;
    
    private HttpClient httpClient = HttpClient.getInstance();
    private LoginManager loginManager = LoginManagerImpl.getInstance();
    private ContactManager contactManager = ContactManagerImpl.getInstance();
    private NoticeManager noticeManager = NoticeManagerImpl.getInstance();
    private CheckInManager checkInManager = CheckInManagerImpl.getInstance();
    private DepartmentManager departmentManager = DepartmentManagerImpl.getInstance();
    private FileManager fileManager = FileManagerImpl.getInstance();
    private UserManager userManager = UserManagerImpl.getInstance();
    private SecurityManager securityManager = SecurityManagerImpl.getInstance();
    private RelativeManager relativeManager = RelativeManagerImpl.getInstance();
    private FeedManager feedManager = FeedManagerImpl.getInstance();
    private ContentManager contentManager = ContentManagerImpl.getInstance();
    private CounterManager counterManager = CounterManagerImpl.getInstance();
    private UpgradeManager upgradeManager = UpgradeManagerImpl.getInstance();
    private MessageManager messageManager = MessageManagerImpl.getInstance();
    private AttendanceManager attendanceManager = AttendanceManagerImpl.getInstance();
    private HomeWorkManager homeWorkManager = HomeWorkManagerImpl.getInstance();
    private LearningAbilityManager learningAbilityManager = LearningAbilityManagerImpl.getInstance();

    private QuoraManager quoraManager = QuoraManagerImpl.getInstance();
    private CommentManager commentManager = CommentManagerImpl.getInstance();
    private ResourceManager resourceManager = ResourceManagerImpl.getInstance();
    private LightAppManager lightAppManager = LightAppManagerImpl.getInstance();
    private CourseManager courseManager = CourseManagerImpl.getInstance();
    private DataReportManager dataReportManager = DataReportManagerImpl.getInstance();


    private void CoreService(){

    }

    public static CoreService getInstance(){
        return instance;
    }

    public void start(Context context, String version, String host, int port) {
        LogUtils.configure();
        logger.debug("CoreService creating...");

        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().registerSticky(this, Constants.EVENT_PRIORITY_SERVICE);
        }
        this.context = context;

        GlobalDbHelper.getInstance().init(this.context);

        httpClient.init(this.context, version);
        httpClient.setHostAddress(host, port);

        initComponents(version);
    }

    public void onEvent(LoginEvent event){
        switch (event.getEvent()){
            case LOGIN_SUCCESS:
                UserDbHelper.getInstance().init(this.context, event.getUser().getUserId());
        }
    }
    
    
    public void initComponents(String version) {
        logger.debug("CoreService starting...");

        loginManager.init(this.context);
        contactManager.init(this.context);
        noticeManager.init(this.context);
        checkInManager.init(this.context);
        departmentManager.init(this.context);
        fileManager.init(this.context);
        relativeManager.init(this.context);
        feedManager.init(this.context);
        contentManager.init(this.context);
        counterManager.init(this.context);
        attendanceManager.init(this.context);
        homeWorkManager.init(this.context);
        courseManager.init(this.context);
        dataReportManager.init(this.context);
    }

    public void stop(){
        logger.debug("CoreService destroy...");

        httpClient.onDestroy();
        loginManager.destroy();
        contactManager.destroy();
        noticeManager.destroy();
        checkInManager.destroy();
        departmentManager.destroy();
        fileManager.destroy();
        relativeManager.destroy();
        feedManager.destroy();
        contentManager.destroy();
        counterManager.destroy();
        attendanceManager.destroy();
        homeWorkManager.destroy();
        courseManager.destroy();
        dataReportManager.destroy();
    }

    public LoginManager getLoginManager(){
        return loginManager;
    }

    public ContactManager getContactManager() {
        return contactManager;
    }

    public NoticeManager getNoticeManager() {
        return noticeManager;
    }

    public CheckInManager getCheckInManager() {
        return checkInManager;
    }

    public DepartmentManager getDepartmentManager() {
        return departmentManager;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public SecurityManager getSecurityManager() {
        return securityManager;
    }

    public RelativeManager getRelativeManager(){
        return relativeManager;
    }

    public FeedManager getFeedManager() {
        return feedManager;
    }

    public ContentManager getContentManager() {
        return contentManager;
    }

    public CounterManager getCounterManager(){
        return counterManager;
    }

    public UpgradeManager getUpgradeManager(){
        return upgradeManager;
    }

    public MessageManager getMessageManager(){
        return messageManager;
    }

    public AttendanceManager getAttendanceManager() { return  attendanceManager; }

    public String getUserToken(){
        return httpClient.getToken();
    }

    public HomeWorkManager getHomeWorkManager() {
        return homeWorkManager;
    }
    public LearningAbilityManager getLearningAbilityManager() {
        return learningAbilityManager;
    }

    public QuoraManager getQuoraManager() {
        return quoraManager;
    }

    public CommentManager getCommentManager() {
        return commentManager;
    }
    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public LightAppManager getLightAppManager() {
        return lightAppManager;
    }

    public CourseManager getCourseManager() {
        return courseManager;
    }

    public DataReportManager getDataReportManager(){
        return dataReportManager;
    }
}
