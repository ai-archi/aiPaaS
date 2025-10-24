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
            assertEquals(TEST_SOURCE, newEvent.getSource());
            assertEquals(TEST_DATA, newEvent.getData());
            assertEquals(TEST_TENANT_ID, newEvent.getTenantId());
            assertNotNull(newEvent.getTimestamp());
            assertEquals(1, newEvent.getVersion());
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
            assertTrue(newEvent.getTimestamp().isAfter(beforeCreation) || 
                      newEvent.getTimestamp().equals(beforeCreation));
            assertTrue(newEvent.getTimestamp().isBefore(afterCreation) || 
                      newEvent.getTimestamp().equals(afterCreation));
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
            assertEquals(newData, event.getData());
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
            assertEquals(secondUpdate, event.getData());
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
            event.setSource(source);
            event.setData(data);
            event.setTimestamp(timestamp);
            event.setTenantId(tenantId);
            event.setCorrelationId(correlationId);
            event.setVersion(version);

            // Then
            assertEquals(eventId, event.getEventId());
            assertEquals(eventType, event.getEventType());
            assertEquals(source, event.getSource());
            assertEquals(data, event.getData());
            assertEquals(timestamp, event.getTimestamp());
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
            event.setData(null);

            // Then
            assertNull(event.getData());
        }

        @Test
        @DisplayName("应该处理空字符串数据")
        void shouldHandleEmptyStringData() {
            // When
            event.setData("");

            // Then
            assertEquals("", event.getData());
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
            event.setData(longData.toString());

            // Then
            assertEquals(longData.toString(), event.getData());
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
