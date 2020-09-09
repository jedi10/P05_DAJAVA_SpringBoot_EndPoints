package com.safetynet.alerts.rest.publicmicroservices;

import com.safetynet.alerts.dao.IMedicalRecordDAO;
import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.service.rto_models.IPersonInfoRTO;
import com.safetynet.alerts.service.rto_models.PersonInfoRTO;
import com.safetynet.alerts.utils.Jackson;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PublicAppController_personInfo_E2E {

    @Autowired
    public PublicAppController publicAppController;

    @Autowired
    public IPersonDAO personDAO;
    @Autowired
    public IMedicalRecordDAO medicalRecordDAO;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate template;

    private HttpHeaders headers;

    private final String rootURL = "/personinfo/";

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
    void redirectGetPerson() throws Exception {
        //***********GIVEN*************
        Person person1 = personDAO.findAll().get(0);
        assertNotNull(person1,
                "True data loading failed: we need them to go further in Tests");
        String urlTemplate = String.format("%s%s&%s",
                new URL("http://localhost:" + port + "/admin/personinfo/"),
                person1.getFirstName(),
                person1.getLastName());
        //***********WHEN*************
        ResponseEntity<Person> result = template.getForEntity(
                URI.create(urlTemplate),
                Person.class);
        //**************THEN****************************
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());

        Person resultJavaObject = result.getBody();
        assertNotNull(resultJavaObject);
        assertThat(person1).isEqualToComparingFieldByField(resultJavaObject);
    }

    @Order(2)
    @Test
    void getPersonInfo_Ok() throws Exception {
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
        List<IPersonInfoRTO> expectedPersonRTOList = personInfoRTOListFull.stream()
                .filter(o -> personChosenForTest.getLastName().equals(o.getLastName()))
                .collect(Collectors.toList());
        IPersonInfoRTO expectedChosenPersonRTO = expectedPersonRTOList.stream()
                .filter(e -> e.getFirstName().equalsIgnoreCase(personChosenForTest.getFirstName())&&
                        e.getLastName().equalsIgnoreCase(personChosenForTest.getLastName()))
                .findAny()
                .orElse(null);
        //https://www.baeldung.com/find-list-element-java

        assertNotNull(expectedChosenPersonRTO,
                "True data loading failed: we need them to go further in Tests");
        String urlTemplate = String.format("%s%s&%s",
                this.baseURL,
                personChosenForTest.getFirstName(),
                personChosenForTest.getLastName());
        //WHEN
        ResponseEntity<List> result = template.getForEntity(
                URI.create(urlTemplate),
                List.class);

        //**************THEN****************************
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());

        List<PersonInfoRTO> resultJavaObject = result.getBody();
        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        assertNotNull(resultJavaObject);
        assertNotNull(resultJavaObject.get(0));
        //*****************Check with Json*************************
        //Check selected Object- perfect match on first and last name
        String expectedChosenPersonRTOString = Jackson.convertJavaToJson(expectedChosenPersonRTO);
        String resultStringObject = Jackson.convertJavaToJson(resultJavaObject.get(0));
        assertEquals(expectedChosenPersonRTOString, resultStringObject);
        JSONAssert.assertEquals(expectedChosenPersonRTOString, resultStringObject, true);
        //Check all list- perfect match on last name
        String expectedPersonInfoRTOListString = Jackson.convertJavaToJson(expectedPersonRTOList);
        String resultStringList = Jackson.convertJavaToJson(resultJavaObject);
        JSONAssert.assertEquals(expectedPersonInfoRTOListString, resultStringList, true);
    }

    @Order(3)
    @ParameterizedTest
    @CsvSource({"toto,riri","toto,''", "'',''"})
    void getPersonInfo_NotFound(String firstName, String lastName) {
        //***********GIVEN*************
        String urlTemplate = String.format("%s%s&%s",
                 this.baseURL,
                 firstName,
                 lastName);
        //WHEN
        ResponseEntity<List> result = template.getForEntity(
                URI.create(urlTemplate),
                List.class);

        //**************THEN****************************
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());

        List<PersonInfoRTO> resultJavaObject = result.getBody();
        assertNull(resultJavaObject);
    }

    /*
    static Stream<Arguments> nullEmptyNames() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(null, ""),
                Arguments.of("", null));
    }

    @Disabled//Useless Test
    @Order(4)
    @ParameterizedTest
    @MethodSource("nullEmptyNames")
    void getPersonInfo_NullCase(String firstName, String lastName) throws Exception {
        //***********GIVEN*************
        String urlTemplate = String.format("%s%s&%s",
                this.baseURL,
                firstName,
                lastName);
        //WHEN
        ResponseEntity<List> result = template.getForEntity(
                URI.create(urlTemplate),
                List.class);

        //**************THEN****************************
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());

        List<PersonInfoRTO> resultJavaObject = result.getBody();
        assertNull(resultJavaObject);
    }*/
}


//https://blog.oio.de/2018/10/26/use-null-values-in-junit-5-parameterized-tests/
//https://www.baeldung.com/parameterized-tests-junit-5
//https://openclassrooms.com/fr/courses/6100311-testez-votre-code-java-pour-realiser-des-applications-de-qualite/6441016-simulez-des-composants-externes-aux-tests-avec-mockito