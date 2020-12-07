package com.cg.patient.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

/**
 * A service for validating the data of the patient.
 * @author Aman Soni
 *
 */
@Service
public class MapValidationErrorService {
	
	/**
	 * Validate the instance of patient based on various validation specified on Patient POJO
	 * @param result
	 * @return null if validation is successful otherwise return the map with error name as keys and values as the message.
	 */
	public ResponseEntity<?> mapValidationError(BindingResult result) {
		if(result.hasErrors()) {
			Map<String, String> errorMap = new HashMap<>();
			for (FieldError fieldError :  result.getFieldErrors()) {
				errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
			}
			return new ResponseEntity<Map<String,String>>(errorMap,HttpStatus.BAD_REQUEST);
		}
		return null;
	}	

}
