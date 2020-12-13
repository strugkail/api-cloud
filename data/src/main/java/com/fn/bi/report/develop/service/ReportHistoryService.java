package com.fn.bi.report.develop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fn.bi.backend.common.dto.Result;
import com.fn.bi.backend.common.entity.*;
import com.fn.bi.backend.common.ienum.Str;
import com.fn.bi.backend.common.util.FileUtil;
import com.fn.bi.report.develop.mapper.ReportHistoryMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RefreshScope
public class ReportHistoryService extends ServiceImpl<ReportHistoryMapper, ReportHistory> {
    @Resource
    private ReportOnlineService reportOnlineService;
    @Resource
    private SheetHistoryService sheetHistoryService;
    @Resource
    private SheetOnlineService sheetOnlineService;
    @Resource
    private ReportHistoryService reportHistoryService;

    @Value("${backend.prod-template-path}")
    private String prodTemplatePath;
    @Value("${backend.test-template-path}")
    private String testTemplatePath;

    //加版本号
    public Result<ReportHistory> getHistoryWithSheets(String reportId) {
        ReportHistory reportHistory = this.getOne(Wrappers.<ReportHistory>lambdaQuery()
                .eq(ReportHistory::getReportId, reportId).eq(ReportHistory::getIsActive, true));
        if (null != reportHistory) {
            List<SheetHistory> sheetHistoryList = sheetHistoryService.list(Wrappers.<SheetHistory>lambdaQuery()
                    .eq(SheetHistory::getReportId, reportId).eq(SheetHistory::getStatus, true)
                    .eq(SheetHistory::getVersion, reportHistory.getVersion())
                    .orderByAsc(SheetHistory::getSortNo));

            reportHistory.setSheets(sheetHistoryList);
        }
        return Result.ok(reportHistory);
    }

    public ReportHistory getHistoryNoSheets(String reportId) {
        return this.getOne(Wrappers.<ReportHistory>lambdaQuery()
                .eq(ReportHistory::getReportId, reportId)
                .eq(ReportHistory::getIsActive, true));
    }

    public Result<List<ReportHistory>> getHistoryNoSheetsByReportIds(String reportIds) {
        String[] split = reportIds.split(Str.d.val);
        List<ReportHistory> reportHistoryList = new ArrayList<>();
        for (String s : split) {
            reportHistoryList.add(this.getOne(Wrappers.<ReportHistory>lambdaQuery()
                    .eq(ReportHistory::getReportId, s)
                    .eq(ReportHistory::getIsActive, true)));
        }
        return Result.ok(reportHistoryList);
    }

    @Transactional
    public Result<?> removeById(String reportId) {
        ReportOnline reportOnline = reportOnlineService.getOnlineWithSheet(reportId);
        if (null != reportOnline) {
            return Result.error(reportOnline.getReportName() + "--> 未下线，请先执行下线操作！");
        } else {
            ReportHistory reportHistory = getHistoryWithSheets(reportId).getData();
            removeById(reportHistory);
            LambdaQueryWrapper<SheetHistory> delWrapper = Wrappers.<SheetHistory>lambdaQuery().eq(FatherSheet::getReportId, reportId);
            List<SheetHistory> list = sheetHistoryService.list(delWrapper);
            for (SheetHistory sheetHistory : list) {
                sheetHistory.setStatus(false);
            }
            sheetHistoryService.updateBatchById(list);
            sheetHistoryService.remove(delWrapper);
            return Result.ok(reportHistory.getReportName() + "--> 删除成功！");
        }
    }


    //发布上线
    @Transactional
    public Result<?> publish(String reportId) throws IOException {
        //1删除线上 report sheet
        reportOnlineService.remove(Wrappers.<ReportOnline>lambdaQuery().eq(ReportOnline::getReportId, reportId));
        sheetOnlineService.remove(Wrappers.<SheetOnline>lambdaQuery().eq(SheetOnline::getReportId, reportId));
        //获取当前版本历史表
        ReportHistory reportHistory = getHistoryWithSheets(reportId).getData();
        //拷贝模板
        String onlinePath = prodTemplatePath + reportHistory.getGenerateType() + Str.l.val + reportHistory.reportId + Str.xlsx.val;
        String oldPath = testTemplatePath + reportHistory.getGenerateType() + Str.l.val + reportHistory.reportId + Str.l.val + reportHistory.getVersion() + Str.xlsx.val;
        //2.修改历史表report sheet取消激活状态
        reportHistory.setIsActive(false);
        reportHistory.setStatus(1);
        this.updateById(reportHistory);
        List<SheetHistory> historySheets = reportHistory.getSheets();
        for (SheetHistory historySheet : historySheets) {
            historySheet.setStatus(false);
        }
        sheetHistoryService.updateBatchById(historySheets);
        //3.版本号report sheet+1,新增历史report sheet，并设置诶激活状态
        reportHistory.setIsActive(true).setVersion(reportHistory.getVersion() + 1).setId(null);
        String newPath = testTemplatePath + reportHistory.getGenerateType() + Str.l.val + reportHistory.reportId + Str.l.val + reportHistory.getVersion() + Str.xlsx.val;
        this.save(reportHistory);
        for (SheetHistory historySheet : historySheets) {
            historySheet.setStatus(true).setVersion(historySheet.getVersion() + 1).setId(null);
        }
        sheetHistoryService.saveBatch(historySheets);
        //5.新增线上report sheet
        ReportOnline reportOnline = new ReportOnline();
        BeanUtils.copyProperties(reportHistory, reportOnline);
        reportOnlineService.save(reportOnline);
        ArrayList<SheetOnline> sheetOnlines = new ArrayList<>();
        for (SheetHistory sheetHistory : historySheets) {
            SheetOnline sheetOnline = new SheetOnline();
            BeanUtils.copyProperties(sheetHistory, sheetOnline);
            sheetOnlines.add(sheetOnline);
        }
        sheetOnlineService.saveBatch(sheetOnlines);
        FileUtil.copyFileNIO(oldPath, newPath);
        FileUtil.copyFileNIO(oldPath, onlinePath);
        return Result.ok(reportHistory.getReportName() + "--> 发布成功！");
    }

    @Transactional
    public Result<?> offline(String reportId) {
        ReportOnline reportOnline;
        //物理删除 online 相关代码
        reportOnline = reportOnlineService.getOnlineWithSheet(reportId);
        reportOnlineService.removeById(reportOnline);
        List<Integer> sheetOnlineList = reportOnline.getSheets().stream().map(SheetOnline::getId).collect(Collectors.toList());
        sheetOnlineService.removeByIds(sheetOnlineList);
        ReportHistory reportHistory = reportHistoryService
                .getOne(Wrappers.<ReportHistory>lambdaQuery().eq(FatherReport::getVersion, reportOnline.getVersion()))
                .setStatus(0);
        reportHistoryService.updateById(reportHistory);
        return Result.ok(reportOnline.getReportName() + "--> 下线成功!");
    }

    public Result<Page<ReportHistory>> getList(ReportHistory reportHistory, Page<ReportHistory> page) {

        LambdaQueryWrapper<ReportHistory> backendReportHistoryQueryWrapper = Wrappers.lambdaQuery();
        Optional.ofNullable(reportHistory.getReportId()).ifPresent(r -> backendReportHistoryQueryWrapper.like(ReportHistory::getReportId, "%" + r + "%"));
        Optional.ofNullable(reportHistory.getReportName()).ifPresent(r -> backendReportHistoryQueryWrapper.like(ReportHistory::getReportName, "%" + r + "%"));
//        Optional.ofNullable(reportHistory.getReportCycle()).ifPresent(r -> backendReportHistoryQueryWrapper.eq(ReportHistory::getReportCycle, r));
        if(!StringUtils.isEmpty(reportHistory.getReportCycle())) backendReportHistoryQueryWrapper.eq(ReportHistory::getReportCycle, reportHistory.getReportCycle());
        Page<ReportHistory> historyPage = page(page, backendReportHistoryQueryWrapper
                .eq(ReportHistory::getIsActive, true).ne(ReportHistory::getStoreType,"AC").orderByAsc(ReportHistory::getStatus)
                .orderByDesc(ReportHistory::getUpdateTime));
        for (ReportHistory history : historyPage.getRecords()) {
            history.setSheets(sheetHistoryService.list(Wrappers.<SheetHistory>lambdaQuery()
                    .eq(SheetHistory::getStatus, true)
                    .eq(SheetHistory::getVersion, history.getVersion())
                    .eq(SheetHistory::getReportId, history.getReportId()).orderByAsc(SheetHistory::getSortNo)));
        }
        return Result.ok(historyPage);
    }

}
