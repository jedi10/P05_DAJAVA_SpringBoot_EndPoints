package com.safetynet.alerts.service;

import com.safetynet.alerts.dao.IPersonDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class CommunityEmailService {

    @Autowired
    IPersonDAO personDAO;

    public List<String> getCommunityEmail(String city){
        List<String> result = null;




        return result;
    }
}
