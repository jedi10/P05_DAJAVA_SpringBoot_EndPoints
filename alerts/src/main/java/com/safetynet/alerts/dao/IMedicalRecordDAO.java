package com.safetynet.alerts.dao;

import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.models.Person;

import java.util.List;

public interface IMedicalRecordDAO extends IDAO {

    public void setMedicalRecordList(List<MedicalRecord> medicalRecordList);
    public List<MedicalRecord> getMedicalRecordList();
    public List<MedicalRecord> findAll();
    public MedicalRecord findByName(String firstName, String lastName);
    public MedicalRecord save(MedicalRecord medicalRecord);
    public MedicalRecord update(MedicalRecord medicalRecord);
    public boolean delete(MedicalRecord medicalRecord);
}
