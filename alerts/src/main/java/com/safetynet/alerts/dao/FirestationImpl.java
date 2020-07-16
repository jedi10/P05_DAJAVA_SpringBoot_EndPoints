package com.safetynet.alerts.dao;

import com.safetynet.alerts.models.Firestation;
import com.safetynet.alerts.models.Person;

import java.util.ArrayList;
import java.util.List;

public class FirestationImpl implements IFirestationDAO {

    public static List<Firestation> firestationList = new ArrayList<>();

    static {
        Firestation firestation1 = new Firestation("1509 Culver St",3);
        Firestation firestation2 = new Firestation("29 15th St", 2);
        Firestation firestationCreated = new Firestation("834 Binoc Ave", 3);
        firestationList.add(firestation1);
        firestationList.add(firestation2);
    }

    @Override
    public List<Firestation> findAll() {
        return firestationList;
    }

    @Override
    public Firestation findByName(String address) {
        return firestationList.stream()
                .filter( x -> {
                    return address.equals(x.getAddress());
                })
                .findAny()                                      // If 'findAny' then return found
                .orElse(null);
        //https://mkyong.com/java8/java-8-streams-filter-examples/
    }

    private boolean isPresent(Firestation firestation){
        return firestationList.stream()
                .anyMatch( p -> {
                    return p.getAddress().equals(firestation.getAddress());
                });
        //https://www.baeldung.com/java-streams-find-list-items
    }

    @Override
    public Firestation save(Firestation firestation) {
        if(!isPresent(firestation)) {
            firestationList.add(firestation);
        }
        return firestation;
    }

    @Override
    public Firestation update(Firestation firestation) {
        Firestation result = null;
        if(isPresent(firestation)){
            firestationList.replaceAll( p ->
            {
                if (p.getAddress().equals(firestation.getAddress())) {
                    return firestation;
                } else {
                    return p;}
            });
            //https://www.programiz.com/java-programming/library/arraylist/replaceall
            result = firestation;
        }
        return result;
    }

    @Override
    public boolean delete(Firestation firestation) {
        return firestationList.remove(firestation);
    }
}
