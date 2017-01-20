package com.xcsd.app.parent.util;

/**
 * Created by shan on 2016/4/15.
 */
public class SysUtil {
    //作业列表请求传此参数
    private static long userId;

    public static long getUserId() {
        return userId;
    }

    public static void setUserId(long userId) {
        SysUtil.userId = userId;
    }
}
