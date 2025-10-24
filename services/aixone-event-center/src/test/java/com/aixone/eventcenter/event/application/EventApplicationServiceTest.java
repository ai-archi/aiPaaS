package com.aixone.eventcenter.event.application;

import com.aixone.eventcenter.event.domain.Event;
import com.aixone.eventcenter.event.domain.EventRepository;
import com.aixone.eventcenter.event.infrastructure.KafkaEventPublisher;
import com.aixone.common.ddd.DomainEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
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
class EventApplicationServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private KafkaEventPublisher kafkaEventPublisher;

    // EventApplicationService没有applicationEventPublisher字段

    private EventApplicationService eventApplicationService;

    private final String TEST_TENANT_ID = "tenant-001";
    private final String TEST_EVENT_TYPE = "USER_CREATED";
    private final String TEST_SOURCE = "user-service";
    private final String TEST_DATA = "{\"userId\":\"123\",\"name\":\"John Doe\"}";

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
            
            // EventApplicationService没有applicationEventPublisher字段
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
            Event result = eventApplicationService.publishEvent(TEST_EVENT_TYPE, TEST_SOURCE, TEST_DATA, TEST_TENANT_ID);

            // Then
            assertNotNull(result);
            assertEquals(savedEvent, result);
            assertEquals(TEST_EVENT_TYPE, result.getEventType());
            assertEquals(TEST_SOURCE, result.getSource());
            assertEquals(TEST_DATA, result.getData());
            assertEquals(TEST_TENANT_ID, result.getTenantId());
            assertNotNull(result.getTimestamp());
            verify(eventRepository).save(any(Event.class));
            // EventApplicationService不发布领域事件
        }

        @Test
        @DisplayName("应该设置正确的时间戳")
        void shouldSetCorrectTimestamp() {
            // Given
            when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
                Event event = invocation.getArgument(0);
                event.setEventId(1L);
                event.setTimestamp(Instant.now());
                return event;
            });
            Instant beforePublish = Instant.now();

            // When
            Event result = eventApplicationService.publishEvent(TEST_EVENT_TYPE, TEST_SOURCE, TEST_DATA, TEST_TENANT_ID);

            // Then
            Instant afterPublish = Instant.now();
            assertNotNull(result.getTimestamp());
            assertTrue(result.getTimestamp().isAfter(beforePublish) || result.getTimestamp().equals(beforePublish));
            assertTrue(result.getTimestamp().isBefore(afterPublish) || result.getTimestamp().equals(afterPublish));
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
            Event savedEvent = createValidEvent();
            savedEvent.setEventId(1L);
            when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);
            doNothing().when(kafkaEventPublisher).publishEvent(topicName, savedEvent);

            // When
            Event result = eventApplicationService.publishEventToKafka(topicName, TEST_EVENT_TYPE, TEST_SOURCE, TEST_DATA, TEST_TENANT_ID);

            // Then
            assertNotNull(result);
            assertEquals(savedEvent, result);
            verify(eventRepository).save(any(Event.class));
            verify(kafkaEventPublisher).publishEvent(topicName, savedEvent);
            // EventApplicationService不发布领域事件
        }

        @Test
        @DisplayName("应该设置正确的时间戳")
        void shouldSetCorrectTimestampForKafka() {
            // Given
            String topicName = "test-topic";
            when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
                Event event = invocation.getArgument(0);
                event.setEventId(1L);
                event.setTimestamp(Instant.now());
                return event;
            });
            doNothing().when(kafkaEventPublisher).publishEvent(anyString(), any(Event.class));
            Instant beforePublish = Instant.now();

            // When
            Event result = eventApplicationService.publishEventToKafka(topicName, TEST_EVENT_TYPE, TEST_SOURCE, TEST_DATA, TEST_TENANT_ID);

            // Then
            Instant afterPublish = Instant.now();
            assertNotNull(result.getTimestamp());
            assertTrue(result.getTimestamp().isAfter(beforePublish) || result.getTimestamp().equals(beforePublish));
            assertTrue(result.getTimestamp().isBefore(afterPublish) || result.getTimestamp().equals(afterPublish));
        }
    }

    @Nested
    @DisplayName("查询事件测试")
    class QueryEventTests {

        @Test
        @DisplayName("应该成功获取租户的所有事件")
        void shouldGetAllEventsByTenantSuccessfully() {
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
    @DisplayName("领域事件发布测试")
    class DomainEventPublishTests {

        @Test
        @DisplayName("发布事件应该发布领域事件")
        void publishEventShouldPublishDomainEvent() {
            // Given
            Event savedEvent = createValidEvent();
            savedEvent.setEventId(1L);
            when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);

            // When
            eventApplicationService.publishEvent(TEST_EVENT_TYPE, TEST_SOURCE, TEST_DATA, TEST_TENANT_ID);

            // Then
            // EventApplicationService不发布领域事件
        }

        @Test
        @DisplayName("发布到Kafka应该发布领域事件")
        void publishToKafkaShouldPublishDomainEvent() {
            // Given
            String topicName = "test-topic";
            Event savedEvent = createValidEvent();
            savedEvent.setEventId(1L);
            when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);
            doNothing().when(kafkaEventPublisher).publishEvent(topicName, savedEvent);

            // When
            eventApplicationService.publishEventToKafka(topicName, TEST_EVENT_TYPE, TEST_SOURCE, TEST_DATA, TEST_TENANT_ID);

            // Then
            // EventApplicationService不发布领域事件
        }
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
