package com.safetynet.alerts.rest.publicmicroservices;

import com.safetynet.alerts.service.PhoneAlertService;
import com.safetynet.alerts.service.rto_models.PersonInfoRTO;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.safetynet.alerts.utils.JsonConvertForTest.parseResponse;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PublicAppController_phoneAlert_Test {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    public PublicAppController publicAppController;

    @Mock
    private PhoneAlertService phoneAlertService;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }

    @Order(1)
    @Test
    void getPhoneAlert_Ok() throws Exception {
        when(phoneAlertService.getPhoneAlert(anyString())).thenReturn(List.of("123456789", "987654321"));
        //Mock Injection in Object tested
        publicAppController.phoneAlertService = phoneAlertService;

        String urlTemplate = String.format("%s%s",
                "/phonealert/",
                2);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(phoneAlertService, Mockito.never()).getPhoneAlert(
                anyString());

        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(containsString("123456789")))
                .andExpect(content().string(containsString("987654321")))
                .andReturn();

        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        //*****************Check with JAVA*************************
        ArrayList<String> resultJavaObject = parseResponse(mvcResult, ArrayList.class);
        assertEquals(List.of("123456789", "987654321"), resultJavaObject);

        //***********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //***********************************************************
        verify(phoneAlertService, Mockito.times(1)).
                getPhoneAlert(anyString());
    }

    @Order(2)
    @Test
    void getPhoneAlert_EmptyList() throws Exception {
        when(phoneAlertService.getPhoneAlert(anyString())).thenReturn(Collections.emptyList());
        //Mock Injection in Object tested
        publicAppController.phoneAlertService = phoneAlertService;

        String urlTemplate = String.format("%s%s",
                "/phonealert/",
                4);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(phoneAlertService, Mockito.never()).getPhoneAlert(
                anyString());

        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isNotFound())
                .andReturn();

        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        assertTrue(mvcResult.getResponse().getContentAsString().isEmpty());

        //***********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //***********************************************************
        verify(phoneAlertService, Mockito.times(1)).
                getPhoneAlert(anyString());
    }
}