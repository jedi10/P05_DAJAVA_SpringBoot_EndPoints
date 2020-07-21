package com.safetynet.alerts.dao;

import com.safetynet.alerts.models.MedicalRecord;

import java.util.List;

public interface IMedicalRecordDAO {

    public List<MedicalRecord> findAll();
    public MedicalRecord findByName(String firstName, String lastName);
    public MedicalRecord save(MedicalRecord medicalRecord);
    public MedicalRecord update(MedicalRecord medicalRecord);
    public boolean delete(MedicalRecord medicalRecord);
}
