package com.safetynet.alerts.rest.publicmicroservices;

import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.models.Person;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PublicAppController_communityEmail_E2E {

    @Autowired
    public PublicAppController publicAppController;

    @Autowired
    public IPersonDAO personDAO;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate template;

    private final String rootURL = "/communityemail/";

    private URL baseURL;

    List<Person> personList;
    List<String> expectedMailList;

    @BeforeAll
    void setUp() throws MalformedURLException {
        //***********GIVEN*************
        this.baseURL = new URL("http://localhost:" + port + rootURL);
        personList = personDAO.findAll();
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
    void getCommunityEmail_cityOk() {
        //***************GIVEN***************
        //***********************************
        //Preparation List of DATA
        //***********************************
        assertNotNull(this.personList,
                "PersonList is Null: we need it for further tests");
        assertTrue(this.personList.size()>2);

        //We want to make sure there is at least one city different from other
        Person personWithDifferentCity = this.personList.get(personList.size()-1);
        personWithDifferentCity.setCity("New York");
        Person personWithDifferentCityAfterUpdate =personDAO.update(personWithDifferentCity);
        assertEquals(personWithDifferentCity, personWithDifferentCityAfterUpdate);
        personList = personDAO.findAll();
        assertEquals("New York", this.personList.get(personList.size()-1).getCity(),
                "We are not sure that at least one person have a different city from another");

        //we choose first element on list to get the city for test
        Person personChosenForTest = this.personList.get(0);
        String city = personChosenForTest.getCity();
        //Filtering list and transformation
        expectedMailList = this.personList.stream()
                .filter(o -> city.equals(o.getCity()))
                .map(n -> n.getEmail())
                .collect(Collectors.toList());
        assertFalse(expectedMailList.contains(personWithDifferentCity.getEmail()),
                "expectedMailList should not have the mail of the person we change the city");

        String urlTemplate = String.format("%s%s",
                this.baseURL,
                city);

        //**********************WHEN*******************************
        ResponseEntity<ArrayList> result = template.getForEntity(
                URI.create(urlTemplate),
                ArrayList.class);

        //**********************THEN*******************************
        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());

        ArrayList<String> resultJavaObject = result.getBody();
        assertNotNull(resultJavaObject);
        assertEquals(expectedMailList, resultJavaObject);
        assertFalse(resultJavaObject.contains(personWithDifferentCity.getEmail()),
                "expectedMailList should not have the mail of the person we change the city");

    }

    @Order(2)
    @ParameterizedTest
    @ValueSource(strings = {"cityName"})
    void getCommunityEmail_cityNotFound(String city) {
        //*********************GIVEN******************************
        String urlTemplate = String.format("%s%s",
                this.baseURL,
                city);

        //**********************WHEN******************************
        ResponseEntity<ArrayList> result = template.getForEntity(
                URI.create(urlTemplate),
                ArrayList.class);

        //**********************THEN*******************************
        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());

        ArrayList<String> resultJavaObject = result.getBody();
        assertNull(resultJavaObject);
    }
}


//https://blog.oio.de/2018/10/26/use-null-values-in-junit-5-parameterized-tests/
//https://www.baeldung.com/parameterized-tests-junit-5
//https://openclassrooms.com/fr/courses/6100311-testez-votre-code-java-pour-realiser-des-applications-de-qualite/6441016-simulez-des-composants-externes-aux-tests-avec-mockito