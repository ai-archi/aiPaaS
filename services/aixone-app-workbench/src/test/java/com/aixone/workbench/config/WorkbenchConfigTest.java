package com.aixone.workbench.config;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 工作台配置测试
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@SpringBootTest
@DisplayName("工作台配置测试")
@Disabled("需要Spring Boot上下文和数据库连接")
class WorkbenchConfigTest {
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Test
    @DisplayName("测试RedisTemplate配置")
    void testRedisTemplateConfiguration() {
        // 验证RedisTemplate Bean是否存在
        assertThat(applicationContext.getBeanNamesForType(org.springframework.data.redis.core.RedisTemplate.class))
                .isNotEmpty();
    }
    
    @Test
    @DisplayName("测试配置类加载")
    void testWorkbenchConfigLoading() {
        WorkbenchConfig config = applicationContext.getBean(WorkbenchConfig.class);
        assertThat(config).isNotNull();
    }
}

