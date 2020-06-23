package com.safetynet.alerts.configuration;

import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.web.client.RequestCallback;

import java.net.URL;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.skyscreamer.jsonassert.JSONAssert;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TraceRequestFilterIT {

    @LocalServerPort
    private int port;

    private URL base;

    @Autowired
    private TestRestTemplate template;

    @BeforeEach
    public void setUp() throws Exception {
        this.base = new URL("http://localhost:" + port + "/actuator");
        template.getForEntity(this.base.toURI(), String.class);
    }

    @Test
    public void actuatorHttpTraceEmptyEndPoint() throws Exception {

        //Test Rest Request on HttpTrace EndPoint
        ResponseEntity<String> result =  template.getForEntity(
                "http://localhost:" + port + "/actuator/httptrace", String.class);
        //**********************
        //***Test Code Status***
        //**********************
        assertEquals(200, result.getStatusCodeValue());
        //******************
        //***Test Content***
        //******************
        String content = result.getBody();
        //String expectedString = "{\"traces\":[]}";
        String expectedJson = "{traces:[null]}";
        //assertEquals(expectedString, content);
        JSONAssert.assertEquals(expectedJson, content, true);

    }
}

// org.skyscreamer.jsonassert.JSONAssert;
// http://jsonassert.skyscreamer.org/cookbook.html
// https://www.springboottutorial.com/unit-testing-for-spring-boot-rest-services

//https://howtodoinjava.com/spring-boot2/resttemplate/resttemplate-get-example/