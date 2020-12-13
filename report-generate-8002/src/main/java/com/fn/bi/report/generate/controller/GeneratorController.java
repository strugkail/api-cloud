package com.fn.bi.report.generate.controller;

import com.fn.bi.backend.common.dto.Result;
import com.fn.bi.backend.common.dto.SendParameter;
import com.fn.bi.report.generate.service.GenerateService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/generate")
public class GeneratorController {

    @Resource
    private GenerateService generatorService;

    //    @PostMapping("/online")
    @PostMapping("/report")
    public Result<String> reportGenerator(@RequestBody SendParameter parameter) {
        generatorService.generatorConsumer(parameter);
        return Result.ok("生成成功!");
    }
    @GetMapping("/batch/report")
    public Result<String> batchReportGenerator(SendParameter parameter,int threadSize) {
        generatorService.batchGeneratorReport(parameter,threadSize);
        return Result.ok("生成成功!");
    }
}
