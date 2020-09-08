package com.safetynet.alerts.configuration;

import org.json.JSONArray;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RequestCallback;

import java.net.URL;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TraceRequestFilterIT {

    @LocalServerPort
    private int port;

    private URL base;

    @Autowired
    private TestRestTemplate template;

    private ResponseEntity<String> result;

    @BeforeEach
    public void setUp() throws Exception {
        //**************GIVEN**************
        this.base = new URL("http://localhost:" + port + "/actuator");
        template.getForEntity(this.base.toURI(), String.class);
        //**************WHEN****************
        //Test Rest Request on HttpTrace EndPoint
        this.result =  template.getForEntity(
                "http://localhost:" + port + "/actuator/httptrace", String.class);
    }

    @Test
    public void actuatorHttpTraceEmptyEndPoint() throws Exception {
        //***************THEN**************
        //*********Test Code Status********
        //*********************************
        assertEquals(200, result.getStatusCodeValue());
    }

    /*
    @Disabled
    @Test
    public void actuatorHttpTraceEmptyEndPointContentResponse() throws Exception {
        //************************THEN*************************
        //*********************Test Content********************
        //ABORTED: This test need a trace reset before the test
        // or to be the first Test to be executed
        //*****************************************************
        String content = result.getBody();
        //String expectedString = "{\"traces\":[]}";
        String expectedJson = "{traces:[null]}";
        //assertEquals(expectedString, content);
        JSONAssert.assertEquals(expectedJson, content, true);
    }*/
}

// org.skyscreamer.jsonassert.JSONAssert;
// http://jsonassert.skyscreamer.org/cookbook.html
// https://www.springboottutorial.com/unit-testing-for-spring-boot-rest-services

//https://howtodoinjava.com/spring-boot2/resttemplate/resttemplate-get-example/

//https://mkyong.com/junit5/junit-5-test-execution-order/