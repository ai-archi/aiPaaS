package com.aixone.event.client;

import com.aixone.event.dto.EventDTO;
import com.aixone.event.dto.TopicDTO;
import com.aixone.common.api.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;

/**
 * EventCenterClient 单元测试
 */
@DisplayName("EventCenterClient 测试")
class EventCenterClientTest {

    private EventCenterClient client;
    private static final String BASE_URL = "http://localhost:8080";
    private static final String TENANT_ID = "tenant-001";

    @BeforeEach
    void setUp() {
        client = new EventCenterClient(BASE_URL, TENANT_ID);
    }

    @Test
    @DisplayName("构造函数测试")
    void testConstructor() {
        assertEquals(BASE_URL, client.getBaseUrl());
        assertEquals(TENANT_ID, client.getTenantId());
    }

    @Test
    @DisplayName("EventApi 方法测试 - 应该抛出 UnsupportedOperationException")
    void testEventApiMethods() {
        EventDTO eventDTO = new EventDTO("user.login", "auth-service", "{}", TENANT_ID);
        
        // 测试所有 EventApi 方法都应该抛出 UnsupportedOperationException
        assertThrows(UnsupportedOperationException.class, () -> 
            client.publishEvent(eventDTO));
        
        assertThrows(UnsupportedOperationException.class, () -> 
            client.publishEventToTopic("user-events", eventDTO));
        
        assertThrows(UnsupportedOperationException.class, () -> 
            client.getAllEvents());
        
        assertThrows(UnsupportedOperationException.class, () -> 
            client.getEventById(123L));
        
        assertThrows(UnsupportedOperationException.class, () -> 
            client.getEventsByType("user.login"));
        
        assertThrows(UnsupportedOperationException.class, () -> 
            client.getEventsByTimeRange(Instant.now().minusSeconds(3600), Instant.now()));
        
        assertThrows(UnsupportedOperationException.class, () -> 
            client.getEventsByCorrelationId("corr-123"));
        
        assertThrows(UnsupportedOperationException.class, () -> 
            client.getEventStats());
    }

    @Test
    @DisplayName("TopicApi 方法测试 - 应该抛出 UnsupportedOperationException")
    void testTopicApiMethods() {
        TopicDTO topicDTO = new TopicDTO("user-events", "auth-service", "用户相关事件", TENANT_ID);
        
        // 测试所有 TopicApi 方法都应该抛出 UnsupportedOperationException
        assertThrows(UnsupportedOperationException.class, () -> 
            client.registerTopic(topicDTO));
        
        assertThrows(UnsupportedOperationException.class, () -> 
            client.getAllTopics());
        
        assertThrows(UnsupportedOperationException.class, () -> 
            client.getTopicByName("user-events"));
        
        assertThrows(UnsupportedOperationException.class, () -> 
            client.updateTopic("user-events", "新的描述"));
        
        assertThrows(UnsupportedOperationException.class, () -> 
            client.activateTopic("user-events"));
        
        assertThrows(UnsupportedOperationException.class, () -> 
            client.deactivateTopic("user-events"));
        
        assertThrows(UnsupportedOperationException.class, () -> 
            client.deleteTopic("user-events"));
    }

    @Test
    @DisplayName("工具方法测试")
    void testUtilityMethods() {
        // 测试 getBaseUrl
        assertEquals(BASE_URL, client.getBaseUrl());
        
        // 测试 getTenantId
        assertEquals(TENANT_ID, client.getTenantId());
        
        // 测试不同的构造函数参数
        String customBaseUrl = "http://custom-host:9090";
        String customTenantId = "custom-tenant";
        
        EventCenterClient customClient = new EventCenterClient(customBaseUrl, customTenantId);
        assertEquals(customBaseUrl, customClient.getBaseUrl());
        assertEquals(customTenantId, customClient.getTenantId());
    }

    @Test
    @DisplayName("异常消息测试")
    void testExceptionMessages() {
        EventDTO eventDTO = new EventDTO("user.login", "auth-service", "{}", TENANT_ID);
        
        try {
            client.publishEvent(eventDTO);
            fail("应该抛出 UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            assertTrue(e.getMessage().contains("需要具体实现"));
            assertTrue(e.getMessage().contains("RestTemplate") || e.getMessage().contains("Feign"));
        }
        
        try {
            client.registerTopic(new TopicDTO("test", "owner", "desc", "tenant"));
            fail("应该抛出 UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            assertTrue(e.getMessage().contains("需要具体实现"));
            assertTrue(e.getMessage().contains("RestTemplate") || e.getMessage().contains("Feign"));
        }
    }

    @Test
    @DisplayName("空值参数测试")
    void testNullParameters() {
        // 测试 null 参数
        assertThrows(UnsupportedOperationException.class, () -> 
            client.publishEvent(null));
        
        assertThrows(UnsupportedOperationException.class, () -> 
            client.publishEventToTopic(null, new EventDTO()));
        
        assertThrows(UnsupportedOperationException.class, () -> 
            client.publishEventToTopic("topic", null));
        
        assertThrows(UnsupportedOperationException.class, () -> 
            client.getEventById(null));
        
        assertThrows(UnsupportedOperationException.class, () -> 
            client.getEventsByType(null));
        
        assertThrows(UnsupportedOperationException.class, () -> 
            client.getEventsByCorrelationId(null));
        
        assertThrows(UnsupportedOperationException.class, () -> 
            client.getTopicByName(null));
        
        assertThrows(UnsupportedOperationException.class, () -> 
            client.updateTopic(null, "desc"));
        
        assertThrows(UnsupportedOperationException.class, () -> 
            client.updateTopic("topic", null));
        
        assertThrows(UnsupportedOperationException.class, () -> 
            client.activateTopic(null));
        
        assertThrows(UnsupportedOperationException.class, () -> 
            client.deactivateTopic(null));
        
        assertThrows(UnsupportedOperationException.class, () -> 
            client.deleteTopic(null));
    }

    @Test
    @DisplayName("构造函数边界值测试")
    void testConstructorBoundaryValues() {
        // 测试空字符串
        EventCenterClient client1 = new EventCenterClient("", "");
        assertEquals("", client1.getBaseUrl());
        assertEquals("", client1.getTenantId());
        
        // 测试 null 值（虽然不推荐，但测试客户端的行为）
        EventCenterClient client2 = new EventCenterClient(null, null);
        assertNull(client2.getBaseUrl());
        assertNull(client2.getTenantId());
        
        // 测试长字符串
        String longUrl = "http://" + "a".repeat(1000) + ".com";
        String longTenant = "tenant-" + "b".repeat(1000);
        EventCenterClient client3 = new EventCenterClient(longUrl, longTenant);
        assertEquals(longUrl, client3.getBaseUrl());
        assertEquals(longTenant, client3.getTenantId());
    }

    @Test
    @DisplayName("接口实现测试")
    void testInterfaceImplementation() {
        // 验证实现了正确的接口
        assertTrue(client instanceof com.aixone.event.api.EventApi);
        assertTrue(client instanceof com.aixone.event.api.TopicApi);
    }
}