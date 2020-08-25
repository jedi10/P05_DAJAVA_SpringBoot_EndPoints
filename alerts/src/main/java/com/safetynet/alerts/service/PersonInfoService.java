package com.safetynet.alerts.service;

import com.safetynet.alerts.dao.IMedicalRecordDAO;
import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.service.rto_models.IPersonInfoRTO;
import com.safetynet.alerts.service.rto_models.PersonInfoRTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PersonInfoService {

    @Autowired
    IPersonDAO personDAO;

    @Autowired
    IMedicalRecordDAO medicalRecordDAO;

    String firstName;

    String lastName;

    List<IPersonInfoRTO> personInfoRTOList;

    public List<IPersonInfoRTO> getPersonInfo(String firstName, String lastName) {
        List<IPersonInfoRTO> result = new ArrayList<>();
        if (firstName != null && lastName != null) {
            //Debounce functionality
            if (firstName.equals(this.firstName) &&
                    lastName.equals(this.lastName) &&
                    this.personInfoRTOList != null) {
                result = this.personInfoRTOList;
            } else {
                this.firstName = firstName;
                this.lastName = lastName;
                this.personInfoRTOList = new ArrayList<>();
                List<Person> personList = personDAO.findAll();
                List<MedicalRecord> medicalRecordList = medicalRecordDAO.findAll();
                List<IPersonInfoRTO> personInfoRTOFull =  PersonInfoRTO.buildPersonInfoRTOList(personList, medicalRecordList);
                //Filtering
                //Put the perfect Match in Result List
                IPersonInfoRTO personInfoRTOPerfectResult = personInfoRTOFull.stream()
                        .filter(o ->
                                    o.getFirstName().equalsIgnoreCase(firstName) &&
                                    o.getLastName().equalsIgnoreCase(lastName))
                        .findAny()
                        .orElse(null);
                //https://www.baeldung.com/find-list-element-java
                if (null != personInfoRTOPerfectResult){
                    personInfoRTOFull.remove(personInfoRTOPerfectResult);
                    this.personInfoRTOList.add(personInfoRTOPerfectResult);
                }
                //Put Last Name Match in Result List
                List<IPersonInfoRTO> personInfoRTOListSameName = personInfoRTOFull.stream()
                        .filter(o ->  o.getLastName().equalsIgnoreCase(lastName))
                        .collect(Collectors.toList());
                this.personInfoRTOList.addAll(personInfoRTOListSameName);

                result = this.personInfoRTOList;
            }
        }
        return result;
    }

    public PersonInfoService() {

    }

    public void setDAO(IPersonDAO personDao, IMedicalRecordDAO medicalRecordDAO){
        this.personDAO = personDao;
        this.medicalRecordDAO = medicalRecordDAO;
    }
}
