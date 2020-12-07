package com.cg.patient.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cg.patient.domain.MedicalStore;
import com.cg.patient.domain.Patient;
import com.cg.patient.exception.PatientException;
import com.cg.patient.repository.PatientRepository;

/**
 * Class which provide all the services to patient.
 * @author Aman Soni
 *
 */
@Service
public class PatientService {
	
	
	
	@Autowired
	PatientRepository patientRepository;
	
	/**
	 * Stores the patient related data inside the database.
	 * @param patient
	 * @return the saved instance of Patient if successfully saved else throw suitable exception.
	 */
	public Patient saveOrUpdate(Patient patient) {

		try {
			patient.setPatientIdentifier(patient.getPatientIdentifier().toUpperCase());
			return patientRepository.save(patient);
		} catch (Exception e) {
			throw new PatientException("PatientIdentifier " + patient.getPatientIdentifier() + " already available");
		}

	}
	
	/**
	 * Retrieves all the patient related data from the database.
	 * @return the iterable of patients.
	 */
	public Iterable<Patient> findAllPatients(){
		return patientRepository.findAll();
		
	}
	
	/**
	 * Find the patient in database based on identifier.
	 * @param patientIdentifier
	 * @return the instance of patient.
	 */
	public Patient findPatientByPatientIdentifier(String patientIdentifier) {
		Patient patient = patientRepository.findByPatientIdentifier(patientIdentifier.toUpperCase());
		if (patient == null) {
			throw new PatientException("Patient Identifier " + patientIdentifier + " not available");
			
		}
		return patient;

	}
	
	/**
	 * Delete the patient data in database based on patient identifier.
	 * @param patientIdentifier
	 * @return true if successfully deleted and vice versa for unsuccessful operation.
	 */
	public boolean deletePatientByPatientIdentifier(String patientIdentifier) {
		Patient patient = findPatientByPatientIdentifier(patientIdentifier.toUpperCase());
		if(patient==null) {
			throw new PatientException("ProjectIdentifier " + patientIdentifier + " not available");
		}
		patientRepository.delete(patient);
		return true;
	}
	
	/**
	 * Find the medicine in the medical store.
	 * @param medicalStore
	 * @param medicineName
	 * @return true if medicine is present in the medical store otherwise return false.
	 */
	public boolean findByMedicine(MedicalStore medicalStore,String medicineName) {
		String[] availableMedicine = medicalStore.getMedicineList();
		for(String med:availableMedicine) {
			if(med.equalsIgnoreCase(medicineName)) {
				return true;
			}
		}
		return false;
	}
	
}
