package com.boston.resource;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.boston.service.InvalidBusinessException;

@ControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler // base class is just extra
{
	@ExceptionHandler({UnsupportedOperationException.class, IllegalArgumentException.class})
	@ResponseBody
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public AppError clientHandler(HttpServletRequest req, Exception ex) {
	    return new AppError(HttpStatus.BAD_REQUEST.value(), "Client error", ex.getMessage());
	} 
	
	@ExceptionHandler(InvalidBusinessException.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public AppError notFound(HttpServletRequest req, InvalidBusinessException ex) {
	    return new AppError(HttpStatus.NOT_FOUND.value(), "Unfound business: " + ex.getBusId(), ex.getMessage());
	}

	@ExceptionHandler
	@ResponseBody
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public AppError defaultHandler(HttpServletRequest req, Exception ex) {
	    return new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal error", ex.getMessage());
	}

}
