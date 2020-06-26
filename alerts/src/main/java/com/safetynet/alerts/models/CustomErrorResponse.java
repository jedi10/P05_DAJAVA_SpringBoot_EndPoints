package com.safetynet.alerts.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Model used for custom error view
 */
public class CustomErrorResponse {

    @Getter
    @Setter
    private Integer status;

    @Getter
    @Setter
    private String path;

    @Getter
    @Setter
    private String errorMessage;

    @Getter
    @Setter
    private String timeStamp;

    @Getter
    @Setter
    private String trace;

    public CustomErrorResponse(Integer status, Map<String, Object> errorAttributes) {
        this.status = status;
        this.setPath((String) errorAttributes.get("path"));
        this.setErrorMessage((String) errorAttributes.get("message"));
        this.setTimeStamp(errorAttributes.get("timestamp").toString());
        this.setTrace((String) errorAttributes.get("trace"));
    }
}
