package com.safetynet.alerts.service.rto_models;

import lombok.Getter;
import lombok.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestationRTO {

    @Getter
    private Map<String, List<PersonInfoRTO>> personInfoRTOMap;

    @Getter
    private Map<String, Long> adultsNumberMap;

    @Getter
    private Map<String, Long> childrenNumberMap;

    public FirestationRTO(@NonNull List<PersonInfoRTO> personInfoRTOList) {
        
        personInfoRTOMap = new HashMap<>();
        personInfoRTOMap.put("Persons", personInfoRTOList);
        adultsNumberMap = new HashMap<>();
        childrenNumberMap = new HashMap<>();
        this.setPersonsTypeNumber(personInfoRTOList);
    }

    private void setPersonsTypeNumber(List<PersonInfoRTO> personInfoRTOList){
        long adultsNumber = personInfoRTOList.stream().
                filter(e->
                        e.getHumanCategory().equals(IPersonInfoRTO.HumanCategory.ADULTS)).
                count();
        adultsNumberMap.put("ADULTS", adultsNumber);

        long childrenNumber = personInfoRTOList.stream().
                filter(e->
                        e.getHumanCategory().equals(IPersonInfoRTO.HumanCategory.CHILDREN)).
                count();
        childrenNumberMap.put("CHILDREN", childrenNumber);
    }
}


//https://www.concretepage.com/java/java-8/java-stream-count