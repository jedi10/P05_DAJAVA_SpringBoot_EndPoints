package com.safetynet.alerts.dao;

import com.safetynet.alerts.configuration.AlertsProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RootFileTest {

    private IRootFile rootFile;

    @Mock
    private AlertsProperties alertsProperties;

    private final String rootFileName = "testData.json";

    @Value("${app.alerts.test-json-file-path}")
    private String testJsonFilePath;

    private byte[] byteStub = "{'zac':'learn'}".getBytes();

    @BeforeAll
    void setUp(){
        //GIVEN-WHEN
        when(alertsProperties.getJsonFilePath()).thenReturn(testJsonFilePath);
        rootFile = new RootFile(alertsProperties);
        rootFile.setBytes(byteStub);
    }

    @AfterAll
    void tearDown() {
        rootFile = null;
    }

    @Order(1)
    @Test
    void getPath() {
        //THEN
        assertNotNull(rootFile);
        assertNotNull(rootFile.getPath(),
                "rootFile Path property should be filled and available by the getter accessor.");
        assertEquals(testJsonFilePath, rootFile.getPath(),
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
    void setBytesWithPath_alreadyDone() throws IOException {
        //**************************************
        //Byte has been define manually by setter
        //Nothing should happen on Byte any more
        //**************************************
        //GIVEN
        rootFile.setPath(testJsonFilePath);
        rootFile.setBytes(byteStub);
        //WHEN
        rootFile.setBytesWithPath(false);
        //THEN
        assertEquals(byteStub, rootFile.getBytes(),
                "byte array has been updated: it should not");
    }

    @Order(6)
    @Test
    void setBytesWithPath_normalCase() throws IOException {

        //**************************
        //Byte has never been define
        //Setter should define it
        //**************************
        //GIVEN
        rootFile.setBytes(null);
        //WHEN
        rootFile.setBytesWithPath(false);
        //THEN
        assertNotNull(rootFile.getBytes(),
                "byte array has not been updated: it should" );
    }
    @Order(7)
    @Test
    void setBytesWithPath_alreadyDone2() throws IOException {
        //**************************************
        //Byte has been define once
        //Nothing should happen on Byte any more
        //**************************************
        //GIVEN
        byte[] byteFromObjectAtStart = rootFile.getBytes();
        //WHEN
        rootFile.setBytesWithPath(false);
        //THEN
        assertEquals(byteFromObjectAtStart, rootFile.getBytes(),
                "byte array has been updated: it should not");
    }
    @Order(8)
    @Test
    void setBytesWithPath_force() throws IOException {
        //**************************************
        //Byte has been define once
        //we force setter to changer it
        //**************************************
        //GIVEN
        byte[] byteFromObjectAtStart = rootFile.getBytes();
        //WHEN
        rootFile.setBytesWithPath(true);
        //THEN
        assertNotEquals(byteFromObjectAtStart, rootFile.getBytes(),
                "byte array has not been updated: it should");
    }

    @Order(9)
    @Test
    void setBytesWithWrongPath() {
        //**************************************
        //Byte has never been define
        //Setter will use a wrong path: Error !
        //**************************************
        //GIVEN
        when(alertsProperties.getJsonFilePath()).thenReturn("wrong/path/"+rootFileName);
        rootFile = new RootFile(alertsProperties);
        //WHEN-THEN
        Exception exception =  assertThrows(IOException.class, () -> {
            rootFile.setBytesWithPath(false);
        });
        assertTrue(exception.getMessage().contains("File don't exist"));
    }
}