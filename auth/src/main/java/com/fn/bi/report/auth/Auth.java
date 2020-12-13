package com.fn.bi.report.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.fn.bi.report.auth.mapper")
public class Auth {
    public static void main(String[] args) {
        SpringApplication.run(Auth.class, args);
    }
}
