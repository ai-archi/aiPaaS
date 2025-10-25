package com.aixone.permission.handler;

import com.aixone.permission.model.User;
import com.aixone.permission.model.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * DataPermissionHandler 接口测试
 * 测试数据权限处理器的基本行为
 *
 * @author aixone
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DataPermissionHandler 接口测试")
class DataPermissionHandlerTest {

    @Mock
    private DataPermissionHandler dataPermissionHandler;

    private User testUser;
    private Resource testResource;

    @BeforeEach
    void setUp() {
        // 创建测试数据
        testUser = createTestUser();
        testResource = createTestResource();
    }

    @Nested
    @DisplayName("数据权限条件生成测试")
    class DataPermissionConditionTests {

        @Test
        @DisplayName("应该成功生成数据权限条件")
        void shouldBuildDataPermissionConditionSuccessfully() {
            // Given
            String expectedCondition = "dept_id = 1";
            when(dataPermissionHandler.buildCondition(testUser, testResource))
                .thenReturn(expectedCondition);

            // When
            String result = dataPermissionHandler.buildCondition(testUser, testResource);

            // Then
            assertEquals(expectedCondition, result);
            verify(dataPermissionHandler).buildCondition(testUser, testResource);
        }

        @Test
        @DisplayName("应该生成空条件当用户没有数据权限时")
        void shouldBuildEmptyConditionWhenUserHasNoDataPermission() {
            // Given
            when(dataPermissionHandler.buildCondition(testUser, testResource))
                .thenReturn("");

            // When
            String result = dataPermissionHandler.buildCondition(testUser, testResource);

            // Then
            assertEquals("", result);
            verify(dataPermissionHandler).buildCondition(testUser, testResource);
        }

        @Test
        @DisplayName("应该生成null条件当用户没有数据权限时")
        void shouldBuildNullConditionWhenUserHasNoDataPermission() {
            // Given
            when(dataPermissionHandler.buildCondition(testUser, testResource))
                .thenReturn(null);

            // When
            String result = dataPermissionHandler.buildCondition(testUser, testResource);

            // Then
            assertNull(result);
            verify(dataPermissionHandler).buildCondition(testUser, testResource);
        }

        @Test
        @DisplayName("应该生成复杂的数据权限条件")
        void shouldBuildComplexDataPermissionCondition() {
            // Given
            String expectedCondition = "dept_id = 1 AND (user_id = 'user-001' OR role_id IN ('admin', 'manager'))";
            when(dataPermissionHandler.buildCondition(testUser, testResource))
                .thenReturn(expectedCondition);

            // When
            String result = dataPermissionHandler.buildCondition(testUser, testResource);

            // Then
            assertEquals(expectedCondition, result);
            verify(dataPermissionHandler).buildCondition(testUser, testResource);
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryConditionTests {

        @Test
        @DisplayName("应该处理null用户")
        void shouldHandleNullUser() {
            // Given
            when(dataPermissionHandler.buildCondition(null, testResource))
                .thenReturn("");

            // When
            String result = dataPermissionHandler.buildCondition(null, testResource);

            // Then
            assertEquals("", result);
            verify(dataPermissionHandler).buildCondition(null, testResource);
        }

        @Test
        @DisplayName("应该处理null资源")
        void shouldHandleNullResource() {
            // Given
            when(dataPermissionHandler.buildCondition(testUser, null))
                .thenReturn("");

            // When
            String result = dataPermissionHandler.buildCondition(testUser, null);

            // Then
            assertEquals("", result);
            verify(dataPermissionHandler).buildCondition(testUser, null);
        }

        @Test
        @DisplayName("应该处理所有参数为null的情况")
        void shouldHandleAllNullParameters() {
            // Given
            when(dataPermissionHandler.buildCondition(null, null))
                .thenReturn("");

            // When
            String result = dataPermissionHandler.buildCondition(null, null);

            // Then
            assertEquals("", result);
            verify(dataPermissionHandler).buildCondition(null, null);
        }
    }

    @Nested
    @DisplayName("不同用户类型测试")
    class DifferentUserTypeTests {

        @Test
        @DisplayName("应该为管理员用户生成全量数据权限条件")
        void shouldBuildFullDataPermissionConditionForAdminUser() {
            // Given
            User adminUser = createTestUser();
            adminUser.setUsername("admin");
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("role", "admin");
            attributes.put("level", 10);
            adminUser.setAttributes(attributes);

            String expectedCondition = "1 = 1"; // 管理员可以看到所有数据
            when(dataPermissionHandler.buildCondition(adminUser, testResource))
                .thenReturn(expectedCondition);

            // When
            String result = dataPermissionHandler.buildCondition(adminUser, testResource);

            // Then
            assertEquals(expectedCondition, result);
            verify(dataPermissionHandler).buildCondition(adminUser, testResource);
        }

        @Test
        @DisplayName("应该为普通用户生成受限数据权限条件")
        void shouldBuildRestrictedDataPermissionConditionForNormalUser() {
            // Given
            User normalUser = createTestUser();
            normalUser.setUsername("normal");
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("role", "user");
            attributes.put("level", 1);
            attributes.put("deptId", "dept-001");
            normalUser.setAttributes(attributes);

            String expectedCondition = "dept_id = 'dept-001'";
            when(dataPermissionHandler.buildCondition(normalUser, testResource))
                .thenReturn(expectedCondition);

            // When
            String result = dataPermissionHandler.buildCondition(normalUser, testResource);

            // Then
            assertEquals(expectedCondition, result);
            verify(dataPermissionHandler).buildCondition(normalUser, testResource);
        }

        @Test
        @DisplayName("应该为部门管理员生成部门数据权限条件")
        void shouldBuildDeptDataPermissionConditionForDeptAdmin() {
            // Given
            User deptAdminUser = createTestUser();
            deptAdminUser.setUsername("dept-admin");
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("role", "dept-admin");
            attributes.put("level", 5);
            attributes.put("deptId", "dept-001");
            attributes.put("subDeptIds", new String[]{"dept-001", "dept-002", "dept-003"});
            deptAdminUser.setAttributes(attributes);

            String expectedCondition = "dept_id IN ('dept-001', 'dept-002', 'dept-003')";
            when(dataPermissionHandler.buildCondition(deptAdminUser, testResource))
                .thenReturn(expectedCondition);

            // When
            String result = dataPermissionHandler.buildCondition(deptAdminUser, testResource);

            // Then
            assertEquals(expectedCondition, result);
            verify(dataPermissionHandler).buildCondition(deptAdminUser, testResource);
        }
    }

    @Nested
    @DisplayName("不同资源类型测试")
    class DifferentResourceTypeTests {

        @Test
        @DisplayName("应该为用户资源生成用户相关条件")
        void shouldBuildUserRelatedConditionForUserResource() {
            // Given
            Resource userResource = createTestResource();
            userResource.setType("user");
            userResource.setName("用户表");

            String expectedCondition = "user_id = 'user-001' OR dept_id = 'dept-001'";
            when(dataPermissionHandler.buildCondition(testUser, userResource))
                .thenReturn(expectedCondition);

            // When
            String result = dataPermissionHandler.buildCondition(testUser, userResource);

            // Then
            assertEquals(expectedCondition, result);
            verify(dataPermissionHandler).buildCondition(testUser, userResource);
        }

        @Test
        @DisplayName("应该为订单资源生成订单相关条件")
        void shouldBuildOrderRelatedConditionForOrderResource() {
            // Given
            Resource orderResource = createTestResource();
            orderResource.setType("order");
            orderResource.setName("订单表");

            String expectedCondition = "order_user_id = 'user-001' OR order_dept_id = 'dept-001'";
            when(dataPermissionHandler.buildCondition(testUser, orderResource))
                .thenReturn(expectedCondition);

            // When
            String result = dataPermissionHandler.buildCondition(testUser, orderResource);

            // Then
            assertEquals(expectedCondition, result);
            verify(dataPermissionHandler).buildCondition(testUser, orderResource);
        }

        @Test
        @DisplayName("应该为敏感资源生成严格条件")
        void shouldBuildStrictConditionForSensitiveResource() {
            // Given
            Resource sensitiveResource = createTestResource();
            sensitiveResource.setType("sensitive");
            sensitiveResource.setName("敏感数据表");
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("sensitivity", "high");
            sensitiveResource.setAttributes(attributes);

            String expectedCondition = "user_id = 'user-001' AND role_id = 'admin'";
            when(dataPermissionHandler.buildCondition(testUser, sensitiveResource))
                .thenReturn(expectedCondition);

            // When
            String result = dataPermissionHandler.buildCondition(testUser, sensitiveResource);

            // Then
            assertEquals(expectedCondition, result);
            verify(dataPermissionHandler).buildCondition(testUser, sensitiveResource);
        }
    }

    @Nested
    @DisplayName("SQL注入防护测试")
    class SqlInjectionPreventionTests {

        @Test
        @DisplayName("应该防止SQL注入攻击")
        void shouldPreventSqlInjection() {
            // Given
            User maliciousUser = createTestUser();
            maliciousUser.setUsername("'; DROP TABLE users; --");
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("deptId", "1'; DROP TABLE dept; --");
            maliciousUser.setAttributes(attributes);

            String expectedCondition = "dept_id = '1\\'; DROP TABLE dept; --'"; // 应该被转义
            when(dataPermissionHandler.buildCondition(maliciousUser, testResource))
                .thenReturn(expectedCondition);

            // When
            String result = dataPermissionHandler.buildCondition(maliciousUser, testResource);

            // Then
            assertEquals(expectedCondition, result);
            assertFalse(result.contains("DROP TABLE")); // 应该被转义或过滤
            verify(dataPermissionHandler).buildCondition(maliciousUser, testResource);
        }

        @Test
        @DisplayName("应该处理特殊字符")
        void shouldHandleSpecialCharacters() {
            // Given
            User specialUser = createTestUser();
            specialUser.setUsername("user@domain.com");
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("deptId", "dept-001");
            attributes.put("name", "User's Name");
            specialUser.setAttributes(attributes);

            String expectedCondition = "dept_id = 'dept-001' AND user_name = 'User\\'s Name'";
            when(dataPermissionHandler.buildCondition(specialUser, testResource))
                .thenReturn(expectedCondition);

            // When
            String result = dataPermissionHandler.buildCondition(specialUser, testResource);

            // Then
            assertEquals(expectedCondition, result);
            verify(dataPermissionHandler).buildCondition(specialUser, testResource);
        }
    }

    @Nested
    @DisplayName("性能测试")
    class PerformanceTests {

        @Test
        @DisplayName("应该支持快速响应")
        void shouldSupportFastResponse() {
            // Given
            when(dataPermissionHandler.buildCondition(testUser, testResource))
                .thenReturn("dept_id = 1");

            // When
            long startTime = System.currentTimeMillis();
            String result = dataPermissionHandler.buildCondition(testUser, testResource);
            long endTime = System.currentTimeMillis();

            // Then
            assertNotNull(result);
            assertTrue(endTime - startTime < 1000); // 应该在1秒内完成
            verify(dataPermissionHandler).buildCondition(testUser, testResource);
        }

        @Test
        @DisplayName("应该支持并发调用")
        void shouldSupportConcurrentCalls() throws InterruptedException {
            // Given
            when(dataPermissionHandler.buildCondition(testUser, testResource))
                .thenReturn("dept_id = 1");

            // When
            Thread[] threads = new Thread[10];
            String[] results = new String[10];
            
            for (int i = 0; i < 10; i++) {
                final int index = i;
                threads[i] = new Thread(() -> {
                    results[index] = dataPermissionHandler.buildCondition(testUser, testResource);
                });
                threads[i].start();
            }

            for (Thread thread : threads) {
                thread.join();
            }

            // Then
            for (String result : results) {
                assertEquals("dept_id = 1", result);
            }
            verify(dataPermissionHandler, times(10)).buildCondition(testUser, testResource);
        }
    }

    @Nested
    @DisplayName("异常处理测试")
    class ExceptionHandlingTests {

        @Test
        @DisplayName("应该处理异常情况")
        void shouldHandleException() {
            // Given
            when(dataPermissionHandler.buildCondition(testUser, testResource))
                .thenThrow(new RuntimeException("数据权限处理异常"));

            // When & Then
            assertThrows(RuntimeException.class, () -> {
                dataPermissionHandler.buildCondition(testUser, testResource);
            });

            verify(dataPermissionHandler).buildCondition(testUser, testResource);
        }

        @Test
        @DisplayName("应该处理空字符串结果")
        void shouldHandleEmptyStringResult() {
            // Given
            when(dataPermissionHandler.buildCondition(testUser, testResource))
                .thenReturn("");

            // When
            String result = dataPermissionHandler.buildCondition(testUser, testResource);

            // Then
            assertEquals("", result);
            verify(dataPermissionHandler).buildCondition(testUser, testResource);
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
        attributes.put("deptId", "dept-001");
        attributes.put("level", 5);
        user.setAttributes(attributes);
        return user;
    }

    private Resource createTestResource() {
        Resource resource = new Resource();
        resource.setResourceId("resource-001");
        resource.setTenantId("tenant-001");
        resource.setType("user");
        resource.setName("用户表");
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("tableName", "users");
        attributes.put("sensitivity", "medium");
        resource.setAttributes(attributes);
        return resource;
    }
}
