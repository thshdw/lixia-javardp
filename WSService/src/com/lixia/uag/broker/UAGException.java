package com.lixia.uag.broker;

public class UAGException extends Exception {

	/**
	 * creates the exception with given message
	 * @param error the error messae
	 */
	public UAGException(String error) {
		super(error);
	}
	
	/**
	 * creates the exception with given message
	 * @param error the error messae
	 * @param throwable the cause 
	 */
	public UAGException(String error, Throwable throwable) {
		super(error, throwable);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
