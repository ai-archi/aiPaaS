package com.aixone.directory.tenant.domain.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 租户状态枚举测试
 */
@DisplayName("租户状态枚举测试")
class TenantStatusTest {

    @Test
    @DisplayName("租户状态值 - ACTIVE")
    void testActiveStatus() {
        // When & Then
        assertEquals("ACTIVE", TenantStatus.ACTIVE.name());
    }

    @Test
    @DisplayName("租户状态值 - SUSPENDED")
    void testSuspendedStatus() {
        // When & Then
        assertEquals("SUSPENDED", TenantStatus.SUSPENDED.name());
    }

    @Test
    @DisplayName("租户状态数量")
    void testStatusCount() {
        // When & Then
        assertEquals(2, TenantStatus.values().length);
    }

    @Test
    @DisplayName("租户状态值Of")
    void testValueOf() {
        // When & Then
        assertEquals(TenantStatus.ACTIVE, TenantStatus.valueOf("ACTIVE"));
        assertEquals(TenantStatus.SUSPENDED, TenantStatus.valueOf("SUSPENDED"));
    }

    @Test
    @DisplayName("租户状态值Of - 非法值")
    void testValueOf_Invalid() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            TenantStatus.valueOf("INVALID");
        });
    }

    @Test
    @DisplayName("租户状态列表")
    void testStatusValues() {
        // When
        TenantStatus[] statuses = TenantStatus.values();

        // Then
        assertNotNull(statuses);
        assertEquals(2, statuses.length);
        assertTrue(java.util.Arrays.asList(statuses).contains(TenantStatus.ACTIVE));
        assertTrue(java.util.Arrays.asList(statuses).contains(TenantStatus.SUSPENDED));
    }
}

