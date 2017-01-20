package com.tuxing.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Base64;
import com.tuxing.sdk.utils.IOUtils;

import java.io.*;

public class PreferenceUtils {
	public static String getPrefString(Context context, String key,final String defaultValue) {
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		return settings.getString(key, defaultValue);
	}
	public static void setPrefString(Context context, final String key,final String value) {
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		settings.edit().putString(key, value).commit();
	}

	public static boolean getPrefBoolean(Context context, final String key,final boolean defaultValue) {
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		return settings.getBoolean(key, defaultValue);
	}

	public static boolean hasKey(Context context, final String key) {
		return PreferenceManager.getDefaultSharedPreferences(context).contains(key);
	}

	public static void setPrefBoolean(Context context, final String key,final boolean value) {
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		settings.edit().putBoolean(key, value).commit();
	}

	public static void setPrefInt(Context context, final String key,final int value) {
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		settings.edit().putInt(key, value).commit();
	}

	public static int getPrefInt(Context context, final String key,final int defaultValue) {
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		return settings.getInt(key, defaultValue);
	}

	public static void setPrefFloat(Context context, final String key,final float value) {
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		settings.edit().putFloat(key, value).commit();
	}

	public static float getPrefFloat(Context context, final String key,final float defaultValue) {
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		return settings.getFloat(key, defaultValue);
	}

	public static void setPrefLong(Context context, final String key,final long value) {
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		settings.edit().putLong(key, value).commit();
	}

	public static long getPrefLong(Context context, final String key,final long defaultValue) {
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		return settings.getLong(key, defaultValue);
	}
	
	

	public static void setObject(Context context, String key, Object object) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream out = null;
		try {

			out = new ObjectOutputStream(baos);
			out.writeObject(object);
			String objectVal = new String(Base64.encode(baos.toByteArray(),
					Base64.DEFAULT));
			Editor editor = sp.edit();
			editor.putString(key, objectVal);
			editor.commit();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.safeClose(baos);
			IOUtils.safeClose(out);
		}
	}

	public static Object getObject(Context context, String key) {
		Object object = null;
		ObjectInputStream ois = null;
		try {
			final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
			String base64Product = settings.getString(key, "");
			// 将base64格式字符串还原成byte数组
			byte[] productBytes = Base64.decode(base64Product.getBytes(),Base64.DEFAULT);
			ByteArrayInputStream bais = new ByteArrayInputStream(productBytes);
			ois = new ObjectInputStream(bais);
			object = ois.readObject();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (OptionalDataException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			IOUtils.safeClose(ois);
		}
		return object;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getObject(Context context,String key, Class<T> clazz) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		if (sp.contains(key)) {
			String objectVal = sp.getString(key, null);
			byte[] buffer = Base64.decode(objectVal, Base64.DEFAULT);
			ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(bais);
				T t = (T) ois.readObject();
				return t;
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				IOUtils.safeClose(bais);
				IOUtils.safeClose(ois);

			}
		}
		return null;
	}

	public static void clearPreference(Context context,final SharedPreferences p) {
		final Editor editor = p.edit();
		editor.clear();
		editor.commit();
	}
}
