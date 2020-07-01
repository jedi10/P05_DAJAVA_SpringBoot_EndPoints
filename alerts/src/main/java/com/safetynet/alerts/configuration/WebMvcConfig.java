package com.safetynet.alerts.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
//@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.mediaType("json", MediaType.APPLICATION_JSON);
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }
}


//https://www.baeldung.com/web-mvc-configurer-adapter-deprecated
//https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-view-jackson