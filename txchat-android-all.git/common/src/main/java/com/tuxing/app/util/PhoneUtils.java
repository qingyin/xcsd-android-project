package com.tuxing.app.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * 手机相关信息
 * 
 * @author zhaomeng
 * 
 */
public class PhoneUtils {
	public static int mNetWorkType;
	/** wifi网络 */
	public static final int NETWORKTYPE_WIFI = 4;
	/** wap网络 */
	public static final int NETWORKTYPE_MOBILE = 1;
	/** 以太网 */
	public static final int NETWORKTYPE_LAN = 5;
	/** 没有网络 */
	public static final int NETWORKTYPE_INVALID = 0;
	/**
	 * 
	 * 获取mac地址
	 * 
	 */
	public static String getMacAddress(Context mContext) {
		WifiManager wifi = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info;
		try {
			info = wifi.getConnectionInfo();
			if (info == null || info.getMacAddress() == null) {//modified by wangst                                                
				return getDeviceId(mContext);
			}
			return info.getMacAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getDeviceId(mContext);
		
	}
	
	public static String getMacAddress0(Context mContext) {
		WifiManager wifi = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info;
		try {
			info = wifi.getConnectionInfo();
			if (info == null || info.getMacAddress() == null) {//modified by wangst
				return "";
			}
			return info.getMacAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
		
	}
	/**
	 * 
	 * 判断是否网路可用
	 * 
	 */
	public static boolean isNetworkAvailable(Context mContext) {
		boolean flag = false;
		ConnectivityManager cwjManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netWorkInfo = cwjManager.getActiveNetworkInfo();
		if (netWorkInfo != null) {
			flag = netWorkInfo.isAvailable();
		}
		return flag;
	}
	/**
	 * 
	 * 获取设备号
	 * 
	 */
	public static String mDeviceId = null;

	public static String getDeviceId(Context mContext) {
		if (mDeviceId == null) {
			String deviceId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
			if (deviceId == null) {
				deviceId = ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
			}
			deviceId += getMacAddress0(mContext);
			return deviceId;
		} else
			return mDeviceId;
	}

	/**
	 * 
	 * 获取imsi
	 * 
	 */
	public static String getImsi(Context mContext) {
		TelephonyManager mTm = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = mTm.getSubscriberId();
		if (imsi == null)
			imsi = "";
		return imsi;
	}
	/**
	 * 
	 * 获取屏幕的分辨率
	 * 
	 */
	public static String getResolution(Context mContext) {
		WindowManager mWindowManager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		int width = mWindowManager.getDefaultDisplay().getWidth();
		int height = mWindowManager.getDefaultDisplay().getHeight();
		return width + "*" + height;
	}
	
	public static void colseKeyboard(Context mContext,View view){
		// 软键盘收起
		InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static  int dip2px(Context context,float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static String getPhoneType(){
		String phontType = "  手机型号: " + android.os.Build.MODEL + ","+
				"系统版本 " + android.os.Build.VERSION.RELEASE;
		return phontType;
	}

	// 判断网络type
	public static int getNetWorkType(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) {
			String type = networkInfo.getTypeName();
			if (type.equalsIgnoreCase("WIFI")) {
				mNetWorkType = NETWORKTYPE_WIFI;
			} else if (type.equalsIgnoreCase("MOBILE")) {
				mNetWorkType = NETWORKTYPE_MOBILE;
			}else {//如果是以太网连接，这里用的排除
				mNetWorkType = NETWORKTYPE_LAN;
			}
		} else {
			mNetWorkType = NETWORKTYPE_INVALID;
		}

		return mNetWorkType;
	}
}
