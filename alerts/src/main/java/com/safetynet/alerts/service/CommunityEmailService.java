package com.safetynet.alerts.service;

import com.safetynet.alerts.dao.IPersonDAO;
import com.safetynet.alerts.models.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <b>List Email of all persons living in city given</b>
 */
@Slf4j
@Component
public class CommunityEmailService {

    final
    IPersonDAO personDAO;

    public CommunityEmailService(IPersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    public List<String> getCommunityEmail(String city){
        List<String> result = null;
        if (null != city) {
            List<Person> personList = personDAO.getPersonList();

            result = personList.stream()
                    .filter(o -> city.equalsIgnoreCase(o.getCity()))
                    .map(Person::getEmail)
                    .collect(Collectors.toList());
        }
        return result;
    }

}

//https://mkyong.com/java8/java-8-streams-filter-examples/
//https://www.baeldung.com/java-avoid-null-check
