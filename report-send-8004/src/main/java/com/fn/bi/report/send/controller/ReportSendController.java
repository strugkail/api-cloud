package com.fn.bi.report.send.controller;

import com.fn.bi.backend.common.dto.Result;
import com.fn.bi.backend.common.dto.SendParameter;
import com.fn.bi.report.send.service.SendService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/send")
public class ReportSendController {

    @Resource
    private SendService sendService;

    @GetMapping("/report")
    public Result<?> sendMail(SendParameter parameter) {
        return sendService.sendMail(parameter);
    }
}
