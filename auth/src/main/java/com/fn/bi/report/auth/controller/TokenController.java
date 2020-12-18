package com.fn.bi.report.auth.controller;

import com.fn.bi.report.auth.service.TokenService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/oauth")
public class TokenController {
    @Resource
    private TokenService tokenService;

    @GetMapping("/getRefreshToken")
    public String getRefreshToken(String enName) {
        return tokenService.getRefreshToken(enName);
    }
}
