package com.aixone.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

/**
 * Redis配置
 */
@Configuration
@EnableRedisRepositories
public class RedisConfig {
    // 可扩展自定义配置
} 