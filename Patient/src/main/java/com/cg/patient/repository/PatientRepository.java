package com.cg.patient.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cg.patient.domain.Patient;

/**
 * Repository interface implemented by spring itself for providing various CRUD operations on Patient POJO.
 * @author Aman Soni
 *
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient,Long> {
	
	/**
	 * A method to find the patient inside the repository based on patient identifier.
	 * @param patientIdentifier
	 * @return the patient instance if patient with particular identifier exist otherwise return null.
	 */
	Patient findByPatientIdentifier(String patientIdentifier);	

}
