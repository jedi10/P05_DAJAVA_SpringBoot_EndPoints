package com.safetynet.alerts.dao;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class RootFileTest {

    @Autowired
    private RootFile rootFile;

    private String rootFileName = "data.json";

    @Test
    void searchForRootFile(){
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(rootFileName).getFile());
        String absolutePath = file.getAbsolutePath();

        assertTrue(absolutePath.endsWith(rootFileName));
        //https://www.baeldung.com/junit-src-test-resources-directory-path
    }

    @Test
    void setBytesWithPath() {
    }

    @Test
    void getPath() {
    }

    @Test
    void getBytes() {
    }

    @Test
    void setPath() {
    }

    @Test
    void setBytes() {
    }
}