package com.safetynet.alerts.rest.publicmicroservices;

import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.service.FirestationAreaService;
import com.safetynet.alerts.service.rto_models.FirestationAreaRTO;
import com.safetynet.alerts.service.rto_models.IFirestationAreaRTO;
import com.safetynet.alerts.service.rto_models.IPersonInfoRTO;
import com.safetynet.alerts.service.rto_models.PersonInfoRTO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.safetynet.alerts.utils.Jackson.convertJavaToJson;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PublicAppController_firestationArea_Test {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    public PublicAppController publicAppController;

    @Mock
    public FirestationAreaService firestationAreaService;

    @Spy
    public List<IPersonInfoRTO> personInfoRTOList = new ArrayList<>();

    private Person person1 = new Person(
            "junior", "boyd", "rue du colisee", "Rome", 45, "06-12-23-34-45", "wermer@mail.it");

    private List<String> medicationList = new ArrayList<>();

    private List<String> allergiesList = new ArrayList<>();

    private MedicalRecord medicalRecord1 = new MedicalRecord("junior", "boyd",
            LocalDate.of(2015, 3, 6));

    @BeforeEach
    void setUp() throws Exception {

    }

    @AfterEach
    void tearDown() {
        personInfoRTOList.clear();
    }

    @AfterAll
    void tearDownAll(){
        publicAppController = null;
        mockMvc = null;
    }

    @Order(1)
    @ParameterizedTest
    @CsvSource({"3"})
    void redirectGetFireStationArea(String station) throws Exception {
        //GIVEN
        String urlTemplate = String.format("%s%s",
                "/firestation?",
                "stationNumber="+ UriUtils.encode(station, StandardCharsets.UTF_8));
        //URLEncoder.encode(city, StandardCharsets.UTF_8));
        String expectedUrl = String.format("%s%s",
                "/firestationarea/",
                UriUtils.encode(station, StandardCharsets.UTF_8));
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate);
        //WHEN
        mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(expectedUrl));
    }

    @Order(2)
    @Test
    void getFireStationArea_Ok() throws Exception {
        //***********GIVEN*************
        medicationList.add("aznol:350mg"); medicationList.add("hydrapermazol:100mg");
        allergiesList.add("nillacilan");
        medicalRecord1.setMedications(medicationList);
        medicalRecord1.setAllergies(allergiesList);

        personInfoRTOList.add(new PersonInfoRTO(person1, medicalRecord1));

        IFirestationAreaRTO firestationAreaRTO = new FirestationAreaRTO(personInfoRTOList);

        when(firestationAreaService.getFirestationArea("3")).
                thenReturn(firestationAreaRTO);
        //Mock Injection in Object tested
        publicAppController.firestationAreaService = firestationAreaService;

        String urlTemplate = String.format("%s%s",
                "/firestationarea/",
                "3");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(firestationAreaService, Mockito.never()).getFirestationArea("3");

        //**************WHEN-THEN****************************
       MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(containsString(person1.getEmail())))
                //.andExpect(jsonPath("$.[1].[0].ADULTS").value(1))
                .andReturn();
        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(firestationAreaService, Mockito.times(1)).getFirestationArea("3");

        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        //*****************Check with JSON*************************
        String expectedJson = null;
        expectedJson = convertJavaToJson(firestationAreaRTO);
        String jsonResult = mvcResult.getResponse().getContentAsString();
        JSONAssert.assertEquals(expectedJson, jsonResult, true);
    }

    @Order(3)
    @ParameterizedTest
    @CsvSource({"0"})
    //@ValueSource(strings = { "toto", "" })
    void getFirestationArea_NotFound(String station) throws Exception {
        //***********GIVEN*************
        IFirestationAreaRTO firestationAreaRTO = null;

        when(firestationAreaService.getFirestationArea(station)).
                thenReturn(firestationAreaRTO);
        //Mock Injection in Object tested
        publicAppController.firestationAreaService = firestationAreaService;

        String urlTemplate = String.format("%s%s",
                "/firestationarea/",
                station);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(firestationAreaService, Mockito.never())
                .getFirestationArea(station);

        //**************WHEN-THEN****************************
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isNotFound())
                .andReturn();

        //***********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //***********************************************************
        verify(firestationAreaService, Mockito.times(1))
                .getFirestationArea(station);

        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        assertNotNull(mvcResult);
        assertNull(mvcResult.getResponse().getContentType());
    }

}


//https://blog.oio.de/2018/10/26/use-null-values-in-junit-5-parameterized-tests/
//https://www.baeldung.com/parameterized-tests-junit-5
//https://openclassrooms.com/fr/courses/6100311-testez-votre-code-java-pour-realiser-des-applications-de-qualite/6441016-simulez-des-composants-externes-aux-tests-avec-mockito