package com.safetynet.alerts.configuration;

import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration to make available actuator/httptrace
 * httptrace endpoint needs a HttpTraceRepository bean
 */
@Configuration
public class HttpTraceActuatorConfiguration {

    @Bean
    public HttpTraceRepository httpTraceRepository() {
        return new InMemoryHttpTraceRepository();
    }
}


//https://stackoverflow.com/questions/59115578/httptrace-endpoint-of-spring-boot-actuator-doesnt-exist-anymore-with-spring-b
