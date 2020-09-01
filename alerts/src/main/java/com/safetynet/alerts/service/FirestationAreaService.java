package com.safetynet.alerts.service;

import com.safetynet.alerts.dao.IFirestationDAO;
import com.safetynet.alerts.dao.IMedicalRecordDAO;
import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.models.Firestation;
import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.service.rto_models.FirestationAreaRTO;
import com.safetynet.alerts.service.rto_models.IPersonInfoRTO;
import com.safetynet.alerts.service.rto_models.PersonInfoRTO;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FirestationAreaService {

    final
    IPersonDAO personDAO;

    final
    IMedicalRecordDAO medicalRecordDAO;

    final
    IFirestationDAO firestationDAO;

    public FirestationAreaService(IPersonDAO personDAO, IMedicalRecordDAO medicalRecordDAO,
                                  IFirestationDAO firestationDAO) {
        this.personDAO = personDAO;
        this.medicalRecordDAO = medicalRecordDAO;
        this.firestationDAO = firestationDAO;
    }

    public FirestationAreaRTO getFirestationArea(@NonNull String firestation){
        FirestationAreaRTO firestationAreaRTO = null;
        //Get Address List linked with firestation number
        List<Firestation> firestationList = firestationDAO.findAll();
        List<String> firestationAddressListResult = firestationList.stream()
                .filter(e-> e.getStation().equalsIgnoreCase(firestation))
                .map(Firestation::getAddress)
                .collect(Collectors.toList());

        if (!firestationAddressListResult.isEmpty()) {
            //Get Persons List having all address finded
            List<Person> personList = personDAO.findAll();
            List<MedicalRecord> medicalRecordList = medicalRecordDAO.findAll();
            List<IPersonInfoRTO> personInfoRTOListFull =
                    PersonInfoRTO.buildPersonInfoRTOList(personList, medicalRecordList);
            List<IPersonInfoRTO> personInfoRTOListFiltered = personInfoRTOListFull.stream()
                    .filter(e-> firestationAddressListResult.contains(e.getAddress()))
                    .collect(Collectors.toList());
            if (!personInfoRTOListFiltered.isEmpty()){
                //Results are treated to be exposed in a view
               firestationAreaRTO = new FirestationAreaRTO(personInfoRTOListFiltered);
            }
        }
        return firestationAreaRTO;
    }
}


//https://www.baeldung.com/java-groupingby-collector
//https://www.concretepage.com/java/java-8/java-stream-count