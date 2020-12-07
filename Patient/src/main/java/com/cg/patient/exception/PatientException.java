package com.cg.patient.exception;

/**
 * Class for exception of the patient.
 * @author Aman Soni
 *
 */
public class PatientException extends RuntimeException{
	
	/**
	 * Create an instance of PatientException.
	 */
	public PatientException() {
		super();
	}
	
	/**
	 * Create an instance of PatientException with given message.
	 * @param errMessage
	 */
	public PatientException(String errMessage) {
		super(errMessage);
	}

}
