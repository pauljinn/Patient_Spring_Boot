package com.cg.patient.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.BindingResult;

import com.cg.patient.domain.Patient;
import com.cg.patient.exception.PatientException;
import com.cg.patient.service.MapValidationErrorService;
import com.cg.patient.service.PatientService;
import com.cg.patient.web.PatientController;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.spy;

/**
 * A class to test the various methods of the rest controller.
 * @author Aman Soni
 *
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = PatientController.class)
@AutoConfigureMockMvc
class PatientControllerTest {
	
	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	PatientService patientService;
	
	@MockBean
	MapValidationErrorService mapValidationErrorService;
	
	@InjectMocks
	PatientController patientController;
	
	/**
	 * Testing the getPatientById method of the rest controller if the patient with given patientIdentifier exist in the repository.
	 * @throws Exception
	 */
	@Test
	void test1_getPatientById() throws Exception {
		BDDMockito.given(patientService.findPatientByPatientIdentifier("PA01")).willReturn(new Patient("PA01","Aman",23,9883L,"XYZ"));
		mockMvc.perform(get("/api/patients/PA01"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$").isMap())
		.andExpect(jsonPath("patientName").value("Aman"))
		.andExpect(jsonPath("patientIdentifier").value("PA01"))
		.andExpect(jsonPath("patientAge").value(23))
		.andExpect(jsonPath("phoneNumber").value(9883L));
	}
	
	/**
	 * Testing the getPatientById method of the rest controller if the patient with given patientIdentifier doesn't exist in the repository.
	 * @throws Exception
	 */
	@Test
	void test2_getPatientById() throws Exception {
		BDDMockito.given(patientService.findPatientByPatientIdentifier("PA01")).willThrow(new PatientException());
		mockMvc.perform(get("/api/patients/PA01"))
		.andExpect(status().isBadRequest());
	}
	
	/**
	 * Testing the deletePatient method of the rest controller if the patient with given patientIdentifier successfully deleted.
	 * @throws Exception
	 */
	@Test
	void test1_deletePatient() throws Exception{
		BDDMockito.given(patientService.deletePatientByPatientIdentifier(Mockito.anyString())).willReturn(true);
		mockMvc.perform(delete("/api/patients/PA01"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$").isString())
		.andExpect(jsonPath("$").value("Patient with Id : PA01 Deleted!"));
	}
	
	/**
	 * Testing the deletePatient method of the rest controller if the patient with given patientIdentifier not deleted
	 * because patient with given patientIdentifer not exist in the repository.
	 * @throws Exception
	 */
	@Test
	void test2_deletePatient_throwPatientException() throws Exception{
		BDDMockito.given(patientService.deletePatientByPatientIdentifier(Mockito.anyString())).willThrow(new PatientException());
		mockMvc.perform(delete("/api/patients/PA01"))
		.andExpect(status().isBadRequest());
	}
	
	/**
	 * Testing the createNewPatient method of the rest controller by giving the valid data of the patient.
	 * @throws Exception
	 */
	@Test
	void test_createNewPatient() throws Exception{
		BindingResult result=null;
		BDDMockito.given(mapValidationErrorService.mapValidationError(result)).willReturn(null);
		Patient patient = new Patient("PA01","Aman",23,9883L,"XYZ");
		BDDMockito.given(patientService.saveOrUpdate(Mockito.any())).willReturn(patient);
		ObjectMapper objectMapper = new ObjectMapper();
		String inputJson = objectMapper.writeValueAsString(patient);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/patients")
		         .contentType(MediaType.APPLICATION_JSON_VALUE)
		         .content(inputJson))
				 .andExpect(status().isCreated())
				 .andExpect(jsonPath("$").isMap())
				 .andExpect(jsonPath("patientName").value("Aman"))
				 .andExpect(jsonPath("patientIdentifier").value("PA01"))
				 .andExpect(jsonPath("patientAge").value(23))
				 .andExpect(jsonPath("phoneNumber").value(9883L));
	}
	
	/**
	 * Testing the findAll method of the rest controller by checking whether the data of the patients posted is what given by the service layer.
	 * @throws Exception
	 */
	@Test
	void test_findAll() throws Exception{
		Iterable<Patient> patientList = (Iterable<Patient>)(Object)Arrays.asList(new Patient[] {new Patient("PA01","Aman",23,9883L,"XYZ")});
		BDDMockito.given(patientService.findAllPatients()).willReturn(patientList);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/patients/all"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.*",hasSize(1)))
				.andExpect(jsonPath("$[0].patientName", is("Aman")))
				.andExpect(jsonPath("$[0].patientAge",is(23)))
				.andExpect(jsonPath("$[0].phoneNumber",is(9883)))
				.andExpect(jsonPath("$[0].patientAddress",is("XYZ")))
				.andExpect(jsonPath("$[0].patientIdentifier",is("PA01")));
	}
	
	/**
	 * Testing the uploadPrescriptionCopy method of the controller on a patient which exist in repository.
	 * @throws Exception
	 */
	@Test
	void test_uploadPrescriptionCopy() throws Exception{
		BDDMockito.given(patientService.findPatientByPatientIdentifier("PA01")).willReturn(new Patient("PA01","Aman",23,9883L,"XYZ"));
		Patient patientWithPrescription = new Patient("PA01","Aman",23,9883L,"XYZ");
		patientWithPrescription.setPrescription("ABC");
		BDDMockito.given(patientService.saveOrUpdate(Mockito.any())).willReturn(patientWithPrescription);
		ObjectMapper objectMapper = new ObjectMapper();
		String inputJson = objectMapper.writeValueAsString(patientWithPrescription);
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/patients/uploadPrescription/PA01")
		.contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(inputJson))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$").isMap())
		.andExpect(jsonPath("patientName").value("Aman"))
		.andExpect(jsonPath("patientIdentifier").value("PA01"))
		.andExpect(jsonPath("patientAge").value(23))
		.andExpect(jsonPath("phoneNumber").value(9883L))
		.andExpect(jsonPath("prescription").value("ABC"));
	}
	
	/**
	 * Testing the uploadMedicalHistory method of the controller on a patient which exist in repository.
	 * @throws Exception
	 */
	@Test
	void test_uploadMedicalHistory() throws Exception{
		BDDMockito.given(patientService.findPatientByPatientIdentifier("PA01")).willReturn(new Patient("PA01","Aman",23,9883L,"XYZ"));
		Patient patientWithMedicalHistory = new Patient("PA01","Aman",23,9883L,"XYZ");
		patientWithMedicalHistory.setMedicalHistory("Had Cancer");
		BDDMockito.given(patientService.saveOrUpdate(Mockito.any())).willReturn(patientWithMedicalHistory);
		ObjectMapper objectMapper = new ObjectMapper();
		String inputJson = objectMapper.writeValueAsString(patientWithMedicalHistory);
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/patients/uploadMedicalHistory/PA01")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
		        .content(inputJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isMap())
				.andExpect(jsonPath("patientName").value("Aman"))
				.andExpect(jsonPath("patientIdentifier").value("PA01"))
				.andExpect(jsonPath("patientAge").value(23))
				.andExpect(jsonPath("phoneNumber").value(9883L))
				.andExpect(jsonPath("medicalHistory").value("Had Cancer"));
	}
	
	/**
	 * Testing the viewMedicalHistory method of the controller on the patient whose medical history is not present in repository.
	 * @throws Exception
	 */
	@Test
	void test1_viewMedicalHistory() throws Exception{
		BDDMockito.given(patientService.findPatientByPatientIdentifier("PA01")).willReturn(new Patient("PA01","Aman",23,9883L,"XYZ"));
		mockMvc.perform(MockMvcRequestBuilders.get("/api/patients/viewMedicalHistory/PA01"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$").isString())
				.andExpect(jsonPath("$").value("No medical history available"));
	}
	
	/**
	 * Testing the viewMedicalHistory method of the controller on the patient whose medical history is available in the repository.
	 * @throws Exception
	 */
	@Test
	void test2_viewMedicalHistory() throws Exception{
		Patient patient = new Patient("PA01","Aman",23,9883L,"XYZ");
		patient.setMedicalHistory("Had Cancer");
		BDDMockito.given(patientService.findPatientByPatientIdentifier("PA01")).willReturn(patient);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/patients/viewMedicalHistory/PA01"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isString())
				.andExpect(jsonPath("$").value("Had Cancer"));
	}
	
	/**
	 * Testing the searchMedicalStore method of the controller on the medicine which is available in the medical store.
	 * @throws Exception
	 */
	@Test
	void test1_searchMedicalStore() throws Exception{
		BDDMockito.given(patientService.findByMedicine(Mockito.any(),Mockito.any())).willReturn(true);
		Map<String,String> medicineMap = new HashMap<>();
		medicineMap.put("medicineName","dispirin");
		//System.out.println(patientController.searchMedicalStore(medicineMap));
		ObjectMapper objectMapper = new ObjectMapper();
		String inputJson = objectMapper.writeValueAsString(medicineMap);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/patients/searchMedicalStore")
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.content(inputJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isString())
			.andExpect(jsonPath("$").value("dispirin is available in the medical store"));;
	}
	
	/**
	 * Testing the searchMedicalStore method of the controller on the medicine which is not available in the medical store.
	 * @throws Exception
	 */
	@Test
	void test2_searchMedicalStore() throws Exception{
		BDDMockito.given(patientService.findByMedicine(Mockito.any(),Mockito.any())).willReturn(false);
		Map<String,String> medicineMap = new HashMap<>();
		medicineMap.put("medicineName","dispirin");
		//System.out.println(patientController.searchMedicalStore(medicineMap));
		ObjectMapper objectMapper = new ObjectMapper();
		String inputJson = objectMapper.writeValueAsString(medicineMap);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/patients/searchMedicalStore")
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.content(inputJson))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$").isString())
			.andExpect(jsonPath("$").value("dispirin is not available in the medical store"));
	}
	
	/**
	 * Testing the orderMedicineAsPerDoctorPrecription method of the controller when the prescription is set for the patient
	 * with given patientIdentifier and the medicine written in the prescription is available inside the medical store.
	 * @throws Exception
	 */
	@Test
	void test1_orderMedicineAsPerDoctorPrescription() throws Exception{
		BDDMockito.given(patientService.findByMedicine(Mockito.any(),Mockito.any())).willReturn(true);
		Patient patientWithPrescription = new Patient("PA01","Aman",23,9883L,"XYZ");
		patientWithPrescription.setPrescription("ABC,EFG");
		BDDMockito.given(patientService.findPatientByPatientIdentifier(Mockito.any())).willReturn(patientWithPrescription);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/patients/orderMedicine/PA01"))
				.andExpect(status().isAccepted())
				.andExpect(jsonPath("$").isString())
				.andExpect(jsonPath("$").value("Order of all medicines placed successfully for patient PA01"));
	}
	
	/**
	 * Testing the orderMedicineAsPerDoctorPrecription method of the controller when the prescription is set for the patient
	 * with given patientIdentifier and the medicine written in the prescription is not available inside the medical store.
	 * @throws Exception
	 */
	@Test
	void test2_orderMedicineAsPerDoctorPrescription() throws Exception{
		BDDMockito.given(patientService.findByMedicine(Mockito.any(),Mockito.any())).willReturn(false);
		Patient patientWithPrescription = new Patient("PA01","Aman",23,9883L,"XYZ");
		patientWithPrescription.setPrescription("ABC,EFG");
		BDDMockito.given(patientService.findPatientByPatientIdentifier(Mockito.any())).willReturn(patientWithPrescription);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/patients/orderMedicine/PA01"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$").isString())
				.andExpect(jsonPath("$").value("Ordered medicine {[ABC, EFG]} is out of stock"));
	}
	
	/**
	 * Testing the orderMedicineAsPerDoctorPrecription method of the controller when the prescription is set for the patient
	 * and some medicine written in the prescription is available inside the medical store.
	 * @throws Exception
	 */
	@Test
	void test3_orderMedicineAsPerDoctorPrescription() throws Exception{
		BDDMockito.given(patientService.findByMedicine(Mockito.any(),Mockito.eq("dispirin"))).willReturn(true);
		BDDMockito.given(patientService.findByMedicine(Mockito.any(),Mockito.eq("EFG"))).willReturn(false);
		Patient patientWithPrescription = new Patient("PA02","Aman",23,9883L,"XYZ");
		patientWithPrescription.setPrescription("dispirin,EFG");
		BDDMockito.given(patientService.findPatientByPatientIdentifier("PA02")).willReturn(patientWithPrescription);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/patients/orderMedicine/PA02"))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$").isString())
				.andExpect(jsonPath("$").value("Order of these medicines with names {dispirin} placed successfully"));
	}
	
	/**
	 * Testing the orderMedicineAsPerDoctorPrecription method of the controller when the prescription is not set for the patient
	 * with given patientIdentifier.
	 * @throws Exception
	 */
	@Test
	void test4_orderMedicineAsPerDoctorPrescription() throws Exception{
		Patient patient = new Patient("PA04","Aman",23,9883L,"XYZ");
		BDDMockito.given(patientService.findPatientByPatientIdentifier(Mockito.any())).willReturn(patient);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/patients/orderMedicine/PA04"))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$").isString())
		.andExpect(jsonPath("$").value("Upload the doctor prescription for patient with identifer PA04"));
	}
	
	/**
	 * Testing the orderMedicineAsPerDoctorPrecription method of the controller when the patient with given patientIdentifier
	 * is not exist in the repository.
	 * @throws Exception
	 */
	@Test
	void test5_orderMedicineAsPerDoctorPrescription() throws Exception{
		BDDMockito.given(patientService.findPatientByPatientIdentifier(Mockito.anyString())).willReturn(null);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/patients/orderMedicine/PA05"))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$").isString())
				.andExpect(jsonPath("$").value("No patient available with identifier: PA05"));
	}
	
	/**
	 * Testing the viewOrderHisory method of the rest controller when the there is some order exist for the patient with given patientIdentifier.
	 * @throws Exception
	 */
	@Test
	void test1_viewOrderHistory() throws Exception{
		mockMvc.perform(MockMvcRequestBuilders.get("/api/patients/viewOrderHistory/PA01"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isString())
				.andExpect(jsonPath("$").value("Order available for these medicines: ABC,EFG"));
	}
	
	/**
	 * Testing the viewOrderHisory method of the rest controller when the there is no order exist for the patient with given patientIdentifier.
	 * @throws Exception
	 */
	@Test
	void test2_viewOrderHistory() throws Exception{
		mockMvc.perform(MockMvcRequestBuilders.get("/api/patients/viewOrderHistory/PA05"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$").isString())
				.andExpect(jsonPath("$").value("No order available"));
	}
}
