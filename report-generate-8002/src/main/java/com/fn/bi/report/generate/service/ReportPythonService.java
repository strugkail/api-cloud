package com.fn.bi.report.generate.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

//@FeignClient(name = "report-develop")
public interface ReportPythonService {

    void funnelsGenerate(@RequestParam("map") Map<String,Object> map);

    void trendGenerate(@RequestParam("map") Map<String,Object> map);

}
