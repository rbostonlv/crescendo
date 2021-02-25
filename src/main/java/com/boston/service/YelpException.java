package com.boston.service;

public class YelpException extends Exception 
{
	private static final long serialVersionUID = -7100644073748740016L;
	private final int httpCode;
	private final String yelpCode;
	
	public YelpException(int httpCode, String yelpCode, String msg) {
		super(msg);
		this.httpCode = httpCode;
		this.yelpCode = yelpCode;
	}

	public int getHttpCode() {
		return httpCode;
	}

	public String getYelpCode() {
		return yelpCode;
	}
}
