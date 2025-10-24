package com.aixone.audit.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

/**
 * AuditContext 领域模型测试
 */
class AuditContextTest {

    private AuditContext context;

    @BeforeEach
    void setUp() {
        context = new AuditContext();
    }

    @Test
    void testAuditContextCreation() {
        assertNotNull(context);
        assertNotNull(context.getStartTime());
        assertNotNull(context.getDetails());
        assertTrue(context.getDetails().isEmpty());
    }

    @Test
    void testAuditContextWithActionAndResource() {
        AuditContext specificContext = new AuditContext("LOGIN", "auth");
        
        assertNotNull(specificContext);
        assertEquals("LOGIN", specificContext.getAction());
        assertEquals("auth", specificContext.getResource());
        assertNotNull(specificContext.getStartTime());
    }

    @Test
    void testAddDetail() {
        context.addDetail("key1", "value1");
        context.addDetail("key2", 123);
        context.addDetail("key3", true);
        
        assertEquals("value1", context.getDetails().get("key1"));
        assertEquals(123, context.getDetails().get("key2"));
        assertEquals(true, context.getDetails().get("key3"));
        assertEquals(3, context.getDetails().size());
    }

    @Test
    void testAddDetails() {
        Map<String, Object> detailsMap = new HashMap<>();
        detailsMap.put("key1", "value1");
        detailsMap.put("key2", "value2");
        detailsMap.put("key3", 456);
        
        context.addDetails(detailsMap);
        
        assertEquals("value1", context.getDetails().get("key1"));
        assertEquals("value2", context.getDetails().get("key2"));
        assertEquals(456, context.getDetails().get("key3"));
        assertEquals(3, context.getDetails().size());
    }

    @Test
    void testAddDetailsToExisting() {
        context.addDetail("existing", "value");
        assertEquals(1, context.getDetails().size());
        
        Map<String, Object> newDetails = new HashMap<>();
        newDetails.put("new1", "value1");
        newDetails.put("new2", "value2");
        
        context.addDetails(newDetails);
        
        assertEquals(3, context.getDetails().size());
        assertEquals("value", context.getDetails().get("existing"));
        assertEquals("value1", context.getDetails().get("new1"));
        assertEquals("value2", context.getDetails().get("new2"));
    }

    @Test
    void testGetDuration() {
        // 等待一小段时间以确保有持续时间
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long duration = context.getDuration();
        assertTrue(duration >= 0);
    }

    @Test
    void testSettersAndGetters() {
        context.setUserId("user123");
        context.setAction("LOGIN");
        context.setResource("auth");
        context.setClientIp("192.168.1.100");
        context.setUserAgent("Mozilla/5.0");
        context.setSessionId("session456");
        context.setTenantId("tenant1");
        
        assertEquals("user123", context.getUserId());
        assertEquals("LOGIN", context.getAction());
        assertEquals("auth", context.getResource());
        assertEquals("192.168.1.100", context.getClientIp());
        assertEquals("Mozilla/5.0", context.getUserAgent());
        assertEquals("session456", context.getSessionId());
        assertEquals("tenant1", context.getTenantId());
    }

    @Test
    void testNullValues() {
        context.setUserId(null);
        context.setAction(null);
        context.setResource(null);
        context.setClientIp(null);
        context.setUserAgent(null);
        context.setSessionId(null);
        context.setTenantId(null);
        
        assertNull(context.getUserId());
        assertNull(context.getAction());
        assertNull(context.getResource());
        assertNull(context.getClientIp());
        assertNull(context.getUserAgent());
        assertNull(context.getSessionId());
        assertNull(context.getTenantId());
    }
}