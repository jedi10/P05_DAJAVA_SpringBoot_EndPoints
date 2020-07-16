package com.safetynet.alerts.rest;

import com.safetynet.alerts.dao.IFirestationDAO;
import com.safetynet.alerts.models.Firestation;
import com.safetynet.alerts.models.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import java.util.List;

import static com.safetynet.alerts.utils.JsonConvert.feedWithJava;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class AdminFirestationControllerTest {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    private AdminFirestationController adminFirestationController;

    @Mock
    private IFirestationDAO firestationDAO;

    private Firestation firestation1 = new Firestation("1509 Culver St","3");
    private Firestation firestation2 = new Firestation("29 15th St", "2");

    @BeforeEach
    void setUp() {
        //***********GIVEN*************
        //          Mockito
        when(firestationDAO.findAll()).thenReturn(List.of(firestation1, firestation2));
        this.adminFirestationController.firestationDAO = firestationDAO;
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAllPFirestations() throws Exception {
        //***********GIVEN*************
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/firestation/")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(firestationDAO, Mockito.times(0)).findAll();

        //**************WHEN-THEN****************************
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(containsString("3")))
                .andExpect(jsonPath("$[0].address").value(firestation1.getAddress()))
                .andReturn();
        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(firestationDAO, Mockito.times(1)).findAll();

        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        String expectedJson = null;
        expectedJson = feedWithJava(List.of(firestation1, firestation2));
        JSONAssert.assertEquals(expectedJson, mvcResult.getResponse().getContentAsString(), true);
    }

    @Test
    void getAllFirestationsVoid() throws Exception {
        //***********GIVEN*************
        when(firestationDAO.findAll()).thenReturn(new ArrayList<Firestation>());
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/firestation/");

        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(firestationDAO, Mockito.times(0)).findAll();

        //**************WHEN-THEN****************************
        mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isNoContent());

        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(firestationDAO, Mockito.times(1)).findAll();//.save(any());
    }
}