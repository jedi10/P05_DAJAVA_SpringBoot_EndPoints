package com.safetynet.alerts.dao;

import lombok.Getter;
import lombok.Setter;

public class DaoImpl implements IDAO {

    @Getter
    @Setter
    private RootFile rootFile;

    public DaoImpl(RootFile rootFile) {
        this.setRootFile(rootFile);
    }

}
