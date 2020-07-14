package com.safetynet.alerts.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class JsonConvert {

    private static ObjectMapper mapper = new ObjectMapper();

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
