package com.safetynet.alerts.models;

import lombok.Getter;
import lombok.Setter;

public class Firestation {

    @Getter
    @Setter
    private String address;

    @Getter
    @Setter
    private String station;

    /**
     * Constructor
     * @param address address
     * @param station station number (String)
     */
    public Firestation(String address, String station) {
        this.address = address;
        this.station = station;
    }

    /**
     * Constructor
     */
    public Firestation(){}
}
