package com.aixone.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * RefreshToken服务实现，基于Redis
 */
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private static final String PREFIX = "refresh_token:";
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public String createAndStore(String userId, String clientId, long expireSeconds) {
        String refreshToken = UUID.randomUUID().toString().replace("-", "");
        String key = PREFIX + refreshToken;
        redisTemplate.opsForValue().set(key, userId, expireSeconds, TimeUnit.SECONDS);
        return refreshToken;
    }

    @Override
    public boolean validate(String refreshToken) {
        String key = PREFIX + refreshToken;
        return redisTemplate.hasKey(key);
    }

    @Override
    public String getUserId(String refreshToken) {
        String key = PREFIX + refreshToken;
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void delete(String refreshToken) {
        String key = PREFIX + refreshToken;
        redisTemplate.delete(key);
    }
} 