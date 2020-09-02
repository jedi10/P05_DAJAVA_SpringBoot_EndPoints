package com.safetynet.alerts.rest.publicmicroservices;

import com.safetynet.alerts.dao.IFirestationDAO;
import com.safetynet.alerts.dao.IMedicalRecordDAO;
import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.models.Firestation;
import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.service.rto_models.FirestationAreaRTO;
import com.safetynet.alerts.service.rto_models.IFirestationAreaRTO;
import com.safetynet.alerts.service.rto_models.IPersonInfoRTO;
import com.safetynet.alerts.service.rto_models.PersonInfoRTO;
import com.safetynet.alerts.utils.Jackson;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriUtils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PublicAppController_firestationArea_E2E {

    @Autowired
    public PublicAppController publicAppController;

    @Autowired
    public IPersonDAO personDAO;
    @Autowired
    public IMedicalRecordDAO medicalRecordDAO;
    @Autowired
    public IFirestationDAO firestationDAO;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate template;

    private HttpHeaders headers;

    private final String rootURL = "/firestationarea/";

    private URL baseURL;

    List<IPersonInfoRTO> personInfoRTOListFull;

    @BeforeAll
    void setUp() throws MalformedURLException {
        //***********GIVEN*************
        this.baseURL = new URL("http://localhost:" + port + rootURL);
    }

    @BeforeEach
    void setUpEach() {

    }

    @AfterEach
    void tearDown() {

    }

    @AfterAll
    void tearDownAll(){
        publicAppController = null;
    }


    @Order(1)
    @ParameterizedTest
    @CsvSource({"3","4"})
    void getFirestationArea_Ok(String firestationNumber) throws Exception {
        //***********GIVEN*************
        List<Person> personList = personDAO.findAll();
        List<MedicalRecord> medicalRecordList = medicalRecordDAO.findAll();
        List<Firestation> firestationList = firestationDAO.findAll();
        assertNotNull(personList,
                "PersonList is Null: we need it for further tests");
        assertTrue(personList.size()>2);
        assertNotNull(medicalRecordList,
                "MedicalRecordList is Null: we need it for further tests");
        assertTrue(medicalRecordList.size()>2);
        assertNotNull(firestationList,
                "FirestationList is Null: we need it for further tests");
        assertTrue(firestationList.size()>2);

        personInfoRTOListFull =  PersonInfoRTO.buildPersonInfoRTOList(personList, medicalRecordList);

        //*****************Get Address List linked to station number******************
        List<String> listAddressResult = firestationList.stream()
                .filter(e-> e.getStation().equalsIgnoreCase(firestationNumber))
                .map(Firestation::getAddress)
                .collect(Collectors.toList());
        //Filtering list
        List<IPersonInfoRTO> expectedPersonRTOList = personInfoRTOListFull.stream()
                .filter(o ->
                        listAddressResult.contains(o.getAddress())
                )
                .collect(Collectors.toList());

        IFirestationAreaRTO expectedFirestationAreaRTO = new FirestationAreaRTO(expectedPersonRTOList);

        String urlTemplate = String.format("%s%s",
                this.baseURL,
                UriUtils.encode(
                        firestationNumber,
                        StandardCharsets.UTF_8));
        //***********************WHEN****************************
        ResponseEntity<Object> result = template.getForEntity(
                URI.create(urlTemplate),
                Object.class);

        //***********************THEN****************************
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());

        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        assertNotNull(result.getBody());
        //*******************WITH JSON*****************************
        //*********************************************************
        String expectedFirestationAreaRTOString = Jackson.convertJavaToJson(expectedFirestationAreaRTO);
        String resultJavaObjectString = Jackson.convertJavaToJson(result.getBody());
        assertNotNull(resultJavaObjectString);

        JSONAssert.assertEquals(expectedFirestationAreaRTOString, resultJavaObjectString, true);
    }

    @Order(2)
    @ParameterizedTest
    @CsvSource({"0"})
    void getFirestationArea_NotFound(String station) {
        //***********GIVEN*************
        String urlTemplate = String.format("%s%s",
                this.baseURL,
                UriUtils.encode(
                        station,
                        StandardCharsets.UTF_8));
        //WHEN
        ResponseEntity<Object> result = template.getForEntity(
                URI.create(urlTemplate),
                Object.class);

        //**************THEN****************************
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());

        Object resultJavaObject = result.getBody();
        assertNull(resultJavaObject);
    }
}


//https://blog.oio.de/2018/10/26/use-null-values-in-junit-5-parameterized-tests/
//https://www.baeldung.com/parameterized-tests-junit-5
//https://openclassrooms.com/fr/courses/6100311-testez-votre-code-java-pour-realiser-des-applications-de-qualite/6441016-simulez-des-composants-externes-aux-tests-avec-mockito