package com.safetynet.alerts.utils;

import com.safetynet.alerts.utils.CustomTraceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class CustomTraceRepositoryTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationContext appContext;

    @BeforeEach
    private void setUp() throws Exception {
        //Go to Hello Word Page
        mockMvc.perform(MockMvcRequestBuilders.get("/")
                .accept(MediaType.TEXT_PLAIN_VALUE));
    }

    @Test
    public void actuatorCustomTraceRepositoryLastTrace(){

        CustomTraceRepository customTraceRepo =   appContext.getBean(CustomTraceRepository.class);
        //************************
        //***Check Bean In Context
        //************************
        assertNotNull(customTraceRepo);

        //*******************************************
        //***Check LastTrace in CustomTraceRepository
        //*******************************************
        AtomicReference<HttpTrace> lastTrace =  customTraceRepo.lastTrace;
        assertNotNull(lastTrace, "LastTrace can't be null and should have a content");
        String urlRecorded = lastTrace.get().getRequest().getUri().getPath();
        assertEquals("/", urlRecorded);
    }

}



//https://stackoverflow.com/questions/11550790/remove-hostname-and-port-from-url-using-regular-expression