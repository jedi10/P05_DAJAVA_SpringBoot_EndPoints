package com.safetynet.alerts.dao;

import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.utils.Jackson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonDaoImplTest {

    private static Map<String, List<Person>> wrapperList = new HashMap<>();
    private static List<Person> personsGiven = new ArrayList<>();

    private IPersonDAO personDAO;

    @Mock
    private RootFile rootFileGiven;

    @Mock
    private RootFile rootFileGivenBis;

    private Person personCreated = new Person(
            "jack",
            "mortimer",
            "rue du stade",
            "Rome",
            45,
            "06-25-50-90-12",
            "mortimer@mail.it");

    private Person personNoSaved = new Person(
            "blake",
            "stocks",
            "rue du stade",
            "Rome",
            45,
            "06-25-50-90-12",
            "mortimer@mail.it");

    static {
        //GIVEN
        Person person = new Person("julia", "werner", "rue du colysee", "Rome", 45, "06-12-23-34-45", "wermer@mail.it");
        Person person2 = new Person("judy", "holmes", "rue de la pensee", "Londre", 89, "06-25-74-90-12", "holmes@mail.en");
        //personList = List.of(person, person2);//immutable
        personsGiven.add(person);
        personsGiven.add(person2);
        wrapperList.put("persons", personsGiven);
    }

    //String staticJsonData = "{\"persons\": [  { \"firstName\":\"John\", \"lastName\":\"Boyd\", \"address\":\"1509 Culver St\", \"city\":\"Culver\", \"zip\":\"97451\", \"phone\":\"841-874-6512\", \"email\":\"jaboyd@email.com\" },{ \"firstName\":\"Jacob\", \"lastName\":\"Boyd\", \"address\":\"1509 Culver St\", \"city\":\"Culver\", \"zip\":\"97451\", \"phone\":\"841-874-6513\", \"email\":\"drk@email.com\" }]}";

    @BeforeAll
    void setUp(){

        //log.warn(persons.toString());
    }

    @BeforeEach
    void setUpEach() throws IOException {
        //GIVEN
        when(rootFileGiven.getPath()).thenReturn("/url/file");
        //when(rootFileGiven.getBytes()).thenReturn("{'data':'infoByte'}".getBytes());
        String personsJson = Jackson.convertJavaToJson(wrapperList);
        when(rootFileGiven.getBytes()).thenReturn(personsJson.getBytes());
        //WHEN
        this.personDAO = new PersonDaoImpl(rootFileGiven);
    }

    @AfterAll
    void tearDown() {
        this.personDAO = null;
    }

    @Order(1)
    @Test
    void getRootFile() {
        //THEN
        //******************************
        //Test Getter for RootFile
        //******************************
        assertNotNull(this.personDAO);
        assertNotNull(this.personDAO.getRootFile());
        assertEquals(rootFileGiven, this.personDAO.getRootFile());
        //******************************
        //Test Content of RootFile
        //******************************
        assertNotNull(this.personDAO.getRootFile().getPath());
        assertEquals(rootFileGiven.getPath(), this.personDAO.getRootFile().getPath());
        assertNotNull(this.personDAO.getRootFile().getBytes());
        assertEquals(rootFileGiven.getBytes(), this.personDAO.getRootFile().getBytes());
        assertArrayEquals(rootFileGiven.getBytes(), this.personDAO.getRootFile().getBytes());
    }

    @Order(2)
    @Test
    void setRootFile() {
        //GIVEN
        RootFile rootFileAtStart = this.personDAO.getRootFile();
        //WHEN
        this.personDAO.setRootFile(rootFileGivenBis);
        //THEN
        assertNotEquals(rootFileAtStart,this.personDAO.getRootFile());
    }

    @Order(3)
    @Test
    void getPersonList() {
        //THEN
        assertNotNull(personDAO);
        assertNotNull(personDAO.getPersonList());
        assertEquals(personsGiven.size(), personDAO.getPersonList().size());
        assertEquals(personsGiven.get(0), personDAO.getPersonList().get(0));
        assertEquals(personsGiven.get(1), personDAO.getPersonList().get(1));
    }
    
    @Order(4)
    @Test
    void setPersonList() {
        //WHEN
        personDAO.setPersonList(null);
        //THEN
        assertNull(personDAO.getPersonList());

        //WHEN
        personDAO.setPersonList(List.of(new Person()));

        //THEN
        assertNotNull(personDAO.getPersonList());
        assertEquals(1, personDAO.getPersonList().size());
    }

    @DisplayName("Check data in DAO (fixture)")
    @Order(5)
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
        assertEquals(personsGiven.size(), personList.size());
        assertEquals(personsGiven.get(0), personList.get(0));
        assertEquals(personsGiven.get(1), personList.get(1));
        //************************************************************
        //check that list returned by method is place on the good place in DAO
        assertNotNull(personDAO.getPersonList());
        assertEquals(personList.size(), personDAO.getPersonList().size());
        assertEquals(personList.get(0), personDAO.getPersonList().get(0));
        assertEquals(personList.get(1), personDAO.getPersonList().get(1));

        /**We have checked that Person constructor contain this:
        personDAO.setPersonList(Jackson.convertJsonRootDataToJava(
                this.personDAO.getRootFile().getBytes(),
                "persons",
                Person.class)
        );**/
    }

    @DisplayName("Check Exception when DAO load wrong data")
    @Order(6)
    @Test
    void convertJsonRootDataToJavaErrorTest() throws IOException {
        //GIVEN
        when(rootFileGiven.getPath()).thenReturn("src/test/resources/testWrongData.json");
        //when(rootFileGiven.getBytes()).thenReturn("{'data':'infoByte'}".getBytes());
        byte[] fileError = Files.readAllBytes(Paths.get(rootFileGiven.getPath()));
        when(rootFileGiven.getBytes()).thenReturn(fileError);
        //WHEN-THEN
        Exception exception = assertThrows(IOException.class, ()-> {
            this.personDAO = new PersonDaoImpl(rootFileGiven);
        });
        assertTrue(exception.getMessage().contains("unexpected file data"));
        //https://www.baeldung.com/junit-assert-exception
    }

    @Order(7)
    @Test
    void findAll() {
        //THEN
        assertNotNull(personDAO);
        assertNotNull(personDAO.getPersonList());
        assertNotNull(personDAO.findAll());
        assertEquals(personDAO.getPersonList(), personDAO.findAll());
        assertEquals(personsGiven.size(), personDAO.findAll().size());
        assertEquals(personsGiven.get(0), personDAO.findAll().get(0));
        assertEquals(personsGiven.get(1), personDAO.findAll().get(1));
    }

    @Order(8)
    @Test
    void findByName() {
        //Given
        Person personExpected = personsGiven.get(1);
        String firstNameGiven = personExpected.getFirstName();
        String lastNameGiven = personExpected.getLastName();
        //When
        Person personResult = personDAO.findByName(firstNameGiven,lastNameGiven);
        //Then
        assertEquals(personExpected, personResult);
    }

    @Order(9)
    @Test
    void save() {
        assertEquals(personsGiven.size(), personDAO.findAll().size());
        //WHEN
        personDAO.save(personCreated);
        //THEN
        assertNotEquals(personsGiven.size(), personDAO.findAll().size());
        assertEquals(personsGiven.size()+1, personDAO.findAll().size());
        //List<Person> listPerson = personDAONoRefresh.getPersonList();
        Person personSaved = personDAO.findByName(personCreated.getFirstName(), personCreated.getLastName());
        assertEquals(personCreated, personSaved);
    }

    @Order(10)
    @Test
    void update() {
        //**************************
        //Case when person not exist
        //**************************
        //When
        Person personUpdated = personDAO.update(personNoSaved);
        //Then
        assertNull(personUpdated);

        //***********************
        //Case when person exists
        //***********************
        //Given
        personDAO.save(personCreated);
        String oldMail = personCreated.getEmail();
        String newMail = "toto@mail.co";
        personCreated.setEmail(newMail);

        //When
        personUpdated = personDAO.update(personCreated);
        //Then
        assertNotNull(personUpdated);
        assertEquals(newMail, personUpdated.getEmail());
        personUpdated = personDAO.findByName(personCreated.getFirstName(), personCreated.getLastName());
        assertEquals(newMail, personUpdated.getEmail());
    }

    @Order(11)
    @Test
    void delete() {
        //**************************
        //Case when person not exist
        //**************************
        //When
        boolean deleteSuccess = personDAO.delete(personNoSaved);
        //Then
        assertFalse(deleteSuccess);

        //***********************
        //Case when person exists
        //***********************
        assertEquals(personsGiven.size(), personDAO.findAll().size());
        Person personToDelete = personDAO.findAll().get(1);
        //When
        deleteSuccess = personDAO.delete(personToDelete);
        //Then
        assertTrue(deleteSuccess);
        assertEquals(personsGiven.size()-1, personDAO.findAll().size());
        Person personSearch = personDAO.findByName(personToDelete.getFirstName(), personToDelete.getLastName());
        assertNull(personSearch);
    }
}