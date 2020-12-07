package com.cg.patient.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Handles all the exception coming during various request on various layer i.e service, controller.
 * @author Aman Soni
 *
 */
@ControllerAdvice
@RestController
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
	/**
	 * A exception handler for handling all the patient related exception.
	 * @param ex
	 * @param request
	 * @return response entity with message as the exception message and status as BAD_REQUEST.
	 */
	@ExceptionHandler
	public final ResponseEntity<Object> handlePatientException(Exception ex, WebRequest request){
		PatientExceptionResponse exceptionResponse=new PatientExceptionResponse(ex.getMessage());
		return new ResponseEntity<Object>(exceptionResponse,HttpStatus.BAD_REQUEST);
	}

}
