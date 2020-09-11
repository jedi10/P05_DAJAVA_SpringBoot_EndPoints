package com.safetynet.alerts.rest.publicmicroservices;

import com.safetynet.alerts.rest.AdminPersonController;
import com.safetynet.alerts.service.*;
import com.safetynet.alerts.service.rto_models.IFirestationAreaRTO;
import com.safetynet.alerts.service.rto_models.IPersonInfoRTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <b>Public END-POINTS</b>
 * <p>All EndPoints give response in JSON</p>
 */
@Slf4j
@RestController
public class PublicAppController {

    @Autowired
    PersonInfoService personInfoService;

    @Autowired
    CommunityEmailService communityEmailService;

    @Autowired
    PhoneAlertService phoneAlertService;

    @Autowired
    FireAddressService fireAddressService;

    @Autowired
    ChildAlertService childAlertService;

    @Autowired
    FirestationAreaService firestationAreaService;

    @Autowired
    FloodStationsService floodStationsService;


    /**
     * <b>redirection to getPerson Admin EndPoint Controller</b>
     * @see AdminPersonController#getPerson(String, String)
     * @param firstName firstName
     * @param lastName lastName
     * @param httpResponse response
     * @throws Exception exception
     */
    @RequestMapping(value = "/admin/personinfo/{firstName}&{lastName}", method = RequestMethod.GET)
    public void redirectGetPerson(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName,
                                  HttpServletResponse httpResponse) throws Exception {

        httpResponse.sendRedirect("/person/"+firstName+"&"+lastName);
    }

    /**
     * <b>getPersonInfo public EndPoint Controller</b>
     * <p>Return all information on a Person with first Name and last Name given</p>
     * <ul>
     *     <li>Result contains one person with same first name and last name</li>
     *     <li>Result contains also people who have the same last Name</li>
     * </ul>
     * @see PersonInfoService#getPersonInfo(String, String)
     * @param firstName firstName
     * @param lastName lastName
     * @param httpResponse response
     * @return ResponseEntity with a List of IPersonInfoRTO as content
     */
    @GetMapping(value = "/personinfo/{firstName}&{lastName}")
    public ResponseEntity<?> getPersonInfo(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName,
                                  HttpServletResponse httpResponse) {
        log.info("Fetching Person with first Name '{}' and last Name '{}'", firstName, lastName );

        List<IPersonInfoRTO> personInfoList = personInfoService.getPersonInfo(firstName, lastName);
        if(personInfoList.isEmpty()){
            log.warn("Fetching Person Aborted: '{}' '{}' not found", firstName, lastName);
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<List<IPersonInfoRTO>>(personInfoList, HttpStatus.OK);
    }

    /**
     * <b>getCommunityEmail public EndPoint Controller</b>
     * <p>Return Email of all persons living in city given</p>
     * @see CommunityEmailService#getCommunityEmail(String)
     * @param city city
     * @param httpResponse response
     * @return ResponseEntity with a List of mail as content
     */
    @GetMapping(value = "/communityemail/{city}")
    public ResponseEntity<?> getCommunityEmail(@PathVariable("city") String city,
                                               HttpServletResponse httpResponse)  {
        log.info("Fetching Email of all person living in city:  '{}'", city);

        List<String> mailList = communityEmailService.getCommunityEmail(city);
        if(mailList.isEmpty()){
            log.warn("Fetching Empty MailList for city: '{}'", city);
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<List<String>>(mailList, HttpStatus.OK);
    }

    /**
     * <b>getPhoneAlert public EndPoint Controller</b>
     * <p>Return Phone of all persons under responsibility of station given</p>
     * @see PhoneAlertService#getPhoneAlert(String)
     * @param station Firestation number
     * @param httpResponse response
     * @return ResponseEntity with a List of tel number as content
     */
    @GetMapping(value = "/phonealert/{firestation}")
    public ResponseEntity<?> getPhoneAlert(@PathVariable("firestation") String station,
                                           HttpServletResponse httpResponse)  {
        log.info("Fetching Phone of all persons under responsibility of station:  '{}'", station);

        List<String> phoneList = phoneAlertService.getPhoneAlert(station);
        if(phoneList.isEmpty()){
            log.warn("Fetching Empty PhoneList for station: '{}'",station);
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<List<String>>(phoneList, HttpStatus.OK);
    }

    /**
     * <b>getFire public EndPoint Controller</b>
     * <p>Return a List of all persons located under address given and the Firestation responsible</p>
     * @see FireAddressService#getFireAndPersonsWithAddress(String)
     * @param address firestation address
     * @param httpResponse response
     * @return ResponseEntity with a Map as content
     */
    @GetMapping(value = "/fire/{address}")
    public ResponseEntity<?> getFireAndPersons(@PathVariable("address") String address,
                                           HttpServletResponse httpResponse)  {
        log.info("Fetching List of all persons located under address: '{}' and the Firestation responsible", address);

        Map<String, List> mapResult = fireAddressService.getFireAndPersonsWithAddress(address);
        if(mapResult.get("Persons").isEmpty() && mapResult.get("Firestation").isEmpty()){
            log.warn("Fetching Empty Map for Address: '{}'", address);
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<Map<String, List>>(mapResult, HttpStatus.OK);
    }

    /**
     * <b>getChildAlert public EndPoint Controller</b>
     * <p>Return a List of all children located under address given and the adults living with them</p>
     * <ul>
     *     <li>no content is returned when no children</li>
     * </ul>
     *
     * @see com.safetynet.alerts.service.ChildAlertService#getChildAlert(String)
     * @param address child address
     * @param httpResponse response
     * @return ResponseEntity with a Map as content
     */
    @GetMapping(value = "/childalert/{address}")
    public ResponseEntity<?> getChildAlert(@PathVariable("address") String address,
                                               HttpServletResponse httpResponse)  {
        log.info("Fetching List of all children located under address: '{}' and the adults living with them", address);

        Map<IPersonInfoRTO.HumanCategory, List<IPersonInfoRTO>> mapResult = childAlertService.getChildAlert(address);
        if(mapResult.isEmpty()){
            log.warn("Fetching Empty Map for Address: '{}'", address);
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(mapResult, HttpStatus.OK);
    }

    /**
     * <b>getFirestationArea public EndPoint Controller</b>
     * <p>return a List of all persons in the area of responsibilities of station given with children and adults counter</p>
     * @see com.safetynet.alerts.service.FirestationAreaService#getFirestationArea(String)
     * @param station station number
     * @param httpResponse response
     * @return ResponseEntity with a IFirestationRTO as Content
     */
    @GetMapping(value = "/firestationarea/{stationNumber}")
    public ResponseEntity<?> getFirestationArea(@PathVariable("stationNumber") String station,
                                           HttpServletResponse httpResponse)  {
        log.info("Fetching List of all persons in the area of responsibilities of station: '{}' with children and adults counter", station);

        IFirestationAreaRTO objectResult = firestationAreaService.getFirestationArea(station);
        if(null == objectResult){
            log.warn("Fetching Empty Data for station: '{}'", station);
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(objectResult, HttpStatus.OK);
    }

    /**
     * <b>getFloodStations public EndPoint Controller</b>
     * <p>return a List of all persons in the area of responsibilities of stations given grouped by address</p>
     * @see com.safetynet.alerts.service.FloodStationsService#getFloodStations(List)
     * @param stations stations number list
     * @param httpResponse response
     * @return ResponseEntity with a Map as content
     */
    @GetMapping(value = "/flood/stations/{stations}")
    public ResponseEntity<?> getFloodStations(@PathVariable("stations") List<String> stations,
                                                HttpServletResponse httpResponse)  {
        log.info("Fetching List of all persons in the area of responsibilities of stations: '{}' grouped by address", stations);

        Map<String, List<IPersonInfoRTO>> objectResult = floodStationsService.getFloodStations(stations);
        if(null == objectResult){
            log.warn("Fetching Empty Data for stations: '{}'", stations);
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(objectResult, HttpStatus.OK);
    }
}
