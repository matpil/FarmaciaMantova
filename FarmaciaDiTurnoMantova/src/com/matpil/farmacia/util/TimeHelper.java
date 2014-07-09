package com.matpil.farmacia.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeHelper {

	public static Date retrieveDate(String start) {
		Date now = new Date();
		if (start == null)
			return now;
		Calendar instance = Calendar.getInstance();
		int idx = start.indexOf(".");
		int hour = Integer.parseInt(start.substring(0, idx));
		int minute = Integer.parseInt(start.substring(idx + 1));
		instance.set(Calendar.HOUR_OF_DAY, hour);
		instance.set(Calendar.MINUTE, minute);
		instance.set(Calendar.SECOND, 0);
		System.out.println(now + " after " + instance.getTime() + ": " + (now.after(instance.getTime())));
		if (now.after(instance.getTime())) {
			return now;
		} else {
			instance.setTime(now);
			instance.set(Calendar.DAY_OF_MONTH, instance.get(Calendar.DAY_OF_MONTH) - 1);
			return instance.getTime();
		}

	}

	public static String retrieveRightDateFormatted(String pattern, String start) {
		Date date = retrieveDate(start);
		String dateFormat = null;
		dateFormat = retrieveDateFormatted(pattern, date);
		return dateFormat;
	}

	public static String retrieveDateFormatted(String pattern, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ITALIAN);
		return sdf.format(date).toUpperCase(Locale.ITALY);
	}
	
	public static String retrieveDateFormattedWithHourAndMin(String startTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ITALIAN);
		Calendar instance = Calendar.getInstance();
		int idx = startTime.indexOf(".");
		int hour = Integer.parseInt(startTime.substring(0, idx));
		int minute = Integer.parseInt(startTime.substring(idx + 1));
		instance.set(Calendar.HOUR_OF_DAY, hour);
		instance.set(Calendar.MINUTE, minute);
		instance.set(Calendar.SECOND, 0);		
		return sdf.format(instance.getTime()).toUpperCase(Locale.ITALY);
	}

	public static String retrieveRangeHour(String periodText, String start, String end) {
		if (start == null || end == null)
			return null;
		else {
			periodText = periodText.replace("start", start);
			periodText = periodText.replace("end", end);
			return periodText;
		}
	}

	public static String retrieveTomorrowRightDateFormatted(String pattern, String startHour) {
		Date retrieveDate = retrieveDate(startHour);
		Calendar cal = Calendar.getInstance();
		cal.setTime(retrieveDate);
		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
		Date time = cal.getTime();
		return retrieveDateFormatted(pattern, time);
	}

}
