package com.fusui.tapir.service.pdp.operator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BooleanOperator implements IOperator {
	private static final Logger logger = LoggerFactory.getLogger(BooleanOperator.class);

	public BooleanOperator() {
	}
	
	public Boolean And(Boolean b1, Boolean b2) {
		return b1 && b2;
	}
	
	public Boolean Or(Boolean b1, Boolean b2) {
		return b1 || b2;
	}

	public Boolean Not(Boolean b1) {
		return !b1;
	}
}
