package com.safetynet.alerts.rest;

import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.models.Person;
import org.junit.jupiter.api.*;
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

import java.util.List;


import static com.safetynet.alerts.utils.JsonConvert.feedWithJava;
import static com.safetynet.alerts.utils.JsonConvertForTest.parseResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
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
    private Person personCreated = new Person("jack", "mortimer", "rue du stade", "Rome", 45, "06-25-50-90-12", "mortimer@mail.it");
    

    @BeforeAll
    void setUp() {

    }

    @BeforeEach
    void setUpEach() {
        //***********GIVEN*************
        //          Mockito
        when(personDAO.findAll()).thenReturn(List.of(person, person2));
        when(personDAO.findByName("julia", "werner")).thenReturn(person);
        //Person personMock = mock(Person.class);
        when(personDAO.save(personCreated)).thenReturn(personCreated);//Mockito.any(Person.class)
        this.adminPersonController.personDAO = personDAO;
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
        expectedJson = feedWithJava(List.of(person, person2));
        JSONAssert.assertEquals(expectedJson, mvcResult.getResponse().getContentAsString(), true);
    }

    @Test
    void getPerson() throws Exception {
        //***********GIVEN*************
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/person/julia&werner")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(personDAO, Mockito.times(0)).findByName("julia","werner");

        //**************WHEN-THEN****************************
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(containsString("wermer@mail.it")))
                .andExpect(jsonPath("$.firstName").value(person.getFirstName()))
                .andReturn();
        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(personDAO, Mockito.times(1)).findByName("julia","werner");


        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        //*****************Check with JSON*************************
        String expectedJson = null;
        expectedJson = feedWithJava(person);
        JSONAssert.assertEquals(expectedJson, mvcResult.getResponse().getContentAsString(), true);
        //*****************Check with JAVA*************************
        Person resultJavaObject = parseResponse(mvcResult, Person.class);
        assertThat(person).isEqualToComparingFieldByField(resultJavaObject);
    }

    @Test
    void createPerson() throws Exception {
        //***********GIVEN*************
        String jsonGiven = feedWithJava(personCreated);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/person/")
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonGiven)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(personDAO, Mockito.times(0)).save(personCreated);

        //**************WHEN-THEN****************************
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl("http://localhost/person/jack&mortimer"))
                .andReturn();
        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(personDAO, Mockito.times(1)).save(ArgumentMatchers.refEq(personCreated));//.save(any());
        //https://stackoverflow.com/questions/57690810/how-to-fix-arguments-are-different-wanted-error-in-junit-and-mockito
        //https://www.softwaretestinghelp.com/mockito-matchers/
    }

    @Test
    void updatePerson() {
    }

    @Test
    void deletePerson() {
    }

}



//https://stackoverflow.com/questions/18336277/how-to-check-string-in-response-body-with-mockmvc
//https://www.logicbig.com/tutorials/spring-framework/spring-web-mvc/json-message-object-conversion.html