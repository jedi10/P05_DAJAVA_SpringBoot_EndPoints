package com.safetynet.alerts.dao;

import lombok.Getter;
import lombok.Setter;

/**
 * <b>Give access to the Data File to all Model DAO</b>
 * @see PersonDaoImpl
 * @see MedicalRecordDaoImpl
 * @see FirestationDaoImpl
 */
public class DaoImpl implements IDAO {

    @Getter
    @Setter
    private RootFile rootFile;

    public DaoImpl(RootFile rootFile) {
        this.setRootFile(rootFile);
    }

}
