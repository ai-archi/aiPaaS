package com.aixone.directory.menu.interfaces.rest;

import com.aixone.common.api.ApiResponse;
import com.aixone.common.api.PageRequest;
import com.aixone.common.api.PageResult;
import com.aixone.directory.menu.application.MenuApplicationService;
import com.aixone.directory.menu.application.MenuDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 菜单管理员控制器单元测试
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("菜单管理员控制器测试")
class MenuAdminControllerTest {

    @Mock
    private MenuApplicationService menuApplicationService;

    @InjectMocks
    private MenuAdminController menuAdminController;

    private String tenantId;
    private String menuId;
    private MenuDto.MenuView testMenuView;

    @BeforeEach
    void setUp() {
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
    @DisplayName("管理员查询菜单列表 - 成功")
    void testGetMenus_Success() {
        // Given
        PageResult<MenuDto.MenuView> pageResult = PageResult.of(2L, new PageRequest(1, 20), 
                List.of(testMenuView));

        when(menuApplicationService.findMenus(any(PageRequest.class), eq(tenantId), 
                any(), any(), any(), any())).thenReturn(pageResult);

        // When
        ResponseEntity<ApiResponse<PageResult<MenuDto.MenuView>>> response = 
                menuAdminController.getMenus(tenantId, null, 1, 20, null, null, null);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getCode());
        assertNotNull(response.getBody().getData());
        assertEquals(2L, response.getBody().getData().getTotal());
        
        verify(menuApplicationService, times(1)).findMenus(any(PageRequest.class), 
                eq(tenantId), any(), any(), any(), any());
    }

    @Test
    @DisplayName("管理员查询菜单列表 - tenantId为空")
    void testGetMenus_EmptyTenantId_ReturnsBadRequest() {
        // When
        ResponseEntity<ApiResponse<PageResult<MenuDto.MenuView>>> response = 
                menuAdminController.getMenus(null, null, 1, 20, null, null, null);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getCode());
        assertEquals("tenantId参数不能为空", response.getBody().getMessage());
        
        verify(menuApplicationService, never()).findMenus(any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("管理员查询菜单详情 - 成功")
    void testGetMenuById_Success() {
        // Given
        when(menuApplicationService.findMenuById(menuId)).thenReturn(Optional.of(testMenuView));

        // When
        ResponseEntity<ApiResponse<MenuDto.MenuView>> response = 
                menuAdminController.getMenuById(menuId, tenantId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getCode());
        assertNotNull(response.getBody().getData());
        assertEquals(menuId, response.getBody().getData().getId());
        
        verify(menuApplicationService, times(1)).findMenuById(menuId);
    }

    @Test
    @DisplayName("管理员查询菜单详情 - 菜单不存在")
    void testGetMenuById_NotFound() {
        // Given
        when(menuApplicationService.findMenuById(menuId)).thenReturn(Optional.empty());

        // When
        ResponseEntity<ApiResponse<MenuDto.MenuView>> response = 
                menuAdminController.getMenuById(menuId, tenantId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getCode());
        assertEquals("菜单不存在", response.getBody().getMessage());
    }

    @Test
    @DisplayName("管理员查询菜单详情 - tenantId不匹配")
    void testGetMenuById_TenantIdMismatch_ReturnsNotFound() {
        // Given
        String otherTenantId = "other-tenant-" + UUID.randomUUID().toString();
        when(menuApplicationService.findMenuById(menuId)).thenReturn(Optional.of(testMenuView));

        // When
        ResponseEntity<ApiResponse<MenuDto.MenuView>> response = 
                menuAdminController.getMenuById(menuId, otherTenantId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getCode());
        assertEquals("菜单不存在或不属于指定租户", response.getBody().getMessage());
    }

    @Test
    @DisplayName("管理员创建菜单 - 成功（tenantId从查询参数）")
    void testCreateMenu_WithQueryParamTenantId_Success() {
        // Given
        MenuDto.CreateMenuCommand command = MenuDto.CreateMenuCommand.builder()
                .name("新菜单")
                .title("新菜单标题")
                .path("/new-menu")
                .build();

        when(menuApplicationService.createMenu(any(MenuDto.CreateMenuCommand.class)))
                .thenReturn(testMenuView);

        // When
        ResponseEntity<ApiResponse<MenuDto.MenuView>> response = 
                menuAdminController.createMenu(tenantId, command);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getCode());
        assertEquals("菜单创建成功", response.getBody().getMessage());
        assertEquals(tenantId, command.getTenantId()); // 验证tenantId被设置
        
        verify(menuApplicationService, times(1)).createMenu(any(MenuDto.CreateMenuCommand.class));
    }

    @Test
    @DisplayName("管理员创建菜单 - 成功（tenantId从请求体）")
    void testCreateMenu_WithRequestBodyTenantId_Success() {
        // Given
        MenuDto.CreateMenuCommand command = MenuDto.CreateMenuCommand.builder()
                .tenantId(tenantId)
                .name("新菜单")
                .title("新菜单标题")
                .path("/new-menu")
                .build();

        when(menuApplicationService.createMenu(any(MenuDto.CreateMenuCommand.class)))
                .thenReturn(testMenuView);

        // When
        ResponseEntity<ApiResponse<MenuDto.MenuView>> response = 
                menuAdminController.createMenu(null, command);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(menuApplicationService, times(1)).createMenu(any(MenuDto.CreateMenuCommand.class));
    }

    @Test
    @DisplayName("管理员创建菜单 - tenantId为空")
    void testCreateMenu_EmptyTenantId_ReturnsBadRequest() {
        // Given
        MenuDto.CreateMenuCommand command = MenuDto.CreateMenuCommand.builder()
                .name("新菜单")
                .title("新菜单标题")
                .path("/new-menu")
                .build();

        // When
        ResponseEntity<ApiResponse<MenuDto.MenuView>> response = 
                menuAdminController.createMenu(null, command);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getCode());
        assertEquals("tenantId不能为空", response.getBody().getMessage());
        
        verify(menuApplicationService, never()).createMenu(any());
    }

    @Test
    @DisplayName("管理员更新菜单 - 成功")
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

        when(menuApplicationService.updateMenu(menuId, command)).thenReturn(updatedMenu);

        // When
        ResponseEntity<ApiResponse<MenuDto.MenuView>> response = 
                menuAdminController.updateMenu(menuId, tenantId, command);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getCode());
        assertEquals("菜单更新成功", response.getBody().getMessage());
        assertEquals("更新后的菜单", response.getBody().getData().getName());
        
        verify(menuApplicationService, times(1)).updateMenu(menuId, command);
    }

    @Test
    @DisplayName("管理员更新菜单 - 业务异常")
    void testUpdateMenu_BusinessException_ReturnsBadRequest() {
        // Given
        MenuDto.UpdateMenuCommand command = MenuDto.UpdateMenuCommand.builder()
                .name("更新后的菜单")
                .title("更新后的标题")
                .path("/updated")
                .build();

        when(menuApplicationService.updateMenu(menuId, command))
                .thenThrow(new IllegalArgumentException("菜单不存在"));

        // When
        ResponseEntity<ApiResponse<MenuDto.MenuView>> response = 
                menuAdminController.updateMenu(menuId, tenantId, command);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getCode());
        assertTrue(response.getBody().getMessage().contains("菜单不存在"));
    }

    @Test
    @DisplayName("管理员删除菜单 - 成功")
    void testDeleteMenu_Success() {
        // Given
        doNothing().when(menuApplicationService).deleteMenu(menuId);

        // When
        ResponseEntity<ApiResponse<Void>> response = 
                menuAdminController.deleteMenu(menuId, tenantId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getCode());
        assertEquals("菜单删除成功", response.getBody().getMessage());
        
        verify(menuApplicationService, times(1)).deleteMenu(menuId);
    }

    @Test
    @DisplayName("管理员删除菜单 - 业务异常")
    void testDeleteMenu_BusinessException_ReturnsBadRequest() {
        // Given
        doThrow(new IllegalArgumentException("菜单存在子菜单，无法删除"))
                .when(menuApplicationService).deleteMenu(menuId);

        // When
        ResponseEntity<ApiResponse<Void>> response = 
                menuAdminController.deleteMenu(menuId, tenantId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getCode());
        assertTrue(response.getBody().getMessage().contains("菜单存在子菜单"));
    }
}

