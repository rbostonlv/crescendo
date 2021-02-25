package com.boston.service;

public class MigratedBusinessException extends Exception 
{
	private static final long serialVersionUID = -7100644073748740016L;
	private final String newBusId;
	
	public MigratedBusinessException(String newBusId) {
		this.newBusId = newBusId;
	}

	public String getNewBusId() {
		return newBusId;
	}
}
