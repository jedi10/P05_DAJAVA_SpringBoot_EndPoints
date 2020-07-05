package com.safetynet.alerts.rest;

import com.safetynet.alerts.models.CustomErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.net.URL;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
//@ContextConfiguration(classes = {TestContext.class})
class CustomErrorControllerTest {

    @Autowired
    public MockMvc mockMvc;

    @InjectMocks
    CustomErrorController customErrorController;

    @Autowired
    public ErrorAttributes errorAttributes ;

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
                .andExpect(status().isNotFound());
        //**********************************************************************
        //Disabled because custom error controller can not be tested with a mock
        /**.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
         .andExpect(jsonPath("$").isNotEmpty())
         .andExpect(jsonPath("$", hasSize(5)))
         .andExpect(jsonPath("$.status").isNotEmpty())
         .andExpect(jsonPath("$.path").isNumber())
         .andExpect(jsonPath("$.status").value(404))
         .andExpect(jsonPath("$.path").isNotEmpty())
         .andExpect(jsonPath("$.path").isString())
         .andExpect(jsonPath("$.path").value("/wrongurl"));**/
    }

    @Test
    public void customErrorMethods_basic() throws Exception {
        //*************************************
        //***** TEST for jacoco coverage *****
        //*************************************
        //Give
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/badurl");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        customErrorController.errorAttributes = errorAttributes;
        assertNotNull(customErrorController);

        //When-Then
        assertEquals("/error", customErrorController.getErrorPath());

        //Give
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setStatus(404);
        //When
        ResponseEntity<CustomErrorResponse> restResponseEntity = customErrorController.handleError(request, response);

        //Then
        assertEquals(404,restResponseEntity.getBody().getStatus());
        //assertEquals("/badurl",restResponseEntity.getBody().getPath());

    }
}


//https://howtodoinjava.com/spring-boot2/testing/rest-controller-unit-test-example/
//https://allaroundjava.com/unit-testing-spring-rest-controllers-mockmvc/
//Test Json with model class
//https://stackoverflow.com/questions/18336277/how-to-check-string-in-response-body-with-mockmvc

//test redirection
//https://stackoverflow.com/questions/11273696/how-to-test-spring-mvc-controller-with-redirectattributes