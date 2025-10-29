package com.aixone.directory.tenant.domain.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 租户聚合根单元测试
 */
@DisplayName("租户领域模型测试")
class TenantTest {

    private Tenant tenant;

    @BeforeEach
    void setUp() {
        // 测试前的初始化
    }

    @Test
    @DisplayName("创建租户 - 成功")
    void testCreateTenant_Success() {
        // Given
        String name = "测试租户";
        String groupId = "group-123";

        // When
        Tenant created = Tenant.create(name, groupId);

        // Then
        assertNotNull(created);
        assertEquals(name, created.getName());
        assertEquals(groupId, created.getGroupId());
        assertEquals(TenantStatus.ACTIVE, created.getStatus());
        assertNotNull(created.getId());
        assertNotNull(created.getCreatedAt());
        assertNotNull(created.getUpdatedAt());
    }

    @Test
    @DisplayName("创建租户 - 无groupId")
    void testCreateTenant_WithoutGroupId() {
        // Given
        String name = "测试租户";

        // When
        Tenant created = Tenant.create(name, null);

        // Then
        assertNotNull(created);
        assertEquals(name, created.getName());
        assertNull(created.getGroupId());
        assertEquals(TenantStatus.ACTIVE, created.getStatus());
    }

    @Test
    @DisplayName("更新租户名称 - 成功")
    void testUpdateName_Success() {
        // Given
        Tenant tenant = Tenant.create("旧名称", "group-123");
        String oldUpdatedAt = tenant.getUpdatedAt().toString();
        
        // 等待一小段时间，确保updatedAt会发生变化
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        String newName = "新名称";
        tenant.updateName(newName);

        // Then
        assertEquals(newName, tenant.getName());
        assertNotEquals(oldUpdatedAt, tenant.getUpdatedAt().toString());
    }

    @Test
    @DisplayName("更新租户组ID - 成功")
    void testUpdateGroupId_Success() {
        // Given
        Tenant tenant = Tenant.create("租户名称", "old-group");
        String oldUpdatedAt = tenant.getUpdatedAt().toString();
        
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        String newGroupId = "new-group";
        tenant.updateGroupId(newGroupId);

        // Then
        assertEquals(newGroupId, tenant.getGroupId());
        assertNotEquals(oldUpdatedAt, tenant.getUpdatedAt().toString());
    }

    @Test
    @DisplayName("暂停租户 - 成功")
    void testSuspend_Success() {
        // Given
        Tenant tenant = Tenant.create("测试租户", "group-123");
        assertEquals(TenantStatus.ACTIVE, tenant.getStatus());

        // When
        tenant.suspend();

        // Then
        assertEquals(TenantStatus.SUSPENDED, tenant.getStatus());
        assertNotNull(tenant.getUpdatedAt());
    }

    @Test
    @DisplayName("激活租户 - 成功")
    void testActivate_Success() {
        // Given
        Tenant tenant = Tenant.create("测试租户", "group-123");
        tenant.suspend();
        assertEquals(TenantStatus.SUSPENDED, tenant.getStatus());

        // When
        tenant.activate();

        // Then
        assertEquals(TenantStatus.ACTIVE, tenant.getStatus());
        assertNotNull(tenant.getUpdatedAt());
    }

    @Test
    @DisplayName("多次暂停和激活租户")
    void testMultipleSuspendAndActivate() {
        // Given
        Tenant tenant = Tenant.create("测试租户", "group-123");

        // When
        tenant.suspend();
        assertEquals(TenantStatus.SUSPENDED, tenant.getStatus());

        tenant.activate();
        assertEquals(TenantStatus.ACTIVE, tenant.getStatus());

        tenant.suspend();
        assertEquals(TenantStatus.SUSPENDED, tenant.getStatus());

        // Then - 验证状态变化
        assertEquals(TenantStatus.SUSPENDED, tenant.getStatus());
    }

    @Test
    @DisplayName("租户ID唯一性")
    void testTenantIdUniqueness() {
        // Given & When
        Tenant tenant1 = Tenant.create("租户1", "group-1");
        Tenant tenant2 = Tenant.create("租户2", "group-2");

        // Then
        assertNotNull(tenant1.getId());
        assertNotNull(tenant2.getId());
        assertNotEquals(tenant1.getId(), tenant2.getId());
    }

    @Test
    @DisplayName("租户状态切换 - 激活状态")
    void testStatusTransition_ActiveToSuspended() {
        // Given
        Tenant tenant = Tenant.create("测试租户", "group-123");
        TenantStatus initialStatus = tenant.getStatus();

        // When
        tenant.suspend();

        // Then
        assertEquals(TenantStatus.ACTIVE, initialStatus);
        assertEquals(TenantStatus.SUSPENDED, tenant.getStatus());
    }

    @Test
    @DisplayName("创建时间不可变")
    void testCreatedAtIsImmutable() {
        // Given
        Tenant tenant = Tenant.create("测试租户", "group-123");
        LocalDateTime createdAt = tenant.getCreatedAt();

        // When
        tenant.updateName("新名称");
        tenant.suspend();
        tenant.activate();

        // Then
        assertEquals(createdAt, tenant.getCreatedAt());
    }
}

