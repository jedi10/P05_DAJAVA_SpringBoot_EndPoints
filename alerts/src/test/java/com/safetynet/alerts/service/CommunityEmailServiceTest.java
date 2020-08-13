package com.safetynet.alerts.service;

import com.safetynet.alerts.dao.IPersonDAO;
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
class CommunityEmailServiceTest {

    @Autowired
    CommunityEmailService communityEmailService;

    @Mock
    IPersonDAO personDAO;

    List<Person> personList;

    @BeforeAll
    void setUp() throws IOException {

    }


    @BeforeEach
    void setUpEach() throws IOException {
        String fileString = Files.readString(Paths.get("src/test/resources/testData.json"));
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
        assertNotNull(this.personList,
                "PersonList is Null: we need it for furthur tests");
        assertTrue(this.personList.size()>2);
        //We want to make sure there is at least one city different from other
        this.personList.get(personList.size()-1).setCity("New York");
        //we choose first element on list to gest the city for test
        Person personChosenForTest = this.personList.get(0);
        String city = personChosenForTest.getCity();
        //Filtering list and transformation
        List<String> expectedMailList = this.personList.stream()
                .filter(o -> city.equals(o.getCity()))
                .map(n -> n.getEmail())
                .collect(Collectors.toList());
        when(personDAO.getPersonList()).thenReturn(this.personList);
        //Mock Injection
        communityEmailService.personDAO = personDAO;
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
        assertTrue(emailListResult.stream().allMatch(item -> item.contains("@")));
        emailListResult.add("toto_on_the_city");
        assertFalse(emailListResult.stream().allMatch(item -> item.contains("@")));
        emailListResult.remove("toto_on_the_city");
        //***********************************************************
        //*********CHECK mail selection with a city******************
        //***********************************************************
        assertTrue(emailListResult.contains(personChosenForTest.getEmail()));
        assertEquals(expectedMailList, emailListResult);

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
                "PersonList is Null: we need it for furthur tests");
        assertTrue(this.personList.size()>2);
        String city = "New York";
        //Filtering list and transformation
        List<String> expectedMailList = this.personList.stream()
                .filter(o -> city.equals(o.getCity()))
                .map(n -> n.getEmail())
                .collect(Collectors.toList());
        when(personDAO.getPersonList()).thenReturn(this.personList);
        //Mock Injection
        communityEmailService.personDAO = personDAO;
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
}