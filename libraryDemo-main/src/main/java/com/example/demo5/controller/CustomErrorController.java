package com.example.demo5.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Integer statusCode = null;
        
        if (status != null) {
            statusCode = Integer.parseInt(status.toString());
        }
        
        if (statusCode != null && statusCode == HttpStatus.NOT_FOUND.value()) {
            // Redirect 404 to login page instead of showing error
            return "redirect:/template/login.html";
        }
        
        return "forward:/template/login.html";
    }

    public String getErrorPath() {
        return "/error";
    }
}
