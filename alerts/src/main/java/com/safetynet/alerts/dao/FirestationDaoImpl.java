package com.safetynet.alerts.dao;

import com.safetynet.alerts.models.Firestation;
import com.safetynet.alerts.utils.Jackson;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

/**
 * <b>Load and Manage Firestation Type Data</b>
 */
@Repository
public class FirestationDaoImpl extends DaoImpl implements IFirestationDAO {

    @Getter
    @Setter
    private List<Firestation> firestationList;

    public FirestationDaoImpl(RootFile rootFile) throws IOException {
        super(rootFile);
        try {
            this.firestationList = Jackson.convertJsonRootDataToJava(
                    this.getRootFile().getBytes(),
                    "firestations",
                    Firestation.class);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    public List<Firestation> findAll() {
        return firestationList;
    }

    @Override
    public Firestation findByAddress(String address) {
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
        Firestation result = null;
        if(!isPresent(firestation)) {
            firestationList.add(firestation);
            result = firestation;
        }
        return result;
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
