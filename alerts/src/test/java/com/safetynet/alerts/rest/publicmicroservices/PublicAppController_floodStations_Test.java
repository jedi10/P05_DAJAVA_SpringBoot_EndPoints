package com.safetynet.alerts.rest.publicmicroservices;

import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.service.FirestationAreaService;
import com.safetynet.alerts.service.FloodStationsService;
import com.safetynet.alerts.service.rto_models.FirestationAreaRTO;
import com.safetynet.alerts.service.rto_models.IFirestationAreaRTO;
import com.safetynet.alerts.service.rto_models.IPersonInfoRTO;
import com.safetynet.alerts.service.rto_models.PersonInfoRTO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.util.*;
import java.util.stream.Stream;

import static com.safetynet.alerts.utils.Jackson.convertJavaToJson;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PublicAppController_floodStations_Test {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    public PublicAppController publicAppController;

    @Mock
    public FloodStationsService floodStationsService;

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

    static Stream<Arguments> stationData() {
        return Stream.of(
                //All stations exist in data
                Arguments.of(Arrays.asList("2","3", "4")),
                //One station doesn't exist in data
                Arguments.of(Arrays.asList("1", "2", "5"))
        );
    }

    @Order(1)
    @ParameterizedTest
    @MethodSource("stationData")
    void redirectGetFloodStations(List<String> stationList) throws Exception {
        //GIVEN
        String urlTemplate = String.format("%s%s",
                "/flood/stations?",
                "stations="+ stationList);
        //URLEncoder.encode(city, StandardCharsets.UTF_8));
        String expectedUrl = String.format("%s%s",
                "/flood/stations/",
                stationList);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate);
        //WHEN
        mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(expectedUrl));
    }



    @Order(2)
    @ParameterizedTest
    @MethodSource("stationData")
    void getFloodStations_Ok(List<String> stationNumberList) throws Exception {
        //***********GIVEN*************
        medicationList.add("aznol:350mg"); medicationList.add("hydrapermazol:100mg");
        allergiesList.add("nillacilan");
        medicalRecord1.setMedications(medicationList);
        medicalRecord1.setAllergies(allergiesList);

        personInfoRTOList.add(new PersonInfoRTO(person1, medicalRecord1));

        Map<String, List<IPersonInfoRTO>> expectedResult = new HashMap<>();
        expectedResult.put(person1.getAddress(), personInfoRTOList);

        //Mock Configuration
        when(floodStationsService.getFloodStations(stationNumberList)).thenReturn(expectedResult);
        //Mock Injection in Object tested
        publicAppController.floodStationsService = floodStationsService;

        String urlTemplate = String.format("%s%s,%s,%s",
                "/flood/stations/",
                stationNumberList.get(0),
                stationNumberList.get(1),
                stationNumberList.get(2));
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(floodStationsService, Mockito.never()).getFloodStations(stationNumberList);

        //**************WHEN-THEN****************************
        //mockMvc.perform(builder).andDo(print());
       MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(containsString(person1.getEmail())))
                //.andExpect(jsonPath("$.[0].[0].lastName").value(person1.getLastName()))
                .andReturn();
        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(floodStationsService, Mockito.times(1))
                .getFloodStations(stationNumberList);

        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        //*****************Check with JSON*************************
        String expectedJson = null;
        expectedJson = convertJavaToJson(expectedResult);
        String jsonResult = mvcResult.getResponse().getContentAsString();
        JSONAssert.assertEquals(expectedJson, jsonResult, true);
    }

    static Stream<Arguments> stationNoExistData() {
        return Stream.of(
                //All stations listed here don't exist in data.
                Arguments.of(List.of("0", "404"))
        );
    }
    @Order(3)
    @ParameterizedTest
    @MethodSource("stationNoExistData")
    //@ValueSource(strings = { "toto", "" })
    void getFloodStations_NotFound(List<String> stations) throws Exception {
        //***********GIVEN*************
        //Mock Configuration
        when(floodStationsService.getFloodStations(anyList())).thenReturn(null);
        //Mock Injection in Object tested
        publicAppController.floodStationsService = floodStationsService;

        String urlTemplate = String.format("%s%s,%s",
                "/flood/stations/",
                stations.get(0),
                stations.get(1));
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(floodStationsService, Mockito.never()).getFloodStations(stations);

        //**************WHEN-THEN****************************
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isNotFound())
                .andReturn();

        //***********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //***********************************************************
        verify(floodStationsService, Mockito.times(1))
                .getFloodStations(stations);

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