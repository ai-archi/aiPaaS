package com.aixone.auth.ratelimit;

/**
 * 接口限流服务接口
 * 定义尝试获取令牌方法
 */
public interface RateLimitService {
    /**
     * 尝试获取令牌，返回是否允许通过
     * @param key 限流key（如手机号、IP等）
     * @param limit 每分钟最大次数
     * @return 是否允许
     */
    boolean tryAcquire(String key, int limit);
} 