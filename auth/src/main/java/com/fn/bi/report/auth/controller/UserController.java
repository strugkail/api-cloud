package com.fn.bi.report.auth.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fn.bi.backend.common.dto.Result;
import com.fn.bi.backend.common.entity.User;
import com.fn.bi.report.auth.service.PermissionService;
import com.fn.bi.report.auth.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author gyf
 * @since 2020-11-25
 */
@RestController
@RequestMapping("/auth/user")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private PermissionService permissionService;

    @GetMapping("/info")
    public Result<?> getUserInfo(@RequestHeader("backend-User-enName") String enName) {
        User user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getEnName, enName));
        HashMap<String, Object> result = new HashMap<>();
        result.put("perms", permissionService.getUrlByEnName(user.getEnName()));
        result.put("user", user);
        return Result.ok(result);
    }


}
