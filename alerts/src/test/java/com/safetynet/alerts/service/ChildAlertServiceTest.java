package com.safetynet.alerts.service;

import com.safetynet.alerts.dao.IMedicalRecordDAO;
import com.safetynet.alerts.dao.IPersonDAO;
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
class ChildAlertServiceTest {

    @Autowired
    private ChildAlertService childAlertService;

    @Mock
    IPersonDAO personDAO;

    @Mock
    IMedicalRecordDAO medicalRecordDAO;

    List<IPersonInfoRTO> personInfoRTOList;

    private List<Person> personList;

    private List<MedicalRecord> medicalRecordList;

    @BeforeEach
    void setUp() throws IOException {
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
    }

    @AfterEach
    void tearDown() {
    }

    @Order(1)
    @Test
    void getChildAlert_OK() {
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
        List<IPersonInfoRTO> expectedChildRTOList = personInfoRTOList.stream()
                .filter(o ->
                        personChosenForTest.getAddress().equalsIgnoreCase(o.getAddress()) &&
                        o.getHumanCategory().equals(IPersonInfoRTO.HumanCategory.CHILD)
                )
                .collect(Collectors.toList());
        assertFalse(expectedChildRTOList.isEmpty());

        List<IPersonInfoRTO> expectedAdultRTOList = personInfoRTOList.stream()
                .filter(o ->
                        personChosenForTest.getAddress().equalsIgnoreCase(o.getAddress()) &&
                        o.getHumanCategory().equals(IPersonInfoRTO.HumanCategory.ADULT)
                )
                .collect(Collectors.toList());
        assertFalse(expectedAdultRTOList.isEmpty());

        //************************************************
        //Mocks Configuration
        //************************************************
        when(personDAO.findAll()).thenReturn(this.personList);
        when(medicalRecordDAO.findAll()).thenReturn(this.medicalRecordList);

        //************************************************
        //DATA available via Mock DAO injection in Service
        //************************************************
        //Mock injection
        childAlertService.personDAO = this.personDAO;
        childAlertService.medicalRecordDAO = this.medicalRecordDAO;

        //***********************************************************
        //***************CHECK MOCK INVOCATION at start**************
        //***********************************************************
        verify(personDAO, Mockito.never()).findAll();
        verify(medicalRecordDAO, Mockito.never()).findAll();

        //WHEN
        Map<String, List> objectListResult = childAlertService.getChildAlert(
                personChosenForTest.getAddress());

        //THEN
        //***********************************************************
        //***************CHECK MOCK INVOCATION at end****************
        //***********************************************************
        verify(personDAO, Mockito.times(1)).findAll();
        verify(medicalRecordDAO, Mockito.times(1)).findAll();


        assertNotNull(objectListResult);
        //*******************************
        //Check Content for Children List
        //*******************************
        List<IPersonInfoRTO> childRTOListResult = objectListResult.get("Children");
        assertTrue(childRTOListResult.stream().allMatch(o ->
                o.getAddress().equals(personChosenForTest.getAddress()) &&
                        o.getAge() <= 18
        ));
        //Sorting expected and Result List to compare them
        childRTOListResult.sort(IPersonInfoRTO.comparator);
        expectedChildRTOList.sort(IPersonInfoRTO.comparator);
        assertEquals(expectedChildRTOList, childRTOListResult);
        //*******************************
        //Check Content for Adult List
        //*******************************
        List<IPersonInfoRTO> adultRTOListResult = objectListResult.get("Adults");
        assertTrue(adultRTOListResult.stream().allMatch(o ->
                o.getAddress().equals(personChosenForTest.getAddress()) &&
                        o.getAge() > 18
        ));
        //Sorting expected and Result List to compare them
        adultRTOListResult.sort(IPersonInfoRTO.comparator);
        expectedChildRTOList.sort(IPersonInfoRTO.comparator);
        assertEquals(expectedChildRTOList, adultRTOListResult);
    }

    @Order(2)
    @Test
    void getChildAlert_NotFound() {
        //GIVEN
        when(personDAO.findAll()).thenReturn(this.personList);
        when(medicalRecordDAO.findAll()).thenReturn(this.medicalRecordList);

        //************************************************
        //DATA available via Mock DAO injection in Service
        //************************************************
        //Mock injection
        childAlertService.personDAO = this.personDAO;
        childAlertService.medicalRecordDAO = this.medicalRecordDAO;

        //WHEN
        Map<String, List> objectListResult = childAlertService.getChildAlert("bad address");

        //THEN
        verify(personDAO, Mockito.times(1)).findAll();
        verify(medicalRecordDAO, Mockito.times(1)).findAll();

        assertNotNull(objectListResult);
        assertNotNull(objectListResult.get("Children"));
        assertNotNull(objectListResult.get("Adults"));
        assertTrue(objectListResult.get("Children").isEmpty());
        assertTrue(objectListResult.get("Adults").isEmpty());
    }

    @Order(3)
    @Test
    void getChildAlert_NullParam() {
        //WHEN
        Map<String, List> objectListResult = childAlertService.getChildAlert(null);

        //THEN
        verify(personDAO, Mockito.never()).findAll();
        verify(medicalRecordDAO, Mockito.never()).findAll();

        assertNotNull(objectListResult);
        assertNotNull(objectListResult.get("Children"));
        assertNotNull(objectListResult.get("Adults"));
        assertTrue(objectListResult.get("Children").isEmpty());
        assertTrue(objectListResult.get("Adults").isEmpty());
    }

}