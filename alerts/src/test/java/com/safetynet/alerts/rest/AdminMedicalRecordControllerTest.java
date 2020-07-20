package com.safetynet.alerts.rest;

import com.safetynet.alerts.dao.IMedicalRecordDAO;
import com.safetynet.alerts.models.Firestation;
import com.safetynet.alerts.models.MedicalRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.safetynet.alerts.utils.JsonConvert.feedWithJava;
import static com.safetynet.alerts.utils.LocalDateFormatter.convertToString;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminMedicalRecordControllerTest {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    private AdminMedicalRecordController adminMedicalRecordController;

    @Mock
    IMedicalRecordDAO medicalRecordDAO;

    private final String rootURL = "/medicalrecord/";

    private List<String> medicationList = new ArrayList<>();

    private List<String> allergiesList = new ArrayList<>();

    private MedicalRecord medicalRecord1 = new MedicalRecord("john", "boyd",
            LocalDate.of(1984, 3, 6));

    private MedicalRecord medicalRecord2 = new MedicalRecord("jacob", "boyd",
            LocalDate.of(1989, 3, 6));

    @BeforeEach
    void setUp() {
        medicationList.add("aznol:350mg"); medicationList.add("hydrapermazol:100mg");
        allergiesList.add("nillacilan");
        medicalRecord1.setMedications(medicationList);
        medicalRecord1.setAllergies(allergiesList);
        medicationList.clear();
        medicationList.add("pharmacol:5000mg"); medicationList.add("terazine:10mg"); medicationList.add("noznazol:250mg");
        medicalRecord2.setMedications(medicationList);

        when(medicalRecordDAO.findAll()).thenReturn(List.of(medicalRecord1, medicalRecord2));
        this.adminMedicalRecordController.medicalRecordDAO = medicalRecordDAO;
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAllMedicalRecord() throws Exception {
        //***********GIVEN*************
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(rootURL)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(medicalRecordDAO, Mockito.times(0)).findAll();

        //**************WHEN-THEN****************************
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(containsString("1984")))
                //.andExpect(content().json("{'birthday':'03-06-1984'}"))
                .andExpect(jsonPath("$[0].birthday").value(convertToString(medicalRecord1.getBirthday(),null)))
                .andExpect(jsonPath("$[0].firstName").value(medicalRecord1.getFirstName()))
                .andReturn();
        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(medicalRecordDAO, Mockito.times(1)).findAll();

        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        String expectedJson = null;
        expectedJson = feedWithJava(List.of(medicalRecord1, medicalRecord2));
        JSONAssert.assertEquals(expectedJson, mvcResult.getResponse().getContentAsString(), false);
    }

    @Test
    void getAllMedicalRecordVoid() throws Exception {
        //***********GIVEN*************
        when(medicalRecordDAO.findAll()).thenReturn(new ArrayList<MedicalRecord>());
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(rootURL);

        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(medicalRecordDAO, Mockito.times(0)).findAll();

        //**************WHEN-THEN****************************
        mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isNoContent());

        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(medicalRecordDAO, Mockito.times(1)).findAll();//.save(any());
    }
}