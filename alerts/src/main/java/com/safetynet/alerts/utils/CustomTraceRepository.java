package com.safetynet.alerts.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <b>Customizing HttpTraceRepository</b>
 * <p>Allow to Log HttpTrace</p>
 */
@Slf4j
@Repository
public class CustomTraceRepository implements HttpTraceRepository {

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
                log.info("Actuator HttpTrace Added: URL: {} : RESPONSE STATUS: {}",
                        lastTrace.get().getRequest().getUri(),
                        lastTrace.get().getResponse().getStatus());
            } else {
                log.warn("Actuator HttpTrace Added: URL: {} : RESPONSE STATUS: {}",
                        lastTrace.get().getRequest().getUri(),
                        lastTrace.get().getResponse().getStatus());
            }
        }
    }
}


//https://www.baeldung.com/spring-boot-actuator-http
//https://howtodoinjava.com/spring-boot2/logging/logging-with-lombok/