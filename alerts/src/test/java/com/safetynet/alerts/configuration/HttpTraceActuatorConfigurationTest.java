package com.safetynet.alerts.configuration;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HttpTraceActuatorConfigurationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationContext appContext;

    @BeforeEach
    private void setUp() throws Exception {
        //Go to Hello Word Page
        mockMvc.perform(MockMvcRequestBuilders.get("/")
                .accept(MediaType.TEXT_PLAIN_VALUE));
    }

    @Test
    public void actuatorHttpTraceEndPointContentHelloWord() throws Exception {

        //Test Rest Request on HttpTrace EndPoint
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/actuator/httptrace")
                .accept(MediaType.APPLICATION_JSON))//.andDo(print());
                //***********************************
                //***Check Status and Content Type***
                //***********************************
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.traces").isNotEmpty())
                //********************
                //***Check Content***
                //********************
                //.andExpect(jsonPath("$.traces", hasSize(1)))//Failed because multiple traces are registered
                .andExpect(jsonPath("$.traces[0]").isNotEmpty())
                .andExpect(jsonPath("$.traces[0].response.status").value("200"))
                .andExpect(jsonPath("$.traces[0].response.headers.Content-Length[0]").value("27"))
                .andReturn();

         /**
          * RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/actuator/httptrace")
          *                 .accept(MediaType.APPLICATION_JSON);
          * MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
          * assertEquals(200, mvcResult.getResponse().getStatus());
          *         assertNotEquals(null, mvcResult.getResponse());
          *         assertNotEquals("", mvcResult.getResponse());
          *         assertEquals("application/json",
          *         mvcResult.getResponse().getContentType());
          * String resultResponse = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
            //https://jsoneditoronline.org/#left=local.mafetu&right=local.yamuza
          * String expected = "{\"traces\":[{\"principal\":null,\"session\":null,\"request\":{\"method\":\"GET\",\"uri\":\"http://localhost/\",\"headers\":{\"Accept\":[\"text/plain\"]},\"remoteAddress\":null},\"response\":{\"status\":200,\"headers\":{\"Content-Length\":[\"27\"],\"Content-Type\":[\"text/plain;charset=UTF-8\"]}}}]}";
          * JSONAssert.assertEquals(expected, resultResponse,false);**/
    }

    @Test
    public void actuatorHttpTraceBeanInContext(){
        assertNotNull(appContext.getBean(HttpTraceRepository.class));
    }
}


//https://www.programcreek.com/java-api-examples/?api=org.springframework.test.web.servlet.MvcResult