package com.fn.bi.report.gateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fn.bi.backend.common.dto.Result;
import com.fn.bi.backend.common.ienum.StatusCode;
import com.fn.bi.report.gateway.service.CheckTokenService;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.*;

@Component
@Slf4j
public class PermissionFilter implements GlobalFilter, Ordered {
    @Resource
    private CheckTokenService checkTokenService;
    @Value("${security.oauth2.client.redirect}")
    private String redirect;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String ipAddress = getIpAddress(request);
        log.info("===========================");
        log.info("真实IP：" + ipAddress);
        log.info("===========================");

//        String host = Objects.requireNonNull(request.getHeaders().get("Host")).get(0);
//        host = host.substring(0, host.indexOf(":"));
        if (ApplicationListener.whiteList.contains(ipAddress)) return chain.filter(exchange);

        String url = request.getPath().value();
        if (!ApplicationListener.permissions.contains(url)) return chain.filter(exchange);


        List<String> strings = request.getHeaders().get("Backend-Token");


        //没有token
        if (strings == null) {
            response.setStatusCode(HttpStatus.SEE_OTHER);
            response.getHeaders().set(HttpHeaders.LOCATION, redirect);
            return response.setComplete();
        }
//            return response.writeWith(Mono.just(response.bufferFactory()
//                .wrap(objToByte(Result.error(StatusCode.notLogin, "请先登录")))));


        LinkedHashMap<String, Object> checkResult;
        try {
            checkResult = checkTokenService.checkToken(strings.get(0));
        } catch (FeignException e) {
            //判断是否过期，否则续期
//            return response.writeWith(Mono.just(response.bufferFactory()
//                    .wrap(objToByte(Result.error(StatusCode.notLogin, "token无效,请重新登录")))));
            response.setStatusCode(HttpStatus.SEE_OTHER);
            response.getHeaders().set(HttpHeaders.LOCATION, redirect);
            return response.setComplete();
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
//        List<String> list = new ArrayList<>();
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
            if (ip.indexOf(",") != -1) {
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("Proxy-Client-IP");
            log.info("代理IP：" + ip);
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
