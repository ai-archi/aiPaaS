package com.aixone.permission.validator;

import com.aixone.permission.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * PermissionValidator 接口测试
 * 测试权限验证器的基本行为
 *
 * @author aixone
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PermissionValidator 接口测试")
class PermissionValidatorTest {

    @Mock
    private PermissionValidator permissionValidator;

    private User testUser;
    private Permission testPermission;
    private Resource testResource;

    @BeforeEach
    void setUp() {
        // 创建测试数据
        testUser = createTestUser();
        testPermission = createTestPermission();
        testResource = createTestResource();
    }

    @Nested
    @DisplayName("权限检查基本行为测试")
    class BasicPermissionCheckTests {

        @Test
        @DisplayName("应该成功检查用户权限")
        void shouldCheckUserPermissionSuccessfully() {
            // Given
            when(permissionValidator.hasPermission(testUser, testPermission, testResource))
                .thenReturn(true);

            // When
            boolean result = permissionValidator.hasPermission(testUser, testPermission, testResource);

            // Then
            assertTrue(result);
            verify(permissionValidator).hasPermission(testUser, testPermission, testResource);
        }

        @Test
        @DisplayName("应该拒绝没有权限的用户")
        void shouldDenyUserWithoutPermission() {
            // Given
            when(permissionValidator.hasPermission(testUser, testPermission, testResource))
                .thenReturn(false);

            // When
            boolean result = permissionValidator.hasPermission(testUser, testPermission, testResource);

            // Then
            assertFalse(result);
            verify(permissionValidator).hasPermission(testUser, testPermission, testResource);
        }

        @Test
        @DisplayName("应该处理null用户")
        void shouldHandleNullUser() {
            // Given
            when(permissionValidator.hasPermission(null, testPermission, testResource))
                .thenReturn(false);

            // When
            boolean result = permissionValidator.hasPermission(null, testPermission, testResource);

            // Then
            assertFalse(result);
            verify(permissionValidator).hasPermission(null, testPermission, testResource);
        }

        @Test
        @DisplayName("应该处理null权限")
        void shouldHandleNullPermission() {
            // Given
            when(permissionValidator.hasPermission(testUser, null, testResource))
                .thenReturn(false);

            // When
            boolean result = permissionValidator.hasPermission(testUser, null, testResource);

            // Then
            assertFalse(result);
            verify(permissionValidator).hasPermission(testUser, null, testResource);
        }

        @Test
        @DisplayName("应该处理null资源")
        void shouldHandleNullResource() {
            // Given
            when(permissionValidator.hasPermission(testUser, testPermission, null))
                .thenReturn(false);

            // When
            boolean result = permissionValidator.hasPermission(testUser, testPermission, null);

            // Then
            assertFalse(result);
            verify(permissionValidator).hasPermission(testUser, testPermission, null);
        }

        @Test
        @DisplayName("应该处理所有参数为null的情况")
        void shouldHandleAllNullParameters() {
            // Given
            when(permissionValidator.hasPermission(null, null, null))
                .thenReturn(false);

            // When
            boolean result = permissionValidator.hasPermission(null, null, null);

            // Then
            assertFalse(result);
            verify(permissionValidator).hasPermission(null, null, null);
        }
    }

    @Nested
    @DisplayName("权限验证器实现测试")
    class PermissionValidatorImplementationTests {

        @Test
        @DisplayName("应该支持多次调用")
        void shouldSupportMultipleCalls() {
            // Given
            when(permissionValidator.hasPermission(testUser, testPermission, testResource))
                .thenReturn(true);

            // When
            boolean result1 = permissionValidator.hasPermission(testUser, testPermission, testResource);
            boolean result2 = permissionValidator.hasPermission(testUser, testPermission, testResource);
            boolean result3 = permissionValidator.hasPermission(testUser, testPermission, testResource);

            // Then
            assertTrue(result1);
            assertTrue(result2);
            assertTrue(result3);
            verify(permissionValidator, times(3)).hasPermission(testUser, testPermission, testResource);
        }

        @Test
        @DisplayName("应该支持不同参数组合")
        void shouldSupportDifferentParameterCombinations() {
            // Given
            User user1 = createTestUser();
            user1.setUserId("user-001");
            User user2 = createTestUser();
            user2.setUserId("user-002");
            
            Permission perm1 = createTestPermission();
            perm1.setResource("user");
            Permission perm2 = createTestPermission();
            perm2.setResource("order");
            
            Resource res1 = createTestResource();
            res1.setType("user");
            Resource res2 = createTestResource();
            res2.setType("order");

            when(permissionValidator.hasPermission(user1, perm1, res1)).thenReturn(true);
            when(permissionValidator.hasPermission(user1, perm2, res1)).thenReturn(false);
            when(permissionValidator.hasPermission(user2, perm1, res1)).thenReturn(false);
            when(permissionValidator.hasPermission(user2, perm2, res2)).thenReturn(true);

            // When & Then
            assertTrue(permissionValidator.hasPermission(user1, perm1, res1));
            assertFalse(permissionValidator.hasPermission(user1, perm2, res1));
            assertFalse(permissionValidator.hasPermission(user2, perm1, res1));
            assertTrue(permissionValidator.hasPermission(user2, perm2, res2));

            verify(permissionValidator).hasPermission(user1, perm1, res1);
            verify(permissionValidator).hasPermission(user1, perm2, res1);
            verify(permissionValidator).hasPermission(user2, perm1, res1);
            verify(permissionValidator).hasPermission(user2, perm2, res2);
        }

        @Test
        @DisplayName("应该支持异常处理")
        void shouldSupportExceptionHandling() {
            // Given
            when(permissionValidator.hasPermission(testUser, testPermission, testResource))
                .thenThrow(new RuntimeException("权限检查异常"));

            // When & Then
            assertThrows(RuntimeException.class, () -> {
                permissionValidator.hasPermission(testUser, testPermission, testResource);
            });

            verify(permissionValidator).hasPermission(testUser, testPermission, testResource);
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryConditionTests {

        @Test
        @DisplayName("应该处理空字符串属性")
        void shouldHandleEmptyStringAttributes() {
            // Given
            testUser.setUsername("");
            testPermission.setResource("");
            testPermission.setAction("");
            testResource.setName("");

            when(permissionValidator.hasPermission(testUser, testPermission, testResource))
                .thenReturn(false);

            // When
            boolean result = permissionValidator.hasPermission(testUser, testPermission, testResource);

            // Then
            assertFalse(result);
            verify(permissionValidator).hasPermission(testUser, testPermission, testResource);
        }

        @Test
        @DisplayName("应该处理特殊字符")
        void shouldHandleSpecialCharacters() {
            // Given
            testUser.setUsername("user@domain.com");
            testPermission.setResource("resource-with-dash");
            testPermission.setAction("action_with_underscore");
            testResource.setName("Resource with spaces");

            when(permissionValidator.hasPermission(testUser, testPermission, testResource))
                .thenReturn(true);

            // When
            boolean result = permissionValidator.hasPermission(testUser, testPermission, testResource);

            // Then
            assertTrue(result);
            verify(permissionValidator).hasPermission(testUser, testPermission, testResource);
        }

        @Test
        @DisplayName("应该处理Unicode字符")
        void shouldHandleUnicodeCharacters() {
            // Given
            testUser.setUsername("用户@域名.com");
            testPermission.setResource("资源-测试");
            testPermission.setAction("操作_测试");
            testResource.setName("资源 名称");

            when(permissionValidator.hasPermission(testUser, testPermission, testResource))
                .thenReturn(true);

            // When
            boolean result = permissionValidator.hasPermission(testUser, testPermission, testResource);

            // Then
            assertTrue(result);
            verify(permissionValidator).hasPermission(testUser, testPermission, testResource);
        }
    }

    @Nested
    @DisplayName("性能测试")
    class PerformanceTests {

        @Test
        @DisplayName("应该支持快速响应")
        void shouldSupportFastResponse() {
            // Given
            when(permissionValidator.hasPermission(testUser, testPermission, testResource))
                .thenReturn(true);

            // When
            long startTime = System.currentTimeMillis();
            boolean result = permissionValidator.hasPermission(testUser, testPermission, testResource);
            long endTime = System.currentTimeMillis();

            // Then
            assertTrue(result);
            assertTrue(endTime - startTime < 1000); // 应该在1秒内完成
            verify(permissionValidator).hasPermission(testUser, testPermission, testResource);
        }

        @Test
        @DisplayName("应该支持并发调用")
        void shouldSupportConcurrentCalls() throws InterruptedException {
            // Given
            when(permissionValidator.hasPermission(testUser, testPermission, testResource))
                .thenReturn(true);

            // When
            Thread[] threads = new Thread[10];
            boolean[] results = new boolean[10];
            
            for (int i = 0; i < 10; i++) {
                final int index = i;
                threads[i] = new Thread(() -> {
                    results[index] = permissionValidator.hasPermission(testUser, testPermission, testResource);
                });
                threads[i].start();
            }

            for (Thread thread : threads) {
                thread.join();
            }

            // Then
            for (boolean result : results) {
                assertTrue(result);
            }
            verify(permissionValidator, times(10)).hasPermission(testUser, testPermission, testResource);
        }
    }

    // ==================== 辅助方法 ====================

    private User createTestUser() {
        User user = new User();
        user.setUserId("user-001");
        user.setTenantId("tenant-001");
        user.setUsername("testuser");
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

    private Resource createTestResource() {
        Resource resource = new Resource();
        resource.setResourceId("user-001");
        resource.setTenantId("tenant-001");
        resource.setType("user");
        resource.setName("测试资源");
        return resource;
    }
}
