package com.safetynet.alerts.dao;

import com.safetynet.alerts.models.Firestation;
import com.safetynet.alerts.utils.Jackson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FirestationDaoImplTest {

    private static Map<String, List<Firestation>> wrapperList = new HashMap<>();
    private static List<Firestation> firestationsGiven = new ArrayList<>();

    private IFirestationDAO firestationDAO;

    @Mock
    private RootFile rootFileGiven;

    @Mock
    private RootFile rootFileGivenBis;

    static {
        Firestation firestation1 = new Firestation("1509 Culver St","3");
        Firestation firestation2 = new Firestation("29 15th St", "2");
        firestationsGiven.add(firestation1);
        firestationsGiven.add(firestation2);
        wrapperList.put("firestations", firestationsGiven);
    }

    private Firestation firestationCreated = new Firestation("834 Binoc Ave", "3");
    private Firestation firestationNoSaved = new Firestation("Trafalgar Square", "7");

    @BeforeEach
    void setUp() throws IOException {
        //GIVEN
        when(rootFileGiven.getPath()).thenReturn("/url/file");
        //when(rootFileGiven.getBytes()).thenReturn("{'data':'infoByte'}".getBytes());
        String personsJson = Jackson.convertJavaToJson(wrapperList);
        when(rootFileGiven.getBytes()).thenReturn(personsJson.getBytes(StandardCharsets.UTF_8));
        //WHEN
        this.firestationDAO = new FirestationDaoImpl(rootFileGiven);
    }

    @AfterEach
    void tearDown() {
        this.firestationDAO = null;
    }

    @Order(1)
    @Test
    void getRootFile() {
        //THEN
        //******************************
        //Test Getter for RootFile
        //******************************
        assertNotNull(this.firestationDAO);
        assertNotNull(this.firestationDAO.getRootFile());
        assertEquals(rootFileGiven, this.firestationDAO.getRootFile());
        //******************************
        //Test Content of RootFile
        //******************************
        assertNotNull(this.firestationDAO.getRootFile().getPath());
        assertEquals(rootFileGiven.getPath(), this.firestationDAO.getRootFile().getPath());
        assertNotNull(this.firestationDAO.getRootFile().getBytes());
        assertEquals(rootFileGiven.getBytes(), this.firestationDAO.getRootFile().getBytes());
        assertArrayEquals(rootFileGiven.getBytes(), this.firestationDAO.getRootFile().getBytes());
    }

    @Order(2)
    @Test
    void setRootFile() {
        //GIVEN
        RootFile rootFileAtStart = this.firestationDAO.getRootFile();
        //WHEN
        this.firestationDAO.setRootFile(rootFileGivenBis);
        //THEN
        assertNotEquals(rootFileAtStart, this.firestationDAO.getRootFile());
    }

    @Order(3)
    @Test
    void getFirestationList() {
        //THEN
        assertNotNull(firestationDAO);
        assertNotNull(firestationDAO.getFirestationList());
        assertEquals(firestationsGiven.size(), firestationDAO.getFirestationList().size());
        assertEquals(firestationsGiven.get(0), firestationDAO.getFirestationList().get(0));
        assertEquals(firestationsGiven.get(1), firestationDAO.getFirestationList().get(1));
    }

    @Order(4)
    @Test
    void setFirestationList() {
        //WHEN
        firestationDAO.setFirestationList(null);
        //THEN
        assertNull(firestationDAO.getFirestationList());

        //WHEN
        firestationDAO.setFirestationList(List.of(new Firestation()));

        //THEN
        assertNotNull(firestationDAO.getFirestationList());
        assertEquals(1, firestationDAO.getFirestationList().size());
    }

    @DisplayName("Check data in DAO (fixture)")
    @Order(5)
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
        assertEquals(firestationsGiven.size(), firestationList.size());
        assertEquals(firestationsGiven.get(0), firestationList.get(0));
        assertEquals(firestationsGiven.get(1), firestationList.get(1));
        //************************************************************
        //check that list returned by method is place on the good place in DAO
        assertNotNull(firestationDAO.getFirestationList());
        assertEquals(firestationList.size(), firestationDAO.getFirestationList().size());
        assertEquals(firestationList.get(0), firestationDAO.getFirestationList().get(0));
        assertEquals(firestationList.get(1), firestationDAO.getFirestationList().get(1));

        /**We have checked that Firestation constructor contain this:
         firestationDAO.setFirestationList(Jackson.convertJsonRootDataToJava(
         this.getRootFile().getBytes(),
         "firestations",
         Firestation.class)
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
            this.firestationDAO = new FirestationDaoImpl(rootFileGiven);
        });
        assertTrue(exception.getMessage().contains("unexpected file data"));
        //https://www.baeldung.com/junit-assert-exception
    }

    @Order(7)
    @Test
    void findAll() {
        //THEN
        assertNotNull(firestationDAO);
        assertNotNull(firestationDAO.getFirestationList());
        assertNotNull(firestationDAO.findAll());
        assertEquals(firestationDAO.getFirestationList(), firestationDAO.findAll());
        assertEquals(firestationsGiven.size(), firestationDAO.findAll().size());
        assertEquals(firestationsGiven.get(0), firestationDAO.findAll().get(0));
        assertEquals(firestationsGiven.get(1), firestationDAO.findAll().get(1));
    }

    @Order(8)
    @Test
    void findByAddress() {
        //Given
        Firestation firestationExpected = firestationsGiven.get(1);
        String addressGiven = firestationExpected.getAddress();
        //When
        Firestation firestationResult = firestationDAO.findByAddress(addressGiven);
        //Then
        assertEquals(firestationExpected, firestationResult);
    }

    @Order(9)
    @Test
    void save() {
        assertEquals(firestationsGiven.size(), firestationDAO.findAll().size());
        //WHEN
        firestationDAO.save(firestationCreated);
        //THEN
        assertNotEquals(firestationsGiven.size(), firestationDAO.findAll().size());
        assertEquals(firestationsGiven.size()+1, firestationDAO.findAll().size());
        Firestation firestationSaved = firestationDAO.findByAddress(firestationCreated.getAddress());
        assertEquals(firestationCreated, firestationSaved);
    }

    @Order(10)
    @Test
    void update_error() {
        //*******************************
        //Case when firestation not exist
        //*******************************
        //When
        Firestation firestationUpdated = firestationDAO.update(firestationNoSaved);
        //Then
        assertNull(firestationUpdated);
    }

    @Order(11)
    @Test
    void update() {
        //****************************
        //Case when firestation exists
        //****************************
        //Given
        firestationDAO.save(firestationCreated);
        String oldStation = firestationCreated.getStation();
        String newStation = "5";
        firestationCreated.setStation(newStation);

        //When
        Firestation firestationUpdated = firestationDAO.update(firestationCreated);
        //Then
        assertNotNull(firestationUpdated);
        assertEquals(newStation, firestationUpdated.getStation());
        firestationUpdated = firestationDAO.findByAddress(firestationCreated.getAddress());
        assertEquals(newStation, firestationUpdated.getStation());
    }

    @Order(12)
    @Test
    void delete_error() {
        //*******************************
        //Case when firestation not exist
        //*******************************
        //When
        boolean deleteSuccess = firestationDAO.delete(firestationNoSaved);
        //Then
        assertFalse(deleteSuccess);
    }

    @Order(13)
    @Test
    void delete() {
        //****************************
        //Case when firestation exists
        //****************************
        assertEquals(firestationsGiven.size(), firestationDAO.findAll().size());
        Firestation firestationToDelete = firestationDAO.findAll().get(1);
        //When
        boolean deleteSuccess = firestationDAO.delete(firestationToDelete);
        //Then
        assertTrue(deleteSuccess);
        assertEquals(firestationsGiven.size()-1, firestationDAO.findAll().size());
        Firestation firestationSearch = firestationDAO.findByAddress(firestationToDelete.getAddress());
        assertNull(firestationSearch);
    }

}