package com.fn.bi.report.auth.controller;

import com.fn.bi.backend.common.dto.Result;
import com.fn.bi.report.auth.service.TokenService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/auth")
public class Logout {
    @Resource
    private TokenService tokenService;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @PostMapping("/logout")
    @Transactional
    public Result<?> logout(@RequestParam String enName) {
        String refreshToken = tokenService.getRefreshToken(enName);
        tokenService.delAccessToken(enName);
        tokenService.delRefreshToken(refreshToken);
        return Result.ok("已退出");
    }

}
