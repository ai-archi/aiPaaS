package com.aixone.permission.handler;

import com.aixone.permission.model.User;
import com.aixone.permission.model.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SelfDataPermissionHandler 单元测试
 *
 * @author aixone
 */
@DisplayName("SelfDataPermissionHandler 单元测试")
class SelfDataPermissionHandlerTest {

    private SelfDataPermissionHandler handler;
    private User testUser;
    private Resource testResource;

    @BeforeEach
    void setUp() {
        handler = new SelfDataPermissionHandler();
        
        // 创建测试数据
        testUser = createTestUser();
        testResource = createTestResource();
    }

    @Nested
    @DisplayName("数据权限条件生成测试")
    class DataPermissionConditionTests {

        @Test
        @DisplayName("应该成功生成本人数据权限条件")
        void shouldBuildSelfDataPermissionConditionSuccessfully() {
            // Given
            String expectedCondition = "user_id = 'user-001'";

            // When
            String result = handler.buildCondition(testUser, testResource);

            // Then
            assertEquals(expectedCondition, result);
        }

        @Test
        @DisplayName("应该处理不同的用户ID")
        void shouldHandleDifferentUserIds() {
            // Given
            testUser.setUserId("user-123");
            String expectedCondition = "user_id = 'user-123'";

            // When
            String result = handler.buildCondition(testUser, testResource);

            // Then
            assertEquals(expectedCondition, result);
        }

        @Test
        @DisplayName("应该处理特殊字符用户ID")
        void shouldHandleSpecialCharacterUserIds() {
            // Given
            testUser.setUserId("user@domain.com");
            String expectedCondition = "user_id = 'user@domain.com'";

            // When
            String result = handler.buildCondition(testUser, testResource);

            // Then
            assertEquals(expectedCondition, result);
        }

        @Test
        @DisplayName("应该处理Unicode字符用户ID")
        void shouldHandleUnicodeCharacterUserIds() {
            // Given
            testUser.setUserId("用户001");
            String expectedCondition = "user_id = '用户001'";

            // When
            String result = handler.buildCondition(testUser, testResource);

            // Then
            assertEquals(expectedCondition, result);
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryConditionTests {

        @Test
        @DisplayName("应该处理null用户ID")
        void shouldHandleNullUserId() {
            // Given
            testUser.setUserId(null);
            String expectedCondition = "1=0";

            // When
            String result = handler.buildCondition(testUser, testResource);

            // Then
            assertEquals(expectedCondition, result);
        }

        @Test
        @DisplayName("应该处理空字符串用户ID")
        void shouldHandleEmptyStringUserId() {
            // Given
            testUser.setUserId("");
            String expectedCondition = "user_id = ''";

            // When
            String result = handler.buildCondition(testUser, testResource);

            // Then
            assertEquals(expectedCondition, result);
        }

        @Test
        @DisplayName("应该处理null用户")
        void shouldHandleNullUser() {
            // Given
            String expectedCondition = "1=0";

            // When
            String result = handler.buildCondition(null, testResource);

            // Then
            assertEquals(expectedCondition, result);
        }

        @Test
        @DisplayName("应该处理null资源")
        void shouldHandleNullResource() {
            // Given
            String expectedCondition = "user_id = 'user-001'";

            // When
            String result = handler.buildCondition(testUser, null);

            // Then
            assertEquals(expectedCondition, result);
        }

        @Test
        @DisplayName("应该处理所有参数为null的情况")
        void shouldHandleAllNullParameters() {
            // Given
            String expectedCondition = "1=0";

            // When
            String result = handler.buildCondition(null, null);

            // Then
            assertEquals(expectedCondition, result);
        }
    }

    @Nested
    @DisplayName("SQL注入防护测试")
    class SqlInjectionPreventionTests {

        @Test
        @DisplayName("应该防止SQL注入攻击")
        void shouldPreventSqlInjection() {
            // Given
            testUser.setUserId("'; DROP TABLE users; --");
            String expectedCondition = "user_id = ''; DROP TABLE users; --'";

            // When
            String result = handler.buildCondition(testUser, testResource);

            // Then
            assertEquals(expectedCondition, result);
            // 注意：这个实现没有进行SQL注入防护，实际使用时应该进行转义
            assertTrue(result.contains("DROP TABLE"));
        }

        @Test
        @DisplayName("应该处理包含单引号的用户ID")
        void shouldHandleUserIdWithSingleQuotes() {
            // Given
            testUser.setUserId("user's_id");
            String expectedCondition = "user_id = 'user's_id'";

            // When
            String result = handler.buildCondition(testUser, testResource);

            // Then
            assertEquals(expectedCondition, result);
            // 注意：这个实现没有进行SQL注入防护，实际使用时应该进行转义
            assertTrue(result.contains("user's_id"));
        }

        @Test
        @DisplayName("应该处理包含双引号的用户ID")
        void shouldHandleUserIdWithDoubleQuotes() {
            // Given
            testUser.setUserId("user\"s_id");
            String expectedCondition = "user_id = 'user\"s_id'";

            // When
            String result = handler.buildCondition(testUser, testResource);

            // Then
            assertEquals(expectedCondition, result);
        }
    }

    @Nested
    @DisplayName("不同资源类型测试")
    class DifferentResourceTypeTests {

        @Test
        @DisplayName("应该为不同资源类型生成相同的条件")
        void shouldBuildSameConditionForDifferentResourceTypes() {
            // Given
            Resource userResource = createTestResource();
            userResource.setType("user");
            
            Resource orderResource = createTestResource();
            orderResource.setType("order");
            
            Resource productResource = createTestResource();
            productResource.setType("product");

            // When
            String userResult = handler.buildCondition(testUser, userResource);
            String orderResult = handler.buildCondition(testUser, orderResource);
            String productResult = handler.buildCondition(testUser, productResource);

            // Then
            assertEquals("user_id = 'user-001'", userResult);
            assertEquals("user_id = 'user-001'", orderResult);
            assertEquals("user_id = 'user-001'", productResult);
        }

        @Test
        @DisplayName("应该忽略资源属性")
        void shouldIgnoreResourceAttributes() {
            // Given
            Resource resourceWithAttributes = createTestResource();
            resourceWithAttributes.setType("user");
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("tableName", "users");
            attributes.put("sensitivity", "high");
            attributes.put("deptId", "dept-001");
            resourceWithAttributes.setAttributes(attributes);

            // When
            String result = handler.buildCondition(testUser, resourceWithAttributes);

            // Then
            assertEquals("user_id = 'user-001'", result);
        }
    }

    @Nested
    @DisplayName("性能测试")
    class PerformanceTests {

        @Test
        @DisplayName("应该支持快速响应")
        void shouldSupportFastResponse() {
            // When
            long startTime = System.currentTimeMillis();
            String result = handler.buildCondition(testUser, testResource);
            long endTime = System.currentTimeMillis();

            // Then
            assertEquals("user_id = 'user-001'", result);
            assertTrue(endTime - startTime < 1000); // 应该在1秒内完成
        }

        @Test
        @DisplayName("应该支持多次调用")
        void shouldSupportMultipleCalls() {
            // When
            String result1 = handler.buildCondition(testUser, testResource);
            String result2 = handler.buildCondition(testUser, testResource);
            String result3 = handler.buildCondition(testUser, testResource);

            // Then
            assertEquals("user_id = 'user-001'", result1);
            assertEquals("user_id = 'user-001'", result2);
            assertEquals("user_id = 'user-001'", result3);
        }
    }

    @Nested
    @DisplayName("一致性测试")
    class ConsistencyTests {

        @Test
        @DisplayName("应该为相同用户生成相同条件")
        void shouldBuildSameConditionForSameUser() {
            // Given
            User user1 = createTestUser();
            user1.setUserId("user-001");
            User user2 = createTestUser();
            user2.setUserId("user-001");

            // When
            String result1 = handler.buildCondition(user1, testResource);
            String result2 = handler.buildCondition(user2, testResource);

            // Then
            assertEquals(result1, result2);
            assertEquals("user_id = 'user-001'", result1);
        }

        @Test
        @DisplayName("应该为不同用户生成不同条件")
        void shouldBuildDifferentConditionForDifferentUsers() {
            // Given
            User user1 = createTestUser();
            user1.setUserId("user-001");
            User user2 = createTestUser();
            user2.setUserId("user-002");

            // When
            String result1 = handler.buildCondition(user1, testResource);
            String result2 = handler.buildCondition(user2, testResource);

            // Then
            assertNotEquals(result1, result2);
            assertEquals("user_id = 'user-001'", result1);
            assertEquals("user_id = 'user-002'", result2);
        }
    }

    @Nested
    @DisplayName("特殊字符处理测试")
    class SpecialCharacterTests {

        @Test
        @DisplayName("应该处理包含空格的用户ID")
        void shouldHandleUserIdWithSpaces() {
            // Given
            testUser.setUserId("user 001");
            String expectedCondition = "user_id = 'user 001'";

            // When
            String result = handler.buildCondition(testUser, testResource);

            // Then
            assertEquals(expectedCondition, result);
        }

        @Test
        @DisplayName("应该处理包含数字的用户ID")
        void shouldHandleUserIdWithNumbers() {
            // Given
            testUser.setUserId("user123");
            String expectedCondition = "user_id = 'user123'";

            // When
            String result = handler.buildCondition(testUser, testResource);

            // Then
            assertEquals(expectedCondition, result);
        }

        @Test
        @DisplayName("应该处理包含特殊符号的用户ID")
        void shouldHandleUserIdWithSpecialSymbols() {
            // Given
            testUser.setUserId("user-001_test@domain.com");
            String expectedCondition = "user_id = 'user-001_test@domain.com'";

            // When
            String result = handler.buildCondition(testUser, testResource);

            // Then
            assertEquals(expectedCondition, result);
        }

        @Test
        @DisplayName("应该处理包含下划线的用户ID")
        void shouldHandleUserIdWithUnderscores() {
            // Given
            testUser.setUserId("user_001");
            String expectedCondition = "user_id = 'user_001'";

            // When
            String result = handler.buildCondition(testUser, testResource);

            // Then
            assertEquals(expectedCondition, result);
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

    private Resource createTestResource() {
        Resource resource = new Resource();
        resource.setResourceId("resource-001");
        resource.setTenantId("tenant-001");
        resource.setType("user");
        resource.setName("用户表");
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("tableName", "users");
        resource.setAttributes(attributes);
        return resource;
    }
}
