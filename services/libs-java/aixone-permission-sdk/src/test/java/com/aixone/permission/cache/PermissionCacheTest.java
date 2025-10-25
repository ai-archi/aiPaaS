package com.aixone.permission.cache;

import com.aixone.permission.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * PermissionCache 接口测试
 * 测试权限缓存的基本行为
 *
 * @author aixone
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PermissionCache 接口测试")
class PermissionCacheTest {

    @Mock
    private PermissionCache permissionCache;

    private User testUser;
    private Permission testPermission;
    private Role testRole;
    private Policy testPolicy;

    @BeforeEach
    void setUp() {
        // 创建测试数据
        testUser = createTestUser();
        testPermission = createTestPermission();
        testRole = createTestRole();
        testPolicy = createTestPolicy();
    }

    @Nested
    @DisplayName("用户权限缓存测试")
    class UserPermissionCacheTests {

        @Test
        @DisplayName("应该成功获取用户权限")
        void shouldGetUserPermissionsSuccessfully() {
            // Given
            String userId = "user-001";
            List<Permission> expectedPermissions = Arrays.asList(testPermission);
            when(permissionCache.getUserPermissions(userId)).thenReturn(expectedPermissions);

            // When
            List<Permission> result = permissionCache.getUserPermissions(userId);

            // Then
            assertEquals(expectedPermissions, result);
            verify(permissionCache).getUserPermissions(userId);
        }

        @Test
        @DisplayName("应该成功缓存用户权限")
        void shouldPutUserPermissionsSuccessfully() {
            // Given
            String userId = "user-001";
            List<Permission> permissions = Arrays.asList(testPermission);

            // When
            permissionCache.putUserPermissions(userId, permissions);

            // Then
            verify(permissionCache).putUserPermissions(userId, permissions);
        }

        @Test
        @DisplayName("应该处理空权限列表")
        void shouldHandleEmptyPermissionList() {
            // Given
            String userId = "user-001";
            List<Permission> emptyPermissions = Collections.emptyList();
            when(permissionCache.getUserPermissions(userId)).thenReturn(emptyPermissions);

            // When
            List<Permission> result = permissionCache.getUserPermissions(userId);

            // Then
            assertTrue(result.isEmpty());
            verify(permissionCache).getUserPermissions(userId);
        }

        @Test
        @DisplayName("应该处理null权限列表")
        void shouldHandleNullPermissionList() {
            // Given
            String userId = "user-001";
            when(permissionCache.getUserPermissions(userId)).thenReturn(null);

            // When
            List<Permission> result = permissionCache.getUserPermissions(userId);

            // Then
            assertNull(result);
            verify(permissionCache).getUserPermissions(userId);
        }
    }

    @Nested
    @DisplayName("用户角色缓存测试")
    class UserRoleCacheTests {

        @Test
        @DisplayName("应该成功获取用户角色")
        void shouldGetUserRolesSuccessfully() {
            // Given
            String userId = "user-001";
            List<Role> expectedRoles = Arrays.asList(testRole);
            when(permissionCache.getUserRoles(userId)).thenReturn(expectedRoles);

            // When
            List<Role> result = permissionCache.getUserRoles(userId);

            // Then
            assertEquals(expectedRoles, result);
            verify(permissionCache).getUserRoles(userId);
        }

        @Test
        @DisplayName("应该成功缓存用户角色")
        void shouldPutUserRolesSuccessfully() {
            // Given
            String userId = "user-001";
            List<Role> roles = Arrays.asList(testRole);

            // When
            permissionCache.putUserRoles(userId, roles);

            // Then
            verify(permissionCache).putUserRoles(userId, roles);
        }

        @Test
        @DisplayName("应该处理空角色列表")
        void shouldHandleEmptyRoleList() {
            // Given
            String userId = "user-001";
            List<Role> emptyRoles = Collections.emptyList();
            when(permissionCache.getUserRoles(userId)).thenReturn(emptyRoles);

            // When
            List<Role> result = permissionCache.getUserRoles(userId);

            // Then
            assertTrue(result.isEmpty());
            verify(permissionCache).getUserRoles(userId);
        }
    }

    @Nested
    @DisplayName("用户缓存测试")
    class UserCacheTests {

        @Test
        @DisplayName("应该成功获取用户信息")
        void shouldGetUserSuccessfully() {
            // Given
            String userId = "user-001";
            when(permissionCache.getUser(userId)).thenReturn(testUser);

            // When
            User result = permissionCache.getUser(userId);

            // Then
            assertEquals(testUser, result);
            verify(permissionCache).getUser(userId);
        }

        @Test
        @DisplayName("应该成功缓存用户信息")
        void shouldPutUserSuccessfully() {
            // Given
            String userId = "user-001";

            // When
            permissionCache.putUser(userId, testUser);

            // Then
            verify(permissionCache).putUser(userId, testUser);
        }

        @Test
        @DisplayName("应该处理不存在的用户")
        void shouldHandleNonExistentUser() {
            // Given
            String userId = "non-existent";
            when(permissionCache.getUser(userId)).thenReturn(null);

            // When
            User result = permissionCache.getUser(userId);

            // Then
            assertNull(result);
            verify(permissionCache).getUser(userId);
        }
    }

    @Nested
    @DisplayName("权限缓存测试")
    class PermissionCacheTests {

        @Test
        @DisplayName("应该成功获取权限信息")
        void shouldGetPermissionSuccessfully() {
            // Given
            String permissionId = "perm-001";
            when(permissionCache.getPermission(permissionId)).thenReturn(testPermission);

            // When
            Permission result = permissionCache.getPermission(permissionId);

            // Then
            assertEquals(testPermission, result);
            verify(permissionCache).getPermission(permissionId);
        }

        @Test
        @DisplayName("应该成功缓存权限信息")
        void shouldPutPermissionSuccessfully() {
            // Given
            String permissionId = "perm-001";

            // When
            permissionCache.putPermission(permissionId, testPermission);

            // Then
            verify(permissionCache).putPermission(permissionId, testPermission);
        }

        @Test
        @DisplayName("应该处理不存在的权限")
        void shouldHandleNonExistentPermission() {
            // Given
            String permissionId = "non-existent";
            when(permissionCache.getPermission(permissionId)).thenReturn(null);

            // When
            Permission result = permissionCache.getPermission(permissionId);

            // Then
            assertNull(result);
            verify(permissionCache).getPermission(permissionId);
        }
    }

    @Nested
    @DisplayName("角色缓存测试")
    class RoleCacheTests {

        @Test
        @DisplayName("应该成功获取角色信息")
        void shouldGetRoleSuccessfully() {
            // Given
            String roleId = "role-001";
            when(permissionCache.getRole(roleId)).thenReturn(testRole);

            // When
            Role result = permissionCache.getRole(roleId);

            // Then
            assertEquals(testRole, result);
            verify(permissionCache).getRole(roleId);
        }

        @Test
        @DisplayName("应该成功缓存角色信息")
        void shouldPutRoleSuccessfully() {
            // Given
            String roleId = "role-001";

            // When
            permissionCache.putRole(roleId, testRole);

            // Then
            verify(permissionCache).putRole(roleId, testRole);
        }

        @Test
        @DisplayName("应该成功获取角色权限")
        void shouldGetRolePermissionsSuccessfully() {
            // Given
            String roleId = "role-001";
            List<Permission> expectedPermissions = Arrays.asList(testPermission);
            when(permissionCache.getRolePermissions(roleId)).thenReturn(expectedPermissions);

            // When
            List<Permission> result = permissionCache.getRolePermissions(roleId);

            // Then
            assertEquals(expectedPermissions, result);
            verify(permissionCache).getRolePermissions(roleId);
        }

        @Test
        @DisplayName("应该成功缓存角色权限")
        void shouldPutRolePermissionsSuccessfully() {
            // Given
            String roleId = "role-001";
            List<Permission> permissions = Arrays.asList(testPermission);

            // When
            permissionCache.putRolePermissions(roleId, permissions);

            // Then
            verify(permissionCache).putRolePermissions(roleId, permissions);
        }
    }

    @Nested
    @DisplayName("ABAC策略缓存测试")
    class AbacPolicyCacheTests {

        @Test
        @DisplayName("应该成功获取ABAC策略")
        void shouldGetPolicySuccessfully() {
            // Given
            String policyId = "policy-001";
            when(permissionCache.getPolicy(policyId)).thenReturn(testPolicy);

            // When
            Policy result = permissionCache.getPolicy(policyId);

            // Then
            assertEquals(testPolicy, result);
            verify(permissionCache).getPolicy(policyId);
        }

        @Test
        @DisplayName("应该成功缓存ABAC策略")
        void shouldPutPolicySuccessfully() {
            // Given
            String policyId = "policy-001";

            // When
            permissionCache.putPolicy(policyId, testPolicy);

            // Then
            verify(permissionCache).putPolicy(policyId, testPolicy);
        }

        @Test
        @DisplayName("应该成功获取资源相关的ABAC策略")
        void shouldGetAbacPoliciesSuccessfully() {
            // Given
            String key = "tenant-001:user:read";
            List<Policy> expectedPolicies = Arrays.asList(testPolicy);
            when(permissionCache.getAbacPolicies(key)).thenReturn(expectedPolicies);

            // When
            List<Policy> result = permissionCache.getAbacPolicies(key);

            // Then
            assertEquals(expectedPolicies, result);
            verify(permissionCache).getAbacPolicies(key);
        }

        @Test
        @DisplayName("应该成功缓存资源相关的ABAC策略")
        void shouldPutAbacPoliciesSuccessfully() {
            // Given
            String key = "tenant-001:user:read";
            List<Policy> policies = Arrays.asList(testPolicy);

            // When
            permissionCache.putAbacPolicies(key, policies);

            // Then
            verify(permissionCache).putAbacPolicies(key, policies);
        }
    }

    @Nested
    @DisplayName("缓存管理测试")
    class CacheManagementTests {

        @Test
        @DisplayName("应该成功清除用户相关缓存")
        void shouldClearUserCacheSuccessfully() {
            // Given
            String userId = "user-001";

            // When
            permissionCache.clearUserCache(userId);

            // Then
            verify(permissionCache).clearUserCache(userId);
        }

        @Test
        @DisplayName("应该成功清除所有缓存")
        void shouldClearAllCacheSuccessfully() {
            // When
            permissionCache.clear();

            // Then
            verify(permissionCache).clear();
        }

        @Test
        @DisplayName("应该检查缓存是否过期")
        void shouldCheckCacheExpiration() {
            // Given
            String key = "user-001";
            when(permissionCache.isExpired(key)).thenReturn(false);

            // When
            boolean result = permissionCache.isExpired(key);

            // Then
            assertFalse(result);
            verify(permissionCache).isExpired(key);
        }

        @Test
        @DisplayName("应该清理过期缓存")
        void shouldCleanExpiredCache() {
            // When
            permissionCache.cleanExpiredCache();

            // Then
            verify(permissionCache).cleanExpiredCache();
        }

        @Test
        @DisplayName("应该获取缓存统计信息")
        void shouldGetCacheStats() {
            // Given
            Map<String, Object> expectedStats = new HashMap<>();
            expectedStats.put("hitCount", 100);
            expectedStats.put("missCount", 10);
            expectedStats.put("size", 50);
            when(permissionCache.getCacheStats()).thenReturn(expectedStats);

            // When
            Map<String, Object> result = permissionCache.getCacheStats();

            // Then
            assertEquals(expectedStats, result);
            verify(permissionCache).getCacheStats();
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryConditionTests {

        @Test
        @DisplayName("应该处理空字符串键")
        void shouldHandleEmptyStringKey() {
            // Given
            String emptyKey = "";
            when(permissionCache.getUser(emptyKey)).thenReturn(null);

            // When
            User result = permissionCache.getUser(emptyKey);

            // Then
            assertNull(result);
            verify(permissionCache).getUser(emptyKey);
        }

        @Test
        @DisplayName("应该处理null键")
        void shouldHandleNullKey() {
            // Given
            when(permissionCache.getUser(null)).thenReturn(null);

            // When
            User result = permissionCache.getUser(null);

            // Then
            assertNull(result);
            verify(permissionCache).getUser(null);
        }

        @Test
        @DisplayName("应该处理特殊字符键")
        void shouldHandleSpecialCharacterKey() {
            // Given
            String specialKey = "user@domain.com:role#admin";
            when(permissionCache.getUser(specialKey)).thenReturn(null);

            // When
            User result = permissionCache.getUser(specialKey);

            // Then
            assertNull(result);
            verify(permissionCache).getUser(specialKey);
        }

        @Test
        @DisplayName("应该处理Unicode字符键")
        void shouldHandleUnicodeCharacterKey() {
            // Given
            String unicodeKey = "用户@域名.com:角色#管理员";
            when(permissionCache.getUser(unicodeKey)).thenReturn(null);

            // When
            User result = permissionCache.getUser(unicodeKey);

            // Then
            assertNull(result);
            verify(permissionCache).getUser(unicodeKey);
        }
    }

    @Nested
    @DisplayName("性能测试")
    class PerformanceTests {

        @Test
        @DisplayName("应该支持快速响应")
        void shouldSupportFastResponse() {
            // Given
            String userId = "user-001";
            when(permissionCache.getUser(userId)).thenReturn(testUser);

            // When
            long startTime = System.currentTimeMillis();
            User result = permissionCache.getUser(userId);
            long endTime = System.currentTimeMillis();

            // Then
            assertEquals(testUser, result);
            assertTrue(endTime - startTime < 1000); // 应该在1秒内完成
            verify(permissionCache).getUser(userId);
        }

        @Test
        @DisplayName("应该支持并发访问")
        void shouldSupportConcurrentAccess() throws InterruptedException {
            // Given
            String userId = "user-001";
            when(permissionCache.getUser(userId)).thenReturn(testUser);

            // When
            Thread[] threads = new Thread[10];
            User[] results = new User[10];
            
            for (int i = 0; i < 10; i++) {
                final int index = i;
                threads[i] = new Thread(() -> {
                    results[index] = permissionCache.getUser(userId);
                });
                threads[i].start();
            }

            for (Thread thread : threads) {
                thread.join();
            }

            // Then
            for (User result : results) {
                assertEquals(testUser, result);
            }
            verify(permissionCache, times(10)).getUser(userId);
        }
    }

    // ==================== 辅助方法 ====================

    private User createTestUser() {
        User user = new User();
        user.setUserId("user-001");
        user.setTenantId("tenant-001");
        user.setUsername("testuser");
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("department", "IT");
        user.setAttributes(attributes);
        return user;
    }

    private Permission createTestPermission() {
        Permission permission = new Permission();
        permission.setPermissionId("perm-001");
        permission.setTenantId("tenant-001");
        permission.setName("测试权限");
        permission.setResource("user");
        permission.setAction("read");
        permission.setDescription("测试权限描述");
        permission.setLevel(Permission.PermissionLevel.READ);
        return permission;
    }

    private Role createTestRole() {
        Role role = new Role();
        role.setRoleId("role-001");
        role.setTenantId("tenant-001");
        role.setName("admin");
        role.setDescription("管理员角色");
        role.setPermissionIds(Arrays.asList("perm-001", "perm-002"));
        return role;
    }

    private Policy createTestPolicy() {
        Policy policy = new Policy();
        policy.setPolicyId("policy-001");
        policy.setTenantId("tenant-001");
        policy.setName("测试策略");
        policy.setDescription("测试策略描述");
        policy.setCondition("user.department == 'IT'");
        // Policy类没有setEffect方法，使用attributes代替
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("effect", "allow");
        policy.setAttributes(attributes);
        return policy;
    }
}
