package com.safetynet.alerts.rest;

import com.safetynet.alerts.dao.IMedicalRecordDAO;
import com.safetynet.alerts.models.MedicalRecord;
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
@RequestMapping("/medicalrecord")
public class AdminMedicalRecordController {

    @Autowired
    IMedicalRecordDAO medicalRecordDAO;

    /**
     * List of Medical Record
     * Not requested by client but useful to test
     * @return list of Medical Record
     */
    @RequestMapping(value = "/", method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<MedicalRecord>> getAllMedicalRecord() {
        List<MedicalRecord> medicalRecords = medicalRecordDAO.findAll();
        log.info("Fetching Medical Record List");
        if (medicalRecords.isEmpty()) {
            log.warn("medicalRecord List is empty");
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<MedicalRecord>>(medicalRecords, HttpStatus.OK);
    }

    @GetMapping(value = "/{firstName}&{lastName}")
    public ResponseEntity<?> getMedicalRecord(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName ) {
        log.info("Fetching Medical Record with first Name {} and lastName {}", firstName, lastName );

        MedicalRecord medicalRecord = medicalRecordDAO.findByName(firstName, lastName);
        if(medicalRecord == null){
            log.warn("Fetching Medical Record Aborted: {} {} not found", firstName, lastName);
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<MedicalRecord>(medicalRecord, HttpStatus.OK);
    }

    @PostMapping(value = "/")
    public ResponseEntity<?> createMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        log.info("Creating Medical Record : {}", medicalRecord);

        MedicalRecord result = medicalRecordDAO.save(medicalRecord);

        if (result == null){
            log.warn("Creating Medical Record Aborted: {} {} already exist",
                    medicalRecord.getFirstName(), medicalRecord.getLastName());
            return ResponseEntity.noContent().build();
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{firstName}&{lastName}")
                .buildAndExpand(medicalRecord.getFirstName(), medicalRecord.getLastName())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping(value = "/")
    public ResponseEntity<?> updateMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        log.info("Updating Medical Record with first Name {} and lastName {}",
                medicalRecord.getFirstName(), medicalRecord.getLastName());

        MedicalRecord result = medicalRecordDAO.update(medicalRecord);

        if (result == null){
            log.warn("Updating Medical Record Aborted: {} {} not found",
                    medicalRecord.getFirstName(), medicalRecord.getLastName());
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<MedicalRecord>(medicalRecord, HttpStatus.OK);
    }

}
