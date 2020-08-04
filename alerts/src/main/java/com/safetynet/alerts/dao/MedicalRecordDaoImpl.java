package com.safetynet.alerts.dao;

import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.utils.Jackson;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
public class MedicalRecordDaoImpl extends DaoImpl implements IMedicalRecordDAO {

    @Getter
    @Setter
    private List<MedicalRecord> medicalRecordList;

    public MedicalRecordDaoImpl(RootFile rootFile) throws IOException {
        super(rootFile);
        try {
            this.medicalRecordList = Jackson.convertJsonRootDataToJava(
                    this.getRootFile().getBytes(),
                    "medicalrecords",
                    MedicalRecord.class);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }


    @Override
    public List<MedicalRecord> findAll() {
        return medicalRecordList;
    }

    @Override
    public MedicalRecord findByName(String firstName, String lastName) {
        return medicalRecordList.stream()
                .filter(x -> {
                    return firstName.equals(x.getFirstName()) &&
                            lastName.equals(x.getLastName());})
                .findAny()                                      // If 'findAny' then return found
                .orElse(null);
        //https://mkyong.com/java8/java-8-streams-filter-examples/
    }

    private boolean isPresent(MedicalRecord medicalRecord){
        return medicalRecordList.stream()
                .anyMatch((p)-> {
                    return p.equals(medicalRecord);}
                );
        //https://www.baeldung.com/java-streams-find-list-items
    }

    @Override
    public MedicalRecord save(MedicalRecord medicalRecord) {
        MedicalRecord result = null;
        if(!isPresent(medicalRecord)) {
            medicalRecordList.add(medicalRecord);
            result = medicalRecord;
        }
        return result;
    }

    @Override
    public MedicalRecord update(MedicalRecord medicalRecord) {
        MedicalRecord result= null;
        if(isPresent(medicalRecord)){
            medicalRecordList.replaceAll(p ->
            {
                if (p.equals(medicalRecord)) {
                    return medicalRecord;
                } else {
                    return p;}
            });
            //https://www.programiz.com/java-programming/library/arraylist/replaceall
            result = medicalRecord;
        }
        return result;
    }

    @Override
    public boolean delete(MedicalRecord medicalRecord) {
        return medicalRecordList.remove(medicalRecord);
    }
}
