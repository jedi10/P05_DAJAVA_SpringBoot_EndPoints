package com.safetynet.alerts.models;

import lombok.Getter;
import lombok.Setter;

public class Firestation {

    @Getter
    @Setter
    private String address;

    @Getter
    @Setter
    private Integer station;

    /**
     * Constructor
     * @param address address
     * @param station station number
     */
    public Firestation(String address, Integer station) {
        this.address = address;
        this.station = station;
    }
}
