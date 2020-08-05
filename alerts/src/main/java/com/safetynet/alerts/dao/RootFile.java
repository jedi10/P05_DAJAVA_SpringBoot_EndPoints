package com.safetynet.alerts.dao;

import com.safetynet.alerts.configuration.AlertsProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * <b>Manage Application File properties</b>
 */
@Repository
public class RootFile implements IRootFile {

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
        try {
            setBytesWithPath(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * init fileBytes array
     */
    @Override
    public void setBytesWithPath(boolean forceSetting) throws IOException {
        try {
            if (this.bytes == null || forceSetting){
                String fileString = Files.readString(Paths.get(this.getPath()));
                this.bytes = fileString.getBytes(StandardCharsets.UTF_8);
                //this.bytes = Files.readAllBytes(Paths.get(this.getPath()));
            }
        } catch (IOException e) {
            throw new IOException("File don't exist: check the file path", e);
        }
    }
}
