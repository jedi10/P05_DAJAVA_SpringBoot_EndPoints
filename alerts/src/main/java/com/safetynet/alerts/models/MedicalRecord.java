package com.safetynet.alerts.models;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
    private LocalDate birthdate;

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
     * @param birthdate birthday
     * @param medications medications list
     * @param allergies allergies list
     */
    public MedicalRecord(String firstName, String lastName, LocalDate birthdate,
                         List<String> medications, List<String> allergies) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.setMedications(medications);
        this.setAllergies(allergies);
    }

    /**
     * Constructor
     * Medications and Allergies List are created and will be empty
     * @param firstName first name
     * @param lastName last name
     * @param birthdate birthday
     */
    public MedicalRecord(String firstName, String lastName, LocalDate birthdate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.medications = new ArrayList<String>();
        this.allergies = new ArrayList<String>();
    }

    /**
     * Constructor
     */
    public MedicalRecord(){};

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicalRecord medicalRecord = (MedicalRecord) o;
        return Objects.equals(
                        firstName.toLowerCase(), medicalRecord.firstName.toLowerCase()) &&
                Objects.equals(
                        lastName.toLowerCase(), medicalRecord.lastName.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName);
    }

}



//https://stackoverflow.com/questions/28802544/java-8-localdate-jackson-format

//https://www.baeldung.com/java-optional
//https://jmdoudoux.fr/java/dej/chap-lambdas.htm
//https://www.baeldung.com/java-copy-list-to-another
//https://stackoverflow.com/questions/689370/how-to-copy-java-collections-list

//size = listOfSomething.map(List::size).orElse(0);