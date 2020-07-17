package com.safetynet.alerts.rest;

import com.safetynet.alerts.dao.IFirestationDAO;
import com.safetynet.alerts.models.Firestation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/firestation")
public class AdminFirestationController {

    @Autowired
    IFirestationDAO firestationDAO;

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

    @PostMapping(value = "/", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> createFirestation(@RequestBody Firestation firestation) {
        log.info("Creating Firestation : {}", firestation);

        Firestation result = firestationDAO.save(firestation);

        if (result == null){
            log.warn("Creating Firestation Aborted: {} already exist", firestation.getAddress());
            return ResponseEntity.noContent().build();
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{address}")
                .buildAndExpand(firestation.getAddress())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping(value = "/{address}")
    public ResponseEntity<?> updateFirestation(@PathVariable("address") String address,
                                               @RequestBody Firestation firestation) {
        log.info("Updating Firestation with Address: {}", address );

        Firestation result = firestationDAO.update(firestation);

        if (result == null){
            log.warn("Updating Firestation Aborted: Address {} not found", firestation.getAddress());
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<Firestation>(firestation, HttpStatus.OK);
    }

}
