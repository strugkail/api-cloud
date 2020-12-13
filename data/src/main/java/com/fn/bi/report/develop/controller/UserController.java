package com.fn.bi.report.develop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fn.bi.backend.common.dto.Result;
import com.fn.bi.backend.common.entity.User;
import com.fn.bi.report.develop.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
//@RequestMapping("/admin/develop/user")    修改
@RequestMapping("/develop/user")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("/list")
//    @RequiresPermissions("admin:admin:list")
    public Result<Page<User>> getList(User user, Page<User> page) {
        return userService.getList(user, page);
    }

    @GetMapping("/addressee")
    public Result<Map<String, List<?>>> getAllUsers(String reportId) {
        return userService.getAllUsers(reportId);
    }

    @PostMapping("/add")
    public Result<?> addUser(User user) {
        return userService.saveOrUpdate(user) ? Result.ok("添加成功") : Result.error("添加失败");
    }

    @PostMapping("/update")
    public Result<?> updateUser(User user) {
        return userService.saveOrUpdate(user) ? Result.ok("添加成功") : Result.error("添加失败");
    }

    @PostMapping("/del")
//    @RequiresPermissions("admin:admin:list")
    public Object delUser(User user) {
        return userService.removeById(user) ? Result.ok("添加成功") : Result.error("添加失败");
    }
}
