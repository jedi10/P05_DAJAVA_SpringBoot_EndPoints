package com.safetynet.alerts.rest;

import com.safetynet.alerts.dao.IMedicalRecordDAO;
import com.safetynet.alerts.models.MedicalRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

}
