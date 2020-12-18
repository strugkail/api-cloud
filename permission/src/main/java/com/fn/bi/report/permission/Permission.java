package com.fn.bi.report.permission;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.fn.bi.report.permission",
        "com.fn.bi.backend.common.config.core",
        "com.fn.bi.backend.common.config.mybatisplus"})
@MapperScan("com.fn.bi.report.permission.mapper")
@EnableDiscoveryClient
public class Permission {
    public static void main(String[] args) {
        SpringApplication.run(Permission.class);
    }
}
