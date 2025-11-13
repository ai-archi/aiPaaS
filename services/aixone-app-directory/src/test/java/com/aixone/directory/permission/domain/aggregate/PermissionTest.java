package com.aixone.directory.permission.domain.aggregate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 权限聚合根单元测试
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@DisplayName("权限聚合根测试")
class PermissionTest {

    private String tenantId;
    private String name;
    private String code;
    private String resource;
    private String action;

    @BeforeEach
    void setUp() {
        tenantId = "tenant-" + UUID.randomUUID().toString();
        name = "测试权限";
        code = "test:read";
        resource = "test";
        action = "read";
    }

    @Test
    @DisplayName("创建权限 - 成功")
    void testCreate_Success() {
        // When
        Permission permission = Permission.create(tenantId, name, code, resource, action);

        // Then
        assertNotNull(permission);
        assertEquals(tenantId, permission.getTenantId());
        assertEquals(name, permission.getName());
        assertEquals(code, permission.getCode());
        assertEquals(resource, permission.getResource());
        assertEquals(action, permission.getAction());
        assertEquals(Permission.PermissionType.FUNCTIONAL, permission.getType());
        assertNotNull(permission.getCreatedAt());
        assertNotNull(permission.getUpdatedAt());
    }

    @Test
    @DisplayName("创建权限 - 默认类型为FUNCTIONAL")
    void testCreate_DefaultTypeIsFunctional() {
        // When
        Permission permission = Permission.create(tenantId, name, code, resource, action);

        // Then
        assertEquals(Permission.PermissionType.FUNCTIONAL, permission.getType());
    }

    @Test
    @DisplayName("更新权限 - 成功")
    void testUpdate_Success() {
        // Given
        Permission permission = Permission.create(tenantId, name, code, resource, action);
        LocalDateTime originalCreatedAt = permission.getCreatedAt();
        
        String newName = "更新后的权限";
        String newCode = "test:write";
        String newResource = "test";
        String newAction = "write";
        Permission.PermissionType newType = Permission.PermissionType.DATA;
        String newDescription = "更新后的描述";
        Map<String, Object> newAbacConditions = new HashMap<>();
        newAbacConditions.put("user.department", "IT");

        // When
        permission.update(newName, newCode, newResource, newAction, newType, newDescription, newAbacConditions);

        // Then
        assertEquals(newName, permission.getName());
        assertEquals(newCode, permission.getCode());
        assertEquals(newResource, permission.getResource());
        assertEquals(newAction, permission.getAction());
        assertEquals(newType, permission.getType());
        assertEquals(newDescription, permission.getDescription());
        assertEquals(newAbacConditions, permission.getAbacConditions());
        assertEquals(originalCreatedAt, permission.getCreatedAt()); // 创建时间不变
        assertNotNull(permission.getUpdatedAt()); // 更新时间已更新
    }

    @Test
    @DisplayName("更新权限 - type为null时不更新")
    void testUpdate_TypeNull_NotUpdated() {
        // Given
        Permission permission = Permission.create(tenantId, name, code, resource, action);
        Permission.PermissionType originalType = permission.getType();

        // When
        permission.update(name, code, resource, action, null, null, null);

        // Then
        assertEquals(originalType, permission.getType());
    }

    @Test
    @DisplayName("检查是否属于指定租户 - 属于")
    void testBelongsToTenant_True() {
        // Given
        Permission permission = Permission.create(tenantId, name, code, resource, action);

        // When
        boolean result = permission.belongsToTenant(tenantId);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("检查是否属于指定租户 - 不属于")
    void testBelongsToTenant_False() {
        // Given
        Permission permission = Permission.create(tenantId, name, code, resource, action);
        String otherTenantId = "other-tenant-" + UUID.randomUUID().toString();

        // When
        boolean result = permission.belongsToTenant(otherTenantId);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("检查是否属于指定租户 - tenantId为null")
    void testBelongsToTenant_TenantIdNull_False() {
        // Given
        Permission permission = Permission.builder()
                .tenantId(null)
                .name(name)
                .code(code)
                .resource(resource)
                .action(action)
                .build();

        // When
        boolean result = permission.belongsToTenant(tenantId);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("获取权限标识 - 成功")
    void testGetPermissionIdentifier_Success() {
        // Given
        Permission permission = Permission.create(tenantId, name, code, resource, action);

        // When
        String identifier = permission.getPermissionIdentifier();

        // Then
        assertEquals("test:read", identifier);
    }

    @Test
    @DisplayName("获取权限标识 - 不同资源操作")
    void testGetPermissionIdentifier_DifferentResourceAction() {
        // Given
        Permission permission = Permission.create(tenantId, name, code, "user", "delete");

        // When
        String identifier = permission.getPermissionIdentifier();

        // Then
        assertEquals("user:delete", identifier);
    }

    @Test
    @DisplayName("检查是否有ABAC条件 - 有ABAC条件")
    void testHasAbacConditions_True() {
        // Given
        Map<String, Object> abacConditions = new HashMap<>();
        abacConditions.put("user.department", "IT");
        Permission permission = Permission.builder()
                .tenantId(tenantId)
                .name(name)
                .code(code)
                .resource(resource)
                .action(action)
                .abacConditions(abacConditions)
                .build();

        // When
        boolean result = permission.hasAbacConditions();

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("检查是否有ABAC条件 - 无ABAC条件（null）")
    void testHasAbacConditions_Null_False() {
        // Given
        Permission permission = Permission.builder()
                .tenantId(tenantId)
                .name(name)
                .code(code)
                .resource(resource)
                .action(action)
                .abacConditions(null)
                .build();

        // When
        boolean result = permission.hasAbacConditions();

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("检查是否有ABAC条件 - 无ABAC条件（空Map）")
    void testHasAbacConditions_EmptyMap_False() {
        // Given
        Permission permission = Permission.builder()
                .tenantId(tenantId)
                .name(name)
                .code(code)
                .resource(resource)
                .action(action)
                .abacConditions(new HashMap<>())
                .build();

        // When
        boolean result = permission.hasAbacConditions();

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("权限类型枚举 - FUNCTIONAL")
    void testPermissionType_Functional() {
        // When
        Permission.PermissionType type = Permission.PermissionType.FUNCTIONAL;

        // Then
        assertNotNull(type);
        assertEquals("FUNCTIONAL", type.name());
    }

    @Test
    @DisplayName("权限类型枚举 - DATA")
    void testPermissionType_Data() {
        // When
        Permission.PermissionType type = Permission.PermissionType.DATA;

        // Then
        assertNotNull(type);
        assertEquals("DATA", type.name());
    }
}

