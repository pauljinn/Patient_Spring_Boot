package com.cg.patient.exception;

/**
 * A class for creating the response of various patient related exception.
 * @author Aman Soni
 *
 */
public class PatientExceptionResponse {
	/**
	 * Response message from the patient exception
	 */
	String responseMessage;

	/**
	 * Create the instance of patientExceptionResponse with given reponse message.
	 * @param responseMessage
	 */
	public PatientExceptionResponse(String responseMessage) {
		super();
		this.responseMessage = responseMessage;
	}
	
	/**
	 * @return the response message.
	 */
	public String getResponseMessage() {
		return responseMessage;
	}

	/**
	 * Set the response message got from an exception.
	 * @param responseMessage
	 */
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	
	

}
