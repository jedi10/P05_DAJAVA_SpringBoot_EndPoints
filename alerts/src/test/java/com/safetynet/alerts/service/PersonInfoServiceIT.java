package com.safetynet.alerts.service;

import com.safetynet.alerts.dao.*;
import com.safetynet.alerts.service.rto_models.IPersonInfoRTO;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersonInfoServiceIT {

    @Autowired
    PersonInfoService personInfoService;

    IPersonDAO personDAO;

    IMedicalRecordDAO medicalRecordDAO;

    @Mock
    private RootFile rootFile;

    private byte[] fileBytes;

    @BeforeAll
    void setUpAll() throws IOException {
        String fileString = Files.readString(Paths.get("src/test/resources/testData.json"));
        fileBytes = fileString.getBytes(StandardCharsets.UTF_8);
    }

    @BeforeEach
    void setUp() throws Exception {
        //GIVEN
        when(rootFile.getBytes()).thenReturn(fileBytes);
        //Mock injection
        personDAO = new PersonDaoImpl(rootFile);
        medicalRecordDAO = new MedicalRecordDaoImpl(rootFile);

        personInfoService.personDAO = this.personDAO;
        personInfoService.medicalRecordDAO = this.medicalRecordDAO;
    }

    @AfterEach
    void tearDown() {
        personInfoService.firstName = null;
        personInfoService.lastName = null;
        personInfoService.personInfoRTOList = null;
    }
/**
    @Order(1)
    @Test
    void testRootFileLoading() {
        //WHEN
        // Before Test, We construct DAO for Person and MedicalRecord
        //THEN
        verify(rootFile, Mockito.times(2)).getBytes();
    }

    @Order(2)
    @Test
    void getPersonInfo_Ok() throws Exception {
        //WHEN
        IPersonInfoRTO personInfoRTO = personInfoService.getPersonInfo("john", "boyd");

        //THEN
        assertNotNull(personInfoRTO);
        assertEquals("john", personInfoRTO.getFirstName().toLowerCase());
        assertEquals("boyd", personInfoRTO.getLastName().toLowerCase());
        assertNotNull(personInfoRTO.getAddress());
        assertFalse(personInfoRTO.getAddress().isEmpty());
        assertNotNull(personInfoRTO.getBirthdate());
        assertNotNull(personInfoRTO.getMedications());
        assertNotNull(personInfoRTO.getAllergies());
    }

    @Order(3)
    @Test
    void getPersonInfoDebounce_Ok() {
        //WHEN
        IPersonInfoRTO personInfoRTO = personInfoService.getPersonInfo("john", "boyd");
        IPersonInfoRTO personInfoRTO2 = personInfoService.getPersonInfo("john", "boyd");

        //THEN
        assertNotNull(personInfoRTO);
        assertNotNull(personInfoRTO2);
        assertEquals(personInfoRTO, personInfoRTO2);
        assertSame(personInfoRTO, personInfoRTO2);
    }

    @Order(4)
    @Test
    void getPersonInfoNull_Error() {
        //WHEN
        IPersonInfoRTO personInfoRTO = personInfoService.getPersonInfo(null, "boyd");

        //THEN
        assertNull(personInfoRTO);
    }

    @Order(5)
    @Test
    void getPersonInfoNullFromDao_Error() throws Exception {
        //WHEN
        IPersonInfoRTO personInfoRTO = personInfoService.getPersonInfo("john2", "boyd");

        //THEN
        assertNull(personInfoRTO);
    }**/
}