package com.aixone.workbench.service;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

/**
 * MenuService 单元测试。
 */
public class MenuServiceTest {
    private final MenuService menuService = new com.aixone.workbench.service.impl.MenuServiceImpl();

    @Test
    void testGetVisibleMenus() {
        // 用随机ID测试返回不为null
        assertNotNull(menuService.getVisibleMenus(UUID.randomUUID(), UUID.randomUUID()));
    }

    @Test
    void testSaveUserMenuCustom() {
        // 不抛异常即可
        assertDoesNotThrow(() -> menuService.saveUserMenuCustom(UUID.randomUUID(), "{}"));
    }
} 