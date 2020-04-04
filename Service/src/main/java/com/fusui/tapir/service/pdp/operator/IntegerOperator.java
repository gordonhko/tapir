package com.fusui.tapir.service.pdp.operator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegerOperator implements IOperator  {
	private static final Logger logger = LoggerFactory.getLogger(IntegerOperator.class);

	public IntegerOperator() {
	}
	
	public Integer Add(Integer i1, Integer i2) {
		return i1+i2;
	}
	
	public Integer Subtract(Integer i1, Integer i2) {
		return i1-i2;
	}
	
	public Integer Multiple(Integer i1, Integer i2) {
		return i1*i2;
	}

	public Integer Divide(Integer i1, Integer i2) {
		return i1/i2;
	}
	
	public Boolean Equal(Integer i1, Integer i2) {
		return i1==i2;
	}

	public Boolean NotEqual(Integer i1, Integer i2) {
		return i1!=i2;
	}

	public Boolean LargerThan(Integer i1, Integer i2) {
		return i1>i2;
	}

	public Boolean LargerThanOrEqual(Integer i1, Integer i2) {
		return i1>=i2;
	}
	
	public Boolean SmallerThan(Integer i1, Integer i2) {
		return i1<i2;
	}

	public Boolean SmallerThanOrEqual(Integer i1, Integer i2) {
		return i1<=i2;
	}
	
	public Boolean IsEmpty(Integer i1) {
		return i1 == null;
	}

	public Boolean IsAnything(Integer i1) {
		return i1 != null;
	}
	
}
