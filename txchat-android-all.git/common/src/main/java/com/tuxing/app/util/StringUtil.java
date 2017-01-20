package com.tuxing.app.util;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtil {

	public static String ensureNotNull(String strTemp) {
		if (strTemp == null) {
			return "";

		} else {
			return strTemp;
		}
	}

	public static boolean isNotNullAndNotEqualsNull(String str) {
		if (isNullOrEmpty(str)) {
			return false;
		} else {
			if (str.equalsIgnoreCase("null")) {
				return false;
			} else {
				return true;
			}
		}
	}

	public static boolean isNullOrEmpty(String str) {
		return (str == null || str.trim().equals("")) ? true : false;
	}

	public static int getInt(String strTemp) {
		strTemp = ensureNotNull(strTemp);

		if (strTemp.equals("")) {
			return 0;
		}

		try {
			return (int) Math.floor(Double.parseDouble(strTemp));

		} catch (Exception e) {
			return 0;
		}
	}

	public static String toGb2312(String str) {
		if (str == null) {
			return null;
		}

		String retStr = str;
		byte b[];

		try {
			b = str.getBytes("ISO8859_1");

			for (int i = 0; i < b.length; i++) {
				byte b1 = b[i];

				if (b1 == 63) {
					break; // 1

				} else if (b1 > 0) {
					continue; // 2

				} else if (b1 < 0) { // 涓嶅彲鑳戒负0锛�涓哄瓧绗︿覆缁撴潫绗� retStr = new
										// String(b, "GB2312");
					break;
				}
			}

		} catch (UnsupportedEncodingException e) {
			// e.printStackTrace(); //To change body of catch statement use File
			// | Settings | File Templates.
		}

		return retStr;
	}

	public static boolean hasFileSpecChar(String name) {
		String regEx = "[\\\\/:*?<>\"|]";
		Pattern p = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(name);
		return m.find();
	}

}
