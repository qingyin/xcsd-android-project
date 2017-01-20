package com.tuxing.app.util;

import android.content.Context;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * ViewUtils
 *
 * @author liangyq
 */
public class ViewUtils {

    private static long mLastClickTime;

    /**
     * @param context
     * @param text
     * @param isLong
     */
    public static void makeTextToast(Context context, String text,
                                     boolean isLong) {
        if (isLong == true) {
            Toast toast = new Toast(context);
            toast.makeText(context, text, Toast.LENGTH_LONG).show();
        } else {
            Toast toast = new Toast(context);
            toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * @param context
     * @param resId
     * @param isLong
     */
    public static void makeTextToast(Context context, int resId,
                                     boolean isLong) {
        if (isLong == true) {
            Toast toast = new Toast(context);
            toast.makeText(context, resId, Toast.LENGTH_LONG).show();
        } else {
            Toast toast = new Toast(context);
            toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * MakeCenterVerticalToast
     *
     * @param context
     * @param text
     * @param isLong
     */
    public static void mMakeCenterVerticalToast(Context context, String text,
                                                boolean isLong) {
        if (isLong == true) {
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        } else {
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        }
    }

    /**
     * showSoftInput
     *
     * @param editText
     */
    public static void showSoftInput(final EditText editText) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) editText
                        .getContext().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }
        }, 600);
    }

    /**
     * 避免点击事件多次点击
     * */
    public static boolean isFirstClick() {
        long currentTime = System.currentTimeMillis();
        long time = currentTime - mLastClickTime;
        if (time > 0 && time < 500) {
            return false;
        }
        mLastClickTime = currentTime;
        return true;
    }
}
