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
                .filter(o -> city.equals(o.getCity()))
                .map(Person::getEmail)
                .collect(Collectors.toList());

        return result;
    }
}

//https://mkyong.com/java8/java-8-streams-filter-examples/
