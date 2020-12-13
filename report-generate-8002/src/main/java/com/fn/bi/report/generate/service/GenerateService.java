package com.fn.bi.report.generate.service;

import com.fn.bi.backend.common.dto.SendParameter;
import com.fn.bi.backend.common.entity.FatherReport;
import com.fn.bi.backend.common.entity.FatherSheet;
import com.fn.bi.backend.common.entity.ReportHistory;
import com.fn.bi.backend.common.entity.ReportOnline;
import com.fn.bi.backend.common.ienum.GenerateType;
import com.fn.bi.backend.common.ienum.Str;
import com.fn.bi.backend.common.model.BackendExcel;
import com.fn.bi.backend.common.model.MsgException;
import com.fn.bi.report.generate.mapper.GenerateMapper;
import com.fn.bi.report.generate.mapper.VerticalGenerateMapper;
import com.fn.bi.report.generate.util.ExcelWriteUtil;
import com.fn.bi.report.generate.util.ExcelWriteUtil2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RefreshScope
public class GenerateService {
    @Resource
    private GenerateMapper adbGeneratorMapper;
    @Resource
    private VerticalGenerateMapper verticalGenerateMapper;
    @Resource
    private ReportDevelopService reportDevelopService;

    @Value("${backend.prod-target-path}")
    private String prodTargetPath;
    @Value("${backend.prod-template-path}")
    private String prodTemplatePath;

    @Value("${backend.test-target-path}")
    private String testTargetPath;
    @Value("${backend.test-template-path}")
    private String testTemplatePath;


    static final LinkedHashMap<String, Object> noData = new LinkedHashMap<>();
    final Pattern patternT_N = Pattern.compile("[#now+]+(-?\\d{2}|-?\\d)");

    static {
        noData.put(Str.noData.val, Str.noData.val);
    }

    //mq消费SendParameter对象
    public void generatorConsumer(SendParameter parameter) throws MsgException {
        if (parameter.getIsTest())
            generateReport(parameter, reportDevelopService.getHistoryWithSheet(parameter.getReportIds()).getData());
        else generateReport(parameter, reportDevelopService.getOnlineWithSheet(parameter.getReportIds()).getData());
    }

    //异步
    //解析并执行SQL获取数据
    private void generateReport(SendParameter parameter, FatherReport<? extends FatherSheet> fatherReport) throws MsgException {
        LocalDateTime dateTime = parameter.getDate();
        String d_t = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(dateTime);
        String m_t = DateTimeFormatter.ofPattern("yyyy-MM").format(dateTime);
        String week = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(dateTime.plusDays(-6));
        String firstDay = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(dateTime.with(TemporalAdjusters.firstDayOfMonth()));
        String dt = DateTimeFormatter.ofPattern("yyyyMMdd").format(dateTime);
        String mt = DateTimeFormatter.ofPattern("yyyyMM").format(dateTime);
        String d_t_h_m = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(dateTime);

        Optional.ofNullable(fatherReport).ifPresent(report -> {
            List<? extends FatherSheet> fatherSheets = report.getSheets();
            List<Object> data = new LinkedList<>();
            String[][] titles = new String[fatherSheets.size()][];

            for (int i = 0; i < fatherSheets.size(); i++) {
                FatherSheet sheet = fatherSheets.get(i);
                formatTitles(d_t_h_m, dateTime, week, firstDay, d_t, m_t, titles, i, sheet);
                String newSQl = formatSql(parameter, d_t, m_t, dt, mt, sheet.getSheetSql());
                if (report.reportForm.contains("回购率") || report.reportForm.contains("小润发合并")) {
                    List<List<LinkedHashMap<String, Object>>> dataMap = new ArrayList<>();
                    String[] split = newSQl.split(Str.end.val);
                    for (String s : split) {
                        log.info("\n" + s);
                        dataMap.add(getLinkedHashMaps(s, report.getGenerateType()));
                    }
                    data.add(dataMap);
                }
                if (report.reportForm.contains("默认")) {
                    log.info("\n" + newSQl);
                    data.add(getLinkedHashMaps(newSQl, report.generateType));
                }
            }
            BackendExcel<?> backendExcel = new BackendExcel<>().setTitles(titles).setData(data).setReportForm(report.reportForm);
            if (report instanceof ReportOnline)
                backendExcel.setTemplatePath(prodTemplatePath + report.getGenerateType() + Str.l.val + report.reportId + Str.xlsx.val)
                        .setTargetFileName(prodTargetPath + report.getGenerateType() + Str.l.val + d_t + Str.l.val + report.reportName + "_" + dt + Str.xlsx.val);

            if (report instanceof ReportHistory)
                backendExcel.setTargetFileName(testTargetPath + report.getGenerateType() + Str.l.val + d_t + Str.l.val + report.reportName + "_" + dt + Str.xlsx.val)
                        .setTemplatePath(testTemplatePath + report.getGenerateType() + Str.l.val + report.reportId + Str.l.val + report.getVersion() + Str.xlsx.val);

            //异步填充SQL文件
            long l = System.currentTimeMillis();
            if (backendExcel.getReportForm().equals("小润发合并")) ExcelWriteUtil2.writeReport(backendExcel);
            else ExcelWriteUtil.writeReport(backendExcel);

            log.info("生成时间--------" + (System.currentTimeMillis() - l));
        });
    }


    private List<LinkedHashMap<String, Object>> getLinkedHashMaps(String sql, String generateType) {
        List<LinkedHashMap<String, Object>> linkedHashMaps = new ArrayList<>();
        if (generateType.equals(GenerateType.adb.val)) linkedHashMaps = adbGeneratorMapper.executeSql(sql);
        if (generateType.equals(GenerateType.vertical.val)) linkedHashMaps = verticalGenerateMapper.executeSql(sql);
        if (linkedHashMaps.size() == 0) linkedHashMaps.add(noData);
        return linkedHashMaps;
    }

    private String formatSql(SendParameter parameter, String d_t, String m_t, String dt, String mt, String sql) {
        String newSQl = sql
                .replaceAll(Str.d_t.val, "'" + d_t + "'")
                .replaceAll(Str.m_t.val, "'" + m_t + "'")
                .replaceAll(Str.mt.val, "'" + mt + "'")
                .replaceAll(Str.dt.val, "'" + dt + "'");
        if (parameter.getIsTest()) newSQl = newSQl.replaceAll(Str.db.val, "rpt_test");
        else newSQl = newSQl.replaceAll(Str.db.val, "rpt");
        return newSQl;
    }


    //将title字符串转为二维数据，并拼接日期
    private void formatTitles(String dt_h_m, LocalDateTime dateTime, String week, String firstDay, String d_t, String m_t, String[][] titles, int i, FatherSheet sheetOnline) {
        if (!StringUtils.isEmpty(sheetOnline.getSheetTitle())) {
            Matcher matcher = patternT_N.matcher(sheetOnline.sheetTitle);
            while (matcher.find()) {
                String group = matcher.group();
                String t_n = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(dateTime.plusDays(Integer.parseInt(group.substring(group.indexOf("+") + 1))));
                sheetOnline.sheetTitle = sheetOnline.sheetTitle.replace(group, t_n);
            }
            titles[i] = sheetOnline.sheetTitle
                    .replaceAll(Str.d_t.val, d_t).replaceAll(Str.m_t.val, m_t)
                    .replaceAll(Str.week.val, week).replaceAll(Str.firstDay.val, firstDay)
                    .replaceAll(Str.d_t_h_m.val, dt_h_m)
                    .split(Str.end.val);
        }
    }
    public void batchGeneratorReport(SendParameter parameter,int threadSize) {
        List<String> list;
        if(parameter.getIsTest())
            list = reportDevelopService.getHistoryAll().getData().stream().map(e -> String.valueOf(e.getReportId())).collect(Collectors.toList());
        else
            list = reportDevelopService.getOnlineAll().getData().stream().map(e -> String.valueOf(e.getReportId())).collect(Collectors.toList());

            // 开始时间
            long start = System.currentTimeMillis();

            // 总数据条数
            int dataSize = list.size();
            // 线程数
            int threadNum = dataSize / threadSize + 1;
            // 定义标记,过滤threadNum为整数
            boolean special = dataSize % threadSize == 0;

            // 创建一个线程池
            ExecutorService exec = Executors.newFixedThreadPool(threadNum);
            // 定义一个任务集合
            List<Callable<Integer>> tasks = new ArrayList<>();
            Callable<Integer> task;
            List<String> cutList;

    }
}
