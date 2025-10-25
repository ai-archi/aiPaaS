package com.aixone.permission.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * User模型单元测试
 *
 * @author aixone
 */
@DisplayName("User模型测试")
class UserTest {

    private User user;
    private String testTenantId;
    private String testUserId;

    @BeforeEach
    void setUp() {
        testTenantId = "tenant-001";
        testUserId = UUID.randomUUID().toString();
        user = new User();
    }

    @Test
    @DisplayName("测试默认构造函数")
    void testDefaultConstructor() {
        assertNotNull(user);
        assertNull(user.getUserId());
        assertNull(user.getTenantId());
        assertNull(user.getUsername());
        assertNull(user.getAttributes());
        assertNull(user.getRoleIds());
        assertNull(user.getPermissions());
        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());
    }

    @Test
    @DisplayName("测试带参数构造函数")
    void testParameterizedConstructor() {
        String username = "testuser";
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("department", "IT");
        attributes.put("level", 5);
        List<String> roleIds = Arrays.asList("role1", "role2");

        User newUser = new User(testUserId, testTenantId, username, attributes);
        newUser.setRoleIds(roleIds);

        assertEquals(testUserId, newUser.getUserId());
        assertEquals(testTenantId, newUser.getTenantId());
        assertEquals(username, newUser.getUsername());
        assertEquals(attributes, newUser.getAttributes());
        assertEquals(roleIds, newUser.getRoleIds());
    }

    @Test
    @DisplayName("测试setter和getter方法")
    void testSettersAndGetters() {
        String username = "admin";
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "admin@example.com");
        attributes.put("phone", "1234567890");
        List<String> roleIds = Arrays.asList("admin", "user");
        Permission perm1 = new Permission();
        perm1.setPermissionId("perm1");
        perm1.setTenantId(testTenantId);
        perm1.setName("权限1");
        perm1.setResource("resource1");
        perm1.setAction("action1");
        perm1.setDescription("描述1");
        perm1.setLevel(Permission.PermissionLevel.READ);
        
        Permission perm2 = new Permission();
        perm2.setPermissionId("perm2");
        perm2.setTenantId(testTenantId);
        perm2.setName("权限2");
        perm2.setResource("resource2");
        perm2.setAction("action2");
        perm2.setDescription("描述2");
        perm2.setLevel(Permission.PermissionLevel.WRITE);
        
        List<Permission> permissions = Arrays.asList(perm1, perm2);
        LocalDateTime now = LocalDateTime.now();

        user.setUserId(testUserId);
        user.setTenantId(testTenantId);
        user.setUsername(username);
        user.setAttributes(attributes);
        user.setRoleIds(roleIds);
        user.setPermissions(permissions);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        assertEquals(testUserId, user.getUserId());
        assertEquals(testTenantId, user.getTenantId());
        assertEquals(username, user.getUsername());
        assertEquals(attributes, user.getAttributes());
        assertEquals(roleIds, user.getRoleIds());
        assertEquals(permissions, user.getPermissions());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }

    @Test
    @DisplayName("测试equals和hashCode方法")
    void testEqualsAndHashCode() {
        User user1 = new User(testUserId, testTenantId, "user1", new HashMap<>());
        user1.setRoleIds(Arrays.asList("role1"));
        User user2 = new User(testUserId, testTenantId, "user2", new HashMap<>());
        user2.setRoleIds(Arrays.asList("role2"));
        User user3 = new User("different-id", testTenantId, "user1", new HashMap<>());
        user3.setRoleIds(Arrays.asList("role1"));

        // 相同ID的用户应该相等
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());

        // 不同ID的用户应该不相等
        assertNotEquals(user1, user3);
        assertNotEquals(user1.hashCode(), user3.hashCode());

        // 与null比较
        assertNotEquals(user1, null);

        // 与不同类型比较
        assertNotEquals(user1, "string");
    }

    @Test
    @DisplayName("测试toString方法")
    void testToString() {
        user.setUserId(testUserId);
        user.setUsername("testuser");
        user.setTenantId(testTenantId);

        String toString = user.toString();
        assertTrue(toString.contains(testUserId));
        assertTrue(toString.contains("testuser"));
        assertTrue(toString.contains(testTenantId));
    }

    @Test
    @DisplayName("测试属性操作")
    void testAttributeOperations() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("department", "IT");
        attributes.put("level", 5);
        attributes.put("active", true);

        user.setAttributes(attributes);

        // 测试hasAttribute方法
        assertTrue(user.hasAttribute("department", "IT"));
        assertTrue(user.hasAttribute("level", 5));
        assertTrue(user.hasAttribute("active", true));
        assertFalse(user.hasAttribute("nonexistent", "value"));

        // 测试getAttribute方法
        assertEquals("IT", user.getAttribute("department"));
        assertEquals(5, user.getAttribute("level"));
        assertEquals(true, user.getAttribute("active"));
        assertNull(user.getAttribute("nonexistent"));

        // 测试默认值 - User类没有带默认值的getAttribute方法
        assertNull(user.getAttribute("nonexistent"));
    }

    @Test
    @DisplayName("测试属性设置和获取")
    void testAttributeSettingAndGetting() {
        // 测试设置单个属性 - User类没有setAttribute方法，需要通过attributes Map
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "test@example.com");
        attributes.put("age", 25);
        attributes.put("active", true);
        user.setAttributes(attributes);

        assertEquals("test@example.com", user.getAttribute("email"));
        assertEquals(25, user.getAttribute("age"));
        assertEquals(true, user.getAttribute("active"));

        // 测试更新属性
        attributes.put("age", 26);
        user.setAttributes(attributes);
        assertEquals(26, user.getAttribute("age"));

        // 测试删除属性（设置为null）
        attributes.put("email", null);
        user.setAttributes(attributes);
        assertNull(user.getAttribute("email"));
    }

    @Test
    @DisplayName("测试角色ID列表操作")
    void testRoleIdsOperations() {
        List<String> roleIds = Arrays.asList("admin", "user", "guest");
        user.setRoleIds(roleIds);

        assertEquals(3, user.getRoleIds().size());
        assertTrue(user.getRoleIds().contains("admin"));
        assertTrue(user.getRoleIds().contains("user"));
        assertTrue(user.getRoleIds().contains("guest"));

        // 测试空列表
        user.setRoleIds(Collections.emptyList());
        assertTrue(user.getRoleIds().isEmpty());

        // 测试null列表
        user.setRoleIds(null);
        assertNull(user.getRoleIds());
    }

    @Test
    @DisplayName("测试权限列表操作")
    void testPermissionsOperations() {
        Permission perm1 = new Permission();
        perm1.setPermissionId("perm1");
        perm1.setTenantId(testTenantId);
        perm1.setName("权限1");
        perm1.setResource("resource1");
        perm1.setAction("action1");
        perm1.setDescription("描述1");
        perm1.setLevel(Permission.PermissionLevel.READ);
        
        Permission perm2 = new Permission();
        perm2.setPermissionId("perm2");
        perm2.setTenantId(testTenantId);
        perm2.setName("权限2");
        perm2.setResource("resource2");
        perm2.setAction("action2");
        perm2.setDescription("描述2");
        perm2.setLevel(Permission.PermissionLevel.WRITE);
        List<Permission> permissions = Arrays.asList(perm1, perm2);

        user.setPermissions(permissions);

        assertEquals(2, user.getPermissions().size());
        assertTrue(user.getPermissions().contains(perm1));
        assertTrue(user.getPermissions().contains(perm2));

        // 测试空列表
        user.setPermissions(Collections.emptyList());
        assertTrue(user.getPermissions().isEmpty());

        // 测试null列表
        user.setPermissions(null);
        assertNull(user.getPermissions());
    }

    @Test
    @DisplayName("测试用户名验证")
    void testUsernameValidation() {
        // 测试正常用户名
        user.setUsername("testuser");
        assertEquals("testuser", user.getUsername());

        // 测试空字符串
        user.setUsername("");
        assertEquals("", user.getUsername());

        // 测试null
        user.setUsername(null);
        assertNull(user.getUsername());

        // 测试特殊字符
        user.setUsername("test_user@domain.com");
        assertEquals("test_user@domain.com", user.getUsername());
    }

    @Test
    @DisplayName("测试时间戳")
    void testTimestamps() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(1);

        user.setCreatedAt(now);
        user.setUpdatedAt(future);

        assertEquals(now, user.getCreatedAt());
        assertEquals(future, user.getUpdatedAt());

        // 测试null时间戳
        user.setCreatedAt(null);
        user.setUpdatedAt(null);

        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());
    }

    @Test
    @DisplayName("测试用户权限关联")
    void testUserPermissionAssociation() {
        // 创建权限
        Permission perm1 = new Permission();
        perm1.setPermissionId("perm1");
        perm1.setTenantId(testTenantId);
        perm1.setName("权限1");
        perm1.setResource("resource1");
        perm1.setAction("action1");
        perm1.setDescription("描述1");
        perm1.setLevel(Permission.PermissionLevel.READ);
        
        Permission perm2 = new Permission();
        perm2.setPermissionId("perm2");
        perm2.setTenantId(testTenantId);
        perm2.setName("权限2");
        perm2.setResource("resource2");
        perm2.setAction("action2");
        perm2.setDescription("描述2");
        perm2.setLevel(Permission.PermissionLevel.WRITE);

        // 设置角色ID列表
        List<String> roleIds = Arrays.asList("role1", "role2");
        user.setRoleIds(roleIds);

        // 设置权限对象列表
        List<Permission> permissions = Arrays.asList(perm1, perm2);
        user.setPermissions(permissions);

        // 验证关联
        assertEquals(2, user.getRoleIds().size());
        assertEquals(2, user.getPermissions().size());
        assertTrue(user.getRoleIds().contains("role1"));
        assertTrue(user.getRoleIds().contains("role2"));
        assertTrue(user.getPermissions().contains(perm1));
        assertTrue(user.getPermissions().contains(perm2));
    }

    @Test
    @DisplayName("测试属性类型")
    void testAttributeTypes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("string", "value");
        attributes.put("integer", 42);
        attributes.put("double", 3.14);
        attributes.put("boolean", true);
        attributes.put("list", Arrays.asList("item1", "item2"));
        attributes.put("map", Collections.singletonMap("key", "value"));

        user.setAttributes(attributes);

        assertEquals("value", user.getAttribute("string"));
        assertEquals(42, user.getAttribute("integer"));
        assertEquals(3.14, user.getAttribute("double"));
        assertEquals(true, user.getAttribute("boolean"));
        assertEquals(Arrays.asList("item1", "item2"), user.getAttribute("list"));
        assertEquals(Collections.singletonMap("key", "value"), user.getAttribute("map"));
    }

    @Test
    @DisplayName("测试边界值")
    void testBoundaryValues() {
        // 测试空字符串
        user.setUsername("");
        assertEquals("", user.getUsername());

        // 测试null值
        user.setUsername(null);
        user.setAttributes(null);
        user.setRoleIds(null);
        user.setPermissions(null);
        assertNull(user.getUsername());
        assertNull(user.getAttributes());
        assertNull(user.getRoleIds());
        assertNull(user.getPermissions());
    }

    @Test
    @DisplayName("测试属性默认值")
    void testAttributeDefaultValues() {
        // 测试默认值 - User类没有带默认值的getAttribute方法
        assertNull(user.getAttribute("nonexistent"));
    }
}
