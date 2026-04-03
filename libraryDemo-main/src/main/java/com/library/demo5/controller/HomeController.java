package com.library.demo5.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String rootToIndex() {
        return "forward:/index.html";
    }

    @GetMapping("/login.html")
    public String loginAlias() {
        return "forward:/template/login.html";
    }

    @GetMapping("/template/index.html")
    public String templateIndexAlias() {
        return "forward:/index.html";
    }
}