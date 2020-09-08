package com.safetynet.alerts.service;

import com.safetynet.alerts.dao.IMedicalRecordDAO;
import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.service.rto_models.IPersonInfoRTO;
import com.safetynet.alerts.service.rto_models.PersonInfoRTO;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static java.util.stream.Collectors.groupingBy;

/**
 *  <b>List Children located at address given (with all adults too)</b>
 */
@Slf4j
@Component
public class ChildAlertService {

    final
    IPersonDAO personDAO;

    final
    IMedicalRecordDAO medicalRecordDAO;

    public ChildAlertService(IPersonDAO personDAO, IMedicalRecordDAO medicalRecordDAO) {
        this.personDAO = personDAO;
        this.medicalRecordDAO = medicalRecordDAO;
    }

    public Map<IPersonInfoRTO.HumanCategory, List<IPersonInfoRTO>> getChildAlert(@NonNull String address){

        Map<IPersonInfoRTO.HumanCategory, List<IPersonInfoRTO>> result = null;
        //Search Data
        List<Person> personList = personDAO.findAll();
        List<MedicalRecord> medicalRecordList = medicalRecordDAO.findAll();
        //Build RTO Object
        List<IPersonInfoRTO> personInfoRTOList =
                PersonInfoRTO.buildPersonInfoRTOList(personList, medicalRecordList);
        //List Filtering and Build a Map grouping by HumanCategory
        Map<IPersonInfoRTO.HumanCategory, List<IPersonInfoRTO>> resultTemp = personInfoRTOList.stream()
                .filter(o -> address.equalsIgnoreCase(o.getAddress()))
                .collect(groupingBy(IPersonInfoRTO::getHumanCategory));
        //Return result only when we have at least one Child
        if(null != resultTemp.get(IPersonInfoRTO.HumanCategory.CHILDREN)){
            result = resultTemp;
        } else {
            //If no child, Empty HashMap
            result = new HashMap<>();
        }

        return result;
    }
}


//https://www.baeldung.com/java-groupingby-collector
// https://grokonez.com/java/java-8/how-to-use-java-8-stream-collectors-groupingby-examples