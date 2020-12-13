package com.fn.bi.report.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@MapperScan("com.fn.bi.report.api.mapper")
public class Api extends SpringBootServletInitializer{
    public static void main(String[] args) {
        SpringApplication.run(Api.class, args);
    }
}

