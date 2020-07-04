package com.safetynet.alerts.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HTMLHomeControllerTest {

    @Autowired
    public MockMvc mockMvc;

    @InjectMocks
    HTMLHomeController htmlHomeController;

    @Test
    public void responseOfControllerMethods_basic() throws Exception {

        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setStatus(200);
        assertNotNull(htmlHomeController);
        assertEquals("index", htmlHomeController.home(response));
        assertEquals("404", htmlHomeController.htmlError(response));
    }

    @Test
    public void htmlHomeResponse_extended() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/html/home")
                .accept(MediaType.TEXT_HTML_VALUE))//.andDo(print());
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML+";charset=UTF-8"))
                .andExpect(content().string(containsString("Alerts Home Page")));
    }

    @Test
    public void htmlErrorResponse_extended() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/html/home/error")
                .accept(MediaType.TEXT_HTML_VALUE))//.andDo(print());
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML+";charset=UTF-8"))
                .andExpect(content().string(containsString("Error Exception")));
    }

    @Test
    public void htmlHomeRedirectionResponse_extended() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/html")
                .accept(MediaType.TEXT_HTML_VALUE))//.andDo(print());
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/html/home"));
    }

    @Test
    public void htmlHomeRedirection2Response_extended() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/")
                .accept(MediaType.TEXT_HTML_VALUE))//.andDo(print());
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/html/home"));
    }
}