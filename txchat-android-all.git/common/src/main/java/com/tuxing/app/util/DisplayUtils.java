package com.tuxing.app.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by 77295 on 2016/4/21.
 */
public class DisplayUtils {

    /**
     * 异常信息
     */
    private static final String ERROR_MSG = "You should init DisplayUtils";

    /**
     * 屏幕宽度，像素
     */
    public static int SCREAN_WIDTH_PX;

    /**
     * 屏幕高度，像素
     */
    public static int SCREAN_HEIGHT_PX;

    /**
     * 屏幕密度
     */
    public static float SCREAN_DENSITY;
    private static boolean inited = false;

    public static void init(Context context) {
        if (inited) {
            return;
        }
        inited = true;
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context
                .WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        SCREAN_WIDTH_PX = dm.widthPixels;
        SCREAN_HEIGHT_PX = dm.heightPixels;
        SCREAN_DENSITY = dm.density;
    }

    /**
     * @param dp 转换dp值
     * @return 转换后的px值
     */
    public static int dp2px(float dp) {
        if (inited)
            return (int) (dp * SCREAN_DENSITY + 0.5f);
        else
            throw new RuntimeException(ERROR_MSG);
    }

    /**
     * @param px 转换sp值
     * @return 转换后的dp值
     */
    public static int px2dp(int px) {
        if (inited)
            return (int) (px / SCREAN_DENSITY + 0.5f);
        else
            throw new RuntimeException(ERROR_MSG);
    }

}
