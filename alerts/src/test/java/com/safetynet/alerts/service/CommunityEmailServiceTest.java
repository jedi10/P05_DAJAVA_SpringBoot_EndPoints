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

    @BeforeEach
    void setUpEach() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getCommunityEmail() {
        //GIVEN
        assertNotNull(this.personList,
                "PersonList is Null: we need it for furthur tests");
        assertTrue(this.personList.size()>2);
        Person personChosenForTest = this.personList.get(0);
        String city = personChosenForTest.getCity();
        when(personDAO.getPersonList()).thenReturn(this.personList);
        //Mock Injection
        communityEmailService.personDAO = personDAO;
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(personDAO, Mockito.never()).getPersonList();


        //WHEN
        List<String> emailListResult = communityEmailService.getCommunityEmail(city);
        assertNotNull(emailListResult);
        assertTrue(emailListResult.contains(personChosenForTest.getEmail()));
        assertTrue(emailListResult.stream().anyMatch(item -> item.contains("@")));

        //THEN

        //***********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //***********************************************************
        verify(personDAO, Mockito.times(1)).getPersonList();
    }
}