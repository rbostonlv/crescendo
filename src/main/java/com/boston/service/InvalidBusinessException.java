package com.boston.service;

public class InvalidBusinessException extends Exception 
{
	private static final long serialVersionUID = -7100644073748740016L;

	private final String busId; 
	
	public InvalidBusinessException(String busId, String msg) {
		super(msg);
		this.busId = busId;
	}

	public InvalidBusinessException(String busId, String msg, Throwable t) {
		super(msg, t);
		this.busId = busId;
	}

	public String getBusId() {
		return busId;
	}
}
