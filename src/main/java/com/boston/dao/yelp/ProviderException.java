package com.boston.dao.yelp;

public class ProviderException extends Exception 
{
	private static final long serialVersionUID = -7100644073748740016L;
	private final int httpCode;
	private final String providerCode;
	
	public ProviderException(int httpCode, String yelpCode, String msg) {
		super(msg);
		this.httpCode = httpCode;
		this.providerCode = yelpCode;
	}

	public int getHttpCode() {
		return httpCode;
	}

	public String getYelpCode() {
		return providerCode;
	}
}
