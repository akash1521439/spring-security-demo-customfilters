package com.org.springsecuritydemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {
    @GetMapping("logins")
    public String login(){
        return "login successfull";
    }
    @GetMapping("home")
    public String home(){
        return "home login successfull";
    }
}
