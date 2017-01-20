package com.tuxing.sdk.manager;

import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.task.AsyncMethod;

/**
 * Created by Alan on 2015/6/25.
 */
public interface LoginManager extends BaseManager {

    /***
     * login完成后触发LoginEvent：
     *
     * 登录成功，EventType为LOGIN_SUCCESS，Event的user为服务器返回的信息
     * 用户不存在，EventType为LOGIN_NO_SUCH_USER
     * 密码不正确，EventType为LOGIN_AUTH_FAILED
     * 其他错误，EventType为LOGIN_FAILED_UNKNOWN_REASON
     *
     * @param username 用户名
     * @param password 密码
     */
    void login(String username, String password);

    /***
     * logout完成后会触发LoginEvent.EventType.LOGOUT事件
     * logout只会通知服务器，不关心服务器的返回，所以没有失败事件。
     */
    void logout();


    /***
     * 获取登录用户
     * @return 返回当前已经登录的用户
     */
    User getCurrentUser();

    /***
     * 激活用户，激活后，用户自动登录
     * 激活成功，触发UserEvent.EventType.ACTIVE_USER_SUCCESS
     * 激活失败，触发UserEvent.EventType.ACTIVE_USER_SUCCESS
     * 验证码无效，触发事件UserEvent.EventType.ACTIVE_USER_VERIFY_CODE_INCORRECT
     * @param phoneNum   手机号
     * @param verifyCode 验证码
     * @param password   密码
     */
    void activeUser(String phoneNum, String verifyCode, String password);

    /***
     * 获取用户密码
     * @param username 用户名
     * @return 密码
     */
    String getPassword(String username);
}
