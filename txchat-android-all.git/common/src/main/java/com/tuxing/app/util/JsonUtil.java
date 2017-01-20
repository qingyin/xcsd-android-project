package com.tuxing.app.util;

import com.tuxing.app.helper.NetManager;

import org.json.JSONException;
import org.json.JSONObject;


public class JsonUtil {

	public static boolean isContentSuccess(String content) {
		boolean result = false;
		if (!StringUtil.isNullOrEmpty(content)) {
			try {
				JSONObject obj = new JSONObject(content);
				int err = obj.getInt("errorCode");
				if (err == 0) {
					result = true;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public static boolean isTokenExpired(String content) {
		boolean result = false;
		if (!StringUtil.isNullOrEmpty(content)) {
			try {
				JSONObject obj = new JSONObject(content);
				int err = obj.getInt("errorCode");
				if (err == NetManager.TOKEN_EXPIRED_CODE) {
					result = true;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

}
