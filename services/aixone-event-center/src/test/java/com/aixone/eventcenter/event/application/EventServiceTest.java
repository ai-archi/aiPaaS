package com.aixone.eventcenter.event.application;

import com.aixone.common.exception.BizException;
import com.aixone.eventcenter.event.EventService;
import com.aixone.eventcenter.event.domain.Event;
import com.aixone.eventcenter.event.domain.EventRepository;
import com.aixone.eventcenter.event.infrastructure.KafkaEventPublisher;
import com.aixone.session.SessionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
 * EventService 应用服务测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EventService 应用服务测试")
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private KafkaEventPublisher kafkaEventPublisher;

    @InjectMocks
    private EventService eventService;

    private final String TEST_TENANT_ID = "tenant-001";

    @BeforeEach
    void setUp() {
        // 模拟 SessionContext
        try (MockedStatic<SessionContext> mockedSessionContext = mockStatic(SessionContext.class)) {
            mockedSessionContext.when(SessionContext::getTenantId).thenReturn(TEST_TENANT_ID);
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
            Event result = eventService.publishEvent(event);

            // Then
            assertNotNull(result);
            assertEquals(savedEvent, result);
            verify(eventRepository).save(any(Event.class));
        }

        @Test
        @DisplayName("空事件应该抛出异常")
        void nullEventShouldThrowException() {
            // When & Then
            assertThrows(BizException.class, () -> 
                eventService.publishEvent(null));
        }

        @Test
        @DisplayName("空事件类型应该抛出异常")
        void nullEventTypeShouldThrowException() {
            // Given
            Event event = createValidEvent();
            event.setEventType(null);

            // When & Then
            assertThrows(BizException.class, () -> 
                eventService.publishEvent(event));
        }

        @Test
        @DisplayName("空白事件类型应该抛出异常")
        void blankEventTypeShouldThrowException() {
            // Given
            Event event = createValidEvent();
            event.setEventType("   ");

            // When & Then
            assertThrows(BizException.class, () -> 
                eventService.publishEvent(event));
        }

        @Test
        @DisplayName("保存失败应该抛出异常")
        void saveFailureShouldThrowException() {
            // Given
            Event event = createValidEvent();
            
            when(eventRepository.save(any(Event.class)))
                .thenThrow(new RuntimeException("Database error"));

            // When & Then
            assertThrows(BizException.class, () -> 
                eventService.publishEvent(event));
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
            Event event = createValidEvent();
            Event savedEvent = createValidEvent();
            savedEvent.setEventId(1L);
            
            when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);
            doNothing().when(kafkaEventPublisher).publishEvent(anyString(), any(Event.class));

            // When
            Event result = eventService.publishEventToKafka(topicName, event);

            // Then
            assertNotNull(result);
            assertEquals(savedEvent, result);
            verify(eventRepository).save(any(Event.class));
            verify(kafkaEventPublisher).publishEvent(topicName, savedEvent);
        }

        @Test
        @DisplayName("空Topic名称应该抛出异常")
        void nullTopicNameShouldThrowException() {
            // Given
            Event event = createValidEvent();

            // When & Then
            assertThrows(BizException.class, () -> 
                eventService.publishEventToKafka(null, event));
        }

        @Test
        @DisplayName("空白Topic名称应该抛出异常")
        void blankTopicNameShouldThrowException() {
            // Given
            Event event = createValidEvent();

            // When & Then
            assertThrows(BizException.class, () -> 
                eventService.publishEventToKafka("   ", event));
        }

        @Test
        @DisplayName("空事件应该抛出异常")
        void nullEventShouldThrowException() {
            // Given
            String topicName = "test-topic";

            // When & Then
            assertThrows(BizException.class, () -> 
                eventService.publishEventToKafka(topicName, null));
        }

        @Test
        @DisplayName("Kafka发布失败应该抛出异常")
        void kafkaPublishFailureShouldThrowException() {
            // Given
            String topicName = "test-topic";
            Event event = createValidEvent();
            Event savedEvent = createValidEvent();
            savedEvent.setEventId(1L);
            
            when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);
            doThrow(new RuntimeException("Kafka error"))
                .when(kafkaEventPublisher).publishEvent(anyString(), any(Event.class));

            // When & Then
            assertThrows(BizException.class, () -> 
                eventService.publishEventToKafka(topicName, event));
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
            List<Event> result = eventService.getAllEvents();

            // Then
            assertEquals(2, result.size());
            assertEquals(events, result);
            verify(eventRepository).findByTenantId(TEST_TENANT_ID);
        }

        @Test
        @DisplayName("应该成功根据ID获取事件")
        void shouldGetEventByIdSuccessfully() {
            // Given
            Long eventId = 1L;
            Event event = createValidEvent();
            event.setEventId(eventId);
            
            when(eventRepository.findByEventIdAndTenantId(eventId, TEST_TENANT_ID))
                .thenReturn(Optional.of(event));

            // When
            Optional<Event> result = eventService.getEventById(eventId);

            // Then
            assertTrue(result.isPresent());
            assertEquals(event, result.get());
            verify(eventRepository).findByEventIdAndTenantId(eventId, TEST_TENANT_ID);
        }

        @Test
        @DisplayName("事件不存在应该返回空")
        void nonExistentEventShouldReturnEmpty() {
            // Given
            Long eventId = 999L;
            
            when(eventRepository.findByEventIdAndTenantId(eventId, TEST_TENANT_ID))
                .thenReturn(Optional.empty());

            // When
            Optional<Event> result = eventService.getEventById(eventId);

            // Then
            assertFalse(result.isPresent());
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
            Instant beforePublish = Instant.now();
            
            when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
                Event savedEvent = invocation.getArgument(0);
                assertTrue(savedEvent.getTimestamp().isAfter(beforePublish) || 
                          savedEvent.getTimestamp().equals(beforePublish));
                return savedEvent;
            });

            // When
            eventService.publishEvent(event);

            // Then
            verify(eventRepository).save(any(Event.class));
        }

        @Test
        @DisplayName("发布到Kafka应该设置当前时间戳")
        void publishToKafkaShouldSetCurrentTimestamp() {
            // Given
            String topicName = "test-topic";
            Event event = createValidEvent();
            Instant beforePublish = Instant.now();
            
            when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
                Event savedEvent = invocation.getArgument(0);
                assertTrue(savedEvent.getTimestamp().isAfter(beforePublish) || 
                          savedEvent.getTimestamp().equals(beforePublish));
                return savedEvent;
            });
            doNothing().when(kafkaEventPublisher).publishEvent(anyString(), any(Event.class));

            // When
            eventService.publishEventToKafka(topicName, event);

            // Then
            verify(eventRepository).save(any(Event.class));
        }
    }

    // 辅助方法
    private Event createValidEvent() {
        Event event = new Event();
        event.setEventType("USER_CREATED");
        event.setSource("user-service");
        event.setData("{\"userId\":\"123\",\"name\":\"John Doe\"}");
        event.setTenantId(TEST_TENANT_ID);
        event.setTimestamp(Instant.now());
        event.setVersion(1);
        return event;
    }
}
