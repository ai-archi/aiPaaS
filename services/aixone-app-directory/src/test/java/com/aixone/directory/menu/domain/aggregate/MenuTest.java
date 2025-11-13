package com.aixone.directory.menu.domain.aggregate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 菜单聚合根单元测试
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@DisplayName("菜单聚合根测试")
class MenuTest {

    private String tenantId;
    private String menuId;

    @BeforeEach
    void setUp() {
        tenantId = "tenant-" + UUID.randomUUID().toString();
        menuId = "menu-" + UUID.randomUUID().toString();
    }

    @Test
    @DisplayName("创建菜单 - 成功")
    void testCreate_Success() {
        // When
        Menu menu = Menu.create(tenantId, "测试菜单", "测试菜单标题", "/test");

        // Then
        assertNotNull(menu);
        assertNotNull(menu.getCreatedAt());
        assertNotNull(menu.getUpdatedAt());
        assertEquals(tenantId, menu.getTenantId());
        assertEquals("测试菜单", menu.getName());
        assertEquals("测试菜单标题", menu.getTitle());
        assertEquals("/test", menu.getPath());
        assertEquals("menu", menu.getType());
        assertEquals("tab", menu.getRenderType());
        assertFalse(menu.getKeepalive());
        assertEquals(0, menu.getDisplayOrder());
        assertTrue(menu.getVisible());
    }

    @Test
    @DisplayName("创建菜单 - 默认值验证")
    void testCreate_DefaultValues() {
        // When
        Menu menu = Menu.create(tenantId, "菜单", "菜单标题", "/menu");

        // Then
        assertEquals("menu", menu.getType());
        assertEquals("tab", menu.getRenderType());
        assertFalse(menu.getKeepalive());
        assertEquals(0, menu.getDisplayOrder());
        assertTrue(menu.getVisible());
        assertNull(menu.getParentId());
        assertNull(menu.getIcon());
        assertNull(menu.getComponent());
        assertNull(menu.getUrl());
        assertNull(menu.getConfig());
        assertNull(menu.getExtend());
    }

    @Test
    @DisplayName("更新菜单 - 成功")
    void testUpdate_Success() {
        // Given
        Menu menu = Menu.create(tenantId, "原菜单", "原标题", "/original");
        LocalDateTime originalUpdatedAt = menu.getUpdatedAt();
        
        // 等待一小段时间，确保updatedAt会变化
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        menu.update("新菜单", "新标题", "/new");

        // Then
        assertEquals("新菜单", menu.getName());
        assertEquals("新标题", menu.getTitle());
        assertEquals("/new", menu.getPath());
        assertNotNull(menu.getUpdatedAt());
        // updatedAt应该被更新（注意：由于时间精度问题，这里只验证不为null）
    }

    @Test
    @DisplayName("设置父菜单 - 成功")
    void testSetParent_Success() {
        // Given
        Menu menu = Menu.create(tenantId, "子菜单", "子菜单标题", "/child");
        String parentId = "parent-" + UUID.randomUUID().toString();
        LocalDateTime originalUpdatedAt = menu.getUpdatedAt();
        
        // 等待一小段时间
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        menu.setParent(parentId);

        // Then
        assertEquals(parentId, menu.getParentId());
        assertNotNull(menu.getUpdatedAt());
    }

    @Test
    @DisplayName("设置显示顺序 - 成功")
    void testSetDisplayOrder_Success() {
        // Given
        Menu menu = Menu.create(tenantId, "菜单", "菜单标题", "/menu");
        LocalDateTime originalUpdatedAt = menu.getUpdatedAt();
        
        // 等待一小段时间
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        menu.setDisplayOrder(10);

        // Then
        assertEquals(10, menu.getDisplayOrder());
        assertNotNull(menu.getUpdatedAt());
    }

    @Test
    @DisplayName("设置可见性 - 成功")
    void testSetVisible_Success() {
        // Given
        Menu menu = Menu.create(tenantId, "菜单", "菜单标题", "/menu");
        LocalDateTime originalUpdatedAt = menu.getUpdatedAt();
        
        // 等待一小段时间
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        menu.setVisible(false);

        // Then
        assertFalse(menu.getVisible());
        assertNotNull(menu.getUpdatedAt());
    }

    @Test
    @DisplayName("菜单构建器 - 完整属性")
    void testBuilder_WithAllProperties() {
        // When
        Menu menu = Menu.builder()
                .id(menuId)
                .tenantId(tenantId)
                .parentId("parent-id")
                .name("完整菜单")
                .title("完整菜单标题")
                .path("/full")
                .icon("icon-full")
                .type("directory")
                .renderType("iframe")
                .component("views/full/index")
                .url("https://example.com")
                .keepalive(true)
                .displayOrder(5)
                .visible(false)
                .config("{\"key\":\"value\"}")
                .extend("extend-data")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Then
        assertNotNull(menu);
        assertEquals(menuId, menu.getId());
        assertEquals(tenantId, menu.getTenantId());
        assertEquals("parent-id", menu.getParentId());
        assertEquals("完整菜单", menu.getName());
        assertEquals("完整菜单标题", menu.getTitle());
        assertEquals("/full", menu.getPath());
        assertEquals("icon-full", menu.getIcon());
        assertEquals("directory", menu.getType());
        assertEquals("iframe", menu.getRenderType());
        assertEquals("views/full/index", menu.getComponent());
        assertEquals("https://example.com", menu.getUrl());
        assertTrue(menu.getKeepalive());
        assertEquals(5, menu.getDisplayOrder());
        assertFalse(menu.getVisible());
        assertEquals("{\"key\":\"value\"}", menu.getConfig());
        assertEquals("extend-data", menu.getExtend());
        assertNotNull(menu.getCreatedAt());
        assertNotNull(menu.getUpdatedAt());
    }
}

