package com.fn.bi.report.send.service;

import com.fn.bi.backend.common.dto.Result;
import com.fn.bi.backend.common.dto.SendParameter;
import com.fn.bi.backend.common.entity.FatherReport;
import com.fn.bi.backend.common.ienum.Str;
import com.fn.bi.backend.common.model.MsgException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RefreshScope
public class SendService {
    @Resource
    private ReportGenerateService reportGenerateService;
    @Resource
    private ReportDevelopService developService;
    @Resource
    private JavaMailSender javaMailSender;

    @Value("${backend.prod-target-path}")
    private String prodTargetPath;
    @Value("${backend.test-target-path}")
    private String testTargetPath;

    //注入配置文件中配置的信息——>from
    @Value("${spring.mail.from}")
    private String from;

    public Result<?> sendMail(SendParameter sendParameter) {
        String mainReportId = sendParameter.getReportIds();

        //获取报表文件路径
        List<FatherReport<?>> sendList = developService.getSendList(mainReportId, sendParameter.getIsTest()).getData();
        String dt = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(sendParameter.getDate());
        List<String> filePaths = new ArrayList<>();
        for (FatherReport<?> report : sendList) {
            if (sendParameter.getIsTest())
                filePaths.add(testTargetPath + report.getGenerateType() + Str.l.val + dt + Str.l.val
                        + report.getReportName() + "_" + dt.replaceAll("-", "") + Str.xlsx.val);
            else
                filePaths.add(prodTargetPath + report.getGenerateType() + Str.l.val + dt + Str.l.val
                        + report.getReportName() + "_" + dt.replaceAll("-", "") + Str.xlsx.val);

            //是否需要生成，是则调用生成接口
            if (sendParameter.getIsGenerate()) {
                Result<?> result = reportGenerateService.generatorReport(sendParameter.setReportIds(report.getReportId().toString()));
                if (result.getStatusCode() != 200) return result;
            }
        }


        //获取收件人，测试还是正式
        List<String> emailList = developService.getUserMails(mainReportId, sendParameter.getAddresseeEnv()).getData();
        if (emailList == null || emailList.size() == 0) throw new MsgException("请先设置收件人！");
        String[] emails = emailList.toArray(new String[0]);

        //获取报表主体
        String title = sendList.get(0).getReportTitle();
        if (sendParameter.getIsTest())
            title = "[test]" + title;
        if (sendParameter.getIsSend())
            sendMail(emails, title, filePaths.toArray(new String[0]));
        return Result.ok(sendList.get(0).getReportName() + "发送成功！");
    }

    private void sendMail(String[] bcc, String subject, String[] filePaths) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper;
        System.setProperty("mail.mime.splitlongparameters", "false");
        try {
            messageHelper = new MimeMessageHelper(message, true, "utf-8");
            messageHelper.setFrom(from);
            messageHelper.setBcc(bcc);
            messageHelper.setSubject(subject);
            messageHelper.setValidateAddresses(true);
            try {
                for (String filePath : filePaths) {
                    //携带附件
                    FileSystemResource file = new FileSystemResource(filePath);
                    messageHelper.addAttachment(MimeUtility.encodeWord(Objects.requireNonNull(file.getFilename()), "utf-8", "B"), file);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            messageHelper.setText("报表邮件");
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
