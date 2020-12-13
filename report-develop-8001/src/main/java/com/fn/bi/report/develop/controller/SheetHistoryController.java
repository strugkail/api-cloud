package com.fn.bi.report.develop.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fn.bi.backend.common.dto.Result;
import com.fn.bi.backend.common.entity.FatherReport;
import com.fn.bi.backend.common.entity.ReportHistory;
import com.fn.bi.backend.common.entity.SheetHistory;
import com.fn.bi.report.develop.service.ReportHistoryService;
import com.fn.bi.report.develop.service.SheetHistoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author gyf
 * @since 2020-10-16
 */
@RestController
@RequestMapping("/develop/sheet/history")
public class SheetHistoryController {
    @Resource
    private SheetHistoryService sheetHistoryService;
    @Resource
    private ReportHistoryService reportHistoryService;

    @PostMapping("/update")
    @Transactional
    public Result<?> updateSheet(@RequestBody SheetHistory sheetHistory) {
        ReportHistory reportHistory = reportHistoryService.getOne(Wrappers.<ReportHistory>lambdaQuery()
                .eq(FatherReport::getReportId, sheetHistory.getReportId())
                .eq(FatherReport::getVersion, sheetHistory.getVersion()));
        reportHistory.setStatus(0);
        reportHistoryService.updateById(reportHistory);
        if (sheetHistory.getId() == null && !StringUtils.isEmpty(sheetHistory.getVersion())) {
            sheetHistory.setStatus(true);
            return sheetHistoryService.save(sheetHistory)
                    ? Result.ok("sheet添加成功", sheetHistoryService.getOne(Wrappers.<SheetHistory>lambdaQuery().setEntity(sheetHistory)))
                    : Result.error("sheet添加失败");
        } else {
            return sheetHistoryService.updateById(sheetHistory) ? Result.ok("sheet更新成功") : Result.error("sheet更新失败");
        }

    }

    @PostMapping("/del")
    public Result<?> delSheet(SheetHistory sheetHistory) {
        return sheetHistoryService.removeById(sheetHistory.getId()) ? Result.ok("sheet删除成功") : Result.error("sheet删除失败");
    }

}
