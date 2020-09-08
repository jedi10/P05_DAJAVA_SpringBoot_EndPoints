package com.safetynet.alerts.service.rto_models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.safetynet.alerts.models.MedicalRecord;
import lombok.Getter;
import lombok.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <b>RTO Class used by FirestationArea Service</b>
 * <p>Give a presentation of list of IPersonInfoRTO</p>
 * <ul>
 *     <li>add a counter for children and adults</li>
 *     <li>in case counter is on zero, we show it anyway</li>
 * </ul>
 * @see com.safetynet.alerts.service.FirestationAreaService
 */
public class FirestationAreaRTO implements IFirestationAreaRTO {

    @Getter
    @JsonProperty("FIRESTATION AREA")
    private Map<String, List<IPersonInfoRTO>> personInfoRTOMap;

    @Getter
    @JsonProperty("COUNTER")
    private Map<IPersonInfoRTO.HumanCategory, Long> humanCategoryMap;

    public FirestationAreaRTO(@NonNull List<IPersonInfoRTO> personInfoRTOList) {
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