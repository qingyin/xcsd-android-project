package com.tuxing.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PreferenceUtil {

	public static void putDefStr(Context myContext, String key, String value) {
		Editor ed = PreferenceManager.getDefaultSharedPreferences(myContext)
				.edit();
		ed.putString(key, value);
		ed.commit();
	}

	public static String getDefStr(Context myContext, String key) {
		SharedPreferences pre = PreferenceManager
				.getDefaultSharedPreferences(myContext);
		return pre.getString(key, "");
	}

	public static void putUserStr(Context myContext, String username,
			String key, String value) {
		Editor ed = myContext.getSharedPreferences(username,
				Context.MODE_PRIVATE).edit();
		ed.putString(key, value);
		ed.commit();
	}

	public static String getUserStr(Context myContext, String username,
			String key) {
		SharedPreferences pre = myContext.getSharedPreferences(username,
				Context.MODE_PRIVATE);
		return pre.getString(key, "");
	}

	public static void putDefBoolen(Context myContext, String key, boolean value) {
		Editor ed = PreferenceManager.getDefaultSharedPreferences(myContext)
				.edit();
		ed.putBoolean(key, value);
		ed.commit();
	}

	public static boolean getDefBoolen(Context myContext, String key) {
		SharedPreferences pre = PreferenceManager
				.getDefaultSharedPreferences(myContext);
		return pre.getBoolean(key, false);
	}

	public static boolean getDefBoolen(Context myContext, String key,
			boolean def_value) {
		SharedPreferences pre = PreferenceManager
				.getDefaultSharedPreferences(myContext);
		return pre.getBoolean(key, def_value);
	}

	public static void putUserBoolen(Context myContext, String username,
			String key, boolean value) {
		Editor ed = myContext.getSharedPreferences(username,
				Context.MODE_PRIVATE).edit();
		ed.putBoolean(key, value);
		ed.commit();
	}

	public static boolean getUserBoolen(Context myContext, String username,
			String key) {
		SharedPreferences pre = myContext.getSharedPreferences(username,
				Context.MODE_PRIVATE);
		return pre.getBoolean(key, false);
	}

	public static void putDefInt(Context myContext, String key, int value) {
		Editor ed = PreferenceManager.getDefaultSharedPreferences(myContext)
				.edit();
		ed.putInt(key, value);
		ed.commit();
	}

	public static int getDefInt(Context myContext, String key) {
		SharedPreferences pre = PreferenceManager
				.getDefaultSharedPreferences(myContext);
		return pre.getInt(key, 0);
	}

	public static void putDefLong(Context myContext, String key, long value) {
		Editor ed = PreferenceManager.getDefaultSharedPreferences(myContext)
				.edit();
		ed.putLong(key, value);
		ed.commit();
	}

	public static long getDefLong(Context myContext, String key) {
		SharedPreferences pre = PreferenceManager
				.getDefaultSharedPreferences(myContext);
		return pre.getLong(key, 0L);
	}

	public static void putUserInt(Context myContext, String username,
			String key, int value) {
		Editor ed = myContext.getSharedPreferences(username,
				Context.MODE_PRIVATE).edit();
		ed.putInt(key, value);
		ed.commit();
	}

	public static int getUserInt(Context myContext, String username, String key) {
		SharedPreferences pre = myContext.getSharedPreferences(username,
				Context.MODE_PRIVATE);
		return pre.getInt(key, 0);
	}

	public static void putUserLong(Context myContext, String username,
			String key, long value) {
		Editor ed = myContext.getSharedPreferences(username,
				Context.MODE_PRIVATE).edit();
		ed.putLong(key, value);
		ed.commit();
	}

	public static long getUserLong(Context myContext, String username,
			String key) {
		SharedPreferences pre = myContext.getSharedPreferences(username,
				Context.MODE_PRIVATE);
		return pre.getLong(key, 0L);
	}

	public static void removeUserKey(Context myContext, String username,
			String key) {
		Editor ed = myContext.getSharedPreferences(username,
				Context.MODE_PRIVATE).edit();
		ed.remove(key);
		ed.commit();
	}

}
