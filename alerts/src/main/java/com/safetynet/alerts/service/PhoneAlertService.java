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

@Slf4j
@Component
public class PhoneAlertService {

    @Autowired
    IPersonDAO personDAO;

    @Autowired
    IFirestationDAO firestationDAO;

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
