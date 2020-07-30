package com.safetynet.alerts.dao;

import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.utils.Jackson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonDaoImplTest {

    private static Map<String, List<Person>> wrapperList = new HashMap<>();
    private static List<Person> personsGiven = new ArrayList<>();

    @Autowired
    private IPersonDAO iPersonDAO;

    static {
        //GIVEN
        Person person = new Person("julia", "werner", "rue du colysée", "Rome", 45, "06-12-23-34-45", "wermer@mail.it");
        Person person2 = new Person("judy", "holmes", "rue de la pensee", "Londre", 89, "06-25-74-90-12", "holmes@mail.en");
        Person personCreated = new Person("jack", "mortimer", "rue du stade", "Rome", 45, "06-25-50-90-12", "mortimer@mail.it");
        //personList = List.of(person, person2);//immutable
        personsGiven.add(person);
        personsGiven.add(person2);
        wrapperList.put("persons", personsGiven);
    }

    //String staticJsonData = "{\"persons\": [  { \"firstName\":\"John\", \"lastName\":\"Boyd\", \"address\":\"1509 Culver St\", \"city\":\"Culver\", \"zip\":\"97451\", \"phone\":\"841-874-6512\", \"email\":\"jaboyd@email.com\" },{ \"firstName\":\"Jacob\", \"lastName\":\"Boyd\", \"address\":\"1509 Culver St\", \"city\":\"Culver\", \"zip\":\"97451\", \"phone\":\"841-874-6513\", \"email\":\"drk@email.com\" }]}";

    @BeforeAll
    void setUp(){
        //GIVEN
        String personsJson = Jackson.convertJavaToJson(wrapperList);
        this.iPersonDAO.getRootFile().setBytes(personsJson.getBytes());
        /**
        iPersonDAO.setPersonList(Jackson.convertJsonRootDataToJava(
                this.iPersonDAO.getRootFile().getBytes(),
                "persons",
                Person.class)
        );**/
        //log.warn(persons.toString());
    }

    @BeforeEach
    void setUpEach() {

    }

    @AfterEach
    void tearDown() {
    }

    @DisplayName("Check data in DAO (fixture)")
    @Order(1)
    @Test
    void convertJsonRootDataToJavaTest(){
        //GIVEN
        String personsJson = Jackson.convertJavaToJson(wrapperList);
        this.iPersonDAO.getRootFile().setBytes(personsJson.getBytes());

        //WHEN
        List<Person> personList = Jackson.convertJsonRootDataToJava(
                this.iPersonDAO.getRootFile().getBytes(),
                "persons",
                Person.class);
        //THEN
        assertNotNull(personList);
        assertEquals(personsGiven.size(), personList.size());
        assertEquals(personsGiven.get(0), personList.get(0));
        assertEquals(personsGiven.get(1), personList.get(1));

        iPersonDAO.setPersonList(Jackson.convertJsonRootDataToJava(
                this.iPersonDAO.getRootFile().getBytes(),
                "persons",
                Person.class)
        );
    }

    @Order(2)
    @Test
    void findAll() {
    }
    @Disabled
    @Order(3)
    @Test
    void findByName() {
    }
    @Disabled
    @Order(4)
    @Test
    void save() {
    }
    @Disabled
    @Order(5)
    @Test
    void update() {
    }
    @Disabled
    @Order(6)
    @Test
    void delete() {
    }
}