package com.fn.bi.report.develop.controller;

import com.fn.bi.backend.common.dto.Result;
import com.fn.bi.backend.common.entity.ReportHistory;
import com.fn.bi.backend.common.ienum.StatusCode;
import com.fn.bi.backend.common.ienum.Str;
import com.fn.bi.backend.common.model.MsgException;
import com.fn.bi.backend.common.util.FileUtil;
import com.fn.bi.report.develop.service.ReportHistoryService;
import com.fn.bi.report.develop.service.UserReportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/develop/upload")
@Slf4j
@RefreshScope
public class UploadController {
    @Resource
    private UserReportService userReportService;
    @Resource
    private ReportHistoryService reportHistoryService;
//    @Value("${backend.prod-target-path}")
//    private String prodTargetPath;
//    @Value("${backend.prod-template-path}")
//    private String prodTemplatePath;
//    @Value("${backend.test-target-path}")
//    private String testTargetPath;
    @Value("${backend.test-template-path}")
    private String testTemplatePath;

    @RequestMapping("/addressee")
    public Result<List<String>> upload(@RequestParam("file") MultipartFile file, Integer reportId) {
        String cellValue;
        List<String> emails;
        try (InputStream fileInputStream = file.getInputStream();
             XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream)) {
            cellValue = workbook.getSheetAt(0).getRow(0).getCell(0).getStringCellValue();
            emails = getEmails(workbook);
        } catch (IOException e) {
            e.printStackTrace();
            throw new MsgException("上传失败,请检查模板");
        }
        assert emails != null;
        List<String> failList = userReportService.setAddresseeByEmail(emails, reportId, cellValue);
        return failList.size() > 0 ? Result.error(StatusCode.addresseeFail, "成功设置" +
                (emails.size() - failList.size()) + "个收件人,\r\n" + "以下收件人失败,请检查:", failList)
                : Result.ok("收件人全部设置成功!");
    }

    @RequestMapping("/excel")
    public Result<?> excel(@RequestParam("file") MultipartFile file, String reportId) {
        ReportHistory historyNoSheets = reportHistoryService.getHistoryNoSheets(reportId);
        String path = testTemplatePath + historyNoSheets.getGenerateType() + Str.l.val
                + reportId + Str.l.val + historyNoSheets.getVersion() + Str.xlsx.val;
        FileUtil.mkdir(path);
        try (InputStream fileInputStream = file.getInputStream();
             XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
             FileOutputStream fileOutputStream = new FileOutputStream(new File(path))) {
            workbook.write(fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MsgException("上传失败");
        }
        return Result.ok("模板更新成功");
    }


    @GetMapping("downloadTemplate")
    public void downloadTemplate(HttpServletResponse response,@RequestParam("reportId") String reportId) throws Exception {
        ReportHistory historyNoSheets = reportHistoryService.getHistoryNoSheets(reportId);
        String path = testTemplatePath + historyNoSheets.getGenerateType() + Str.l.val
                + reportId + Str.l.val + historyNoSheets.getVersion() + Str.xlsx.val;
        FileInputStream fis = new FileInputStream(new File(path));
        // 设置相关格式
        response.setContentType("application/force-download");
        // 设置下载后的文件名以及header
        response.addHeader("Content-disposition", "attachment;fileName=" + historyNoSheets.getReportName()+Str.xlsx.val);
        // 创建输出对象
        OutputStream os = response.getOutputStream();
        // 常规操作
        byte[] buf = new byte[1024];
        int len;
        while ((len = fis.read(buf)) != -1) {
            os.write(buf, 0, len);
        }
        fis.close();
    }


    public List<String> getEmails(XSSFWorkbook workbook) {
        List<String> list = new ArrayList<>();
        for (Sheet sheet : workbook) {
            for (Row row : sheet) {
                if (row.getRowNum() == 0)
                    continue;
                for (Cell cell : row) {
                    list.add(cell.getStringCellValue());
                }
            }
        }
        return list;
    }
}
