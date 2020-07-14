package com.safetynet.alerts.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.utils.JsonConvert;
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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdminPersonControllerTest {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    private AdminPersonController adminPersonController;

    @Mock
    private IPersonDAO personDAO;

    private Person person = new Person("julia", "werner", "rue du colisee", "Rome", 45, "06-12-23-34-45", "wermer@mail.it");
    private Person person2 = new Person("judy", "holmes", "rue de la pensee", "Londre", 89, "06-25-74-90-12", "holmes@mail.en");
    
    @BeforeAll
    void setUp() {
        //***********GIVEN*************
        //          Mockito
        when(personDAO.findAll()).thenReturn(List.of(person, person2));
        adminPersonController.IPersonDAO = personDAO;
    }

    @BeforeEach
    void setUpEach() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAllPersons() throws Exception {
        //***********GIVEN*************
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/person/")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(personDAO, Mockito.times(0)).findAll();

        //**************WHEN-THEN****************************
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(containsString("wermer@mail.it")))
                .andExpect(jsonPath("$[0].firstName").value(person.getFirstName()))
                .andReturn();
        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(personDAO, Mockito.times(1)).findAll();


        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        String expectedJson = null;
        expectedJson = JsonConvert.feedWithJava(List.of(person, person2));
        JSONAssert.assertEquals(expectedJson, mvcResult.getResponse().getContentAsString(), true);

    }

    @Test
    void getPerson() {
    }

    @Test
    void createPerson() {
    }

    @Test
    void updatePerson() {
    }

    @Test
    void deletePerson() {
    }

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule());

    public static <T> T parseResponse(MvcResult result, Class<T> responseClass) {
        try {
            String contentAsString = result.getResponse().getContentAsString();
            return MAPPER.readValue(contentAsString, responseClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}



//https://stackoverflow.com/questions/18336277/how-to-check-string-in-response-body-with-mockmvc
//https://www.logicbig.com/tutorials/spring-framework/spring-web-mvc/json-message-object-conversion.html