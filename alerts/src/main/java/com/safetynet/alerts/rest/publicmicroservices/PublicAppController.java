package com.safetynet.alerts.rest.publicmicroservices;

import com.safetynet.alerts.rest.AdminPersonController;
import com.safetynet.alerts.service.FireAddressService;
import com.safetynet.alerts.service.PhoneAlertService;
import com.safetynet.alerts.service.CommunityEmailService;
import com.safetynet.alerts.service.PersonInfoService;
import com.safetynet.alerts.service.rto_models.IPersonInfoRTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

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


    /**
     * redirection to getPerson Admin EndPoint Controller
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
     * getPersonInfo public EndPoint Controller
     * @see PersonInfoService#getPersonInfo(String, String)
     * @param firstName firstName
     * @param lastName lastName
     * @param httpResponse response
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
     * getCommunityEmail public EndPoint Controller
     * @see CommunityEmailService#getCommunityEmail(String)
     * @param city city
     * @param httpResponse response
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
     * getPhoneAlert public EndPoint Controller
     * @see PhoneAlertService#getPhoneAlert(String)
     * @param station Firestation number
     * @param httpResponse response
     */
    @GetMapping(value = "/phonealert/{station}")
    public ResponseEntity<?> getPhoneAlert(@PathVariable("station") String station,
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
     * getFire public EndPoint Controller
     * @see FireAddressService#getFireAndPersonsWithAddress(String)
     * @param address firestation address
     * @param httpResponse response
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
}
