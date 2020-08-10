package com.safetynet.alerts.service;

import com.safetynet.alerts.dao.IMedicalRecordDAO;
import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.service.rto_models.IPersonInfoRTO;
import com.safetynet.alerts.service.rto_models.PersonInfoRTO;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PersonInfoService {

    @Autowired
    IPersonDAO personDAO;

    @Autowired
    IMedicalRecordDAO medicalRecordDAO;

    private String firstName;

    private String lastName;

    IPersonInfoRTO personInfo;

    public IPersonInfoRTO getPersonInfo(String firstName, String lastName) {
        IPersonInfoRTO result = null;
        if (firstName != null && lastName != null) {
            //Debounce functionality
            if (firstName.equals(this.firstName) &&
                    lastName.equals(this.lastName) &&
                    this.personInfo != null) {
                result = this.personInfo;
            } else {
                this.firstName = firstName;
                this.lastName = lastName;
                Person person =
                        personDAO.findByName(this.firstName, this.lastName);
                MedicalRecord medicalRecord =
                        medicalRecordDAO.findByName(this.firstName, this.lastName);
                try {
                    this.personInfo = new PersonInfoRTO(person, medicalRecord);
                    result = this.personInfo;
                } catch (Exception e) {
                    log.error("PersonInfoService: "+ e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public PersonInfoService() {

    }
}
