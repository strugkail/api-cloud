package com.fn.bi.report.develop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fn.bi.backend.common.dto.Result;
import com.fn.bi.backend.common.entity.User;
import com.fn.bi.backend.common.entity.UserReport;
import com.fn.bi.report.develop.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {
    @Resource
    private UserReportService userReportService;

    public Result<Page<User>> getList(User user, Page<User> page) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        Optional.ofNullable(user.getEmail()).ifPresent(r -> userLambdaQueryWrapper.like(User::getEmail, "%" + r + "%"));
        Optional.ofNullable(user.getCnName()).ifPresent(r -> userLambdaQueryWrapper.like(User::getCnName, "%" + r + "%"));
        Page<User> historyPage = page(page, userLambdaQueryWrapper);
        return Result.ok(historyPage);
    }

    public Result<Map<String, List<?>>> getAllUsers(String reportId) {
        LambdaQueryWrapper<UserReport> userReportLambdaQueryWrapper = Wrappers.lambdaQuery();
        userReportLambdaQueryWrapper.eq(UserReport::getIsProd, true).eq(UserReport::getReportId, reportId);
        List<Integer> prodList = userReportService.list(userReportLambdaQueryWrapper).stream().map(UserReport::getUserId).collect(Collectors.toList());

        userReportLambdaQueryWrapper.clear();
        userReportLambdaQueryWrapper.eq(UserReport::getIsTest, true).eq(UserReport::getReportId, reportId);
        List<Integer> testList = userReportService.list(userReportLambdaQueryWrapper).stream().map(UserReport::getUserId).collect(Collectors.toList());

        Map<String, List<?>> allUsers = new HashMap<>();
        allUsers.put("allUsers", this.list());
        allUsers.put("testAddressee", testList);
        allUsers.put("proAddressee", prodList);
        return Result.ok(allUsers);
    }

//    public List<BackendUser> getList(BackendUser user, MyPage page){
//        BackendUserExample backendUserExample = null;
//        if(StringUtil.isNotEmpty(user.getEmailAddress())){
//            backendUserExample = new BackendUserExample();
//            backendUserExample.createCriteria().andEmailAddressLike("%"+user.getEmailAddress()+"%");
//        }
//        PageHelper.startPage(page.getPage(),page.getLimit());
//        List<BackendUser> backendUsers = backendUserMapper.selectByExample(backendUserExample);
//        return backendUsers;
//    }

    //    public List<BackendUser> getListByIds(List<Integer> userIds){
//        BackendUserExample backendUserExample = null;
//        backendUserExample = new BackendUserExample();
//        backendUserExample.createCriteria().andIdIn(userIds);
//        List<BackendUser> backendUsers = backendUserMapper.selectByExample(backendUserExample);
//        return backendUsers;
//    }
//    public List<User> getListByMaileAddress(List<String> mailAddress) {
//        return this.list(Wrappers.<User>lambdaQuery().in(User::getEmail, mailAddress));
//    }
//    public HashMap<String, List> getAddressee(String reportId){
//        BackendUserExample backendUserExample = new BackendUserExample();
//         backendUserExample.createCriteria().andDeptNameLike("%技术%");
//
//        List<BackendUser> allUsers = backendUserMapper.selectByExample(backendUserExample);
//        List<Integer> testAddressee = userMapper.getTestAddressee(reportId);
//        List<Integer> proAddressee = userMapper.getProAddressee(reportId);
//        HashMap<String, List> users = new HashMap<>();
//        users.put("allUsers",allUsers);
//        users.put("testAddressee",testAddressee);
//        users.put("proAddressee",proAddressee);
//        return users;
//    }
//
//
//    public Integer add(BackendUser user){
//        return backendUserMapper.insert(user);
//    }
//
//    public Integer del(BackendUser user){
//        return backendUserMapper.deleteByPrimaryKey(user.getId());
//    }
//
//    public Integer update(BackendUser user){
//        user.setUpdateTime(LocalDateTime.now());
//        return backendUserMapper.updateByPrimaryKeySelective(user);
//    }
}
