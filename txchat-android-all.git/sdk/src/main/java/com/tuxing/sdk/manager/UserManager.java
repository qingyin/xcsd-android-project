package com.tuxing.sdk.manager;

import com.tuxing.sdk.db.entity.User;
import com.tuxing.sdk.modle.NotificationSetting;
import com.tuxing.sdk.task.AsyncMethod;

/**
 * Created by Alan on 2015/6/25.
 */
public interface UserManager extends BaseManager {
    /***
     * 获取当前用户的小孩账号的信息
     * 请求成功，触发事件BindChildEvent.EventType.GET_CHILD_SUCCESS
     * 请求失败，触发事件BindChildEvent.EventType.GET_CHILD_FAILED
     */
    void getChild();

    void requestUserInfoFromServer(long userId);

    @AsyncMethod
    void getUserInfoFromLocal(long userId);

    void getBindCard();

    /***
     * 根据用户ID从本地缓存中取出用户信息，同步方法
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUserInfo(long userId);

    /***
     * 更新手机号码
     * 更新成功，触发事件UserEvent.EventType.UPDATE_MOBILE_SUCCESS
     * 更新失败，触发事件UserEvent.EventType.UPDATE_MOBILE_FAILED
     * 验证码无效，触发事件UserEvent.EventType.UPDATE_MOBILE_VERIFY_CODE_INCORRECT
     * @param phoneNum   新的手机号码
     * @param verifyCode 验证码
     */
    void updatePhoneNum(String phoneNum, String verifyCode);

    /***
     * 更新用户信息
     * 更新成功，触发事件UserEvent.EventType.UPDATE_USER_SUCCESS
     * 更新失败，触发事件UserEvent.EventType.UPDATE_USER_FAILED
     * @param user 新的用户信息
     */
    void updateUserInfo(User user);

    /***
     * 挂失卡
     * 挂失成功，触发事件CheckInCardEvent.EventType.CARD_UNBIND_SUCCESS
     * 挂失失败，触发事件CheckInCardEvent.EventType.CARD_UNBIND_FAILED
     *
     * @param codeNum 卡号
     */
    void unbindCheckInCard(String codeNum);

    /***
     * 修改消息通知的设置
     * 修改成功，触发事件NotificationSettingEvent.EventType.CHANGE_SETTING_SUCCESS
     * 修改失败，触发事件NotificationSettingEvent.EventType.CHANGE_SETTING_FAILED
     * @param setting 消息通知设置，没有发生改变的项可以设置为null
     */
    void changeNotificationSetting(NotificationSetting setting);


    /***
     * 从本地数据库中取出这些设置
     * 获取成功，返回触发事件NotificationSettingEvent.EventType.GET_SETTING_SUCCESS
     * 获取失败，返回触发事件NotificationSettingEvent.EventType.GET_SETTING_FAILED
     */
    @AsyncMethod
    void getNotificationSetting();

    /***
     * 在后台更新用户设置项，结果存入数据库
     */
    void updateUserProfile();

    /***
     * 获取用户的设置，没有该设置项，返回默认值
     * @param field 设置项名
     * @param defaultVal 默认值
     * @return 设置项的值
     */
    String getUserProfile(String field, String defaultVal);

    /***
     * 更新密码
     * 更新成功，触发事件UserEvent.EventType.CHANGE_PASSWORD_SUCCESS
     * 更新失败，触发事件UserEvent.EventType.CHANGE_PASSWORD_FAILED
     * @param oldPassword 老密码
     * @param newPassword 新密码
     */
    void changePassword(String oldPassword, String newPassword);

    /***
     * 删除用户缓存
     */
    void clearUserCache();

    /***
     * 用户每日自动签到
     */
    void checkIn();

}
