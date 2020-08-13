package com.safetynet.alerts.rest;

import com.safetynet.alerts.models.Firestation;
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
import java.util.Arrays;
import java.util.List;

import static com.safetynet.alerts.utils.Jackson.convertJavaToJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminFirestationControllerIT {

    @Autowired
    private AdminFirestationController adminFirestationController;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate template;

    private HttpHeaders headers;

    private final String rootURL = "/firestation/";

    private URL baseURL;

    private Firestation firestation1;
    private Firestation firestation2;
    private Firestation firestationCreated = new Firestation("210 Jump Street", "3");
    private Firestation firestationUpdated = new Firestation("210 Jump Street", "5");

    @BeforeAll
    void setUp() throws MalformedURLException {
        //***********GIVEN*************
        this.baseURL = new URL("http://localhost:" + port + rootURL);
        this.headers = new HttpHeaders();
        this.headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @BeforeEach
    void setUpEach() {
    }

    @AfterEach
    void tearDown() {
    }

    @Order(1)
    @Test
    void getAllFirestations() {
        //***********WHEN*************
        ResponseEntity<Firestation[]> result = template.getForEntity(
                this.baseURL.getPath(),
                Firestation[].class);

        //**************THEN***************
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());//200
        assertNotNull(result.getBody());

        List<Firestation> firestationList = Arrays.asList(result.getBody());
        assertNotNull(firestationList);
        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        //****************Check with java**************************
        assertNotNull(firestationList);
        assertTrue(firestationList.size() > 1);
        assertNotNull(firestationList.get(0));
        assertNotNull(firestationList.get(1));

        //********GIVEN For following Test********
        firestation1 = firestationList.get(0);
        firestation2 = firestationList.get(1);
        //THEN
        assertNotEquals(firestation1, firestation2);
    }

    @Order(2)
    @Test
    void getFirestation() {
        //***********GIVEN*************
        String urlTemplate = String.format("%s%s",
                this.baseURL.getPath(),
                firestation1.getAddress());
        //WHEN
        ResponseEntity<Firestation> result = template.getForEntity(
                urlTemplate, Firestation.class);
        //THEN
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());//200

        Firestation resultJavaObject = result.getBody();
        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        //*****************Check with JAVA*************************
        assertNotNull(resultJavaObject);
        assertThat(firestation1).isEqualToComparingFieldByField(resultJavaObject);
    }

    @Order(3)
    @Test
    void createFirestation() {
        //****************GIVEN*******************
        String jsonGiven = convertJavaToJson(firestationCreated);
        String urlDestination = String.format("%s",
                UriUtils.encode(
                        firestationCreated.getAddress(), StandardCharsets.UTF_8));
        HttpEntity<String> request = new HttpEntity<String>(jsonGiven, headers);

        //****************WHEN********************
        URI locationHeader = template.postForLocation(
                this.baseURL.getPath(),
                request);

        //****************THEN********************
        assertNotNull(locationHeader);
        //assertEquals(HttpStatus.CREATED.value(), request.);//201//already tested in unit test.
        //*********************************************************
        //**************CHECK LOCATION RETURNED********************
        //*********************************************************
        String urlReturned = locationHeader.getPath();
        assertTrue(urlReturned.contains(
                UriUtils.decode(urlDestination, StandardCharsets.UTF_8)));
        //*********************************************************
        //**************CHECK OBJECT created***********************
        //*********************************************************
        ResponseEntity<Firestation> response = template.getForEntity(
                locationHeader.getPath(),
                Firestation.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        Firestation resultJavaObject = response.getBody();
        assertNotNull(resultJavaObject);
        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        //*****************Check with JAVA*************************
        assertNotNull(resultJavaObject);
        assertThat(firestationCreated).isEqualToComparingFieldByField(resultJavaObject);
        //https://www.baeldung.com/rest-template
        //https://www.baeldung.com/spring-resttemplate-post-json
        //https://stackoverflow.com/questions/52364187/resttemplate-exchange-vs-postforentity-vs-execute
    }

    @Order(4)
    @Test
    void updateFirestation() throws Exception {
        //*********************GIVEN*************************
        String jsonGiven = convertJavaToJson(firestationUpdated);
        String urlUpdated = String.format("%s%s",
                this.baseURL.getPath(),
                firestationUpdated.getAddress());
        HttpEntity<String> request = new HttpEntity<String>(jsonGiven, headers);
        //********************WHEN****************************
        ResponseEntity<Firestation> response = template.exchange(
                this.baseURL.toURI(),
                HttpMethod.PUT,
                request,
                Firestation.class);

        //********************THEN****************************
        assertEquals(HttpStatus.OK, response.getStatusCode());

        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        //*****************Check with JAVA*************************
        Firestation resultJavaObject = response.getBody();
        assertNotNull(resultJavaObject);
        assertThat(firestationUpdated).isEqualToComparingFieldByField(resultJavaObject);

        //*********************************************************
        //**************CHECK OBJECT updated***********************
        //*********************************************************
        ResponseEntity<Firestation> response2 = template.getForEntity(
                urlUpdated,
                Firestation.class);
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        Firestation resultJavaObject2 = response2.getBody();
        assertNotNull(resultJavaObject2);
        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        //*****************Check with JAVA*************************
        assertThat(firestationUpdated).isEqualToComparingFieldByField(resultJavaObject2);
        assertEquals(firestationUpdated.getStation(), resultJavaObject2.getStation());
        assertNotEquals(firestationCreated.getStation(), resultJavaObject2.getStation());

        //https://www.baeldung.com/rest-template
        //https://o7planning.org/fr/11647/exemple-spring-boot-restful-client-avec-resttemplate#a13901576
    }

    @Order(5)
    @Test
    void deleteFirestation() {
        String urlTemplate = String.format("%s%s",
                this.baseURL.getPath(),
                UriUtils.encode(
                        firestationUpdated.getAddress(), StandardCharsets.UTF_8));
        URI uriFull = URI.create(urlTemplate);
        HttpEntity<Firestation> request = new HttpEntity<>(headers);
        //WHEN
        ResponseEntity<Firestation> response = template.exchange(
                uriFull,
                HttpMethod.DELETE,
                request,
                Firestation.class);
        //THEN
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Firestation resultJavaObject = response.getBody();
        assertNull(resultJavaObject);

        //**************CHECK Deleted Object**************
        ResponseEntity<Firestation> response2 = template.getForEntity(
                urlTemplate,
                Firestation.class);
        assertNotNull(response2);
        assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
        Firestation resultJavaObject2 = response2.getBody();
        assertNull(resultJavaObject2);
    }
}



//https://stackoverflow.com/questions/18336277/how-to-check-string-in-response-body-with-mockmvc
//https://www.logicbig.com/tutorials/spring-framework/spring-web-mvc/json-message-object-conversion.html