//package com.fn.bi.permission.service;
//
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.util.LinkedHashMap;
//
//@FeignClient(name = "aaaa", url = "http://localhost:8081/")
//public interface CheckTokenService {
//
//    @GetMapping("/oauth/check_token")
//    LinkedHashMap<String, Object> checkToken(@RequestParam("token") String token);
//
//    @PostMapping("/oauth/token")
//    LinkedHashMap<String, Object> login(@RequestParam("username") String username, @RequestParam("password") String password,
//                                        @RequestParam("client_id") String client_id, @RequestParam("client_secret") String client_secret,
//                                        @RequestParam("grant_type") String grant_type,@RequestParam("refresh_token") String refresh_token,
//                                        @RequestParam("code") String code);
//
////    @PostMapping("/oauth/refresh_token")
////    LinkedHashMap<String, Object> refresh_token(
////            @RequestParam("client_id") String client_id, @RequestParam("client_secret") String client_secret,
////            @RequestParam("refresh_token") String refresh_token, @RequestParam("grant_type") String grant_type);
//}
