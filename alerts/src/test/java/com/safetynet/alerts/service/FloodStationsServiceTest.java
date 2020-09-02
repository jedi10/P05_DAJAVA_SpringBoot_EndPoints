package com.safetynet.alerts.service;

import com.safetynet.alerts.dao.IFirestationDAO;
import com.safetynet.alerts.dao.IMedicalRecordDAO;
import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.models.Firestation;
import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.service.rto_models.IFirestationAreaRTO;
import com.safetynet.alerts.service.rto_models.IPersonInfoRTO;
import com.safetynet.alerts.service.rto_models.PersonInfoRTO;
import com.safetynet.alerts.utils.Jackson;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FloodStationsServiceTest {

    @Autowired
    private FloodStationsService floodStationsService;

    @Mock
    IPersonDAO personDAO;

    @Mock
    IMedicalRecordDAO medicalRecordDAO;

    @Mock
    IFirestationDAO firestationDAO;

    List<IPersonInfoRTO> personInfoRTOList;

    private List<Person> personList;

    private List<MedicalRecord> medicalRecordList;

    private List<Firestation> firestationList;

    @BeforeEach
    void setUp() throws IOException {
        String fileString = Files.readString(Paths.get("src/test/resources/testData.json"));
        byte[] fileBytes = fileString.getBytes(StandardCharsets.UTF_8);
        this.personList = Jackson.convertJsonRootDataToJava(
                fileBytes,
                "persons",
                Person.class);
        this.medicalRecordList = Jackson.convertJsonRootDataToJava(
                fileBytes,
                "medicalrecords",
                MedicalRecord.class);
        this.firestationList = Jackson.convertJsonRootDataToJava(
                fileBytes,
                "firestations",
                Firestation.class);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getFloddStations_OK() {
        //GIVEN
        List<String> stationNumberList = List.of("3","4");
        //*********************check data used in test*****************************
        assertNotNull(this.personList,
                "PersonList is Null: we need it for further tests");
        assertTrue(this.personList.size()>2);
        assertNotNull(this.medicalRecordList,
                "MedicalRecordList is Null: we need it for further tests");
        assertTrue(this.medicalRecordList.size()>2);
        assertNotNull(this.firestationList,
                "FirestationList is Null: we need it for further tests");
        assertTrue(this.firestationList.size()>2);

        personInfoRTOList =  PersonInfoRTO.buildPersonInfoRTOList(this.personList, this.medicalRecordList);

        //*****************Get Address List linked to station number******************
        List<String> listAddressResult = this.firestationList.stream()
                .filter(e-> stationNumberList.contains(e.getStation()))
                .distinct()
                .map(Firestation::getAddress)
                .collect(Collectors.toList());
        //Filtering list
        Map<String,List<IPersonInfoRTO>> expectedPersonRTOMap = personInfoRTOList.stream()
                .filter(o ->
                        listAddressResult.contains(o.getAddress())
                )
                .collect(Collectors.groupingBy(IPersonInfoRTO::getAddress));

        //************************************************
        //Mocks Configuration
        //************************************************
        when(personDAO.findAll()).thenReturn(this.personList);
        when(medicalRecordDAO.findAll()).thenReturn(this.medicalRecordList);
        when(firestationDAO.findAll()).thenReturn(this.firestationList);

        //************************************************
        //DATA available via Mock DAO injection in Service
        //************************************************
        //Mock injection
        floodStationsService = new FloodStationsService(
                this.personDAO,
                this.medicalRecordDAO,
                this.firestationDAO);

        //***********************************************************
        //***************CHECK MOCK INVOCATION at start**************
        //***********************************************************
        verify(personDAO, Mockito.never()).findAll();
        verify(medicalRecordDAO, Mockito.never()).findAll();
        verify(firestationDAO, Mockito.never()).findAll();

        //WHEN
        Map<String,List<IPersonInfoRTO>> objectListResult =
                floodStationsService.getFloodStations(stationNumberList);

        //THEN
        //***********************************************************
        //***************CHECK MOCK INVOCATION at end****************
        //***********************************************************
        verify(personDAO, Mockito.times(1)).findAll();
        verify(medicalRecordDAO, Mockito.times(1)).findAll();
        verify(firestationDAO, Mockito.times(1)).findAll();

        assertNotNull(objectListResult);
        //********************************
        //Check Content for PersonRTO List
        //********************************
        assertEquals(expectedPersonRTOMap, objectListResult);
    }

    static Stream<Arguments> stationNoExistData() {
        return Stream.of(
                Arguments.of(Arrays.asList("0", "404"))
        );
    }

    @Order(2)
    @ParameterizedTest
    @MethodSource("stationNoExistData")
    void getFloodStations_NoStationAddress(List<String> stationNumberList) {
        //GIVEN
        //*********************check data used in test*****************************
        assertNotNull(this.personList,
                "PersonList is Null: we need it for further tests");
        assertTrue(this.personList.size()>2);
        assertNotNull(this.medicalRecordList,
                "MedicalRecordList is Null: we need it for further tests");
        assertTrue(this.medicalRecordList.size()>2);
        assertNotNull(this.firestationList,
                "FirestationList is Null: we need it for further tests");
        assertTrue(this.firestationList.size()>2);

        personInfoRTOList =  PersonInfoRTO.buildPersonInfoRTOList(this.personList, this.medicalRecordList);

        //*****************Get Address List linked to station number******************
        //*****************Get Address List linked to station number******************
        List<String> listAddressResult = this.firestationList.stream()
                .filter(e-> stationNumberList.contains(e.getStation()))
                .distinct()
                .map(Firestation::getAddress)
                .collect(Collectors.toList());
        assertTrue(listAddressResult.isEmpty());

        //************************************************
        //Mocks Configuration
        //************************************************
        when(personDAO.findAll()).thenReturn(this.personList);
        when(medicalRecordDAO.findAll()).thenReturn(this.medicalRecordList);
        when(firestationDAO.findAll()).thenReturn(this.firestationList);

        //************************************************
        //DATA available via Mock DAO injection in Service
        //************************************************
        //Mock injection
        floodStationsService = new FloodStationsService(
                this.personDAO,
                this.medicalRecordDAO,
                this.firestationDAO);

        //***********************************************************
        //***************CHECK MOCK INVOCATION at start**************
        //***********************************************************
        verify(personDAO, Mockito.never()).findAll();
        verify(medicalRecordDAO, Mockito.never()).findAll();
        verify(firestationDAO, Mockito.never()).findAll();

        //WHEN
        Map<String,List<IPersonInfoRTO>> objectListResult =
                floodStationsService.getFloodStations(stationNumberList);

        //THEN
        //***********************************************************
        //***************CHECK MOCK INVOCATION at end****************
        //***********************************************************
        verify(personDAO, Mockito.never()).findAll();
        verify(medicalRecordDAO, Mockito.never()).findAll();
        verify(firestationDAO, Mockito.times(1)).findAll();

        assertNull(objectListResult);
    }

    @Order(3)
    @ParameterizedTest
    @NullSource
    void getFloodStations_NullParam(List<String> stationNumberList) {
        //WHEN-THEN
        Exception exception = assertThrows(NullPointerException.class, () -> {
            floodStationsService.getFloodStations(stationNumberList);
        });
        assertTrue(exception.getMessage().contains(
                "stationNumberList is marked non-null but is null"));

        verify(personDAO, Mockito.never()).findAll();
        verify(medicalRecordDAO, Mockito.never()).findAll();
        verify(firestationDAO, Mockito.never()).findAll();
    }
}