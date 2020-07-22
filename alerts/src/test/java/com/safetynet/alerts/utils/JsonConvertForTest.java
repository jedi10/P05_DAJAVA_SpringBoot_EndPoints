package com.safetynet.alerts.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;

public class JsonConvertForTest {

    private static final ObjectMapper MAPPER2 = new ObjectMapper()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule());

    /**
     * <b>Convert Mvc Result (unit test) with JSON Content to a Java Class</b>
     * <p>JUNIT Tools</p>
     * @param result MvcResult (springframework.test.web.servlet)
     * @param responseClass Java Class
     * @param <T> Type of Class wanted
     * @return Java Class
     */
    public static <T> T parseResponse(MvcResult result, Class<T> responseClass) {
        try {
            String contentAsString = result.getResponse().getContentAsString();
            return MAPPER2.readValue(contentAsString, responseClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //https://stackoverflow.com/questions/18336277/how-to-check-string-in-response-body-with-mockmvc
}
