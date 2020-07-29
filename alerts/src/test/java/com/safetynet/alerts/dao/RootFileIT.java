package com.safetynet.alerts.dao;

import com.safetynet.alerts.configuration.AlertsProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RootFileIT {

    @Autowired
    private RootFile rootFile;

    @Autowired
    private AlertsProperties alertsProperties;

    private String rootFileName = "data.json";

    @Order(1)
    @Test
    void getPath() {
        //***********************************************
        //Assert that alertsProperties define File Path
        //***********************************************
        assertNotNull(alertsProperties);
        String expectedPath = alertsProperties.getJsonFilePath();
        assertNotNull(expectedPath);
        //**************************************************
        //Assert that File Path is present in RootFile Class
        //**************************************************
        assertNotNull(rootFile.getPath(), "We should have a File path in rootFile Class");
        assertEquals(expectedPath, rootFile.getPath(),
                "We should have File path in rootFile Class, and it has to be the same the one defined in alertsProperties"
        );
    }

    @Order(2)
    @Test
    void searchForFile(){
        //*******************************************
        //Search file as if we don't know where it is
        //*******************************************
        //Test in Target
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(rootFileName).getFile());
        String absolutePath = file.getAbsolutePath();
        assertTrue(absolutePath.endsWith(rootFileName));

        //***********************************************
        //Search file in localisation we know (resources)
        //***********************************************
        //Test in Project
        Path resourceDirectory = Paths.get("src","main","resources", rootFileName);
        String absolutePath2 = resourceDirectory.toFile().getAbsolutePath();
        assertTrue(absolutePath2.endsWith(rootFileName));

        //******************************************************
        //Check path provided with Spring Configuration Metadata
        //******************************************************
        assertNotNull(rootFile.getPath());
        assertTrue(absolutePath2.endsWith(rootFile.getPath()));

        File f = new File(rootFile.getPath());
        assertTrue(f.exists() && !f.isDirectory());
        //******************************************************
        //https://www.baeldung.com/junit-src-test-resources-directory-path
    }

    @Order(3)
    @Test
    void setBytesWithPath() {
        //GIVEN
        assertNotNull(rootFile.getBytes());
        byte[] bytesAtStart = rootFile.getBytes();
        //WHEN
        rootFile.setBytesWithPath(true);
        //THEN
        assertNotEquals(bytesAtStart, rootFile.getBytes());
        assertEquals(bytesAtStart.length, rootFile.getBytes().length);
    }
}
