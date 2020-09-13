package com.safetynet.alerts.rest.publicmicroservices;

import com.safetynet.alerts.dao.IFirestationDAO;
import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.models.Firestation;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.service.PhoneAlertService;
import com.safetynet.alerts.service.rto_models.PersonInfoRTO;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.safetynet.alerts.utils.JsonConvertForTest.parseResponse;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PublicAppController_phoneAlert_E2E {

    @Autowired
    public PublicAppController publicAppController;

    @Autowired
    private PhoneAlertService phoneAlertService;

    @Autowired
    IPersonDAO personDAO;

    @Autowired
    IFirestationDAO firestationDAO;

    @Value("${app.alerts.test-server-url-without-port}")
    private String testServerUrlWithoutPort;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate template;

    private final String rootURL = "/phonealert/";

    private URL baseURL;

    private List<Firestation> firestationList;

    private List<Person> personList;

    @BeforeAll()
    void setUp() throws IOException {
        //***********GIVEN*************
        this.baseURL = new URL(this.testServerUrlWithoutPort + port + rootURL);
    }

    @BeforeEach
    void setUpEach() {
        firestationList = firestationDAO.findAll();
        personList = personDAO.findAll();
    }

    @AfterEach
    void tearDown() {
    }

    @AfterAll
    void tearDownAll(){
        publicAppController = null;
    }

    @Order(1)
    @Test
    void getPhoneAlert_Ok() {
        //**************************GIVEN****************************
        assertNotNull(personList);
        assertTrue(personList.size()>2);
        assertNotNull(firestationList);
        assertTrue(firestationList.size()>2);

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
                this.baseURL,
                stationChosenForTest);

        //**************************WHEN*****************************
        ResponseEntity<List> phoneListResult = template.getForEntity(
                URI.create(urlTemplate),
                List.class);

        //************************THEN*****************************
        assertNotNull(phoneListResult);
        assertEquals(HttpStatus.OK, phoneListResult.getStatusCode());

        List<String> resultJavaObject = phoneListResult.getBody();
        assertNotNull(resultJavaObject);
        //*********************************************************
        //****************CHECK RESPONSE CONTENT*******************
        //*********************************************************
        //*******************Check with JAVA***********************
        assertEquals(expectedPhoneList, resultJavaObject);
    }

    @Order(2)
    @Test
    void getPhoneAlert_EmptyList() throws Exception {
        //**************************GIVEN****************************
        assertNotNull(personList);
        assertTrue(personList.size()>2);
        assertNotNull(firestationList);
        assertTrue(firestationList.size()>2);

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
                this.baseURL,
                stationChosenForTest);

        //**************************WHEN****************************
        ResponseEntity<List> phoneListResult = template.getForEntity(
                URI.create(urlTemplate),
                List.class);

        //************************THEN*****************************
        //*********************************************************
        //****************CHECK RESPONSE CONTENT*******************
        //*********************************************************
        assertNotNull(phoneListResult);
        assertEquals(HttpStatus.NOT_FOUND, phoneListResult.getStatusCode());

        List resultJavaObject = phoneListResult.getBody();
        assertNull(resultJavaObject);
    }
}