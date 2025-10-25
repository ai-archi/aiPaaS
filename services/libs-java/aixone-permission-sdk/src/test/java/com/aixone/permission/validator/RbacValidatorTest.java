package com.aixone.permission.validator;

import com.aixone.permission.model.*;
import com.aixone.permission.provider.UserPermissionProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * RbacValidator 单元测试
 *
 * @author aixone
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RbacValidator 单元测试")
class RbacValidatorTest {

    @Mock
    private UserPermissionProvider userPermissionProvider;

    private RbacValidator rbacValidator;
    private User testUser;
    private Permission testPermission;
    private Resource testResource;
    private Role testRole;

    @BeforeEach
    void setUp() {
        rbacValidator = new RbacValidator(userPermissionProvider);
        
        // 创建测试数据
        testUser = createTestUser();
        testPermission = createTestPermission();
        testResource = createTestResource();
        testRole = createTestRole();
    }

    @Nested
    @DisplayName("权限检查测试")
    class PermissionCheckTests {

        @Test
        @DisplayName("应该成功检查用户权限")
        void shouldCheckUserPermissionSuccessfully() {
            // Given
            List<Permission> userPermissions = Arrays.asList(testPermission);
            when(userPermissionProvider.getUserPermissions(testUser.getTenantId(), testUser.getUserId()))
                .thenReturn(userPermissions);

            // When
            boolean result = rbacValidator.hasPermission(testUser, testPermission, testResource);

            // Then
            assertTrue(result);
            verify(userPermissionProvider).getUserPermissions(testUser.getTenantId(), testUser.getUserId());
        }

        @Test
        @DisplayName("应该拒绝空用户或权限的访问")
        void shouldDenyAccessForNullUserOrPermission() {
            // When & Then
            assertFalse(rbacValidator.hasPermission(null, testPermission, testResource));
            assertFalse(rbacValidator.hasPermission(testUser, null, testResource));
            assertFalse(rbacValidator.hasPermission(null, null, testResource));
        }

        @Test
        @DisplayName("应该拒绝没有权限的用户")
        void shouldDenyUserWithoutPermissions() {
            // Given
            when(userPermissionProvider.getUserPermissions(testUser.getTenantId(), testUser.getUserId()))
                .thenReturn(Collections.emptyList());

            // When
            boolean result = rbacValidator.hasPermission(testUser, testPermission, testResource);

            // Then
            assertFalse(result);
            verify(userPermissionProvider).getUserPermissions(testUser.getTenantId(), testUser.getUserId());
        }

        @Test
        @DisplayName("应该拒绝null权限列表的用户")
        void shouldDenyUserWithNullPermissions() {
            // Given
            when(userPermissionProvider.getUserPermissions(testUser.getTenantId(), testUser.getUserId()))
                .thenReturn(null);

            // When
            boolean result = rbacValidator.hasPermission(testUser, testPermission, testResource);

            // Then
            assertFalse(result);
            verify(userPermissionProvider).getUserPermissions(testUser.getTenantId(), testUser.getUserId());
        }

        @Test
        @DisplayName("应该拒绝不匹配的权限")
        void shouldDenyNonMatchingPermission() {
            // Given
            Permission differentPermission = createTestPermission();
            differentPermission.setResource("different-resource");
            differentPermission.setAction("different-action");
            
            List<Permission> userPermissions = Arrays.asList(differentPermission);
            when(userPermissionProvider.getUserPermissions(testUser.getTenantId(), testUser.getUserId()))
                .thenReturn(userPermissions);

            // When
            boolean result = rbacValidator.hasPermission(testUser, testPermission, testResource);

            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("应该处理权限检查异常")
        void shouldHandlePermissionCheckException() {
            // Given
            when(userPermissionProvider.getUserPermissions(testUser.getTenantId(), testUser.getUserId()))
                .thenThrow(new RuntimeException("权限检查异常"));

            // When
            boolean result = rbacValidator.hasPermission(testUser, testPermission, testResource);

            // Then
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("资源和操作权限检查测试")
    class ResourceActionPermissionTests {

        @Test
        @DisplayName("应该成功检查资源和操作权限")
        void shouldCheckResourceActionPermissionSuccessfully() {
            // Given
            String resource = "user";
            String action = "read";
            List<Permission> userPermissions = Arrays.asList(testPermission);
            when(userPermissionProvider.getUserPermissions(testUser.getTenantId(), testUser.getUserId()))
                .thenReturn(userPermissions);

            // When
            boolean result = rbacValidator.hasPermission(testUser, resource, action);

            // Then
            assertTrue(result);
            verify(userPermissionProvider).getUserPermissions(testUser.getTenantId(), testUser.getUserId());
        }

        @Test
        @DisplayName("应该拒绝空用户、资源或操作的访问")
        void shouldDenyAccessForNullUserResourceOrAction() {
            // When & Then
            assertFalse(rbacValidator.hasPermission(null, "resource", "action"));
            assertFalse(rbacValidator.hasPermission(testUser, (String) null, "action"));
            assertFalse(rbacValidator.hasPermission(testUser, "resource", (String) null));
            assertFalse(rbacValidator.hasPermission((User) null, (String) null, (String) null));
        }

        @Test
        @DisplayName("应该拒绝不匹配的资源和操作")
        void shouldDenyNonMatchingResourceAndAction() {
            // Given
            String resource = "different-resource";
            String action = "different-action";
            List<Permission> userPermissions = Arrays.asList(testPermission);
            when(userPermissionProvider.getUserPermissions(testUser.getTenantId(), testUser.getUserId()))
                .thenReturn(userPermissions);

            // When
            boolean result = rbacValidator.hasPermission(testUser, resource, action);

            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("应该处理资源和操作权限检查异常")
        void shouldHandleResourceActionPermissionCheckException() {
            // Given
            when(userPermissionProvider.getUserPermissions(testUser.getTenantId(), testUser.getUserId()))
                .thenThrow(new RuntimeException("权限检查异常"));

            // When
            boolean result = rbacValidator.hasPermission(testUser, "resource", "action");

            // Then
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("角色检查测试")
    class RoleCheckTests {

        @Test
        @DisplayName("应该成功检查用户角色")
        void shouldCheckUserRoleSuccessfully() {
            // Given
            String roleName = "admin";
            List<Role> userRoles = Arrays.asList(testRole);
            when(userPermissionProvider.getUserRoles(testUser.getTenantId(), testUser.getUserId()))
                .thenReturn(userRoles);

            // When
            boolean result = rbacValidator.hasRole(testUser, roleName);

            // Then
            assertTrue(result);
            verify(userPermissionProvider).getUserRoles(testUser.getTenantId(), testUser.getUserId());
        }

        @Test
        @DisplayName("应该拒绝空用户或角色名的检查")
        void shouldDenyRoleCheckForNullUserOrRoleName() {
            // When & Then
            assertFalse(rbacValidator.hasRole(null, "admin"));
            assertFalse(rbacValidator.hasRole(testUser, null));
            assertFalse(rbacValidator.hasRole(null, null));
        }

        @Test
        @DisplayName("应该拒绝没有角色的用户")
        void shouldDenyUserWithoutRoles() {
            // Given
            when(userPermissionProvider.getUserRoles(testUser.getTenantId(), testUser.getUserId()))
                .thenReturn(Collections.emptyList());

            // When
            boolean result = rbacValidator.hasRole(testUser, "admin");

            // Then
            assertFalse(result);
            verify(userPermissionProvider).getUserRoles(testUser.getTenantId(), testUser.getUserId());
        }

        @Test
        @DisplayName("应该拒绝null角色列表的用户")
        void shouldDenyUserWithNullRoles() {
            // Given
            when(userPermissionProvider.getUserRoles(testUser.getTenantId(), testUser.getUserId()))
                .thenReturn(null);

            // When
            boolean result = rbacValidator.hasRole(testUser, "admin");

            // Then
            assertFalse(result);
            verify(userPermissionProvider).getUserRoles(testUser.getTenantId(), testUser.getUserId());
        }

        @Test
        @DisplayName("应该拒绝不存在的角色")
        void shouldDenyNonExistentRole() {
            // Given
            String roleName = "non-existent";
            List<Role> userRoles = Arrays.asList(testRole);
            when(userPermissionProvider.getUserRoles(testUser.getTenantId(), testUser.getUserId()))
                .thenReturn(userRoles);

            // When
            boolean result = rbacValidator.hasRole(testUser, roleName);

            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("应该处理角色检查异常")
        void shouldHandleRoleCheckException() {
            // Given
            when(userPermissionProvider.getUserRoles(testUser.getTenantId(), testUser.getUserId()))
                .thenThrow(new RuntimeException("角色检查异常"));

            // When
            boolean result = rbacValidator.hasRole(testUser, "admin");

            // Then
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryConditionTests {

        @Test
        @DisplayName("应该处理多个权限的情况")
        void shouldHandleMultiplePermissions() {
            // Given
            Permission perm1 = createTestPermission();
            perm1.setResource("user");
            perm1.setAction("read");
            
            Permission perm2 = createTestPermission();
            perm2.setResource("user");
            perm2.setAction("write");
            
            Permission perm3 = createTestPermission();
            perm3.setResource("order");
            perm3.setAction("read");
            
            List<Permission> userPermissions = Arrays.asList(perm1, perm2, perm3);
            when(userPermissionProvider.getUserPermissions(testUser.getTenantId(), testUser.getUserId()))
                .thenReturn(userPermissions);

            // When & Then
            assertTrue(rbacValidator.hasPermission(testUser, "user", "read"));
            assertTrue(rbacValidator.hasPermission(testUser, "user", "write"));
            assertTrue(rbacValidator.hasPermission(testUser, "order", "read"));
            assertFalse(rbacValidator.hasPermission(testUser, "order", "write"));
            assertFalse(rbacValidator.hasPermission(testUser, "product", "read"));
        }

        @Test
        @DisplayName("应该处理多个角色的情况")
        void shouldHandleMultipleRoles() {
            // Given
            Role role1 = createTestRole();
            role1.setName("admin");
            
            Role role2 = createTestRole();
            role2.setName("user");
            
            Role role3 = createTestRole();
            role3.setName("guest");
            
            List<Role> userRoles = Arrays.asList(role1, role2, role3);
            when(userPermissionProvider.getUserRoles(testUser.getTenantId(), testUser.getUserId()))
                .thenReturn(userRoles);

            // When & Then
            assertTrue(rbacValidator.hasRole(testUser, "admin"));
            assertTrue(rbacValidator.hasRole(testUser, "user"));
            assertTrue(rbacValidator.hasRole(testUser, "guest"));
            assertFalse(rbacValidator.hasRole(testUser, "manager"));
        }

        @Test
        @DisplayName("应该处理大小写敏感的角色名")
        void shouldHandleCaseSensitiveRoleNames() {
            // Given
            Role role = createTestRole();
            role.setName("Admin");
            List<Role> userRoles = Arrays.asList(role);
            when(userPermissionProvider.getUserRoles(testUser.getTenantId(), testUser.getUserId()))
                .thenReturn(userRoles);

            // When & Then
            assertTrue(rbacValidator.hasRole(testUser, "Admin"));
            assertFalse(rbacValidator.hasRole(testUser, "admin"));
            assertFalse(rbacValidator.hasRole(testUser, "ADMIN"));
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
        resource.setType("user");
        resource.setName("测试资源");
        return resource;
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
}
