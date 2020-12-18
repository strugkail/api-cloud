package com.fn.bi.report.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

@Configuration
public class RedisConfig {
    @Value("${redisHttp}")
    private String redisHttp;

    public @Bean
    RedisConnectionFactory connectionFactory() {
        RestTemplate restTemplate = new RestTemplate();
        LinkedHashMap<String, String> resultMap = restTemplate.getForObject(redisHttp, LinkedHashMap.class);
        assert resultMap != null;
        String ips = resultMap.get("shardInfo");
        List<String> ipList = Arrays.asList(ips.split(" "));
        return new LettuceConnectionFactory(new RedisClusterConfiguration(ipList));
    }
}
