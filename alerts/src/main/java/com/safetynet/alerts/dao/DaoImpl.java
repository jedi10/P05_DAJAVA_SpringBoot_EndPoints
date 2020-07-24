package com.safetynet.alerts.dao;

import com.safetynet.alerts.configuration.AlertsProperties;
import org.springframework.beans.factory.annotation.Autowired;

public class DaoImpl implements IDAO {

    public AlertsProperties alertsProperties;


    public DaoImpl(AlertsProperties alertsProperties) {
        this.alertsProperties = alertsProperties;
    }

    @Override
    public String getJsonFilePath() {
        return alertsProperties.getJsonFilePath();
    }
}
