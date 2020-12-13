package com.fn.bi.report.login;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.fn.bi.report.login","com.fn.bi.backend.common.config.core"})
@MapperScan("com.fn.bi.report.login.mapper")
@EnableDiscoveryClient
public class ReportLogin {
    public static void main(String[] args) {
        SpringApplication.run(ReportLogin.class,args);
    }
}
