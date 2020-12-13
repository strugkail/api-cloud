package com.fn.bi.report.develop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fn.bi.backend.common.dto.Result;
import com.fn.bi.backend.common.entity.*;
import com.fn.bi.report.develop.service.ReportOnlineService;
import com.fn.bi.report.develop.service.SheetOnlineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/develop/report/online")
@Validated
@Slf4j
public class ReportOnlineController {

    @Resource
    private ReportOnlineService reportOnlineService;

    @Resource
    private SheetOnlineService sheetOnlineService;

    @GetMapping("/list")
    public Result<Page<ReportOnline>> getList(ReportOnline reportOnline, Page<ReportOnline> page) {
        LambdaQueryWrapper<ReportOnline> backendReportQueryWrapper = Wrappers.lambdaQuery();
        Optional.ofNullable(reportOnline.getReportId()).ifPresent(r -> backendReportQueryWrapper.like(ReportOnline::getReportId, "%" + r + "%"));
        Optional.ofNullable(reportOnline.getReportName()).ifPresent(r -> backendReportQueryWrapper.like(ReportOnline::getReportName, "%" + r + "%"));
        Page<ReportOnline> onlinePage = reportOnlineService.page(page, backendReportQueryWrapper.ne(ReportOnline::getStoreType,"AC").orderByAsc(FatherReport::getSortNo));
        for (ReportOnline online : onlinePage.getRecords()) {
            online.setSheets(sheetOnlineService.list(Wrappers.<SheetOnline>lambdaQuery()
                    .eq(FatherSheet::getReportId, online.getReportId()).orderByAsc(FatherSheet::getSortNo)));
        }
        return Result.ok(onlinePage);
    }

    @GetMapping("/getAll")
    public Result<List<ReportOnline>> getAll() {
        return Result.ok( reportOnlineService.list());
    }

    @GetMapping("/get")
    public Result<ReportOnline> get(String reportId) {
        return Result.ok(reportOnlineService.getOnlineWithSheet(reportId));
    }

    @GetMapping("/getOnlineWithSheet")
    public Result<ReportOnline> getOnlineWithSheet(String reportId) {
        ReportOnline sortReportOnline = reportOnlineService.getOnlineWithSheet(reportId);
        return Result.ok(sortReportOnline);
    }

}
