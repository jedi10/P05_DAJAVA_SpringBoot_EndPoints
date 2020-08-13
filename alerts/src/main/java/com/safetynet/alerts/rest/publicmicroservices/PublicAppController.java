package com.safetynet.alerts.rest.publicmicroservices;

import com.safetynet.alerts.rest.AdminPersonController;
import com.safetynet.alerts.service.PersonInfoService;
import com.safetynet.alerts.service.rto_models.IPersonInfoRTO;
import com.safetynet.alerts.service.rto_models.PersonInfoRTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
public class PublicAppController {

    @Autowired
    PersonInfoService personInfoService;


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
     * @see AdminPersonController#getPerson(String, String)
     * @param firstName firstName
     * @param lastName lastName
     * @param httpResponse response
     * @throws Exception exception
     */
    @GetMapping(value = "/personinfo/{firstName}&{lastName}")
    public ResponseEntity<?> getPersonInfo(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName,
                                  HttpServletResponse httpResponse) throws Exception {
        log.info("Fetching Person with first Name '{}' and last Name '{}'", firstName, lastName );

        IPersonInfoRTO personInfo = personInfoService.getPersonInfo(firstName, lastName);
        if(personInfo == null){
            log.warn("Fetching Person Aborted: {} {} not found", firstName, lastName);
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<IPersonInfoRTO>(personInfo, HttpStatus.OK);
    }
}
