package com.safetynet.alerts.models;

import lombok.Getter;
import lombok.Setter;

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
}
