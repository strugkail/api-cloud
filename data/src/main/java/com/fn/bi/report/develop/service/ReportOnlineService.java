package com.fn.bi.report.develop.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fn.bi.backend.common.entity.ReportOnline;
import com.fn.bi.backend.common.entity.SheetOnline;
import com.fn.bi.report.develop.mapper.ReportOnlineMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ReportOnlineService extends ServiceImpl<ReportOnlineMapper, ReportOnline> {
    @Resource
    private SheetOnlineService sheetOnlineService;

    public ReportOnline getOnlineWithSheet(String reportId) {
        ReportOnline reportOnline = this.getOne(Wrappers.<ReportOnline>lambdaQuery()
                .eq(ReportOnline::getReportId, reportId));

        if (reportOnline != null) {
            List<SheetOnline> sheetOnlineList = sheetOnlineService.list(Wrappers.<SheetOnline>lambdaQuery()
                    .eq(SheetOnline::getReportId, reportId)
                    .orderByAsc(SheetOnline::getSortNo));
            reportOnline.setSheets(sheetOnlineList);
        }

        return reportOnline;
    }

//    public ReportOnline getOnlineNoSheet(String reportId) {
//        return this.getOne(Wrappers.<ReportOnline>lambdaQuery()
//                .eq(ReportOnline::getReportId, reportId));
//    }


//    public List<BackendReport> getList(BackendReport report, MyPage page) {
//        BackendReportExample backendReportExample = new BackendReportExample();
//        BackendReportExample.Criteria criteria = backendReportExample.createCriteria();
//        if (StringUtil.isNotEmpty(report.getReportName())) {
//            criteria.andReportNameLike("%" + report.getReportName() + "%");
//        }
//        if (StringUtil.isNotEmpty(report.getReportId())) {
//            criteria.andReportIdLike("%" + report.getReportId() + "%");
//        }
//        PageHelper.startPage(page.getPage(), page.getLimit());
//        List<BackendReport> backendReports = reportMapper.selectByExample(backendReportExample);
//        for (BackendReport backendReport : backendReports) {
//            BackendSheetExample backendSheetExample = new BackendSheetExample();
//            BackendSheetExample.Criteria sheetExampleCriteria = backendSheetExample.createCriteria();
//            sheetExampleCriteria.andReportIdEqualTo(backendReport.getReportId());
//            backendReportExample.setOrderByClause(CommonData.SORT_NO);
//            List<BackendSheet> backendSheets = sheetMapper.selectByExample(backendSheetExample);
//            backendReport.setBackendSheets(backendSheets);
//        }
//        return backendReports;
//    }
//
//    public Integer add(BackendReport report) {
//        return reportMapper.insert(report);
//    }
//
//    @Transactional(rollbackFor = Exception.class)
//    public Integer del(String reportId) {
//        reportMapper.deleteByPrimaryKey(reportId);
//        BackendSheetExample sheetExample = new BackendSheetExample();
//        BackendSheetExample.Criteria criteria = sheetExample.createCriteria();
//        criteria.andReportIdEqualTo(reportId);
//        return sheetMapper.deleteByExample(sheetExample);
//    }
//
//    public Integer update(BackendReport report) {
//        report.setUpdateTime(LocalDateTime.now());
//        return reportMapper.updateByPrimaryKeySelective(report);
//    }
//    public BackendReport getSortBackendReport(String reportId) {
//        BackendReport backendReport = reportMapper.selectByPrimaryKey(reportId);
//        BackendSheetExample sheetExample = new BackendSheetExample();
//        sheetExample.setOrderByClause(CommonData.SORT_NO);
//        BackendSheetExample.Criteria criteria = sheetExample.createCriteria();
//        criteria.andReportIdEqualTo(reportId);
//        List<BackendSheet> backendSheets = sheetMapper.selectByExample(sheetExample);
//        backendReport.setBackendSheets(backendSheets);
//        return backendReport;
//    }
//    public BackendReport get(String reportId) {
//        return reportMapper.selectByPrimaryKey(reportId);
//    }


}
