package com.aixone.workbench.integration;

import com.aixone.workbench.menu.interfaces.rest.MenuController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 菜单集成测试
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("菜单集成测试")
class MenuIntegrationTest {
    
    @Autowired
    private MenuController menuController;
    
    @Test
    @DisplayName("测试菜单控制器注入")
    void testMenuControllerInjection() {
        assertThat(menuController).isNotNull();
    }
}

