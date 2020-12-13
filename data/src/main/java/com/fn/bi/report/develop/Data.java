package com.fn.bi.report.develop;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.fn.bi.report.develop"
        , "com.fn.bi.backend.common.config.core"
        , "com.fn.bi.backend.common.config.mybatisplus"})
@MapperScan({"com.fn.bi.report.develop.mapper"})
@EnableFeignClients
@EnableDiscoveryClient
public class Data {
    public static void main(String[] args) {
        SpringApplication.run(Data.class, args);
    }
}
