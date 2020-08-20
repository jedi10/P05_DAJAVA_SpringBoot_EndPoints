package com.safetynet.alerts.service.rto_models;

import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.utils.Jackson;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonInfoRTOTest {

    private Person person;

    private MedicalRecord medicalRecord;

    private PersonInfoRTO personInfoRTO;

    @BeforeEach
    void setUp() throws Exception {
        //GIVEN
        person = new Person("John",
                "Wallas",
                "1509 Culver St",
                "Culver",
                97451,
                "841-874-6512",
                "jwallas@email.com");

        medicalRecord = new MedicalRecord("John",
                "Wallas",
                //https://www.oracle.com/technical-resources/articles/java/jf14-date-time.html
                LocalDate.of(2084, Month.MARCH, 06),
                Arrays.asList("pharmacol:5000mg","terazine:10mg","noznazol:250mg"),
                Arrays.asList("shellfish","peanut"));

        personInfoRTO = new PersonInfoRTO(person, medicalRecord);
    }

    @Order(1)
    @Test
    void getFirstName() {
        //WHEN
        String dataFromObject = person.getFirstName();
        //THEN
        assertEquals("John", dataFromObject);
    }

    @Order(2)
    @Test
    void getLastName() {
        //WHEN
        String dataFromObject = person.getLastName();
        //THEN
        assertEquals("Wallas", dataFromObject);
    }

    @Order(3)
    @Test
    void getAddress() {
        //WHEN
        String dataFromObject = person.getAddress();
        //THEN
        assertEquals("1509 Culver St", dataFromObject);
    }

    @Order(4)
    @Test
    void getCity() {
        //WHEN
        String dataFromObject = person.getCity();
        //THEN
        assertEquals("Culver", dataFromObject);
    }

    @Order(5)
    @Test
    void getZip() {
        //WHEN
        Integer dataFromObject = person.getZip();
        //THEN
        assertEquals(97451, dataFromObject);
    }

    @Order(6)
    @Test
    void getPhone() {
        //WHEN
        String dataFromObject = person.getPhone();
        //THEN
        assertEquals("841-874-6512", dataFromObject);
    }

    @Order(7)
    @Test
    void getEmail() {
        //WHEN
        String dataFromObject = person.getEmail();
        //THEN
        assertEquals("jwallas@email.com", dataFromObject);
    }

    @Order(8)
    @Test
    void getBirthdate() {
        //WHEN
        LocalDate dateFromObject = medicalRecord.getBirthdate();
        //THEN
        assertEquals(LocalDate.of(2084, Month.MARCH, 06),
                dateFromObject);
    }

    @Order(9)
    @Test()
    void getMedications() {
        //WHEN
        List dataFromObject = medicalRecord.getMedications();
        //THEN
        assertEquals(Arrays.asList("pharmacol:5000mg","terazine:10mg","noznazol:250mg"),
                dataFromObject);
    }

    @Order(10)
    @Test()
    void getAllergies() {
        //WHEN
        List dataFromObject = medicalRecord.getAllergies();
        //THEN
        assertEquals(Arrays.asList("shellfish","peanut"), dataFromObject);
    }

    @Order(11)
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
                "ListGiven and ListReturned refer to same object:" +
                        " Medications setter have to create a new List instance");

        //WHEN
        medicalRecord.setMedications(medicationsNull);
        //THEN
        //*************************************
        //****Check List is not null********
        //*************************************
        assertNotNull(medicalRecord.getMedications(),
                "Medications setter allows Null but have to create an empty list");
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

    @Order(12)
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
                "ListGiven and ListReturned refer to same object:" +
                        " Allergies setter have to create a new List instance");

        //WHEN
        medicalRecord.setAllergies(allergiesNull);
        //THEN
        assertNotNull(medicalRecord.getAllergies(),
                "Allergies setter allows Null but have to create an empty list");
        assertTrue(medicalRecord.getAllergies() instanceof ArrayList);
        assertTrue(medicalRecord.getAllergies().isEmpty(),
               "Allergies setter allows Null but have to create an empty list");
    }

    @Order(13)
    @Test()
    void constructorParamNull_Exception() {
        //WHEN-THEN
        Exception exception = assertThrows(Exception.class, ()-> {
            personInfoRTO = new PersonInfoRTO(person, null);
        });
        assertTrue(exception.getMessage().contains(
                "constructor need not Null person and medicalRecord"));


        exception = assertThrows(Exception.class, ()-> {
            personInfoRTO = new PersonInfoRTO(null, medicalRecord);
        });
        assertTrue(exception.getMessage().contains(
                "constructor need not Null person and medicalRecord"));
    }

    @Order(14)
    @Test()
    void constructorMismatchNameOnParam_Exception() {
        //GIVEN
        person.setFirstName("Chuck");

        //WHEN-THEN
        Exception exception = assertThrows(Exception.class, () -> {
            personInfoRTO = new PersonInfoRTO(person, medicalRecord);
        });
        assertTrue(exception.getMessage().contains(
                "need the same firstname and lastname properties"));
    }

    @Order(15)
    @Test()
    void buildPersonInfoRTOList_OK() throws IOException {
        //GIVEN
        String fileString = Files.readString(Paths.get("src/test/resources/testData.json"));
        byte[] fileBytes = fileString.getBytes(StandardCharsets.UTF_8);
        List<Person> personList;
        List<MedicalRecord> medicalRecordList;
        try {
            personList = Jackson.convertJsonRootDataToJava(
                    fileBytes,
                    "persons",
                    Person.class);
            medicalRecordList = Jackson.convertJsonRootDataToJava(
                    fileBytes,
                    "medicalrecords",
                    MedicalRecord.class);
        } catch (IOException e) {
            throw new IOException(e);
        }

        //WHEN
        List<PersonInfoRTO> personInfoRTOListResult =  PersonInfoRTO.buildPersonInfoRTOList(personList, medicalRecordList);

        //THEN
        assertNotNull(personInfoRTOListResult);
        assertEquals(personList.size(), personInfoRTOListResult.size());

        //WHEN
        Collections.reverse(personList);
        personInfoRTOListResult =  PersonInfoRTO.buildPersonInfoRTOList(personList, medicalRecordList);

        //THEN
        assertNotNull(personInfoRTOListResult);
        assertEquals(personList.size(), personInfoRTOListResult.size());
    }

    @Order(16)
    @Test()
    void buildPersonInfoRTOList_EmptyList() {
        //***********************************************
        //CHECK One Person with Empty Medical Record List
        //***********************************************
        //GIVEN
        List<Person> personList = new ArrayList<>();
        personList.add(person);
        List<MedicalRecord> medicalRecordList = new ArrayList<>();


        //WHEN
        List<PersonInfoRTO> personInfoRTOListResult =  PersonInfoRTO.buildPersonInfoRTOList(personList, medicalRecordList);

        //THEN
        assertNotNull(personInfoRTOListResult);
        assertTrue(personInfoRTOListResult.isEmpty());

        //*******************************************************
        //CHECK One Person with One Medical Record different name
        //*******************************************************
        //GIVEN
        medicalRecord.setLastName("differentName");
        medicalRecordList.add(medicalRecord);

        //WHEN
        personInfoRTOListResult =  PersonInfoRTO.buildPersonInfoRTOList(personList, medicalRecordList);

        //THEN
        assertNotNull(personInfoRTOListResult);
        assertTrue(personInfoRTOListResult.isEmpty());

    }
}