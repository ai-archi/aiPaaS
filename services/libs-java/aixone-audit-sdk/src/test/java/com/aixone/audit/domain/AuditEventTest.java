package com.aixone.audit.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * AuditEvent 领域模型测试
 */
class AuditEventTest {

    @Test
    void testAuditEventCreation() {
        AuditEvent event = new AuditEvent(1L, "user123", "LOGIN", "SUCCESS");
        
        assertNotNull(event);
        assertEquals(1L, event.getAuditLogId());
        assertEquals("user123", event.getUserId());
        assertEquals("LOGIN", event.getAction());
        assertEquals("SUCCESS", event.getResult());
    }

    @Test
    void testAuditEventWithTenantId() {
        AuditEvent event = new AuditEvent(2L, "user456", "LOGOUT", "SUCCESS", "tenant1");
        
        assertNotNull(event);
        assertEquals(2L, event.getAuditLogId());
        assertEquals("user456", event.getUserId());
        assertEquals("LOGOUT", event.getAction());
        assertEquals("SUCCESS", event.getResult());
        assertEquals("tenant1", event.getTenantId());
    }

    @Test
    void testAuditEventFailure() {
        AuditEvent event = new AuditEvent(3L, "user789", "LOGIN", "FAILURE");
        
        assertNotNull(event);
        assertEquals(3L, event.getAuditLogId());
        assertEquals("user789", event.getUserId());
        assertEquals("LOGIN", event.getAction());
        assertEquals("FAILURE", event.getResult());
    }

    @Test
    void testAuditEventPermissionCheck() {
        AuditEvent event = new AuditEvent(4L, "user101", "PERMISSION_CHECK", "SUCCESS", "tenant2");
        
        assertNotNull(event);
        assertEquals(4L, event.getAuditLogId());
        assertEquals("user101", event.getUserId());
        assertEquals("PERMISSION_CHECK", event.getAction());
        assertEquals("SUCCESS", event.getResult());
        assertEquals("tenant2", event.getTenantId());
    }

    @Test
    void testAuditEventWithNullValues() {
        AuditEvent event = new AuditEvent(null, null, null, null);
        
        assertNotNull(event);
        assertNull(event.getAuditLogId());
        assertNull(event.getUserId());
        assertNull(event.getAction());
        assertNull(event.getResult());
    }

    @Test
    void testAuditEventWithNullTenantId() {
        AuditEvent event = new AuditEvent(5L, "user202", "DATA_ACCESS", "SUCCESS", null);
        
        assertNotNull(event);
        assertEquals(5L, event.getAuditLogId());
        assertEquals("user202", event.getUserId());
        assertEquals("DATA_ACCESS", event.getAction());
        assertEquals("SUCCESS", event.getResult());
        assertNull(event.getTenantId());
    }
}