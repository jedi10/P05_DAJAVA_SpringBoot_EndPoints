package com.safetynet.alerts.rest;

import com.safetynet.alerts.models.MedicalRecord;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.util.UriUtils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.safetynet.alerts.utils.Jackson.convertJavaToJson;
import static com.safetynet.alerts.utils.Jackson.deepCopy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminMedicalRecordControllerIT {

    @Autowired
    private AdminMedicalRecordController adminMedicalRecordController;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate template;

    private HttpHeaders headers;

    private final String rootURL = "/medicalrecord/";

    private URL baseURL;

    private List<String> medicationList = new ArrayList<>();

    private List<String> allergiesList = new ArrayList<>();

    private MedicalRecord medicalRecord1 = new MedicalRecord("john", "boyd",
            LocalDate.of(1984, 3, 6));

    private MedicalRecord medicalRecord2 = new MedicalRecord("jacob", "boyd",
            LocalDate.of(1989, 3, 6));

    private MedicalRecord medicalRecordCreated = new MedicalRecord("jack", "mortimer",
            LocalDate.of(1961, 1, 25));

    private MedicalRecord medicalRecordUpdated = deepCopy(medicalRecordCreated, MedicalRecord.class);
    @BeforeAll
    void setUp() throws MalformedURLException {
        //***********GIVEN*************
        this.baseURL = new URL("http://localhost:" + port + rootURL);
        this.headers = new HttpHeaders();
        this.headers.setContentType(MediaType.APPLICATION_JSON);

        medicalRecordUpdated.getAllergies().add("penicilium");
    }

    @BeforeEach
    void setUpEach() {
    }

    @AfterEach
    void tearDown() {
    }

    @Order(1)
    @Test
    void getAllMedicalRecord() throws Exception {
        //***********WHEN*************
        ResponseEntity<MedicalRecord[]> result = template.getForEntity(
                this.baseURL.getPath(),
                MedicalRecord[].class);

        //**************THEN***************
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());//200

        List<MedicalRecord> resultMedicalRecordList = Arrays.asList(result.getBody());
        assertNotNull(resultMedicalRecordList);
        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        //****************Check with java**************************
        assertNotNull(resultMedicalRecordList);
        assertTrue(resultMedicalRecordList.size() > 1);
        assertNotNull(resultMedicalRecordList.get(0));
        assertNotNull(resultMedicalRecordList.get(1));

        //********GIVEN For following Test********
        medicalRecord1 = resultMedicalRecordList.get(0);
        medicalRecord2 = resultMedicalRecordList.get(1);
        //THEN
        assertNotEquals(medicalRecord1, medicalRecord2);
    }

    @Order(2)
    @Test
    void getMedicalRecord() {
        //***********GIVEN*************
        String urlTemplate = String.format("%s%s&%s",
                this.baseURL.getPath(),
                medicalRecord1.getFirstName(),
                medicalRecord1.getLastName());
        //WHEN
        ResponseEntity<MedicalRecord> result = template.getForEntity(
                urlTemplate,
                MedicalRecord.class);
        //THEN
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());//200

        MedicalRecord resultJavaObject = result.getBody();
        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        //*****************Check with JAVA*************************
        assertNotNull(resultJavaObject);
        assertThat(medicalRecord1).isEqualToComparingFieldByField(resultJavaObject);
    }

    @Order(3)
    @Test
    void createMedicalRecord() {
        //****************GIVEN*******************
        String jsonGiven = convertJavaToJson(medicalRecordCreated);
        String urlDestination = String.format("%s&%s",
                UriUtils.encode(
                        medicalRecordCreated.getFirstName(), StandardCharsets.UTF_8),
                UriUtils.encode
                        (medicalRecordCreated.getLastName(), StandardCharsets.UTF_8));
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
        assertTrue(locationHeader.getPath().contains(urlDestination));
        //*********************************************************
        //**************CHECK OBJECT created***********************
        //*********************************************************
        ResponseEntity<MedicalRecord> response = template.getForEntity(
                        locationHeader.getPath(),
                        MedicalRecord.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        MedicalRecord resultJavaObject = response.getBody();
        assertNotNull(resultJavaObject);
        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        //*****************Check with JAVA*************************
        assertNotNull(resultJavaObject);
        assertThat(medicalRecordCreated).isEqualToComparingFieldByField(resultJavaObject);
        //https://www.baeldung.com/rest-template
        //https://www.baeldung.com/spring-resttemplate-post-json
        //https://stackoverflow.com/questions/52364187/resttemplate-exchange-vs-postforentity-vs-execute
    }

    @Test
    void updateMedicalRecord() {
        //*********************GIVEN*************************
        String jsonGiven = convertJavaToJson(medicalRecordUpdated);
        String urlUpdated = String.format("%s%s&%s",
                this.baseURL.getPath(),
                UriUtils.encode(
                        medicalRecordUpdated.getFirstName(), StandardCharsets.UTF_8),
                UriUtils.encode(
                        medicalRecordUpdated.getLastName(), StandardCharsets.UTF_8));
        HttpEntity<String> request = new HttpEntity<String>(jsonGiven, headers);
        //********************WHEN****************************
        ResponseEntity<MedicalRecord> response = template.exchange(
                this.baseURL.getPath(),
                HttpMethod.PUT,
                request,
                MedicalRecord.class);

        //********************THEN****************************
        assertEquals(HttpStatus.OK, response.getStatusCode());

        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        //*****************Check with JAVA*************************
        MedicalRecord resultJavaObject = response.getBody();
        assertNotNull(resultJavaObject);
        assertThat(medicalRecordUpdated).isEqualToComparingFieldByField(resultJavaObject);

        //*********************************************************
        //**************CHECK OBJECT updated***********************
        //*********************************************************
        ResponseEntity<MedicalRecord> response2 = template.getForEntity(
                urlUpdated,
                MedicalRecord.class);
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        MedicalRecord resultJavaObject2 = response2.getBody();
        assertNotNull(resultJavaObject2);
        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        //*****************Check with JAVA*************************
        assertThat(medicalRecordUpdated).isEqualToComparingFieldByField(resultJavaObject2);
        assertEquals(medicalRecordUpdated.getAllergies(), resultJavaObject2.getAllergies());
        assertNotEquals(medicalRecordCreated.getAllergies(), resultJavaObject2.getAllergies());

        //https://www.baeldung.com/rest-template
        //https://o7planning.org/fr/11647/exemple-spring-boot-restful-client-avec-resttemplate#a13901576
    }


    @Test
    void deleteMedicalRecord() {
        String urlTemplate = String.format("%s%s&%s",
                this.baseURL.getPath(),
                medicalRecordUpdated.getFirstName(),
                medicalRecordUpdated.getLastName());
        URI uriFull = URI.create(urlTemplate);
        HttpEntity<MedicalRecord> request = new HttpEntity<>(headers);
        //WHEN
        ResponseEntity<MedicalRecord> response = template.exchange(
                uriFull,
                HttpMethod.DELETE,
                request,
                MedicalRecord.class);

        //THEN
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        MedicalRecord medicalResponse = response.getBody();
        assertNull(medicalResponse);

        //**************CHECK Deleted Object**************
        ResponseEntity<MedicalRecord> response2 = template.getForEntity(
                urlTemplate,
                MedicalRecord.class);
        assertNotNull(response2);
        assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode());
        MedicalRecord medicalResponse2 = response2.getBody();
        assertNull(medicalResponse2);
    }
}


//https://stackoverflow.com/questions/18336277/how-to-check-string-in-response-body-with-mockmvc
//https://www.logicbig.com/tutorials/spring-framework/spring-web-mvc/json-message-object-conversion.html