package com.safetynet.alerts.service;

import com.safetynet.alerts.dao.IMedicalRecordDAO;
import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.service.rto_models.IPersonInfoRTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class PersonInfoServiceTest {

    @Autowired
    PersonInfoService personInfoService;

    @Mock
    IPersonDAO personDAO;

    @Mock
    IMedicalRecordDAO medicalRecordDAO;

    @Mock
    IPersonInfoRTO personInfoRTOMock;

    private Person person1 = new Person(
            "john", "boyd", "rue du colisee", "Rome", 45, "06-12-23-34-45", "wermer@mail.it");

    private List<String> medicationList = new ArrayList<>();

    private List<String> allergiesList = new ArrayList<>();

    private MedicalRecord medicalRecord1 = new MedicalRecord("john", "boyd",
            LocalDate.of(1984, 3, 6));

    @BeforeEach
    void setUp() throws Exception {
        medicationList.add("aznol:350mg"); medicationList.add("hydrapermazol:100mg");
        allergiesList.add("nillacilan");
        medicalRecord1.setMedications(medicationList);
        medicalRecord1.setAllergies(allergiesList);
        when(medicalRecordDAO.findByName("john", "boyd")).thenReturn(medicalRecord1);
        when(personDAO.findByName("john", "boyd")).thenReturn(person1);
        when(medicalRecordDAO.findByName("john2", "boyd")).thenReturn(null);
        when(personDAO.findByName("john2", "boyd")).thenReturn(person1);
        //when(personInfoRTOMock.checkDataConstructor(person1, medicalRecord1)).thenReturn(true);
        //when(personInfoRTOMock.checkDataConstructor(person1, null)).thenThrow(new Exception("coucou"));
        //when(personInfoRTOMock.checkDataConstructor(null, medicalRecord1)).thenReturn(false);
        //Mock injection
        personInfoService.personDAO = this.personDAO;
        personInfoService.medicalRecordDAO = this.medicalRecordDAO;
        personInfoService.personInfo = this.personInfoRTOMock;
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getPersonInfo_Ok() throws Exception {
        //WHEN
        IPersonInfoRTO personInfoRTO = personInfoService.getPersonInfo("john", "boyd");

        //THEN
        verify(personDAO, Mockito.times(1)).findByName("john", "boyd");
        verify(medicalRecordDAO, Mockito.times(1)).findByName("john", "boyd");
        //verify(this.personInfoRTOMock, Mockito.times(1)).checkDataConstructor(person1, medicalRecord1);

        assertNotNull(personInfoRTO);
        assertEquals(person1.getFirstName(), personInfoRTO.getFirstName());
        assertEquals(medicalRecord1.getFirstName(), personInfoRTO.getFirstName());
        assertEquals(medicalRecord1.getMedications(), personInfoRTO.getMedications());
        assertNotSame(medicalRecord1.getMedications(), personInfoRTO.getMedications());
    }

    @Test
    void getPersonInfoDebounce_Ok() {
        //WHEN
        IPersonInfoRTO personInfoRTO = personInfoService.getPersonInfo("john", "boyd");
        IPersonInfoRTO personInfoRTO2 = personInfoService.getPersonInfo("john", "boyd");

        //THEN
        verify(personDAO, Mockito.times(1)).findByName("john", "boyd");
        verify(medicalRecordDAO, Mockito.times(1)).findByName("john", "boyd");

        assertNotNull(personInfoRTO);
        assertNotNull(personInfoRTO2);
        assertEquals(personInfoRTO, personInfoRTO2);
        assertSame(personInfoRTO, personInfoRTO2);
    }

    @Test
    void getPersonInfoNull_Error() {
        //WHEN
        IPersonInfoRTO personInfoRTO = personInfoService.getPersonInfo(null, "boyd");

        //THEN
        verify(personDAO, Mockito.times(0)).findByName("john", "boyd");
        verify(medicalRecordDAO, Mockito.times(0)).findByName("john", "boyd");

        assertNull(personInfoRTO);
    }

    @Test
    void getPersonInfoNullFromDao_Error() throws Exception {
        //WHEN
        IPersonInfoRTO personInfoRTO = personInfoService.getPersonInfo("john2", "boyd");

        //THEN
        verify(personDAO, Mockito.times(1)).findByName("john2", "boyd");
        verify(medicalRecordDAO, Mockito.times(1)).findByName("john2", "boyd");//return null
        //verify(this.personInfoRTO, Mockito.times(1)).checkDataConstructor(person1, null);
        assertNull(personInfoRTO);
    }

}