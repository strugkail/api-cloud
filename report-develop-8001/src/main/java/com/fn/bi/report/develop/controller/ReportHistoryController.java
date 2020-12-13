package com.fn.bi.report.develop.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fn.bi.backend.common.dto.Result;
import com.fn.bi.backend.common.entity.FatherReport;
import com.fn.bi.backend.common.entity.ReportHistory;
import com.fn.bi.backend.common.entity.ReportOnline;
import com.fn.bi.report.develop.service.ReportHistoryService;
import com.fn.bi.report.develop.service.ReportOnlineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/develop/report/history")
@Validated
@Slf4j
public class ReportHistoryController {

    @Resource
    private ReportHistoryService reportHistoryService;
    @Resource
    private ReportOnlineService reportOnlineService;

    @GetMapping("/list")
    public Result<Page<ReportHistory>> getList(ReportHistory reportHistory, Page<ReportHistory> page) {
        return reportHistoryService.getList(reportHistory, page);
    }

    @GetMapping("/getAll")
    public Result<List<ReportHistory>> getAll() {
        List<ReportHistory> list = reportHistoryService.list(Wrappers.<ReportHistory>lambdaQuery().eq(ReportHistory::getIsActive, true));
        return Result.ok(list);
    }

    @PostMapping("/update")
    public Result<?> update(ReportHistory reportHistory) {
        reportHistory.setStatus(0);
        reportHistoryService.updateById(reportHistory);
        return Result.ok("修改成功");
    }

    @PostMapping("/add")
    public Result<?> add(ReportHistory reportHistory) {
        List<ReportHistory> histories = reportHistoryService.list(Wrappers.<ReportHistory>lambdaQuery().eq(FatherReport::getReportId, reportHistory.getReportId()));
        if (histories.size() != 0) return Result.error("reportId重复");
        else {
            reportHistory.setStatus(0);
            reportHistoryService.save(reportHistory);
        }
        return Result.ok("添加成功");
    }

    @PostMapping("/del")
    public Result<?> delete(String reportId) {
        return reportHistoryService.removeById(reportId);
    }

    @PostMapping("/publish")
    public Result<?> publish(String reportId) throws IOException {
        return reportHistoryService.publish(reportId);
    }

    @PostMapping("/offline")
    public Result<?> offline(String reportId) {
        return reportHistoryService.offline(reportId);
    }

    @GetMapping("/getHistoryWithSheet")
    public Result<ReportHistory> getHistoryWithSheets(String reportId) {
        return reportHistoryService.getHistoryWithSheets(reportId);
    }

    @GetMapping("/getHistoryNoSheetsByReportIds")
    public Result<List<ReportHistory>> getHistoryNoSheetsByReportIds(String reportIds) {
        return reportHistoryService.getHistoryNoSheetsByReportIds(reportIds);
    }

    @GetMapping("/getSendList")
    public Result<List<FatherReport<?>>> getSendList(String reportIds, Boolean isTest) {
        List<FatherReport<?>> list = new ArrayList<>();
        if (isTest) {
            FatherReport<?> fatherReport = reportHistoryService.getOne(Wrappers.<ReportHistory>lambdaQuery().eq(ReportHistory::getReportId, reportIds).eq(ReportHistory::getIsActive, true));
            list.add(fatherReport);
            Optional.ofNullable(fatherReport.getEnclosureId()).ifPresent(e -> {
                String[] split = e.split(",");
                for (String s : split) {
                    list.add(reportHistoryService.getOne(Wrappers.<ReportHistory>lambdaQuery().eq(ReportHistory::getReportId, s).eq(ReportHistory::getIsActive, true)));
                }
            });
        } else {
            FatherReport<?> fatherReport = reportOnlineService.getOne(Wrappers.<ReportOnline>lambdaQuery().eq(ReportOnline::getReportId, reportIds));
            list.add(fatherReport);
            Optional.ofNullable(fatherReport.getEnclosureId()).ifPresent(e -> {
                String[] split = e.split(",");
                for (String s : split) {
                    list.add(reportOnlineService.getOne(Wrappers.<ReportOnline>lambdaQuery().eq(ReportOnline::getReportId, s)));
                }
            });
        }
        return Result.ok(list);
    }


}
