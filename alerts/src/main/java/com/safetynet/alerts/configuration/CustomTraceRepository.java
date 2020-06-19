package com.safetynet.alerts.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Customizing HttpTraceRepository
 * A Way to have a hand on Traces with code
 */
@Repository
@Primary
public class CustomTraceRepository implements HttpTraceRepository {

    private final Logger logger = LoggerFactory.getLogger(CustomTraceRepository.class);

    AtomicReference<HttpTrace> lastTrace = new AtomicReference<>();

    @Override
    public List<HttpTrace> findAll() {
        return Collections.singletonList(lastTrace.get());
    }

    @Override
    public void add(HttpTrace trace) {

        if ("GET".equals(trace.getRequest().getMethod())) {
            lastTrace.set(trace);
            if (200 == lastTrace.get().getResponse().getStatus()){
                logger.info("Actuator HttpTrace Added: URL: {} : RESPONSE STATUS: {}",
                        lastTrace.get().getRequest().getUri(),
                        lastTrace.get().getResponse().getStatus());
            } else {
                logger.warn("Actuator HttpTrace Added: URL: {} : RESPONSE STATUS: {}",
                        lastTrace.get().getRequest().getUri(),
                        lastTrace.get().getResponse().getStatus());
            }
        }
    }

}


//https://www.baeldung.com/spring-boot-actuator-http