package com.safetynet.alerts.service.rto_models;

import com.safetynet.alerts.models.MedicalRecord;
import com.safetynet.alerts.models.Person;
import com.safetynet.alerts.utils.Jackson;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FirestationAreaRTOTest {

    private FirestationAreaRTO firestationAreaRTO;

    private Person person;

    private MedicalRecord medicalRecord;

    private List<IPersonInfoRTO> personInfoRTOList = new ArrayList<>();

    @BeforeEach
    void setUp() throws Exception {
        //GIVEN
        person = new Person("John",
                "Wallas",
                "1509 Culver St",
                "Culver",
                97451,
                "841-874-6512",
                "jwallas@email.com");

        medicalRecord = new MedicalRecord("John",
                "Wallas",
                //https://www.oracle.com/technical-resources/articles/java/jf14-date-time.html
                LocalDate.of(1984, Month.MARCH, 06),
                Arrays.asList("pharmacol:5000mg","terazine:10mg","noznazol:250mg"),
                Arrays.asList("shellfish","peanut"));
        PersonInfoRTO personInfoRTO = new PersonInfoRTO(person, medicalRecord);
        personInfoRTOList.add(personInfoRTO);
        PersonInfoRTO personInfoRTO2 = Jackson.deepCopy(personInfoRTO, PersonInfoRTO.class);
        personInfoRTO2.setAge(LocalDate.now().minusYears(15));
        personInfoRTO2.setFirstName("Junior");
        personInfoRTOList.add(personInfoRTO2);
        PersonInfoRTO personInfoRTO3 = Jackson.deepCopy(personInfoRTO, PersonInfoRTO.class);
        personInfoRTO3.setAge(LocalDate.now().minusYears(17));
        personInfoRTO3.setFirstName("Junior2");
        personInfoRTOList.add(personInfoRTO3);
    }

    @Order(1)
    @Test()
    void firestationRTO_Ok() {
        //GIVEN
        assertFalse(personInfoRTOList.isEmpty());
        assertEquals(3, personInfoRTOList.size());

        //WHEN
        firestationAreaRTO = new FirestationAreaRTO(personInfoRTOList);

        //THEN
        //*********************************
        //Test Content of PersonInfoRTO Map
        //*********************************
        Map<String, List<IPersonInfoRTO>> personInfoRTOMapResult = firestationAreaRTO.getPersonInfoRTOMap();
        assertNotNull(personInfoRTOMapResult);
        assertEquals(personInfoRTOList, personInfoRTOMapResult.get("PERSONS"));
        //*********************************
        //Test Content of HumanCategory Map
        //*********************************
        Map<IPersonInfoRTO.HumanCategory, Long> humanCategoryMapResult = firestationAreaRTO.getHumanCategoryMap();
        assertNotNull(humanCategoryMapResult);
        assertEquals(1, humanCategoryMapResult.get(IPersonInfoRTO.HumanCategory.ADULTS));
        assertEquals(2, humanCategoryMapResult.get(IPersonInfoRTO.HumanCategory.CHILDREN));
    }

    @Order(2)
    @Test()
    void constructorParamNull_Exception() {
        //WHEN-THEN
        Exception exception = assertThrows(NullPointerException.class, ()-> {
            firestationAreaRTO = new FirestationAreaRTO(null);
        });
        assertTrue(exception.getMessage().contains(
                "personInfoRTOList is marked non-null but is null"));
    }



}