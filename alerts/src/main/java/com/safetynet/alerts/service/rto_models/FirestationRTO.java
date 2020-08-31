package com.safetynet.alerts.service.rto_models;

import lombok.Getter;
import lombok.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class FirestationRTO {

    @Getter
    private Map<String, List<IPersonInfoRTO>> personInfoRTOMap;

    @Getter
    private Map<IPersonInfoRTO.HumanCategory, Long> humanCategoryMap;


    public FirestationRTO(@NonNull List<IPersonInfoRTO> personInfoRTOList) {
        personInfoRTOMap = new HashMap<>();
        personInfoRTOMap.put("PERSONS", personInfoRTOList);
        this.setHumanCategoryMap(personInfoRTOList);
    }
    private void setHumanCategoryMap(List<IPersonInfoRTO> personInfoRTOList){
        humanCategoryMap = personInfoRTOList.stream().collect(
                Collectors.groupingBy(
                        IPersonInfoRTO::getHumanCategory, Collectors.counting()
                )
        );
        humanCategoryMap.putIfAbsent(IPersonInfoRTO.HumanCategory.CHILDREN, 0L);
        humanCategoryMap.putIfAbsent(IPersonInfoRTO.HumanCategory.ADULTS, 0L);
    }
}

//https://mkyong.com/java8/java-8-collectors-groupingby-and-mapping-example
//https://www.concretepage.com/java/java-8/java-stream-count