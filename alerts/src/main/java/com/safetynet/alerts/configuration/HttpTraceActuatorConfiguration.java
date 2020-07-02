package com.safetynet.alerts.configuration;

import com.safetynet.alerts.utils.CustomTraceRepository;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration to make available actuator/httptrace
 * httptrace endpoint needs a HttpTraceRepository bean since Spring Boot 2.2
 *
 * See also CustomTraceRepository which intercept HttpTrace to Log them
 * @see CustomTraceRepository
 */
@Configuration
public class HttpTraceActuatorConfiguration {

    @Bean
    @Primary
    public HttpTraceRepository httpTraceRepository() {
        return new CustomTraceRepository();
    }
}

//https://www.baeldung.com/spring-primary
//https://stackoverflow.com/questions/59115578/httptrace-endpoint-of-spring-boot-actuator-doesnt-exist-anymore-with-spring-b
//https://stackoverflow.com/questions/33744875/spring-boot-how-to-log-all-requests-and-responses-with-exceptions-in-single-pl