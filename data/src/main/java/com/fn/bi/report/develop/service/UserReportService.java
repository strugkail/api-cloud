package com.fn.bi.report.develop.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fn.bi.backend.common.dto.Result;
import com.fn.bi.backend.common.entity.User;
import com.fn.bi.backend.common.entity.UserReport;
import com.fn.bi.report.develop.mapper.UserReportMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserReportService extends ServiceImpl<UserReportMapper, UserReport> {
    @Resource
    private UserReportMapper userReportMapper;
    @Resource
    private UserService userService;

    public Result<List<String>> getUserMails(String reportIds, boolean isTest) {
        List<Integer> userIdList;
        if (isTest)
            userIdList = list(Wrappers.<UserReport>lambdaQuery()
                    .eq(UserReport::getReportId, reportIds)
                    .eq(UserReport::getIsTest, true))
                    .stream().map(UserReport::getUserId).collect(Collectors.toList());
        else
            userIdList = list(Wrappers.<UserReport>lambdaQuery()
                    .eq(UserReport::getReportId, reportIds)
                    .eq(UserReport::getIsProd, true))
                    .stream().map(UserReport::getUserId).collect(Collectors.toList());
        if (userIdList.size() > 0) {
            return Result.ok(userService.listByIds(userIdList).stream().map(User::getEmail).collect(Collectors.toList()));
        } else {
            return Result.error("没有收件人");
        }
    }

    public int setTestAddressee(String setTestAddressee, String reportId) {
        return userReportMapper.setTestAddressee(setTestAddressee, reportId);
    }

    public int setProAddressee(String setProAddressee, String reportId) {
        return userReportMapper.setProAddressee(setProAddressee, reportId);
    }

    public int cancelTestAddressee(String cancelTestAddressee, String reportId) {
        return userReportMapper.cancelTestAddressee(cancelTestAddressee, reportId);
    }

    public int cancelProAddressee(String cancelProAddressee, String reportId) {
        return userReportMapper.cancelProAddressee(cancelProAddressee, reportId);
    }

    @Transactional
    public List<String> setAddresseeByEmail(List<String> emails, Integer reportId, String isTest) {
        List<String> failEmails = new ArrayList<>();
        for (String email : emails) {
            User user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getEmail, email));
            if (user == null) {
                failEmails.add(email);
            } else {
                UserReport userReport;
                userReport = userReportMapper.selectOne(Wrappers.<UserReport>lambdaQuery().eq(UserReport::getReportId, reportId).eq(UserReport::getUserId, user.getId()));
                if (userReport == null) {
                    userReport = new UserReport().setReportId(reportId).setUserId(user.getId());
                    if (isTest.toLowerCase().contains("test")) {
                        userReport.setIsTest(true);
                    }
                    if (isTest.toLowerCase().contains("prod")) {
                        userReport.setIsProd(true);
                    }
                    userReportMapper.insert(userReport);
                } else {
                    if (isTest.toLowerCase().contains("test")) {
                        userReport.setIsTest(true);
                    }
                    if (isTest.toLowerCase().contains("prod")) {
                        userReport.setIsProd(true);
                    }
                    userReportMapper.updateById(userReport);
                }
            }
        }
        return failEmails;
    }
}
