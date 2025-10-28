package com.aixone.workbench.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.aixone.workbench.menu.interfaces.rest.MenuController;
import com.aixone.workbench.dashboard.interfaces.rest.DashboardController;
import com.aixone.workbench.quickentry.interfaces.rest.QuickEntryController;
import com.aixone.workbench.message.interfaces.rest.MessageController;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 工作台集成测试
 * 验证所有控制器是否正确注入和配置
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("工作台集成测试")
class WorkbenchIntegrationTest {
    
    @Autowired(required = false)
    private MenuController menuController;
    
    @Autowired(required = false)
    private DashboardController dashboardController;
    
    @Autowired(required = false)
    private QuickEntryController quickEntryController;
    
    @Autowired(required = false)
    private MessageController messageController;
    
    @Test
    @DisplayName("测试菜单控制器注入")
    void testMenuControllerInjection() {
        assertThat(menuController).isNotNull();
    }
    
    @Test
    @DisplayName("测试仪表盘控制器注入")
    void testDashboardControllerInjection() {
        assertThat(dashboardController).isNotNull();
    }
    
    @Test
    @DisplayName("测试快捷入口控制器注入")
    void testQuickEntryControllerInjection() {
        assertThat(quickEntryController).isNotNull();
    }
    
    @Test
    @DisplayName("测试消息控制器注入")
    void testMessageControllerInjection() {
        assertThat(messageController).isNotNull();
    }
}

