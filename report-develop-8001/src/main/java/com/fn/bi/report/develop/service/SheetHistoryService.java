package com.fn.bi.report.develop.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fn.bi.backend.common.entity.SheetHistory;
import com.fn.bi.report.develop.mapper.SheetHistoryMapper;
import org.springframework.stereotype.Service;

@Service
public class SheetHistoryService extends ServiceImpl<SheetHistoryMapper, SheetHistory> {
//    @Resource
//    private SheetHistoryMapper sheetHistoryMapper;
//    @Resource
//    private ReportHistoryMapper reportHistoryMapper;
//    @Resource
//    private BackendMailService mailService;

//    public List<BackendSheetHistory> getList(String reportId) {
//        BackendSheetHistoryExample backendSheetHistoryExample = new BackendSheetHistoryExample();
//        BackendSheetHistoryExample.Criteria criteria = backendSheetHistoryExample.createCriteria();
//        criteria.andDeletedEqualTo(false).andReportIdEqualTo(reportId);
//        backendSheetHistoryExample.setOrderByClause(CommonData.SORT_NO);
//        List<BackendSheetHistory> backendSheetHistories = backendSheetHistoryMapper.selectByExample(backendSheetHistoryExample);
//        return backendSheetHistories;
//    }
//
//    public Integer add(BackendSheetHistory sheet) {
//        BackendReportHistory backendReportHistory = reportHistoryMapper.selectByPrimaryKey(mailService.getReportLatestId(sheet.getReportId()));
//        sheet.setVersion(backendReportHistory.getVersion());
//        sheet.setAddTime(LocalDateTime.now());
//        sheet.setUpdateTime(LocalDateTime.now());
//        return backendSheetHistoryMapper.insert(sheet);
//    }
//
//    public Integer del(BackendSheetHistory sheet) {
//        sheet.setDeleted(true);
//        return backendSheetHistoryMapper.updateByPrimaryKeySelective(sheet);
//    }
//
//    public Integer update(BackendSheetHistory sheet) {
//        int i = 0;
//        if(StringUtil.isEmpty(sheet.getSheetId())){
//            sheet.setSheetId(String.valueOf(SeqGenerateUtil.getId()));
//            sheet.setAddTime(LocalDateTime.now());
//            sheet.setUpdateTime(LocalDateTime.now());
//            i = backendSheetHistoryMapper.insert(sheet);
//        }else{
//            sheet.setUpdateTime(LocalDateTime.now());
//            i = backendSheetHistoryMapper.updateByPrimaryKeySelective(sheet);
//        }
//        return i;
//    }

}
