package com.fn.bi.report.send.service;

import com.fn.bi.backend.common.dto.Result;
import com.fn.bi.backend.common.dto.SendParameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "report-generate")
public interface ReportGenerateService {

    @PostMapping(value = "/generate/report")
    Result<?> generatorReport(@RequestBody SendParameter parameter);

}
