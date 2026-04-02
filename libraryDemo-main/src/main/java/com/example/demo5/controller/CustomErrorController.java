package com.example.demo5.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        String originalUri = String.valueOf(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));

        // Guard against redirect/forward loops when login path itself errors.
        if ("/template/login.html".equals(originalUri) || "/login.html".equals(originalUri) || "/error".equals(originalUri)) {
            return "forward:/error.html";
        }

        return "forward:/error.html";
    }

    public String getErrorPath() {
        return "/error";
    }
}
