package com.aixone.eventcenter.event.interfaces;

import com.aixone.common.api.ApiResponse;
import com.aixone.eventcenter.event.application.EventApplicationService;
import com.aixone.eventcenter.event.domain.Event;
import com.aixone.common.session.SessionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * EventController 控制器测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EventController 控制器测试")
class EventControllerTest {

    @Mock
    private EventApplicationService eventApplicationService;

    private EventController eventController;

    private final String TEST_TENANT_ID = "tenant-001";
    private final String TEST_EVENT_TYPE = "USER_CREATED";
    private final String TEST_SOURCE = "user-service";
    private final String TEST_DATA = "{\"userId\":\"123\",\"name\":\"John Doe\"}";

    @BeforeEach
    void setUp() {
        eventController = new EventController();
        // 使用反射设置私有字段
        try {
            java.lang.reflect.Field eventApplicationServiceField = EventController.class.getDeclaredField("eventApplicationService");
            eventApplicationServiceField.setAccessible(true);
            eventApplicationServiceField.set(eventController, eventApplicationService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up EventController dependencies", e);
        }
    }

    @Nested
    @DisplayName("发布事件测试")
    class PublishEventTests {

        @Test
        @DisplayName("应该成功发布事件")
        void shouldPublishEventSuccessfully() {
            // Given
            EventController.EventRequest request = createValidEventRequest();
            Event savedEvent = createValidEvent();
            savedEvent.setEventId(1L);
            
            when(eventApplicationService.publishEvent(TEST_EVENT_TYPE, TEST_SOURCE, TEST_DATA, TEST_TENANT_ID))
                .thenReturn(savedEvent);

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                ApiResponse<Event> result = eventController.publishEvent(request);

                // Then
                assertNotNull(result);
                assertEquals(200, result.getCode());
                assertEquals(savedEvent, result.getData());
                verify(eventApplicationService).publishEvent(TEST_EVENT_TYPE, TEST_SOURCE, TEST_DATA, TEST_TENANT_ID);
            }
        }

        @Test
        @DisplayName("缺少租户ID应该返回错误")
        void missingTenantIdShouldReturnError() {
            // Given
            EventController.EventRequest request = createValidEventRequest();

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(null);
                
                ApiResponse<Event> result = eventController.publishEvent(request);

                // Then
                assertNotNull(result);
                assertNotEquals(200, result.getCode());
                assertEquals(40001, result.getCode());
                assertEquals("缺少租户ID", result.getMessage());
                verify(eventApplicationService, never()).publishEvent(anyString(), anyString(), anyString(), anyString());
            }
        }
    }

    @Nested
    @DisplayName("发布事件到Kafka测试")
    class PublishEventToKafkaTests {

        @Test
        @DisplayName("应该成功发布事件到Kafka")
        void shouldPublishEventToKafkaSuccessfully() {
            // Given
            String topicName = "test-topic";
            EventController.EventRequest request = createValidEventRequest();
            Event savedEvent = createValidEvent();
            savedEvent.setEventId(1L);
            
            when(eventApplicationService.publishEventToKafka(topicName, TEST_EVENT_TYPE, TEST_SOURCE, TEST_DATA, TEST_TENANT_ID))
                .thenReturn(savedEvent);

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                ApiResponse<Event> result = eventController.publishEventToKafka(topicName, request);

                // Then
                assertNotNull(result);
                assertEquals(200, result.getCode());
                assertEquals(savedEvent, result.getData());
                verify(eventApplicationService).publishEventToKafka(topicName, TEST_EVENT_TYPE, TEST_SOURCE, TEST_DATA, TEST_TENANT_ID);
            }
        }

        @Test
        @DisplayName("缺少租户ID应该返回错误")
        void missingTenantIdShouldReturnError() {
            // Given
            String topicName = "test-topic";
            EventController.EventRequest request = createValidEventRequest();

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(null);
                
                ApiResponse<Event> result = eventController.publishEventToKafka(topicName, request);

                // Then
                assertNotNull(result);
                assertNotEquals(200, result.getCode());
                assertEquals(40001, result.getCode());
                assertEquals("缺少租户ID", result.getMessage());
                verify(eventApplicationService, never()).publishEventToKafka(anyString(), anyString(), anyString(), anyString(), anyString());
            }
        }
    }

    @Nested
    @DisplayName("查询事件测试")
    class QueryEventTests {

        @Test
        @DisplayName("应该成功获取所有事件")
        void shouldGetAllEventsSuccessfully() {
            // Given
            List<Event> events = Arrays.asList(createValidEvent(), createValidEvent());
            when(eventApplicationService.getEventsByTenant(TEST_TENANT_ID)).thenReturn(events);

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                ApiResponse<List<Event>> result = eventController.getAllEvents();

                // Then
                assertNotNull(result);
                assertEquals(200, result.getCode());
                assertEquals(2, result.getData().size());
                verify(eventApplicationService).getEventsByTenant(TEST_TENANT_ID);
            }
        }

        @Test
        @DisplayName("应该成功根据ID获取事件")
        void shouldGetEventByIdSuccessfully() {
            // Given
            Long eventId = 1L;
            Event event = createValidEvent();
            event.setEventId(eventId);
            when(eventApplicationService.getEventById(eventId, TEST_TENANT_ID))
                .thenReturn(Optional.of(event));

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                ApiResponse<Event> result = eventController.getEventById(eventId);

                // Then
                assertNotNull(result);
                assertEquals(200, result.getCode());
                assertEquals(eventId, result.getData().getId());
                verify(eventApplicationService).getEventById(eventId, TEST_TENANT_ID);
            }
        }

        @Test
        @DisplayName("不存在的事件应该返回错误")
        void nonExistentEventShouldReturnError() {
            // Given
            Long eventId = 999L;
            when(eventApplicationService.getEventById(eventId, TEST_TENANT_ID))
                .thenReturn(Optional.empty());

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                ApiResponse<Event> result = eventController.getEventById(eventId);

                // Then
                assertNotNull(result);
                assertNotEquals(200, result.getCode());
                assertEquals(40401, result.getCode());
                assertEquals("事件不存在", result.getMessage());
            }
        }

        @Test
        @DisplayName("应该成功根据事件类型获取事件")
        void shouldGetEventsByTypeSuccessfully() {
            // Given
            List<Event> events = Arrays.asList(createValidEvent(), createValidEvent());
            when(eventApplicationService.getEventsByType(TEST_EVENT_TYPE, TEST_TENANT_ID))
                .thenReturn(events);

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                ApiResponse<List<Event>> result = eventController.getEventsByType(TEST_EVENT_TYPE);

                // Then
                assertNotNull(result);
                assertEquals(200, result.getCode());
                assertEquals(2, result.getData().size());
                verify(eventApplicationService).getEventsByType(TEST_EVENT_TYPE, TEST_TENANT_ID);
            }
        }

        @Test
        @DisplayName("应该成功根据时间范围获取事件")
        void shouldGetEventsByTimeRangeSuccessfully() {
            // Given
            Instant startTime = Instant.now().minusSeconds(3600);
            Instant endTime = Instant.now();
            List<Event> events = Arrays.asList(createValidEvent(), createValidEvent());
            when(eventApplicationService.getEventsByTimeRange(TEST_TENANT_ID, startTime, endTime))
                .thenReturn(events);

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                ApiResponse<List<Event>> result = eventController.getEventsByTimeRange(startTime, endTime);

                // Then
                assertNotNull(result);
                assertEquals(200, result.getCode());
                assertEquals(2, result.getData().size());
                verify(eventApplicationService).getEventsByTimeRange(TEST_TENANT_ID, startTime, endTime);
            }
        }

        @Test
        @DisplayName("应该成功根据关联ID获取事件")
        void shouldGetEventsByCorrelationIdSuccessfully() {
            // Given
            String correlationId = "corr-123";
            List<Event> events = Arrays.asList(createValidEvent(), createValidEvent());
            when(eventApplicationService.getEventsByCorrelationId(correlationId, TEST_TENANT_ID))
                .thenReturn(events);

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                ApiResponse<List<Event>> result = eventController.getEventsByCorrelationId(correlationId);

                // Then
                assertNotNull(result);
                assertEquals(200, result.getCode());
                assertEquals(2, result.getData().size());
                verify(eventApplicationService).getEventsByCorrelationId(correlationId, TEST_TENANT_ID);
            }
        }
    }

    @Nested
    @DisplayName("事件统计测试")
    class EventStatsTests {

        @Test
        @DisplayName("应该成功获取事件统计")
        void shouldGetEventStatsSuccessfully() {
            // Given
            long eventCount = 100L;
            when(eventApplicationService.getEventCountByTenant(TEST_TENANT_ID)).thenReturn(eventCount);

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                ApiResponse<EventController.EventStats> result = eventController.getEventStats();

                // Then
                assertNotNull(result);
                assertEquals(200, result.getCode());
                assertEquals(eventCount, result.getData().getTotalCount());
                verify(eventApplicationService).getEventCountByTenant(TEST_TENANT_ID);
            }
        }

        @Test
        @DisplayName("缺少租户ID应该返回错误")
        void missingTenantIdShouldReturnError() {
            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(null);
                
                ApiResponse<EventController.EventStats> result = eventController.getEventStats();

                // Then
                assertNotNull(result);
                assertNotEquals(200, result.getCode());
                assertEquals(40001, result.getCode());
                assertEquals("缺少租户ID", result.getMessage());
                verify(eventApplicationService, never()).getEventCountByTenant(anyString());
            }
        }
    }

    @Nested
    @DisplayName("缺少租户ID测试")
    class MissingTenantIdTests {

        @Test
        @DisplayName("获取所有事件缺少租户ID应该返回错误")
        void getAllEventsMissingTenantIdShouldReturnError() {
            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(null);
                
                ApiResponse<List<Event>> result = eventController.getAllEvents();

                // Then
                assertNotNull(result);
                assertNotEquals(200, result.getCode());
                assertEquals(40001, result.getCode());
                assertEquals("缺少租户ID", result.getMessage());
            }
        }

        @Test
        @DisplayName("根据事件类型获取事件缺少租户ID应该返回错误")
        void getEventsByTypeMissingTenantIdShouldReturnError() {
            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(null);
                
                ApiResponse<List<Event>> result = eventController.getEventsByType(TEST_EVENT_TYPE);

                // Then
                assertNotNull(result);
                assertNotEquals(200, result.getCode());
                assertEquals(40001, result.getCode());
                assertEquals("缺少租户ID", result.getMessage());
            }
        }

        @Test
        @DisplayName("根据时间范围获取事件缺少租户ID应该返回错误")
        void getEventsByTimeRangeMissingTenantIdShouldReturnError() {
            // Given
            Instant startTime = Instant.now().minusSeconds(3600);
            Instant endTime = Instant.now();

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(null);
                
                ApiResponse<List<Event>> result = eventController.getEventsByTimeRange(startTime, endTime);

                // Then
                assertNotNull(result);
                assertNotEquals(200, result.getCode());
                assertEquals(40001, result.getCode());
                assertEquals("缺少租户ID", result.getMessage());
            }
        }

        @Test
        @DisplayName("根据关联ID获取事件缺少租户ID应该返回错误")
        void getEventsByCorrelationIdMissingTenantIdShouldReturnError() {
            // Given
            String correlationId = "corr-123";

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(null);
                
                ApiResponse<List<Event>> result = eventController.getEventsByCorrelationId(correlationId);

                // Then
                assertNotNull(result);
                assertNotEquals(200, result.getCode());
                assertEquals(40001, result.getCode());
                assertEquals("缺少租户ID", result.getMessage());
            }
        }
    }

    private EventController.EventRequest createValidEventRequest() {
        EventController.EventRequest request = new EventController.EventRequest();
        request.setEventType(TEST_EVENT_TYPE);
        request.setSource(TEST_SOURCE);
        request.setData(TEST_DATA);
        request.setCorrelationId("corr-123");
        return request;
    }

    private Event createValidEvent() {
        Event event = new Event();
        event.setEventType(TEST_EVENT_TYPE);
        event.setSource(TEST_SOURCE);
        event.setData(TEST_DATA);
        event.setTenantId(TEST_TENANT_ID);
        event.setTimestamp(Instant.now());
        return event;
    }
}
