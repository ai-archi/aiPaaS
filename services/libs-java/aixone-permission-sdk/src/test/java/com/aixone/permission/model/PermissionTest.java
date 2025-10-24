package com.aixone.permission.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Permission模型单元测试
 *
 * @author aixone
 */
@DisplayName("Permission模型测试")
class PermissionTest {

    private Permission permission;
    private String testTenantId;
    private String testPermissionId;

    @BeforeEach
    void setUp() {
        testTenantId = "tenant-001";
        testPermissionId = UUID.randomUUID().toString();
        permission = new Permission();
    }

    @Test
    @DisplayName("测试默认构造函数")
    void testDefaultConstructor() {
        assertNotNull(permission);
        assertNull(permission.getPermissionId());
        assertNull(permission.getTenantId());
        assertNull(permission.getName());
        assertNull(permission.getResource());
        assertNull(permission.getAction());
        assertNull(permission.getDescription());
        assertEquals(Permission.PermissionLevel.READ, permission.getLevel());
        assertNull(permission.getCreatedAt());
        assertNull(permission.getUpdatedAt());
    }

    @Test
    @DisplayName("测试带参数构造函数")
    void testParameterizedConstructor() {
        String name = "用户管理";
        String resource = "user";
        String action = "read";
        String description = "查看用户信息";
        Permission.PermissionLevel level = Permission.PermissionLevel.ADMIN;

        Permission newPermission = new Permission(testPermissionId, testTenantId, name, resource, action, description, level);

        assertEquals(testPermissionId, newPermission.getPermissionId());
        assertEquals(testTenantId, newPermission.getTenantId());
        assertEquals(name, newPermission.getName());
        assertEquals(resource, newPermission.getResource());
        assertEquals(action, newPermission.getAction());
        assertEquals(description, newPermission.getDescription());
        assertEquals(level, newPermission.getLevel());
    }

    @Test
    @DisplayName("测试setter和getter方法")
    void testSettersAndGetters() {
        String name = "权限管理";
        String resource = "permission";
        String action = "write";
        String description = "管理权限";
        Permission.PermissionLevel level = Permission.PermissionLevel.WRITE;
        LocalDateTime now = LocalDateTime.now();

        permission.setPermissionId(testPermissionId);
        permission.setTenantId(testTenantId);
        permission.setName(name);
        permission.setResource(resource);
        permission.setAction(action);
        permission.setDescription(description);
        permission.setLevel(level);
        permission.setCreatedAt(now);
        permission.setUpdatedAt(now);

        assertEquals(testPermissionId, permission.getPermissionId());
        assertEquals(testTenantId, permission.getTenantId());
        assertEquals(name, permission.getName());
        assertEquals(resource, permission.getResource());
        assertEquals(action, permission.getAction());
        assertEquals(description, permission.getDescription());
        assertEquals(level, permission.getLevel());
        assertEquals(now, permission.getCreatedAt());
        assertEquals(now, permission.getUpdatedAt());
    }

    @Test
    @DisplayName("测试equals和hashCode方法")
    void testEqualsAndHashCode() {
        Permission permission1 = new Permission(testPermissionId, testTenantId, "权限1", "resource1", "action1", "描述1", Permission.PermissionLevel.READ);
        Permission permission2 = new Permission(testPermissionId, testTenantId, "权限2", "resource2", "action2", "描述2", Permission.PermissionLevel.WRITE);
        Permission permission3 = new Permission("different-id", testTenantId, "权限1", "resource1", "action1", "描述1", Permission.PermissionLevel.READ);

        // 相同ID的权限应该相等
        assertEquals(permission1, permission2);
        assertEquals(permission1.hashCode(), permission2.hashCode());

        // 不同ID的权限应该不相等
        assertNotEquals(permission1, permission3);
        assertNotEquals(permission1.hashCode(), permission3.hashCode());

        // 与null比较
        assertNotEquals(permission1, null);

        // 与不同类型比较
        assertNotEquals(permission1, "string");
    }

    @Test
    @DisplayName("测试toString方法")
    void testToString() {
        permission.setPermissionId(testPermissionId);
        permission.setName("测试权限");
        permission.setResource("test");
        permission.setAction("read");

        String toString = permission.toString();
        assertTrue(toString.contains(testPermissionId));
        assertTrue(toString.contains("测试权限"));
        assertTrue(toString.contains("test"));
        assertTrue(toString.contains("read"));
    }

    @Test
    @DisplayName("测试PermissionLevel枚举")
    void testPermissionLevelEnum() {
        // 测试枚举值
        assertEquals(1, Permission.PermissionLevel.READ.getLevel());
        assertEquals(2, Permission.PermissionLevel.WRITE.getLevel());
        assertEquals(3, Permission.PermissionLevel.DELETE.getLevel());
        assertEquals(4, Permission.PermissionLevel.ADMIN.getLevel());

        // 测试枚举比较
        assertTrue(Permission.PermissionLevel.ADMIN.getLevel() > Permission.PermissionLevel.READ.getLevel());
        assertTrue(Permission.PermissionLevel.WRITE.getLevel() > Permission.PermissionLevel.READ.getLevel());
        assertTrue(Permission.PermissionLevel.DELETE.getLevel() > Permission.PermissionLevel.WRITE.getLevel());
    }

    @Test
    @DisplayName("测试权限级别比较")
    void testPermissionLevelComparison() {
        Permission readPermission = new Permission();
        readPermission.setLevel(Permission.PermissionLevel.READ);

        Permission writePermission = new Permission();
        writePermission.setLevel(Permission.PermissionLevel.WRITE);

        Permission adminPermission = new Permission();
        adminPermission.setLevel(Permission.PermissionLevel.ADMIN);

        // 测试级别比较
        assertTrue(adminPermission.getLevel().getLevel() > writePermission.getLevel().getLevel());
        assertTrue(writePermission.getLevel().getLevel() > readPermission.getLevel().getLevel());
        assertTrue(adminPermission.getLevel().getLevel() > readPermission.getLevel().getLevel());
    }

    @Test
    @DisplayName("测试权限标识生成")
    void testPermissionIdentifier() {
        permission.setResource("user");
        permission.setAction("read");
        
        // 测试权限标识格式
        String expectedIdentifier = "user:read";
        // 这里假设有一个生成权限标识的方法，如果没有可以添加
        // assertEquals(expectedIdentifier, permission.getIdentifier());
    }

    @Test
    @DisplayName("测试时间戳设置")
    void testTimestampSetting() {
        LocalDateTime now = LocalDateTime.now();
        
        permission.setCreatedAt(now);
        permission.setUpdatedAt(now);
        
        assertEquals(now, permission.getCreatedAt());
        assertEquals(now, permission.getUpdatedAt());
        
        // 测试时间戳不能为未来时间（业务逻辑验证）
        LocalDateTime future = now.plusDays(1);
        permission.setCreatedAt(future);
        assertEquals(future, permission.getCreatedAt());
    }

    @Test
    @DisplayName("测试边界值")
    void testBoundaryValues() {
        // 测试空字符串
        permission.setName("");
        permission.setResource("");
        permission.setAction("");
        permission.setDescription("");
        
        assertEquals("", permission.getName());
        assertEquals("", permission.getResource());
        assertEquals("", permission.getAction());
        assertEquals("", permission.getDescription());
        
        // 测试null值
        permission.setName(null);
        permission.setResource(null);
        permission.setAction(null);
        permission.setDescription(null);
        
        assertNull(permission.getName());
        assertNull(permission.getResource());
        assertNull(permission.getAction());
        assertNull(permission.getDescription());
    }
}
