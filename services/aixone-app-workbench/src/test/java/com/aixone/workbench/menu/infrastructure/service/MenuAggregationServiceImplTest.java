package com.aixone.workbench.menu.infrastructure.service;

import com.aixone.workbench.menu.application.dto.MenuDTO;
import com.aixone.workbench.menu.application.dto.UserMenuCustomDTO;
import com.aixone.workbench.menu.domain.model.Menu;
import com.aixone.workbench.menu.domain.model.UserMenuCustom;
import com.aixone.workbench.menu.domain.remote.DirectoryServiceClient;
import com.aixone.workbench.menu.domain.repository.MenuRepository;
import com.aixone.workbench.menu.domain.repository.UserMenuCustomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 菜单聚合服务测试
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("菜单聚合服务测试")
class MenuAggregationServiceImplTest {
    
    @Mock
    private MenuRepository menuRepository;
    
    @Mock
    private UserMenuCustomRepository userMenuCustomRepository;
    
    @Mock
    private DirectoryServiceClient directoryServiceClient;
    
    @InjectMocks
    private MenuAggregationServiceImpl menuAggregationService;
    
    private UUID userId;
    private UUID tenantId;
    private UUID menuId1;
    private UUID menuId2;
    private Menu rootMenu;
    private Menu childMenu;
    private UserMenuCustom customConfig;
    
    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        tenantId = UUID.randomUUID();
        menuId1 = UUID.randomUUID();
        menuId2 = UUID.randomUUID();
        
        rootMenu = createRootMenu();
        childMenu = createChildMenu();
        customConfig = createUserMenuCustom();
    }
    
    @Test
    @DisplayName("测试聚合用户可见菜单 - 成功")
    void testAggregateVisibleMenus_Success() {
        // Given
        List<Menu> menus = List.of(rootMenu, childMenu);
        when(menuRepository.findByTenantIdOrderByDisplayOrderAsc(tenantId)).thenReturn(menus);
        when(userMenuCustomRepository.findByUserIdAndTenantId(userId, tenantId))
                .thenReturn(List.of(customConfig));
        
        // When
        List<MenuDTO> result = menuAggregationService.aggregateVisibleMenus(
                userId, tenantId, List.of(UUID.randomUUID()));
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1); // 只有根菜单
        
        verify(menuRepository).findByTenantIdOrderByDisplayOrderAsc(tenantId);
        verify(userMenuCustomRepository).findByUserIdAndTenantId(userId, tenantId);
    }
    
    @Test
    @DisplayName("测试聚合用户可见菜单 - 从目录服务拉取")
    void testAggregateVisibleMenus_FromDirectory() {
        // Given
        when(menuRepository.findByTenantIdOrderByDisplayOrderAsc(tenantId)).thenReturn(List.of());
        
        List<Map<String, Object>> directoryMenus = List.of(
                Map.of("id", menuId1.toString(), "name", "菜单1", "type", "MENU", 
                       "displayOrder", 1, "visible", true)
        );
        when(directoryServiceClient.getMenus(tenantId)).thenReturn(directoryMenus);
        when(userMenuCustomRepository.findByUserIdAndTenantId(userId, tenantId))
                .thenReturn(List.of());
        
        // When
        List<MenuDTO> result = menuAggregationService.aggregateVisibleMenus(
                userId, tenantId, List.of(UUID.randomUUID()));
        
        // Then
        assertThat(result).isNotNull();
        verify(directoryServiceClient).getMenus(tenantId);
    }
    
    @Test
    @DisplayName("测试获取用户菜单个性化配置")
    void testGetUserMenuCustomConfig() {
        // Given
        List<UserMenuCustom> customs = List.of(customConfig);
        when(userMenuCustomRepository.findByUserIdAndTenantId(userId, tenantId)).thenReturn(customs);
        
        // When
        Map<String, UserMenuCustomDTO> result = menuAggregationService.getUserMenuCustomConfig(userId, tenantId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).containsKey(menuId1.toString());
        verify(userMenuCustomRepository).findByUserIdAndTenantId(userId, tenantId);
    }
    
    @Test
    @DisplayName("测试保存用户菜单个性化配置 - 新配置")
    void testSaveUserMenuCustom_NewConfig() {
        // Given
        String config = "{\"isQuickEntry\": true}";
        when(userMenuCustomRepository.findByUserIdAndTenantIdAndMenuId(userId, tenantId, menuId1))
                .thenReturn(Optional.empty());
        when(userMenuCustomRepository.save(any(UserMenuCustom.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        menuAggregationService.saveUserMenuCustom(userId, tenantId, menuId1, config);
        
        // Then
        verify(userMenuCustomRepository).save(any(UserMenuCustom.class));
    }
    
    @Test
    @DisplayName("测试保存用户菜单个性化配置 - 更新配置")
    void testSaveUserMenuCustom_UpdateConfig() {
        // Given
        String config = "{\"isQuickEntry\": false}";
        when(userMenuCustomRepository.findByUserIdAndTenantIdAndMenuId(userId, tenantId, menuId1))
                .thenReturn(Optional.of(customConfig));
        when(userMenuCustomRepository.save(any(UserMenuCustom.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        menuAggregationService.saveUserMenuCustom(userId, tenantId, menuId1, config);
        
        // Then
        verify(userMenuCustomRepository).save(customConfig);
        assertThat(customConfig.getConfig()).isEqualTo(config);
    }
    
    @Test
    @DisplayName("测试构建菜单树")
    void testBuildMenuTree() {
        // Given
        List<Menu> menus = List.of(rootMenu, childMenu);
        when(menuRepository.findByTenantIdOrderByDisplayOrderAsc(tenantId)).thenReturn(menus);
        
        // When
        List<MenuDTO> result = menuAggregationService.buildMenuTree(tenantId, List.of());
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1); // 只有根菜单
        
        MenuDTO root = result.get(0);
        assertThat(root.getId()).isEqualTo(menuId1.toString());
        assertThat(root.getChildren()).isNotNull();
        assertThat(root.getChildren()).hasSize(1);
    }
    
    @Test
    @DisplayName("测试聚合菜单 - 远程服务调用异常")
    void testAggregateVisibleMenus_RemoteServiceError() {
        // Given
        when(menuRepository.findByTenantIdOrderByDisplayOrderAsc(tenantId)).thenReturn(List.of());
        when(directoryServiceClient.getMenus(tenantId)).thenThrow(new RuntimeException("Remote service error"));
        
        // When
        List<MenuDTO> result = menuAggregationService.aggregateVisibleMenus(
                userId, tenantId, List.of(UUID.randomUUID()));
        
        // Then
        assertThat(result).isEmpty();
        verify(directoryServiceClient).getMenus(tenantId);
    }
    
    @Test
    @DisplayName("测试聚合菜单 - 空菜单列表")
    void testAggregateVisibleMenus_EmptyMenuList() {
        // Given
        when(menuRepository.findByTenantIdOrderByDisplayOrderAsc(tenantId)).thenReturn(List.of());
        when(directoryServiceClient.getMenus(tenantId)).thenReturn(List.of());
        when(userMenuCustomRepository.findByUserIdAndTenantId(userId, tenantId))
                .thenReturn(List.of());
        
        // When
        List<MenuDTO> result = menuAggregationService.aggregateVisibleMenus(
                userId, tenantId, List.of());
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    @DisplayName("测试保存个性化配置 - 配置为空")
    void testSaveUserMenuCustom_EmptyConfig() {
        // Given
        String config = "";
        when(userMenuCustomRepository.findByUserIdAndTenantIdAndMenuId(userId, tenantId, menuId1))
                .thenReturn(Optional.empty());
        
        // When
        menuAggregationService.saveUserMenuCustom(userId, tenantId, menuId1, config);
        
        // Then
        verify(userMenuCustomRepository).save(any(UserMenuCustom.class));
    }
    
    @Test
    @DisplayName("测试获取个性化配置 - 空配置")
    void testGetUserMenuCustomConfig_EmptyConfig() {
        // Given
        when(userMenuCustomRepository.findByUserIdAndTenantId(userId, tenantId))
                .thenReturn(List.of());
        
        // When
        Map<String, UserMenuCustomDTO> result = menuAggregationService.getUserMenuCustomConfig(userId, tenantId);
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    @DisplayName("测试构建菜单树 - 多层嵌套")
    void testBuildMenuTree_MultiLevelNesting() {
        // Given
        Menu grandChild = Menu.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .parentId(menuId2)
                .name("孙菜单")
                .type("menu")
                .displayOrder(1)
                .visible(true)
                .build();
        
        List<Menu> menus = List.of(rootMenu, childMenu, grandChild);
        when(menuRepository.findByTenantIdOrderByDisplayOrderAsc(tenantId)).thenReturn(menus);
        
        // When
        List<MenuDTO> result = menuAggregationService.buildMenuTree(tenantId, List.of());
        
        // Then
        assertThat(result).hasSize(1);
        MenuDTO root = result.get(0);
        assertThat(root.getChildren()).hasSize(1);
        assertThat(root.getChildren().get(0).getChildren()).hasSize(1);
    }
    
    @Test
    @DisplayName("测试构建菜单树 - 过滤隐藏菜单")
    void testBuildMenuTree_FilterHiddenMenus() {
        // Given
        Menu hiddenMenu = Menu.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .parentId(null)
                .name("隐藏菜单")
                .type("menu")
                .displayOrder(2)
                .visible(false)
                .build();
        
        List<Menu> menus = List.of(rootMenu, hiddenMenu);
        when(menuRepository.findByTenantIdOrderByDisplayOrderAsc(tenantId)).thenReturn(menus);
        
        // When
        List<MenuDTO> result = menuAggregationService.buildMenuTree(tenantId, List.of());
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("根菜单");
    }
    
    @Test
    @DisplayName("测试构建菜单树 - 应用个性化隐藏")
    void testBuildMenuTree_ApplyCustomHidden() {
        // Given
        List<Menu> menus = List.of(rootMenu);
        when(menuRepository.findByTenantIdOrderByDisplayOrderAsc(tenantId)).thenReturn(menus);
        
        UserMenuCustom hiddenCustom = UserMenuCustom.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tenantId(tenantId)
                .menuId(menuId1)
                .isHidden(true)
                .build();
        
        when(userMenuCustomRepository.findByUserIdAndTenantId(userId, tenantId))
                .thenReturn(List.of(hiddenCustom));
        
        // When
        List<MenuDTO> result = menuAggregationService.aggregateVisibleMenus(
                userId, tenantId, List.of());
        
        // Then
        assertThat(result).isEmpty();
    }
    
    // Helper methods
    
    private Menu createRootMenu() {
        return Menu.builder()
                .id(menuId1)
                .tenantId(tenantId)
                .parentId(null)
                .name("根菜单")
                .path("/root")
                .icon("icon-folder")
                .type("menu_dir")
                .displayOrder(1)
                .visible(true)
                .config("{}")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    private Menu createChildMenu() {
        return Menu.builder()
                .id(menuId2)
                .tenantId(tenantId)
                .parentId(menuId1)
                .name("子菜单")
                .path("/child")
                .icon("icon-file")
                .type("menu")
                .displayOrder(1)
                .visible(true)
                .config("{}")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    private UserMenuCustom createUserMenuCustom() {
        return UserMenuCustom.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tenantId(tenantId)
                .menuId(menuId1)
                .config("{\"isQuickEntry\": true}")
                .isQuickEntry(true)
                .customOrder(1)
                .isHidden(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
