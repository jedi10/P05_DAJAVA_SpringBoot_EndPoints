package com.safetynet.alerts.rest.publicmicroservices;

import com.safetynet.alerts.dao.IFirestationDAO;
import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.models.Firestation;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.service.PhoneAlertService;
import com.safetynet.alerts.utils.Jackson;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.safetynet.alerts.utils.JsonConvertForTest.parseResponse;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PublicAppController_phoneAlert_IT {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    public PublicAppController publicAppController;

    private PhoneAlertService phoneAlertService;

    @Mock
    IPersonDAO personDAO;

    @Mock
    IFirestationDAO firestationDAO;

    private byte[] fileBytes;

    private List<Firestation> firestationList;

    private List<Person> personList;

    @Value("${app.alerts.test-json-file-path}")
    private String testJsonFilePath;

    @BeforeAll()
    void setUp() throws IOException {
        String fileString = Files.readString(Paths.get(testJsonFilePath));
        fileBytes = fileString.getBytes(StandardCharsets.UTF_8);
    }

    @BeforeEach
    void setUpEach() throws IOException {
        try {
            this.personList = Jackson.convertJsonRootDataToJava(
                    fileBytes,
                    "persons",
                    Person.class);
            this.firestationList = Jackson.convertJsonRootDataToJava(
                    fileBytes,
                    "firestations",
                    Firestation.class);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @AfterEach
    void tearDown() {
    }

    @AfterAll
    void tearDownAll(){
        publicAppController = null;
        mockMvc = null;
    }

    @Order(1)
    @Test
    void getPhoneAlert_Ok() throws Exception {
        //**************************GIVEN****************************
        assertNotNull(personList);
        assertTrue(personList.size()>2);
        assertNotNull(firestationList);
        assertTrue(firestationList.size()>2);

        when(firestationDAO.findAll()).thenReturn(firestationList);
        when(personDAO.findAll()).thenReturn(personList);
        //Inject Mocks in tested Object
        phoneAlertService = new PhoneAlertService(personDAO, firestationDAO);
        publicAppController.phoneAlertService = phoneAlertService;

        String stationChosenForTest = firestationList.get(0).getStation();
        assertNotNull(stationChosenForTest,
                "True data loading failed: we need them to go further in Tests");
        List<String> expectedAddressList = firestationList.stream()
                .filter(e-> e.getStation().equalsIgnoreCase(stationChosenForTest))
                .map(Firestation::getAddress)
                .collect(Collectors.toList());

        List<String> expectedPhoneList = personList.stream()
                .filter(e ->  expectedAddressList.contains(e.getAddress()))
                .map(Person::getPhone)
                .collect(Collectors.toList());

        String urlTemplate = String.format("%s%s",
                "/phonealert/",
                stationChosenForTest);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(firestationDAO, Mockito.never()).findAll();
        verify(personDAO, Mockito.never()).findAll();

        //**************************WHEN*****************************
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(containsString(expectedPhoneList.get(0))))
                .andReturn();

        //************************THEN*****************************
        //*********************************************************
        //****************CHECK RESPONSE CONTENT*******************
        //*********************************************************
        //*******************Check with JAVA***********************
        ArrayList<String> resultJavaObject = parseResponse(mvcResult, ArrayList.class);
        assertEquals(expectedPhoneList, resultJavaObject);

        //***********************************************************
        //***************CHECK MOCK INVOCATION at end****************
        //***********************************************************
        verify(firestationDAO, Mockito.times(1)).findAll();
        verify(personDAO, Mockito.times(1)).findAll();
    }

    @Order(2)
    @Test
    void getPhoneAlert_EmptyList() throws Exception {
        //**************************GIVEN****************************
        assertNotNull(personList);
        assertTrue(personList.size()>2);
        assertNotNull(firestationList);
        assertTrue(firestationList.size()>2);

        when(firestationDAO.findAll()).thenReturn(firestationList);
        when(personDAO.findAll()).thenReturn(personList);
        //Inject Mocks in tested Object
        phoneAlertService = new PhoneAlertService(personDAO, firestationDAO);
        publicAppController.phoneAlertService = phoneAlertService;

        String stationChosenForTest = "404";
        List<String> expectedAddressList = firestationList.stream()
                .filter(e-> e.getStation().equalsIgnoreCase(stationChosenForTest))
                .map(Firestation::getAddress)
                .collect(Collectors.toList());
        //Check station choosen is nowhere in data given
        assertTrue(expectedAddressList.isEmpty());
        //Phone list is necessary empty
        List<String> expectedPhoneList = List.of();

        String urlTemplate = String.format("%s%s",
                "/phonealert/",
                stationChosenForTest);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(firestationDAO, Mockito.never()).findAll();
        verify(personDAO, Mockito.never()).findAll();

        //**************************WHEN****************************
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isNotFound())
                .andReturn();

        //************************THEN*****************************
        //*********************************************************
        //****************CHECK RESPONSE CONTENT*******************
        //*********************************************************
        assertTrue(mvcResult.getResponse().getContentAsString().isEmpty());

        //***********************************************************
        //***************CHECK MOCK INVOCATION at end****************
        //***********************************************************
        verify(firestationDAO, Mockito.times(1)).findAll();
        verify(personDAO, Mockito.never()).findAll();
    }
}