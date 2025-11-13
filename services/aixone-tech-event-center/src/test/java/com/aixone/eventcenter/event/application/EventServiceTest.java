package com.aixone.eventcenter.event.application;

import com.aixone.eventcenter.event.domain.Event;
import com.aixone.eventcenter.event.domain.EventRepository;
import com.aixone.eventcenter.event.infrastructure.KafkaEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * EventApplicationService 应用服务测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EventApplicationService 应用服务测试")
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private KafkaEventPublisher kafkaEventPublisher;

    @Mock
    private EventRoutingService eventRoutingService;

    private EventApplicationService eventApplicationService;

    private final String TEST_TENANT_ID = "tenant-001";

    @BeforeEach
    void setUp() {
        eventApplicationService = new EventApplicationService();
        // 使用反射设置私有字段
        try {
            java.lang.reflect.Field eventRepositoryField = EventApplicationService.class.getDeclaredField("eventRepository");
            eventRepositoryField.setAccessible(true);
            eventRepositoryField.set(eventApplicationService, eventRepository);
            
            java.lang.reflect.Field kafkaEventPublisherField = EventApplicationService.class.getDeclaredField("kafkaEventPublisher");
            kafkaEventPublisherField.setAccessible(true);
            kafkaEventPublisherField.set(eventApplicationService, kafkaEventPublisher);
            
            java.lang.reflect.Field eventRoutingServiceField = EventApplicationService.class.getDeclaredField("eventRoutingService");
            eventRoutingServiceField.setAccessible(true);
            eventRoutingServiceField.set(eventApplicationService, eventRoutingService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up EventApplicationService dependencies", e);
        }
    }

    @Nested
    @DisplayName("发布事件测试")
    class PublishEventTests {

        @Test
        @DisplayName("应该成功发布事件")
        void shouldPublishEventSuccessfully() {
            // Given
            Event savedEvent = createValidEvent();
            savedEvent.setEventId(1L);
            
            when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);

            // When
            Event result = eventApplicationService.publishEvent("test-event", "test-source", "test data", TEST_TENANT_ID);

            // Then
            assertNotNull(result);
            assertEquals(savedEvent, result);
            verify(eventRepository).save(any(Event.class));
            verify(eventRoutingService).routeAndDistributeAsync(any(Event.class));
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
            List<Event> result = eventApplicationService.getEventsByTenant(TEST_TENANT_ID);

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(eventRepository).findByTenantId(TEST_TENANT_ID);
        }

        @Test
        @DisplayName("应该成功根据ID获取事件")
        void shouldGetEventByIdSuccessfully() {
            // Given
            Long eventId = 1L;
            Event event = createValidEvent();
            event.setEventId(eventId);
            when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

            // When
            Optional<Event> result = eventApplicationService.getEventById(eventId, TEST_TENANT_ID);

            // Then
            assertTrue(result.isPresent());
            assertEquals(eventId, result.get().getEventId());
            verify(eventRepository).findById(eventId);
        }

        @Test
        @DisplayName("不存在的事件应该返回空")
        void nonExistentEventShouldReturnEmpty() {
            // Given
            Long eventId = 999L;
            when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

            // When
            Optional<Event> result = eventApplicationService.getEventById(eventId, TEST_TENANT_ID);

            // Then
            assertFalse(result.isPresent());
            verify(eventRepository).findById(eventId);
        }
    }

    @Nested
    @DisplayName("发布事件到Kafka测试")
    class PublishEventToKafkaTests {

        @Test
        @DisplayName("应该成功发布事件到Kafka")
        void shouldPublishEventToKafkaSuccessfully() {
            // Given
            Event savedEvent = createValidEvent();
            savedEvent.setEventId(1L);
            String topic = "test-topic";
            
            when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);
            doNothing().when(kafkaEventPublisher).publishEvent(anyString(), any(Event.class));

            // When
            Event result = eventApplicationService.publishEventToKafka(topic, "test-event", "test-source", "test data", TEST_TENANT_ID);

            // Then
            assertNotNull(result);
            assertEquals(savedEvent, result);
            verify(eventRepository, atLeastOnce()).save(any(Event.class));
            verify(kafkaEventPublisher).publishEvent(anyString(), any(Event.class));
            verify(eventRoutingService).routeAndDistributeAsync(any(Event.class));
        }
    }

    private Event createValidEvent() {
        Event event = new Event("test-event", "test-source", "test data", TEST_TENANT_ID);
        return event;
    }
}
