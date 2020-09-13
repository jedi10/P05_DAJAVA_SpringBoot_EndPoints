package com.safetynet.alerts.service;

import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.utils.Jackson;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommunityEmailServiceTest {

    CommunityEmailService communityEmailService;

    @Mock
    IPersonDAO personDAO;

    List<Person> personList;

    @Value("${app.alerts.test-json-file-path}")
    private String testJsonFilePath;

    @BeforeAll
    void setUp() {

    }

    @BeforeEach
    void setUpEach() throws IOException {
        String fileString = Files.readString(Paths.get(testJsonFilePath));
        byte[] fileBytes = fileString.getBytes(StandardCharsets.UTF_8);
        try {
            this.personList = Jackson.convertJsonRootDataToJava(
                    fileBytes,
                    "persons",
                    Person.class);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @AfterEach
    void tearDown() {
    }

    @Order(1)
    @Test
    void getCommunityEmail_cityOK() {
        //GIVEN
        //***********************************
        //Preparation List of DATA
        //***********************************
        assertNotNull(this.personList,
                "PersonList is Null: we need it for further tests");
        assertTrue(this.personList.size()>2);
        //We want to make sure there is at least one city different from other
        Person personWithDifferentCity = this.personList.get(personList.size()-1);
        personWithDifferentCity.setCity("New York");
        //we choose first element on list to get the city for test
        Person personChosenForTest = this.personList.get(0);
        String city = personChosenForTest.getCity();
        //Filtering list and transformation
        List<String> expectedMailList = this.personList.stream()
                .filter(o -> city.equals(o.getCity()))
                .map(n -> n.getEmail())
                .collect(Collectors.toList());
        assertFalse(expectedMailList.contains(personWithDifferentCity.getEmail()),
                "expectedMailList should not have the mail of the person we change the city");
        when(personDAO.getPersonList()).thenReturn(this.personList);
        //Mock Injection
        communityEmailService = new CommunityEmailService(personDAO);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(personDAO, Mockito.never()).getPersonList();


        //WHEN
        List<String> emailListResult = communityEmailService.getCommunityEmail(city);

        //THEN
        //***********************************************************
        //*****CHECK transformation List<Person> to List<mail>*******
        //***********************************************************
        assertNotNull(emailListResult);
        assertTrue(emailListResult.stream().anyMatch(item -> item.contains("@")));
        assertTrue(emailListResult.stream().allMatch(item -> item.contains("@")),
                "resultList should have email as elements");
        emailListResult.add("toto_on_the_city");
        assertFalse(emailListResult.stream().allMatch(item -> item.contains("@")));
        emailListResult.remove("toto_on_the_city");
        //***********************************************************
        //*********CHECK mail selection with a city******************
        //***********************************************************
        assertTrue(emailListResult.contains(personChosenForTest.getEmail()),
                "result list don't contains mail of choosen Person to grab city filter value");
        assertNotEquals(this.personList.size(), emailListResult.size(),
                "emailListResult should have, at least, one element less than initial personList" +
                        " (we change city on one of it's element");
        assertFalse(emailListResult.contains(personWithDifferentCity.getEmail()),
                "emailListResult should not have the mail of the person we change the city");
        assertEquals(expectedMailList, emailListResult,
                "expected and result should be the same");

        //***********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //***********************************************************
        verify(personDAO, Mockito.times(1)).getPersonList();
    }

    @Order(2)
    @Test
    void getCommunityEmail_cityNotFound() {
        //GIVEN
        assertNotNull(this.personList,
                "PersonList is Null: we need it for further tests");
        assertTrue(this.personList.size()>2);
        String city = "New York";
        //Filtering list and transformation
        List<String> expectedMailList = this.personList.stream()
                .filter(o -> city.equals(o.getCity()))
                .map(n -> n.getEmail())
                .collect(Collectors.toList());
        when(personDAO.getPersonList()).thenReturn(this.personList);
        //Mock Injection
        communityEmailService = new CommunityEmailService(personDAO);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(personDAO, Mockito.never()).getPersonList();

        //WHEN
        List<String> emailListResult = communityEmailService.getCommunityEmail(city);

        //THEN
        //***********************************************************
        //*****CHECK transformation List<Person> to List<mail>*******
        //***********************************************************
        assertNotNull(emailListResult);

        //***********************************************************
        //*********CHECK mail selection with a city******************
        //***********************************************************
        assertTrue(emailListResult.isEmpty());
        assertEquals(expectedMailList, emailListResult);

        //***********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //***********************************************************
        verify(personDAO, Mockito.times(1)).getPersonList();
    }

    @Order(3)
    @Test
    void getCommunityEmail_cityNullCase() {
        //GIVEN
        assertNotNull(this.personList,
                "PersonList is Null: we need it for further tests");
        assertTrue(this.personList.size()>2);
        String city = null;

        when(personDAO.getPersonList()).thenReturn(this.personList);
        //Mock Injection
        communityEmailService = new CommunityEmailService(personDAO);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(personDAO, Mockito.never()).getPersonList();

        //WHEN
        List<String> emailListResult = communityEmailService.getCommunityEmail(city);

        //THEN
        //***********************************************************
        //*********CHECK result Null**********
        //***********************************************************
        assertNull(emailListResult);

        //***********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //***********************************************************
        verify(personDAO, Mockito.never()).getPersonList();
    }
}