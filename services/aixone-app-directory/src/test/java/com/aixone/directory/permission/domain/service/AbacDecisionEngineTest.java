package com.aixone.directory.permission.domain.service;

import com.aixone.directory.permission.domain.aggregate.Permission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ABAC权限决策引擎单元测试
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ABAC权限决策引擎测试")
class AbacDecisionEngineTest {

    @InjectMocks
    private AbacDecisionEngine abacDecisionEngine;

    private String tenantId;
    private String userId;
    private Permission permissionWithoutAbac;
    private Permission permissionWithAbac;

    @BeforeEach
    void setUp() {
        tenantId = "tenant-" + UUID.randomUUID().toString();
        userId = "user-" + UUID.randomUUID().toString();

        permissionWithoutAbac = Permission.builder()
                .permissionId("permission-1")
                .tenantId(tenantId)
                .name("无ABAC条件的权限")
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
                .permissionId("permission-2")
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
    @DisplayName("检查权限 - 没有ABAC条件，默认通过")
    void testCheckPermission_NoAbacConditions_ReturnsTrue() {
        // Given
        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("department", "IT");

        // When
        boolean result = abacDecisionEngine.checkPermission(
                userId, tenantId, permissionWithoutAbac, userAttributes, null, null);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("检查权限 - ABAC条件为空，默认通过")
    void testCheckPermission_EmptyAbacConditions_ReturnsTrue() {
        // Given
        Permission permission = Permission.builder()
                .permissionId("permission-3")
                .tenantId(tenantId)
                .abacConditions(new HashMap<>())
                .build();

        // When
        boolean result = abacDecisionEngine.checkPermission(
                userId, tenantId, permission, null, null, null);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("检查权限 - 满足ABAC条件（等于）")
    void testCheckPermission_MeetsAbacCondition_Equals_Success() {
        // Given
        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("department", "IT");

        // When
        boolean result = abacDecisionEngine.checkPermission(
                userId, tenantId, permissionWithAbac, userAttributes, null, null);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("检查权限 - 不满足ABAC条件（等于）")
    void testCheckPermission_NotMeetsAbacCondition_Equals_Failure() {
        // Given
        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("department", "HR");

        // When
        boolean result = abacDecisionEngine.checkPermission(
                userId, tenantId, permissionWithAbac, userAttributes, null, null);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("检查权限 - 满足ABAC条件（不等于 $ne）")
    void testCheckPermission_MeetsAbacCondition_NotEquals_Success() {
        // Given
        Map<String, Object> abacConditions = new HashMap<>();
        Map<String, Object> neCondition = new HashMap<>();
        neCondition.put("$ne", "HR");
        abacConditions.put("user.department", neCondition);
        
        Permission permission = Permission.builder()
                .permissionId("permission-4")
                .tenantId(tenantId)
                .abacConditions(abacConditions)
                .build();

        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("department", "IT");

        // When
        boolean result = abacDecisionEngine.checkPermission(
                userId, tenantId, permission, userAttributes, null, null);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("检查权限 - 不满足ABAC条件（不等于 $ne）")
    void testCheckPermission_NotMeetsAbacCondition_NotEquals_Failure() {
        // Given
        Map<String, Object> abacConditions = new HashMap<>();
        Map<String, Object> neCondition = new HashMap<>();
        neCondition.put("$ne", "IT");
        abacConditions.put("user.department", neCondition);
        
        Permission permission = Permission.builder()
                .permissionId("permission-5")
                .tenantId(tenantId)
                .abacConditions(abacConditions)
                .build();

        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("department", "IT");

        // When
        boolean result = abacDecisionEngine.checkPermission(
                userId, tenantId, permission, userAttributes, null, null);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("检查权限 - 满足ABAC条件（包含 $in）")
    void testCheckPermission_MeetsAbacCondition_In_Success() {
        // Given
        Map<String, Object> abacConditions = new HashMap<>();
        Map<String, Object> inCondition = new HashMap<>();
        inCondition.put("$in", java.util.List.of("admin", "manager", "IT"));
        abacConditions.put("user.roles", inCondition);
        
        Permission permission = Permission.builder()
                .permissionId("permission-6")
                .tenantId(tenantId)
                .abacConditions(abacConditions)
                .build();

        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("roles", "admin");

        // When
        boolean result = abacDecisionEngine.checkPermission(
                userId, tenantId, permission, userAttributes, null, null);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("检查权限 - 不满足ABAC条件（包含 $in）")
    void testCheckPermission_NotMeetsAbacCondition_In_Failure() {
        // Given
        Map<String, Object> abacConditions = new HashMap<>();
        Map<String, Object> inCondition = new HashMap<>();
        inCondition.put("$in", java.util.List.of("admin", "manager"));
        abacConditions.put("user.roles", inCondition);
        
        Permission permission = Permission.builder()
                .permissionId("permission-7")
                .tenantId(tenantId)
                .abacConditions(abacConditions)
                .build();

        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("roles", "user");

        // When
        boolean result = abacDecisionEngine.checkPermission(
                userId, tenantId, permission, userAttributes, null, null);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("检查权限 - 满足ABAC条件（大于 $gt）")
    void testCheckPermission_MeetsAbacCondition_GreaterThan_Success() {
        // Given
        Map<String, Object> abacConditions = new HashMap<>();
        Map<String, Object> gtCondition = new HashMap<>();
        gtCondition.put("$gt", 5);
        abacConditions.put("user.level", gtCondition);
        
        Permission permission = Permission.builder()
                .permissionId("permission-8")
                .tenantId(tenantId)
                .abacConditions(abacConditions)
                .build();

        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("level", 8);

        // When
        boolean result = abacDecisionEngine.checkPermission(
                userId, tenantId, permission, userAttributes, null, null);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("检查权限 - 不满足ABAC条件（大于 $gt）")
    void testCheckPermission_NotMeetsAbacCondition_GreaterThan_Failure() {
        // Given
        Map<String, Object> abacConditions = new HashMap<>();
        Map<String, Object> gtCondition = new HashMap<>();
        gtCondition.put("$gt", 5);
        abacConditions.put("user.level", gtCondition);
        
        Permission permission = Permission.builder()
                .permissionId("permission-9")
                .tenantId(tenantId)
                .abacConditions(abacConditions)
                .build();

        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("level", 3);

        // When
        boolean result = abacDecisionEngine.checkPermission(
                userId, tenantId, permission, userAttributes, null, null);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("检查权限 - 满足ABAC条件（大于等于 $gte）")
    void testCheckPermission_MeetsAbacCondition_GreaterThanOrEqual_Success() {
        // Given
        Map<String, Object> abacConditions = new HashMap<>();
        Map<String, Object> gteCondition = new HashMap<>();
        gteCondition.put("$gte", 5);
        abacConditions.put("user.level", gteCondition);
        
        Permission permission = Permission.builder()
                .permissionId("permission-10")
                .tenantId(tenantId)
                .abacConditions(abacConditions)
                .build();

        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("level", 5);

        // When
        boolean result = abacDecisionEngine.checkPermission(
                userId, tenantId, permission, userAttributes, null, null);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("检查权限 - 满足ABAC条件（小于 $lt）")
    void testCheckPermission_MeetsAbacCondition_LessThan_Success() {
        // Given
        Map<String, Object> abacConditions = new HashMap<>();
        Map<String, Object> ltCondition = new HashMap<>();
        ltCondition.put("$lt", 10);
        abacConditions.put("user.level", ltCondition);
        
        Permission permission = Permission.builder()
                .permissionId("permission-11")
                .tenantId(tenantId)
                .abacConditions(abacConditions)
                .build();

        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("level", 5);

        // When
        boolean result = abacDecisionEngine.checkPermission(
                userId, tenantId, permission, userAttributes, null, null);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("检查权限 - 满足ABAC条件（小于等于 $lte）")
    void testCheckPermission_MeetsAbacCondition_LessThanOrEqual_Success() {
        // Given
        Map<String, Object> abacConditions = new HashMap<>();
        Map<String, Object> lteCondition = new HashMap<>();
        lteCondition.put("$lte", 10);
        abacConditions.put("user.level", lteCondition);
        
        Permission permission = Permission.builder()
                .permissionId("permission-12")
                .tenantId(tenantId)
                .abacConditions(abacConditions)
                .build();

        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("level", 10);

        // When
        boolean result = abacDecisionEngine.checkPermission(
                userId, tenantId, permission, userAttributes, null, null);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("检查权限 - 嵌套属性（user.department）")
    void testCheckPermission_NestedAttribute_Success() {
        // Given
        Map<String, Object> userAttributes = new HashMap<>();
        Map<String, Object> user = new HashMap<>();
        user.put("department", "IT");
        userAttributes.put("user", user);

        // When
        boolean result = abacDecisionEngine.checkPermission(
                userId, tenantId, permissionWithAbac, userAttributes, null, null);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("检查权限 - 多个ABAC条件，全部满足")
    void testCheckPermission_MultipleConditions_AllMet_Success() {
        // Given
        Map<String, Object> abacConditions = new HashMap<>();
        abacConditions.put("user.department", "IT");
        Map<String, Object> gtCondition = new HashMap<>();
        gtCondition.put("$gt", 5);
        abacConditions.put("user.level", gtCondition);
        
        Permission permission = Permission.builder()
                .permissionId("permission-13")
                .tenantId(tenantId)
                .abacConditions(abacConditions)
                .build();

        Map<String, Object> userAttributes = new HashMap<>();
        Map<String, Object> user = new HashMap<>();
        user.put("department", "IT");
        user.put("level", 8);
        userAttributes.put("user", user);

        // When
        boolean result = abacDecisionEngine.checkPermission(
                userId, tenantId, permission, userAttributes, null, null);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("检查权限 - 多个ABAC条件，部分不满足")
    void testCheckPermission_MultipleConditions_PartiallyNotMet_Failure() {
        // Given
        Map<String, Object> abacConditions = new HashMap<>();
        abacConditions.put("user.department", "IT");
        Map<String, Object> gtCondition = new HashMap<>();
        gtCondition.put("$gt", 5);
        abacConditions.put("user.level", gtCondition);
        
        Permission permission = Permission.builder()
                .permissionId("permission-14")
                .tenantId(tenantId)
                .abacConditions(abacConditions)
                .build();

        Map<String, Object> userAttributes = new HashMap<>();
        Map<String, Object> user = new HashMap<>();
        user.put("department", "IT");
        user.put("level", 3); // 不满足大于5的条件
        userAttributes.put("user", user);

        // When
        boolean result = abacDecisionEngine.checkPermission(
                userId, tenantId, permission, userAttributes, null, null);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("检查权限 - 使用资源属性")
    void testCheckPermission_WithResourceAttributes_Success() {
        // Given
        Map<String, Object> abacConditions = new HashMap<>();
        abacConditions.put("resource.owner", userId);
        
        Permission permission = Permission.builder()
                .permissionId("permission-15")
                .tenantId(tenantId)
                .abacConditions(abacConditions)
                .build();

        Map<String, Object> resourceAttributes = new HashMap<>();
        resourceAttributes.put("owner", userId);

        // When
        boolean result = abacDecisionEngine.checkPermission(
                userId, tenantId, permission, null, resourceAttributes, null);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("检查权限 - 使用环境属性")
    void testCheckPermission_WithEnvironmentAttributes_Success() {
        // Given
        Map<String, Object> abacConditions = new HashMap<>();
        abacConditions.put("environment.location", "office");
        
        Permission permission = Permission.builder()
                .permissionId("permission-16")
                .tenantId(tenantId)
                .abacConditions(abacConditions)
                .build();

        Map<String, Object> environmentAttributes = new HashMap<>();
        environmentAttributes.put("location", "office");

        // When
        boolean result = abacDecisionEngine.checkPermission(
                userId, tenantId, permission, null, null, environmentAttributes);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("检查权限 - 属性优先级（用户属性 > 资源属性 > 环境属性）")
    void testCheckPermission_AttributePriority_Success() {
        // Given
        Map<String, Object> abacConditions = new HashMap<>();
        abacConditions.put("department", "IT");
        
        Permission permission = Permission.builder()
                .permissionId("permission-17")
                .tenantId(tenantId)
                .abacConditions(abacConditions)
                .build();

        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("department", "IT");
        Map<String, Object> resourceAttributes = new HashMap<>();
        resourceAttributes.put("department", "HR");
        Map<String, Object> environmentAttributes = new HashMap<>();
        environmentAttributes.put("department", "Finance");

        // When
        boolean result = abacDecisionEngine.checkPermission(
                userId, tenantId, permission, userAttributes, resourceAttributes, environmentAttributes);

        // Then
        assertTrue(result); // 用户属性优先级最高，应该使用"IT"
    }
}

