package com.safetynet.alerts.dao;

import com.safetynet.alerts.models.Person;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PersonDaoImpl implements PersonDAO {

    public static List<Person> personList;

    static {
        Person person = new Person("julia", "werner", "rue du colysée", "Rome", 45, "06-12-23-34-45", "wermer@mail.it");
        Person person2 = new Person("judy", "holmes", "rue de la pensee", "Londre", 89, "06-25-74-90-12", "holmes@mail.en");
        Person personCreated = new Person("jack", "mortimer", "rue du stade", "Rome", 45, "06-25-50-90-12", "mortimer@mail.it");
        personList = List.of(person, person2, personCreated);
    }

    @Override
    public List<Person> findAll() {
        return personList;
    }

    @Override
    public Person findByName(String firstName, String lastName) {
        return personList.stream()
                .filter(x -> {
                    return firstName.equals(x.getFirstName()) &&
                            lastName.equals(x.getLastName());})
                .findAny()                                      // If 'findAny' then return found
                .orElse(null);
        //https://mkyong.com/java8/java-8-streams-filter-examples/
    }

    @Override
    public Person save(Person person) {
        personList.add(person);
        return person;
    }
}
