package com.safetynet.alerts.service;

import com.safetynet.alerts.dao.IFirestationDAO;
import com.safetynet.alerts.dao.IMedicalRecordDAO;
import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.models.Firestation;
import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.service.rto_models.IPersonInfoRTO;
import com.safetynet.alerts.service.rto_models.PersonInfoRTO;
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
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FireAddressServiceTest {

    @Autowired
    FireAddressService fireAddressService;

    @Mock
    IPersonDAO personDAO;

    @Mock
    IMedicalRecordDAO medicalRecordDAO;

    @Mock
    IFirestationDAO firestationDAO;

    List<IPersonInfoRTO> personInfoRTOList;

    private List<Person> personList;

    private List<MedicalRecord> medicalRecordList;

    private Firestation firestation;

    @BeforeEach
    void setUp() throws IOException {
        String fileString = Files.readString(Paths.get("src/test/resources/testData.json"));
        byte[] fileBytes = fileString.getBytes(StandardCharsets.UTF_8);
        try {
            this.personList = Jackson.convertJsonRootDataToJava(
                    fileBytes,
                    "persons",
                    Person.class);
            this.medicalRecordList = Jackson.convertJsonRootDataToJava(
                    fileBytes,
                    "medicalrecords",
                    MedicalRecord.class);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @AfterEach
    void tearDown() {
    }

    @Order(1)
    @Test
    void getFireAddress_OK() {
        //GIVEN
        assertNotNull(this.personList,
                "PersonList is Null: we need it for further tests");
        assertTrue(this.personList.size()>2);
        assertNotNull(this.medicalRecordList,
                "MedicalRecordList is Null: we need it for further tests");
        assertTrue(this.medicalRecordList.size()>2);

       personInfoRTOList =  PersonInfoRTO.buildPersonInfoRTOList(this.personList, this.medicalRecordList);

        //we choose first element on list to get the address for test
        Person personChosenForTest = this.personList.get(0);

        //Filtering list
        List<IPersonInfoRTO> expectedPersonRTOList = personInfoRTOList.stream()
                .filter(o -> personChosenForTest.getAddress().equalsIgnoreCase(o.getAddress()))
                .collect(Collectors.toList());
        assertFalse(expectedPersonRTOList.isEmpty());

        Firestation expectedFirestation = firestationDAO.findByAddress(
                personChosenForTest.getAddress());
        assertNotNull(expectedFirestation);
        String expectedFirestationNumber = expectedFirestation.getStation();


        when(personDAO.findAll()).thenReturn(this.personList);
        when(medicalRecordDAO.findAll()).thenReturn(this.medicalRecordList);
        when(firestationDAO.findByAddress(personChosenForTest.getAddress())).thenReturn(this.firestation);

        //************************************************
        //DATA available via Mock DAO injection in Service
        //************************************************
        //Mock injection
        fireAddressService.personDAO = this.personDAO;
        fireAddressService.medicalRecordDAO = this.medicalRecordDAO;
        fireAddressService.firestationDAO = this.firestationDAO;

        //***********************************************************
        //***************CHECK MOCK INVOCATION at start**************
        //***********************************************************
        verify(personDAO, Mockito.never()).findAll();
        verify(medicalRecordDAO, Mockito.never()).findAll();
        verify(firestationDAO, Mockito.never()).findByAddress(
                personChosenForTest.getAddress());

        //WHEN
        Map<String, List> objectListResult = fireAddressService.getFireAddress(
                personChosenForTest.getAddress());

        //THEN
        //***********************************************************
        //***************CHECK MOCK INVOCATION at end****************
        //***********************************************************
        verify(personDAO, Mockito.times(1)).findAll();
        verify(medicalRecordDAO, Mockito.times(1)).findAll();
        verify(firestationDAO, Mockito.times(1)).findByAddress(
                personChosenForTest.getAddress());

        assertNotNull(objectListResult);
        List<IPersonInfoRTO> personRTOListResult = objectListResult.get("Persons");
        assertTrue(personRTOListResult.stream().allMatch(o -> o.getAddress().equals(personChosenForTest.getAddress())));
        //Sorting expected and Result List to compare them
        personRTOListResult.sort(IPersonInfoRTO.comparator);
        expectedPersonRTOList.sort(IPersonInfoRTO.comparator);
        assertEquals(expectedPersonRTOList, personRTOListResult);

        List<String> stationListResult = objectListResult.get("Firestation");
        assertFalse(stationListResult.isEmpty());
        assertEquals(expectedFirestation, stationListResult.get(0));
    }

    @Order(2)
    @Test
    void getFireAddress_NullParam() {
        //WHEN
        Map<String, List> objectListResult = fireAddressService.getFireAddress(null);

        //THEN
        verify(personDAO, Mockito.never()).findAll();
        verify(medicalRecordDAO, Mockito.never()).findAll();
        verify(firestationDAO, Mockito.never()).findByAddress(anyString());

        assertNotNull(objectListResult);
        assertNotNull(objectListResult.get("Persons"));
        assertNotNull(objectListResult.get("Firestation"));
        assertTrue(objectListResult.get("Persons").isEmpty());
        assertTrue(objectListResult.get("Firestation").isEmpty());
    }

    @Order(3)
    @Test
    void getFireAddress_addressNotFound() {
        //WHEN
        Map<String, List> objectListResult = fireAddressService.getFireAddress("bad address");

        //THEN
        verify(personDAO, Mockito.times(1)).findAll();
        verify(medicalRecordDAO, Mockito.times(1)).findAll();
        verify(firestationDAO, Mockito.times(1)).findByAddress(anyString());

        assertNotNull(objectListResult);
        assertNotNull(objectListResult.get("Persons"));
        assertNotNull(objectListResult.get("Firestation"));
        assertTrue(objectListResult.get("Persons").isEmpty());
        assertTrue(objectListResult.get("Firestation").isEmpty());
    }
}