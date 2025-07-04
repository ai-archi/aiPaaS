package com.aixone.auth.ratelimit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

/**
 * 分布式限流服务实现，基于Redis
 * 支持每分钟最大次数限制
 */
@Service
public class RateLimitServiceImpl implements RateLimitService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final long WINDOW_MILLIS = 60_000L;

    /**
     * 尝试获取令牌，滑动窗口计数法，支持分布式
     */
    @Override
    public boolean tryAcquire(String key, int limit) {
        String redisKey = "ratelimit:" + key;
        Long count = redisTemplate.opsForValue().increment(redisKey);
        if (count == 1) {
            // 第一次，设置过期时间
            redisTemplate.expire(redisKey, WINDOW_MILLIS, TimeUnit.MILLISECONDS);
        }
        return count != null && count <= limit;
    }
} 