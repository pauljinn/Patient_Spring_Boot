package com.cg.patient.web;

import java.util.Arrays;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cg.patient.domain.MedicalStore;
import com.cg.patient.domain.Patient;
import com.cg.patient.exception.PatientException;
import com.cg.patient.service.MapValidationErrorService;
import com.cg.patient.service.PatientService;
/**
 * A rest controller which handles all the URL request given by the application for various services.
 * @author Aman Soni
 *
 */
@RestController
@RequestMapping("/api/patients")
public class PatientController {
	
	@Autowired
	private PatientService patientService;
	
	@Autowired
	private MapValidationErrorService mapValidationErrorService;
	public MedicalStore medicalStore = new MedicalStore("Avon", 98383L, "XYZ", new String[] {"dispirin","d-cold"} ,"Shubham");
	
	/**
	 * Handles the request for storing the data of the new patient.
	 * @param patient
	 * @param result
	 * @return response entity with status as created if patient is successfully saved otherwise throw the appropriate exception related to patient.
	 */
	@PostMapping("")
	public ResponseEntity<?> createNewPatient(@Valid @RequestBody Patient patient, BindingResult result) {
		
		ResponseEntity<?> errorMap =  mapValidationErrorService.mapValidationError(result);
		if(errorMap!=null) return errorMap;
		Patient newPatient = patientService.saveOrUpdate(patient);
		return new ResponseEntity<Patient>(newPatient, HttpStatus.CREATED);
	}
	
	/**
	 * Handles the request for showing the data of all the patients.
	 * @return the iterable of type patients.
	 */
	@GetMapping("/all")
	public Iterable<Patient> getAllPatient(){
		return patientService.findAllPatients();
	}
	
	/**
	 * Handles the request for getting the patient data based on patient identifier.
	 * @param patientIdentifier
	 * @return ResponseEntity of patient type having the details of the patient with given identifier and status as ok and throw the patient exception if the patient is not present.
	 */
	@GetMapping("/{patientIdentifier}")
	public ResponseEntity<?> getPatientById(@PathVariable String patientIdentifier){
		return new ResponseEntity<Patient>( patientService.findPatientByPatientIdentifier(patientIdentifier),HttpStatus.OK);
	}
	
	/**
	 * Handles the request for deleting the patient in the database with the given patient identifier.
	 * @param patientIdentifier
	 * @return the response entity of string with appropriate message if patient is successfully deleted otherwise throw the patient exception with appropriate message.
	 */
	@DeleteMapping("/{patientIdentifier}")
	public ResponseEntity<?> deletePatient(@PathVariable String patientIdentifier){
		patientService.deletePatientByPatientIdentifier(patientIdentifier);
		return new ResponseEntity<String> ("Patient with Id : "+patientIdentifier.toUpperCase()+" Deleted!",HttpStatus.OK);
	}
	
	/**
	 * Handles the request for uploading the prescription for the patient with given identifier given by the doctor.
	 * @param patientIdentifier
	 * @param patient
	 * @return the response entity of patient type with status as ok if patient is successfully deleted 
	 * and throw patient exception in case patient with given identifier is not exist in the database.
	 */
	@RequestMapping(value = "/uploadPrescription/{patientIdentifier}",method = RequestMethod.PATCH)
	public ResponseEntity<?> uploadPrescriptionCopy(@PathVariable String patientIdentifier, @RequestBody Patient patient) {
		Patient existingPatient = patientService.findPatientByPatientIdentifier(patientIdentifier);
		existingPatient.setPrescription(patient.getPrescription());
		return new ResponseEntity<Patient>(patientService.saveOrUpdate(existingPatient),HttpStatus.OK);
	}
	
	/**
	 * Handles the request for uploading the medical history for the patient with given identifier.
	 * @param patientIdentifier
	 * @param patient
	 * @return response entity of patient type with status as ok if medical history is successfully uploaded and
	 * throw the patient exception if patient with identifier not exist inside the database.
	 */
	@RequestMapping(value = "/uploadMedicalHistory/{patientIdentifier}",method = RequestMethod.PATCH)
	public ResponseEntity<?> uploadMedicalHistory(@PathVariable String patientIdentifier, @RequestBody Patient patient) {
		Patient existingPatient = patientService.findPatientByPatientIdentifier(patientIdentifier);
		existingPatient.setMedicalHistory(patient.getMedicalHistory());
		return new ResponseEntity<Patient>(patientService.saveOrUpdate(existingPatient),HttpStatus.OK);
	}
	/**
	 * Handles the request for showing  the medical history for patient with given identifier.
	 * @param patientIdentifier
	 * @return response entity of string type with status as ok if patient with given patient identifier
	 * exist inside the database otherwise return response entity with status as BAD_REQUEST.
	 */
	@RequestMapping(value = "/viewMedicalHistory/{patientIdentifier}",method = RequestMethod.GET)
	public ResponseEntity<?> viewMedicalHistory(@PathVariable String patientIdentifier){
		Patient existingPatient = patientService.findPatientByPatientIdentifier(patientIdentifier);
		if(existingPatient.getMedicalHistory()==null) {
			return new ResponseEntity<String>("No medical history available",HttpStatus.BAD_REQUEST);
		}
		else {
			return new ResponseEntity<String>(existingPatient.getMedicalHistory(),HttpStatus.OK);
		}
	}
	
	/**
	 * Handles the request to search the particular medicine inside the medical store.
	 * @param medicineMap
	 * @return response entity of string type with status as ok if medicine is available inside the medical store
	 * and return response entity of string type with status as BAD_REQUEST if medicine is not available.
	 */
	@RequestMapping(value="/searchMedicalStore",method = RequestMethod.POST)
	public ResponseEntity<?> searchMedicalStore(@RequestBody Map<String,String> medicineMap) {
		String medicineName = medicineMap.get("medicineName");
		boolean medicineAvailable = patientService.findByMedicine(medicalStore,medicineName);
		if(medicineAvailable) {
			return new ResponseEntity<String>(medicineName+" is available in the medical store",HttpStatus.OK);
		}
		else {
			return new ResponseEntity<String>(medicineName+" is not available in the medical store",HttpStatus.BAD_REQUEST);
		}
		
	}
	
	/**
	 * Handles the request for oredering medicine based on the doctor prescription.
	 * @param patientIdentifier
	 * @return the appropriate response entity on various status of order, existence of patient and prescription  
	 * like patient not exist, prescription not uploaded, order already placed, order successfully placed etc.
	 */
	@RequestMapping(value="/orderMedicine/{patientIdentifier}",method = RequestMethod.GET)
	public ResponseEntity<?> orderMedicineAsPerDoctorPrescription(@PathVariable String patientIdentifier){
		boolean orderAdded = false;
		//Counter for checking whether order is already present for the given patient.
		int count = 1;
		Patient patient = patientService.findPatientByPatientIdentifier(patientIdentifier);
		if(patient!=null) {
			String prescription = patient.getPrescription();
			if(prescription!=null) {
				String[] medicineToOrder = prescription.split(",");
				for(String medicine:medicineToOrder) {
					if(patientService.findByMedicine(medicalStore, medicine)) {
						if(medicalStore.getOrderMap().containsKey(patient.getPatientIdentifier().toUpperCase())) {
							if(count!=1) {
								medicalStore.getOrderMap().put(patientIdentifier.toUpperCase(), medicalStore.getOrderMap().get(patientIdentifier.toUpperCase()).concat(","+medicine));
							}
							else {
								return new ResponseEntity<String>("Order already placed for patient with identifier "+patientIdentifier.toUpperCase(),HttpStatus.BAD_REQUEST);
							}
						}
						else {
							Map<String,String> orderUpdatedMap = medicalStore.getOrderMap();
							orderUpdatedMap.put(patientIdentifier.toUpperCase(), medicine);
							medicalStore.setOrderMap(orderUpdatedMap);
							orderAdded = true;
							count++;
						}
					}
				}
				if(orderAdded) {
					if(medicalStore.getOrderMap().get(patientIdentifier.toUpperCase()).split(",").length==medicineToOrder.length) {
						return new ResponseEntity<String>("Order of all medicines placed successfully for patient "+patientIdentifier,HttpStatus.ACCEPTED);
					}
					else {
						return new ResponseEntity<String>("Order of these medicines with names {"+ medicalStore.getOrderMap().get(patientIdentifier.toUpperCase())+"} placed successfully ",HttpStatus.CONFLICT);
					}
				}
				else {
					return new ResponseEntity<String>("Ordered medicine {"+ Arrays.toString(medicineToOrder)+"} is out of stock",HttpStatus.BAD_REQUEST);
				}
				
			}
			else {
				return new ResponseEntity<String>("Upload the doctor prescription for patient with identifer "+patientIdentifier,HttpStatus.BAD_REQUEST);
			}
			
		}
		else {
			return new ResponseEntity<String>("No patient available with identifier: "+patientIdentifier,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	/**
	 * Method to handle the request for viewing the order history.
	 * @param patientIdentifier
	 * @return appropriate response entity if order is available and if order is not available for particular patient identifier.
	 */
	@RequestMapping(value="/viewOrderHistory/{patientIdentifier}",method = RequestMethod.GET)
	public ResponseEntity<?> viewOrderHistory(@PathVariable String patientIdentifier){
		if(medicalStore.getOrderMap().containsKey(patientIdentifier.toUpperCase())) {
			return new ResponseEntity<String>("Order available for these medicines: " + medicalStore.getOrderMap().get(patientIdentifier.toUpperCase()),HttpStatus.OK);
		}
		else {
			return new ResponseEntity<String>("No order available",HttpStatus.BAD_REQUEST);
		}
	}
}
