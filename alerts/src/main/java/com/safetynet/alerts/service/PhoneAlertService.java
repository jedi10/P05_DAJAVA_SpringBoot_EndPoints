package com.safetynet.alerts.service;

import com.safetynet.alerts.dao.IFirestationDAO;
import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.models.Firestation;
import com.safetynet.alerts.models.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <b>List phone number of all people located in firestation area</b>
 */
@Slf4j
@Component
public class PhoneAlertService {

    final
    IPersonDAO personDAO;

    final
    IFirestationDAO firestationDAO;

    public PhoneAlertService(IPersonDAO personDAO, IFirestationDAO firestationDAO) {
        this.personDAO = personDAO;
        this.firestationDAO = firestationDAO;
    }

    public List<String> getPhoneAlert(String firestation){
        List<String> result = new ArrayList<>();
        if (null != firestation){
            List<Firestation> firestationList = firestationDAO.findAll();

            List<String> addressListResult = firestationList.stream()
                    .filter(e-> e.getStation().equalsIgnoreCase(firestation))
                    .map(Firestation::getAddress)
                    .collect(Collectors.toList());
            if (!addressListResult.isEmpty()){
                List<Person> personList = personDAO.findAll();
                List<String> phoneListResult = personList.stream()
                        .filter(e ->  addressListResult.contains(e.getAddress()))
                        .map(Person::getPhone)
                        .collect(Collectors.toList());
                result = phoneListResult;
            }
        }
        return result;
    }

}
