package com.safetynet.alerts.models;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MedicalRecordTest {

    private MedicalRecord medicalRecord;
    private MedicalRecord medicalRecordNoList;

    @BeforeAll
    void setUp() {
        //GIVEN
        medicalRecord = new MedicalRecord("John",
                "Wallas",
                //https://www.oracle.com/technical-resources/articles/java/jf14-date-time.html
                LocalDate.of(2084, Month.MARCH, 06),
                Arrays.asList("pharmacol:5000mg","terazine:10mg","noznazol:250mg"),
                Arrays.asList("shellfish","peanut"));

        medicalRecordNoList = new MedicalRecord("John",
                "Wallas",
                //https://www.oracle.com/technical-resources/articles/java/jf14-date-time.html
                LocalDate.of(2084, Month.MARCH, 06));
    }

    @AfterAll
    void tearDown() {
        medicalRecord = null;
        medicalRecordNoList = null;
    }

    @Order(1)
    @Test
    void getFirstName() {
        //WHEN
        String dataFromObject = medicalRecord.getFirstName();
        //THEN
        assertEquals("John", dataFromObject);
    }

    @Order(2)
    @Test
    void getLastName() {
        //WHEN
        String dataFromObject = medicalRecord.getLastName();
        //THEN
        assertEquals("Wallas", dataFromObject);
    }

    @Order(3)
    @Test
    void getBirthdate() {
        //WHEN
        LocalDate dateFromObject = medicalRecord.getBirthdate();
        //THEN
        assertEquals(LocalDate.of(2084, Month.MARCH, 06),
                dateFromObject);
    }

    @Order(4)
    @Test()
    void getMedications() {
        //WHEN
        List dataFromObject = medicalRecord.getMedications();
        //THEN
        assertEquals(Arrays.asList("pharmacol:5000mg","terazine:10mg","noznazol:250mg"),
                dataFromObject);

        //WHEN
        dataFromObject = medicalRecordNoList.getMedications();
        //THEN
        //*************************************
        //****Check List is not null********
        //*************************************
        assertNotNull(dataFromObject,
                "Constructor Without Medications List have to create an empty List");
        //*******************************************
        //****Check List is Arraylist Type********
        //*******************************************
        assertTrue(dataFromObject instanceof ArrayList);
        //*******************************
        //****Check List is Empty********
        //*******************************
        assertTrue(dataFromObject.isEmpty(),
                "Constructor Without Medications List have to create an empty List");
    }

    @Order(5)
    @Test()
    void getAllergies() {
        //WHEN
        List dataFromObject = medicalRecord.getAllergies();
        //THEN
        assertEquals(Arrays.asList("shellfish","peanut"), dataFromObject);

        //WHEN
        dataFromObject = medicalRecordNoList.getAllergies();
        //THEN
        //*************************************
        //****Check List is not null********
        //*************************************
        assertNotNull(dataFromObject,
                "Constructor Without Allergies List have to create an empty List");
        //*******************************************
        //****Check List is Arraylist Type********
        //*******************************************
        assertTrue(dataFromObject instanceof ArrayList);
        //*******************************
        //****Check List is Empty********
        //*******************************
        assertTrue(dataFromObject.isEmpty(),
                "Constructor Without Allergies List have to create an empty List");
    }

    @Order(6)
    @ParameterizedTest()
    @ValueSource( strings = { "Julia" })
    void setFirstName(String firstNameGiven) {
        //WHEN
        medicalRecord.setFirstName(firstNameGiven);
        //THEN
        assertEquals(firstNameGiven, medicalRecord.getFirstName());
    }
    @Order(7)
    @ParameterizedTest
    @ValueSource(strings = { "Marrack" })
    void setLastName(String lastNameGiven) {
        //WHEN
        medicalRecord.setLastName(lastNameGiven);
        //THEN
        assertEquals(lastNameGiven, medicalRecord.getLastName());
    }

    @Order(8)
    @Test
    void setBirthdate() {
        //GIVEN
        LocalDate birthdayGiven = LocalDate.of(1989, Month.JANUARY, 03);
        //WHEN
        medicalRecord.setBirthdate(birthdayGiven);
        //THEN
        assertEquals(birthdayGiven, medicalRecord.getBirthdate());
    }

    @Order(9)
    @Test()
    void setMedications() {
        //GIVEN
        List<String> medicationsGiven = Arrays.asList("aznol:350mg","hydrapermazol:100mg");
        List<String> medicationsNull = null;
        //WHEN
        medicalRecord.setMedications(medicationsGiven);
        //THEN
        //******************************
        //****Check List Content********
        //******************************
        assertEquals(medicationsGiven, medicalRecord.getMedications());
        //**************************************************************************
        //****Check that list returns is not the same instance of list given********
        //**************************************************************************
        assertNotSame(medicationsGiven, medicalRecord.getMedications(),
                "ListGiven and ListReturned refer to same object");

        //WHEN
        medicalRecord.setMedications(medicationsNull);
        //THEN
        //*************************************
        //****Check List is not null********
        //*************************************
        assertNotNull(medicalRecord.getMedications(),
                "Null Given instead of List is stored in MedicalRecord Medications properties");
        //*******************************************
        //****Check List is Arraylist Type********
        //*******************************************
        assertTrue(medicalRecord.getMedications() instanceof ArrayList);
        //*******************************
        //****Check List is Empty********
        //*******************************
        assertTrue(medicalRecord.getMedications().isEmpty(),
                "Medications setter allows Null but have to create an empty list");
    }

    @Order(10)
    @Test()
    void setAllergies() {
        //GIVEN
        List<String> allergiesGiven = Arrays.asList("nillacilan");
        List<String> allergiesNull = null;
        //WHEN
        medicalRecord.setAllergies(allergiesGiven);
        //THEN
        assertEquals(allergiesGiven, medicalRecord.getAllergies());
        assertNotSame(allergiesGiven, medicalRecord.getAllergies(),
                "ListGiven and ListReturned refer to same object");

        //WHEN
        medicalRecord.setAllergies(allergiesNull);
        //THEN
        assertNotNull(medicalRecord.getAllergies(),
                "Null Given instead of List is stored in MedicalRecord Allergies properties");
        assertTrue(medicalRecord.getAllergies() instanceof ArrayList);
        assertTrue(medicalRecord.getAllergies().isEmpty(),
                "Allergies setter allows Null but have to create an empty list");
    }
}

//https://junit.org/junit5/docs/current/user-guide/
/** Kotlin File
 @Test
 fun `grouped assertions`() {
 assertAll("Medicalrecord properties",
 { assertEquals("John", medicalRecord.getFirstName()) },
 { assertEquals("Wallas", medicalRecord.getLastName()) },
 { assertEquals(LocalDate.of(2084, Month.MARCH, 06),
 medicalRecord.getBirthday());
 )
 }**/