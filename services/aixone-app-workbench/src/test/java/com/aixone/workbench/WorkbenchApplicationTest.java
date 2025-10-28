package com.aixone.workbench;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 工作台应用启动测试
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("工作台应用启动测试")
@Disabled("需要完整的Spring Boot上下文")
class WorkbenchApplicationTest {
    
    @Test
    @DisplayName("测试应用启动")
    void testApplicationContextLoads() {
        // 验证Spring上下文能够正常加载
        // 如果上下文加载失败，测试会抛出异常
    }
}

