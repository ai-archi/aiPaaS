package com.aixone.directory.menu.interfaces.rest;

import com.aixone.common.api.ApiResponse;
import com.aixone.common.api.PageRequest;
import com.aixone.common.api.PageResult;
import com.aixone.common.api.RowData;
import com.aixone.common.session.SessionContext;
import com.aixone.directory.menu.application.MenuApplicationService;
import com.aixone.directory.menu.application.MenuDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 菜单控制器单元测试
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("菜单控制器测试")
class MenuControllerTest {

    @Mock
    private MenuApplicationService menuApplicationService;

    @InjectMocks
    private MenuController menuController;

    private ObjectMapper objectMapper;
    private String tenantId;
    private String menuId;
    private MenuDto.MenuView testMenuView;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        tenantId = "tenant-" + UUID.randomUUID().toString();
        menuId = "menu-" + UUID.randomUUID().toString();
        
        testMenuView = MenuDto.MenuView.builder()
                .id(menuId)
                .tenantId(tenantId)
                .name("测试菜单")
                .title("测试菜单标题")
                .path("/test")
                .type("menu")
                .renderType("tab")
                .keepalive(false)
                .displayOrder(0)
                .visible(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("获取菜单列表 - 成功")
    void testGetMenus_Success() {
        // Given
        PageResult<MenuDto.MenuView> pageResult = PageResult.of(2L, new PageRequest(1, 20), 
                List.of(testMenuView));

        when(menuApplicationService.findMenus(any(PageRequest.class), eq(tenantId), 
                any(), any(), any(), any())).thenReturn(pageResult);

        // When
        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(tenantId);
            
            ResponseEntity<ApiResponse<PageResult<MenuDto.MenuView>>> response = 
                    menuController.getMenus(1, 20, null, null, null, null, null, null, null);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(200, response.getBody().getCode());
            assertNotNull(response.getBody().getData());
            assertEquals(2L, response.getBody().getData().getTotal());
            assertEquals(1, response.getBody().getData().getList().size());
            
            verify(menuApplicationService, times(1)).findMenus(any(PageRequest.class), 
                    eq(tenantId), any(), any(), any(), any());
        }
    }

    @Test
    @DisplayName("获取菜单列表 - 缺少租户ID")
    void testGetMenus_MissingTenantId_ReturnsUnauthorized() {
        // When
        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(null);
            
            ResponseEntity<ApiResponse<PageResult<MenuDto.MenuView>>> response = 
                    menuController.getMenus(1, 20, null, null, null, null, null, null, null);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(401, response.getBody().getCode());
            assertEquals("未提供有效的租户信息", response.getBody().getMessage());
            
            verify(menuApplicationService, never()).findMenus(any(), any(), any(), any(), any(), any());
        }
    }

    @Test
    @DisplayName("获取菜单列表 - 兼容page/limit参数")
    void testGetMenus_WithPageLimitParams() {
        // Given
        PageResult<MenuDto.MenuView> pageResult = PageResult.of(1L, new PageRequest(2, 10), 
                List.of(testMenuView));

        when(menuApplicationService.findMenus(any(PageRequest.class), eq(tenantId), 
                any(), any(), any(), any())).thenReturn(pageResult);

        // When
        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(tenantId);
            
            ResponseEntity<ApiResponse<PageResult<MenuDto.MenuView>>> response = 
                    menuController.getMenus(null, null, 2, 10, null, null, null, null, null);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(menuApplicationService, times(1)).findMenus(any(PageRequest.class), 
                    eq(tenantId), any(), any(), any(), any());
        }
    }

    @Test
    @DisplayName("获取菜单列表 - 查询根菜单（parentId=null）")
    void testGetMenus_WithNullParentId() {
        // Given
        PageResult<MenuDto.MenuView> pageResult = PageResult.of(1L, new PageRequest(1, 20), 
                List.of(testMenuView));

        when(menuApplicationService.findMenus(any(PageRequest.class), eq(tenantId), 
                eq("null"), any(), any(), any())).thenReturn(pageResult);

        // When
        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(tenantId);
            
            ResponseEntity<ApiResponse<PageResult<MenuDto.MenuView>>> response = 
                    menuController.getMenus(1, 20, null, null, "null", null, null, null, null);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(menuApplicationService, times(1)).findMenus(any(PageRequest.class), 
                    eq(tenantId), eq("null"), any(), any(), any());
        }
    }

    @Test
    @DisplayName("获取菜单详情 - 成功")
    void testGetMenuById_Success() {
        // Given
        when(menuApplicationService.findMenuById(menuId, tenantId))
                .thenReturn(Optional.of(testMenuView));

        // When
        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(tenantId);
            
            ResponseEntity<ApiResponse<RowData<MenuDto.MenuView>>> response = 
                    menuController.getMenuById(menuId);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(200, response.getBody().getCode());
            assertNotNull(response.getBody().getData());
            assertNotNull(response.getBody().getData().getRow());
            assertEquals(menuId, response.getBody().getData().getRow().getId());
            
            verify(menuApplicationService, times(1)).findMenuById(menuId, tenantId);
        }
    }

    @Test
    @DisplayName("获取菜单详情 - 菜单不存在")
    void testGetMenuById_NotFound() {
        // Given
        when(menuApplicationService.findMenuById(menuId, tenantId))
                .thenReturn(Optional.empty());

        // When
        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(tenantId);
            
            ResponseEntity<ApiResponse<RowData<MenuDto.MenuView>>> response = 
                    menuController.getMenuById(menuId);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(404, response.getBody().getCode());
            assertEquals("菜单不存在或不属于当前租户", response.getBody().getMessage());
            
            verify(menuApplicationService, times(1)).findMenuById(menuId, tenantId);
        }
    }

    @Test
    @DisplayName("获取菜单的子菜单 - 成功")
    void testGetMenuChildren_Success() {
        // Given
        MenuDto.MenuView childMenu = MenuDto.MenuView.builder()
                .id("child-menu")
                .tenantId(tenantId)
                .parentId(menuId)
                .name("子菜单")
                .title("子菜单标题")
                .path("/child")
                .type("menu")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(menuApplicationService.findMenuChildren(menuId, tenantId))
                .thenReturn(List.of(childMenu));

        // When
        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(tenantId);
            
            ResponseEntity<ApiResponse<List<MenuDto.MenuView>>> response = 
                    menuController.getMenuChildren(menuId);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(200, response.getBody().getCode());
            assertNotNull(response.getBody().getData());
            assertEquals(1, response.getBody().getData().size());
            assertEquals("子菜单", response.getBody().getData().get(0).getName());
            
            verify(menuApplicationService, times(1)).findMenuChildren(menuId, tenantId);
        }
    }

    @Test
    @DisplayName("创建菜单 - 成功")
    void testCreateMenu_Success() {
        // Given
        MenuDto.CreateMenuCommand command = MenuDto.CreateMenuCommand.builder()
                .name("新菜单")
                .title("新菜单标题")
                .path("/new-menu")
                .icon("icon-menu")
                .type("menu")
                .build();

        when(menuApplicationService.createMenu(any(MenuDto.CreateMenuCommand.class)))
                .thenReturn(testMenuView);

        // When
        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(tenantId);
            
            ResponseEntity<ApiResponse<MenuDto.MenuView>> response = 
                    menuController.createMenu(command);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(200, response.getBody().getCode());
            assertEquals("菜单创建成功", response.getBody().getMessage());
            assertNotNull(response.getBody().getData());
            assertEquals(tenantId, command.getTenantId()); // 验证tenantId被设置
            
            verify(menuApplicationService, times(1)).createMenu(any(MenuDto.CreateMenuCommand.class));
        }
    }

    @Test
    @DisplayName("创建菜单 - 缺少租户ID")
    void testCreateMenu_MissingTenantId_ReturnsUnauthorized() {
        // Given
        MenuDto.CreateMenuCommand command = MenuDto.CreateMenuCommand.builder()
                .name("新菜单")
                .title("新菜单标题")
                .path("/new-menu")
                .build();

        // When
        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(null);
            
            ResponseEntity<ApiResponse<MenuDto.MenuView>> response = 
                    menuController.createMenu(command);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(401, response.getBody().getCode());
            
            verify(menuApplicationService, never()).createMenu(any());
        }
    }

    @Test
    @DisplayName("创建菜单 - 业务异常")
    void testCreateMenu_BusinessException_ReturnsBadRequest() {
        // Given
        MenuDto.CreateMenuCommand command = MenuDto.CreateMenuCommand.builder()
                .name("新菜单")
                .title("新菜单标题")
                .path("/new-menu")
                .build();

        when(menuApplicationService.createMenu(any(MenuDto.CreateMenuCommand.class)))
                .thenThrow(new IllegalArgumentException("菜单名称已存在"));

        // When
        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(tenantId);
            
            ResponseEntity<ApiResponse<MenuDto.MenuView>> response = 
                    menuController.createMenu(command);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(400, response.getBody().getCode());
            assertTrue(response.getBody().getMessage().contains("菜单名称已存在"));
        }
    }

    @Test
    @DisplayName("更新菜单 - 成功")
    void testUpdateMenu_Success() {
        // Given
        MenuDto.UpdateMenuCommand command = MenuDto.UpdateMenuCommand.builder()
                .name("更新后的菜单")
                .title("更新后的标题")
                .path("/updated")
                .build();

        MenuDto.MenuView updatedMenu = MenuDto.MenuView.builder()
                .id(menuId)
                .tenantId(tenantId)
                .name("更新后的菜单")
                .title("更新后的标题")
                .path("/updated")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(menuApplicationService.updateMenu(menuId, tenantId, command))
                .thenReturn(updatedMenu);

        // When
        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(tenantId);
            
            ResponseEntity<ApiResponse<MenuDto.MenuView>> response = 
                    menuController.updateMenu(menuId, command);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(200, response.getBody().getCode());
            assertEquals("菜单更新成功", response.getBody().getMessage());
            assertEquals("更新后的菜单", response.getBody().getData().getName());
            
            verify(menuApplicationService, times(1)).updateMenu(menuId, tenantId, command);
        }
    }

    @Test
    @DisplayName("更新菜单 - 缺少租户ID")
    void testUpdateMenu_MissingTenantId_ReturnsUnauthorized() {
        // Given
        MenuDto.UpdateMenuCommand command = MenuDto.UpdateMenuCommand.builder()
                .name("更新后的菜单")
                .title("更新后的标题")
                .path("/updated")
                .build();

        // When
        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(null);
            
            ResponseEntity<ApiResponse<MenuDto.MenuView>> response = 
                    menuController.updateMenu(menuId, command);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            verify(menuApplicationService, never()).updateMenu(anyString(), anyString(), any());
        }
    }

    @Test
    @DisplayName("删除菜单 - 成功")
    void testDeleteMenu_Success() {
        // Given
        doNothing().when(menuApplicationService).deleteMenu(menuId, tenantId);

        // When
        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(tenantId);
            
            ResponseEntity<ApiResponse<Void>> response = 
                    menuController.deleteMenu(menuId);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(200, response.getBody().getCode());
            assertEquals("菜单删除成功", response.getBody().getMessage());
            
            verify(menuApplicationService, times(1)).deleteMenu(menuId, tenantId);
        }
    }

    @Test
    @DisplayName("删除菜单 - 缺少租户ID")
    void testDeleteMenu_MissingTenantId_ReturnsUnauthorized() {
        // When
        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(null);
            
            ResponseEntity<ApiResponse<Void>> response = 
                    menuController.deleteMenu(menuId);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            verify(menuApplicationService, never()).deleteMenu(anyString(), anyString());
        }
    }

    @Test
    @DisplayName("删除菜单 - 业务异常")
    void testDeleteMenu_BusinessException_ReturnsBadRequest() {
        // Given
        doThrow(new IllegalArgumentException("菜单存在子菜单，无法删除"))
                .when(menuApplicationService).deleteMenu(menuId, tenantId);

        // When
        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(tenantId);
            
            ResponseEntity<ApiResponse<Void>> response = 
                    menuController.deleteMenu(menuId);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(400, response.getBody().getCode());
            assertTrue(response.getBody().getMessage().contains("菜单存在子菜单"));
        }
    }
}

