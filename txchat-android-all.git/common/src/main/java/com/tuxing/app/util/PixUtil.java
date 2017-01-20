package com.tuxing.app.util;

import android.content.Context;
import android.util.DisplayMetrics;

public class PixUtil {
	
	public static int convertDpToPixel(float dp, Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }
	
	public static float convertPixelToDp(Context context, float val) {
        float density = context.getResources().getDisplayMetrics().density;
        return val * density;
    }
	
}
