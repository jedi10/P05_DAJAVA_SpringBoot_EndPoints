package com.safetynet.alerts.dao;

import com.safetynet.alerts.configuration.AlertsProperties;
import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.utils.Jackson;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MedicalRecordDaoImpl extends DaoImpl implements IMedicalRecordDAO {

    @Getter
    @Setter
    private List<MedicalRecord> medicalRecordList;
    /*
    static {
        List<String> medicationList = new ArrayList<>();
        medicationList.add("aznol:350mg"); medicationList.add("hydrapermazol:100mg");
        List<String> allergiesList = new ArrayList<>();
        allergiesList.add("nillacilan");
        //https://howtodoinjava.com/java/date-time/java-time-localdate-class/
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-d-yyyy");
        MedicalRecord medicalRecord1 = new MedicalRecord("john", "boyd",
                LocalDate.of(1984, 3, 6),
                medicationList, allergiesList);
        medicationList.clear();
        medicationList.add("pharmacol:5000mg"); medicationList.add("terazine:10mg"); medicationList.add("noznazol:250mg");
        allergiesList.clear();
        MedicalRecord medicalRecord2 = new MedicalRecord("jacob", "boyd",
                LocalDate.of(1989, 3, 6),
                medicationList, allergiesList);
        medicalRecordList.add(medicalRecord1);
        medicalRecordList.add(medicalRecord2);
    }*/

    public MedicalRecordDaoImpl(RootFile rootFile){
        super(rootFile);
        /**this.setFileBytesWithPath(this.getJsonFilePath());
        this.medicalRecordList = Jackson.convertJsonFileToJava(
                this.getFileBytes(),
                "medicalrecords",
                MedicalRecord.class);**/
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
