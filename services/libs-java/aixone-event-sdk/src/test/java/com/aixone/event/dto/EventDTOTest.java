package com.aixone.event.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;

/**
 * EventDTO 单元测试
 */
@DisplayName("EventDTO 测试")
class EventDTOTest {

    @Test
    @DisplayName("默认构造函数测试")
    void testDefaultConstructor() {
        EventDTO event = new EventDTO();
        
        assertNull(event.getEventId());
        assertNull(event.getEventType());
        assertNull(event.getSource());
        assertNull(event.getData());
        assertNull(event.getTimestamp());
        assertNull(event.getTenantId());
        assertNull(event.getCorrelationId());
        assertEquals(Integer.valueOf(1), event.getVersion());
    }

    @Test
    @DisplayName("业务构造函数测试")
    void testBusinessConstructor() {
        String eventType = "user.login";
        String source = "auth-service";
        String data = "{\"userId\": 123}";
        String tenantId = "tenant-001";
        
        EventDTO event = new EventDTO(eventType, source, data, tenantId);
        
        assertNull(event.getEventId());
        assertEquals(eventType, event.getEventType());
        assertEquals(source, event.getSource());
        assertEquals(data, event.getData());
        assertEquals(tenantId, event.getTenantId());
        assertNotNull(event.getTimestamp());
        assertEquals(Integer.valueOf(1), event.getVersion());
        assertNull(event.getCorrelationId());
    }

    @Test
    @DisplayName("Getters 和 Setters 测试")
    void testGettersAndSetters() {
        EventDTO event = new EventDTO();
        
        // 测试 eventId
        Long eventId = 123L;
        event.setEventId(eventId);
        assertEquals(eventId, event.getEventId());
        
        // 测试 eventType
        String eventType = "user.logout";
        event.setEventType(eventType);
        assertEquals(eventType, event.getEventType());
        
        // 测试 source
        String source = "web-app";
        event.setSource(source);
        assertEquals(source, event.getSource());
        
        // 测试 data
        String data = "{\"sessionId\": \"abc123\"}";
        event.setData(data);
        assertEquals(data, event.getData());
        
        // 测试 timestamp
        Instant timestamp = Instant.now();
        event.setTimestamp(timestamp);
        assertEquals(timestamp, event.getTimestamp());
        
        // 测试 tenantId
        String tenantId = "tenant-002";
        event.setTenantId(tenantId);
        assertEquals(tenantId, event.getTenantId());
        
        // 测试 correlationId
        String correlationId = "corr-123";
        event.setCorrelationId(correlationId);
        assertEquals(correlationId, event.getCorrelationId());
        
        // 测试 version
        Integer version = 2;
        event.setVersion(version);
        assertEquals(version, event.getVersion());
    }

    @Test
    @DisplayName("toString 方法测试")
    void testToString() {
        EventDTO event = new EventDTO();
        event.setEventId(123L);
        event.setEventType("user.login");
        event.setSource("auth-service");
        event.setTenantId("tenant-001");
        event.setTimestamp(Instant.parse("2024-01-01T10:00:00Z"));
        event.setVersion(1);
        
        String result = event.toString();
        
        assertTrue(result.contains("eventId=123"));
        assertTrue(result.contains("eventType='user.login'"));
        assertTrue(result.contains("source='auth-service'"));
        assertTrue(result.contains("tenantId='tenant-001'"));
        assertTrue(result.contains("version=1"));
    }

    @Test
    @DisplayName("序列化测试")
    void testSerialization() {
        EventDTO original = new EventDTO("user.login", "auth-service", "{\"userId\": 123}", "tenant-001");
        original.setEventId(123L);
        original.setCorrelationId("corr-123");
        
        // 验证实现了 Serializable 接口
        assertTrue(original instanceof java.io.Serializable);
        
        // 验证 serialVersionUID
        try {
            java.lang.reflect.Field field = EventDTO.class.getDeclaredField("serialVersionUID");
            field.setAccessible(true);
            long serialVersionUID = field.getLong(null);
            assertEquals(1L, serialVersionUID);
        } catch (Exception e) {
            fail("serialVersionUID 字段不存在或访问失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("边界值测试")
    void testBoundaryValues() {
        EventDTO event = new EventDTO();
        
        // 测试空字符串
        event.setEventType("");
        assertEquals("", event.getEventType());
        
        event.setSource("");
        assertEquals("", event.getSource());
        
        event.setData("");
        assertEquals("", event.getData());
        
        // 测试 null 值
        event.setEventType(null);
        assertNull(event.getEventType());
        
        event.setSource(null);
        assertNull(event.getSource());
        
        event.setData(null);
        assertNull(event.getData());
        
        // 测试长字符串
        String longString = "a".repeat(1000);
        event.setData(longString);
        assertEquals(longString, event.getData());
    }

    @Test
    @DisplayName("时间戳测试")
    void testTimestamp() {
        EventDTO event = new EventDTO("user.login", "auth-service", "{}", "tenant-001");
        
        // 验证时间戳在合理范围内（当前时间前后1秒内）
        Instant now = Instant.now();
        Instant eventTime = event.getTimestamp();
        
        assertTrue(eventTime.isAfter(now.minusSeconds(1)));
        assertTrue(eventTime.isBefore(now.plusSeconds(1)));
    }
}