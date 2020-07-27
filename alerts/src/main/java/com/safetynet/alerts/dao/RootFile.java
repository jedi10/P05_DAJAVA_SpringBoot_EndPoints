package com.safetynet.alerts.dao;

import com.safetynet.alerts.configuration.AlertsProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Repository
public class RootFile {

    @Getter
    @Setter
    private String path;

    @Getter
    @Setter
    private byte[] bytes = null;

    /**
     * <b>Constructor</b>
     * @param alertsProperties All Custom properties of App
     */
    public RootFile(AlertsProperties alertsProperties) {
        this.path = alertsProperties.getJsonFilePath();
        setBytesWithPath(false);
    }

    /**
     * init fileBytes array
     */
    public void setBytesWithPath(boolean forceSetting){
        try {
            if (this.bytes == null || forceSetting){
                this.bytes = Files.readAllBytes(Paths.get(this.getPath()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
