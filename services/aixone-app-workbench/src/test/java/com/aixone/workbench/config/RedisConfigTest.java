package com.aixone.workbench.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.junit.jupiter.api.Disabled;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Redis配置测试
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Redis配置测试")
@Disabled("需要Spring Boot上下文和Redis连接")
class RedisConfigTest {
    
    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;
    
    @Test
    @DisplayName("测试RedisTemplate配置")
    void testRedisTemplateConfiguration() {
        // 验证RedisTemplate Bean是否正确配置
        assertThat(redisTemplate).isNotNull();
    }
    
    @Test
    @DisplayName("测试Redis序列化配置")
    void testRedisSerialization() {
        // Given
        String key = "test:key";
        String value = "test value";
        
        // When
        if (redisTemplate != null) {
            redisTemplate.opsForValue().set(key, value);
            Object result = redisTemplate.opsForValue().get(key);
            
            // Then
            assertThat(result).isEqualTo(value);
        }
    }
}

