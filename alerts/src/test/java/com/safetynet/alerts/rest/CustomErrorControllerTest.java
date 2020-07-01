package com.safetynet.alerts.rest;

import com.safetynet.alerts.models.CustomErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * UNIT TEST Disabled because custom error controller can not be tested with a mock
 */
@Disabled
@SpringBootTest
@AutoConfigureMockMvc
//@ContextConfiguration(classes = {TestContext.class})
class CustomErrorControllerTest {

    @Autowired
    public MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {

    }

    @Test
    public void handleErrorTest() throws Exception {
        //*********************************
        //*** Load URL with no Mapping ***
        //*********************************
        mockMvc.perform(MockMvcRequestBuilders.get("/wrongurl")
                .accept(MediaType.APPLICATION_JSON_VALUE))//.andDo(print());
                //.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
                /**.andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$.status").isNotEmpty())
                .andExpect(jsonPath("$.path").isNumber())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").isNotEmpty())
                .andExpect(jsonPath("$.path").isString())
                .andExpect(jsonPath("$.path").value("/wrongurl"));**/
    }


}