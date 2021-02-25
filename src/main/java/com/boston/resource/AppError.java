package com.boston.resource;

import java.io.Serializable;

public class AppError implements Serializable 
{
	private static final long serialVersionUID = 1L;
	private String type;
	private String title;
	private int status;
	private String detail;
	private String instance;
	
	// Extra for debugging but we may want to turn this off for production
	private String stackTrace;

	public AppError() {
	}

	public AppError(Integer status, String title) {
		this.status = status;
		this.setTitle(title);
	}

	public AppError(Integer status, String title, String detail) {
		this.status = status;
		this.setTitle(title);
		this.detail = detail;
	}

	public int getCode() {
		return status;
	}

	public void setCode(int code) {
		this.status = code;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getInstance() {
		return instance;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

}
