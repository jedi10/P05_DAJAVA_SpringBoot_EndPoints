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
     * App needs a file to load all Data at start
     * application.properties can overcome this value
     */
    @Setter
    @Getter
    private String jsonFilePath = "src/main/resources/data.json";

    /**
     * Test Json File Path
     * App Test need a file to load all Data
     * application.properties can overcome this value
     */
    @Setter
    @Getter
    private String testJsonFilePath = "src/test/resources/testData.json";

    /**
     * Test Server URL
     * App need a server to launch execute End To End Tests
     * application.properties can overcome this value
     */
    @Setter
    @Getter
    private String testServerUrlWithoutPort = "https://localhost:";
}


//https://stackoverflow.com/questions/32058814/spring-boot-custom-variables-in-application-properties
//https://www.baeldung.com/intellij-resolve-spring-boot-configuration-properties
