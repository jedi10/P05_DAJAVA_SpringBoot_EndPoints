package com.safetynet.alerts.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.safetynet.alerts.configuration.AlertsProperties;
import com.safetynet.alerts.dao.IDAO;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.format.SignStyle;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoField.*;

/**
 * <b>Tools based on Jackson API</b>
 * @author Jedy10
 */
public class Jackson {

    private static ObjectMapper mapper = new ObjectMapper()
            .registerModule(new SimpleModule().addSerializer(
                    new LocalDateSerializer(new DateTimeFormatterBuilder()
                            .appendValue(MONTH_OF_YEAR, 2)
                            .appendLiteral('-')
                            .appendValue(DAY_OF_MONTH, 2)
                            .appendLiteral('-')
                            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
                            .toFormatter())))
            .registerModule(new SimpleModule().addDeserializer(LocalDate.class,
                    new LocalDateDeserializer(new DateTimeFormatterBuilder()
                            .appendValue(MONTH_OF_YEAR, 2)
                            .appendLiteral('-')
                            .appendValue(DAY_OF_MONTH, 2)
                            .appendLiteral('-')
                            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
                            .toFormatter())));
            //.configure(JsonWriteFeature.ESCAPE_NON_ASCII.mappedFeature(), true);
            //https://stackoverflow.com/questions/23121765/write-objectnode-to-json-string-with-utf-8-characters-to-escaped-ascii

    /**
     * <b>Convert Json data to ArrayList of Object</b>
     * <p>Search in Bytes Array the List of Object from the given type</p>
     * @param fileByte bytes of the file
     * @param listWrapperName name of list in json file
     * @param workingClass name of argument
     * @param <T> Type of Object we are working on
     * @return List of Object expected
     * @throws IOException Exception Throws
     */
    public static <T> List<T> convertJsonRootDataToJava(byte[] fileByte , String listWrapperName, Class<T> workingClass) throws IOException {
        List<T> expectedJavaObject = null;
        //String className = workingClass.getSimpleName();
        try {
            //Class<?> c = Class.forName("Person");
            //read json file data to String
            //byte[] jsonData = Files.readAllBytes(Paths.get(jsonFilePath));

            //*******************************
            //Transform Json Data in JsonNode
            JsonNode rootNode = mapper.readTree(fileByte);
            //*****************************
            //Get specific part of the data
            JsonNode objectWantedNode = rootNode.path(listWrapperName);
            String listFromJson = objectWantedNode.toString();
            //**********************
            //Convert in Java Object
            expectedJavaObject = mapper.readValue(listFromJson, mapper.getTypeFactory().constructCollectionType(ArrayList.class, workingClass));
            //expectedJavaObject = mapper.readValue(listFromJson, new TypeReference<ArrayList<T>>(){});// T is inoperative !!!
            //System.out.println("expectedJavaObject = " + expectedJavaObject.toString());
        } catch (IOException e) {
            throw new IOException("unexpected file data: check File associate with RootFile", e);
        }
        return expectedJavaObject;
        //https://stackoverflow.com/questions/44589381/how-to-convert-json-string-into-list-of-java-object
        //https://thepracticaldeveloper.com/2018/07/31/java-and-json-jackson-serialization-with-objectmapper/#json-serialization-with-java

        //https://blog.codota.com/how-to-convert-a-java-object-into-a-json-string/
        //https://mkyong.com/java/jackson-how-to-parse-json/
        //https://www.journaldev.com/2324/jackson-json-java-parser-api-example-tutorial
        //https://mkyong.com/java/how-to-check-if-a-file-exists-in-java/
        //String staticJsonData = "{\"persons\": [  { \"firstName\":\"John\", \"lastName\":\"Boyd\", \"address\":\"1509 Culver St\", \"city\":\"Culver\", \"zip\":\"97451\", \"phone\":\"841-874-6512\", \"email\":\"jaboyd@email.com\" },{ \"firstName\":\"Jacob\", \"lastName\":\"Boyd\", \"address\":\"1509 Culver St\", \"city\":\"Culver\", \"zip\":\"97451\", \"phone\":\"841-874-6513\", \"email\":\"drk@email.com\" }]}";
    }

    /**
     * <b>Convert Json data to ArrayList of Object</b>
     * <p>Search in Bytes Array the List of Object from the given type</p>
     * @param fileByte bytes of the file
     * @param workingClass name of argument
     * @param <T> Type of Object we are working on
     * @return List of Object expected
     * @throws IOException Exception Throws
     */
    public static <T> List<T> convertJsonListToJava(byte[] fileByte , Class<T> workingClass) throws IOException {
        List<T> expectedJavaObject = null;
        try {
            //**********************
            //Convert in Java Object
            expectedJavaObject = mapper.readValue(fileByte, mapper.getTypeFactory().constructCollectionType(ArrayList.class, workingClass));

            //expectedJavaObject = mapper.readValue(listFromJson, new TypeReference<ArrayList<T>>(){});// T is inoperative !!!
            //System.out.println("expectedJavaObject = " + expectedJavaObject.toString());
            //https://stackoverflow.com/questions/19333106/jsonmappingexception-out-of-start-array-token
        } catch (IOException e) {
            throw new IOException("unexpected data: check data", e);
        }
        return expectedJavaObject;
        }

    /**
     * <b>Convert Json data to ArrayList of Object</b>
     * <p>Search in Bytes Array the List of Object from the given type</p>
     * @param fileByte bytes of the file
     * @param workingClass name of argument
     * @param <T> Type of Object we are working on
     * @return Object expected
     * @throws IOException Exception Throws
     */
    public static <T> T convertJsonToJava(byte[] fileByte , Class<T> workingClass) throws IOException {
        T expectedJavaObject = null;
        try {
            //**********************
            //Convert in Java Object
            expectedJavaObject = mapper.readValue(fileByte, workingClass);
            //expectedJavaObject = mapper.readValue(listFromJson, new TypeReference<ArrayList<T>>(){});// T is inoperative !!!
            //System.out.println("expectedJavaObject = " + expectedJavaObject.toString());
        } catch (IOException e) {
            throw new IOException("unexpected data: check data", e);
        }
        return expectedJavaObject;
    }

    /**
     * <b>Convert java Object to Json</b>
     * @param javaObject java Object
     * @return JSON string
     */
    public static String convertJavaToJson(Object javaObject){
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

    /**
     * <b>Make a deep copy of an object</b>
     * <p>object have to implement default constructor</p>
     * @param object object to copy
     * @param responseClass name of response
     * @param <T> Type of Class
     * @return deep copy
     */
    public static <T> T deepCopy(Object object, Class<T> responseClass) {
        T result = null;
        try {
            result = mapper.readValue(mapper.writeValueAsString(object), responseClass);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
        //https://www.baeldung.com/java-deep-copy
        //https://www.baeldung.com/java-copy-list-to-another
    }

}


//https://thepracticaldeveloper.com/2018/07/31/java-and-json-jackson-serialization-with-objectmapper/
//https://www.baeldung.com/jackson-serialize-dates