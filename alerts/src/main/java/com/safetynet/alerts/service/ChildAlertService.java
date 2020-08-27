package com.safetynet.alerts.service;

import com.safetynet.alerts.dao.IMedicalRecordDAO;
import com.safetynet.alerts.dao.IPersonDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ChildAlertService {

    @Autowired
    IPersonDAO personDAO;

    @Autowired
    IMedicalRecordDAO medicalRecordDAO;

    public Map<String, List> getChildAlert(String address){
        Map<String, List> result = new HashMap<>();
        List<IPersonDAO> childList = new ArrayList<>();
        List<IPersonDAO> adultList = new ArrayList<>();



        result.put("Children", childList);
        result.put("Adults", adultList);
        return result;
    }
}
