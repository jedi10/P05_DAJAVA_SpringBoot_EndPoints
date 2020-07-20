package com.safetynet.alerts.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.io.IOException;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.format.SignStyle;

import static java.time.temporal.ChronoField.*;

public class JsonConvert {

    private static ObjectMapper mapper = new ObjectMapper()
            .registerModule(new SimpleModule().addSerializer(
                    new LocalDateSerializer(new DateTimeFormatterBuilder()
                            .appendValue(MONTH_OF_YEAR, 2)
                            .appendLiteral('-')
                            .appendValue(DAY_OF_MONTH, 2)
                            .appendLiteral('-')
                            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
                            .toFormatter())));

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


//https://thepracticaldeveloper.com/2018/07/31/java-and-json-jackson-serialization-with-objectmapper/
//https://www.baeldung.com/jackson-serialize-dates