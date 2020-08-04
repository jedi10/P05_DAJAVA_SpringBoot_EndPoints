package com.safetynet.alerts.dao;

import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.utils.Jackson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MedicalRecordDaoImplTest {

    private static Map<String, List<MedicalRecord>> wrapperList = new HashMap<>();
    private static List<MedicalRecord> medicalsGiven = new ArrayList<>();

    private IMedicalRecordDAO medicalRecordDAO;

    @Mock
    private RootFile rootFileGiven;

    @Mock
    private RootFile rootFileGivenBis;

    private static List<String> medicationList = new ArrayList<>();
    private static List<String> allergiesList = new ArrayList<>();
    static {
        medicationList.add("aznol:350mg"); medicationList.add("hydrapermazol:100mg");
        allergiesList.add("nillacilan");
        //https://howtodoinjava.com/java/date-time/java-time-localdate-class/
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-d-yyyy");
        MedicalRecord medicalRecord1 = new MedicalRecord("john", "boyd",
                LocalDate.of(1984, 3, 6),
                medicationList, allergiesList);
        medicationList.clear();
        medicationList.add("pharmacol:5000mg"); medicationList.add("terazine:10mg"); medicationList.add("noznazol:250mg");
        allergiesList.clear();
        MedicalRecord medicalRecord2 = new MedicalRecord("jacob", "boyd",
                LocalDate.of(1989, 3, 6),
                medicationList, allergiesList);
        medicalsGiven.add(medicalRecord1);
        medicalsGiven.add(medicalRecord2);
        wrapperList.put("medicalrecords", medicalsGiven);
        medicationList.clear();
        allergiesList.clear();
        medicationList.add("pharmacol:1000mg"); medicationList.add("terazine:115mg");
        allergiesList.add("penicillium");
    }

    private MedicalRecord medicalRecordCreated = new MedicalRecord("Tom", "Bezof",
            LocalDate.of(1959, 7, 30),
            medicationList, allergiesList);

    private MedicalRecord medicalRecordNotSaved = new MedicalRecord("Tom", "Bezof",
            LocalDate.of(1959, 7, 30));

    @BeforeEach
    void setUp() throws IOException {
        //GIVEN
        when(rootFileGiven.getPath()).thenReturn("/url/file");
        //when(rootFileGiven.getBytes()).thenReturn("{'data':'infoByte'}".getBytes());
        String personsJson = Jackson.convertJavaToJson(wrapperList);
        when(rootFileGiven.getBytes()).thenReturn(personsJson.getBytes(StandardCharsets.UTF_8));
        //WHEN
        this.medicalRecordDAO = new MedicalRecordDaoImpl(rootFileGiven);
    }

    @AfterEach
    void tearDown() {
        this.medicalRecordDAO = null;
    }

    @Order(1)
    @Test
    void getRootFile() {
        //THEN
        //******************************
        //Test Getter for RootFile
        //******************************
        assertNotNull(this.medicalRecordDAO);
        assertNotNull(this.medicalRecordDAO.getRootFile());
        assertEquals(rootFileGiven, this.medicalRecordDAO.getRootFile());
        //******************************
        //Test Content of RootFile
        //******************************
        assertNotNull(this.medicalRecordDAO.getRootFile().getPath());
        assertEquals(rootFileGiven.getPath(), this.medicalRecordDAO.getRootFile().getPath());
        assertNotNull(this.medicalRecordDAO.getRootFile().getBytes());
        assertEquals(rootFileGiven.getBytes(), this.medicalRecordDAO.getRootFile().getBytes());
        assertArrayEquals(rootFileGiven.getBytes(), this.medicalRecordDAO.getRootFile().getBytes());
    }

    @Order(2)
    @Test
    void setRootFile() {
        //GIVEN
        RootFile rootFileAtStart = this.medicalRecordDAO.getRootFile();
        //WHEN
        this.medicalRecordDAO.setRootFile(rootFileGivenBis);
        //THEN
        assertNotEquals(rootFileAtStart,this.medicalRecordDAO.getRootFile());
    }

    @Order(3)
    @Test
    void getMedicalRecordList() {
        //THEN
        assertNotNull(medicalRecordDAO);
        assertNotNull(medicalRecordDAO.getMedicalRecordList());
        assertEquals(medicalsGiven.size(),
                medicalRecordDAO.getMedicalRecordList().size());
        assertEquals(medicalsGiven.get(0),
                medicalRecordDAO.getMedicalRecordList().get(0));
        assertEquals(medicalsGiven.get(1),
                medicalRecordDAO.getMedicalRecordList().get(1));
    }

    @Order(4)
    @Test
    void setMedicalRecordList() {
        //WHEN
        medicalRecordDAO.setMedicalRecordList(null);
        //THEN
        assertNull(medicalRecordDAO.getMedicalRecordList());

        //WHEN
        medicalRecordDAO.setMedicalRecordList(List.of(new MedicalRecord()));
        //THEN
        assertNotNull(medicalRecordDAO.getMedicalRecordList());
        assertEquals(1, medicalRecordDAO.getMedicalRecordList().size());
    }

    @DisplayName("Check data in DAO (fixture)")
    @Order(5)
    @Test
    void convertJsonRootDataToJavaTest() throws IOException {
        //GIVEN
        //Medical Record List as we should find in DAO Medical Record List
        List<MedicalRecord> medicalList = Jackson.convertJsonRootDataToJava(
                this.medicalRecordDAO.getRootFile().getBytes(),
                "medicalrecords",
                MedicalRecord.class);
        //THEN
        //**************************************
        //check if method build list as intended
        assertNotNull(medicalList);
        assertEquals(medicalsGiven.size(), medicalList.size());
        assertEquals(medicalsGiven.get(0), medicalList.get(0));
        assertEquals(medicalsGiven.get(1), medicalList.get(1));
        //************************************************************
        //check that list returned by method is place on the good place in DAO
        assertNotNull(medicalRecordDAO.getMedicalRecordList());
        assertEquals(medicalList.size(), medicalRecordDAO.getMedicalRecordList().size());
        assertEquals(medicalList.get(0), medicalRecordDAO.getMedicalRecordList().get(0));
        assertEquals(medicalList.get(1), medicalRecordDAO.getMedicalRecordList().get(1));

        /**We have checked that Person constructor contain this:
         medicalRecordDAO.setMedicalRecordList(Jackson.convertJsonRootDataToJava(
         this.getRootFile().getBytes(),
         "medicalrecords",
         MedicalRecord.class)
         );**/
    }

    @DisplayName("Check Exception when DAO load wrong data")
    @Order(6)
    @Test
    void convertJsonRootDataToJavaErrorTest() throws IOException {
        //GIVEN
        when(rootFileGiven.getPath()).thenReturn("src/test/resources/testWrongData.json");
        //when(rootFileGiven.getBytes()).thenReturn("{'data':'infoByte'}".getBytes());
        byte[] fileError = Files.readAllBytes(Paths.get(rootFileGiven.getPath()));
        when(rootFileGiven.getBytes()).thenReturn(fileError);
        //WHEN-THEN
        Exception exception = assertThrows(IOException.class, ()-> {
            this.medicalRecordDAO = new MedicalRecordDaoImpl(rootFileGiven);
        });
        assertTrue(exception.getMessage().contains("unexpected file data"));
        //https://www.baeldung.com/junit-assert-exception
    }

    @Order(7)
    @Test
    void findAll() {
        //THEN
        assertNotNull(medicalRecordDAO);
        assertNotNull(medicalRecordDAO.getMedicalRecordList());
        assertNotNull(medicalRecordDAO.findAll());
        assertEquals(medicalRecordDAO.getMedicalRecordList(), medicalRecordDAO.findAll());
        assertEquals(medicalsGiven.size(), medicalRecordDAO.findAll().size());
        assertEquals(medicalsGiven.get(0), medicalRecordDAO.findAll().get(0));
        assertEquals(medicalsGiven.get(1), medicalRecordDAO.findAll().get(1));
    }

    @Order(8)
    @Test
    void findByName() {
        //Given
        MedicalRecord medicalRecordExpected = medicalsGiven.get(1);
        String firstNameGiven = medicalRecordExpected.getFirstName();
        String lastNameGiven = medicalRecordExpected.getLastName();
        //When
        MedicalRecord medicalRecordResult = medicalRecordDAO.findByName(
                firstNameGiven, lastNameGiven);
        //Then
        assertEquals(medicalRecordExpected, medicalRecordResult);
    }

    @Order(9)
    @Test
    void save() {
        assertEquals(medicalsGiven.size(), medicalRecordDAO.findAll().size());
        //WHEN
        medicalRecordDAO.save(medicalRecordCreated);
        //THEN
        assertNotEquals(medicalsGiven.size(), medicalRecordDAO.findAll().size());
        assertEquals(medicalsGiven.size()+1, medicalRecordDAO.findAll().size());
        MedicalRecord medicalRecordSaved = medicalRecordDAO.findByName(
                medicalRecordCreated.getFirstName(),
                medicalRecordCreated.getLastName());
        assertEquals(medicalRecordCreated, medicalRecordSaved);
    }

    @Order(10)
    @Test
    void update_error() {
        //**********************************
        //Case when medical record not exist
        //**********************************
        //When
        MedicalRecord medicalRecordUpdated = medicalRecordDAO.update(medicalRecordNotSaved);
        //Then
        assertNull(medicalRecordUpdated);
    }

    @Order(11)
    @Test
    void update() {
        //*******************************
        //Case when medical Record exists
        //*******************************
        //Given
        medicalRecordDAO.save(medicalRecordCreated);
        List<String> oldMedicationList = medicalRecordCreated.getMedications();
        List<String> newMedicationList = new ArrayList<>();
        newMedicationList.add(oldMedicationList.get(0));
        newMedicationList.add("azithromycine 4Mg");
        newMedicationList.add("hydroxycholoriquine 2cp");
        newMedicationList.add("zinc");

        medicalRecordCreated.setMedications(newMedicationList);

        //When
        MedicalRecord medicalRecordUpdated = medicalRecordDAO.update(medicalRecordCreated);
        //Then
        assertNotNull(medicalRecordUpdated);
        assertEquals(newMedicationList, medicalRecordUpdated.getMedications());
        medicalRecordUpdated = medicalRecordDAO.findByName(
                medicalRecordCreated.getFirstName(),
                medicalRecordCreated.getLastName());
        assertEquals(newMedicationList, medicalRecordUpdated.getMedications());
    }

    @Order(12)
    @Test
    void delete_error() {
        //**********************************
        //Case when medical Record not exist
        //**********************************
        //When
        boolean deleteSuccess = medicalRecordDAO.delete(medicalRecordNotSaved);
        //Then
        assertFalse(deleteSuccess);
    }

    @Order(13)
    @Test
    void delete() {
        //*******************************
        //Case when medical Record exists
        //*******************************
        assertEquals(medicalsGiven.size(), medicalRecordDAO.findAll().size());
        MedicalRecord medicalRecordToDelete = medicalRecordDAO.findAll().get(1);
        //When
        boolean deleteSuccess = medicalRecordDAO.delete(medicalRecordToDelete);
        //Then
        assertTrue(deleteSuccess);
        assertEquals(medicalsGiven.size()-1, medicalRecordDAO.findAll().size());
        MedicalRecord medicalRecordSearch = medicalRecordDAO.findByName(
                medicalRecordToDelete.getFirstName(),
                medicalRecordToDelete.getLastName());
        assertNull(medicalRecordSearch);
    }


}