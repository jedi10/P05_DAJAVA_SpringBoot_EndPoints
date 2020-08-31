package com.safetynet.alerts.service;

import com.safetynet.alerts.dao.IFirestationDAO;
import com.safetynet.alerts.dao.IMedicalRecordDAO;
import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.models.Firestation;
import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.service.rto_models.FirestationRTO;
import com.safetynet.alerts.service.rto_models.IPersonInfoRTO;
import com.safetynet.alerts.service.rto_models.PersonInfoRTO;
import com.safetynet.alerts.utils.Jackson;
import org.apache.tomcat.jni.Address;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FirestationServiceTest {

    @Autowired
    private FirestationService firestationService;

    @Mock
    IPersonDAO personDAO;

    @Mock
    IMedicalRecordDAO medicalRecordDAO;

    @Mock
    IFirestationDAO firestationDAO;

    List<IPersonInfoRTO> personInfoRTOList;

    private List<Person> personList;

    private List<MedicalRecord> medicalRecordList;

    private List<Firestation> firestationList;


    @BeforeEach
    void setUp() throws Exception {
        String fileString = Files.readString(Paths.get("src/test/resources/testData.json"));
        byte[] fileBytes = fileString.getBytes(StandardCharsets.UTF_8);
        this.personList = Jackson.convertJsonRootDataToJava(
                fileBytes,
                "persons",
                Person.class);
        this.medicalRecordList = Jackson.convertJsonRootDataToJava(
                fileBytes,
                "medicalrecords",
                MedicalRecord.class);
        this.firestationList = Jackson.convertJsonRootDataToJava(
                fileBytes,
                "firestations",
                Firestation.class);
    }

    @AfterEach
    void tearDown() {
    }

    @Order(1)
    @ParameterizedTest
    @CsvSource({"3","4"})
    void getFirestationArea_Ok(String firestationNumber) {
        //GIVEN
        //*********************check data used in test*****************************
        assertNotNull(this.personList,
                "PersonList is Null: we need it for further tests");
        assertTrue(this.personList.size()>2);
        assertNotNull(this.medicalRecordList,
                "MedicalRecordList is Null: we need it for further tests");
        assertTrue(this.medicalRecordList.size()>2);
        assertNotNull(this.firestationList,
                "FirestationList is Null: we need it for further tests");
        assertTrue(this.firestationList.size()>2);

        personInfoRTOList =  PersonInfoRTO.buildPersonInfoRTOList(this.personList, this.medicalRecordList);

        //*****************Get Address List linked to station number******************
        List<String> listAddressResult = this.firestationList.stream()
                .filter(e-> e.getStation().equalsIgnoreCase(firestationNumber))
                .map(Firestation::getAddress)
                .collect(Collectors.toList());
        //Filtering list
        List<IPersonInfoRTO> expectedPersonRTOList = personInfoRTOList.stream()
                .filter(o ->
                        listAddressResult.contains(o.getAddress())
                )
                .collect(Collectors.toList());

        //*****************Get expected Persons List with Children******************
        List<IPersonInfoRTO> expectedChildRTOList = personInfoRTOList.stream()
                .filter(o ->
                        listAddressResult.contains(o.getAddress()) &&
                                o.getHumanCategory().equals(IPersonInfoRTO.HumanCategory.CHILDREN)
                )
                .collect(Collectors.toList());

        //*****************Get expected Persons List with Adults******************
        List<IPersonInfoRTO> expectedAdultRTOList = personInfoRTOList.stream()
                .filter(o ->
                        listAddressResult.contains(o.getAddress()) &&
                                o.getHumanCategory().equals(IPersonInfoRTO.HumanCategory.ADULTS)
                )
                .collect(Collectors.toList());


        //************************************************
        //Mocks Configuration
        //************************************************
        when(personDAO.findAll()).thenReturn(this.personList);
        when(medicalRecordDAO.findAll()).thenReturn(this.medicalRecordList);
        when(firestationDAO.findAll()).thenReturn(this.firestationList);

        //************************************************
        //DATA available via Mock DAO injection in Service
        //************************************************
        //Mock injection
        firestationService = new FirestationService(
                this.personDAO,
                this.medicalRecordDAO,
                this.firestationDAO);

        //***********************************************************
        //***************CHECK MOCK INVOCATION at start**************
        //***********************************************************
        verify(personDAO, Mockito.never()).findAll();
        verify(medicalRecordDAO, Mockito.never()).findAll();
        verify(firestationDAO, Mockito.never()).findAll();

        //WHEN
        FirestationRTO objectListResult =
                firestationService.getFirestationArea(firestationNumber);

        //THEN
        //***********************************************************
        //***************CHECK MOCK INVOCATION at end****************
        //***********************************************************
        verify(personDAO, Mockito.times(1)).findAll();
        verify(medicalRecordDAO, Mockito.times(1)).findAll();
        verify(firestationDAO, Mockito.times(1)).findAll();

        assertNotNull(objectListResult);
        assertNotNull(objectListResult.getPersonInfoRTOMap());
        assertNotNull(objectListResult.getHumanCategoryMap());
        //********************************
        //Check Content for PersonRTO List
        //********************************
        List<IPersonInfoRTO> personInfoRTOListResult = objectListResult.getPersonInfoRTOMap().get("PERSONS");
        assertEquals(expectedPersonRTOList, personInfoRTOListResult);
        //*******************************
        //Check Children number
        //*******************************
        Long childrenNumberResult = objectListResult.getHumanCategoryMap().get(IPersonInfoRTO.HumanCategory.CHILDREN);
        assertEquals(expectedChildRTOList.size(), childrenNumberResult);
        //*******************************
        //Check Adult number
        //*******************************
        Long adultNumberResult = objectListResult.getHumanCategoryMap().get(IPersonInfoRTO.HumanCategory.ADULTS);
        assertEquals(expectedAdultRTOList.size(), adultNumberResult);

    }
    @Order(2)
    @ParameterizedTest
    @CsvSource({"0"})
    void getFirestationArea_NoStationAddress(String firestationNumber) {
        //GIVEN
        //*********************check data used in test*****************************
        assertNotNull(this.personList,
                "PersonList is Null: we need it for further tests");
        assertTrue(this.personList.size()>2);
        assertNotNull(this.medicalRecordList,
                "MedicalRecordList is Null: we need it for further tests");
        assertTrue(this.medicalRecordList.size()>2);
        assertNotNull(this.firestationList,
                "FirestationList is Null: we need it for further tests");
        assertTrue(this.firestationList.size()>2);

        personInfoRTOList =  PersonInfoRTO.buildPersonInfoRTOList(this.personList, this.medicalRecordList);

        //*****************Get Address List linked to station number******************
        List<String> listAddressResult = this.firestationList.stream()
                .filter(e-> e.getStation().equalsIgnoreCase(firestationNumber))
                .map(Firestation::getAddress)
                .collect(Collectors.toList());
        assertTrue(listAddressResult.isEmpty());

        //************************************************
        //Mocks Configuration
        //************************************************
        when(personDAO.findAll()).thenReturn(this.personList);
        when(medicalRecordDAO.findAll()).thenReturn(this.medicalRecordList);
        when(firestationDAO.findAll()).thenReturn(this.firestationList);

        //************************************************
        //DATA available via Mock DAO injection in Service
        //************************************************
        //Mock injection
        firestationService = new FirestationService(
                this.personDAO,
                this.medicalRecordDAO,
                this.firestationDAO);

        //***********************************************************
        //***************CHECK MOCK INVOCATION at start**************
        //***********************************************************
        verify(personDAO, Mockito.never()).findAll();
        verify(medicalRecordDAO, Mockito.never()).findAll();
        verify(firestationDAO, Mockito.never()).findAll();

        //WHEN
        FirestationRTO objectListResult =
                firestationService.getFirestationArea(firestationNumber);

        //THEN
        //***********************************************************
        //***************CHECK MOCK INVOCATION at end****************
        //***********************************************************
        verify(personDAO, Mockito.never()).findAll();
        verify(medicalRecordDAO, Mockito.never()).findAll();
        verify(firestationDAO, Mockito.times(1)).findAll();

        assertNull(objectListResult);
    }

    @Order(3)
    @Test
    void getChildAlert_NullParam() {
        //WHEN-THEN
        Exception exception = assertThrows(NullPointerException.class, () -> {
            firestationService.getFirestationArea(null);
        });
        assertTrue(exception.getMessage().contains(
                "firestation is marked non-null but is null"));

        verify(personDAO, Mockito.never()).findAll();
        verify(medicalRecordDAO, Mockito.never()).findAll();
        verify(firestationDAO, Mockito.never()).findAll();
    }
}