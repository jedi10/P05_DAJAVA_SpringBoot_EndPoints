package com.safetynet.alerts.service;

import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.models.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CommunityEmailService {

    @Autowired
    IPersonDAO personDAO;

    public List<String> getCommunityEmail(String city){
        List<String> result = null;
        List<Person> personList = personDAO.getPersonList();

        result = personList.stream()
                .map(n -> n.getEmail())
                .collect(Collectors.toList());

        return result;
    }
}
