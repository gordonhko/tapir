package com.fusui.tapir.common;


/*
 * @author gko
 */

@SuppressWarnings("serial")
public class TapirException extends Exception {

	public TapirException(Throwable t) {
		this (t.getMessage(), t);
	}
	
	public TapirException(String message) {
		this (message, null);
	}
	
	public TapirException(String message,  Throwable cause) {
		super(message, cause);
	}

}
