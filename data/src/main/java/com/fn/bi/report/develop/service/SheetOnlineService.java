package com.fn.bi.report.develop.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fn.bi.backend.common.entity.SheetOnline;
import com.fn.bi.report.develop.mapper.SheetOnlineMapper;
import org.springframework.stereotype.Service;

@Service
public class SheetOnlineService extends ServiceImpl<SheetOnlineMapper, SheetOnline> {
//    @Resource
//    private ReportOnlineMapper reportOnlineMapper;
//
//    @Resource
//    private SheetOnlineMapper sheetOnlineMapper;

//    public Integer add(BackendSheet sheet) {
//        return backendSheetMapper.insert(sheet);
//    }
//
//    public Integer delByReportId(String reportId) {
//        BackendSheetExample sheetExample = new BackendSheetExample();
//        BackendSheetExample.Criteria criteria = sheetExample.createCriteria();
//        criteria.andReportIdEqualTo(reportId);
//        return backendSheetMapper.deleteByExample(sheetExample);
//    }
//    public Integer update(BackendSheet sheet) {
//        sheet.setUpdateTime(LocalDateTime.now());
//        return backendSheetMapper.updateByPrimaryKeySelective(sheet);
//    }
}
