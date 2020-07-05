package com.safetynet.alerts.rest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration Test for Custom Error Controller
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomErrorControllerIT {

    private final String URL_TO_TEST = "/wrongurl";

    @LocalServerPort
    private int port;

    private URL base;

    @Autowired
    private TestRestTemplate template;

    @BeforeAll
    public void setUp() throws Exception {
        //****************GIVEN*****************
        this.base = new URL("http://localhost:" + port + URL_TO_TEST);
    }

   @BeforeEach
    public void setUpEach() throws Exception {

    }

    @Test
    public void handleErrorTest() throws Exception {
        //****************WHEN*****************
        //****Load BAD URL with no Mapping ****
        //*************************************
        ResponseEntity<String> result =  template.getForEntity(
                this.base.toURI(), String.class);
        //***********THEN************
        //****Test JSON Response ****
        //***************************
        assertNotNull(result);
        assertEquals(404, result.getStatusCodeValue());
        String content = result.getBody();
        String expectedJson =
                "{status: 404," +
                " path:'"+URL_TO_TEST+"'}";
        JSONAssert.assertEquals(expectedJson, content, false);
    }
}