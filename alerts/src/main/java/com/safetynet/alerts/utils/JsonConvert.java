package com.safetynet.alerts.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

public class JsonConvert {

    private static ObjectMapper mapper = new ObjectMapper();

    /**
     * <b>Convert java Object to Json</b>
     * @param javaObject java Object
     * @return JSON string
     */
    public static String feedWithJava(Object javaObject){
        String expectedJson = null;
        try {
            expectedJson = mapper.writeValueAsString(javaObject);
            //System.out.println("ResultingJSONstring = " + expectedJson);
            //System.out.println(json);
            //https://blog.codota.com/how-to-convert-a-java-object-into-a-json-string/
        } catch (
                JsonProcessingException e) {
                    e.printStackTrace();
        }
        return expectedJson;
    }

}
