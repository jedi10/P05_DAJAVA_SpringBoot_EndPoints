package com.safetynet.alerts.service;

import com.safetynet.alerts.dao.IFirestationDAO;
import com.safetynet.alerts.dao.IMedicalRecordDAO;
import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.models.Firestation;
import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.service.rto_models.IPersonInfoRTO;
import com.safetynet.alerts.service.rto_models.PersonInfoRTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FireAddressService {

    @Autowired
    IPersonDAO personDAO;

    @Autowired
    IMedicalRecordDAO medicalRecordDAO;

    @Autowired
    IFirestationDAO firestationDAO;


    public Map<String, List> getFireAddress(String address) {
        Map<String, List> result = new HashMap<>();
        List<IPersonInfoRTO> personInfoRTOList = new ArrayList<>();
        List<String> fireStationNumber = new ArrayList<>();
        if (null != address){
            Firestation firestation = firestationDAO.findByAddress(address);
            if ( null != firestation){
                fireStationNumber.add(firestation.getStation());
            }

            List<Person> personList = personDAO.findAll();
            List<MedicalRecord> medicalRecordList = medicalRecordDAO.findAll();
            List<IPersonInfoRTO> personInfoRTOListFull = PersonInfoRTO.buildPersonInfoRTOList(personList, medicalRecordList);
            if (!personInfoRTOListFull.isEmpty()){
                personInfoRTOList = personInfoRTOListFull.stream()
                        .filter(e-> e.getAddress().equalsIgnoreCase(address))
                        .collect(Collectors.toList());
            }
        }
        result.put("Persons", personInfoRTOList);
        result.put("Firestation", fireStationNumber);
        return result;
    }
}



//https://www.codeflow.site/fr/article/java-hashmap