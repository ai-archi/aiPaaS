package com.aixone.event.constant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * EventType 单元测试
 */
@DisplayName("EventType 测试")
class EventTypeTest {

    @Test
    @DisplayName("用户相关事件常量测试")
    void testUserEventConstants() {
        assertEquals("user.login", EventType.USER_LOGIN);
        assertEquals("user.logout", EventType.USER_LOGOUT);
        assertEquals("user.register", EventType.USER_REGISTER);
        assertEquals("user.update", EventType.USER_UPDATE);
        assertEquals("user.delete", EventType.USER_DELETE);
    }

    @Test
    @DisplayName("权限相关事件常量测试")
    void testPermissionEventConstants() {
        assertEquals("permission.grant", EventType.PERMISSION_GRANT);
        assertEquals("permission.revoke", EventType.PERMISSION_REVOKE);
        assertEquals("role.assign", EventType.ROLE_ASSIGN);
        assertEquals("role.unassign", EventType.ROLE_UNASSIGN);
    }

    @Test
    @DisplayName("系统相关事件常量测试")
    void testSystemEventConstants() {
        assertEquals("system.start", EventType.SYSTEM_START);
        assertEquals("system.stop", EventType.SYSTEM_STOP);
        assertEquals("system.error", EventType.SYSTEM_ERROR);
        assertEquals("system.warning", EventType.SYSTEM_WARNING);
    }

    @Test
    @DisplayName("业务相关事件常量测试")
    void testBusinessEventConstants() {
        assertEquals("business.create", EventType.BUSINESS_CREATE);
        assertEquals("business.update", EventType.BUSINESS_UPDATE);
        assertEquals("business.delete", EventType.BUSINESS_DELETE);
        assertEquals("business.approve", EventType.BUSINESS_APPROVE);
        assertEquals("business.reject", EventType.BUSINESS_REJECT);
    }

    @Test
    @DisplayName("审计相关事件常量测试")
    void testAuditEventConstants() {
        assertEquals("audit.login", EventType.AUDIT_LOGIN);
        assertEquals("audit.logout", EventType.AUDIT_LOGOUT);
        assertEquals("audit.access", EventType.AUDIT_ACCESS);
        assertEquals("audit.operation", EventType.AUDIT_OPERATION);
    }

    @Test
    @DisplayName("通知相关事件常量测试")
    void testNotificationEventConstants() {
        assertEquals("notification.send", EventType.NOTIFICATION_SEND);
        assertEquals("notification.read", EventType.NOTIFICATION_READ);
        assertEquals("notification.delete", EventType.NOTIFICATION_DELETE);
    }

    @Test
    @DisplayName("数据相关事件常量测试")
    void testDataEventConstants() {
        assertEquals("data.create", EventType.DATA_CREATE);
        assertEquals("data.update", EventType.DATA_UPDATE);
        assertEquals("data.delete", EventType.DATA_DELETE);
        assertEquals("data.export", EventType.DATA_EXPORT);
        assertEquals("data.import", EventType.DATA_IMPORT);
    }

    @Test
    @DisplayName("其他事件常量测试")
    void testOtherEventConstants() {
        assertEquals("other", EventType.OTHER);
    }

    @Test
    @DisplayName("isValid 方法测试")
    void testIsValid() {
        // 有效的事件类型
        assertTrue(EventType.isValid("user.login"));
        assertTrue(EventType.isValid("system.start"));
        assertTrue(EventType.isValid("business.create"));
        assertTrue(EventType.isValid("custom.event"));
        assertTrue(EventType.isValid("a"));
        
        // 无效的事件类型
        assertFalse(EventType.isValid(null));
        assertFalse(EventType.isValid(""));
        assertFalse(EventType.isValid("   "));
        assertFalse(EventType.isValid("\t"));
        assertFalse(EventType.isValid("\n"));
    }

    @Test
    @DisplayName("getCategory 方法测试")
    void testGetCategory() {
        // 用户相关
        assertEquals("user", EventType.getCategory("user.login"));
        assertEquals("user", EventType.getCategory("user.logout"));
        assertEquals("user", EventType.getCategory("user.register"));
        assertEquals("user", EventType.getCategory("user.update"));
        assertEquals("user", EventType.getCategory("user.delete"));
        
        // 权限相关
        assertEquals("permission", EventType.getCategory("permission.grant"));
        assertEquals("permission", EventType.getCategory("permission.revoke"));
        assertEquals("permission", EventType.getCategory("role.assign"));
        assertEquals("permission", EventType.getCategory("role.unassign"));
        
        // 系统相关
        assertEquals("system", EventType.getCategory("system.start"));
        assertEquals("system", EventType.getCategory("system.stop"));
        assertEquals("system", EventType.getCategory("system.error"));
        assertEquals("system", EventType.getCategory("system.warning"));
        
        // 业务相关
        assertEquals("business", EventType.getCategory("business.create"));
        assertEquals("business", EventType.getCategory("business.update"));
        assertEquals("business", EventType.getCategory("business.delete"));
        assertEquals("business", EventType.getCategory("business.approve"));
        assertEquals("business", EventType.getCategory("business.reject"));
        
        // 审计相关
        assertEquals("audit", EventType.getCategory("audit.login"));
        assertEquals("audit", EventType.getCategory("audit.logout"));
        assertEquals("audit", EventType.getCategory("audit.access"));
        assertEquals("audit", EventType.getCategory("audit.operation"));
        
        // 通知相关
        assertEquals("notification", EventType.getCategory("notification.send"));
        assertEquals("notification", EventType.getCategory("notification.read"));
        assertEquals("notification", EventType.getCategory("notification.delete"));
        
        // 数据相关
        assertEquals("data", EventType.getCategory("data.create"));
        assertEquals("data", EventType.getCategory("data.update"));
        assertEquals("data", EventType.getCategory("data.delete"));
        assertEquals("data", EventType.getCategory("data.export"));
        assertEquals("data", EventType.getCategory("data.import"));
        
        // 其他
        assertEquals("other", EventType.getCategory("custom.event"));
        assertEquals("other", EventType.getCategory("unknown.type"));
        assertEquals("other", EventType.getCategory(""));
        assertEquals("other", EventType.getCategory("a"));
        
        // null 值
        assertEquals("unknown", EventType.getCategory(null));
    }

    @Test
    @DisplayName("边界值测试")
    void testBoundaryValues() {
        // 空字符串
        assertFalse(EventType.isValid(""));
        assertEquals("other", EventType.getCategory(""));
        
        // 只有空格
        assertFalse(EventType.isValid(" "));
        assertFalse(EventType.isValid("  "));
        assertFalse(EventType.isValid("\t"));
        assertFalse(EventType.isValid("\n"));
        
        // 单字符
        assertTrue(EventType.isValid("a"));
        assertEquals("other", EventType.getCategory("a"));
        
        // 长字符串
        String longEventType = "a".repeat(1000);
        assertTrue(EventType.isValid(longEventType));
        assertEquals("other", EventType.getCategory(longEventType));
    }

    @Test
    @DisplayName("常量不可变性测试")
    void testConstantsImmutability() {
        // 验证常量是 final 的（通过反射检查）
        try {
            java.lang.reflect.Field[] fields = EventType.class.getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                if (field.getName().startsWith("USER_") || 
                    field.getName().startsWith("PERMISSION_") ||
                    field.getName().startsWith("ROLE_") ||
                    field.getName().startsWith("SYSTEM_") ||
                    field.getName().startsWith("BUSINESS_") ||
                    field.getName().startsWith("AUDIT_") ||
                    field.getName().startsWith("NOTIFICATION_") ||
                    field.getName().startsWith("DATA_") ||
                    field.getName().equals("OTHER")) {
                    
                    assertTrue(java.lang.reflect.Modifier.isFinal(field.getModifiers()));
                    assertTrue(java.lang.reflect.Modifier.isStatic(field.getModifiers()));
                    assertTrue(java.lang.reflect.Modifier.isPublic(field.getModifiers()));
                }
            }
        } catch (Exception e) {
            fail("检查常量不可变性失败: " + e.getMessage());
        }
    }
}