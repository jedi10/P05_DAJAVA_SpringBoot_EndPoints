package com.safetynet.alerts.service;

import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.dao.MedicalRecordDaoImpl;
import com.safetynet.alerts.dao.PersonDaoImpl;
import com.safetynet.alerts.dao.RootFile;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.utils.Jackson;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
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
class CommunityEmailServiceIT {

    @Autowired
    CommunityEmailService communityEmailService;

    IPersonDAO personDAO;

    @Mock
    private RootFile rootFile;

    private byte[] fileBytes;

    List<Person> personList;

    @BeforeAll
    void setUp() throws IOException {
        //GIVEN
        String fileString = Files.readString(Paths.get("src/test/resources/testData.json"));
        fileBytes = fileString.getBytes(StandardCharsets.UTF_8);
    }


    @BeforeEach
    void setUpEach()  {

    }


    @AfterEach
    void tearDown() {
    }

    @Order(1)
    @Test
    void getCommunityEmail_cityOK() throws IOException {
        //GIVEN
        when(rootFile.getBytes()).thenReturn(fileBytes);
        //Mock injection
        personDAO = new PersonDaoImpl(rootFile);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(rootFile, Mockito.times(1)).getBytes();

        //***********************************
        //Preparation List of DATA
        //***********************************
        personList = personDAO.getPersonList();
        communityEmailService.personDAO = personDAO;
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
        verify(rootFile, Mockito.times(1)).getBytes();
    }

    @Order(2)
    @Test
    void getCommunityEmail_cityNotFound() throws IOException {
        //GIVEN
        when(rootFile.getBytes()).thenReturn(fileBytes);
        //Mock injection
        personDAO = new PersonDaoImpl(rootFile);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(rootFile, Mockito.times(1)).getBytes();

        personList = personDAO.getPersonList();
        communityEmailService.personDAO = personDAO;
        String city = "New York";
        //Filtering list and transformation
        List<String> expectedMailList = this.personList.stream()
                .filter(o -> city.equals(o.getCity()))
                .map(n -> n.getEmail())
                .collect(Collectors.toList());

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
        verify(rootFile, Mockito.times(1)).getBytes();
    }

    @Order(3)
    @Test
    void getCommunityEmail_cityNull() throws IOException {
        //GIVEN
        when(rootFile.getBytes()).thenReturn(fileBytes);
        //Mock injection
        personDAO = new PersonDaoImpl(rootFile);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(rootFile, Mockito.times(1)).getBytes();

        personList = personDAO.getPersonList();
        communityEmailService.personDAO = personDAO;
        String city = null;

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
        verify(rootFile, Mockito.times(1)).getBytes();
    }
}