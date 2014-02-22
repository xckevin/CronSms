package com.xckevin.cronsms.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	private static final SimpleDateFormat smsFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	public static String formatSmsTime(Calendar cal) {
		return smsFormat.format(cal.getTime());
	}
	
	public static String formatSmsTime(Date date) {
		return smsFormat.format(date);
	}
	
	public static Calendar parseSmsTimeForCalendar(String time) {
		Calendar cal = Calendar.getInstance();
		try {
			Date date = smsFormat.parse(time);
			cal.setTime(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return cal;
	}
	
	public static Date parseSmsTimeForDate(String time) {
		try {
			return smsFormat.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return new Date();
	}
	
	public static long parseSmsTimeForLong(String time) {
		try {
			return smsFormat.parse(time).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
}
