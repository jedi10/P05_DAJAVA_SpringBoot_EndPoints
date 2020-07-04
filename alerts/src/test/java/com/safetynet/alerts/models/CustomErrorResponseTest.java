package com.safetynet.alerts.models;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomErrorResponseTest {

    CustomErrorResponse customErrorResponse;
    String timestamp;

    @BeforeAll
    void setUpAll() {
        //GIVE
        timestamp = Timestamp.valueOf(now()).toString();
        //Since Java 9
        Map<String, Object> map = Map.of(
                "path","bad_page_url",
                "message","error page",
                "timestamp", timestamp,
                "trace","go to error page"
        );
        //WHEN
        customErrorResponse = new CustomErrorResponse(404, map );
    }

    @AfterAll
    void tearDown() {
        customErrorResponse = null;
    }

    @Order(1)
    @Test
    void constructor() {
        //THEN
        assertNotNull(customErrorResponse);
    }

    @Order(2)
    @Test
    void getStatus() {
        assertEquals(404, customErrorResponse.getStatus());
    }

    @Order(3)
    @Test
    void getPath() {
        assertEquals("bad_page_url", customErrorResponse.getPath());
    }

    @Order(4)
    @Test
    void getErrorMessage() {
        assertEquals("error page", customErrorResponse.getErrorMessage());
    }

    @Order(5)
    @Test
    void getTimeStamp() {
        assertEquals(timestamp, customErrorResponse.getTimeStamp());
    }

    @Order(6)
    @Test
    void getTrace() {
        assertEquals("go to error page", customErrorResponse.getTrace());
    }

    @Order(7)
    @Test
    void setStatus() {
        customErrorResponse.setStatus(200);
        assertEquals(200, customErrorResponse.getStatus());
    }

    @Order(8)
    @Test
    void setPath() {
        customErrorResponse.setPath("GoodPage");
        assertEquals("GoodPage", customErrorResponse.getPath());
    }

    @Order(9)
    @Test
    void setErrorMessage() {
        customErrorResponse.setErrorMessage("Toto crush the page");
        assertEquals("Toto crush the page", customErrorResponse.getErrorMessage());
    }

    @Order(10)
    @Test
    void setTimeStamp() {
        String expectedTimestamp = Timestamp.valueOf(now()).toString();
        customErrorResponse.setTimeStamp(expectedTimestamp);
        assertEquals(expectedTimestamp, customErrorResponse.getTimeStamp());
    }

    @Order(11)
    @Test
    void setTrace() {
        customErrorResponse.setTrace("go to error page provided by god");
        assertEquals("go to error page provided by god", customErrorResponse.getTrace());
    }
}