package com.fusui.tapir.service.util;

import java.text.ParseException;
import java.util.Date;

import com.fusui.tapir.common.FoundryDateTimeFormatter;

public class EvaluationCtx {

	/*
	 * TODO other time based access control: getMonth getDayOfWeek (1-7) getDayOfYear (ex. even userid can access on even calendar day only)
	 */

	// private Date currentTime;

	public String getCurrentDateTime() {
		return FoundryDateTimeFormatter.getMillisecondsByDateTime(new Date());
	}

	// Date in string
	public String getCurrentDate() throws ParseException {
		// dateTimeHelper();
		return FoundryDateTimeFormatter.getMillisecondsByDate(new Date());
	}

	// time in string
	public String getCurrentTime() throws ParseException {
		return FoundryDateTimeFormatter.getMillisecondsByTime(new Date());
	}

	public String getCurrentWeekday() throws ParseException {
		return FoundryDateTimeFormatter.getCurrentWeekday();
	}

	/**
	 * 
	 * Sets the value for the current date. The current time, current date, and current dateTime are consistent, so that they all represent the same moment in this evaluation context
	 */
	// private void dateTimeHelper() {
	//
	// if (currentTime != null)
	// return;
	//
	// // get the current moment
	// currentTime = new Date();
	//
	// }

}
