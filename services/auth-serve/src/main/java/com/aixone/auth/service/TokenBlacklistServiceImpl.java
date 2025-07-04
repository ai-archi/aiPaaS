package com.aixone.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

/**
 * Token黑名单服务实现，基于Redis
 */
@Service
public class TokenBlacklistServiceImpl implements TokenBlacklistService {
    private static final String PREFIX = "token_blacklist:";
    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final long DEFAULT_EXPIRE_SECONDS = 7 * 24 * 3600; // 默认7天

    /**
     * 添加Token到黑名单，默认过期时间为7天
     */
    @Override
    public void addToBlacklist(String token) {
        String key = PREFIX + token;
        redisTemplate.opsForValue().set(key, "1", DEFAULT_EXPIRE_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * 校验Token是否在黑名单
     */
    @Override
    public boolean isBlacklisted(String token) {
        String key = PREFIX + token;
        return redisTemplate.hasKey(key);
    }

    /**
     * 移除Token（可选）
     */
    @Override
    public void removeFromBlacklist(String token) {
        String key = PREFIX + token;
        redisTemplate.delete(key);
    }
} 