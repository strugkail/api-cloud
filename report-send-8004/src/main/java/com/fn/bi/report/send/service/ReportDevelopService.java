package com.fn.bi.report.send.service;

import com.fn.bi.backend.common.dto.Result;
import com.fn.bi.backend.common.entity.FatherReport;
import com.fn.bi.backend.common.entity.ReportHistory;
import com.fn.bi.backend.common.entity.ReportOnline;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "report-develop")
public interface ReportDevelopService {

    @PostMapping(value = "/develop/userReport/getUserMails")
    Result<List<String>> getUserMails(@RequestParam("reportIds") String reportIds, @RequestParam("isTest") boolean isTest);

    @PostMapping(value = "/develop/report/online/getOnlineWithSheet")
    Result<ReportOnline> getOnlineWithSheet(@RequestParam("reportId") String reportId);

    @GetMapping(value = "/develop/report/history/getHistoryWithSheet")
    Result<ReportHistory> getHistoryWithSheet(@RequestParam("reportId") String reportId);

    @GetMapping(value = "/develop/report/history/getHistoryNoSheetsByReportIds")
    Result<List<ReportHistory>> getHistoryNoSheetsByReportIds(@RequestParam("reportIds") String reportIds);

    @GetMapping("/develop/report/history/getSendList")
    Result<List<FatherReport<?>>> getSendList(@RequestParam("reportIds") String reportIds,@RequestParam("isTest")  Boolean isTest);
}
