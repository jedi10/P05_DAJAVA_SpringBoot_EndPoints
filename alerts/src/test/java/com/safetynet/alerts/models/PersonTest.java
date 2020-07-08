package com.safetynet.alerts.models;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonTest {

    private Person person;

    @BeforeAll
    void setUp() {
        //GIVEN
        person = new Person("John",
                "Wallas",
                "1509 Culver St",
                "Culver",
                97451,
                "841-874-6512",
                "jwallas@email.com");
    }

    @AfterAll
    void tearDown() {
        person = null;
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
    @ParameterizedTest()
    @ValueSource(strings = { "Lilou" })
    void setFirstName(String firstNameGiven) {

        //WHEN
        person.setFirstName(firstNameGiven);
        //THEN
        assertEquals(firstNameGiven, person.getFirstName());
    }

    @Order(9)
    @ParameterizedTest()
    @ValueSource(strings = { "Undefined" })
    void setLastName(String lastNameGiven) {
        //WHEN
        person.setLastName(lastNameGiven);
        //THEN
        assertEquals(lastNameGiven, person.getLastName());
    }

    @Order(10)
    @ParameterizedTest()
    @ValueSource(strings = { "Benedictine Church" })
    void setAddress(String addressGiven) {
        //WHEN
        person.setAddress(addressGiven);
        //THEN
        assertEquals(addressGiven, person.getAddress());
    }

    @Order(11)
    @ParameterizedTest()
    @ValueSource(strings = { "Nivalis" })
    void setCity(String cityGiven) {
        //WHEN
        person.setCity(cityGiven);
        //THEN
        assertEquals(cityGiven, person.getCity());
    }

    @Order(12)
    @ParameterizedTest()
    @ValueSource(ints = { 12345 })
    void setZip(int cityGiven) {
        //WHEN
        person.setZip(cityGiven);
        //THEN
        assertEquals(cityGiven, person.getZip());
    }

    @Order(12)
    @ParameterizedTest()
    @ValueSource(strings = { "841-874-6513" })
    void setPhone(String phoneGiven) {
        //WHEN
        person.setPhone(phoneGiven);
        //THEN
        assertEquals(phoneGiven, person.getPhone());
    }

    @Order(13)
    @ParameterizedTest()
    @ValueSource(strings = { "lilou@email.com" })
    void setEmail(String emailGiven) {
        //WHEN
        person.setEmail(emailGiven);
        //THEN
        assertEquals(emailGiven, person.getEmail());
    }

}