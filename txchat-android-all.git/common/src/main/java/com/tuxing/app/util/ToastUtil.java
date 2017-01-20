package com.tuxing.app.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtil {

	public static void show(Context context, int sting_id) {
		show(context, context.getResources().getString(sting_id));
	}

	public static void show(Context context, String str) {
		Toast toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
		//toast.setGravity(Gravity.BOTTOM, 0, 300);
		toast.show();
	}

}
