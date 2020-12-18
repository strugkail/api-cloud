package com.fn.bi.report.auth.service;

import com.fn.bi.report.auth.mapper.TokenMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TokenService {
    @Resource
    private TokenMapper tokenMapper;

    public void delAccessToken(String enName) {
        tokenMapper.delAccessToken(enName);
    }

    public String getRefreshToken(String enName) {
        return tokenMapper.getRefreshToken(enName);
    }

    public void delRefreshToken(String accessToken) {
        tokenMapper.delRefreshToken(accessToken);
    }
}
