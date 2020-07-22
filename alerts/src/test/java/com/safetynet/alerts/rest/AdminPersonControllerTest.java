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
import org.springframework.web.util.UriUtils;

import java.util.ArrayList;
import java.util.List;


import static com.safetynet.alerts.utils.Jackson.convertJavaToJson;
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

    private final String rootURL = "/person/";

    private Person person1 = new Person("julia", "werner", "rue du colisee", "Rome", 45, "06-12-23-34-45", "wermer@mail.it");
    private Person person2 = new Person("judy", "holmes", "rue de la pensee", "Londre", 89, "06-25-74-90-12", "holmes@mail.en");
    private Person personCreated = new Person("jack", "mortimer", "rue du stade", "Rome", 45, "06-25-50-90-12", "mortimer@mail.it");
    private Person personUpdated = new Person("jack", "mortimer", "rue du colisee", "Rome", 45, "06-25-23-99-00", "mortimer@mail.it");
    private Person unknownPerson = new Person("grrr","trex","teodor","citeor", 3455, "123467555","mail");

    @BeforeAll
    void setUp() {

    }

    @BeforeEach
    void setUpEach() {
        //***********GIVEN*************
        //          Mockito
        when(personDAO.findAll()).thenReturn(List.of(person1, person2));
        when(personDAO.findByName("julia", "werner")).thenReturn(person1);
        when(personDAO.findByName("jack", "mortimer")).thenReturn(personUpdated);
        when(personDAO.findByName("grrr", "trex")).thenReturn(null);
        //Person personMock = mock(Person.class);
        when(personDAO.save(personCreated)).thenReturn(personCreated);//Mockito.any(Person.class)
        when(personDAO.save(person1)).thenReturn(null);
        when(personDAO.update(personUpdated)).thenReturn(personUpdated);
        when(personDAO.update(unknownPerson)).thenReturn(null);
        when(personDAO.delete(personUpdated)).thenReturn(true);
        this.adminPersonController.personDAO = personDAO;
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAllPersons() throws Exception {
        //***********GIVEN*************
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(rootURL)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(personDAO, Mockito.times(0)).findAll();

        //**************WHEN-THEN****************************
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(containsString(person1.getEmail())))
                .andExpect(jsonPath("$[0].firstName").value(person1.getFirstName()))
                .andReturn();
        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(personDAO, Mockito.times(1)).findAll();


        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        String expectedJson = null;
        expectedJson = convertJavaToJson(List.of(person1, person2));
        JSONAssert.assertEquals(expectedJson, mvcResult.getResponse().getContentAsString(), true);
    }

    @Test
    void getAllPersonsVoid() throws Exception {
        //***********GIVEN*************
        when(personDAO.findAll()).thenReturn(new ArrayList<Person>());
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(rootURL);

        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(personDAO, Mockito.times(0)).findAll();

        //**************WHEN-THEN****************************
        mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isNoContent());

        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(personDAO, Mockito.times(1)).findAll();//.save(any());
    }

    @Test
    void getPerson() throws Exception {
        //***********GIVEN*************
        String urlTemplate = String.format("%s%s&%s",
                rootURL,
                person1.getFirstName(),
                person1.getLastName());//"julia&werner"
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(personDAO, Mockito.times(0)).findByName("julia","werner");

        //**************WHEN-THEN****************************
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(containsString(person1.getEmail())))
                .andExpect(jsonPath("$.firstName").value(person1.getFirstName()))
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
        expectedJson = convertJavaToJson(person1);
        JSONAssert.assertEquals(expectedJson, mvcResult.getResponse().getContentAsString(), true);
        //*****************Check with JAVA*************************
        Person resultJavaObject = parseResponse(mvcResult, Person.class);
        assertThat(person1).isEqualToComparingFieldByField(resultJavaObject);
    }

    @Test
    void getUnknownPerson() throws Exception {
        //***********GIVEN*************
        String urlTemplate = String.format("%s%s&%s",
                rootURL,
                unknownPerson.getFirstName(),
                unknownPerson.getLastName());//"grrr&trex"
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate);

        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(personDAO, Mockito.times(0)).findByName("grrr","trex");

        //**************WHEN-THEN****************************
        mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isNotFound());//404

        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(personDAO, Mockito.times(1)).findByName("grrr","trex");//.save(any());
    }

    @Test
    void createPerson() throws Exception {
        //***********GIVEN*************
        String jsonGiven = convertJavaToJson(personCreated);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(rootURL)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonGiven)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        String urlDestination = String.format("%s&%s",
                UriUtils.encode(personCreated.getFirstName(), "UTF-8"),
                UriUtils.encode(personCreated.getLastName(), "UTF-8"));
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(personDAO, Mockito.times(0)).save(personCreated);

        //**************WHEN-THEN****************************
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl("http://localhost" + rootURL + urlDestination))
                .andReturn();
        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(personDAO, Mockito.times(1)).save(ArgumentMatchers.refEq(personCreated));//.save(any());
        //https://stackoverflow.com/questions/57690810/how-to-fix-arguments-are-different-wanted-error-in-junit-and-mockito
        //https://www.softwaretestinghelp.com/mockito-matchers/
    }

    @Test
    void createPersonAlreadyThere() throws Exception {
        //***********GIVEN*************
        String jsonGiven = convertJavaToJson(person1);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(rootURL)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonGiven)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(personDAO, Mockito.times(0)).save(person1);

        //**************WHEN-THEN****************************
        mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isConflict());//409

        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(personDAO, Mockito.times(1)).save(ArgumentMatchers.refEq(person1));//.save(any());
    }

    @Test
    void updatePerson() throws Exception {
        //***********GIVEN*************
        String jsonGiven = convertJavaToJson(personUpdated);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(rootURL)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonGiven)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(personDAO, Mockito.times(0)).update(personUpdated);

        //**************WHEN-THEN****************************
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(containsString(personUpdated.getEmail())))
                .andExpect(jsonPath("$.firstName").value(personUpdated.getFirstName()))
                .andExpect(jsonPath("$.address").value(personUpdated.getAddress()))
                .andReturn();
        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(personDAO, Mockito.times(1)).update(ArgumentMatchers.refEq(personUpdated));//.save(any());

        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        //*****************Check with JSON*************************
        String expectedJson = null;
        expectedJson = convertJavaToJson(personUpdated);
        JSONAssert.assertEquals(expectedJson, mvcResult.getResponse().getContentAsString(), true);
        //*****************Check with JAVA*************************
        Person resultJavaObject = parseResponse(mvcResult, Person.class);
        assertThat(personUpdated).isEqualToComparingFieldByField(resultJavaObject);
    }

    @Test
    void updateUnknownPerson() throws Exception {
        //***********GIVEN*************
        String jsonGiven = convertJavaToJson(unknownPerson);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(rootURL)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonGiven);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(personDAO, Mockito.times(0)).update(unknownPerson);

        //**************WHEN-THEN****************************
        mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isNotFound());

        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(personDAO, Mockito.times(1)).update(ArgumentMatchers.refEq(unknownPerson));//.save(any());
    }

    @Test
    void deletePerson() throws Exception {
        //***********GIVEN*************
        String urlTemplate = String.format("%s%s&%s",
                rootURL,
                personUpdated.getFirstName(),
                personUpdated.getLastName());//jack&mortimer");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(urlTemplate);

        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(personDAO, Mockito.times(0)).findByName("jack","mortimer");
        verify(personDAO, Mockito.times(0)).delete(personUpdated);

        //**************WHEN-THEN****************************
        mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isOk());//200
        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(personDAO, Mockito.times(1)).findByName("jack","mortimer");
        verify(personDAO, Mockito.times(1)).delete(ArgumentMatchers.refEq(personUpdated));
    }

    @Test
    void deleteUnknownPerson() throws Exception {
        //***********GIVEN*************
        String urlTemplate = String.format("%s%s&%s",
                rootURL,
                unknownPerson.getFirstName(),
                unknownPerson.getLastName());//"grrr&trex"
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(urlTemplate);

        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(personDAO, Mockito.times(0)).delete(unknownPerson);

        //**************WHEN-THEN****************************
        mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isNotFound());

        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(personDAO, Mockito.times(1)).findByName("grrr","trex");//.save(any());
        verify(personDAO, Mockito.times(0)).delete(ArgumentMatchers.refEq(unknownPerson));//.save(any());
    }
}



//https://stackoverflow.com/questions/18336277/how-to-check-string-in-response-body-with-mockmvc
//https://www.logicbig.com/tutorials/spring-framework/spring-web-mvc/json-message-object-conversion.html