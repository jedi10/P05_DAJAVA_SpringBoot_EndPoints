package com.safetynet.alerts.configuration;

import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Useless Since we use a CustomTraceRepository which create a Bean
 * Configuration to make available actuator/httptrace
 * httptrace endpoint needs a HttpTraceRepository bean
 */
//@Configuration
public class HttpTraceActuatorConfiguration {

    /**Useless to create a second bean
    @Bean
    public HttpTraceRepository httpTraceRepository() {
        return new InMemoryHttpTraceRepository();
    }

    @Bean
    @Primary
    public HttpTraceRepository httpTraceRepositoryCustom() {
        return new InMemoryHttpTraceRepository();
    }**/
}

//https://www.baeldung.com/spring-primary
//https://stackoverflow.com/questions/59115578/httptrace-endpoint-of-spring-boot-actuator-doesnt-exist-anymore-with-spring-b
//https://stackoverflow.com/questions/33744875/spring-boot-how-to-log-all-requests-and-responses-with-exceptions-in-single-pl