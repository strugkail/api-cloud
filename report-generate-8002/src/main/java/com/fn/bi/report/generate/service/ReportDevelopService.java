package com.fn.bi.report.generate.service;

import com.fn.bi.backend.common.dto.Result;
import com.fn.bi.backend.common.entity.ReportHistory;
import com.fn.bi.backend.common.entity.ReportOnline;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "report-develop")
public interface ReportDevelopService {

    @GetMapping(value = "/develop/report/online/getOnlineWithSheet")
    Result<ReportOnline> getOnlineWithSheet(@RequestParam("reportId") String reportId);

    @GetMapping(value = "/develop/report/online/getAll")
    Result<List<ReportOnline>> getOnlineAll();

    @GetMapping(value = "/develop/report/history/getHistoryWithSheet")
    Result<ReportHistory> getHistoryWithSheet(@RequestParam("reportId") String reportId);

    @GetMapping(value = "/develop/report/history/getAll")
    Result<List<ReportHistory>> getHistoryAll();
}
