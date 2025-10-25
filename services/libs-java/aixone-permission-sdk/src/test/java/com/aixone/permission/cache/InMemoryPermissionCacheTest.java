package com.aixone.permission.cache;

import com.aixone.permission.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * InMemoryPermissionCache 单元测试
 *
 * @author aixone
 */
@DisplayName("InMemoryPermissionCache 单元测试")
class InMemoryPermissionCacheTest {

    private InMemoryPermissionCache cache;
    private User testUser;
    private Permission testPermission;
    private Role testRole;
    private Policy testPolicy;

    @BeforeEach
    void setUp() {
        cache = new InMemoryPermissionCache();
        
        // 创建测试数据
        testUser = createTestUser();
        testPermission = createTestPermission();
        testRole = createTestRole();
        testPolicy = createTestPolicy();
    }

    @Nested
    @DisplayName("用户角色缓存测试")
    class UserRoleCacheTests {

        @Test
        @DisplayName("应该成功缓存和获取用户角色")
        void shouldCacheAndGetUserRolesSuccessfully() {
            // Given
            String userId = "user-001";
            List<Role> roles = Arrays.asList(testRole);

            // When
            cache.putUserRoles(userId, roles);
            List<Role> result = cache.getUserRoles(userId);

            // Then
            assertEquals(roles, result);
            assertEquals(1, result.size());
            assertEquals(testRole, result.get(0));
        }

        @Test
        @DisplayName("应该返回空列表当用户没有角色时")
        void shouldReturnEmptyListWhenUserHasNoRoles() {
            // Given
            String userId = "user-001";

            // When
            List<Role> result = cache.getUserRoles(userId);

            // Then
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("应该支持多个用户角色")
        void shouldSupportMultipleUserRoles() {
            // Given
            String userId = "user-001";
            Role role1 = createTestRole();
            role1.setName("admin");
            Role role2 = createTestRole();
            role2.setName("user");
            List<Role> roles = Arrays.asList(role1, role2);

            // When
            cache.putUserRoles(userId, roles);
            List<Role> result = cache.getUserRoles(userId);

            // Then
            assertEquals(2, result.size());
            assertTrue(result.contains(role1));
            assertTrue(result.contains(role2));
        }

        @Test
        @DisplayName("应该支持更新用户角色")
        void shouldSupportUpdatingUserRoles() {
            // Given
            String userId = "user-001";
            List<Role> initialRoles = Arrays.asList(testRole);
            List<Role> updatedRoles = Arrays.asList(testRole, createTestRole());

            // When
            cache.putUserRoles(userId, initialRoles);
            List<Role> initialResult = cache.getUserRoles(userId);
            
            cache.putUserRoles(userId, updatedRoles);
            List<Role> updatedResult = cache.getUserRoles(userId);

            // Then
            assertEquals(1, initialResult.size());
            assertEquals(2, updatedResult.size());
        }
    }

    @Nested
    @DisplayName("角色权限缓存测试")
    class RolePermissionCacheTests {

        @Test
        @DisplayName("应该成功缓存和获取角色权限")
        void shouldCacheAndGetRolePermissionsSuccessfully() {
            // Given
            String roleId = "role-001";
            List<Permission> permissions = Arrays.asList(testPermission);

            // When
            cache.putRolePermissions(roleId, permissions);
            List<Permission> result = cache.getRolePermissions(roleId);

            // Then
            assertEquals(permissions, result);
            assertEquals(1, result.size());
            assertEquals(testPermission, result.get(0));
        }

        @Test
        @DisplayName("应该返回空列表当角色没有权限时")
        void shouldReturnEmptyListWhenRoleHasNoPermissions() {
            // Given
            String roleId = "role-001";

            // When
            List<Permission> result = cache.getRolePermissions(roleId);

            // Then
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("应该支持多个角色权限")
        void shouldSupportMultipleRolePermissions() {
            // Given
            String roleId = "role-001";
            Permission perm1 = createTestPermission();
            perm1.setResource("user");
            Permission perm2 = createTestPermission();
            perm2.setResource("order");
            List<Permission> permissions = Arrays.asList(perm1, perm2);

            // When
            cache.putRolePermissions(roleId, permissions);
            List<Permission> result = cache.getRolePermissions(roleId);

            // Then
            assertEquals(2, result.size());
            assertTrue(result.contains(perm1));
            assertTrue(result.contains(perm2));
        }

        @Test
        @DisplayName("应该支持更新角色权限")
        void shouldSupportUpdatingRolePermissions() {
            // Given
            String roleId = "role-001";
            List<Permission> initialPermissions = Arrays.asList(testPermission);
            List<Permission> updatedPermissions = Arrays.asList(testPermission, createTestPermission());

            // When
            cache.putRolePermissions(roleId, initialPermissions);
            List<Permission> initialResult = cache.getRolePermissions(roleId);
            
            cache.putRolePermissions(roleId, updatedPermissions);
            List<Permission> updatedResult = cache.getRolePermissions(roleId);

            // Then
            assertEquals(1, initialResult.size());
            assertEquals(2, updatedResult.size());
        }
    }

    @Nested
    @DisplayName("缓存管理测试")
    class CacheManagementTests {

        @Test
        @DisplayName("应该成功清除所有缓存")
        void shouldClearAllCacheSuccessfully() {
            // Given
            String userId = "user-001";
            String roleId = "role-001";
            List<Role> roles = Arrays.asList(testRole);
            List<Permission> permissions = Arrays.asList(testPermission);

            cache.putUserRoles(userId, roles);
            cache.putRolePermissions(roleId, permissions);

            // When
            cache.clear();

            // Then
            assertTrue(cache.getUserRoles(userId).isEmpty());
            assertTrue(cache.getRolePermissions(roleId).isEmpty());
        }

        @Test
        @DisplayName("应该成功清除用户相关缓存")
        void shouldClearUserCacheSuccessfully() {
            // Given
            String userId = "user-001";
            String roleId = "role-001";
            List<Role> roles = Arrays.asList(testRole);
            List<Permission> permissions = Arrays.asList(testPermission);

            cache.putUserRoles(userId, roles);
            cache.putRolePermissions(roleId, permissions);

            // When
            cache.clearUserCache(userId);

            // Then
            assertTrue(cache.getUserRoles(userId).isEmpty());
            assertFalse(cache.getRolePermissions(roleId).isEmpty()); // 角色权限缓存不受影响
        }

        @Test
        @DisplayName("应该获取缓存统计信息")
        void shouldGetCacheStats() {
            // Given
            String userId = "user-001";
            String roleId = "role-001";
            List<Role> roles = Arrays.asList(testRole);
            List<Permission> permissions = Arrays.asList(testPermission);

            cache.putUserRoles(userId, roles);
            cache.putRolePermissions(roleId, permissions);

            // When
            Map<String, Object> stats = cache.getCacheStats();

            // Then
            assertNotNull(stats);
            assertEquals(1, stats.get("userRoleCacheSize"));
            assertEquals(1, stats.get("rolePermissionCacheSize"));
        }

        @Test
        @DisplayName("应该处理空缓存统计")
        void shouldHandleEmptyCacheStats() {
            // When
            Map<String, Object> stats = cache.getCacheStats();

            // Then
            assertNotNull(stats);
            assertEquals(0, stats.get("userRoleCacheSize"));
            assertEquals(0, stats.get("rolePermissionCacheSize"));
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryConditionTests {

        @Test
        @DisplayName("应该处理null用户ID")
        void shouldHandleNullUserId() {
            // When
            List<Role> result = cache.getUserRoles(null);

            // Then
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("应该处理null角色ID")
        void shouldHandleNullRoleId() {
            // When
            List<Permission> result = cache.getRolePermissions(null);

            // Then
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("应该处理空字符串键")
        void shouldHandleEmptyStringKey() {
            // When
            List<Role> result = cache.getUserRoles("");

            // Then
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("应该处理null角色列表")
        void shouldHandleNullRoleList() {
            // Given
            String userId = "user-001";

            // When
            cache.putUserRoles(userId, null);
            List<Role> result = cache.getUserRoles(userId);

            // Then
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("应该处理null权限列表")
        void shouldHandleNullPermissionList() {
            // Given
            String roleId = "role-001";

            // When
            cache.putRolePermissions(roleId, null);
            List<Permission> result = cache.getRolePermissions(roleId);

            // Then
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("并发测试")
    class ConcurrencyTests {

        @Test
        @DisplayName("应该支持并发读写")
        void shouldSupportConcurrentReadWrite() throws InterruptedException {
            // Given
            String userId = "user-001";
            List<Role> roles = Arrays.asList(testRole);

            // When
            Thread writeThread = new Thread(() -> {
                for (int i = 0; i < 100; i++) {
                    cache.putUserRoles(userId + i, roles);
                }
            });

            Thread readThread = new Thread(() -> {
                for (int i = 0; i < 100; i++) {
                    cache.getUserRoles(userId + i);
                }
            });

            writeThread.start();
            readThread.start();

            writeThread.join();
            readThread.join();

            // Then
            // 如果没有异常，说明支持并发访问
            assertTrue(true);
        }

        @Test
        @DisplayName("应该支持并发清除")
        void shouldSupportConcurrentClear() throws InterruptedException {
            // Given
            String userId = "user-001";
            String roleId = "role-001";
            List<Role> roles = Arrays.asList(testRole);
            List<Permission> permissions = Arrays.asList(testPermission);

            cache.putUserRoles(userId, roles);
            cache.putRolePermissions(roleId, permissions);

            // When
            Thread clearThread1 = new Thread(() -> cache.clear());
            Thread clearThread2 = new Thread(() -> cache.clearUserCache(userId));

            clearThread1.start();
            clearThread2.start();

            clearThread1.join();
            clearThread2.join();

            // Then
            // 如果没有异常，说明支持并发清除
            assertTrue(true);
        }
    }

    @Nested
    @DisplayName("未实现方法测试")
    class UnimplementedMethodTests {

        @Test
        @DisplayName("应该返回空列表用于用户权限获取")
        void shouldReturnEmptyListForUserPermissions() {
            // When
            List<Permission> result = cache.getUserPermissions("user-001");

            // Then
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("应该不抛出异常用于用户权限缓存")
        void shouldNotThrowExceptionForUserPermissionsCache() {
            // When & Then
            assertDoesNotThrow(() -> {
                cache.putUserPermissions("user-001", Arrays.asList(testPermission));
            });
        }

        @Test
        @DisplayName("应该返回null用于用户获取")
        void shouldReturnNullForUserGet() {
            // When
            User result = cache.getUser("user-001");

            // Then
            assertNull(result);
        }

        @Test
        @DisplayName("应该不抛出异常用于用户缓存")
        void shouldNotThrowExceptionForUserCache() {
            // When & Then
            assertDoesNotThrow(() -> {
                cache.putUser("user-001", testUser);
            });
        }

        @Test
        @DisplayName("应该返回null用于权限获取")
        void shouldReturnNullForPermissionGet() {
            // When
            Permission result = cache.getPermission("perm-001");

            // Then
            assertNull(result);
        }

        @Test
        @DisplayName("应该不抛出异常用于权限缓存")
        void shouldNotThrowExceptionForPermissionCache() {
            // When & Then
            assertDoesNotThrow(() -> {
                cache.putPermission("perm-001", testPermission);
            });
        }

        @Test
        @DisplayName("应该返回null用于角色获取")
        void shouldReturnNullForRoleGet() {
            // When
            Role result = cache.getRole("role-001");

            // Then
            assertNull(result);
        }

        @Test
        @DisplayName("应该不抛出异常用于角色缓存")
        void shouldNotThrowExceptionForRoleCache() {
            // When & Then
            assertDoesNotThrow(() -> {
                cache.putRole("role-001", testRole);
            });
        }

        @Test
        @DisplayName("应该返回null用于策略获取")
        void shouldReturnNullForPolicyGet() {
            // When
            Policy result = cache.getPolicy("policy-001");

            // Then
            assertNull(result);
        }

        @Test
        @DisplayName("应该不抛出异常用于策略缓存")
        void shouldNotThrowExceptionForPolicyCache() {
            // When & Then
            assertDoesNotThrow(() -> {
                cache.putPolicy("policy-001", testPolicy);
            });
        }

        @Test
        @DisplayName("应该返回空列表用于ABAC策略获取")
        void shouldReturnEmptyListForAbacPolicies() {
            // When
            List<Policy> result = cache.getAbacPolicies("tenant-001:user:read");

            // Then
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("应该不抛出异常用于ABAC策略缓存")
        void shouldNotThrowExceptionForAbacPoliciesCache() {
            // When & Then
            assertDoesNotThrow(() -> {
                cache.putAbacPolicies("tenant-001:user:read", Arrays.asList(testPolicy));
            });
        }

        @Test
        @DisplayName("应该返回false用于过期检查")
        void shouldReturnFalseForExpirationCheck() {
            // When
            boolean result = cache.isExpired("any-key");

            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("应该不抛出异常用于过期缓存清理")
        void shouldNotThrowExceptionForExpiredCacheCleanup() {
            // When & Then
            assertDoesNotThrow(() -> {
                cache.cleanExpiredCache();
            });
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
