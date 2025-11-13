package com.aixone.eventcenter.event.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Event 领域模型单元测试
 */
@DisplayName("Event 领域模型测试")
class EventTest {

    private Event event;
    private final String TEST_EVENT_TYPE = "USER_CREATED";
    private final String TEST_SOURCE = "user-service";
    private final String TEST_DATA = "{\"userId\":\"123\",\"name\":\"John Doe\"}";
    private final String TEST_TENANT_ID = "tenant-001";

    @BeforeEach
    void setUp() {
        event = new Event(TEST_EVENT_TYPE, TEST_SOURCE, TEST_DATA, TEST_TENANT_ID);
    }

    @Nested
    @DisplayName("状态管理测试")
    class StatusManagementTests {

        @Test
        @DisplayName("应该能够标记为已发布")
        void shouldMarkAsPublished() {
            // When
            event.markAsPublished();

            // Then
            assertEquals(Event.EventStatus.PUBLISHED, event.getStatus());
        }

        @Test
        @DisplayName("应该能够标记为失败")
        void shouldMarkAsFailed() {
            // When
            event.markAsFailed();

            // Then
            assertEquals(Event.EventStatus.FAILED, event.getStatus());
        }

        @Test
        @DisplayName("应该能够取消事件")
        void shouldCancelEvent() {
            // When
            event.cancel();

            // Then
            assertEquals(Event.EventStatus.CANCELLED, event.getStatus());
        }

        @Test
        @DisplayName("应该能够更新状态")
        void shouldUpdateStatus() {
            // When
            event.updateStatus(Event.EventStatus.PUBLISHED);

            // Then
            assertEquals(Event.EventStatus.PUBLISHED, event.getStatus());
        }
    }

    @Nested
    @DisplayName("构造函数测试")
    class ConstructorTests {

        @Test
        @DisplayName("应该正确创建事件对象")
        void shouldCreateEventCorrectly() {
            // Given & When
            Event newEvent = new Event(TEST_EVENT_TYPE, TEST_SOURCE, TEST_DATA, TEST_TENANT_ID);

            // Then
            assertNotNull(newEvent);
            assertEquals(TEST_EVENT_TYPE, newEvent.getEventType());
            assertEquals(TEST_SOURCE, newEvent.getEventSource());
            assertEquals(TEST_DATA, newEvent.getEventData());
            assertEquals(TEST_TENANT_ID, newEvent.getTenantId());
            assertNotNull(newEvent.getCreatedAt());
            assertEquals(1, newEvent.getVersion());
            assertEquals(Event.EventStatus.PENDING, newEvent.getStatus());
        }

        @Test
        @DisplayName("应该使用当前时间作为时间戳")
        void shouldUseCurrentTimeAsTimestamp() {
            // Given
            Instant beforeCreation = Instant.now();

            // When
            Event newEvent = new Event(TEST_EVENT_TYPE, TEST_SOURCE, TEST_DATA, TEST_TENANT_ID);
            Instant afterCreation = Instant.now();

            // Then
            assertTrue(newEvent.getCreatedAt().isAfter(beforeCreation) || 
                      newEvent.getCreatedAt().equals(beforeCreation));
            assertTrue(newEvent.getCreatedAt().isBefore(afterCreation) || 
                      newEvent.getCreatedAt().equals(afterCreation));
        }
    }

    @Nested
    @DisplayName("业务方法测试")
    class BusinessMethodTests {

        @Test
        @DisplayName("应该正确更新事件数据并增加版本号")
        void shouldUpdateDataAndIncrementVersion() {
            // Given
            String newData = "{\"userId\":\"123\",\"name\":\"Jane Doe\"}";
            int originalVersion = event.getVersion();

            // When
            event.updateData(newData);

            // Then
            assertEquals(newData, event.getEventData());
            assertEquals(originalVersion + 1, event.getVersion());
        }

        @Test
        @DisplayName("应该支持多次更新数据")
        void shouldSupportMultipleDataUpdates() {
            // Given
            String firstUpdate = "{\"userId\":\"123\",\"name\":\"Jane Doe\"}";
            String secondUpdate = "{\"userId\":\"123\",\"name\":\"Jane Smith\"}";

            // When
            event.updateData(firstUpdate);
            event.updateData(secondUpdate);

            // Then
            assertEquals(secondUpdate, event.getEventData());
            assertEquals(3, event.getVersion()); // 初始版本1 + 两次更新
        }
    }

    @Nested
    @DisplayName("Getter 和 Setter 测试")
    class GetterSetterTests {

        @Test
        @DisplayName("应该正确设置和获取所有属性")
        void shouldSetAndGetAllProperties() {
            // Given
            Long eventId = 1L;
            String eventType = "ORDER_CREATED";
            String source = "order-service";
            String data = "{\"orderId\":\"456\"}";
            Instant timestamp = Instant.now();
            String tenantId = "tenant-002";
            String correlationId = "corr-123";
            Integer version = 2;

            // When
            event.setEventId(eventId);
            event.setEventType(eventType);
            event.setEventSource(source);
            event.setEventData(data);
            event.setCreatedAt(timestamp);
            event.setTenantId(tenantId);
            event.setCorrelationId(correlationId);
            event.setVersion(version);

            // Then
            assertEquals(eventId, event.getEventId());
            assertEquals(eventType, event.getEventType());
            assertEquals(source, event.getEventSource());
            assertEquals(data, event.getEventData());
            assertEquals(timestamp, event.getCreatedAt());
            assertEquals(tenantId, event.getTenantId());
            assertEquals(correlationId, event.getCorrelationId());
            assertEquals(version, event.getVersion());
        }

        @Test
        @DisplayName("getId 应该返回 eventId")
        void getIdShouldReturnEventId() {
            // Given
            Long eventId = 999L;
            event.setEventId(eventId);

            // When & Then
            assertEquals(eventId, event.getId());
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("应该处理空数据")
        void shouldHandleNullData() {
            // When
            event.setEventData(null);

            // Then
            assertNull(event.getEventData());
        }

        @Test
        @DisplayName("应该处理空字符串数据")
        void shouldHandleEmptyStringData() {
            // When
            event.setEventData("");

            // Then
            assertEquals("", event.getEventData());
        }

        @Test
        @DisplayName("应该处理长数据")
        void shouldHandleLongData() {
            // Given
            StringBuilder longData = new StringBuilder();
            for (int i = 0; i < 1000; i++) {
                longData.append("This is a very long data string. ");
            }

            // When
            event.setEventData(longData.toString());

            // Then
            assertEquals(longData.toString(), event.getEventData());
        }
    }

    @Nested
    @DisplayName("版本控制测试")
    class VersionControlTests {

        @Test
        @DisplayName("初始版本应该是1")
        void initialVersionShouldBeOne() {
            // When & Then
            assertEquals(1, event.getVersion());
        }

        @Test
        @DisplayName("每次更新数据应该增加版本号")
        void shouldIncrementVersionOnEachUpdate() {
            // When
            event.updateData("data1");
            event.updateData("data2");
            event.updateData("data3");

            // Then
            assertEquals(4, event.getVersion());
            assertEquals("data3", event.getEventData());
        }

        @Test
        @DisplayName("直接设置版本号应该生效")
        void shouldAllowDirectVersionSetting() {
            // Given
            Integer newVersion = 10;

            // When
            event.setVersion(newVersion);

            // Then
            assertEquals(newVersion, event.getVersion());
        }
    }
}
