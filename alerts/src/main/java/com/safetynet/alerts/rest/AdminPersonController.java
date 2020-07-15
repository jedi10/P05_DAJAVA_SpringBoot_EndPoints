package com.safetynet.alerts.rest;

import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.models.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/person")
public class AdminPersonController {

    @Autowired
    IPersonDAO personDAO;

    /**
     * List of Persons
     * Not requested by client but useful to test
     * @return list of persons
     */
    @RequestMapping(value = "/", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Person>> getAllPersons() {
        List<Person> persons = personDAO.findAll();
        if (persons.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Person>>(persons, HttpStatus.OK);
    }


    @RequestMapping(value = "/{firstName}&{lastName}", method = RequestMethod.GET)
    public ResponseEntity<?> getPerson(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName ) {
        log.info("Fetching Person with first Name {} and lastName {}", firstName, lastName );

        Person person = personDAO.findByName(firstName, lastName);

        return new ResponseEntity<Person>(person, HttpStatus.OK);
    }

    @PostMapping(value = "/", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> createPerson(@RequestBody Person person, UriComponentsBuilder ucBuilder) {
        log.info("Creating Person : {}", person);

        Person result = personDAO.save(person);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/person/{firstName}&{lastName}").buildAndExpand(person.getFirstName(), person.getLastName()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{firstName}&{lastName}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePerson(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName,
                                          @RequestBody Person person) {
        log.info("Updating Person with first Name {} and lastName {}", firstName, lastName );

        Person result = personDAO.update(person);

        return new ResponseEntity<Person>(person, HttpStatus.OK);
    }

    @RequestMapping(value = "/{firstName}&{lastName}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePerson(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName) {
        log.info("Fetching & Deleting User with first Name {} and lastName {}", firstName, lastName );
        boolean result = false;

        Person person = personDAO.findByName(firstName, lastName);
        if(person != null){
            result = personDAO.delete(person);
        }
        return new ResponseEntity<Person>(HttpStatus.NO_CONTENT);
    }
}
