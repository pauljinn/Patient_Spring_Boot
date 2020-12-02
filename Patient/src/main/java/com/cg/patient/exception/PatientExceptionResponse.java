package com.cg.patient.exception;

public class PatientExceptionResponse {
	String responseMessage;

	public PatientExceptionResponse(String responseMessage) {
		super();
		this.responseMessage = responseMessage;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	
	

}
