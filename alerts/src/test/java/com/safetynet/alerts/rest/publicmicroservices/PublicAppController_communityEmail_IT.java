package com.safetynet.alerts.rest.publicmicroservices;

import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.service.CommunityEmailService;
import com.safetynet.alerts.utils.Jackson;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.safetynet.alerts.utils.JsonConvertForTest.parseResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PublicAppController_communityEmail_IT {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    public PublicAppController publicAppController;

    @Autowired
    public CommunityEmailService communityEmailService;

    @Mock
    public IPersonDAO personDAO;



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
    }

    @AfterEach
    void tearDown() {

    }


    @Order(1)
    @Test
    void getCommunityEmail_Ok() throws Exception {
        //***********GIVEN*************
        //***********************************
        //Preparation List of DATA
        //***********************************
        assertNotNull(this.personList,
                "PersonList is Null: we need it for further tests");
        assertTrue(this.personList.size()>2);
        //We want to make sure there is at least one city different from other
        Person personWithDifferentCity = this.personList.get(personList.size()-1);
        personWithDifferentCity.setCity("New York");
        //we choose first element on list to get the city for test
        Person personChosenForTest = this.personList.get(0);
        String city = personChosenForTest.getCity();
        //Filtering list and transformation
        expectedMailList = this.personList.stream()
                .filter(o -> city.equals(o.getCity()))
                .map(n -> n.getEmail())
                .collect(Collectors.toList());
        assertFalse(expectedMailList.contains(personWithDifferentCity.getEmail()),
                "expectedMailList should not have the mail of the person we change the city");

        //Mock Configuration
        when(personDAO.getPersonList()).thenReturn(this.personList);
        //Mock Injection
        communityEmailService.setDAO(personDAO);

        //communityEmailService = new CommunityEmailService();
        //publicAppController.communityEmailService = communityEmailService;


        String urlTemplate = String.format("%s%s",
                "/communityemail/",
                city);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate)
                .accept(MediaType.APPLICATION_JSON_VALUE);

        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(personDAO, Mockito.never()).getPersonList();

        //**************WHEN-THEN****************************
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string(containsString(personChosenForTest.getEmail())))
                .andReturn();
        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(personDAO, Mockito.times(1)).getPersonList();

        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************

        //*****************Check with JAVA*************************
        ArrayList<?> resultJavaObject = parseResponse(mvcResult, ArrayList.class);
        assertThat(expectedMailList).isEqualTo(resultJavaObject);
        assertFalse(resultJavaObject.contains(personWithDifferentCity.getEmail()),
                "resultJavaObject should not have the mail of the person we change the city");
    }

    @Order(2)
    @Test
    void getCommunityEmail_NotFound() throws Exception {
        //***********GIVEN*************
        //Mock Configuration
        when(personDAO.getPersonList()).thenReturn(this.personList);
        //Mock Injection
        communityEmailService.setDAO(personDAO);

        String urlTemplate = String.format("%s%s",
                "/communityemail/",
                "cityName");
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        //***********************************************************
        //**************CHECK MOCK INVOCATION at start***************
        //***********************************************************
        verify(personDAO, Mockito.never()).getPersonList();

        //**************WHEN-THEN****************************
        MvcResult mvcResult = mockMvc.perform(builder)//.andDo(print());
                .andExpect(status().isNotFound())
                .andReturn();
        //*********************************************************
        //**************CHECK MOCK INVOCATION at end***************
        //*********************************************************
        verify(personDAO, Mockito.times(1)).getPersonList();

        //*********************************************************
        //**************CHECK RESPONSE CONTENT*********************
        //*********************************************************
        assertTrue(mvcResult.getResponse().getContentAsString().isEmpty());
    }
}


//https://blog.oio.de/2018/10/26/use-null-values-in-junit-5-parameterized-tests/
//https://www.baeldung.com/parameterized-tests-junit-5
//https://openclassrooms.com/fr/courses/6100311-testez-votre-code-java-pour-realiser-des-applications-de-qualite/6441016-simulez-des-composants-externes-aux-tests-avec-mockito