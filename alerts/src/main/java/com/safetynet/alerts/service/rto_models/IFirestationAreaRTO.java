package com.safetynet.alerts.service.rto_models;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;
import java.util.stream.Collectors;

public interface IFirestationAreaRTO {
    private void setHumanCategoryMap(List<IPersonInfoRTO> personInfoRTOList) {}

    java.util.Map<String, List<IPersonInfoRTO>> getPersonInfoRTOMap();

    java.util.Map<IPersonInfoRTO.HumanCategory, Long> getHumanCategoryMap();
}

//https://www.logicbig.com/tutorials/misc/jackson/json-type-info-with-logical-type-name.html
