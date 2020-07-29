package com.safetynet.alerts.dao;

import com.safetynet.alerts.configuration.AlertsProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RootFileTest {

    @Autowired
    private RootFile rootFile;

    @Autowired
    private AlertsProperties alertsProperties;

    private String rootFileName = "testData.json";

    private String pathRepository = "src/test/resources/";

    private byte[] byteStub = "{'zac':'learn'}".getBytes();

    @BeforeAll
    void setUp(){
        //GIVEN-WHEN
        rootFile.setPath(pathRepository+rootFileName);
        rootFile.setBytes(byteStub);
    }

    @Order(1)
    @Test
    void getPath() {
        //THEN
        assertNotNull(rootFile);
        assertNotNull(rootFile.getPath(),
                "rootFile Path property should be filled and available by the getter accessor.");
        assertEquals(pathRepository+rootFileName, rootFile.getPath(),
                "the path in rootFile.path should contain the same path as the one given before the test");
    }

    @Order(2)
    @Test
    void setPath() {
        //Given
        String newPath = "src/file";
        //When
        rootFile.setPath(newPath);
        //Then
        assertEquals(newPath, rootFile.getPath());
    }

    @Order(3)
    @Test
    void getBytes() {
        //THEN
        assertNotNull(rootFile.getBytes(),
                "rootFile Bytes property should be filled and available by the getter accessor.");
        assertEquals(byteStub, rootFile.getBytes(),
                "the bytes in rootFile.bytes should contain the same bytes as the one given before the test");
    }

    @Order(4)
    @Test
    void setBytes() {
        //Given
        byte[] byteStub2 = "Hello".getBytes();
        //When
        rootFile.setBytes(byteStub2);
        //Then
        assertEquals(byteStub2,  rootFile.getBytes());
    }

    @Order(5)
    @Test
    void setBytesWithPath() {
        //GIVEN
        rootFile.setPath(pathRepository+rootFileName);
        rootFile.setBytes(byteStub);
        //WHEN
        rootFile.setBytesWithPath(false);
        //THEN
        assertEquals(byteStub, rootFile.getBytes(), "byte array has been updated: it should not");
        //assertEquals(expectedBytes.length, rootFile.getBytes().length);

        //GIVEN
        rootFile.setBytes(null);
        //WHEN
        rootFile.setBytesWithPath(false);
        //THEN
        assertNotNull(rootFile.getBytes(), "byte array has not been updated: it should" );

        //GIVEN
        byte[] byteFromObjectAtStart = rootFile.getBytes();
        //WHEN
        rootFile.setBytesWithPath(false);
        //THEN
        assertEquals(byteFromObjectAtStart, rootFile.getBytes(), "byte array has been updated: it should not");

        //WHEN
        rootFile.setBytesWithPath(true);
        //THEN
        assertNotEquals(byteFromObjectAtStart, rootFile.getBytes(), "byte array has not been updated: it should");
    }
}