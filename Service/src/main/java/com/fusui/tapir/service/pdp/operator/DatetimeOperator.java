package com.fusui.tapir.service.pdp.operator;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatetimeOperator implements IOperator {
	private static final Logger logger = LoggerFactory.getLogger(DatetimeOperator.class);

	public Boolean Equal(Date d1, Date d2) {
		return d1.equals(d2);
	}

	public Boolean After(Date d1, Date d2) {
		return d1.after(d2);
	}

	public Boolean Before(Date d1, Date d2) {
		return d1.before(d2);
	}
	
	public Boolean IsEmpty(Date d1) {
		return d1 == null;
	}

	public Boolean IsAnything(Date d1) {
		return d1 != null;
	}

	
	
}
