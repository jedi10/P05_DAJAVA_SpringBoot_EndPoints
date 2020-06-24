package com.safetynet.alerts.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
public class CustomErrorController implements ErrorController {

    public CustomErrorController() {}

    @GetMapping(value = "/error", produces = {"text/html"} )
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        log.error("Error with status code " + status + " happened.");
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                return "Error 404: resource doesn't exist";
            }
            else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "Error 500: internal Server Error ";
            }
            else {
                return "Error"+ status;
            }
        }
        return "Undefined Error";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

}



//https://www.baeldung.com/spring-boot-custom-error-page