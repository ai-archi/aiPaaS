package com.aixone.permission.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Role模型单元测试
 *
 * @author aixone
 */
@DisplayName("Role模型测试")
class RoleTest {

    private Role role;
    private String testTenantId;
    private String testRoleId;

    @BeforeEach
    void setUp() {
        testTenantId = "tenant-001";
        testRoleId = UUID.randomUUID().toString();
        role = new Role();
    }

    @Test
    @DisplayName("测试默认构造函数")
    void testDefaultConstructor() {
        assertNotNull(role);
        assertNull(role.getRoleId());
        assertNull(role.getTenantId());
        assertNull(role.getName());
        assertNull(role.getDescription());
        assertNull(role.getPermissionIds());
        assertNull(role.getPermissions());
        assertNull(role.getCreatedAt());
        assertNull(role.getUpdatedAt());
    }

    @Test
    @DisplayName("测试带参数构造函数")
    void testParameterizedConstructor() {
        String name = "管理员";
        String description = "系统管理员角色";
        List<String> permissionIds = Arrays.asList("perm1", "perm2", "perm3");

        Role newRole = new Role(testRoleId, testTenantId, name, description, permissionIds);

        assertEquals(testRoleId, newRole.getRoleId());
        assertEquals(testTenantId, newRole.getTenantId());
        assertEquals(name, newRole.getName());
        assertEquals(description, newRole.getDescription());
        assertEquals(permissionIds, newRole.getPermissionIds());
    }

    @Test
    @DisplayName("测试setter和getter方法")
    void testSettersAndGetters() {
        String name = "用户角色";
        String description = "普通用户角色";
        List<String> permissionIds = Arrays.asList("user:read", "user:update");
        List<Permission> permissions = Arrays.asList(
            new Permission("perm1", testTenantId, "用户查看", "user", "read", "查看用户", Permission.PermissionLevel.READ),
            new Permission("perm2", testTenantId, "用户更新", "user", "update", "更新用户", Permission.PermissionLevel.WRITE)
        );
        LocalDateTime now = LocalDateTime.now();

        role.setRoleId(testRoleId);
        role.setTenantId(testTenantId);
        role.setName(name);
        role.setDescription(description);
        role.setPermissionIds(permissionIds);
        role.setPermissions(permissions);
        role.setCreatedAt(now);
        role.setUpdatedAt(now);

        assertEquals(testRoleId, role.getRoleId());
        assertEquals(testTenantId, role.getTenantId());
        assertEquals(name, role.getName());
        assertEquals(description, role.getDescription());
        assertEquals(permissionIds, role.getPermissionIds());
        assertEquals(permissions, role.getPermissions());
        assertEquals(now, role.getCreatedAt());
        assertEquals(now, role.getUpdatedAt());
    }

    @Test
    @DisplayName("测试equals和hashCode方法")
    void testEqualsAndHashCode() {
        Role role1 = new Role(testRoleId, testTenantId, "角色1", "描述1", Arrays.asList("perm1"));
        Role role2 = new Role(testRoleId, testTenantId, "角色2", "描述2", Arrays.asList("perm2"));
        Role role3 = new Role("different-id", testTenantId, "角色1", "描述1", Arrays.asList("perm1"));

        // 相同ID的角色应该相等
        assertEquals(role1, role2);
        assertEquals(role1.hashCode(), role2.hashCode());

        // 不同ID的角色应该不相等
        assertNotEquals(role1, role3);
        assertNotEquals(role1.hashCode(), role3.hashCode());

        // 与null比较
        assertNotEquals(role1, null);

        // 与不同类型比较
        assertNotEquals(role1, "string");
    }

    @Test
    @DisplayName("测试toString方法")
    void testToString() {
        role.setRoleId(testRoleId);
        role.setName("测试角色");
        role.setDescription("测试描述");

        String toString = role.toString();
        assertTrue(toString.contains(testRoleId));
        assertTrue(toString.contains("测试角色"));
        assertTrue(toString.contains("测试描述"));
    }

    @Test
    @DisplayName("测试权限ID列表操作")
    void testPermissionIdsOperations() {
        List<String> permissionIds = Arrays.asList("perm1", "perm2", "perm3");
        role.setPermissionIds(permissionIds);

        assertEquals(3, role.getPermissionIds().size());
        assertTrue(role.getPermissionIds().contains("perm1"));
        assertTrue(role.getPermissionIds().contains("perm2"));
        assertTrue(role.getPermissionIds().contains("perm3"));

        // 测试空列表
        role.setPermissionIds(Collections.emptyList());
        assertTrue(role.getPermissionIds().isEmpty());

        // 测试null列表
        role.setPermissionIds(null);
        assertNull(role.getPermissionIds());
    }

    @Test
    @DisplayName("测试权限对象列表操作")
    void testPermissionsOperations() {
        Permission perm1 = new Permission("perm1", testTenantId, "权限1", "resource1", "action1", "描述1", Permission.PermissionLevel.READ);
        Permission perm2 = new Permission("perm2", testTenantId, "权限2", "resource2", "action2", "描述2", Permission.PermissionLevel.WRITE);
        List<Permission> permissions = Arrays.asList(perm1, perm2);

        role.setPermissions(permissions);

        assertEquals(2, role.getPermissions().size());
        assertTrue(role.getPermissions().contains(perm1));
        assertTrue(role.getPermissions().contains(perm2));

        // 测试空列表
        role.setPermissions(Collections.emptyList());
        assertTrue(role.getPermissions().isEmpty());

        // 测试null列表
        role.setPermissions(null);
        assertNull(role.getPermissions());
    }

    @Test
    @DisplayName("测试角色名称验证")
    void testRoleNameValidation() {
        // 测试正常名称
        role.setName("管理员");
        assertEquals("管理员", role.getName());

        // 测试空字符串
        role.setName("");
        assertEquals("", role.getName());

        // 测试null
        role.setName(null);
        assertNull(role.getName());

        // 测试长名称
        String longName = "这是一个非常长的角色名称，用于测试边界情况";
        role.setName(longName);
        assertEquals(longName, role.getName());
    }

    @Test
    @DisplayName("测试描述信息")
    void testDescription() {
        // 测试正常描述
        String description = "这是一个角色描述";
        role.setDescription(description);
        assertEquals(description, role.getDescription());

        // 测试空描述
        role.setDescription("");
        assertEquals("", role.getDescription());

        // 测试null描述
        role.setDescription(null);
        assertNull(role.getDescription());

        // 测试长描述
        String longDescription = "这是一个非常长的角色描述，用于测试边界情况，包含了很多详细信息";
        role.setDescription(longDescription);
        assertEquals(longDescription, role.getDescription());
    }

    @Test
    @DisplayName("测试时间戳")
    void testTimestamps() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(1);

        role.setCreatedAt(now);
        role.setUpdatedAt(future);

        assertEquals(now, role.getCreatedAt());
        assertEquals(future, role.getUpdatedAt());

        // 测试null时间戳
        role.setCreatedAt(null);
        role.setUpdatedAt(null);

        assertNull(role.getCreatedAt());
        assertNull(role.getUpdatedAt());
    }

    @Test
    @DisplayName("测试角色权限关联")
    void testRolePermissionAssociation() {
        // 创建权限
        Permission perm1 = new Permission("perm1", testTenantId, "权限1", "resource1", "action1", "描述1", Permission.PermissionLevel.READ);
        Permission perm2 = new Permission("perm2", testTenantId, "权限2", "resource2", "action2", "描述2", Permission.PermissionLevel.WRITE);

        // 设置权限ID列表
        List<String> permissionIds = Arrays.asList("perm1", "perm2");
        role.setPermissionIds(permissionIds);

        // 设置权限对象列表
        List<Permission> permissions = Arrays.asList(perm1, perm2);
        role.setPermissions(permissions);

        // 验证关联
        assertEquals(2, role.getPermissionIds().size());
        assertEquals(2, role.getPermissions().size());
        assertTrue(role.getPermissionIds().contains("perm1"));
        assertTrue(role.getPermissionIds().contains("perm2"));
        assertTrue(role.getPermissions().contains(perm1));
        assertTrue(role.getPermissions().contains(perm2));
    }

    @Test
    @DisplayName("测试边界值")
    void testBoundaryValues() {
        // 测试空字符串
        role.setName("");
        role.setDescription("");
        assertEquals("", role.getName());
        assertEquals("", role.getDescription());

        // 测试null值
        role.setName(null);
        role.setDescription(null);
        role.setPermissionIds(null);
        role.setPermissions(null);
        assertNull(role.getName());
        assertNull(role.getDescription());
        assertNull(role.getPermissionIds());
        assertNull(role.getPermissions());
    }
}
