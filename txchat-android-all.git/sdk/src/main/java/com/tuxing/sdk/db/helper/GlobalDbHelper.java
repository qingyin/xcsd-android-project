package com.tuxing.sdk.db.helper;

import android.content.Context;
import com.tuxing.sdk.db.dao.global.DaoMaster;
import com.tuxing.sdk.db.dao.global.DaoSession;
import com.tuxing.sdk.db.dao.global.LoginUserDao;
import com.tuxing.sdk.db.entity.LoginUser;
import com.tuxing.sdk.utils.Constants;
import de.greenrobot.dao.query.QueryBuilder;

import java.util.List;

/**
 * Created by Alan on 2015/5/17.
 */
public class GlobalDbHelper {
    private static GlobalDbHelper instance = new GlobalDbHelper();

    private DaoSession session;
    private String dbFile;

//    static{
//        QueryBuilder.LOG_SQL = true;
//        QueryBuilder.LOG_VALUES = true;
//    }

    private GlobalDbHelper() {

    }

    public void init(Context context){
        if(context == null){
            throw new IllegalStateException("Cannot get the service context");
        }

        this.dbFile = Constants.GLOBAL_DB_FILE;

        DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context, dbFile, null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());

        session = daoMaster.newSession();
    }

    public static GlobalDbHelper getInstance(){
        return instance;
    }

    public String getDbFile() {
        return dbFile;
    }

    /*------------------------LOGIN_USER--------------------------------*/
    public LoginUser getLoginUser(){
        LoginUserDao loginUserDao = session.getLoginUserDao();

        return loginUserDao.queryBuilder().where(
                LoginUserDao.Properties.Status.eq(1)).unique();
    }

    public LoginUser getLoginUserByName(String username){
        LoginUserDao loginUserDao = session.getLoginUserDao();

        return loginUserDao.queryBuilder().where(
                LoginUserDao.Properties.Username.eq(username))
                .unique();
    }

    public void changeLoginName(Long userId, String loginName){
        LoginUser loginUser = session.getLoginUserDao()
                .queryBuilder()
                .where(LoginUserDao.Properties.UserId.eq(userId))
                .unique();

        if(loginUser != null) {
            loginUser.setUsername(loginName);
            session.getLoginUserDao().insertOrReplace(loginUser);
        }
    }

    public void login(LoginUser user){
        LoginUserDao loginUserDao = session.getLoginUserDao();
        logout();

        user.setStatus(1);
        user.setLastLoginTime(System.currentTimeMillis());
        loginUserDao.insertOrReplace(user);
    }

    public long saveLoginUser(LoginUser loginUser){
        return session.getLoginUserDao().insertOrReplace(loginUser);
    }


    public void logout(){
        List<LoginUser> loginUserList = session.getLoginUserDao()
                .queryBuilder()
                .where(LoginUserDao.Properties.Status.eq(1))
                .list();

        for(LoginUser loginUser : loginUserList){
            loginUser.setStatus(0);
            session.getLoginUserDao().insertOrReplace(loginUser);
        }
    }

    public void resetPassword(Long userId, String password) {
        LoginUser loginUser = session.getLoginUserDao()
                .queryBuilder()
                .where(LoginUserDao.Properties.UserId.eq(userId))
                .unique();

        if(loginUser != null) {
            loginUser.setPassword(password);
            session.getLoginUserDao().insertOrReplace(loginUser);
        }
    }

    public void activeUser(Long userId) {
        LoginUser loginUser = session.getLoginUserDao()
                .queryBuilder()
                .where(LoginUserDao.Properties.UserId.eq(userId))
                .unique();

        if(loginUser != null) {
            loginUser.setActive(true);
            session.getLoginUserDao().insertOrReplace(loginUser);
        }
    }

    public LoginUser getLastLoginUser(){
        return session.getLoginUserDao()
                .queryBuilder()
                .orderDesc(LoginUserDao.Properties.LastLoginTime)
                .limit(1)
                .unique();
    }
}
