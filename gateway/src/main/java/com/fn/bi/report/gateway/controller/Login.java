package com.fn.bi.report.gateway.controller;


import com.fn.bi.backend.common.dto.Result;
import com.fn.bi.backend.common.entity.User;
import com.fn.bi.report.gateway.service.PermissionService;
import com.fn.bi.report.gateway.service.TokenService;
import com.fn.bi.report.gateway.service.UserService;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

@RestController
@RefreshScope
public class Login {
    @Resource
    private TokenService checkTokenService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${security.oauth2.client.client-id}")
    private String client_id;
    @Value("${security.oauth2.client.client-secret}")
    private String client_secret;

    @PostMapping("/login")
    public Result<LinkedHashMap<String, Object>> getToken(@RequestBody User user) {
        LinkedHashMap<String, Object> loginInfo;
        try {
            loginInfo = checkTokenService.login(user.getEnName(), user.getPassword(), client_id, client_secret, "password", null, null);
            redisTemplate.opsForValue().set((String) loginInfo.get("access_token"), loginInfo.get("refresh_token"), 86400, TimeUnit.SECONDS);
            loginInfo.put("en_name", user.getEnName());
        } catch (FeignException e) {
            e.printStackTrace();
            return Result.error("登陆失败，请检查用户名密码!");
        }
        return Result.ok(loginInfo);
    }

//    @PostMapping("/refreshToken")
//    public Result<LinkedHashMap<String, Object>> refreshToken(@RequestHeader("Backend-Token") String access_token, @RequestParam("refresh_token") String refresh_token) {
//        LinkedHashMap<String, Object> loginInfo;
//        try {
//            loginInfo = checkTokenService.login(null, null, client_id, client_secret, "refresh_token", refresh_token, null);
//            redisTemplate.delete(access_token);
//            redisTemplate.opsForValue().set((String) loginInfo.get("access_token"), loginInfo.get("refresh_token"), 86400, TimeUnit.SECONDS);
//        } catch (FeignException e) {
//            e.printStackTrace();
//            return Result.error("登录过期，请重新登录!");
//        }
//        return Result.ok(loginInfo);
//    }

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


//    @GetMapping("/userInfo")
//    public Result<?> userInfo(@RequestHeader("Backend-Token") @NotNull String token) {
//        LinkedHashMap<String, Object> map = null;
//        try {
//            map = checkTokenService.checkToken(token);
//        }catch (Exception e){
//            e.printStackTrace();
//            return Result.error(StatusCode.loginExpired,"登录过期,请重新登录");
//        }
//        Object enName = map.get("user_name");
//        Object perms = map.get("authorities");
////        Object roles = map.get("authorities");
////        List<String> urls = permissionService.getUrlByEnName(enName.toString());
//        User user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getEnName, enName.toString()));
//        user.setPassword(null);
//        HashMap<String, Object> resultMap = new HashMap<>();
//        resultMap.put("perms", perms);
//        resultMap.put("user", user);
////        resultMap.put("roles", roles);
//        return Result.ok(resultMap);
//    }
}
