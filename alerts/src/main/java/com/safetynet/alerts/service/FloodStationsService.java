package com.safetynet.alerts.service;

import com.safetynet.alerts.dao.IFirestationDAO;
import com.safetynet.alerts.dao.IMedicalRecordDAO;
import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.service.rto_models.IPersonInfoRTO;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class FloodStationsService {

    final
    IPersonDAO personDAO;

    final
    IMedicalRecordDAO medicalRecordDAO;

    final
    IFirestationDAO firestationDAO;

    public FloodStationsService(IPersonDAO personDAO, IMedicalRecordDAO medicalRecordDAO, IFirestationDAO firestationDAO) {
        this.personDAO = personDAO;
        this.medicalRecordDAO = medicalRecordDAO;
        this.firestationDAO = firestationDAO;
    }

    public Map<String, List<IPersonInfoRTO>> getFloodStations(@NonNull List<String> stationNumberList){
        Map<String, List<IPersonInfoRTO>> result = null;


        return result;
    }
}
