package com.fn.bi.report.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/users")
    public Object users(){
        return "users";
    }

    @GetMapping("/test")
    public Object test(){
        return "test";
    }
}
