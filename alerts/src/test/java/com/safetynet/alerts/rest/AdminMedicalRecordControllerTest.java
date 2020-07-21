package com.safetynet.alerts.rest;

import com.safetynet.alerts.dao.IMedicalRecordDAO;
import com.safetynet.alerts.models.MedicalRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
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

import static com.safetynet.alerts.utils.Jackson.convertJavaToJson;
import static com.safetynet.alerts.utils.Jackson.deepCopy;
import static com.safetynet.alerts.utils.JsonConvertForTest.parseResponse;
import static com.safetynet.alerts.utils.LocalDateFormatter.convertToString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    private MedicalRecord unknownMedicalRecord = new MedicalRecord("grrr","trex",
            null);

    private MedicalRecord medicalRecordCreated = new MedicalRecord("jack", "mortimer",
            LocalDate.of(1961, 1, 25));

    private MedicalRecord medicalRecordUpdated = deepCopy(medicalRecordCreated, MedicalRecord.class);

    @BeforeEach
    void setUp() {
        medicationList.add("aznol:350mg"); medicationList.add("hydrapermazol:100mg");
        allergiesList.add("nillacilan");
        medicalRecord1.setMedications(medicationList);
        medicalRecord1.setAllergies(allergiesList);
        medicationList.clear();
        medicationList.add("pharmacol:5000mg"); medicationList.add("terazine:10mg"); medicationList.add("noznazol:250mg");
        medicalRecord2.setMedications(medicationList);
        allergiesList.clear();
        allergiesList.add("peanut");
        medicalRecordCreated.setAllergies(allergiesList);
        medicationList.clear();
        medicationList.add("tetracyclaz:650mg");
        medicalRecordUpdated.setMedications(medicationList);
        medicalRecordUpdated.setAllergies(allergiesList);

        when(medicalRecordDAO.findAll()).thenReturn(List.of(medicalRecord1, medicalRecord2));
        when(medicalRecordDAO.findByName("john", "boyd")).thenReturn(medicalRecord1);
        when(medicalRecordDAO.findByName("grrr", "trex")).thenReturn(null);
        when(medicalRecordDAO.save(medicalRecordCreated)).thenReturn(medicalRecordCreated);
        when(medicalRecordDAO.save(medicalRecord1)).thenReturn(null);
        when(medicalRecordDAO.update(medicalRecordUpdated)).thenReturn(medicalRecordUpdated);
        when(medicalRecordDAO.update(unknownMedicalRecord)).thenReturn(null);
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
        expectedJson = convertJavaToJson(List.of(medicalRecord1, medicalRecord2));
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

    @Test
    void getMedicalRecord() throws Exception {
        //***********GIVEN*************
        String urlTemplate = String.format("%s%s&%s",
                rootURL,
                medicalRecord1.getFirstName(),
                medicalRecord1.getLastName());
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(medicalRecordDAO, Mockito.times(0)).findByName(
                medicalRecord1.getFirstName(),
                medicalRecord1.getLastName());

        //**************WHEN-THEN****************************
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(containsString("aznol:350mg")))
                .andExpect(jsonPath("$.medications").value(medicalRecord1.getMedications()))
                .andReturn();
        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(medicalRecordDAO, Mockito.times(1)).findByName(
                medicalRecord1.getFirstName(),
                medicalRecord1.getLastName());

        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        //*****************Check with JSON*************************
        String expectedJson = null;
        expectedJson = convertJavaToJson(medicalRecord1);
        JSONAssert.assertEquals(expectedJson, mvcResult.getResponse().getContentAsString(), true);
        //*****************Check with JAVA*************************
        MedicalRecord resultJavaObject = parseResponse(mvcResult, MedicalRecord.class);
        assertThat(medicalRecord1).isEqualToComparingFieldByField(resultJavaObject);
    }

    @Test
    void getUnknownMedicalRecord() throws Exception {
        //***********GIVEN*************
        String urlTemplate = String.format("%s%s&%s",
                rootURL,
                unknownMedicalRecord.getFirstName(),
                unknownMedicalRecord.getLastName());//"grrr&trex"
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate);

        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(medicalRecordDAO, Mockito.times(0)).findByName("grrr","trex");

        //**************WHEN-THEN****************************
        mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isNotFound());

        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(medicalRecordDAO, Mockito.times(1)).findByName("grrr","trex");//.save(any());
    }

    @Test
    void createMedicalRecord() throws Exception {
        //***********GIVEN*************
        String jsonGiven = convertJavaToJson(medicalRecordCreated);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(rootURL)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonGiven)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(medicalRecordDAO, Mockito.times(0)).save(medicalRecordCreated);

        //**************WHEN-THEN****************************
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl("http://localhost/medicalrecord/jack&mortimer"))
                .andReturn();
        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(medicalRecordDAO, Mockito.times(1)).save(ArgumentMatchers.refEq(medicalRecordCreated));//.save(any());
        //https://stackoverflow.com/questions/57690810/how-to-fix-arguments-are-different-wanted-error-in-junit-and-mockito
        //https://www.softwaretestinghelp.com/mockito-matchers/
    }

    @Test
    void createMedicalRecordAlreadyThere() throws Exception {
        //***********GIVEN*************
        String jsonGiven = convertJavaToJson(medicalRecord1);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(rootURL)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonGiven)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(medicalRecordDAO, Mockito.times(0)).save(medicalRecord1);

        //**************WHEN-THEN****************************
        mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isNoContent());

        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(medicalRecordDAO, Mockito.times(1)).save(ArgumentMatchers.refEq(medicalRecord1));//.save(any());
    }

    @Test
    void updateMedicalRecord() throws Exception {
        //***********GIVEN*************
        String jsonGiven = convertJavaToJson(medicalRecordUpdated);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(rootURL)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonGiven)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(medicalRecordDAO, Mockito.times(0)).update(medicalRecordUpdated);

        //**************WHEN-THEN****************************
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(containsString(medicalRecordUpdated.getFirstName())))
                .andExpect(jsonPath("$.lastName").value(medicalRecordUpdated.getLastName()))
                .andExpect(jsonPath("$.birthday").value(convertToString(medicalRecordUpdated.getBirthday(), null)))
                .andExpect(jsonPath("$.medications").value(medicalRecordUpdated.getMedications()))
                .andReturn();
        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(medicalRecordDAO, Mockito.times(1)).update(ArgumentMatchers.refEq(medicalRecordUpdated));//.save(any());

        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        //*****************Check with JSON*************************
        String expectedJson = null;
        expectedJson = convertJavaToJson(medicalRecordUpdated);
        JSONAssert.assertEquals(expectedJson, mvcResult.getResponse().getContentAsString(), true);
        //*****************Check with JAVA*************************
        MedicalRecord resultJavaObject = parseResponse(mvcResult, MedicalRecord.class);
        assertThat(medicalRecordUpdated).isEqualToComparingFieldByField(resultJavaObject);
    }

    @Test
    void updateUnknownPerson() throws Exception {
        //***********GIVEN*************
        String jsonGiven = convertJavaToJson(unknownMedicalRecord);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(rootURL)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonGiven);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(medicalRecordDAO, Mockito.times(0)).update(unknownMedicalRecord);

        //**************WHEN-THEN****************************
        mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isNotFound());

        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(medicalRecordDAO, Mockito.times(1)).update(ArgumentMatchers.refEq(unknownMedicalRecord));//.save(any());
    }

}