package com.fn.bi.report.generate;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.fn.bi.report.generate"
        , "com.fn.bi.backend.common.config.core"
        , "com.fn.bi.backend.common.config.mybatisplus"},
        exclude= {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@MapperScan("com.fn.bi.report.generate.mapper")
@EnableFeignClients
public class ReportGenerate {
    public static void main(String[] args) {
        SpringApplication.run(ReportGenerate.class, args);
    }
}
