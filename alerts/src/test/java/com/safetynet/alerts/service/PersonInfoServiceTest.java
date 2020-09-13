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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonInfoServiceTest {

    PersonInfoService personInfoService;

    @Mock
    IPersonDAO personDAO;

    @Mock
    IMedicalRecordDAO medicalRecordDAO;

    @Mock
    IPersonInfoRTO personInfoRTOMock;

    private List<Person> personList;

    private List<MedicalRecord> medicalRecordList;

    private Person person1 = new Person(
            "john", "boyd", "rue du colisee", "Rome", 45, "06-12-23-34-45", "wermer@mail.it");

    private List<String> medicationList = new ArrayList<>();

    private List<String> allergiesList = new ArrayList<>();

    private MedicalRecord medicalRecord1 = new MedicalRecord("john", "boyd",
            LocalDate.of(1984, 3, 6));

    @Value("${app.alerts.test-json-file-path}")
    private String testJsonFilePath;

    @BeforeEach
    void setUp() throws Exception {
        String fileString = Files.readString(Paths.get(testJsonFilePath));
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
        medicationList.add("aznol:350mg"); medicationList.add("hydrapermazol:100mg");
        allergiesList.add("nillacilan");
        medicalRecord1.setMedications(medicationList);
        medicalRecord1.setAllergies(allergiesList);

        when(medicalRecordDAO.findAll()).thenReturn(this.medicalRecordList);
        when(personDAO.findAll()).thenReturn(this.personList);

        //when(personInfoRTOMock.checkDataConstructor(person1, medicalRecord1)).thenReturn(true);
        //when(personInfoRTOMock.checkDataConstructor(person1, null)).thenThrow(new Exception("coucou"));
        //when(personInfoRTOMock.checkDataConstructor(null, medicalRecord1)).thenReturn(false);
        //************************************************
        //DATA available via Mock DAO injection in Service
        //************************************************
        //Mock injection
        personInfoService = new PersonInfoService(this.personDAO, this.medicalRecordDAO);

        //personInfoService.personInfoRTO = this.personInfoRTOMock;
    }

    @AfterEach
    void tearDown() {
        personInfoService.firstName = null;
        personInfoService.lastName = null;
        //personInfoService.personInfo = null;
    }

    @Order(1)
    @Test
    void getPersonInfo_Ok() throws Exception {
        //GIVEN
        assertNotNull(this.personList,
                "PersonList is Null: we need it for further tests");
        assertTrue(this.personList.size()>2);
        assertNotNull(this.medicalRecordList,
                "MedicalRecordList is Null: we need it for further tests");
        assertTrue(this.medicalRecordList.size()>2);

        List<IPersonInfoRTO> personInfoRTOListFull =  PersonInfoRTO.buildPersonInfoRTOList(this.personList, this.medicalRecordList);

        //we choose first element on list to get the name for test
        Person personChosenForTest = this.personList.get(0);

        //Filtering list
        List<IPersonInfoRTO> expectedPersonRTOList = personInfoRTOListFull.stream()
                .filter(o -> personChosenForTest.getLastName().equals(o.getLastName()))
                .collect(Collectors.toList());
        IPersonInfoRTO expectedChosenPersonRTO = expectedPersonRTOList.stream()
                .filter(e -> e.getFirstName().equalsIgnoreCase(personChosenForTest.getFirstName())&&
                        e.getLastName().equalsIgnoreCase(personChosenForTest.getLastName()))
                .findAny()
                .orElse(null);
        //https://www.baeldung.com/find-list-element-java

        assertNotNull(expectedChosenPersonRTO);

        //***********************************************************
        //***************CHECK MOCK INVOCATION at start**************
        //***********************************************************
        verify(personDAO, Mockito.never()).findAll();
        verify(medicalRecordDAO, Mockito.never()).findAll();

        //WHEN
        List<IPersonInfoRTO> personInfoRTOListResult = personInfoService.getPersonInfo(
                personChosenForTest.getFirstName(),
                personChosenForTest.getLastName());

        //THEN
        //***********************************************************
        //***************CHECK MOCK INVOCATION at end****************
        //***********************************************************
        verify(personDAO, Mockito.times(1)).findAll();
        verify(medicalRecordDAO, Mockito.times(1)).findAll();
        //verify(this.personInfoRTOMock, Mockito.times(1)).checkDataConstructor(person1, medicalRecord1);

        assertNotNull(personInfoRTOListResult);
        assertTrue(personInfoRTOListResult.stream().anyMatch(o ->
                o.getFirstName().equals(expectedChosenPersonRTO.getFirstName()) &&
                o.getLastName().equals(expectedChosenPersonRTO.getLastName())
        ));
        assertTrue(personInfoRTOListResult.stream().allMatch(o -> o.getLastName().equals(expectedChosenPersonRTO.getLastName())));

        //Sorting expected and Result List to compare them
        personInfoRTOListResult.sort(IPersonInfoRTO.comparator);
        expectedPersonRTOList.sort(IPersonInfoRTO.comparator);
        assertEquals(expectedPersonRTOList, personInfoRTOListResult);

    }

    @Order(2)
    @Test
    void getPersonInfoDebounce_Ok() {
        //WHEN
        List<IPersonInfoRTO> personInfoRTOListResult = personInfoService.getPersonInfo(
                person1.getFirstName(),person1.getLastName());
        List<IPersonInfoRTO> personInfoRTO2ListResult2 = personInfoService.getPersonInfo(
                person1.getFirstName(),person1.getLastName());

        //THEN
        verify(personDAO, Mockito.times(1)).findAll();
        verify(medicalRecordDAO, Mockito.times(1)).findAll();

        assertNotNull(personInfoRTOListResult);
        assertNotNull(personInfoRTO2ListResult2);
        assertEquals(personInfoRTOListResult, personInfoRTO2ListResult2);
        assertSame(personInfoRTOListResult, personInfoRTO2ListResult2);
    }

    @Order(3)
    @Test
    void getPersonInfoNullOnParamLastName() {
        //WHEN
        List<IPersonInfoRTO> personInfoRTOListResult = personInfoService.getPersonInfo(
                person1.getFirstName(), null);

        //THEN
        verify(personDAO, Mockito.never()).findAll();
        verify(medicalRecordDAO, Mockito.never()).findAll();

        assertNotNull(personInfoRTOListResult);
        assertTrue(personInfoRTOListResult.isEmpty());
    }

    @Order(4)
    @Test
    void getPersonInfo_firstAndLastNameNotFound() {
        //WHEN
        List<IPersonInfoRTO> personInfoRTOListResult = personInfoService.getPersonInfo("john2", "boyd2");

        //THEN
        verify(personDAO, Mockito.times(1)).findAll();
        verify(medicalRecordDAO, Mockito.times(1)).findAll();
        //verify(this.personInfoRTOListResult, Mockito.times(1)).checkDataConstructor(person1, null);

        assertNotNull(personInfoRTOListResult);
        assertTrue(personInfoRTOListResult.isEmpty());
    }

}