package com.aixone.directory.tenant.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.aixone.directory.tenant.domain.aggregate.Tenant;
import com.aixone.directory.tenant.domain.aggregate.TenantStatus;
import com.aixone.directory.tenant.infrastructure.persistence.dbo.TenantDbo;

/**
 * 租户映射器单元测试
 */
@DisplayName("租户映射器测试")
class TenantMapperTest {

    private TenantMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TenantMapper();
    }

    @Test
    @DisplayName("租户转数据库对象 - 成功")
    void testToDbo_Success() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Tenant tenant = new Tenant(
                "tenant-123",
                "测试租户",
                "group-123",
                TenantStatus.ACTIVE,
                now,
                now
        );

        // When
        TenantDbo dbo = mapper.toDbo(tenant);

        // Then
        assertNotNull(dbo);
        assertEquals("tenant-123", dbo.getId());
        assertEquals("测试租户", dbo.getName());
        assertEquals("group-123", dbo.getGroupId());
        assertEquals(TenantStatus.ACTIVE, dbo.getStatus());
        assertEquals(now, dbo.getCreatedAt());
        assertEquals(now, dbo.getUpdatedAt());
    }

    @Test
    @DisplayName("租户转数据库对象 - null")
    void testToDbo_Null() {
        // When
        TenantDbo dbo = mapper.toDbo(null);

        // Then
        assertNull(dbo);
    }

    @Test
    @DisplayName("数据库对象转租户 - 成功")
    void testToDomain_Success() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        TenantDbo dbo = new TenantDbo();
        dbo.setId("tenant-123");
        dbo.setName("测试租户");
        dbo.setGroupId("group-123");
        dbo.setStatus(TenantStatus.ACTIVE);
        dbo.setCreatedAt(now);
        dbo.setUpdatedAt(now);

        // When
        Tenant tenant = mapper.toDomain(dbo);

        // Then
        assertNotNull(tenant);
        assertEquals("tenant-123", tenant.getId());
        assertEquals("测试租户", tenant.getName());
        assertEquals("group-123", tenant.getGroupId());
        assertEquals(TenantStatus.ACTIVE, tenant.getStatus());
        assertEquals(now, tenant.getCreatedAt());
        assertEquals(now, tenant.getUpdatedAt());
    }

    @Test
    @DisplayName("数据库对象转租户 - null")
    void testToDomain_Null() {
        // When
        Tenant tenant = mapper.toDomain(null);

        // Then
        assertNull(tenant);
    }

    @Test
    @DisplayName("双向映射一致性测试")
    void testBidirectionalMapping_Consistency() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Tenant original = new Tenant(
                "tenant-123",
                "测试租户",
                "group-123",
                TenantStatus.SUSPENDED,
                now,
                now.plusHours(1)
        );

        // When
        TenantDbo dbo = mapper.toDbo(original);
        Tenant converted = mapper.toDomain(dbo);

        // Then
        assertEquals(original.getId(), converted.getId());
        assertEquals(original.getName(), converted.getName());
        assertEquals(original.getGroupId(), converted.getGroupId());
        assertEquals(original.getStatus(), converted.getStatus());
        assertEquals(original.getCreatedAt(), converted.getCreatedAt());
        assertEquals(original.getUpdatedAt(), converted.getUpdatedAt());
    }

    @Test
    @DisplayName("映射 - 租户无groupId")
    void testMapping_WithoutGroupId() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Tenant tenant = new Tenant(
                "tenant-123",
                "测试租户",
                null,
                TenantStatus.ACTIVE,
                now,
                now
        );

        // When
        TenantDbo dbo = mapper.toDbo(tenant);
        Tenant converted = mapper.toDomain(dbo);

        // Then
        assertNull(converted.getGroupId());
        assertEquals(tenant.getName(), converted.getName());
    }

    @Test
    @DisplayName("映射 - 不同状态")
    void testMapping_DifferentStatuses() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        TenantStatus[] statuses = {TenantStatus.ACTIVE, TenantStatus.SUSPENDED};

        for (TenantStatus status : statuses) {
            // When
            Tenant tenant = new Tenant(
                    "tenant-" + status.name(),
                    "测试租户",
                    "group-123",
                    status,
                    now,
                    now
            );

            TenantDbo dbo = mapper.toDbo(tenant);
            Tenant converted = mapper.toDomain(dbo);

            // Then
            assertEquals(status, converted.getStatus());
        }
    }
}

