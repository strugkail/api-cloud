package com.fn.bi.report.develop.controller;

import com.fn.bi.backend.common.dto.Result;
import com.fn.bi.report.develop.service.UserReportService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/develop/userReport")
public class UserReportController {
    @Resource
    private UserReportService userReportService;

    @PostMapping("/setAddressee")
    public Object setAddressee(String setTestAddressee, String setProAddressee,
                               String cancelTestAddressee, String cancelProAddressee, String reportId) {
        int num;
        if (!StringUtils.isEmpty(setTestAddressee)) {
            num = userReportService.setTestAddressee(setTestAddressee, reportId);
            return Result.ok("成功设置" + num + "个测试收件人");
        }
        if (!StringUtils.isEmpty(setProAddressee)) {
            num = userReportService.setProAddressee(setProAddressee, reportId);
            return Result.ok("成功设置" + num + "个正式收件人");
        }
        if (!StringUtils.isEmpty(cancelTestAddressee)) {
            num = userReportService.cancelTestAddressee(cancelTestAddressee, reportId);
            return Result.ok("成功取消" + num + "个测试收件人");
        }
        if (!StringUtils.isEmpty(cancelProAddressee)) {
            num = userReportService.cancelProAddressee(cancelProAddressee, reportId);
            return Result.ok("成功取消" + num + "个正式收件人");
        }
        return null;
    }

    @PostMapping("/getUserMails")
    public Result<?> getUserMails(String reportIds, boolean isTest) {
        return userReportService.getUserMails(reportIds, isTest);
    }
}
