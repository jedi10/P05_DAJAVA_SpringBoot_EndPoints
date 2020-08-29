package com.safetynet.alerts.rest.publicmicroservices;

import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.service.ChildAlertService;
import com.safetynet.alerts.service.rto_models.IPersonInfoRTO;
import com.safetynet.alerts.service.rto_models.PersonInfoRTO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.safetynet.alerts.utils.Jackson.convertJavaToJson;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PublicAppController_childAlert_Test {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    public PublicAppController publicAppController;

    @Mock
    public ChildAlertService childAlertService;

    @Spy
    public List<IPersonInfoRTO> personInfoRTOList = new ArrayList<>();

    private Person person1 = new Person(
            "junior", "boyd", "rue du colisee", "Rome", 45, "06-12-23-34-45", "wermer@mail.it");

    private List<String> medicationList = new ArrayList<>();

    private List<String> allergiesList = new ArrayList<>();

    private MedicalRecord medicalRecord1 = new MedicalRecord("junior", "boyd",
            LocalDate.of(2015, 3, 6));

    @BeforeEach
    void setUp() throws Exception {

    }

    @AfterEach
    void tearDown() {
        personInfoRTOList.clear();
    }

    @Order(1)
    @Test
    void getFireAndPersons_Ok() throws Exception {
        //***********GIVEN*************
        medicationList.add("aznol:350mg"); medicationList.add("hydrapermazol:100mg");
        allergiesList.add("nillacilan");
        medicalRecord1.setMedications(medicationList);
        medicalRecord1.setAllergies(allergiesList);

        personInfoRTOList.add(new PersonInfoRTO(person1, medicalRecord1));
        Map<IPersonInfoRTO.HumanCategory, List<IPersonInfoRTO>> childAlertServiceResult = new HashMap<>();
        childAlertServiceResult.put(IPersonInfoRTO.HumanCategory.CHILDREN, personInfoRTOList);
        childAlertServiceResult.put(IPersonInfoRTO.HumanCategory.ADULTS, personInfoRTOList);

        when(childAlertService.getChildAlert(person1.getAddress())).
                thenReturn(childAlertServiceResult);
        //Mock Injection in Object tested
        publicAppController.childAlertService = childAlertService;

        String urlTemplate = String.format("%s%s",
                "/childalert/",
                person1.getAddress());
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(childAlertService, Mockito.never()).getChildAlert(
                person1.getAddress());

        //**************WHEN-THEN****************************
       MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(containsString(person1.getEmail())))
                .andExpect(jsonPath("$.CHILDREN.[0].firstName").value(person1.getFirstName()))
                .andReturn();
        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(childAlertService, Mockito.times(1)).getChildAlert(
                person1.getAddress());

        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        //*****************Check with JSON*************************
        String expectedJson = null;
        expectedJson = convertJavaToJson(childAlertServiceResult);
        String jsonResult = mvcResult.getResponse().getContentAsString();
        JSONAssert.assertEquals(expectedJson, jsonResult, true);
    }

    @Order(2)
    @ParameterizedTest
    @CsvSource({"10 trafalgar square","X"})
    //@ValueSource(strings = { "toto", "" })
    void getChildAlert_NotFound(String address) throws Exception {
        //***********GIVEN*************
        Map<IPersonInfoRTO.HumanCategory, List<IPersonInfoRTO>> childAlertServiceResult = new HashMap<>();

        when(childAlertService.getChildAlert(address)).
                thenReturn(childAlertServiceResult);
        //Mock Injection in Object tested
        publicAppController.childAlertService = childAlertService;

        String urlTemplate = String.format("%s%s",
                "/childalert/",
                address);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(childAlertService, Mockito.never())
                .getChildAlert(address);

        //**************WHEN-THEN****************************
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isNotFound())
                .andReturn();

        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(childAlertService, Mockito.times(1))
                .getChildAlert(address);

        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        assertNotNull(mvcResult);
        assertNull(mvcResult.getResponse().getContentType());
    }

}


//https://blog.oio.de/2018/10/26/use-null-values-in-junit-5-parameterized-tests/
//https://www.baeldung.com/parameterized-tests-junit-5
//https://openclassrooms.com/fr/courses/6100311-testez-votre-code-java-pour-realiser-des-applications-de-qualite/6441016-simulez-des-composants-externes-aux-tests-avec-mockito