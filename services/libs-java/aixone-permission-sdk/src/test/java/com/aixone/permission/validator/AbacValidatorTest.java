package com.aixone.permission.validator;

import com.aixone.permission.model.*;
import com.aixone.permission.abac.AbacExpressionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AbacValidator 单元测试
 *
 * @author aixone
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AbacValidator 单元测试")
class AbacValidatorTest {

    @Mock
    private AbacExpressionUtil expressionUtil;

    private AbacValidator abacValidator;
    private User testUser;
    private Permission testPermission;
    private Resource testResource;
    private Policy testPolicy;

    @BeforeEach
    void setUp() {
        abacValidator = new AbacValidator(expressionUtil);
        
        // 创建测试数据
        testUser = createTestUser();
        testPermission = createTestPermission();
        testResource = createTestResource();
        testPolicy = createTestPolicy();
    }

    @Nested
    @DisplayName("权限检查测试")
    class PermissionCheckTests {

        @Test
        @DisplayName("应该成功检查用户权限")
        void shouldCheckUserPermissionSuccessfully() {
            // Given
            when(expressionUtil.evaluate(anyString(), anyMap())).thenReturn(true);

            // When
            boolean result = abacValidator.hasPermission(testUser, testPermission, testResource);

            // Then
            assertTrue(result);
        }

        @Test
        @DisplayName("应该拒绝空用户或权限的访问")
        void shouldDenyAccessForNullUserOrPermission() {
            // When & Then
            assertFalse(abacValidator.hasPermission(null, testPermission, testResource));
            assertFalse(abacValidator.hasPermission(testUser, null, testResource));
            assertFalse(abacValidator.hasPermission(null, null, testResource));
        }

        @Test
        @DisplayName("应该处理权限检查异常")
        void shouldHandlePermissionCheckException() {
            // Given
            when(expressionUtil.evaluate(anyString(), anyMap()))
                .thenThrow(new RuntimeException("表达式评估异常"));

            // When
            boolean result = abacValidator.hasPermission(testUser, testPermission, testResource);

            // Then
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("ABAC策略检查测试")
    class AbacPolicyCheckTests {

        @Test
        @DisplayName("应该成功通过策略检查")
        void shouldPassPolicyCheckSuccessfully() {
            // Given
            Map<String, Object> context = new HashMap<>();
            context.put("user.department", "IT");
            context.put("resource.type", "sensitive");
            
            when(expressionUtil.evaluate(anyString(), anyMap())).thenReturn(true);

            // When
            boolean result = abacValidator.checkAbacPolicies("tenant-001", "resource", "action", context);

            // Then
            assertTrue(result);
        }

        @Test
        @DisplayName("应该拒绝策略检查失败")
        void shouldDenyPolicyCheckFailure() {
            // Given
            Map<String, Object> context = new HashMap<>();
            context.put("user.department", "HR");
            context.put("resource.type", "sensitive");
            
            when(expressionUtil.evaluate(anyString(), anyMap())).thenReturn(false);

            // When
            boolean result = abacValidator.checkAbacPolicies("tenant-001", "resource", "action", context);

            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("应该处理策略检查异常")
        void shouldHandlePolicyCheckException() {
            // Given
            Map<String, Object> context = new HashMap<>();
            when(expressionUtil.evaluate(anyString(), anyMap()))
                .thenThrow(new RuntimeException("策略检查异常"));

            // When
            boolean result = abacValidator.checkAbacPolicies("tenant-001", "resource", "action", context);

            // Then
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("上下文构建测试")
    class ContextBuildingTests {

        @Test
        @DisplayName("应该正确构建用户上下文")
        void shouldBuildUserContextCorrectly() {
            // Given
            Map<String, Object> userAttributes = new HashMap<>();
            userAttributes.put("department", "IT");
            userAttributes.put("level", 5);
            userAttributes.put("active", true);
            testUser.setAttributes(userAttributes);

            // When
            boolean result = abacValidator.hasPermission(testUser, testPermission, testResource);

            // Then
            // 验证表达式工具被调用，并且上下文包含用户属性
            verify(expressionUtil).evaluate(anyString(), argThat(context -> {
                return context.containsKey("user.id") &&
                       context.containsKey("user.tenantId") &&
                       context.containsKey("user.username") &&
                       context.containsKey("user.department") &&
                       context.containsKey("user.level") &&
                       context.containsKey("user.active");
            }));
        }

        @Test
        @DisplayName("应该正确构建资源上下文")
        void shouldBuildResourceContextCorrectly() {
            // Given
            Map<String, Object> resourceAttributes = new HashMap<>();
            resourceAttributes.put("sensitivity", "high");
            resourceAttributes.put("owner", "admin");
            testResource.setAttributes(resourceAttributes);

            // When
            boolean result = abacValidator.hasPermission(testUser, testPermission, testResource);

            // Then
            // 验证表达式工具被调用，并且上下文包含资源属性
            verify(expressionUtil).evaluate(anyString(), argThat(context -> {
                return context.containsKey("resource.id") &&
                       context.containsKey("resource.tenantId") &&
                       context.containsKey("resource.type") &&
                       context.containsKey("resource.name") &&
                       context.containsKey("resource.sensitivity") &&
                       context.containsKey("resource.owner");
            }));
        }

        @Test
        @DisplayName("应该包含环境属性")
        void shouldIncludeEnvironmentAttributes() {
            // When
            boolean result = abacValidator.hasPermission(testUser, testPermission, testResource);

            // Then
            // 验证表达式工具被调用，并且上下文包含环境属性
            verify(expressionUtil).evaluate(anyString(), argThat(context -> {
                return context.containsKey("time") &&
                       context.containsKey("date") &&
                       context.containsKey("timestamp");
            }));
        }

        @Test
        @DisplayName("应该处理null资源")
        void shouldHandleNullResource() {
            // When
            boolean result = abacValidator.hasPermission(testUser, testPermission, null);

            // Then
            // 验证表达式工具被调用，即使资源为null
            verify(expressionUtil).evaluate(anyString(), anyMap());
        }
    }

    @Nested
    @DisplayName("策略评估测试")
    class PolicyEvaluationTests {

        @Test
        @DisplayName("应该处理空策略条件")
        void shouldHandleEmptyPolicyCondition() {
            // Given
            Policy policy = new Policy();
            policy.setCondition("");
            Map<String, Object> context = new HashMap<>();

            // When
            boolean result = abacValidator.checkAbacPolicies("tenant-001", "resource", "action", context);

            // Then
            assertTrue(result);
        }

        @Test
        @DisplayName("应该处理null策略条件")
        void shouldHandleNullPolicyCondition() {
            // Given
            Policy policy = new Policy();
            policy.setCondition(null);
            Map<String, Object> context = new HashMap<>();

            // When
            boolean result = abacValidator.checkAbacPolicies("tenant-001", "resource", "action", context);

            // Then
            assertTrue(result);
        }

        @Test
        @DisplayName("应该处理策略评估异常")
        void shouldHandlePolicyEvaluationException() {
            // Given
            Map<String, Object> context = new HashMap<>();
            when(expressionUtil.evaluate(anyString(), anyMap()))
                .thenThrow(new RuntimeException("策略评估异常"));

            // When
            boolean result = abacValidator.checkAbacPolicies("tenant-001", "resource", "action", context);

            // Then
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryConditionTests {

        @Test
        @DisplayName("应该处理空用户属性")
        void shouldHandleEmptyUserAttributes() {
            // Given
            testUser.setAttributes(new HashMap<>());

            // When
            boolean result = abacValidator.hasPermission(testUser, testPermission, testResource);

            // Then
            // 由于没有ABAC策略，默认返回true，不会调用expressionUtil
            assertTrue(result);
        }

        @Test
        @DisplayName("应该处理null用户属性")
        void shouldHandleNullUserAttributes() {
            // Given
            testUser.setAttributes(null);

            // When
            boolean result = abacValidator.hasPermission(testUser, testPermission, testResource);

            // Then
            // 由于没有ABAC策略，默认返回true，不会调用expressionUtil
            assertTrue(result);
        }

        @Test
        @DisplayName("应该处理空资源属性")
        void shouldHandleEmptyResourceAttributes() {
            // Given
            testResource.setAttributes(new HashMap<>());

            // When
            boolean result = abacValidator.hasPermission(testUser, testPermission, testResource);

            // Then
            // 由于没有ABAC策略，默认返回true，不会调用expressionUtil
            assertTrue(result);
        }

        @Test
        @DisplayName("应该处理null资源属性")
        void shouldHandleNullResourceAttributes() {
            // Given
            testResource.setAttributes(null);

            // When
            boolean result = abacValidator.hasPermission(testUser, testPermission, testResource);

            // Then
            verify(expressionUtil).evaluate(anyString(), argThat(context -> {
                return context.containsKey("resource.id") &&
                       context.containsKey("resource.tenantId") &&
                       context.containsKey("resource.type") &&
                       context.containsKey("resource.name");
            }));
        }
    }

    @Nested
    @DisplayName("复杂场景测试")
    class ComplexScenarioTests {

        @Test
        @DisplayName("应该处理复杂的用户属性")
        void shouldHandleComplexUserAttributes() {
            // Given
            Map<String, Object> userAttributes = new HashMap<>();
            userAttributes.put("department", "IT");
            userAttributes.put("level", 5);
            userAttributes.put("active", true);
            userAttributes.put("roles", Arrays.asList("admin", "user"));
            userAttributes.put("permissions", Collections.singletonMap("read", true));
            testUser.setAttributes(userAttributes);

            // When
            boolean result = abacValidator.hasPermission(testUser, testPermission, testResource);

            // Then
            verify(expressionUtil).evaluate(anyString(), argThat(context -> {
                return context.containsKey("user.department") &&
                       context.containsKey("user.level") &&
                       context.containsKey("user.active") &&
                       context.containsKey("user.roles") &&
                       context.containsKey("user.permissions");
            }));
        }

        @Test
        @DisplayName("应该处理复杂的资源属性")
        void shouldHandleComplexResourceAttributes() {
            // Given
            Map<String, Object> resourceAttributes = new HashMap<>();
            resourceAttributes.put("sensitivity", "high");
            resourceAttributes.put("owner", "admin");
            resourceAttributes.put("tags", Arrays.asList("confidential", "internal"));
            resourceAttributes.put("metadata", Collections.singletonMap("version", "1.0"));
            testResource.setAttributes(resourceAttributes);

            // When
            boolean result = abacValidator.hasPermission(testUser, testPermission, testResource);

            // Then
            verify(expressionUtil).evaluate(anyString(), argThat(context -> {
                return context.containsKey("resource.sensitivity") &&
                       context.containsKey("resource.owner") &&
                       context.containsKey("resource.tags") &&
                       context.containsKey("resource.metadata");
            }));
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
        attributes.put("level", 5);
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

    private Resource createTestResource() {
        Resource resource = new Resource();
        resource.setResourceId("user-001");
        resource.setTenantId("tenant-001");
        resource.setType("user");
        resource.setName("测试资源");
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sensitivity", "medium");
        resource.setAttributes(attributes);
        return resource;
    }

    private Policy createTestPolicy() {
        Policy policy = new Policy();
        policy.setPolicyId("policy-001");
        policy.setTenantId("tenant-001");
        policy.setName("测试策略");
        policy.setDescription("测试策略描述");
        policy.setCondition("user.department == 'IT' && resource.sensitivity == 'medium'");
        // Policy类没有setEffect方法，使用attributes代替
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("effect", "allow");
        policy.setAttributes(attributes);
        return policy;
    }
}
