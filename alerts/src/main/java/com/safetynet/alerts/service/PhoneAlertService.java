package com.safetynet.alerts.service;

import com.safetynet.alerts.dao.IFirestationDAO;
import com.safetynet.alerts.dao.IPersonDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class PhoneAlertService {

    @Autowired
    IPersonDAO personDAO;

    @Autowired
    IFirestationDAO firestationDAO;

    public List<String> getPhoneAlert(String firestation){
        List<String> result = null;

        return result;
    }
}
