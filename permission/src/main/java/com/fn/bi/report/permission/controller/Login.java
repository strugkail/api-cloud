//package com.fn.bi.login.controller;
//
//
//import com.fn.bi.backend.common.dto.Result;
//import com.fn.bi.login.service.CheckTokenService;
//import com.fn.bi.backend.common.entity.User;
//import feign.FeignException;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.context.config.annotation.RefreshScope;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.Resource;
//import java.util.LinkedHashMap;
//
//@RestController
//@RefreshScope
//public class Login {
//    @Resource
//    private CheckTokenService checkTokenService;
//
//    @Value("${security.oauth2.client.client-id}")
//    private String client_id;
//    @Value("${security.oauth2.client.client-secret}")
//    private String client_secret;
//
//    @PostMapping("/login")
//    public Result<LinkedHashMap<String, Object>> getToken(@RequestBody User user) {
//        LinkedHashMap<String, Object> loginInfo;
//        try {
//            loginInfo = checkTokenService.login(user.getEnName(), user.getPassword(), client_id, client_secret, "password", null, null);
//            loginInfo.put("en_name", user.getEnName());
//        } catch (FeignException e) {
//            e.printStackTrace();
//            return Result.error("登陆失败，请检查用户名密码!");
//        }
//        return Result.ok(loginInfo);
//    }
//
//    @PostMapping("/refreshToken")
//    public Result<LinkedHashMap<String, Object>> refreshToken(String refresh_token) {
//        LinkedHashMap<String, Object> loginInfo;
//        try {
//            loginInfo = checkTokenService.login(null, null, client_id, client_secret, "refresh_token", refresh_token, null);
//        } catch (FeignException e) {
//            e.printStackTrace();
//            return Result.error("登录过期，请重新登录!");
//        }
//        return Result.ok(loginInfo);
//    }
//
//    @PostMapping("/loginByCode")
//    public Result<LinkedHashMap<String, Object>> getTokenByCode(String code) {
//        LinkedHashMap<String, Object> loginInfo;
//        try {
//            loginInfo = checkTokenService.login(null, null, client_id, client_secret, "authorization_code", null, code);
//        } catch (FeignException e) {
//            e.printStackTrace();
//            return Result.error("授权码无效!");
//        }
//        return Result.ok(loginInfo);
//    }
//}
