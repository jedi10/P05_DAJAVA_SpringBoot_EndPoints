package com.safetynet.alerts.service.rto_models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.models.Person;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <b>Result Transfert Object Model for Person Info EndPoint</b>
 * <p>Fusion of all informations we have about a person</p>
 */
public class PersonInfoRTO implements IPersonInfoRTO {
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
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private LocalDate birthdate;

    @Getter
    private Integer age;

    @Getter
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private HumanCategory humanCategory;

    @Getter
    private List<String> medications;

    @Getter
    private List<String> allergies;

    /**
     * <p>new instance of List will be created with content given</p>
     * <p>if Medications List is Null, an empty List will be created</p>
     * @param medications medications List
     */
    @Override
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
    @Override
    public void setAllergies(List<String> allergies){
        this.allergies = Optional.ofNullable(allergies)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .collect(Collectors.toList());
    }

    @Override
    public void setAge(LocalDate birthdate) {
        this.age = Period.between(birthdate, LocalDate.now()).getYears();
        //https://www.baeldung.com/java-get-age
    }

    @Override
    public void setHumanCategory(){
        if (null != this.age) {
            if (this.age > 18){
                this.humanCategory = HumanCategory.ADULTS;
            } else {
                this.humanCategory = HumanCategory.CHILDREN;
            }
        }
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
            this.setAge(medicalRecord.getBirthdate());
            this.setHumanCategory();
            this.setMedications(medicalRecord.getMedications());
            this.setAllergies(medicalRecord.getAllergies());
    }

    public PersonInfoRTO() {}

    /**
     * <b>Factory: build PersonInfoRTO List</b>
     * <p>person and medicalrecord have to have same first and last name</p>
     * @param personList personList
     * @param medicalRecordList medicalRecordList
     * @return
     */
    public static List<IPersonInfoRTO> buildPersonInfoRTOList(List<Person> personList, List<MedicalRecord> medicalRecordList) {
        List<IPersonInfoRTO> personInfoRTOList = new ArrayList<>();
        personList.forEach(
                e -> { List<MedicalRecord> medRecFilters = medicalRecordList.stream()
                        .filter(med -> med.getFirstName().equalsIgnoreCase(e.getFirstName()) &&
                                        med.getLastName().equalsIgnoreCase(e.getLastName()))
                        .collect(Collectors.toList());
                    if(!medRecFilters.isEmpty()){
                        try {
                            PersonInfoRTO personInfoRTO = new PersonInfoRTO(e, medRecFilters.get(0));
                            personInfoRTOList.add(personInfoRTO);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
        );
        return personInfoRTOList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonInfoRTO that = (PersonInfoRTO) o;
        return Objects.equals(
                firstName.toLowerCase(), that.firstName.toLowerCase()) &&
                Objects.equals(
                        lastName.toLowerCase(), that.lastName.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName);
    }

}


//https://stackoverflow.com/questions/14708386/want-to-hide-some-fields-of-an-object-that-are-being-mapped-to-json-by-jackson