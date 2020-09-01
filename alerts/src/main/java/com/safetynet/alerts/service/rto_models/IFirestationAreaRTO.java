package com.safetynet.alerts.service.rto_models;

import java.util.List;
import java.util.stream.Collectors;

public interface IFirestationAreaRTO {
    private void setHumanCategoryMap(List<IPersonInfoRTO> personInfoRTOList) {}

    java.util.Map<String, List<IPersonInfoRTO>> getPersonInfoRTOMap();

    java.util.Map<IPersonInfoRTO.HumanCategory, Long> getHumanCategoryMap();
}
