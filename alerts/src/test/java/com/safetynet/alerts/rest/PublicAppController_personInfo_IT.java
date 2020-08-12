package com.safetynet.alerts.rest;

import com.safetynet.alerts.dao.IMedicalRecordDAO;
import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.service.PersonInfoService;
import com.safetynet.alerts.service.rto_models.IPersonInfoRTO;
import com.safetynet.alerts.service.rto_models.PersonInfoRTO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.safetynet.alerts.utils.Jackson.convertJavaToJson;
import static com.safetynet.alerts.utils.JsonConvertForTest.parseResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PublicAppController_personInfo_IT {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    public PublicAppController publicAppController;

    @Autowired
    public PersonInfoService personInfoService;

    @Mock
    public IPersonInfoRTO personInfoRTO;

    @Mock
    public IPersonDAO personDAO;

    @Mock
    public IMedicalRecordDAO medicalRecordDAO;

    private Person person1 = new Person(
            "john", "boyd", "rue du colisee", "Rome", 45, "06-12-23-34-45", "wermer@mail.it");

    private List<String> medicationList = new ArrayList<>();

    private List<String> allergiesList = new ArrayList<>();

    private MedicalRecord medicalRecord1 = new MedicalRecord("john", "boyd",
            LocalDate.of(1984, 3, 6));

    @BeforeEach
    void setUp() throws Exception {

    }

    @AfterEach
    void tearDown() {

    }

    @Order(1)
    @Test
    void getPersonInfo_Ok() throws Exception {
        //***********GIVEN*************
        medicationList.add("aznol:350mg"); medicationList.add("hydrapermazol:100mg");
        allergiesList.add("nillacilan");
        medicalRecord1.setMedications(medicationList);
        medicalRecord1.setAllergies(allergiesList);
        personInfoRTO = new PersonInfoRTO(person1, medicalRecord1);
        when(personDAO.findByName(person1.getFirstName(), person1.getLastName())).
                thenReturn(person1);
        when(medicalRecordDAO.findByName(person1.getFirstName(), person1.getLastName())).
                thenReturn(medicalRecord1);
        //Mock Injection in Object tested
        personInfoService.setDAO(personDAO, medicalRecordDAO);

        String urlTemplate = String.format("%s%s&%s",
                "/personinfo/",
                person1.getFirstName(),
                person1.getLastName());
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(personDAO, Mockito.never()).findByName(
                person1.getFirstName(), person1.getLastName());
        verify(medicalRecordDAO, Mockito.never()).findByName(
                person1.getFirstName(), person1.getLastName());

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
        verify(personDAO, Mockito.times(1)).findByName(
                person1.getFirstName(), person1.getLastName());
        verify(medicalRecordDAO, Mockito.times(1)).findByName(
                person1.getFirstName(), person1.getLastName());

        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        //*****************Check with JSON*************************
        String expectedJson = null;
        expectedJson = convertJavaToJson(personInfoRTO);
        JSONAssert.assertEquals(expectedJson, mvcResult.getResponse().getContentAsString(), true);
        //*****************Check with JAVA*************************
        PersonInfoRTO resultJavaObject = parseResponse(mvcResult, PersonInfoRTO.class);
        assertThat(personInfoRTO).isEqualToComparingFieldByField(resultJavaObject);
    }

    @Order(2)
    @ParameterizedTest
    @CsvSource({"toto,riri","toto,''", "'',''"})
        //@ValueSource(strings = { "toto", "" })
    void getPersonInfo_NotFound(String firstName, String lastName) throws Exception {
        //***********GIVEN*************
        when(personDAO.findByName(firstName, lastName)).thenReturn(null);
        when(medicalRecordDAO.findByName(firstName, lastName)).thenReturn(null);
        //Mock Injection in Object tested
        personInfoService.setDAO(personDAO, medicalRecordDAO);

        String urlTemplate = String.format("%s%s&%s",
                "/personinfo/",
                firstName,
                lastName);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(personDAO, Mockito.never()).findByName(firstName, lastName);
        verify(medicalRecordDAO, Mockito.never()).findByName(firstName, lastName);

        //**************WHEN-THEN****************************
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isNotFound())
                .andReturn();

        assertNotNull(mvcResult);
        assertNull(mvcResult.getResponse().getContentType());
        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(personDAO, Mockito.times(1)).findByName(
                firstName, lastName);
        verify(medicalRecordDAO, Mockito.times(1)).findByName(
                firstName, lastName);
    }

    static Stream<Arguments> nullEmptyNames() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(null, ""),
                Arguments.of("", null));
    }

    @Order(3)
    @ParameterizedTest
    @MethodSource("nullEmptyNames")
    void getPersonInfo_NullCase(String firstName, String lastName) throws Exception {
        //***********GIVEN*************
        when(personDAO.findByName(firstName, lastName)).thenReturn(null);
        when(medicalRecordDAO.findByName(firstName, lastName)).thenReturn(null);
        //Mock Injection in Object tested
        personInfoService.setDAO(personDAO, medicalRecordDAO);

        String urlTemplate = String.format("%s%s&%s",
                "/personinfo/",
                firstName,
                lastName);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(personDAO, Mockito.never()).findByName(firstName, lastName);
        verify(medicalRecordDAO, Mockito.never()).findByName(firstName, lastName);

        //**************WHEN-THEN****************************
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isNotFound())
                .andReturn();

        assertNotNull(mvcResult);
        assertNull(mvcResult.getResponse().getContentType());
        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(personDAO, Mockito.never()).findByName(firstName, lastName);
        verify(medicalRecordDAO, Mockito.never()).findByName(firstName, lastName);
    }

}


//https://blog.oio.de/2018/10/26/use-null-values-in-junit-5-parameterized-tests/
//https://www.baeldung.com/parameterized-tests-junit-5