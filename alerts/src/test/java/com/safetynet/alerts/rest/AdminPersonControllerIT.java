package com.safetynet.alerts.rest;

import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.utils.Jackson;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.safetynet.alerts.utils.Jackson.convertJavaToJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminPersonControllerIT {

    @Autowired
    private AdminPersonController adminPersonController;

    @Autowired
    private IPersonDAO personDAO;

    //@Autowired
    //private RootFile rootFile;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate template;

    private HttpHeaders headers;

    private final String rootURL = "/person/";

    private URL baseURL;

    private Person person1;
    private Person person2;
    private Person personCreated = new Person("jack", "mortimer", "rue du stade", "Rome", 45, "06-25-50-90-12", "mortimer@mail.it");
    private Person personUpdated = new Person("jack", "mortimer", "rue du colisee", "Rome", 45, "06-25-23-99-00", "mortimer@mail.it");

    @BeforeAll
    void setUp() throws MalformedURLException {
        //***********GIVEN*************
        this.baseURL = new URL("http://localhost:" + port + rootURL);
        this.headers = new HttpHeaders();
        this.headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @BeforeEach
    void setUpEach() throws IOException {
        //***********GIVEN*************
        //personDAO = new PersonDaoImpl(rootFile);
        this.adminPersonController.personDAO = personDAO;
    }

    @AfterEach
    void tearDown() {
    }

    @Order(1)
    @Test
    void getAllPersons() throws Exception {
        //***********WHEN*************
        ResponseEntity<String> result = template.getForEntity(
                this.baseURL.toURI(),
                String.class);

        //**************THEN***************
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());//200

        String content = result.getBody();
        assertNotNull(content);
        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        //****************Check with java**************************
        List<Person> personList = Jackson.convertJsonListToJava(
                content.getBytes(StandardCharsets.UTF_8),
                Person.class);
        assertNotNull(personList);
        assertTrue(personList.size() > 1);
        assertNotNull(personList.get(0));
        assertNotNull(personList.get(1));

        //********GIVEN For following Test********
        person1 = personList.get(0);
        person2 = personList.get(1);
        //THEN
        assertNotEquals(person1, person2);
    }

    @Order(2)
    @Test
    void getPerson() throws IOException {
        //***********GIVEN*************
        String urlTemplate = String.format("%s%s&%s",
                this.baseURL.getPath(),
                person1.getFirstName(),
                person1.getLastName());
        //WHEN
        ResponseEntity<String> result = template.getForEntity(
                urlTemplate,
                String.class);
        //THEN
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());//200

        String content = result.getBody();
        assertNotNull(content);
        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        //*****************Check with JAVA*************************
        Person resultJavaObject = Jackson.convertJsonToJava(
                content.getBytes(StandardCharsets.UTF_8),
                Person.class);
        assertNotNull(resultJavaObject);
        assertThat(person1).isEqualToComparingFieldByField(resultJavaObject);
    }

    @Order(3)
    @Test
    void createPerson() throws Exception {
        //****************GIVEN*******************
        String jsonGiven = convertJavaToJson(personCreated);
        String urlDestination = String.format("%s&%s",
                UriUtils.encode(
                        personCreated.getFirstName(), StandardCharsets.UTF_8),
                UriUtils.encode(
                        personCreated.getLastName(), StandardCharsets.UTF_8));
        HttpEntity<String> request = new HttpEntity<String>(jsonGiven, headers);

        //****************WHEN********************
        URI locationHeader = template.postForLocation(
                this.baseURL.toURI(),
                request);

        //****************THEN********************
        assertNotNull(locationHeader);
        //assertEquals(HttpStatus.CREATED.value(), request.);//201//already tested in unit test.
        //*********************************************************
        //**************CHECK LOCATION RETURNED********************
        //*********************************************************
        assertTrue(locationHeader.getPath().contains(urlDestination));
        //*********************************************************
        //**************CHECK OBJECT created***********************
        //*********************************************************
        ResponseEntity<String> response = template.getForEntity(
                locationHeader.getPath(),
                String.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        String content = response.getBody();
        assertNotNull(content);
        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        //*****************Check with JAVA*************************
        Person resultJavaObject = Jackson.convertJsonToJava(
                content.getBytes(StandardCharsets.UTF_8),
                Person.class);
        assertNotNull(resultJavaObject);
        assertThat(personCreated).isEqualToComparingFieldByField(resultJavaObject);
        //https://www.baeldung.com/rest-template
        //https://www.baeldung.com/spring-resttemplate-post-json
        //https://stackoverflow.com/questions/52364187/resttemplate-exchange-vs-postforentity-vs-execute
    }

    @Order(4)
    @Test
    void updatePerson() throws Exception {
        //*********************GIVEN*************************
        String jsonGiven = convertJavaToJson(personUpdated);
        String urlUpdated = String.format("%s%s&%s",
                this.baseURL.getPath(),
                UriUtils.encode(
                        personUpdated.getFirstName(), StandardCharsets.UTF_8),
                UriUtils.encode(
                        personUpdated.getLastName(), StandardCharsets.UTF_8));
        HttpEntity<String> request = new HttpEntity<String>(jsonGiven, headers);
        //********************WHEN****************************
        ResponseEntity<Person> response = template.exchange(
                this.baseURL.toURI(),
                HttpMethod.PUT,
                request,
                Person.class);

        //********************THEN****************************
        assertEquals(HttpStatus.OK, response.getStatusCode());

        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        //*****************Check with JAVA*************************
        Person resultJavaObject = response.getBody();
        assertNotNull(resultJavaObject);
        assertThat(personUpdated).isEqualToComparingFieldByField(resultJavaObject);

        //*********************************************************
        //**************CHECK OBJECT updated***********************
        //*********************************************************
        ResponseEntity<Person> response2 = template.getForEntity(
                urlUpdated,
                Person.class);
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        Person resultJavaObject2 = response2.getBody();
        assertNotNull(resultJavaObject2);
        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        //*****************Check with JAVA*************************
        assertThat(personUpdated).isEqualToComparingFieldByField(resultJavaObject2);
        assertEquals(personUpdated.getAddress(), resultJavaObject2.getAddress());
        assertNotEquals(personCreated.getAddress(), resultJavaObject2.getAddress());

        //https://www.baeldung.com/rest-template
        //https://o7planning.org/fr/11647/exemple-spring-boot-restful-client-avec-resttemplate#a13901576
    }

    @Order(5)
    @Test
    void deletePerson() throws Exception {
        String urlTemplate = String.format("%s%s&%s",
                this.baseURL.getPath(),
                personUpdated.getFirstName(),
                personUpdated.getLastName());
        URI uriFull = URI.create(urlTemplate);
        HttpEntity<Person> request = new HttpEntity<>(headers);
        //WHEN
        ResponseEntity<Person> response = template.exchange(
                uriFull,
                HttpMethod.DELETE,
                request,
                Person.class);

        //THEN
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Person resultJavaObject = response.getBody();
        assertNull(resultJavaObject);

        //**************CHECK Deleted Object**************
        ResponseEntity<Person> response2 = template.getForEntity(
                urlTemplate,
                Person.class);
        assertNotNull(response2);
        assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
        Person resultJavaObject2 = response2.getBody();
        assertNull(resultJavaObject2);
    }
}



//https://stackoverflow.com/questions/18336277/how-to-check-string-in-response-body-with-mockmvc
//https://www.logicbig.com/tutorials/spring-framework/spring-web-mvc/json-message-object-conversion.html