package com.fn.bi.report.send;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.fn.bi.report.send", "com.fn.bi.backend.common.config.core"})
@EnableFeignClients
@EnableDiscoveryClient
public class ReportSend {
    public static void main(String[] args) {
        SpringApplication.run(ReportSend.class, args);
    }
}
