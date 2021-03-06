package com.cg.patient.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.cg.patient.domain.*;

/**
 * A class for testing the various methods of PatientRepository.
 * @author Aman Soni
 *
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class PatientRepositoryTest {
	
	@Autowired
	private PatientRepository patientRepository;

	/**
	 * Test the findByPatientIdentifier method of patientRepository if the patient exist in the repository.
	 * @throws Exception
	 */
	@Test
	void test_findByPatientIdentifier() throws Exception{
		Patient patient= patientRepository.findByPatientIdentifier("PA20");
		assertNotNull(patient);
		
	}
}
