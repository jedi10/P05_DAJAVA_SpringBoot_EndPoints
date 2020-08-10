package com.safetynet.alerts.service.rto_models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.models.Person;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <b>Result Transfert Object Model</b>
 * <p>Fusion of all informations we have about a person</p>
 */
public class PersonInfoRTO {
    @Getter
    @Setter
    private String firstName;

    @Getter
    @Setter
    private String lastName;

    @Getter
    @Setter
    private String address;

    @Getter
    @Setter
    private String city;

    @Getter
    @Setter
    private Integer zip;

    @Getter
    @Setter
    private String phone;

    @Getter
    @Setter
    private String email;

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
     * <b>Constructor PersonInfoRTO</b>
     * <p>First and last name of Person and MedicalRecord have to be the same</p>
     * <ul>
     *    <li>if one param is null, return an exception</li>
     *    <li>if first name and last name of param (person, medicalrecord) are not the same, return an exception</li>
     * </ul>
     * @param person person Model
     * @param medicalRecord medicalRecord model
     * @throws Exception exception with message
     */
    public PersonInfoRTO(Person person, MedicalRecord medicalRecord) throws Exception {
            checkDataConstructor(person, medicalRecord);
            this.setFirstName(person.getFirstName());
            this.setLastName(person.getLastName());
            this.setAddress(person.getAddress());
            this.setCity(person.getCity());
            this.setZip(person.getZip());
            this.setPhone(person.getPhone());
            this.setEmail(person.getEmail());
            this.setBirthdate(medicalRecord.getBirthdate());
            this.setMedications(medicalRecord.getMedications());
            this.setAllergies(medicalRecord.getAllergies());

    }

    /**
     * <b>Check that Person and MedicalRecord Model are relative to the same person</b>
     * @param person person Model
     * @param medicalRecord medicalRecord model
     * @return boolean true if check is ok
     * @throws Exception throw an Exception with a message
     */
    private boolean checkDataConstructor(Person person, MedicalRecord medicalRecord) throws Exception {
        boolean result = false;
        if (person != null && medicalRecord != null){
            if (Objects.equals(
                        person.getFirstName(), medicalRecord.getFirstName()) &&
                Objects.equals(
                        person.getLastName(), medicalRecord.getLastName())){
                result = true;
            } else {
                throw new Exception(
                        "PersonInfo constructor param (person, medicalRecord) need the same firstname and lastname properties");
            }
        } else {
            throw new Exception(
                    "PersonInfo constructor need not Null person and medicalRecord");
        }
        return result;
    }
}
