package cn.rutui.oauth2server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PageController {

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/test")
    @ResponseBody
    public String test(HttpServletRequest request) {
        if (request.getUserPrincipal() == null) {
            return "hello: anonymous";
        }
        return "hello: " + request.getUserPrincipal().getName();
    }

}