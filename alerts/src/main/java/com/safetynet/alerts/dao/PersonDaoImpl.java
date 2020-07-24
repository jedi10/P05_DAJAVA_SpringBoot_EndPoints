package com.safetynet.alerts.dao;

import com.safetynet.alerts.configuration.AlertsProperties;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.utils.Jackson;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PersonDaoImpl extends DaoImpl implements IPersonDAO {

    public List<Person> personList;
    /*
    static {
        Person person = new Person("julia", "werner", "rue du colysée", "Rome", 45, "06-12-23-34-45", "wermer@mail.it");
        Person person2 = new Person("judy", "holmes", "rue de la pensee", "Londre", 89, "06-25-74-90-12", "holmes@mail.en");
        Person personCreated = new Person("jack", "mortimer", "rue du stade", "Rome", 45, "06-25-50-90-12", "mortimer@mail.it");
        //personList = List.of(person, person2);//immutable
        personList.add(person);
        personList.add(person2);
    }*/

    public PersonDaoImpl(AlertsProperties alertsProperties){
        super(alertsProperties);
        this.personList = Jackson.convertJsonFileToJava(
                this.getJsonFilePath(),
                "persons",
                Person.class);
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

    private boolean isPresent(Person person){
        return personList.stream()
                .anyMatch((p)->{
                    return p.getFirstName().equals(person.getFirstName())&&
                            p.getLastName().equals(person.getLastName());});
        //https://www.baeldung.com/java-streams-find-list-items
    }
    @Override
    public Person save(Person person) {
        Person result = null;
        if(!isPresent(person)) {
            personList.add(person);
            result = person;
        }
        return result;
    }

    @Override
    public Person update(Person person) {
        Person result= null;
        if(isPresent(person)){
            personList.replaceAll(p ->
            {
                if (p.getFirstName().equals(person.getFirstName()) &&
                        p.getLastName().equals(person.getLastName())) {
                    return person;
                }else {
                    return p;}
            });
            //https://www.programiz.com/java-programming/library/arraylist/replaceall
            result = person;
        }
       return result;
    }

    @Override
    public boolean delete(Person person) {
        return personList.remove(person);
    }
}



//https://www.baeldung.com/java-copy-list-to-another