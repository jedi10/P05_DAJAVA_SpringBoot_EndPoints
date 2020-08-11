package com.safetynet.alerts.dao;

import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.utils.Jackson;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

/**
 * <b>Load and Manage Person Type Data</b>
 */
@Repository
public class PersonDaoImpl extends DaoImpl implements IPersonDAO {

    @Getter
    @Setter
    private List<Person> personList;

    public PersonDaoImpl(RootFile rootFile) throws IOException {
        super(rootFile);
        try {
            this.personList = Jackson.convertJsonRootDataToJava(
                    this.getRootFile().getBytes(),
                    "persons",
                    Person.class);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    public List<Person> findAll() {
        return personList;
    }

    @Override
    public Person findByName(String firstName, String lastName) {
        return personList.stream()
                .filter(x -> {
                    return firstName.toLowerCase().equals(
                                    x.getFirstName().toLowerCase()) &&
                            lastName.toLowerCase().equals(
                                    x.getLastName().toLowerCase());
                        })
                .findAny()        // If 'findAny' then return found
                .orElse(null);
        //https://mkyong.com/java8/java-8-streams-filter-examples/
    }

    private boolean isPresent(Person person){
        return personList.stream()
                .anyMatch((p)->{
                    return p.equals(person);});
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
                if (p.equals(person)) {
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