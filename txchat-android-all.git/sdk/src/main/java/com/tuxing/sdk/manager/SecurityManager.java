package com.tuxing.sdk.manager;

/**
 * Created by Alan on 2015/6/25.
 */
public interface SecurityManager extends BaseManager {

    /***
     * 发送验证码
     * @param phoneNum 手机号
     * @param type 验证码使用类型
     * @param isVoice 是否发送语音验证码
     */
    void sendVerifyCode(String phoneNum, int type, boolean isVoice);

    /***
     * 重置密码，重置密码需要先发送手机验证码，
     * 再输入收到的短信验证码
     * 验证码不正确，触发事件ResetPasswordEvent.EventType.VERIFY_CODE_INCORRECT
     * 重置成功后，触发事件ResetPasswordEvent.EventType.RESET_PASSWORD_SUCCESS
     * 重置失败后，触发事件ResetPasswordEvent.EventType.RESET_PASSWORD_FAILED
     *
     * @param phoneNum   手机号
     * @param verifyCode 验证码
     * @param password   新密码
     */
    void setPassword(String phoneNum, String verifyCode, String password);

    /***
     * 上传设备的device token
     * 上传成功，触发事件DeviceTokenEvent.EventType.UPDATE_DEVICE_TOKEN_SUCCESS
     * 上传成功，触发事件DeviceTokenEvent.EventType.UPDATE_DEVICE_TOKEN_FAILED
     * @param deviceToken 设备的device token
     * @param deviceId 设备ID
     * @param osVersion 操作系统版本
     * @param model 手机型号
     */
    void updateDeviceToken(String deviceToken, String deviceId, String osVersion, String model);
}
