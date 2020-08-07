package com.safetynet.alerts.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
public class PublicAppController {


    /**
     * redirection to getPerson EndPoint Controller
     * @see AdminPersonController#getPerson(String, String)
     * @param firstName firstName
     * @param lastName lastName
     * @param httpResponse response
     * @throws Exception exception
     */
    @RequestMapping(value = "/personinfo/{firstName}&{lastName}", method = RequestMethod.GET)
    public void redirectGetPerson(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName,
                                  HttpServletResponse httpResponse) throws Exception {

        httpResponse.sendRedirect("/person/"+firstName+"&"+lastName);
    }
}
