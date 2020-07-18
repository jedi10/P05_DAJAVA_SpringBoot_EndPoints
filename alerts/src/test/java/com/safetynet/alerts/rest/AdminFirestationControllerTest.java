package com.safetynet.alerts.rest;

import com.safetynet.alerts.dao.IFirestationDAO;
import com.safetynet.alerts.models.Firestation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
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
import org.springframework.web.util.UriUtils;

import java.util.ArrayList;
import java.util.List;

import static com.safetynet.alerts.utils.JsonConvert.feedWithJava;
import static com.safetynet.alerts.utils.JsonConvertForTest.parseResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
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

    private final String rootURL = "/firestation/";

    private Firestation firestation1 = new Firestation("1509 Culver St","3");
    private Firestation firestation2 = new Firestation("29 15th St", "2");
    private Firestation firestationCreated = new Firestation("210 Jump Street", "3");
    private Firestation firestationUpdated = new Firestation("210 Jump Street", "5");
    private Firestation unknownFirestation = new Firestation("7 downing Street", "5");

    @BeforeEach
    void setUp() {
        //***********GIVEN*************
        //          Mockito
        when(firestationDAO.findAll()).thenReturn(List.of(firestation1, firestation2));
        when(firestationDAO.findByAddress("1509 Culver St")).thenReturn(firestation1);
        when(firestationDAO.findByAddress("7 downing Street")).thenReturn(null);
        when(firestationDAO.findByAddress(firestationUpdated.getAddress())).thenReturn(firestationUpdated);
        when(firestationDAO.save(firestationCreated)).thenReturn(firestationCreated);
        when(firestationDAO.save(firestation1)).thenReturn(null);
        when(firestationDAO.update(firestationUpdated)).thenReturn(firestationUpdated);
        when(firestationDAO.update(unknownFirestation)).thenReturn(null);
        when(firestationDAO.delete(firestationUpdated)).thenReturn(true);
        this.adminFirestationController.firestationDAO = firestationDAO;
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAllPFirestations() throws Exception {
        //***********GIVEN*************
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(rootURL)
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
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(rootURL);

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

    @Test
    void getFirestation() throws Exception {
        //***********GIVEN*************
        String urlTemplate = String.format("%s%s",
                rootURL,
                firestation1.getAddress());
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(firestationDAO, Mockito.times(0)).findByAddress(firestation1.getAddress());

        //**************WHEN-THEN****************************
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(containsString("3")))
                .andExpect(jsonPath("$.address").value(firestation1.getAddress()))
                .andReturn();
        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(firestationDAO, Mockito.times(1)).findByAddress(firestation1.getAddress());


        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        //*****************Check with JSON*************************
        String expectedJson = null;
        expectedJson = feedWithJava(firestation1);
        JSONAssert.assertEquals(expectedJson, mvcResult.getResponse().getContentAsString(), true);
        //*****************Check with JAVA*************************
        Firestation resultJavaObject = parseResponse(mvcResult, Firestation.class);
        assertThat(firestation1).isEqualToComparingFieldByField(resultJavaObject);
    }

    @Test
    void getUnknownFirestation() throws Exception {
        //***********GIVEN*************
        String urlTemplate = String.format("%s%s",
                rootURL,
                unknownFirestation.getAddress());
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate);

        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(firestationDAO, Mockito.times(0)).findByAddress(unknownFirestation.getAddress());

        //**************WHEN-THEN****************************
        mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isNotFound());

        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(firestationDAO, Mockito.times(1)).findByAddress(unknownFirestation.getAddress());//.save(any());
    }

    @Test
    void createFirestation() throws Exception {
        //***********GIVEN*************
        String jsonGiven = feedWithJava(firestationCreated);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(rootURL)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonGiven)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        String urlDestination =  UriUtils.encode(firestationCreated.getAddress(), "UTF-8");
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(firestationDAO, Mockito.times(0)).save(firestationCreated);

        //**************WHEN-THEN****************************

        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl("http://localhost"+ rootURL + urlDestination))
                //.andExpect(redirectedUrl("http://localhost/firestation/210%20Jump%20Street"))
                .andReturn();
        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(firestationDAO, Mockito.times(1)).save(ArgumentMatchers.refEq(firestationCreated));//.save(any());
        //https://stackoverflow.com/questions/57690810/how-to-fix-arguments-are-different-wanted-error-in-junit-and-mockito
        //https://www.softwaretestinghelp.com/mockito-matchers/
    }

    @Test
    void createFirestationAlreadyThere() throws Exception {
        //***********GIVEN*************
        String jsonGiven = feedWithJava(firestation1);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(rootURL)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonGiven)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(firestationDAO, Mockito.times(0)).save(firestation1);

        //**************WHEN-THEN****************************
        mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isNoContent());//204

        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(firestationDAO, Mockito.times(1)).save(ArgumentMatchers.refEq(firestation1));//.save(any());
    }

    @Test
    void updateFirestation() throws Exception {
        //***********GIVEN*************
        String jsonGiven = feedWithJava(firestationUpdated);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(rootURL)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonGiven)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(firestationDAO, Mockito.times(0)).update(firestationUpdated);

        //**************WHEN-THEN****************************
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(containsString("5")))
                .andExpect(jsonPath("$.address").value(firestationUpdated.getAddress()))
                .andReturn();
        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(firestationDAO, Mockito.times(1)).update(ArgumentMatchers.refEq(firestationUpdated));//.save(any());

        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        //*****************Check with JSON*************************
        String expectedJson = null;
        expectedJson = feedWithJava(firestationUpdated);
        JSONAssert.assertEquals(expectedJson, mvcResult.getResponse().getContentAsString(), true);
        //*****************Check with JAVA*************************
        Firestation resultJavaObject = parseResponse(mvcResult, Firestation.class);
        assertThat(firestationUpdated).isEqualToComparingFieldByField(resultJavaObject);
    }

    @Test
    void updateUnknownFirestation() throws Exception {
        //***********GIVEN*************
        String jsonGiven = feedWithJava(unknownFirestation);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(rootURL)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonGiven);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(firestationDAO, Mockito.times(0)).update(unknownFirestation);

        //**************WHEN-THEN****************************
        mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isNotFound());

        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(firestationDAO, Mockito.times(1)).update(ArgumentMatchers.refEq(unknownFirestation));//.save(any());
    }

    @Test
    void deleteFirestation() throws Exception {
        //***********GIVEN*************
        String urlTemplate = String.format("%s%s",
                rootURL,
                firestationUpdated.getAddress());
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(urlTemplate);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(firestationDAO, Mockito.times(0)).findByAddress(firestationUpdated.getAddress());
        verify(firestationDAO, Mockito.times(0)).delete(firestationUpdated);

        //**************WHEN-THEN****************************
        mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isNoContent());
        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(firestationDAO, Mockito.times(1)).findByAddress(firestationUpdated.getAddress());
        verify(firestationDAO, Mockito.times(1)).delete(ArgumentMatchers.refEq(firestationUpdated));
    }

    @Test
    void deleteUnknownFirestation() throws Exception {
        //***********GIVEN*************
        String urlTemplate = String.format("%s%s",
                rootURL,
                unknownFirestation.getAddress());
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(urlTemplate);

        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(firestationDAO, Mockito.times(0)).delete(unknownFirestation);

        //**************WHEN-THEN****************************
        mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isNotFound());

        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(firestationDAO, Mockito.times(1)).findByAddress(unknownFirestation.getAddress());//.save(any());
        verify(firestationDAO, Mockito.times(0)).delete(ArgumentMatchers.refEq(unknownFirestation));//.save(any());
    }

}