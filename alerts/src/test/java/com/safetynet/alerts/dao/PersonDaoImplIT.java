package com.safetynet.alerts.dao;

import com.safetynet.alerts.configuration.AlertsProperties;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.utils.Jackson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonDaoImplIT {

    private static Map<String, List<Person>> wrapperList = new HashMap<>();
    private static List<Person> personsGiven = new ArrayList<>();

    @Autowired
    private IPersonDAO personDAO;

    @Autowired
    private AlertsProperties alertsProperties;

    //String staticJsonData = "{\"persons\": [  { \"firstName\":\"John\", \"lastName\":\"Boyd\", \"address\":\"1509 Culver St\", \"city\":\"Culver\", \"zip\":\"97451\", \"phone\":\"841-874-6512\", \"email\":\"jaboyd@email.com\" },{ \"firstName\":\"Jacob\", \"lastName\":\"Boyd\", \"address\":\"1509 Culver St\", \"city\":\"Culver\", \"zip\":\"97451\", \"phone\":\"841-874-6513\", \"email\":\"drk@email.com\" }]}";

    @BeforeAll
    void setUp(){
        //log.warn(persons.toString());
    }

    @BeforeEach
    void setUpEach() {
        //GIVEN

        //WHEN

    }

    @AfterAll
    void tearDown() {
        this.personDAO = null;
    }



    @Order(1)
    @Test
    void getRootFile() throws IOException {
        //THEN
        //******************************
        //Test Getter for RootFile
        //******************************
        assertNotNull(this.personDAO);
        assertNotNull(this.personDAO.getRootFile());

        //******************************
        //Test Content of RootFile
        //******************************
        //THEN for PATH
        assertNotNull(this.personDAO.getRootFile().getPath());
        assertNotNull(alertsProperties);
        assertNotNull(alertsProperties.getJsonFilePath());
        assertEquals(alertsProperties.getJsonFilePath(), this.personDAO.getRootFile().getPath());

        //GIVEN
        byte[] fileBytesExpected = Files.readAllBytes(Paths.get(alertsProperties.getJsonFilePath()));
        //THEN for BYTE
        assertNotNull(this.personDAO.getRootFile().getBytes());
        assertEquals(fileBytesExpected.length, this.personDAO.getRootFile().getBytes().length);
        assertArrayEquals(fileBytesExpected, this.personDAO.getRootFile().getBytes());
    }

    @DisplayName("Check data in DAO")
    @Order(2)
    @Test
    void convertJsonRootDataToJavaTest() throws IOException {
        //GIVEN
        //this.personDAO.getRootFile().setBytes(personsJson.getBytes());
        //Person List as we should find in DAO Person List
        List<Person> personList = Jackson.convertJsonRootDataToJava(
                this.personDAO.getRootFile().getBytes(),
                "persons",
                Person.class);
        //THEN
        //**************************************
        //check if method build list as intended
        assertNotNull(personList);
        assertNotNull(personDAO.getPersonList());

        //************************************************************
        //check that list returned by method is place on the good place in DAO
        assertEquals(personList.size(), personDAO.getPersonList().size());
        assertEquals(personList.get(0), personDAO.getPersonList().get(0));
        assertEquals(
                personList.get(personList.size()-1),
                personDAO.getPersonList().get(personList.size()-1));

        /**We have checked that Person constructor contain this:
        personDAO.setPersonList(Jackson.convertJsonRootDataToJava(
                this.personDAO.getRootFile().getBytes(),
                "persons",
                Person.class)
        );**/
    }
}