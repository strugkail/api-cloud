package com.fn.bi.report.gateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fn.bi.backend.common.dto.Result;
import com.fn.bi.backend.common.ienum.StatusCode;
import com.fn.bi.report.gateway.service.TokenService;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RefreshScope
public class PermissionFilter implements GlobalFilter, Ordered {
    @Resource
    private TokenService tokenService;
    @Value("${security.oauth2.client.redirect}")
    private String redirect;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${security.oauth2.client.client-id}")
    private String client_id;
    @Value("${security.oauth2.client.client-secret}")
    private String client_secret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String ipAddress = getIpAddress(request);

//        String host = Objects.requireNonNull(request.getHeaders().get("Host")).get(0);
//        host = host.substring(0, host.indexOf(":"));
        if (ApplicationListener.whiteList.contains(ipAddress)) return chain.filter(exchange);

        String url = request.getPath().value();
        if (!ApplicationListener.permissions.contains(url)) return chain.filter(exchange);


        List<String> tokens = request.getHeaders().get("Backend-Token");


        //没有token
        if (tokens == null) {
            response.setStatusCode(HttpStatus.SEE_OTHER);
            response.getHeaders().set(HttpHeaders.LOCATION, redirect);
            return response.setComplete();
        }
//            return response.writeWith(Mono.just(response.bufferFactory()
//                .wrap(objToByte(Result.error(StatusCode.notLogin, "请先登录")))));


        LinkedHashMap<String, Object> checkResult;
        try {
            checkResult = tokenService.checkToken(tokens.get(0));
        } catch (FeignException e) {
            //判断是否过期，否则续期
//            if (e.getMessage().contains("Token has expired")) {
            Object refreshToken = redisTemplate.opsForValue().get(tokens.get(0));
            if (refreshToken != null) {
                LinkedHashMap<String, Object> login = tokenService.login(null, null
                        , client_id, client_secret, "refresh_token", (String) refreshToken, null);
                redisTemplate.delete(tokens.get(0));
                redisTemplate.opsForValue().set((String) login.get("access_token"), login.get("refresh_token"), 86400, TimeUnit.SECONDS);
                response.getHeaders().add("access_token", (String) login.get("access_token"));
                response.getHeaders().add("refresh_token", (String) login.get("refresh_token"));
                response.getHeaders().add("Access-Control-Expose-Headers", "access_token");
                response.getHeaders().add("Access-Control-Expose-Headers", "refresh_token");
                return chain.filter(exchange);
//                }

//                return response.writeWith(Mono.just(response.bufferFactory()
//                        .wrap(objToByte(Result.error(StatusCode.loginExpired, "登录过期")))));
//                String refreshToken = tokenService.getRefreshToken(request.getHeaders().get("enName").get(0));
//                LinkedHashMap<String, LinkedHashMap<String,String>> newRefreshToken = tokenService.refreshToken(refreshToken);
//                response.getHeaders().add("New-Token",((HashMap<String,String>)newRefreshToken.get("data")).get("newRefreshToken"));
//                response.getHeaders().add("Access-Control-Expose-Headers","New-Token");
            }
//            return response.writeWith(Mono.just(response.bufferFactory()
//                    .wrap(objToByte(Result.error(StatusCode.notLogin, "token无效,请重新登录")))));
//            response.getHeaders().add("Backend-Token","*-*-*-****-*-*-*-*-*-*-*-*---*-*");
//            response.getHeaders().add("Access-Control-Expose-Headers","Backend-Token");
            return response.writeWith(Mono.just(response.bufferFactory()
                    .wrap(objToByte(Result.error(StatusCode.badToken, "请登录")))));
        }

        response.getHeaders().add("Content-Type", "json/plain;charset=UTF-8");
        HashSet<String> authorities = objToSet(checkResult.get("authorities"));
        //无权限
        if (authorities == null || !authorities.contains(url))
            return response.writeWith(Mono.just(response.bufferFactory()
                    .wrap(objToByte(Result.error(StatusCode.notAuthz, "无权限")))));
        //权限不符合
//        for (String authority : authorities) {
//            String permissionsUrl = ApplicationListener.permissions.get(authority);
//            if (permissionsUrl.equals(url)) return chain.filter(exchange);
//            if (permissionsUrl.contains("**") && url.indexOf(permissionsUrl.substring(0, permissionsUrl.length() - 2)) == 0)
//                return chain.filter(exchange);
//        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }


    public HashSet<String> objToSet(Object obj) {
        HashSet<String> set = new HashSet<>();
        if (obj instanceof ArrayList<?>) {
            for (Object o : (List<?>) obj) {
                set.add((String) o);
            }
        }
        return set;
    }

    public byte[] objToByte(Object obj) {
        byte[] bytes = null;
        try {
            bytes = new ObjectMapper().writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static String getIpAddress(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        String ip = headers.getFirst("x-forwarded-for");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if (ip.contains(",")) {
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddress().getAddress().getHostAddress();
        }
        return ip;
    }
}
