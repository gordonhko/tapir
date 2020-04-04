package com.fusui.tapir.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FoundryDateTimeFormatter {

	private static Logger logger = LoggerFactory.getLogger(FoundryDateTimeFormatter.class);

	// 01/01/2013 16:00
	private final static SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
	private final static SimpleDateFormat dateTimeFormatter2 = new SimpleDateFormat("MM/dd/yyyy HH:mm");

	// 01/01/2013
	private final static SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy 00:00");
	private final static SimpleDateFormat dateFormatter2 = new SimpleDateFormat("MM/dd/yyyy");

	// 16:00
	private final static SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
	private final static SimpleDateFormat timeFormatter2 = new SimpleDateFormat("HH:mm");

	// Monday, Tuesday ...
	private final static SimpleDateFormat weekdayFormatter = new SimpleDateFormat("EEEE");

	// **********************************************************************
	// The following functions convert date String or date object
	// into a String in long format (mill seconds starting from 01/01/1970
	// **********************************************************************

	// input is a string in long format
	private static String getFixLenghLongDateString(long lstr) {
		String dateStr = Long.toString(lstr);
		int prefixLen = 20 - dateStr.length();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < prefixLen; i++) {
			sb.append('0');
		}
		sb.append(dateStr);
		return sb.toString();
	}

	public static String getCurrentMilliseconds() {
		// max long = 9,223,372,036,854,775,807
		long ld = new Date().getTime();
		return getFixLenghLongDateString(ld);
	}

	public static String getMillisecondsByDateTime(Date date) {
		// max long = 9,223,372,036,854,775,807
		long ld = date.getTime();
		return getFixLenghLongDateString(ld);
	}

	public static String getMillisecondsByDateTimeString(String dateStr) throws ParseException {
		Date date = dateTimeFormatter.parse(dateStr);
		long ld = date.getTime();
		return getFixLenghLongDateString(ld);
	}

	public static String getMillisecondsByDate(Date date) throws ParseException {
		Calendar tCalendar = Calendar.getInstance();
		tCalendar.setTime(date);
		tCalendar.set(Calendar.HOUR, 0);
		tCalendar.set(Calendar.MINUTE, 0);
		tCalendar.set(Calendar.SECOND, 0);
		long ld = tCalendar.getTimeInMillis();
		return getFixLenghLongDateString(ld);
	}

	// the input can be date-time but output will have date info only, time is wiped out
	public static String getMillisecondsByDateString(String dateStr) throws ParseException {
		Date date = dateFormatter.parse(dateStr);
		long ld = date.getTime();
		return getFixLenghLongDateString(ld);
	}

	public static String getMillisecondsByTime(Date date) throws ParseException {
		Calendar tCalendar = Calendar.getInstance();
		tCalendar.setTime(date);
		tCalendar.set(1970, 0, 1); // month is 1 based
		long ld = tCalendar.getTimeInMillis();
		return getFixLenghLongDateString(ld);
	}

	// the input can be date-time but output will have time info only, date is wiped out
	public static String getMillisecondsByTimeString(String dateStr) throws ParseException {
		Date date = timeFormatter.parse(dateStr);
		long ld = date.getTime();
		return getFixLenghLongDateString(ld);
	}

	public static String getCurrentWeekday() throws ParseException {
		return weekdayFormatter.format(new Date());
	}

	// **********************************************************************
	// The following functions convert a String in long format (mill seconds starting from 01/01/1970)
	// into date String or date object
	// **********************************************************************

	public static Date getDateByMilliseconds(String ldStr) {
		if (ldStr != null) {
			try {
				long ld = Long.parseLong(ldStr);
				Date date = new Date(ld);
				return date;
			} catch (NumberFormatException e) {
				logger.error("Error parsing long date: " + e.getMessage());
				return null;// it's ok to return empty so UI won't break it
			}
		} else {
			return null;
		}
	}

	public static String getDateTimeStringByMilliseconds(String ldStr) {
		Date dd = getDateByMilliseconds(ldStr);
		if (dd != null) {
			return dateTimeFormatter2.format(dd);
		}
		return null;
	}

	public static String getDateStringByMilliseconds(String ldStr) {
		Date dd = getDateByMilliseconds(ldStr);
		if (dd != null) {
			return dateFormatter2.format(dd);
		}
		return null;
	}

	public static String getTimeStringByMilliseconds(String ldStr) {
		Date dd = getDateByMilliseconds(ldStr);
		if (dd != null) {
			return timeFormatter2.format(dd);
		}
		return null;
	}

}
