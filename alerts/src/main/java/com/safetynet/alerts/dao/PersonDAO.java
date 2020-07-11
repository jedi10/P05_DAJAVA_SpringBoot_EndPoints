package com.safetynet.alerts.dao;

import com.safetynet.alerts.models.Person;

import java.util.List;

public interface PersonDAO {
    public List<Person> findAll();
    public Person findByName(String firstName, String lastName);
    public Person save(Person person);
}
