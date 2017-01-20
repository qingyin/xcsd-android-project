package com.tuxing.app.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tuxing.app.activity.WebSubUrlActivity;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理文字显示的类
 * 
 * 
 */

public class TextUtils {
	public static String parseText(String text) {
		if (text == null || "".equals(text.trim())) {
			return text;
		}
		String newString = "";
		try {
			Integer i = Integer.parseInt(text);
			if (null != i) {
				if (i >= 0 && i <= 9) {
					newString = " " + i + " ";
				} else {
					newString = i + "";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return text;
		}
		return newString;
	}

	public static int Dp2Px(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	public static int Px2Dp(Context context, float px) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}

	public static String Trim(String string) {
		if (string == null) {
			return "";
		}
		return string.trim();
	}

	public static String[] transPdf(String sourcePath) {
		String[] s = new String[2];
		if (sourcePath != null && !"".equals(sourcePath.trim())) {
			String name = sourcePath.substring(sourcePath.indexOf("]", 1) + 1,
					sourcePath.indexOf("[", 2));
			String value = sourcePath.substring(sourcePath.indexOf("\"") + 1,
					sourcePath.lastIndexOf("\""));
			s[0] = name;
			s[1] = value;
		}
		return s;
	}

	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static String toHexString(byte[] b) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);
		}
		return sb.toString();
	}

	public static String md5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			return toHexString(messageDigest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return "";
	}

	public static String retrieveContent(InputStream in) {
		StringBuilder sb = new StringBuilder();
		byte[] buff = new byte[1024];
		int len = 0;
		try {
			while ((len = in.read(buff)) > 0) {
				sb.append(new String(buff, 0, len, "UTF-8"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static String[] CH_MONTHS = { "一", "二", "三", "四", "五", "六", "七",
			"八", "九", "十", "十一", "十二" };
	public static String[] CH_WEEKS = { "一", "二", "三", "四", "五", "六", "日" };

	public static String[] calcuDateForChooseStatium(String sourceDate) {
		String[] dates = new String[3];
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy/MM/dd HH:mm:ss");
			Date d = dateFormat.parse(sourceDate);
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			int month = c.get(Calendar.MONTH);
			dates[0] = CH_MONTHS[month] + "月";
			int dayForWeek = 0;
			if (c.get(Calendar.DAY_OF_WEEK) == 1) {
				dayForWeek = 7;
			} else {
				dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
			}
			dates[1] = "周" + CH_WEEKS[dayForWeek - 1];
			int hao = c.get(Calendar.DAY_OF_MONTH);
			dates[2] = hao + "日";
			return dates;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static String calcuDateForOrderPayDetail(String sourceDate) {
		StringBuffer sb = new StringBuffer();
		try {
			SimpleDateFormat dateFormat = null;
			if (sourceDate.contains("T")) {
				sourceDate = sourceDate.replace("T", " ");
				dateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
			} else {
				dateFormat = new SimpleDateFormat(
						"yyyy/MM/dd HH:mm:ss");
			}
//			SimpleDateFormat dateFormat = new SimpleDateFormat(
//					"yyyy/MM/dd HH:mm:ss");
			Date d = dateFormat.parse(sourceDate);
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			int year = c.get(Calendar.YEAR);
			sb.append(year);
			sb.append("-");
			int month = c.get(Calendar.MONTH)+1;
			if (month <10) {
				sb.append("0").append(month);
			} else {
				sb.append(month);
			}
			sb.append("-");
			
			int day = c.get(Calendar.DAY_OF_MONTH);
			if (day <10) {
				sb.append("0").append(day);
			} else {
				sb.append(day);
			}
			sb.append(" ");
			
			int dayForWeek = 0;
			if (c.get(Calendar.DAY_OF_WEEK) == 1) {
				dayForWeek = 7;
			} else {
				dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
			}
			String week = "星期" + CH_WEEKS[dayForWeek - 1];
			sb.append(week);
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 2015-02-05T00:09:31.927---2015-02-15  13:20
	 * @param sourceDate
	 * @return
	 */
	public static String parseReviewDateFormat(String sourceDate) {
		try {
			String td = sourceDate.replace("T", " ");
			td = td.substring(0, td.lastIndexOf("."));
			return td;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 2015-02-05T00:09:31.927---2015-02-15  13:20
	 * @param sourceDate
	 * @return
	 */
	public static String parseOrderListDateFormat(String sourceDate) {
		try {
			String td = sourceDate.replace("T", " ");
			return td;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	
	public static String parseOrderPrice(String price) {
		BigDecimal bd = new BigDecimal(price);
		bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		return bd.toString();
	}
	
	
	public static String parseMainStadiumListWeek(String sourceDate) {
		StringBuffer sb = new StringBuffer();
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy/MM/dd HH:mm:ss");
			Date d = dateFormat.parse(sourceDate);
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			
			int dayForWeek = 0;
			if (c.get(Calendar.DAY_OF_WEEK) == 1) {
				dayForWeek = 7;
			} else {
				dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
			}
			String week = "周" + CH_WEEKS[dayForWeek - 1];
			sb.append(week);
			sb.append("/");
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static String parseMainStadiumListMonth(String sourceDate) {
		StringBuffer sb = new StringBuffer();
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy/MM/dd HH:mm:ss");
			Date d = dateFormat.parse(sourceDate);
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			
			int month = c.get(Calendar.MONTH)+1;
			sb.append(month);
			sb.append(".");
			
			int day = c.get(Calendar.DAY_OF_MONTH);
			if (day <10) {
				sb.append("0").append(day);
			} else {
				sb.append(day);
			}
			
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static String parseYYYYMMDD(String sourceDate) {
		StringBuffer sb = new StringBuffer();
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date d = dateFormat.parse(sourceDate);
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			sb.append(c.get(Calendar.YEAR) + "年");
			
			int month = c.get(Calendar.MONTH)+1;
			sb.append(month);
			sb.append("月");
			
			int day = c.get(Calendar.DAY_OF_MONTH);
			if (day <10) {
				sb.append("0").append(day);
			} else {
				sb.append(day);
			}
			sb.append("日");
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	
	public static String parseUserCardDate(String sourceDate) {
		try {
			return sourceDate.substring(0, sourceDate.indexOf("T"));
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static String parseOrderUnPayTime(String time) {
		StringBuffer sb = new StringBuffer();
		try {
			String sourceDate = time.replace("T", " ");
			sourceDate = sourceDate.substring(0, sourceDate.lastIndexOf("."));
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date d = dateFormat.parse(sourceDate);
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			
			int month = c.get(Calendar.MONTH)+1;
			sb.append(month);
			sb.append("月");
			
			int day = c.get(Calendar.DAY_OF_MONTH);
			if (day <10) {
				sb.append("0").append(day);
			} else {
				sb.append(day);
			}
			sb.append("日");
			
			int dayForWeek = 0;
			if (c.get(Calendar.DAY_OF_WEEK) == 1) {
				dayForWeek = 7;
			} else {
				dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
			}
			String week = "星期" + CH_WEEKS[dayForWeek - 1];
			sb.append(week);
			
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static String parseMyOrderListTime(String time) {
		StringBuffer sb = new StringBuffer();
		try {
			String sourceDate = time.replace("T", " ");
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date d = dateFormat.parse(sourceDate);
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			
			int month = c.get(Calendar.MONTH)+1;
			sb.append(month);
			sb.append("月");
			
			int day = c.get(Calendar.DAY_OF_MONTH);
			if (day <10) {
				sb.append("0").append(day);
			} else {
				sb.append(day);
			}
			sb.append("日");
			sb.append(" ");
			int dayForWeek = 0;
			if (c.get(Calendar.DAY_OF_WEEK) == 1) {
				dayForWeek = 7;
			} else {
				dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
			}
			String week = "星期" + CH_WEEKS[dayForWeek - 1];
			sb.append(week);
			
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	
	public static int calculatePayReleaseTime(String createTime) {
		try {
			String sourceDate = createTime.replace("T", " ");
			sourceDate = sourceDate.substring(0, sourceDate.lastIndexOf("."));
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date d = dateFormat.parse(sourceDate);
			long start = d.getTime() + 5*60*1000;
			long current = System.currentTimeMillis();
			
			return (int)((start - current)/1000);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	
	/**
	 * 
	 * @param sourceDate  2017-02-21 00:00:00
	 * @return            2017-02-21
	 */
	public static String parseYYYYMMDD2(String sourceDate) {
		StringBuffer sb = new StringBuffer();
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date d = dateFormat.parse(sourceDate);
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			sb.append(c.get(Calendar.YEAR)).append("-");
			
			int month = c.get(Calendar.MONTH)+1;
			if (month <10) {
				sb.append("0").append(month);
			} else {
				sb.append(month);
			}
			sb.append("-");
			
			int day = c.get(Calendar.DAY_OF_MONTH);
			if (day <10) {
				sb.append("0").append(day);
			} else {
				sb.append(day);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 
	 * @param sourceDate  
	 * @return            2017/02/21
	 */
	public static String parseYYYYMMDD3() {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy/MM/dd");
			Date d = new Date(System.currentTimeMillis());
			return dateFormat.format(d);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	
	
	/**
	 * 
	 * @param sourceDate  
	 * @return            2017/02/21
	 */
	public static String parseYYYYMMDD4(String source) {
		try {
			return source.substring(0, source.lastIndexOf(":"));
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	
	/**
	 * 
	 * @param sourceDate  2017-02-21 00:00:00
	 * @return            
	 */
	public static boolean isToday(String sourceDate) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy/MM/dd");
			Date d = dateFormat.parse(sourceDate);
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			int tYear = c.get(Calendar.YEAR);
			int tMonth = c.get(Calendar.MONTH)+1;
			int tDay = c.get(Calendar.DAY_OF_MONTH);
			Date currentDate = new Date();
			c.setTime(currentDate);
			int cYear = c.get(Calendar.YEAR);
			int cMonth = c.get(Calendar.MONTH)+1;
			int cDay = c.get(Calendar.DAY_OF_MONTH); 
			return ((tYear == cYear) && (tMonth == cMonth) && (tDay == cDay));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}


	public static class MyURLSpan extends ClickableSpan {

		private String mUrl;
		private Context mContext;
		Pattern b = Pattern.compile(SysConstants.LINK_WEB_REGEX);

		public MyURLSpan(Context context, String url) {
			this.mUrl = url;
			this.mContext = context;
		}

		@Override
		public void onClick(View widget) {
			if (mUrl.matches(b.toString())) {
				Log.d("ctx", mUrl);
				WebSubUrlActivity.invoke(mContext, mUrl, "");
			} else {
				try {
					Uri uri = Uri.parse(mUrl);
					Intent it = new Intent(Intent.ACTION_VIEW, uri);
					mContext.startActivity(it);
				} catch (Exception e) {
					Log.e("url error:", e.getMessage());
				}
			}
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			super.updateDrawState(ds);
			ds.setUnderlineText(false);
		}
	}


//	public static void setTextLinkStyle(Context mContext, TextView desc, CharSequence charSequence) {
//		if (charSequence != null) {
//			Spannable spannable = new SpannableString(charSequence);
//			Linkify.addLinks(spannable, Linkify.WEB_URLS);
//			final Pattern a = Pattern.compile(SysConstants.LINK_PHONE_REGEX);
//			Linkify.addLinks(spannable, a, "tel:");
//			desc.setText(spannable);
//			desc.setLinksClickable(true);
//			desc.setMovementMethod(LinkMovementClickMethod.getInstance());
//
//			CharSequence text = desc.getText();
//			if (text instanceof Spannable) {
//				int end = text.length();
//				Spannable sp = (Spannable) desc.getText();
//				URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
//				SpannableStringBuilder style = new SpannableStringBuilder(text);
//				style.clearSpans();
//				for (URLSpan url : urls) {
//					Pattern pattern = Pattern.compile(SysConstants.LINK_WEB_REGEX);
//					Matcher m = pattern.matcher(url.getURL());
//					if (m.find()) {
//						MyURLSpan myURLSpan = new MyURLSpan(mContext, url.getURL());
//						style.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//					}
//				}
//				desc.setText(style);
//			}
//		}
//	}
	
	/**
	 * 
	 * @param 
	 * @return   ex: 5:00
	 */
	public static String parseCurrentTime() {
		try {
			Calendar c = Calendar.getInstance();
			return c.get(Calendar.HOUR_OF_DAY) + ":00";
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
