package com.safetynet.alerts.service;

import com.safetynet.alerts.dao.*;
import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.service.rto_models.IPersonInfoRTO;
import com.safetynet.alerts.service.rto_models.PersonInfoRTO;
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
class PersonInfoServiceIT {

    PersonInfoService personInfoService;

    IPersonDAO personDAO;

    IMedicalRecordDAO medicalRecordDAO;

    @Mock
    private RootFile rootFile;

    private byte[] fileBytes;

    private List<Person> personList;

    private List<MedicalRecord> medicalRecordList;

    @Value("${app.alerts.test-json-file-path}")
    private String testJsonFilePath;

    @BeforeAll
    void setUpAll() throws IOException {
        String fileString = Files.readString(Paths.get(testJsonFilePath));
        fileBytes = fileString.getBytes(StandardCharsets.UTF_8);
    }

    @BeforeEach
    void setUp() throws Exception {
        //GIVEN
        when(rootFile.getBytes()).thenReturn(fileBytes);
        //**************************************************
        //DATA available via Mock FileBytes injection in DAO
        //**************************************************
        //Mock injection
        personDAO = new PersonDaoImpl(rootFile);
        medicalRecordDAO = new MedicalRecordDaoImpl(rootFile);

        personInfoService = new PersonInfoService(this.personDAO, this.medicalRecordDAO);

        this.personList = personDAO.findAll();
        this.medicalRecordList = medicalRecordDAO.findAll();
    }

    @AfterEach
    void tearDown() {
        personInfoService.firstName = null;
        personInfoService.lastName = null;
        personInfoService.personInfoRTOList = null;
    }

    @Order(1)
    @Test
    void testRootFileLoading() {
        //WHEN
        // Before Test, We construct DAO for Person and MedicalRecord
        //THEN
        verify(rootFile, Mockito.times(2)).getBytes();
        assertNotNull(this.personList,
                "PersonList is Null: we need it for further tests");
        assertTrue(this.personList.size()>2);
        assertNotNull(this.medicalRecordList,
                "MedicalRecordList is Null: we need it for further tests");
        assertTrue(this.medicalRecordList.size()>2);
    }

    @Order(2)
    @Test
    void getPersonInfo_Ok() {
        //GIVEN
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

        //WHEN
        List<IPersonInfoRTO> personInfoRTOListResult = personInfoService.getPersonInfo(
                personChosenForTest.getFirstName(),
                personChosenForTest.getLastName());

        //THEN
        assertNotNull(personInfoRTOListResult);
        assertTrue(personInfoRTOListResult.stream().anyMatch(o ->
                o.getFirstName().equals(expectedChosenPersonRTO.getFirstName()) &&
                        o.getLastName().equals(expectedChosenPersonRTO.getLastName())
        ));
        assertTrue(personInfoRTOListResult.stream().allMatch(o ->
                o.getLastName().equals(expectedChosenPersonRTO.getLastName())));

        //Sorting expected and Result List to compare them
        personInfoRTOListResult.sort(IPersonInfoRTO.comparator);
        expectedPersonRTOList.sort(IPersonInfoRTO.comparator);
        assertEquals(expectedPersonRTOList, personInfoRTOListResult);

    }

    @Order(3)
    @Test
    void getPersonInfoDebounce_Ok() {
        //GIVEN
        //we choose first element on list to get the name for test
        Person personChosenForTest = this.personList.get(0);
        //WHEN
        List<IPersonInfoRTO> personInfoRTOListResult = personInfoService.getPersonInfo(
                personChosenForTest.getFirstName(), personChosenForTest.getLastName());
        List<IPersonInfoRTO> personInfoRTOListResult2 = personInfoService.getPersonInfo(
                personChosenForTest.getFirstName(), personChosenForTest.getLastName());
        //THEN
        assertNotNull(personInfoRTOListResult);
        assertNotNull(personInfoRTOListResult2);
        assertEquals(personInfoRTOListResult, personInfoRTOListResult2);
        assertSame(personInfoRTOListResult, personInfoRTOListResult2);
    }

    @Order(4)
    @Test
    void getPersonInfoNull_Error() {
        //GIVEN
        //we choose first element on list to get the name for test
        Person personChosenForTest = this.personList.get(0);

        //WHEN
        List<IPersonInfoRTO> personInfoRTOListResult = personInfoService.getPersonInfo(
                personChosenForTest.getFirstName(), null);

        //THEN
        assertNotNull(personInfoRTOListResult);
        assertTrue(personInfoRTOListResult.isEmpty());
    }

    @Order(5)
    @Test
    void getPersonInfo_notFound() {
        //WHEN
        List<IPersonInfoRTO> personInfoRTOListResult = personInfoService.getPersonInfo("john2", "boyd2");

        //THEN
        assertNotNull(personInfoRTOListResult);
        assertTrue(personInfoRTOListResult.isEmpty());
    }
}