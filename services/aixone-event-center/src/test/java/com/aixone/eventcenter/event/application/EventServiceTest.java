package com.aixone.eventcenter.event.application;

import com.aixone.common.exception.BizException;
import com.aixone.eventcenter.event.EventService;
import com.aixone.eventcenter.event.domain.Event;
import com.aixone.eventcenter.event.EventRepository;
import com.aixone.eventcenter.event.infrastructure.KafkaEventPublisher;
import com.aixone.common.session.SessionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * EventService 应用服务测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EventService 应用服务测试")
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private KafkaEventPublisher kafkaEventPublisher;

    private EventService eventService;

    private final String TEST_TENANT_ID = "tenant-001";

    @BeforeEach
    void setUp() {
        // 手动创建 EventService 实例并设置依赖
        eventService = new EventService();
        // 使用反射设置私有字段
        try {
            java.lang.reflect.Field eventRepositoryField = EventService.class.getDeclaredField("eventRepository");
            eventRepositoryField.setAccessible(true);
            eventRepositoryField.set(eventService, eventRepository);
            
            java.lang.reflect.Field kafkaEventPublisherField = EventService.class.getDeclaredField("kafkaEventPublisher");
            kafkaEventPublisherField.setAccessible(true);
            kafkaEventPublisherField.set(eventService, kafkaEventPublisher);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up EventService dependencies", e);
        }
    }

    @Nested
    @DisplayName("发布事件测试")
    class PublishEventTests {

        @Test
        @DisplayName("应该成功发布事件")
        void shouldPublishEventSuccessfully() {
            // Given
            Event event = createValidEvent();
            Event savedEvent = createValidEvent();
            savedEvent.setEventId(1L);
            
            when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                Event result = eventService.publishEvent(event);

                // Then
                assertNotNull(result);
                assertEquals(savedEvent, result);
                verify(eventRepository).save(any(Event.class));
            }
        }

        @Test
        @DisplayName("空事件应该抛出异常")
        void nullEventShouldThrowException() {
            // When & Then
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                assertThrows(BizException.class, () -> eventService.publishEvent(null));
            }
        }

        @Test
        @DisplayName("事件类型为空应该抛出异常")
        void eventWithNullEventTypeShouldThrowException() {
            // Given
            Event event = createValidEvent();
            event.setEventType(null);

            // When & Then
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                assertThrows(BizException.class, () -> eventService.publishEvent(event));
            }
        }

        @Test
        @DisplayName("事件类型为空字符串应该抛出异常")
        void eventWithEmptyEventTypeShouldThrowException() {
            // Given
            Event event = createValidEvent();
            event.setEventType("");

            // When & Then
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                assertThrows(BizException.class, () -> eventService.publishEvent(event));
            }
        }

        @Test
        @DisplayName("数据库保存失败应该抛出异常")
        void databaseSaveFailureShouldThrowException() {
            // Given
            Event event = createValidEvent();
            when(eventRepository.save(any(Event.class))).thenThrow(new RuntimeException("Database error"));

            // When & Then
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                BizException exception = assertThrows(BizException.class, () -> eventService.publishEvent(event));
                assertEquals("EVENT_PUBLISH_FAILED", exception.getErrorCode());
                assertTrue(exception.getMessage().contains("事件发布失败"));
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
            when(eventRepository.findByTenantId(TEST_TENANT_ID)).thenReturn(events);

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                List<Event> result = eventService.getAllEvents();

                // Then
                assertNotNull(result);
                assertEquals(2, result.size());
                verify(eventRepository).findByTenantId(TEST_TENANT_ID);
            }
        }

        @Test
        @DisplayName("应该成功根据ID获取事件")
        void shouldGetEventByIdSuccessfully() {
            // Given
            Long eventId = 1L;
            Event event = createValidEvent();
            event.setEventId(eventId);
            when(eventRepository.findByEventIdAndTenantId(eventId, TEST_TENANT_ID)).thenReturn(Optional.of(event));

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                Optional<Event> result = eventService.getEventById(eventId);

                // Then
                assertTrue(result.isPresent());
                assertEquals(eventId, result.get().getEventId());
                verify(eventRepository).findByEventIdAndTenantId(eventId, TEST_TENANT_ID);
            }
        }

        @Test
        @DisplayName("不存在的事件应该返回空")
        void nonExistentEventShouldReturnEmpty() {
            // Given
            Long eventId = 999L;
            when(eventRepository.findByEventIdAndTenantId(eventId, TEST_TENANT_ID)).thenReturn(Optional.empty());

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                Optional<Event> result = eventService.getEventById(eventId);

                // Then
                assertFalse(result.isPresent());
                verify(eventRepository).findByEventIdAndTenantId(eventId, TEST_TENANT_ID);
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
            Event event = createValidEvent();
            Event savedEvent = createValidEvent();
            savedEvent.setEventId(1L);
            String topic = "test-topic";
            
            when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);
            doNothing().when(kafkaEventPublisher).publishEvent(anyString(), any(Event.class));

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                Event result = eventService.publishEventToKafka(topic, event);

                // Then
                assertNotNull(result);
                assertEquals(savedEvent, result);
                verify(eventRepository).save(any(Event.class));
                verify(kafkaEventPublisher).publishEvent(anyString(), any(Event.class));
            }
        }

        @Test
        @DisplayName("Topic名称为空应该抛出异常")
        void nullTopicNameShouldThrowException() {
            // Given
            Event event = createValidEvent();

            // When & Then
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                assertThrows(BizException.class, () -> eventService.publishEventToKafka(null, event));
            }
        }

        @Test
        @DisplayName("Topic名称为空字符串应该抛出异常")
        void emptyTopicNameShouldThrowException() {
            // Given
            Event event = createValidEvent();

            // When & Then
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                assertThrows(BizException.class, () -> eventService.publishEventToKafka("", event));
            }
        }

        @Test
        @DisplayName("Kafka发布失败应该抛出异常")
        void kafkaPublishFailureShouldThrowException() {
            // Given
            Event event = createValidEvent();
            Event savedEvent = createValidEvent();
            savedEvent.setEventId(1L);
            String topic = "test-topic";
            
            when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);
            doThrow(new RuntimeException("Kafka error")).when(kafkaEventPublisher).publishEvent(topic, event);

            // When & Then
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                BizException exception = assertThrows(BizException.class, () -> eventService.publishEventToKafka(topic, event));
                assertEquals("EVENT_PUBLISH_KAFKA_FAILED", exception.getErrorCode());
                assertTrue(exception.getMessage().contains("事件发布到Kafka失败"));
            }
        }
    }

    @Nested
    @DisplayName("事件时间戳测试")
    class EventTimestampTests {

        @Test
        @DisplayName("发布事件应该设置当前时间戳")
        void publishEventShouldSetCurrentTimestamp() {
            // Given
            Event event = createValidEvent();
            
            when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
                Event eventArg = invocation.getArgument(0);
                eventArg.setEventId(1L);
                return eventArg;
            });

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                Event result = eventService.publishEvent(event);

                // Then
                assertNotNull(result);
                assertNotNull(result.getTimestamp());
                verify(eventRepository).save(any(Event.class));
            }
        }

        @Test
        @DisplayName("发布到Kafka应该设置当前时间戳")
        void publishToKafkaShouldSetCurrentTimestamp() {
            // Given
            Event event = createValidEvent();
            String topic = "test-topic";
            
            when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
                Event eventArg = invocation.getArgument(0);
                eventArg.setEventId(1L);
                return eventArg;
            });
            doNothing().when(kafkaEventPublisher).publishEvent(anyString(), any(Event.class));

            // When
            try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
                mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
                
                Event result = eventService.publishEventToKafka(topic, event);

                // Then
                assertNotNull(result);
                assertNotNull(result.getTimestamp());
                verify(eventRepository).save(any(Event.class));
                verify(kafkaEventPublisher).publishEvent(anyString(), any(Event.class));
            }
        }
    }

    private Event createValidEvent() {
        Event event = new Event();
        event.setEventType("test-event");
        event.setData("test data");
        event.setTenantId(TEST_TENANT_ID);
        return event;
    }
}
