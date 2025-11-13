package com.aixone.directory.permission.domain.service;

import com.aixone.directory.permission.domain.aggregate.Permission;
import com.aixone.directory.permission.domain.repository.PermissionRepository;
import com.aixone.directory.permission.domain.repository.RolePermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * RBAC权限决策引擎单元测试
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RBAC权限决策引擎测试")
class RbacDecisionEngineTest {

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private RbacDecisionEngine rbacDecisionEngine;

    private String tenantId;
    private String userId;
    private String roleId1;
    private String roleId2;
    private String permissionId;
    private Permission testPermission;

    @BeforeEach
    void setUp() {
        tenantId = "tenant-" + UUID.randomUUID().toString();
        userId = "user-" + UUID.randomUUID().toString();
        roleId1 = "role-1-" + UUID.randomUUID().toString();
        roleId2 = "role-2-" + UUID.randomUUID().toString();
        permissionId = "permission-" + UUID.randomUUID().toString();

        testPermission = Permission.builder()
                .permissionId(permissionId)
                .tenantId(tenantId)
                .name("测试权限")
                .code("test:read")
                .resource("test")
                .action("read")
                .type(Permission.PermissionType.FUNCTIONAL)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("检查权限 - 用户有权限（通过角色）")
    void testCheckPermission_UserHasPermission_Success() {
        // Given
        List<String> roleIds = List.of(roleId1, roleId2);
        String resource = "test";
        String action = "read";

        when(permissionRepository.findByTenantIdAndResourceAndAction(tenantId, resource, action))
                .thenReturn(Optional.of(testPermission));
        when(rolePermissionRepository.hasPermission(roleId1, permissionId)).thenReturn(false);
        when(rolePermissionRepository.hasPermission(roleId2, permissionId)).thenReturn(true);

        // When
        boolean result = rbacDecisionEngine.checkPermission(userId, tenantId, roleIds, resource, action);

        // Then
        assertTrue(result);
        verify(permissionRepository, times(1)).findByTenantIdAndResourceAndAction(tenantId, resource, action);
        verify(rolePermissionRepository, times(1)).hasPermission(roleId1, permissionId);
        verify(rolePermissionRepository, times(1)).hasPermission(roleId2, permissionId);
    }

    @Test
    @DisplayName("检查权限 - 用户没有权限")
    void testCheckPermission_UserNoPermission_Failure() {
        // Given
        List<String> roleIds = List.of(roleId1, roleId2);
        String resource = "test";
        String action = "read";

        when(permissionRepository.findByTenantIdAndResourceAndAction(tenantId, resource, action))
                .thenReturn(Optional.of(testPermission));
        when(rolePermissionRepository.hasPermission(roleId1, permissionId)).thenReturn(false);
        when(rolePermissionRepository.hasPermission(roleId2, permissionId)).thenReturn(false);

        // When
        boolean result = rbacDecisionEngine.checkPermission(userId, tenantId, roleIds, resource, action);

        // Then
        assertFalse(result);
        verify(permissionRepository, times(1)).findByTenantIdAndResourceAndAction(tenantId, resource, action);
        verify(rolePermissionRepository, times(1)).hasPermission(roleId1, permissionId);
        verify(rolePermissionRepository, times(1)).hasPermission(roleId2, permissionId);
    }

    @Test
    @DisplayName("检查权限 - 用户没有角色")
    void testCheckPermission_UserNoRoles_Failure() {
        // Given
        List<String> roleIds = List.of();
        String resource = "test";
        String action = "read";

        // When
        boolean result = rbacDecisionEngine.checkPermission(userId, tenantId, roleIds, resource, action);

        // Then
        assertFalse(result);
        verify(permissionRepository, never()).findByTenantIdAndResourceAndAction(anyString(), anyString(), anyString());
        verify(rolePermissionRepository, never()).hasPermission(anyString(), anyString());
    }

    @Test
    @DisplayName("检查权限 - 角色列表为null")
    void testCheckPermission_NullRoleIds_Failure() {
        // Given
        List<String> roleIds = null;
        String resource = "test";
        String action = "read";

        // When
        boolean result = rbacDecisionEngine.checkPermission(userId, tenantId, roleIds, resource, action);

        // Then
        assertFalse(result);
        verify(permissionRepository, never()).findByTenantIdAndResourceAndAction(anyString(), anyString(), anyString());
        verify(rolePermissionRepository, never()).hasPermission(anyString(), anyString());
    }

    @Test
    @DisplayName("检查权限 - 权限不存在")
    void testCheckPermission_PermissionNotFound_Failure() {
        // Given
        List<String> roleIds = List.of(roleId1);
        String resource = "test";
        String action = "read";

        when(permissionRepository.findByTenantIdAndResourceAndAction(tenantId, resource, action))
                .thenReturn(Optional.empty());

        // When
        boolean result = rbacDecisionEngine.checkPermission(userId, tenantId, roleIds, resource, action);

        // Then
        assertFalse(result);
        verify(permissionRepository, times(1)).findByTenantIdAndResourceAndAction(tenantId, resource, action);
        verify(rolePermissionRepository, never()).hasPermission(anyString(), anyString());
    }

    @Test
    @DisplayName("通过权限标识检查权限 - 成功")
    void testCheckPermissionByIdentifier_Success() {
        // Given
        List<String> roleIds = List.of(roleId1);
        String permissionIdentifier = "test:read";

        when(permissionRepository.findByTenantIdAndResourceAndAction(tenantId, "test", "read"))
                .thenReturn(Optional.of(testPermission));
        when(rolePermissionRepository.hasPermission(roleId1, permissionId)).thenReturn(true);

        // When
        boolean result = rbacDecisionEngine.checkPermissionByIdentifier(userId, tenantId, roleIds, permissionIdentifier);

        // Then
        assertTrue(result);
        verify(permissionRepository, times(1)).findByTenantIdAndResourceAndAction(tenantId, "test", "read");
    }

    @Test
    @DisplayName("通过权限标识检查权限 - 无效格式")
    void testCheckPermissionByIdentifier_InvalidFormat_Failure() {
        // Given
        List<String> roleIds = List.of(roleId1);
        String permissionIdentifier = "invalid-format";

        // When
        boolean result = rbacDecisionEngine.checkPermissionByIdentifier(userId, tenantId, roleIds, permissionIdentifier);

        // Then
        assertFalse(result);
        verify(permissionRepository, never()).findByTenantIdAndResourceAndAction(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("通过权限标识检查权限 - null值")
    void testCheckPermissionByIdentifier_NullIdentifier_Failure() {
        // Given
        List<String> roleIds = List.of(roleId1);
        String permissionIdentifier = null;

        // When
        boolean result = rbacDecisionEngine.checkPermissionByIdentifier(userId, tenantId, roleIds, permissionIdentifier);

        // Then
        assertFalse(result);
        verify(permissionRepository, never()).findByTenantIdAndResourceAndAction(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("获取用户权限列表 - 成功")
    void testGetUserPermissions_Success() {
        // Given
        List<String> roleIds = List.of(roleId1, roleId2);
        String permissionId2 = "permission-2-" + UUID.randomUUID().toString();
        
        Permission permission2 = Permission.builder()
                .permissionId(permissionId2)
                .tenantId(tenantId)
                .name("测试权限2")
                .code("test:write")
                .resource("test")
                .action("write")
                .type(Permission.PermissionType.FUNCTIONAL)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(rolePermissionRepository.findPermissionIdsByRoleId(roleId1, tenantId))
                .thenReturn(List.of(permissionId));
        when(rolePermissionRepository.findPermissionIdsByRoleId(roleId2, tenantId))
                .thenReturn(List.of(permissionId, permissionId2));
        when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(testPermission));
        when(permissionRepository.findById(permissionId2)).thenReturn(Optional.of(permission2));

        // When
        Set<String> result = rbacDecisionEngine.getUserPermissions(userId, tenantId, roleIds);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("test:read"));
        assertTrue(result.contains("test:write"));
        verify(rolePermissionRepository, times(1)).findPermissionIdsByRoleId(roleId1, tenantId);
        verify(rolePermissionRepository, times(1)).findPermissionIdsByRoleId(roleId2, tenantId);
    }

    @Test
    @DisplayName("获取用户权限列表 - 用户没有角色")
    void testGetUserPermissions_NoRoles_ReturnsEmpty() {
        // Given
        List<String> roleIds = List.of();

        // When
        Set<String> result = rbacDecisionEngine.getUserPermissions(userId, tenantId, roleIds);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(rolePermissionRepository, never()).findPermissionIdsByRoleId(anyString(), anyString());
    }

    @Test
    @DisplayName("获取用户权限列表 - 角色列表为null")
    void testGetUserPermissions_NullRoleIds_ReturnsEmpty() {
        // Given
        List<String> roleIds = null;

        // When
        Set<String> result = rbacDecisionEngine.getUserPermissions(userId, tenantId, roleIds);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(rolePermissionRepository, never()).findPermissionIdsByRoleId(anyString(), anyString());
    }

    @Test
    @DisplayName("批量检查权限 - 成功")
    void testCheckPermissions_Success() {
        // Given
        List<String> roleIds = List.of(roleId1);
        List<String> permissions = List.of("test:read", "test:write");
        
        String permissionId2 = "permission-2-" + UUID.randomUUID().toString();
        Permission permission2 = Permission.builder()
                .permissionId(permissionId2)
                .tenantId(tenantId)
                .name("测试权限2")
                .code("test:write")
                .resource("test")
                .action("write")
                .type(Permission.PermissionType.FUNCTIONAL)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(permissionRepository.findByTenantIdAndResourceAndAction(tenantId, "test", "read"))
                .thenReturn(Optional.of(testPermission));
        when(permissionRepository.findByTenantIdAndResourceAndAction(tenantId, "test", "write"))
                .thenReturn(Optional.of(permission2));
        when(rolePermissionRepository.hasPermission(roleId1, permissionId)).thenReturn(true);
        when(rolePermissionRepository.hasPermission(roleId1, permissionId2)).thenReturn(false);

        // When
        java.util.Map<String, Boolean> result = rbacDecisionEngine.checkPermissions(userId, tenantId, roleIds, permissions);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get("test:read"));
        assertFalse(result.get("test:write"));
    }
}

