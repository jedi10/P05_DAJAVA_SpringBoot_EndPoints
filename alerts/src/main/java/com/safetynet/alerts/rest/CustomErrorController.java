package com.safetynet.alerts.rest;

import com.safetynet.alerts.models.CustomErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Custom Error Controller With JSON Response
 *
 * @author Jedi10
 * @since 25.06.2020
 */
@Slf4j
@RestController
@RequestMapping("/")
public class CustomErrorController implements ErrorController {

    public CustomErrorController() {}

    @Autowired
    private ErrorAttributes errorAttributes ;

    @GetMapping(value = "error", produces = {MediaType.APPLICATION_JSON_VALUE})
    public CustomErrorResponse handleError(HttpServletRequest request, HttpServletResponse response) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        log.error("Error with status code " + status + " happened. ");

        return new CustomErrorResponse(response.getStatus(), getErrorAttributes(request));
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    private Map<String, Object> getErrorAttributes(HttpServletRequest request) {
        WebRequest webRequest = new ServletWebRequest(request);
        return errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());
    }
}



//https://www.baeldung.com/spring-boot-custom-error-page
//https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc
//JSON Error Response https://gist.github.com/jonikarppinen/662c38fb57a23de61c8b
//https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-error-handling
//https://stackoverflow.com/questions/25356781/spring-boot-remove-whitelabel-error-page