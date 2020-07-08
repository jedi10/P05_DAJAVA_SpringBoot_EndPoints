package com.safetynet.alerts.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MedicalRecord {

    @Getter
    @Setter
    private String firstName;

    @Getter
    @Setter
    private String lastName;

    @Getter
    @Setter
    private LocalDate birthday;

    @Getter
    private List<String> medications;

    @Getter
    private List<String> allergies;


    /**
     * <p>new instance of List will be created with content given</p>
     * <p>if Medications List is Null, an empty List will be created</p>
     * @param medications medications List
     */
    public void setMedications(List<String> medications){
        this.medications = Optional.ofNullable(medications)
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * <p>new instance of List will be created with content given</p>
     * <p>if Allergies List is Null, an empty List will be created</p>
     * @param allergies allergies List
     */
    public void setAllergies(List<String> allergies){
        this.allergies = Optional.ofNullable(allergies)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .collect(Collectors.toList());
    }

    /**
     * <b>Constructor</b>
     * <p>if Medications or Allergies List are Null, an empty List will be created</p>
     * @param firstName first name
     * @param lastName last name
     * @param birthday birthday
     * @param medications medications list
     * @param allergies allergies list
     */
    public MedicalRecord(String firstName, String lastName, LocalDate birthday,
                         List<String> medications, List<String> allergies) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.setMedications(medications);
        this.setAllergies(allergies);
    }

    /**
     * Constructor
     * Medications and Allergies List are created and will be empty
     * @param firstName first name
     * @param lastName last name
     * @param birthday birthday
     */
    public MedicalRecord(String firstName, String lastName, LocalDate birthday) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.medications = new ArrayList<String>();
        this.allergies = new ArrayList<String>();
    }
}



//https://www.baeldung.com/java-optional
//https://jmdoudoux.fr/java/dej/chap-lambdas.htm
//https://www.baeldung.com/java-copy-list-to-another
//https://stackoverflow.com/questions/689370/how-to-copy-java-collections-list

//size = listOfSomething.map(List::size).orElse(0);