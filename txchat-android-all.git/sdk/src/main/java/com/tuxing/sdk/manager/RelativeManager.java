package com.tuxing.sdk.manager;

/**
 * Created by Alan on 2015/6/28.
 */
public interface RelativeManager extends BaseManager{
    /***
     * 绑定小孩
     * 绑定成功，触发事件BindChildEvent.EventType.BIND_CHILD_SUCCESS
     * 绑定失败，触发事件BindChildEvent.EventType.BIND_CHILD_FAILED
     * 亲属被其他用户绑定，触发事件BindChildEvent.EventType.CHILD_BIND_BY_OTHERS
     * @param childUserId   小孩用户的ID
     * @param relativeType  家长类型
     * @param birthday      小孩生日
     * @param guarder       监护人
     */
    void bindChild(long childUserId, int relativeType, long birthday, String guarder);

    /***
     * 获取当前用户的小孩的亲属列表
     * 请求成功，触发事件RelativeEvent.EventType.GET_RELATIVE_SUCCESS，其中包括有亲属的列表
     * 请求失败，触发事件RelativeEvent.EventType.GET_RELATIVE_FAILED
     */
    void getRelativeList();

    /***
     * 修改亲属关系
       请求成功，触发事件RelativeEvent.EventType.UPDATE_RELATIVE_SUCCESS
     * 请求失败，触发事件RelativeEvent.EventType.UPDATE_RELATIVE_FAILED
     * @param userId 要修改绑定关系的用户ID
     * @param relativeType 要修改绑定关系的亲属类型
     */
    void changeRelative(long userId, int relativeType);

    /***
     * 取消亲属关系，本方法只能由小孩的主账号才有权限调用
     * 请求成功，触发事件RelativeEvent.EventType.REMOVE_RELATIVE_SUCCESS
     * 请求失败，触发事件RelativeEvent.EventType.REMOVE_RELATIVE_FAILED
     * @param userId，要取消的用户Id
     */
    void removeRelative(long userId);

    /***
     * 为自己的小孩添加亲属，本方法只能由小孩的主账号才有权限调用
     * 添加成功后，会为小孩的亲属生成一个新的账号
     * 请求成功，触发事件RelativeEvent.EventType.ADD_RELATIVE_SUCCESS
     * 请求失败，触发事件RelativeEvent.EventType.ADD_RELATIVE_FAILED
     * 验证码无效，触发事件RelativeEvent.EventType.ADD_RELATIVE_VERIFY_CODE_INCORRECT
     * 亲属关系被其他用户绑定，触发事件RelativeEvent.EventType.ADD_RELATIVE_BIND_BY_OTHER
     * @param phoneNum   新的账号要绑定的手机号
     * @param verifyCode 验证码
     * @param relativeType 亲属类型
     * @param password 新的账号的密码
     */
    void addRelative(String phoneNum, String verifyCode, int relativeType, String password);
}
