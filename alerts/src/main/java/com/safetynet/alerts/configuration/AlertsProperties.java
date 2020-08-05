package com.safetynet.alerts.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * <b>Activate Spring Configuration Metadata</b>
 * <p>application.properties can define variable declared in this class</p>
 */
@Configuration
@ConfigurationProperties(prefix = "app.alerts")
public class AlertsProperties {

    /**
     * Json File Path
     * App need a file to load all Data at start
     * application.properties can overcome this value
     */
    @Setter
    @Getter
    private String jsonFilePath = "src/main/resources/data.json";
}


//https://stackoverflow.com/questions/32058814/spring-boot-custom-variables-in-application-properties
//https://www.baeldung.com/intellij-resolve-spring-boot-configuration-properties
