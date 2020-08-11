package com.safetynet.alerts.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

public class Person {

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

    /**
     * Constructor
     * @param firstName first name
     * @param lastName last name
     * @param address adress
     * @param city city
     * @param zip zip code
     * @param phone phone
     * @param email email
     */
    public Person(String firstName, String lastName, String address, String city, Integer zip, String phone, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.zip = zip;
        this.phone = phone;
        this.email = email;
    }

    public Person() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(
                        firstName.toLowerCase(), person.firstName.toLowerCase()) &&
                Objects.equals(
                        lastName.toLowerCase(), person.lastName.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName);
    }
}
