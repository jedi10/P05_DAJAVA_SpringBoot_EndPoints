package com.safetynet.alerts.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class TraceRequestFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    private void setUp() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator")
                .accept(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    public void actuatorHttpTraceEmptyEndPoint() throws Exception {

        //Test Rest Request on HttpTrace EndPoint
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/httptrace")
                .accept(MediaType.APPLICATION_JSON))//.andDo(print());
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.traces").isNotEmpty())
                //.andExpect(jsonPath("$.traces", hasSize(1)))//Test can Fail because we can have several traces
                .andExpect(jsonPath("$.traces[0]").isNotEmpty())
                .andExpect(jsonPath("$.traces[0].response.status").value("200"))
                .andExpect(jsonPath("$.traces[0].response.headers.Content-Length[0]").doesNotExist());

                /**Other way to do a Test
                 * RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/actuator/httptrace")
                  .accept(MediaType.APPLICATION_JSON);
                 MvcResult result = mockMvc.perform(requestBuilder).andReturn();
                 String resultResponse = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                 String expected = "{id:Course1,name:Spring,description:10 Steps}";**/
    }
}


//https://www.springboottutorial.com/unit-testing-for-spring-boot-rest-services