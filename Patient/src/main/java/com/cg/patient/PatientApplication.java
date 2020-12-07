package com.cg.patient;

import org.springframework.boot.SpringApplication;
import java.util.regex.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Class for starting our spring boot application.
 * @author Aman Soni
 *
 */
@SpringBootApplication
public class PatientApplication {

	/**
	 * Start the spring boot application.
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(PatientApplication.class, args);
	}

}
