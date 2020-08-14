package com.safetynet.alerts.rest.publicmicroservices;

import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.service.CommunityEmailService;
import com.safetynet.alerts.service.PersonInfoService;

import com.safetynet.alerts.service.rto_models.PersonInfoRTO;
import com.safetynet.alerts.utils.Jackson;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.safetynet.alerts.utils.Jackson.convertJavaToJson;
import static com.safetynet.alerts.utils.JsonConvertForTest.parseResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PublicAppController_communityEmail_Test {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    public PublicAppController publicAppController;

    @Mock
    public CommunityEmailService communityEmailService;

    List<Person> personList;
    List<String> expectedMailList;

    @BeforeEach
    void setUp() throws Exception {
        String fileString = Files.readString(Paths.get("src/test/resources/testData.json"));
        byte[] fileBytes = fileString.getBytes(StandardCharsets.UTF_8);
        try {
            this.personList = Jackson.convertJsonRootDataToJava(
                    fileBytes,
                    "persons",
                    Person.class);
        } catch (IOException e) {
            throw new IOException(e);
        }
        expectedMailList = this.personList.stream()
                .map(n -> n.getEmail())
                .collect(Collectors.toList());
    }

    @AfterEach
    void tearDown() {

    }


    @Order(1)
    @Test
    void getCommunityEmail_Ok() throws Exception {
        //***********GIVEN*************
        //Mock Configuration
        when(communityEmailService.getCommunityEmail(anyString())).thenReturn(expectedMailList);
        //Mock Injection in tested Object
        publicAppController.communityEmailService = communityEmailService;

        String urlTemplate = String.format("%s%s",
                "/communityemail/",
                "cityName");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        Person person1 = this.personList.get(0);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(communityEmailService, Mockito.never()).getCommunityEmail("cityName");

        //**************WHEN-THEN****************************
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(containsString(person1.getEmail())))
                .andReturn();
        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(communityEmailService, Mockito.times(1)).
                getCommunityEmail("cityName");

        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************

        //*****************Check with JAVA*************************
        ArrayList<?> resultJavaObject = parseResponse(mvcResult, ArrayList.class);
        assertThat(expectedMailList).isEqualTo(resultJavaObject);
    }

    @Order(2)
    @Test
    void getCommunityEmail_NotFound() throws Exception {
        //***********GIVEN*************
        //Mock Configuration
        when(communityEmailService.getCommunityEmail(anyString())).thenReturn(new ArrayList<>());
        //Mock Injection in tested Object
        publicAppController.communityEmailService = communityEmailService;

        String urlTemplate = String.format("%s%s",
                "/communityemail/",
                "cityName");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(communityEmailService, Mockito.never()).getCommunityEmail("cityName");

        //**************WHEN-THEN****************************
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isNotFound())
                .andReturn();
        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(communityEmailService, Mockito.times(1)).
                getCommunityEmail("cityName");

        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        assertTrue(mvcResult.getResponse().getContentAsString().isEmpty());
    }
}


//https://blog.oio.de/2018/10/26/use-null-values-in-junit-5-parameterized-tests/
//https://www.baeldung.com/parameterized-tests-junit-5
//https://openclassrooms.com/fr/courses/6100311-testez-votre-code-java-pour-realiser-des-applications-de-qualite/6441016-simulez-des-composants-externes-aux-tests-avec-mockito