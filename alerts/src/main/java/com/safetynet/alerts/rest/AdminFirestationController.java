package com.safetynet.alerts.rest;

import com.safetynet.alerts.dao.IFirestationDAO;
import com.safetynet.alerts.models.Firestation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/firestation")
public class AdminFirestationController {

    @Autowired
    IFirestationDAO firestationDAO;

    private Firestation firestation1 = new Firestation("1509 Culver St","3");
    private Firestation firestation2 = new Firestation("29 15th St", "2");

    /**
     * List of Persons
     * Not requested by client but useful to test
     * @return list of persons
     */
    @RequestMapping(value = "/", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Firestation>> getAllPFirestations() {
        List<Firestation> firestations = firestationDAO.findAll();
        log.info("Fetching Firestation List");
        if (firestations.isEmpty()) {
            log.warn("Firestation List is empty");
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Firestation>>(firestations, HttpStatus.OK);
    }

    @RequestMapping(value = "/{address}", method = RequestMethod.GET)
    public ResponseEntity<?> getFirestation(@PathVariable("address") String address ) {
        log.info("Fetching Firestation with address  {} ", address);

        Firestation firestation = firestationDAO.findByAddress(address);
        if(firestation == null){
            log.warn("Fetching Firestation Aborted: address not found: {}", address);
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<Firestation>(firestation, HttpStatus.OK);
    }

}
