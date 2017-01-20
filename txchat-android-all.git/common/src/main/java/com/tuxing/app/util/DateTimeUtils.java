package com.tuxing.app.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateTimeUtils {
	private static final long ONE_MINUTE = 60000L;
	private static final long ONE_HOUR = 3600000L;
	private static final long ONE_DAY = 86400000L;
	private static final long ONE_WEEK = 604800000L;

	private static final String ONE_SECOND_AGO = "秒前";
	private static final String ONE_MINUTE_AGO = "分钟前";
	private static final String ONE_HOUR_AGO = "小时前";
	private static final String ONE_DAY_AGO = "天前";
	private static final String ONE_MONTH_AGO = "月前";
	private static final String ONE_YEAR_AGO = "年前";
	private static long inTime = 0;
	
	public static String getDateString(String value){
		Date date = new Date(value);

		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
		DateFormat cstFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		TimeZone cstTime = TimeZone.getTimeZone("CST");
		cstFormat.setTimeZone(cstTime);
		String value2 = cstFormat.format(date);
		return value2;

	}
	public static String getDateString2(String value){
		Date date = new Date(value);
		
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
		DateFormat cstFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		TimeZone cstTime = TimeZone.getTimeZone("CST");
		cstFormat.setTimeZone(cstTime);
		String value2 = cstFormat.format(date);
		return value2;
		
	}
	
	public static Date getDate(Long value) {
		if(value==null||value==0)
			return new Date();
		Date date = new Date(value);

		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		date = calendar.getTime(); // 这个时间就是日期往后推一天的结果

		DateFormat cstFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		TimeZone cstTime = TimeZone.getTimeZone("CST");
		cstFormat.setTimeZone(cstTime);
		String value2 = cstFormat.format(date);

		DateFormat cstFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return cstFormat2.parse(value2);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return date;

	}

	public static long getDateTime(Long value) {
		if(value==null||value==0)
			return 0;
		Date date = new Date(value);
		inTime = value;
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		date = calendar.getTime(); // 这个时间就是日期往后推一天的结果

		DateFormat cstFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		TimeZone cstTime = TimeZone.getTimeZone("CST");
		cstFormat.setTimeZone(cstTime);
		String value2 = cstFormat.format(date);

		DateFormat cstFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return cstFormat2.parse(value2).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;

	}

	public static String formatRelativeDate(long dateL) {
		Calendar calendar = Calendar.getInstance();//获取当前日历对象
		long unixTime = calendar.getTimeInMillis();//获取当前时区下日期时间对应的时间戳
		unixTime=unixTime-TimeZone.getDefault().getRawOffset();//获取标准格林尼治时间下日期时间对应的时间戳
		long delta = unixTime - dateL;
		if (delta < 1L * ONE_MINUTE) {
			long seconds = toSeconds(delta);
			return (seconds <= 0 ? 1 : seconds) + ONE_SECOND_AGO;
		}
		if (delta < 45L * ONE_MINUTE) {
			long minutes = toMinutes(delta);
			return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
		}
		if (delta < 24L * ONE_HOUR) {
			long hours = toHours(delta);
			return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
		}
		if (delta < 48L * ONE_HOUR) {
			return "昨天";
		}
//		if (delta < 30L * ONE_DAY) {
//			long days = toDays(delta);
//			return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
//		}
//		if (delta < 12L * 4L * ONE_WEEK) {
//			long months = toMonths(delta);
//			return (months <= 0 ? 1 : months) + ONE_MONTH_AGO;
//		} 
		else {
//			 long years = toYears(delta);
//			 return (years <= 0 ? 1 : years) + ONE_YEAR_AGO;
			return Date2Millisecond(dateL);
		}
	}

	public static String formatRelativeDate(Date date) {
		long delta = new Date().getTime() - date.getTime();
		if (delta < 1L * ONE_MINUTE) {
			long seconds = toSeconds(delta);
			return (seconds <= 0 ? 1 : seconds) + ONE_SECOND_AGO;
		}
		if (delta < 45L * ONE_MINUTE) {
			long minutes = toMinutes(delta);
			return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
		}
		if (delta < 24L * ONE_HOUR) {
			long hours = toHours(delta);
			return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
		}
		if (delta < 48L * ONE_HOUR) {
			return "昨天";
		}
		// if (delta < 30L * ONE_DAY) {
		// long days = toDays(delta);
		// return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
		// }
		// if (delta < 12L * 4L * ONE_WEEK) {
		// long months = toMonths(delta);
		// return (months <= 0 ? 1 : months) + ONE_MONTH_AGO;
		// }
		else {
			// long years = toYears(delta);
			// return (years <= 0 ? 1 : years) + ONE_YEAR_AGO;
			return Date2Millisecond(date.getTime());
		}
	}

	private static long toSeconds(long date) {
		return date / 1000L;
	}

	private static long toMinutes(long date) {
		return toSeconds(date) / 60L;
	}

	private static long toHours(long date) {
		return toMinutes(date) / 60L;
	}

	private static long toDays(long date) {
		return toHours(date) / 24L;
	}

	private static long toMonths(long date) {
		return toDays(date) / 30L;
	}

	private static long toYears(long date) {
		return toMonths(date) / 365L;
	}

	private static String Date2Millisecond(long longs) {

		Date date = new Date(inTime);

		TimeZone cstTime = TimeZone.getTimeZone("Asia/Shanghai");
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeZone(cstTime);
		calendar.setTime(date);
		date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
		DateFormat cstFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		cstFormat.setTimeZone(cstTime);
		
		return cstFormat.format(date);

	}

	public static String Date2YYYYMMDD_C(long longs) {
		return TimeLong2Date("yyyy年MM月dd日",longs);
	}

	public static String TimeLong2Date(String fomat,long longs) {

		Date date = new Date(longs);

		TimeZone cstTime = TimeZone.getTimeZone("Asia/Shanghai");
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeZone(cstTime);
		calendar.setTime(date);
		date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
		DateFormat cstFormat = new SimpleDateFormat(fomat);//yyyy-MM-dd HH:mm
		cstFormat.setTimeZone(cstTime);

		return cstFormat.format(date);

	}

	public static  String convertMonth(String str){
		String month = "";
		if(str.equals("01月")){
			month = "一月";
		}else if(str.equals("02月")){
			month = "二月";
		}else if(str.equals("03月")){
			month = "三月";
		}else if(str.equals("04月")){
			month = "四月";
		}else if(str.equals("05月")){
			month = "五月";
		}else if(str.equals("06月")){
			month = "六月";
		}else if(str.equals("07月")){
			month = "七月";
		}else if(str.equals("08月")){
			month = "八月";
		}else if(str.equals("09月")){
			month = "九月";
		}else if(str.equals("10月")){
			month = "十月";
		}else if(str.equals("11月")){
			month = "十一月";
		}else if(str.equals("12月")){
			month = "十二月";
		}
		return month;
	}

    public static String getDateString(Date date, String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

}
