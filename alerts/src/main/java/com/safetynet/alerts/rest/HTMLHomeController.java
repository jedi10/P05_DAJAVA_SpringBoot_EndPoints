package com.safetynet.alerts.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
@RequestMapping("/")
public class HTMLHomeController {

    @RequestMapping(value = "html/home", method = RequestMethod.GET)
    public String home(HttpServletResponse httpResponse) throws Exception {

        return "index";
    }

    @RequestMapping(method = RequestMethod.GET)
    public void redirectHome(HttpServletResponse httpResponse) throws Exception {

        httpResponse.sendRedirect("/html/home");
    }

    @RequestMapping(value = "/html", method = RequestMethod.GET)
    public void redirectHome2(HttpServletResponse httpResponse) throws Exception {

        httpResponse.sendRedirect("/html/home");
    }
}
