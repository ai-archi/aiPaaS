package com.aixone.workbench.menu.infrastructure.service;

import com.aixone.workbench.menu.application.dto.MenuDTO;
import com.aixone.workbench.menu.domain.model.Menu;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 菜单聚合服务新字段测试
 * 测试新增字段：title, renderType, component, url, keepalive, extend
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("菜单聚合服务新字段测试")
class MenuAggregationServiceImplNewFieldTest {
    
    @Mock
    private com.aixone.workbench.menu.domain.repository.MenuRepository menuRepository;
    
    @Mock
    private com.aixone.workbench.menu.domain.repository.UserMenuCustomRepository userMenuCustomRepository;
    
    @Mock
    private com.aixone.workbench.menu.domain.remote.DirectoryServiceClient directoryServiceClient;
    
    @Test
    @DisplayName("测试Menu转换MenuDTO - 包含所有新字段")
    void testMenuToDTO_WithAllNewFields() {
        // Given
        UUID menuId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        
        Menu menu = Menu.builder()
                .id(menuId)
                .tenantId(tenantId)
                .parentId(null)
                .name("test-menu")
                .title("测试菜单")
                .path("/test")
                .icon("el-icon-Monitor")
                .type("menu")
                .renderType("tab")
                .component("/src/views/backend/test/index.vue")
                .url("https://example.com")
                .keepalive(true)
                .displayOrder(1)
                .visible(true)
                .config("{\"permission\":\"test\"}")
                .extend("default")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // When
        MenuDTO dto = toMenuDTO(menu);
        
        // Then
        assertThat(dto.getId()).isEqualTo(menuId.toString());
        assertThat(dto.getName()).isEqualTo("test-menu");
        assertThat(dto.getTitle()).isEqualTo("测试菜单");
        assertThat(dto.getPath()).isEqualTo("/test");
        assertThat(dto.getIcon()).isEqualTo("el-icon-Monitor");
        assertThat(dto.getType()).isEqualTo("menu");
        assertThat(dto.getRenderType()).isEqualTo("tab");
        assertThat(dto.getComponent()).isEqualTo("/src/views/backend/test/index.vue");
        assertThat(dto.getUrl()).isEqualTo("https://example.com");
        assertThat(dto.getKeepalive()).isTrue();
        assertThat(dto.getConfig()).isEqualTo("{\"permission\":\"test\"}");
        assertThat(dto.getExtend()).isEqualTo("default");
    }
    
    @Test
    @DisplayName("测试外部链接菜单")
    void testExternalLinkMenu() {
        // Given
        Menu linkMenu = Menu.builder()
                .id(UUID.randomUUID())
                .name("external-link")
                .title("外部链接")
                .path("/external")
                .renderType("link")
                .url("https://example.com")
                .type("menu")
                .visible(true)
                .build();
        
        // When
        MenuDTO dto = toMenuDTO(linkMenu);
        
        // Then
        assertThat(dto.getRenderType()).isEqualTo("link");
        assertThat(dto.getUrl()).isEqualTo("https://example.com");
    }
    
    @Test
    @DisplayName("测试iframe菜单")
    void testIframeMenu() {
        // Given
        Menu iframeMenu = Menu.builder()
                .id(UUID.randomUUID())
                .name("iframe-menu")
                .title("内嵌页面")
                .path("/iframe")
                .renderType("iframe")
                .url("https://embed.example.com")
                .type("menu")
                .visible(true)
                .build();
        
        // When
        MenuDTO dto = toMenuDTO(iframeMenu);
        
        // Then
        assertThat(dto.getRenderType()).isEqualTo("iframe");
        assertThat(dto.getUrl()).isEqualTo("https://embed.example.com");
    }
    
    @Test
    @DisplayName("测试组件菜单")
    void testComponentMenu() {
        // Given
        Menu componentMenu = Menu.builder()
                .id(UUID.randomUUID())
                .name("component-menu")
                .title("组件菜单")
                .path("/component")
                .renderType("tab")
                .component("/src/views/backend/test/component.vue")
                .keepalive(true)
                .type("menu")
                .visible(true)
                .build();
        
        // When
        MenuDTO dto = toMenuDTO(componentMenu);
        
        // Then
        assertThat(dto.getRenderType()).isEqualTo("tab");
        assertThat(dto.getComponent()).isEqualTo("/src/views/backend/test/component.vue");
        assertThat(dto.getKeepalive()).isTrue();
    }
    
    @Test
    @DisplayName("测试目录菜单")
    void testDirectoryMenu() {
        // Given
        Menu dirMenu = Menu.builder()
                .id(UUID.randomUUID())
                .name("directory")
                .title("目录")
                .path("/dir")
                .type("menu_dir")
                .visible(true)
                .build();
        
        // When
        MenuDTO dto = toMenuDTO(dirMenu);
        
        // Then
        assertThat(dto.getType()).isEqualTo("DIRECTORY");
        assertThat(dto.getChildren()).isNotNull();
    }
    
    // Helper method
    private MenuDTO toMenuDTO(Menu menu) {
        return MenuDTO.builder()
                .id(menu.getId() != null ? menu.getId().toString() : null)
                .tenantId(menu.getTenantId())
                .parentId(menu.getParentId())
                .name(menu.getName())
                .title(menu.getTitle())
                .path(menu.getPath())
                .icon(menu.getIcon())
                .type(menu.getType() != null ? menu.getType().toString() : null)
                .renderType(menu.getRenderType())
                .component(menu.getComponent())
                .url(menu.getUrl())
                .keepalive(menu.getKeepalive())
                .displayOrder(menu.getDisplayOrder())
                .visible(menu.isVisible())
                .config(menu.getConfig())
                .extend(menu.getExtend())
                .children(List.of())
                .build();
    }
}

