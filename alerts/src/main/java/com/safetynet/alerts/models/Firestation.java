package com.safetynet.alerts.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Firestation that = (Firestation) o;
        return address.equals(that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }
}
