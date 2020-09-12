package com.safetynet.alerts.rest.publicmicroservices;

import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.service.PersonInfoService;
import com.safetynet.alerts.service.rto_models.IPersonInfoRTO;
import com.safetynet.alerts.service.rto_models.PersonInfoRTO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.ArgumentMatchers;
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
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PublicAppController_personInfo_Test {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    public PublicAppController publicAppController;

    @Mock
    public PersonInfoService personInfoService;

    @Spy
    public List<IPersonInfoRTO> personInfoRTOList = new ArrayList<>();

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

    @AfterAll
    void tearDownAll(){
        publicAppController = null;
        mockMvc = null;
    }

    @Order(1)
    @ParameterizedTest
    @CsvSource({"julia,roberts"})
    void redirectGetPerson(String firstName, String lastName) throws Exception {
        //GIVEN
        String urlTemplate = String.format("%s%s&%s",
                "/admin/personinfo/",
                firstName,
                lastName);
        String expectedUrl = String.format("%s%s&%s",
                "/person/",
                firstName,
                lastName);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate);
        //WHEN
        mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(expectedUrl));
    }

    @Order(2)
    @ParameterizedTest
    @CsvSource({"julia,roberts"})
    void redirectGetPersonInfo(String firstName, String lastName) throws Exception {
        //GIVEN
        String urlTemplate = String.format("%s%s&%s",
                "/personInfo?",
                "firstName=" + firstName,
                "lastName=" + lastName);
        String expectedUrl = String.format("%s%s&%s",
                "/personinfo/",
                firstName,
                lastName);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate);
        //WHEN
        mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(expectedUrl));
    }

    @Order(3)
    @Test
    void getPersonInfo_Ok() throws Exception {
        //***********GIVEN*************
        medicationList.add("aznol:350mg"); medicationList.add("hydrapermazol:100mg");
        allergiesList.add("nillacilan");
        medicalRecord1.setMedications(medicationList);
        medicalRecord1.setAllergies(allergiesList);

        personInfoRTOList.add(new PersonInfoRTO(person1, medicalRecord1));

        when(personInfoService.getPersonInfo(anyString(), anyString())).
                thenReturn(personInfoRTOList);
        //Mock Injection in Object tested
        publicAppController.personInfoService = personInfoService;

        String urlTemplate = String.format("%s%s&%s",
                "/personinfo/",
                person1.getFirstName(),
                person1.getLastName());
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(personInfoService, Mockito.never()).getPersonInfo(
                person1.getFirstName(), person1.getLastName());

        //**************WHEN-THEN****************************
       MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(containsString(person1.getEmail())))
                .andExpect(jsonPath("$.[0].firstName").value(person1.getFirstName()))
                .andReturn();
        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(personInfoService, Mockito.times(1)).getPersonInfo(
                person1.getFirstName(), person1.getLastName());

        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        //*****************Check with JSON*************************
        String expectedJson = null;
        expectedJson = convertJavaToJson(personInfoRTOList);
        JSONAssert.assertEquals(expectedJson, mvcResult.getResponse().getContentAsString(), true);
    }

    @Order(4)
    @ParameterizedTest
    @CsvSource({"toto,riri","toto,''", "'',''"})
    //@ValueSource(strings = { "toto", "" })
    void getPersonInfo_NotFound(String firstName, String lastName) throws Exception {
        //***********GIVEN*************
        when(personInfoService.getPersonInfo(firstName, lastName)).
                thenReturn(new ArrayList<>());
        //Mock Injection in Object tested
        publicAppController.personInfoService = personInfoService;

        String urlTemplate = String.format("%s%s&%s",
                "/personinfo/",
                 firstName,
                 lastName);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(personInfoService, Mockito.never()).getPersonInfo(
                firstName, lastName);

        //**************WHEN-THEN****************************
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isNotFound())
                .andReturn();

        assertNotNull(mvcResult);
        assertNull(mvcResult.getResponse().getContentType());
        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(personInfoService, Mockito.times(1)).getPersonInfo(
                firstName, lastName);
    }

    /*
    static Stream<Arguments> nullEmptyNames() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(null, ""),
                Arguments.of("", null));
    }

    @Disabled//this test is useless
    @Order(5)
    @ParameterizedTest
    @MethodSource("nullEmptyNames")
    void getPersonInfo_NullCase(String firstName, String lastName) throws Exception {
        //***********GIVEN*************
        when(personInfoService.getPersonInfo(ArgumentMatchers.isNull(), ArgumentMatchers.isNull())).
                thenReturn(null);
        when(personInfoService.getPersonInfo(ArgumentMatchers.isNull(), anyString())).
                thenReturn(null);
        when(personInfoService.getPersonInfo(anyString(), ArgumentMatchers.isNull())).
                thenReturn(null);
        //Mock Injection in Object tested
        publicAppController.personInfoService = personInfoService;

        String urlTemplate = String.format("%s%s&%s",
                "/personinfo/",
                firstName,
                lastName);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(personInfoService, Mockito.never()).getPersonInfo(
                firstName, lastName);

        //**************WHEN-THEN****************************
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isNotFound())
                .andReturn();

        assertNotNull(mvcResult);
        assertNull(mvcResult.getResponse().getContentType());
        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(personInfoService, Mockito.times(1)).getPersonInfo(
                any(), any());
    }*/
}


//https://blog.oio.de/2018/10/26/use-null-values-in-junit-5-parameterized-tests/
//https://www.baeldung.com/parameterized-tests-junit-5
//https://openclassrooms.com/fr/courses/6100311-testez-votre-code-java-pour-realiser-des-applications-de-qualite/6441016-simulez-des-composants-externes-aux-tests-avec-mockito