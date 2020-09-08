package com.safetynet.alerts.service;

import com.safetynet.alerts.dao.IFirestationDAO;
import com.safetynet.alerts.dao.IMedicalRecordDAO;
import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.models.Firestation;
import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.service.rto_models.IPersonInfoRTO;
import com.safetynet.alerts.service.rto_models.PersonInfoRTO;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        List<Firestation> firestationList = firestationDAO.findAll();
        List<String> listAddressResult = firestationList.stream()
                .filter(e-> stationNumberList.contains(e.getStation()))
                .distinct()
                .map(Firestation::getAddress)
                .collect(Collectors.toList());
        if (!listAddressResult.isEmpty()){
            List<Person> personList = personDAO.findAll();
            List<MedicalRecord> medicalRecordList = medicalRecordDAO.findAll();
            List<IPersonInfoRTO> personInfoRTOList =  PersonInfoRTO.buildPersonInfoRTOList(personList, medicalRecordList);
            //Filtering list
            Map<String,List<IPersonInfoRTO>> personRTOMapResult = personInfoRTOList.stream()
                    .filter(o ->
                            listAddressResult.contains(o.getAddress())
                    )
                    .collect(Collectors.groupingBy(IPersonInfoRTO::getAddress));
            if(!personInfoRTOList.isEmpty()){
                result = personRTOMapResult;
            }
        }
        return result;
    }
}
