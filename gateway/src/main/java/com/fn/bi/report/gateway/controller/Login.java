package com.fn.bi.report.gateway.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fn.bi.report.gateway.service.CheckTokenService;
import com.fn.bi.report.gateway.service.PermissionService;
import com.fn.bi.report.gateway.service.UserService;
import com.fn.bi.backend.common.dto.Result;
import com.fn.bi.backend.common.entity.User;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RefreshScope
public class Login {
    @Resource
    private CheckTokenService checkTokenService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private UserService userService;

    @Value("${security.oauth2.client.client-id}")
    private String client_id;
    @Value("${security.oauth2.client.client-secret}")
    private String client_secret;

    @PostMapping("/login")
    public Result<LinkedHashMap<String, Object>> getToken(@RequestBody User user) {
        LinkedHashMap<String, Object> loginInfo;
        try {
            loginInfo = checkTokenService.login(user.getEnName(), user.getPassword(), client_id, client_secret, "password", null, null);
            loginInfo.put("en_name", user.getEnName());
        } catch (FeignException e) {
            e.printStackTrace();
            return Result.error("登陆失败，请检查用户名密码!");
        }
        return Result.ok(loginInfo);
    }

    @PostMapping("/refreshToken")
    public Result<LinkedHashMap<String, Object>> refreshToken(String refresh_token) {
        LinkedHashMap<String, Object> loginInfo;
        try {
            loginInfo = checkTokenService.login(null, null, client_id, client_secret, "refresh_token", refresh_token, null);
        } catch (FeignException e) {
            e.printStackTrace();
            return Result.error("登录过期，请重新登录!");
        }
        return Result.ok(loginInfo);
    }

    @PostMapping("/loginByCode")
    public Result<LinkedHashMap<String, Object>> getTokenByCode(String code) {
        LinkedHashMap<String, Object> loginInfo;
        try {
            loginInfo = checkTokenService.login(null, null, client_id, client_secret, "authorization_code", null, code);
        } catch (FeignException e) {
            e.printStackTrace();
            return Result.error("授权码无效!");
        }
        return Result.ok(loginInfo);
    }


    @GetMapping("/userInfo")
    public Result<HashMap<String, Object>> userInfo(@RequestHeader("Backend-Token") @NotNull String token) {
        LinkedHashMap<String, Object> map = checkTokenService.checkToken(token);
        Object enName = map.get("user_name");
        Object roles = map.get("authorities");
        List<String> urls = permissionService.getUrlByEnName(enName.toString());
        User user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getEnName, enName.toString()));
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("perms", urls);
        resultMap.put("roles", roles);
        resultMap.put("user", user);
        return Result.ok(resultMap);
    }
}
