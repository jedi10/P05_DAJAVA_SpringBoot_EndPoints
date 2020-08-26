package com.safetynet.alerts.service;

import com.safetynet.alerts.dao.IFirestationDAO;
import com.safetynet.alerts.dao.IMedicalRecordDAO;
import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.service.rto_models.IPersonInfoRTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class FireAddressService {

    @Autowired
    IPersonDAO personDAO;

    @Autowired
    IMedicalRecordDAO medicalRecordDAO;

    @Autowired
    IFirestationDAO firestationDAO;

    List<IPersonInfoRTO> personInfoRTOList;

    public Map<String, List> getFireAddress(String address) {
        Map<String, List> result = new HashMap<>();
        List<IPersonInfoRTO> personInfoRTOList = new ArrayList<>();
        List<String> fireStationNumber = new ArrayList<>();






        result.put("Persons", personInfoRTOList);
        result.put("Firestation", fireStationNumber);
        return result;
    }
}



//https://www.codeflow.site/fr/article/java-hashmap