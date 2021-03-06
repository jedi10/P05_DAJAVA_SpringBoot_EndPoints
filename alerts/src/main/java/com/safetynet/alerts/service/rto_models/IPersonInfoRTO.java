package com.safetynet.alerts.service.rto_models;

import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.models.Person;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public interface IPersonInfoRTO {
    void setMedications(List<String> medications);

    void setAllergies(List<String> allergies);

    /**
     * <b>Check that Person and MedicalRecord Model are relative to the same person</b>
     * @param person person Model
     * @param medicalRecord medicalRecord model
     * @return boolean true if check is ok
     * @throws Exception throw an Exception with a message
     */
    default boolean checkDataConstructor(Person person, MedicalRecord medicalRecord) throws Exception {
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

    String getFirstName();

    String getLastName();

    String getAddress();

    String getCity();

    Integer getZip();

    String getPhone();

    String getEmail();

    java.time.LocalDate getBirthdate();

    Integer getAge();

    HumanCategory getHumanCategory();

    List<String> getMedications();

    List<String> getAllergies();

    void setFirstName(String firstName);

    void setLastName(String lastName);

    void setAddress(String address);

    void setCity(String city);

    void setZip(Integer zip);

    void setPhone(String phone);

    void setEmail(String email);

    void setBirthdate(java.time.LocalDate birthdate);

    void setAge(java.time.LocalDate birthdate);

    void setHumanCategory();

    Comparator<IPersonInfoRTO> comparator = Comparator.comparing(IPersonInfoRTO::getLastName).thenComparing(IPersonInfoRTO::getFirstName);

    enum HumanCategory {
        CHILDREN("Children"),
        ADULTS("Adults");

        private final String label;

        HumanCategory(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }
}

//https://www.baeldung.com/java-static-default-methods
//https://reversecoding.net/java-8-comparator-how-to-sort-a-list/