package com.safetynet.alerts.rest;

import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.models.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/person")
public class AdminPersonController {

    @Autowired
    IPersonDAO IPersonDAO;

    /**
     * List of Persons
     * Not requested by client but useful to test
     * @return list of persons
     */
    @RequestMapping(value = "/", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Person>> getAllPersons() {
        List<Person> persons = IPersonDAO.findAll();
        if (persons.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Person>>(persons, HttpStatus.OK);
    }


    @RequestMapping(value = "/{firstName}&{lastName}", method = RequestMethod.GET)
    public ResponseEntity<?> getPerson(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName ) {
        log.info("Fetching Person with first Name {} and lastName {}", firstName, lastName );

        Person person = IPersonDAO.findByName(firstName, lastName);

        return new ResponseEntity<Person>(person, HttpStatus.OK);
    }

    /**
     * redirection
     * @param httpResponse response
     * @throws Exception
     */
    @RequestMapping(value = "/personinfo/{firstName}&{lastName}", method = RequestMethod.GET)
    public void redirectGetPerson(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName,
                             HttpServletResponse httpResponse) throws Exception {

        httpResponse.sendRedirect("/person/"+firstName+"&"+lastName);
    }
}
