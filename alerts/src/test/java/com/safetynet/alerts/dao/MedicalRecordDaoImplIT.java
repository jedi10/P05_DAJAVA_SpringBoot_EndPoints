package com.safetynet.alerts.dao;

import com.safetynet.alerts.configuration.AlertsProperties;
import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.utils.Jackson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MedicalRecordDaoImplIT {

    @Autowired
    private IMedicalRecordDAO medicalRecordDAO;

    @Autowired
    private AlertsProperties alertsProperties;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
        this.medicalRecordDAO = null;
    }

    @Order(1)
    @Test
    void getRootFile() throws IOException {
        //THEN
        //******************************
        //Test Getter for RootFile
        //******************************
        assertNotNull(this.medicalRecordDAO);
        assertNotNull(this.medicalRecordDAO.getRootFile());

        //******************************
        //Test Content of RootFile
        //******************************
        //THEN for PATH
        assertNotNull(this.medicalRecordDAO.getRootFile().getPath());
        assertNotNull(alertsProperties);
        assertNotNull(alertsProperties.getJsonFilePath());
        assertEquals(alertsProperties.getJsonFilePath(), this.medicalRecordDAO.getRootFile().getPath());

        //GIVEN
        byte[] fileBytesExpected = Files.readAllBytes(Paths.get(alertsProperties.getJsonFilePath()));
        //THEN for BYTE
        assertNotNull(this.medicalRecordDAO.getRootFile().getBytes());
        assertEquals(fileBytesExpected.length, this.medicalRecordDAO.getRootFile().getBytes().length);
        assertArrayEquals(fileBytesExpected, this.medicalRecordDAO.getRootFile().getBytes());
    }

    @DisplayName("Check data in DAO")
    @Order(2)
    @Test
    void convertJsonRootDataToJavaTest() throws IOException {
        //GIVEN
        //Medical Record List as we should find in DAO MedicalRecord List
        List<MedicalRecord> medicalList = Jackson.convertJsonRootDataToJava(
                this.medicalRecordDAO.getRootFile().getBytes(),
                "medicalrecords",
                MedicalRecord.class);
        //THEN
        //**************************************
        //check if method build list as intended
        assertNotNull(medicalList);
        assertNotNull(medicalRecordDAO.getMedicalRecordList());
        //************************************************************
        //check that list returned by method is place on the good place in DAO
        assertEquals(medicalList.size(), medicalRecordDAO.getMedicalRecordList().size());
        assertEquals(medicalList.get(0), medicalRecordDAO.getMedicalRecordList().get(0));
        assertEquals(
                medicalList.get(medicalList.size()-1),
                medicalRecordDAO.getMedicalRecordList().get(medicalList.size()-1));

        /**We have checked that Person constructor contain this:
         medicalRecordDAO.setMedicalRecordList(Jackson.convertJsonRootDataToJava(
         this.getRootFile().getBytes(),
         "medicalrecords",
         MedicalRecord.class)
         );**/
    }




}