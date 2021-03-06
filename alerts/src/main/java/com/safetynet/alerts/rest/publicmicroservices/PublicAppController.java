package com.safetynet.alerts.rest.publicmicroservices;

import com.safetynet.alerts.rest.AdminPersonController;
import com.safetynet.alerts.service.*;
import com.safetynet.alerts.service.rto_models.IFirestationAreaRTO;
import com.safetynet.alerts.service.rto_models.IPersonInfoRTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <b>Public END-POINTS</b>
 * <p>All EndPoints give response in JSON</p>
 */
@Api(tags = {"Public App Controller"})
@Tag(name = "Public App Controller", description = "Public End-Points")
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
    @ApiOperation(value = "redirection to Person Admin End-Point")
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
    @ApiOperation(value = "Return all information on Person with same first Name and last Name given and all persons with same last Name")
    @GetMapping(value = "/personinfo/{firstName}&{lastName}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<IPersonInfoRTO>>
            getPersonInfo(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName,
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
     * <b>redirection to getPersonInfo EndPoint Controller</b>
     * @param firstName firstName
     * @param lastName lastName
     * @param httpResponse response
     * @throws Exception exception
     */
    @ApiOperation(value = "redirection to PersonInfo End-Point")
    @GetMapping(value = "/personInfo")
    public void redirectGetPersonInfo(@RequestParam String firstName, @RequestParam String lastName,
                                  HttpServletResponse httpResponse) throws Exception {
        httpResponse.sendRedirect("/personinfo/"+firstName+"&"+lastName);
    }

    /**
     * <b>getCommunityEmail public EndPoint Controller</b>
     * <p>Return Email of all persons living in city given</p>
     * @see CommunityEmailService#getCommunityEmail(String)
     * @param city city
     * @param httpResponse response
     * @return ResponseEntity with a List of mail as content
     */
    @ApiOperation(value = "Return Email of all persons living in city given")
    @GetMapping(value = "/communityemail/{city}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<String>> getCommunityEmail(@PathVariable("city") String city,
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
     * <b>redirection to getCommunityEmail EndPoint Controller</b>
     * @param city cityName
     * @param httpResponse response
     * @throws Exception exception
     */
    @ApiOperation(value = "redirection to CommunityEmail End-Point")
    @GetMapping(value = "/communityEmail")
    public void redirectGetCommunityEmail(@RequestParam String city,
                                      HttpServletResponse httpResponse) throws Exception {
        httpResponse.sendRedirect("/communityemail/"+city);
    }

    /**
     * <b>getPhoneAlert public EndPoint Controller</b>
     * <p>Return Phone of all persons under responsibility of station given</p>
     * @see PhoneAlertService#getPhoneAlert(String)
     * @param station Firestation number
     * @param httpResponse response
     * @return ResponseEntity with a List of tel number as content
     */
    @ApiOperation(value = "Return Phone of all persons under responsibility of station given")
    @GetMapping(value = "/phonealert/{firestation}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<String>> getPhoneAlert(@PathVariable("firestation") String station,
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
     * <b>redirection to getPhoneAlert EndPoint Controller</b>
     * @param firestation firestation number
     * @param httpResponse response
     * @throws Exception exception
     */
    @ApiOperation(value = "redirection to PhoneAlert End-Point")
    @GetMapping(value = "/phoneAlert")
    public void redirectGetPhoneAlert(@RequestParam String firestation,
                                      HttpServletResponse httpResponse) throws Exception {
        httpResponse.sendRedirect("/phonealert/"+ firestation);
    }

    /**
     * <b>getFire public EndPoint Controller</b>
     * <p>Return a List of all persons located under address given and the Firestation responsible</p>
     * @see FireAddressService#getFireAndPersonsWithAddress(String)
     * @param address firestation address
     * @param httpResponse response
     * @return ResponseEntity with a Map as content
     */
    @ApiOperation(value = "Return all persons located under address given and the Firestation responsible")
    @GetMapping(value = "/fire/{address}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, List>>
            getFireAndPersons(@PathVariable("address") String address,
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
     * <b>redirection to getPhoneAlert EndPoint Controller</b>
     * @param address address
     * @param httpResponse response
     * @throws Exception exception
     */
    @ApiOperation(value = "redirection to FireAndPersons End-Point")
    @GetMapping(value = "/fire")
    public void redirectGetFireAndPersons(@RequestParam String address,
                                      HttpServletResponse httpResponse) throws Exception {
        httpResponse.sendRedirect("/fire/"+ address);
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
    @ApiOperation(value = "Return all children located under address given and the adults living with them")
    @GetMapping(value = "/childalert/{address}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<IPersonInfoRTO.HumanCategory, List<IPersonInfoRTO>>>
            getChildAlert(@PathVariable("address") String address,
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
     * <b>redirection to getChildAlert EndPoint Controller</b>
     * @param address address
     * @param httpResponse response
     * @throws Exception exception
     */
    @ApiOperation(value = "redirection to ChildAlert End-Point")
    @GetMapping(value = "/childAlert")
    public void redirectGetChildAlert(@RequestParam String address,
                                          HttpServletResponse httpResponse) throws Exception {
        httpResponse.sendRedirect("/childalert/"+ address);
    }

    /**
     * <b>getFirestationArea public EndPoint Controller</b>
     * <p>return a List of all persons in the area of responsibilities of station given with children and adults counter</p>
     * @see com.safetynet.alerts.service.FirestationAreaService#getFirestationArea(String)
     * @param station station number
     * @param httpResponse response
     * @return ResponseEntity with a IFirestationRTO as Content
     */
    @ApiOperation(value = "Return all persons in the area of responsibilities of station given with children and adults counter")
    @GetMapping(value = "/firestationarea/{stationNumber}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<IFirestationAreaRTO>
            getFirestationArea(@PathVariable("stationNumber") String station,
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
     * <b>redirection to getFirestationArea EndPoint Controller</b>
     * @param stationNumber station number
     * @param httpResponse response
     * @throws Exception exception
     */
    @ApiOperation(value = "redirection to FirestationArea End-Point")
    @GetMapping(value = "/firestation")
    public void redirectFirestationArea(@RequestParam String stationNumber,
                                      HttpServletResponse httpResponse) throws Exception {
        httpResponse.sendRedirect("/firestationarea/"+ stationNumber);
    }

    /**
     * <b>getFloodStations public EndPoint Controller</b>
     * <p>return a List of all persons in the area of responsibilities of stations given grouped by address</p>
     * @see com.safetynet.alerts.service.FloodStationsService#getFloodStations(List)
     * @param stations stations number list
     * @param httpResponse response
     * @return ResponseEntity with a Map as content
     */
    @ApiOperation(value = "Return all persons in the area of responsibilities of stations given grouped by address")
    @GetMapping(value = "/flood/stations/{stations}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, List<IPersonInfoRTO>>>
            getFloodStations(@PathVariable("stations") List<String> stations,
                                                HttpServletResponse httpResponse)  {
        log.info("Fetching List of all persons in the area of responsibilities of stations: '{}' grouped by address", stations);

        Map<String, List<IPersonInfoRTO>> objectResult = floodStationsService.getFloodStations(stations);
        if(null == objectResult){
            log.warn("Fetching Empty Data for stations: '{}'", stations);
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(objectResult, HttpStatus.OK);
    }

    /**
     * <b>redirection to getFloodStations EndPoint Controller</b>
     * @param stations List of station number
     * @param httpResponse response
     * @throws Exception exception
     */
    @ApiOperation(value = "redirection to FloodStations End-Point")
    @GetMapping(value = "/flood/stations", params = {"stations"})
    public void redirectFloodStations(@RequestParam String stations,
                                        HttpServletResponse httpResponse) throws Exception {
        httpResponse.sendRedirect("/flood/stations/"+ stations);
    }
}
