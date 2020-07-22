package com.safetynet.alerts.dao;

import com.safetynet.alerts.models.Firestation;
import com.safetynet.alerts.models.Person;

import java.util.List;

public interface IFirestationDAO {

    public List<Firestation> findAll();
    public Firestation findByAddress(String address);
    public Firestation save(Firestation firestation);
    public Firestation update(Firestation firestation);
    public boolean delete(Firestation firestation);
}
