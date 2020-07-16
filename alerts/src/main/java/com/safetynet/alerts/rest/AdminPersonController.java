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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
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
        log.info("Fetching Person List");
        if (persons.isEmpty()) {
            log.warn("Person List is empty");
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Person>>(persons, HttpStatus.OK);
    }


    @RequestMapping(value = "/{firstName}&{lastName}", method = RequestMethod.GET)
    public ResponseEntity<?> getPerson(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName ) {
        log.info("Fetching Person with first Name {} and lastName {}", firstName, lastName );

        Person person = personDAO.findByName(firstName, lastName);
        if(person == null){
            log.warn("Fetching Person Aborted: {} {} not found", firstName, lastName);
            return ResponseEntity.notFound().build();
        }

        return new ResponseEntity<Person>(person, HttpStatus.OK);
    }

    @PostMapping(value = "/", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> createPerson(@RequestBody Person person) {
        log.info("Creating Person : {}", person);

        Person result = personDAO.save(person);

        if (result == null){
            log.warn("Creating Person Aborted: {} {} already exist", person.getFirstName(), person.getLastName());
            return ResponseEntity.noContent().build();
        }

        //HttpHeaders headers = new HttpHeaders();
        //injection in param : UriComponentsBuilder ucBuilder
        //headers.setLocation(ucBuilder.path("/person/{firstName}&{lastName}").buildAndExpand(person.getFirstName(), person.getLastName()).toUri());
        //return new ResponseEntity<String>(headers, HttpStatus.CREATED);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{firstName}&{lastName}")
                .buildAndExpand(person.getFirstName(), person.getLastName())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @RequestMapping(value = "/{firstName}&{lastName}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePerson(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName,
                                          @RequestBody Person person) {
        log.info("Updating Person with first Name {} and lastName {}", firstName, lastName );

        Person result = personDAO.update(person);

        if (result == null){
            log.warn("Updating Person Aborted: {} {} not found", person.getFirstName(), person.getLastName());
            return ResponseEntity.notFound().build();
        }

        return new ResponseEntity<Person>(person, HttpStatus.OK);
    }

    @RequestMapping(value = "/{firstName}&{lastName}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePerson(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName) {
        log.info("Fetching & Deleting User with first Name {} and lastName {}", firstName, lastName );

        Person person = personDAO.findByName(firstName, lastName);
        if(person == null){
            log.warn("Deleting Person Aborted: {} {} not found", firstName, lastName);
            return ResponseEntity.notFound().build();
        }
        personDAO.delete(person);
        return new ResponseEntity<Person>(HttpStatus.NO_CONTENT);
    }
}
