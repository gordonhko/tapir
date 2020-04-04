package com.fusui.tapir.service.pdp.operator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DoubleOperator implements IOperator  {
	private static final Logger logger = LoggerFactory.getLogger(DoubleOperator.class);

	public DoubleOperator() {
	}
	
	public Double Add(Double d1, Double d2) {
		return d1+d2;
	}
	
	public Double Subtract(Double d1, Double d2) {
		return d1-d2;
	}
	
	public Double Multiple(Double d1, Double d2) {
		return d1*d2;
	}

	public Double Divide(Double d1, Double d2) {
		return d1/d2;
	}
	
	public Boolean Equal(Double d1, Double d2) {
		return d1==d2;
	}

	public Boolean NotEqual(Double d1, Double d2) {
		return d1!=d2;
	}

	public Boolean LargerThan(Double d1, Double d2) {
		return d1>d2;
	}

	public Boolean LargerThanOrEqual(Double d1, Double d2) {
		return d1>=d2;
	}
	
	public Boolean SmallerThan(Double d1, Double d2) {
		return d1<d2;
	}

	public Boolean SmallerThanOrEqual(Double d1, Double d2) {
		return d1<=d2;
	}

	public Boolean IsEmpty(Double d1) {
		return d1 == null;
	}

	public Boolean IsAnything(Double d1) {
		return d1 != null;
	}

}
