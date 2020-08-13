package com.safetynet.alerts.rest;

import com.safetynet.alerts.dao.IMedicalRecordDAO;
import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.dao.PersonDaoImpl;
import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.service.PersonInfoService;
import com.safetynet.alerts.service.rto_models.IPersonInfoRTO;
import com.safetynet.alerts.service.rto_models.PersonInfoRTO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.safetynet.alerts.utils.Jackson.convertJavaToJson;
import static com.safetynet.alerts.utils.JsonConvertForTest.parseResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


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

    public PersonInfoRTO personInfoRTO;

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
    void setUpEach() throws Exception {

    }

    @AfterEach
    void tearDown() {

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
        Person person1 = personDAO.findAll().get(0);
        MedicalRecord medicalRecord1 = medicalRecordDAO.findAll().get(0);
        personInfoRTO = new PersonInfoRTO(person1, medicalRecord1);
        assertNotNull(personInfoRTO,
                "True data loading failed: we need them to go further in Tests");
        String urlTemplate = String.format("%s%s&%s",
                this.baseURL,
                personInfoRTO.getFirstName(),
                personInfoRTO.getLastName());
        //WHEN
        ResponseEntity<PersonInfoRTO> result = template.getForEntity(
                URI.create(urlTemplate),
                PersonInfoRTO.class);

        //**************THEN****************************
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());

        PersonInfoRTO resultJavaObject = result.getBody();
        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        //*****************Check with JAVA*************************
        assertNotNull(resultJavaObject);
        assertThat(personInfoRTO).isEqualToComparingFieldByField(resultJavaObject);
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
        ResponseEntity<PersonInfoRTO> result = template.getForEntity(
                URI.create(urlTemplate),
                PersonInfoRTO.class);

        //**************THEN****************************
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());

        PersonInfoRTO resultJavaObject = result.getBody();
        assertNull(resultJavaObject);
    }

    static Stream<Arguments> nullEmptyNames() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(null, ""),
                Arguments.of("", null));
    }

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
        ResponseEntity<PersonInfoRTO> result = template.getForEntity(
                URI.create(urlTemplate),
                PersonInfoRTO.class);

        //**************THEN****************************
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());

        PersonInfoRTO resultJavaObject = result.getBody();
        assertNull(resultJavaObject);
    }
}


//https://blog.oio.de/2018/10/26/use-null-values-in-junit-5-parameterized-tests/
//https://www.baeldung.com/parameterized-tests-junit-5
//https://openclassrooms.com/fr/courses/6100311-testez-votre-code-java-pour-realiser-des-applications-de-qualite/6441016-simulez-des-composants-externes-aux-tests-avec-mockito