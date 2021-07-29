package torgovator.utils;

import java.util.Calendar;

public class DayUtils {
	public static int getNowYear() {
		Calendar calendarNow = Calendar.getInstance();
		calendarNow.setTime(new java.util.Date());
		int year = calendarNow.get(Calendar.YEAR);
		return year;
	}

	public static int getNowMonth() {
		Calendar calendarNow = Calendar.getInstance();
		calendarNow.setTime(new java.util.Date());
		int Month = calendarNow.get(Calendar.MONTH) + 1;
		return Month;
	}

	public static int getNowDay() {
		Calendar calendarNow = Calendar.getInstance();
		calendarNow.setTime(new java.util.Date());
		int Day = calendarNow.get(Calendar.DAY_OF_MONTH);
		return Day;
	}

	public static int getNowHOUR() {
		Calendar calendarNow = Calendar.getInstance();
		calendarNow.setTime(new java.util.Date());
		int hour = calendarNow.get(Calendar.HOUR_OF_DAY);
		return hour;
	}

	public static int getNowMin() {
		Calendar calendarNow = Calendar.getInstance();
		calendarNow.setTime(new java.util.Date());
		int minute = calendarNow.get(Calendar.MINUTE);
		return minute;
	}

	public static int getNowSec() {
		Calendar calendarNow = Calendar.getInstance();
		calendarNow.setTime(new java.util.Date());
		int secodn = calendarNow.get(Calendar.SECOND);
		return secodn;
	}

	public static String getNowYear_Month_Day() {
		return Integer.toString(getNowYear() * 10000 + getNowMonth() * 100 + getNowDay());

	}

	public static String getNowYear_Month_Day_Hour_minute_second() {
		return Integer.toString(getNowYear() * 10000 + getNowMonth() * 100 + getNowDay())
				/*
				 * + Integer.toString(getNowHOUR() * 10000 + getNowMin() * 100 + getNowSec());
				 */

				+ String.format("%06d", getNowHOUR() * 10000 + getNowMin() * 100 + getNowSec());

	}
}
