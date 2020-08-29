package com.safetynet.alerts.service;

import com.safetynet.alerts.dao.IFirestationDAO;
import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.models.Firestation;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.utils.Jackson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PhoneAlertServiceTest {

   PhoneAlertService phoneAlertService;

    @Mock
    IPersonDAO personDAO;

    @Mock
    IFirestationDAO firestationDAO;

    private List<Firestation> firestationList;

    private List<Person> personList;

    private byte[] fileBytes;

    @BeforeAll()
    void setUp() throws IOException {
        String fileString = Files.readString(Paths.get("src/test/resources/testData.json"));
        fileBytes = fileString.getBytes(StandardCharsets.UTF_8);
    }


    @BeforeEach
    void setUpEach() throws IOException {
        try {
            this.personList = Jackson.convertJsonRootDataToJava(
                    fileBytes,
                    "persons",
                    Person.class);
            this.firestationList = Jackson.convertJsonRootDataToJava(
                    fileBytes,
                    "firestations",
                    Firestation.class);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @AfterEach
    void tearDown() {
    }

    @Order(1)
    @Test
    void getPhoneAlert() {
        //**************************GIVEN****************************
        assertNotNull(personList);
        assertTrue(personList.size()>2);
        assertNotNull(firestationList);
        assertTrue(firestationList.size()>2);

        when(firestationDAO.findAll()).thenReturn(firestationList);
        when(personDAO.findAll()).thenReturn(personList);
        //Inject Mocks in tested Object
        phoneAlertService = new PhoneAlertService(this.personDAO, this.firestationDAO);

        String stationChosenForTest = firestationList.get(0).getStation();
        List<String> expectedAddressList = firestationList.stream()
                .filter(e-> e.getStation().equalsIgnoreCase(stationChosenForTest))
                .map(Firestation::getAddress)
                .collect(Collectors.toList());

        List<String> expectedPhoneList = personList.stream()
                .filter(e ->  expectedAddressList.contains(e.getAddress()))
                .map(Person::getPhone)
                .collect(Collectors.toList());

        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(firestationDAO, Mockito.never()).findAll();
        verify(personDAO, Mockito.never()).findAll();

        //**************************WHEN****************************
        List<String> phoneListResult = phoneAlertService.getPhoneAlert(
                stationChosenForTest);

        //**************************THEN****************************
        assertNotNull(phoneListResult);
        assertFalse(phoneListResult.isEmpty());
        assertEquals(expectedPhoneList, phoneListResult);

        //***********************************************************
        //***************CHECK MOCK INVOCATION at end****************
        //***********************************************************
        verify(firestationDAO, Mockito.times(1))
                .findAll();
        verify(personDAO, Mockito.times(1))
                .findAll();
    }

    @Order(2)
    @ParameterizedTest
    @ValueSource(strings = {"stationNotFound"})
    void getPhoneAlert_stationNotFound(String station) {
        //**************************GIVEN****************************
        assertNotNull(personList);
        assertTrue(personList.size()>2);
        assertNotNull(firestationList);
        assertTrue(firestationList.size()>2);

        when(firestationDAO.findAll()).thenReturn(firestationList);
        when(personDAO.findAll()).thenReturn(personList);
        //Inject Mocks in tested Object
        phoneAlertService = new PhoneAlertService(this.personDAO, this.firestationDAO);

        List<String> expectedPhoneList = new ArrayList<>();

        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(firestationDAO, Mockito.never()).getFirestationList();
        verify(personDAO, Mockito.never()).getPersonList();
        //**************************WHEN****************************
        List<String> phoneListResult = phoneAlertService.getPhoneAlert(
                station);

        //**************************THEN****************************
        assertNotNull(phoneListResult);
        assertTrue(phoneListResult.isEmpty());
        assertEquals(expectedPhoneList, phoneListResult);

        //***********************************************************
        //***************CHECK MOCK INVOCATION at end****************
        //***********************************************************
        verify(firestationDAO, Mockito.times(1))
                .findAll();
        verify(personDAO, Mockito.never()).findAll();
    }

    @Order(3)
    @Test
    void getPhoneAlert_stationNull() {
        //**************************GIVEN****************************
        assertNotNull(personList);
        assertTrue(personList.size()>2);
        assertNotNull(firestationList);
        assertTrue(firestationList.size()>2);

        when(firestationDAO.findAll()).thenReturn(firestationList);
        when(personDAO.findAll()).thenReturn(personList);
        //Inject Mocks in tested Object
        phoneAlertService = new PhoneAlertService(this.personDAO, this.firestationDAO);

        List<String> expectedPhoneList = new ArrayList<>();

        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(firestationDAO, Mockito.never()).findAll();
        verify(personDAO, Mockito.never()).findAll();
        //**************************WHEN****************************
        List<String> phoneListResult = phoneAlertService.getPhoneAlert(
                null);

        //**************************THEN****************************
        assertNotNull(phoneListResult);
        assertTrue(phoneListResult.isEmpty());
        assertEquals(expectedPhoneList, phoneListResult);

        //***********************************************************
        //***************CHECK MOCK INVOCATION at end****************
        //***********************************************************
        verify(firestationDAO, Mockito.never()).findAll();
        verify(personDAO, Mockito.never()).findAll();
    }
}