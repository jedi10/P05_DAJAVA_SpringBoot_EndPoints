package com.safetynet.alerts.models;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FirestationTest {

    private Firestation firestation;

    @BeforeAll
    void setUp() {
        //GIVEN
        firestation = new Firestation(
                "1509 Culver St",
                "3");
    }

    @AfterAll
    void tearDown() {
        firestation = null;
    }

    @Order(1)
    @Test
    void getAddress() {
        //WHEN
        String dataFromObject = firestation.getAddress();
        //THEN
        assertEquals("1509 Culver St", dataFromObject);
    }

    @Order(2)
    @Test
    void getStation() {
        //WHEN
        String dataFromObject = firestation.getStation();
        //THEN
        assertEquals("3", dataFromObject);
    }

    @Order(3)
    @ParameterizedTest
    @ValueSource(strings = { "29 15th St" })
    void setAddress(String addressGiven) {
        //WHEN
        firestation.setAddress(addressGiven);
        //THEN
        assertEquals(addressGiven, firestation.getAddress());
    }

    @Order(4)
    @ParameterizedTest
    @ValueSource(strings = { "2" })
    void setStation(String stationGiven) {
        //WHEN
        firestation.setStation(stationGiven);
        //THEN
        assertEquals(stationGiven, firestation.getStation());
    }
}