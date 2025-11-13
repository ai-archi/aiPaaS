package com.aixone.directory.permission.domain.service;

import com.aixone.directory.permission.domain.aggregate.Permission;
import com.aixone.directory.permission.domain.repository.PermissionRepository;
import com.aixone.directory.permission.infrastructure.provider.UserPermissionProviderImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 权限决策服务单元测试
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("权限决策服务测试")
class PermissionDecisionServiceTest {

    @Mock
    private RbacDecisionEngine rbacDecisionEngine;

    @Mock
    private AbacDecisionEngine abacDecisionEngine;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private UserPermissionProviderImpl userPermissionProvider;

    @InjectMocks
    private PermissionDecisionService permissionDecisionService;

    private String tenantId;
    private String userId;
    private String roleId1;
    private String permissionId;
    private Permission testPermission;
    private Permission permissionWithAbac;

    @BeforeEach
    void setUp() {
        tenantId = "tenant-" + UUID.randomUUID().toString();
        userId = "user-" + UUID.randomUUID().toString();
        roleId1 = "role-1-" + UUID.randomUUID().toString();
        permissionId = "permission-" + UUID.randomUUID().toString();

        testPermission = Permission.builder()
                .permissionId(permissionId)
                .tenantId(tenantId)
                .name("测试权限")
                .code("test:read")
                .resource("test")
                .action("read")
                .type(Permission.PermissionType.FUNCTIONAL)
                .abacConditions(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Map<String, Object> abacConditions = new HashMap<>();
        abacConditions.put("user.department", "IT");
        permissionWithAbac = Permission.builder()
                .permissionId("permission-abac-" + UUID.randomUUID().toString())
                .tenantId(tenantId)
                .name("有ABAC条件的权限")
                .code("test:write")
                .resource("test")
                .action("write")
                .type(Permission.PermissionType.FUNCTIONAL)
                .abacConditions(abacConditions)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("检查权限 - RBAC通过，无ABAC条件")
    void testCheckPermission_RbacPass_NoAbac_Success() {
        // Given
        String resource = "test";
        String action = "read";
        List<String> roleIds = List.of(roleId1);

        when(userPermissionProvider.getUserRoles(userId, tenantId)).thenReturn(roleIds);
        when(permissionRepository.findByTenantIdAndResourceAndAction(tenantId, resource, action))
                .thenReturn(Optional.of(testPermission));
        when(rbacDecisionEngine.checkPermission(userId, tenantId, roleIds, resource, action))
                .thenReturn(true);

        // When
        boolean result = permissionDecisionService.checkPermission(
                userId, tenantId, resource, action, null);

        // Then
        assertTrue(result);
        verify(userPermissionProvider, times(1)).getUserRoles(userId, tenantId);
        verify(permissionRepository, times(1)).findByTenantIdAndResourceAndAction(tenantId, resource, action);
        verify(rbacDecisionEngine, times(1)).checkPermission(userId, tenantId, roleIds, resource, action);
        verify(abacDecisionEngine, never()).checkPermission(anyString(), anyString(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("检查权限 - RBAC通过，ABAC通过")
    void testCheckPermission_RbacPass_AbacPass_Success() {
        // Given
        String resource = "test";
        String action = "write";
        List<String> roleIds = List.of(roleId1);
        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("department", "IT");
        PermissionDecisionService.PermissionContext context = PermissionDecisionService.PermissionContext.builder()
                .userAttributes(userAttributes)
                .build();

        when(userPermissionProvider.getUserRoles(userId, tenantId)).thenReturn(roleIds);
        when(permissionRepository.findByTenantIdAndResourceAndAction(tenantId, resource, action))
                .thenReturn(Optional.of(permissionWithAbac));
        when(rbacDecisionEngine.checkPermission(userId, tenantId, roleIds, resource, action))
                .thenReturn(true);
        when(abacDecisionEngine.checkPermission(
                eq(userId), eq(tenantId), eq(permissionWithAbac), 
                eq(userAttributes), isNull(), isNull()))
                .thenReturn(true);

        // When
        boolean result = permissionDecisionService.checkPermission(
                userId, tenantId, resource, action, context);

        // Then
        assertTrue(result);
        verify(rbacDecisionEngine, times(1)).checkPermission(userId, tenantId, roleIds, resource, action);
        verify(abacDecisionEngine, times(1)).checkPermission(
                eq(userId), eq(tenantId), eq(permissionWithAbac), 
                eq(userAttributes), isNull(), isNull());
    }

    @Test
    @DisplayName("检查权限 - RBAC通过，ABAC不通过")
    void testCheckPermission_RbacPass_AbacFail_Failure() {
        // Given
        String resource = "test";
        String action = "write";
        List<String> roleIds = List.of(roleId1);
        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("department", "HR"); // 不满足ABAC条件
        PermissionDecisionService.PermissionContext context = PermissionDecisionService.PermissionContext.builder()
                .userAttributes(userAttributes)
                .build();

        when(userPermissionProvider.getUserRoles(userId, tenantId)).thenReturn(roleIds);
        when(permissionRepository.findByTenantIdAndResourceAndAction(tenantId, resource, action))
                .thenReturn(Optional.of(permissionWithAbac));
        when(rbacDecisionEngine.checkPermission(userId, tenantId, roleIds, resource, action))
                .thenReturn(true);
        when(abacDecisionEngine.checkPermission(
                eq(userId), eq(tenantId), eq(permissionWithAbac), 
                eq(userAttributes), isNull(), isNull()))
                .thenReturn(false);

        // When
        boolean result = permissionDecisionService.checkPermission(
                userId, tenantId, resource, action, context);

        // Then
        assertFalse(result);
        verify(rbacDecisionEngine, times(1)).checkPermission(userId, tenantId, roleIds, resource, action);
        verify(abacDecisionEngine, times(1)).checkPermission(
                eq(userId), eq(tenantId), eq(permissionWithAbac), 
                eq(userAttributes), isNull(), isNull());
    }

    @Test
    @DisplayName("检查权限 - RBAC不通过")
    void testCheckPermission_RbacFail_Failure() {
        // Given
        String resource = "test";
        String action = "read";
        List<String> roleIds = List.of(roleId1);

        when(userPermissionProvider.getUserRoles(userId, tenantId)).thenReturn(roleIds);
        when(permissionRepository.findByTenantIdAndResourceAndAction(tenantId, resource, action))
                .thenReturn(Optional.of(testPermission));
        when(rbacDecisionEngine.checkPermission(userId, tenantId, roleIds, resource, action))
                .thenReturn(false);

        // When
        boolean result = permissionDecisionService.checkPermission(
                userId, tenantId, resource, action, null);

        // Then
        assertFalse(result);
        verify(rbacDecisionEngine, times(1)).checkPermission(userId, tenantId, roleIds, resource, action);
        verify(abacDecisionEngine, never()).checkPermission(anyString(), anyString(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("检查权限 - 用户没有角色")
    void testCheckPermission_UserNoRoles_Failure() {
        // Given
        String resource = "test";
        String action = "read";

        when(userPermissionProvider.getUserRoles(userId, tenantId)).thenReturn(List.of());

        // When
        boolean result = permissionDecisionService.checkPermission(
                userId, tenantId, resource, action, null);

        // Then
        assertFalse(result);
        verify(userPermissionProvider, times(1)).getUserRoles(userId, tenantId);
        verify(permissionRepository, never()).findByTenantIdAndResourceAndAction(anyString(), anyString(), anyString());
        verify(rbacDecisionEngine, never()).checkPermission(anyString(), anyString(), any(), anyString(), anyString());
    }

    @Test
    @DisplayName("检查权限 - 权限不存在")
    void testCheckPermission_PermissionNotFound_Failure() {
        // Given
        String resource = "test";
        String action = "read";
        List<String> roleIds = List.of(roleId1);

        when(userPermissionProvider.getUserRoles(userId, tenantId)).thenReturn(roleIds);
        when(permissionRepository.findByTenantIdAndResourceAndAction(tenantId, resource, action))
                .thenReturn(Optional.empty());

        // When
        boolean result = permissionDecisionService.checkPermission(
                userId, tenantId, resource, action, null);

        // Then
        assertFalse(result);
        verify(userPermissionProvider, times(1)).getUserRoles(userId, tenantId);
        verify(permissionRepository, times(1)).findByTenantIdAndResourceAndAction(tenantId, resource, action);
        verify(rbacDecisionEngine, never()).checkPermission(anyString(), anyString(), any(), anyString(), anyString());
    }

    @Test
    @DisplayName("通过权限标识检查权限 - 成功")
    void testCheckPermissionByIdentifier_Success() {
        // Given
        String permissionIdentifier = "test:read";
        List<String> roleIds = List.of(roleId1);

        when(userPermissionProvider.getUserRoles(userId, tenantId)).thenReturn(roleIds);
        when(permissionRepository.findByTenantIdAndResourceAndAction(tenantId, "test", "read"))
                .thenReturn(Optional.of(testPermission));
        when(rbacDecisionEngine.checkPermission(userId, tenantId, roleIds, "test", "read"))
                .thenReturn(true);

        // When
        boolean result = permissionDecisionService.checkPermissionByIdentifier(
                userId, tenantId, permissionIdentifier, null);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("通过权限标识检查权限 - 无效格式")
    void testCheckPermissionByIdentifier_InvalidFormat_Failure() {
        // Given
        String permissionIdentifier = "invalid-format";

        // When
        boolean result = permissionDecisionService.checkPermissionByIdentifier(
                userId, tenantId, permissionIdentifier, null);

        // Then
        assertFalse(result);
        verify(userPermissionProvider, never()).getUserRoles(anyString(), anyString());
    }

    @Test
    @DisplayName("通过权限标识检查权限 - null值")
    void testCheckPermissionByIdentifier_NullIdentifier_Failure() {
        // Given
        String permissionIdentifier = null;

        // When
        boolean result = permissionDecisionService.checkPermissionByIdentifier(
                userId, tenantId, permissionIdentifier, null);

        // Then
        assertFalse(result);
        verify(userPermissionProvider, never()).getUserRoles(anyString(), anyString());
    }

    @Test
    @DisplayName("批量检查权限 - 成功")
    void testCheckPermissions_Success() {
        // Given
        List<String> permissions = List.of("test:read", "test:write");
        List<String> roleIds = List.of(roleId1);

        when(userPermissionProvider.getUserRoles(userId, tenantId)).thenReturn(roleIds);
        when(permissionRepository.findByTenantIdAndResourceAndAction(tenantId, "test", "read"))
                .thenReturn(Optional.of(testPermission));
        when(permissionRepository.findByTenantIdAndResourceAndAction(tenantId, "test", "write"))
                .thenReturn(Optional.of(permissionWithAbac));
        when(rbacDecisionEngine.checkPermission(userId, tenantId, roleIds, "test", "read"))
                .thenReturn(true);
        when(rbacDecisionEngine.checkPermission(userId, tenantId, roleIds, "test", "write"))
                .thenReturn(false);

        // When
        Map<String, Boolean> result = permissionDecisionService.checkPermissions(
                userId, tenantId, permissions, null);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get("test:read"));
        assertFalse(result.get("test:write"));
    }

    @Test
    @DisplayName("获取用户有效权限列表 - 成功")
    void testGetUserEffectivePermissions_Success() {
        // Given
        List<String> roleIds = List.of(roleId1);
        Set<String> permissions = Set.of("test:read", "test:write");

        when(userPermissionProvider.getUserRoles(userId, tenantId)).thenReturn(roleIds);
        when(rbacDecisionEngine.getUserPermissions(userId, tenantId, roleIds))
                .thenReturn(permissions);

        // When
        Set<String> result = permissionDecisionService.getUserEffectivePermissions(userId, tenantId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("test:read"));
        assertTrue(result.contains("test:write"));
        verify(userPermissionProvider, times(1)).getUserRoles(userId, tenantId);
        verify(rbacDecisionEngine, times(1)).getUserPermissions(userId, tenantId, roleIds);
    }

    @Test
    @DisplayName("检查权限 - 带完整上下文（用户、资源、环境属性）")
    void testCheckPermission_WithFullContext_Success() {
        // Given
        String resource = "test";
        String action = "write";
        List<String> roleIds = List.of(roleId1);
        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("department", "IT");
        Map<String, Object> resourceAttributes = new HashMap<>();
        resourceAttributes.put("category", "sensitive");
        Map<String, Object> environmentAttributes = new HashMap<>();
        environmentAttributes.put("time", "09:00");
        
        PermissionDecisionService.PermissionContext context = PermissionDecisionService.PermissionContext.builder()
                .userAttributes(userAttributes)
                .resourceAttributes(resourceAttributes)
                .environmentAttributes(environmentAttributes)
                .build();

        when(userPermissionProvider.getUserRoles(userId, tenantId)).thenReturn(roleIds);
        when(permissionRepository.findByTenantIdAndResourceAndAction(tenantId, resource, action))
                .thenReturn(Optional.of(permissionWithAbac));
        when(rbacDecisionEngine.checkPermission(userId, tenantId, roleIds, resource, action))
                .thenReturn(true);
        when(abacDecisionEngine.checkPermission(
                eq(userId), eq(tenantId), eq(permissionWithAbac), 
                eq(userAttributes), eq(resourceAttributes), eq(environmentAttributes)))
                .thenReturn(true);

        // When
        boolean result = permissionDecisionService.checkPermission(
                userId, tenantId, resource, action, context);

        // Then
        assertTrue(result);
        verify(abacDecisionEngine, times(1)).checkPermission(
                eq(userId), eq(tenantId), eq(permissionWithAbac), 
                eq(userAttributes), eq(resourceAttributes), eq(environmentAttributes));
    }
}

