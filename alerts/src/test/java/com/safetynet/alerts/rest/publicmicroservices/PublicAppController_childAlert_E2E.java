package com.safetynet.alerts.rest.publicmicroservices;

import com.safetynet.alerts.dao.IFirestationDAO;
import com.safetynet.alerts.dao.IMedicalRecordDAO;
import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.models.Person;
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
class PublicAppController_childAlert_E2E {

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

    private final String rootURL = "/childalert/";

    private URL baseURL;

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
    @Test
    void getChildAlert_Ok() throws Exception {
        //***********GIVEN*************
        List<Person> personList = personDAO.findAll();
        List<MedicalRecord> medicalRecordList = medicalRecordDAO.findAll();
        assertNotNull(personList,
                "PersonList is Null: we need it for further tests");
        assertTrue(personList.size()>2);
        assertNotNull(medicalRecordList,
                "MedicalRecordList is Null: we need it for further tests");
        assertTrue(medicalRecordList.size()>2);

        List<IPersonInfoRTO> personInfoRTOListFull =  PersonInfoRTO.buildPersonInfoRTOList(personList, medicalRecordList);

        //we choose first element on list to get the name for test
        Person personChosenForTest = personList.get(0);

        //Filtering list
        List<IPersonInfoRTO> expectedChildRTOList = personInfoRTOListFull.stream()
                .filter(o ->
                        personChosenForTest.getAddress().equalsIgnoreCase(o.getAddress()) &&
                                o.getHumanCategory().equals(IPersonInfoRTO.HumanCategory.CHILDREN)
                )
                .collect(Collectors.toList());
        assertFalse(expectedChildRTOList.isEmpty());

        List<IPersonInfoRTO> expectedAdultRTOList = personInfoRTOListFull.stream()
                .filter(o ->
                        personChosenForTest.getAddress().equalsIgnoreCase(o.getAddress()) &&
                                o.getHumanCategory().equals(IPersonInfoRTO.HumanCategory.ADULTS)
                )
                .collect(Collectors.toList());
        assertFalse(expectedAdultRTOList.isEmpty());

        String urlTemplate = String.format("%s%s",
                this.baseURL,
                UriUtils.encode(
                        personChosenForTest.getAddress(),
                        StandardCharsets.UTF_8));
        //***********************WHEN****************************
        ResponseEntity<Map> result = template.getForEntity(
                URI.create(urlTemplate),
                Map.class);

        //***********************THEN****************************
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());

        Map<IPersonInfoRTO.HumanCategory, List<IPersonInfoRTO>> resultJavaObject = result.getBody();
        assertNotNull(resultJavaObject);
        assertNotNull(resultJavaObject.get(IPersonInfoRTO.HumanCategory.CHILDREN.toString()));
        assertNotNull(resultJavaObject.get(IPersonInfoRTO.HumanCategory.ADULTS.toString()));
        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        //*******************WITH JSON*****************************
        //*********************************************************
        //****************for Children List************************
        //*********************************************************
        List<IPersonInfoRTO> childRTOListResult = resultJavaObject.get(IPersonInfoRTO.HumanCategory.CHILDREN.toString());
        String childRTOListResultString = Jackson.convertJavaToJson(childRTOListResult);
        String expectedChildRTOListString = Jackson.convertJavaToJson(expectedChildRTOList);

        JSONAssert.assertEquals(expectedChildRTOListString, childRTOListResultString, true);

        //*********************************************************
        //****************for Adult List************************
        //*********************************************************
        List<IPersonInfoRTO> adultRTOListResult = resultJavaObject.get(IPersonInfoRTO.HumanCategory.ADULTS.toString());
        String adultRTOListResultString = Jackson.convertJavaToJson(adultRTOListResult);
        String expectedAdultRTOListString = Jackson.convertJavaToJson(expectedAdultRTOList);

        JSONAssert.assertEquals(expectedAdultRTOListString, adultRTOListResultString, true);

    }

    @Order(2)
    @ParameterizedTest
    @CsvSource({"10 trafalgar square","X"})
    void getChildAlert_NotFound(String address) {
        //***********GIVEN*************
        String urlTemplate = String.format("%s%s",
                 this.baseURL,
                UriUtils.encode(
                        address,
                        StandardCharsets.UTF_8));
        //WHEN
        ResponseEntity<Map> result = template.getForEntity(
                URI.create(urlTemplate),
                Map.class);

        //**************THEN****************************
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());

        Map<IPersonInfoRTO.HumanCategory, List<IPersonInfoRTO>> resultJavaObject = result.getBody();
        assertNull(resultJavaObject);
    }

}


//https://blog.oio.de/2018/10/26/use-null-values-in-junit-5-parameterized-tests/
//https://www.baeldung.com/parameterized-tests-junit-5
//https://openclassrooms.com/fr/courses/6100311-testez-votre-code-java-pour-realiser-des-applications-de-qualite/6441016-simulez-des-composants-externes-aux-tests-avec-mockito