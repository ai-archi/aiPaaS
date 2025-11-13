package com.aixone.directory.permission.interfaces.rest;

import com.aixone.common.api.ApiResponse;
import com.aixone.common.api.PageRequest;
import com.aixone.common.api.PageResult;
import com.aixone.common.session.SessionContext;
import com.aixone.directory.permission.application.PermissionApplicationService;
import com.aixone.directory.permission.application.PermissionDto;
import com.aixone.directory.permission.domain.aggregate.Permission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
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
 * 权限控制器单元测试
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("权限控制器测试")
class PermissionControllerTest {

    @Mock
    private PermissionApplicationService permissionApplicationService;

    @InjectMocks
    private PermissionController permissionController;

    private String tenantId;
    private String permissionId;
    private PermissionDto.PermissionView testPermissionView;

    @BeforeEach
    void setUp() {
        tenantId = "tenant-" + UUID.randomUUID().toString();
        permissionId = "permission-" + UUID.randomUUID().toString();

        testPermissionView = PermissionDto.PermissionView.builder()
                .permissionId(permissionId)
                .tenantId(tenantId)
                .name("测试权限")
                .code("test:read")
                .resource("test")
                .action("read")
                .type(Permission.PermissionType.FUNCTIONAL)
                .description("测试权限描述")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("获取权限列表 - 成功")
    void testGetPermissions_Success() {
        // Given
        PageRequest pageRequest = new PageRequest(1, 20);
        PageResult<PermissionDto.PermissionView> pageResult = PageResult.of(2L, pageRequest, List.of(testPermissionView));

        when(permissionApplicationService.findPermissions(any(PageRequest.class), eq(tenantId), any(), any()))
                .thenReturn(pageResult);

        // When
        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(tenantId);

            ResponseEntity<ApiResponse<PageResult<PermissionDto.PermissionView>>> response =
                    permissionController.getPermissions(1, 20, null, null, null, null, null);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(200, response.getBody().getCode());
            assertNotNull(response.getBody().getData());
            assertEquals(2L, response.getBody().getData().getTotal());
            assertEquals(1, response.getBody().getData().getList().size());
        }
    }

    @Test
    @DisplayName("获取权限列表 - 未提供租户信息")
    void testGetPermissions_NoTenantId_ReturnsUnauthorized() {
        // When
        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(null);

            ResponseEntity<ApiResponse<PageResult<PermissionDto.PermissionView>>> response =
                    permissionController.getPermissions(1, 20, null, null, null, null, null);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(401, response.getBody().getCode());
        }
    }

    @Test
    @DisplayName("获取权限详情 - 成功")
    void testGetPermissionById_Success() {
        // Given
        when(permissionApplicationService.findPermissionById(permissionId, tenantId))
                .thenReturn(Optional.of(testPermissionView));

        // When
        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(tenantId);

            ResponseEntity<ApiResponse<PermissionDto.PermissionView>> response =
                    permissionController.getPermissionById(permissionId);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(200, response.getBody().getCode());
            assertNotNull(response.getBody().getData());
            assertEquals(permissionId, response.getBody().getData().getPermissionId());
        }
    }

    @Test
    @DisplayName("获取权限详情 - 权限不存在")
    void testGetPermissionById_NotFound_ReturnsNotFound() {
        // Given
        when(permissionApplicationService.findPermissionById(permissionId, tenantId))
                .thenReturn(Optional.empty());

        // When
        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(tenantId);

            ResponseEntity<ApiResponse<PermissionDto.PermissionView>> response =
                    permissionController.getPermissionById(permissionId);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(404, response.getBody().getCode());
        }
    }

    @Test
    @DisplayName("创建权限 - 成功")
    void testCreatePermission_Success() {
        // Given
        PermissionDto.CreatePermissionCommand command = PermissionDto.CreatePermissionCommand.builder()
                .name("新权限")
                .code("new:read")
                .resource("new")
                .action("read")
                .type(Permission.PermissionType.FUNCTIONAL)
                .description("新权限描述")
                .build();

        when(permissionApplicationService.createPermission(any(PermissionDto.CreatePermissionCommand.class)))
                .thenReturn(testPermissionView);

        // When
        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(tenantId);

            ResponseEntity<ApiResponse<PermissionDto.PermissionView>> response =
                    permissionController.createPermission(command);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(200, response.getBody().getCode());
            assertNotNull(response.getBody().getData());
            verify(permissionApplicationService, times(1)).createPermission(any(PermissionDto.CreatePermissionCommand.class));
        }
    }

    @Test
    @DisplayName("创建权限 - 未提供租户信息")
    void testCreatePermission_NoTenantId_ReturnsUnauthorized() {
        // Given
        PermissionDto.CreatePermissionCommand command = PermissionDto.CreatePermissionCommand.builder()
                .name("新权限")
                .code("new:read")
                .resource("new")
                .action("read")
                .build();

        // When
        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(null);

            ResponseEntity<ApiResponse<PermissionDto.PermissionView>> response =
                    permissionController.createPermission(command);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(401, response.getBody().getCode());
            verify(permissionApplicationService, never()).createPermission(any());
        }
    }

    @Test
    @DisplayName("创建权限 - 业务异常")
    void testCreatePermission_BusinessException_ReturnsBadRequest() {
        // Given
        PermissionDto.CreatePermissionCommand command = PermissionDto.CreatePermissionCommand.builder()
                .name("新权限")
                .code("new:read")
                .resource("new")
                .action("read")
                .build();

        when(permissionApplicationService.createPermission(any(PermissionDto.CreatePermissionCommand.class)))
                .thenThrow(new IllegalArgumentException("权限编码已存在"));

        // When
        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(tenantId);

            ResponseEntity<ApiResponse<PermissionDto.PermissionView>> response =
                    permissionController.createPermission(command);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(400, response.getBody().getCode());
        }
    }

    @Test
    @DisplayName("更新权限 - 成功")
    void testUpdatePermission_Success() {
        // Given
        PermissionDto.UpdatePermissionCommand command = PermissionDto.UpdatePermissionCommand.builder()
                .name("更新后的权限")
                .code("updated:read")
                .resource("updated")
                .action("read")
                .build();

        PermissionDto.PermissionView updatedView = PermissionDto.PermissionView.builder()
                .permissionId(permissionId)
                .tenantId(tenantId)
                .name("更新后的权限")
                .code("updated:read")
                .resource("updated")
                .action("read")
                .build();

        when(permissionApplicationService.updatePermission(eq(permissionId), any(PermissionDto.UpdatePermissionCommand.class)))
                .thenReturn(updatedView);

        // When
        ResponseEntity<ApiResponse<PermissionDto.PermissionView>> response =
                permissionController.updatePermission(permissionId, command);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getCode());
        assertNotNull(response.getBody().getData());
        assertEquals("更新后的权限", response.getBody().getData().getName());
    }

    @Test
    @DisplayName("更新权限 - 业务异常")
    void testUpdatePermission_BusinessException_ReturnsBadRequest() {
        // Given
        PermissionDto.UpdatePermissionCommand command = PermissionDto.UpdatePermissionCommand.builder()
                .name("更新后的权限")
                .code("updated:read")
                .resource("updated")
                .action("read")
                .build();

        when(permissionApplicationService.updatePermission(eq(permissionId), any(PermissionDto.UpdatePermissionCommand.class)))
                .thenThrow(new IllegalArgumentException("权限不存在"));

        // When
        ResponseEntity<ApiResponse<PermissionDto.PermissionView>> response =
                permissionController.updatePermission(permissionId, command);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getCode());
    }

    @Test
    @DisplayName("删除权限 - 成功")
    void testDeletePermission_Success() {
        // Given
        doNothing().when(permissionApplicationService).deletePermission(permissionId);

        // When
        ResponseEntity<ApiResponse<Void>> response = permissionController.deletePermission(permissionId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getCode());
        verify(permissionApplicationService, times(1)).deletePermission(permissionId);
    }

    @Test
    @DisplayName("分配权限给角色 - 成功")
    void testAssignRolePermission_Success() {
        // Given
        String roleId = "role-" + UUID.randomUUID().toString();
        doNothing().when(permissionApplicationService).assignRolePermission(roleId, permissionId, tenantId);

        // When
        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(tenantId);

            ResponseEntity<ApiResponse<Void>> response =
                    permissionController.assignRolePermission(roleId, permissionId);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(200, response.getBody().getCode());
            verify(permissionApplicationService, times(1)).assignRolePermission(roleId, permissionId, tenantId);
        }
    }

    @Test
    @DisplayName("移除角色权限 - 成功")
    void testRemoveRolePermission_Success() {
        // Given
        String roleId = "role-" + UUID.randomUUID().toString();
        doNothing().when(permissionApplicationService).removeRolePermission(roleId, permissionId);

        // When
        ResponseEntity<ApiResponse<Void>> response =
                permissionController.removeRolePermission(roleId, permissionId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(200, response.getBody().getCode());
        verify(permissionApplicationService, times(1)).removeRolePermission(roleId, permissionId);
    }

    @Test
    @DisplayName("获取角色的权限列表 - 成功")
    void testGetRolePermissions_Success() {
        // Given
        String roleId = "role-" + UUID.randomUUID().toString();
        List<PermissionDto.PermissionView> permissions = List.of(testPermissionView);

        when(permissionApplicationService.getRolePermissions(roleId, tenantId)).thenReturn(permissions);

        // When
        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(tenantId);

            ResponseEntity<ApiResponse<List<PermissionDto.PermissionView>>> response =
                    permissionController.getRolePermissions(roleId);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(200, response.getBody().getCode());
            assertNotNull(response.getBody().getData());
            assertEquals(1, response.getBody().getData().size());
            verify(permissionApplicationService, times(1)).getRolePermissions(roleId, tenantId);
        }
    }
}

