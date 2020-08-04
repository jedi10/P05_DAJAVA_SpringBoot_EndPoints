package com.safetynet.alerts.dao;

import com.safetynet.alerts.configuration.AlertsProperties;
import com.safetynet.alerts.models.Firestation;
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
class FirestationDaoImplIT {

    @Autowired
    private IFirestationDAO firestationDAO;

    @Autowired
    private AlertsProperties alertsProperties;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {

    }

    @Order(1)
    @Test
    void getRootFile() throws IOException {
        //THEN
        //******************************
        //Test Getter for RootFile
        //******************************
        assertNotNull(this.firestationDAO);
        assertNotNull(this.firestationDAO.getRootFile());

        //******************************
        //Test Content of RootFile
        //******************************
        //THEN for PATH
        assertNotNull(this.firestationDAO.getRootFile().getPath());
        assertNotNull(alertsProperties);
        assertNotNull(alertsProperties.getJsonFilePath());
        assertEquals(alertsProperties.getJsonFilePath(), this.firestationDAO.getRootFile().getPath());

        //GIVEN
        byte[] fileBytesExpected = Files.readAllBytes(Paths.get(alertsProperties.getJsonFilePath()));
        //THEN for BYTE
        assertNotNull(this.firestationDAO.getRootFile().getBytes());
        assertEquals(fileBytesExpected.length, this.firestationDAO.getRootFile().getBytes().length);
        assertArrayEquals(fileBytesExpected, this.firestationDAO.getRootFile().getBytes());
    }


    @DisplayName("Check data in DAO")
    @Order(2)
    @Test
    void convertJsonRootDataToJavaTest() throws IOException {
        //GIVEN
        //Firestation List as we should find in DAO Firestation List
        List<Firestation> firestationList = Jackson.convertJsonRootDataToJava(
                this.firestationDAO.getRootFile().getBytes(),
                "firestations",
                Firestation.class);
        //THEN
        //**************************************
        //check if method build list as intended
        assertNotNull(firestationList);
        assertNotNull(firestationDAO.getFirestationList());
        //************************************************************
        //check that list returned by method is place on the good place in DAO
        assertEquals(firestationList.size(), firestationDAO.getFirestationList().size());
        assertEquals(firestationList.get(0), firestationDAO.getFirestationList().get(0));
        assertEquals(
                firestationList.get(firestationList.size()-1),
                firestationDAO.getFirestationList().get(firestationList.size()-1));

        /**We have checked that Firestation constructor contain this:
         firestationDAO.setFirestationList(Jackson.convertJsonRootDataToJava(
         this.getRootFile().getBytes(),
         "firestations",
         Firestation.class)
         );**/
    }
}