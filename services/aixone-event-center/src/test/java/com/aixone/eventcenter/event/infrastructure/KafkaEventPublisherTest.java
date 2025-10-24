package com.aixone.eventcenter.event.infrastructure;

import com.aixone.eventcenter.event.domain.Event;
import com.aixone.eventcenter.event.domain.Topic;
import com.aixone.eventcenter.event.domain.TopicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import java.util.concurrent.CompletableFuture;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * KafkaEventPublisher 基础设施测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("KafkaEventPublisher 基础设施测试")
class KafkaEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private TopicRepository topicRepository;

    private KafkaEventPublisher kafkaEventPublisher;

    private final String TEST_TOPIC_NAME = "test-topic";
    private final String TEST_EVENT_TYPE = "USER_CREATED";
    private final String TEST_SOURCE = "user-service";
    private final String TEST_DATA = "{\"userId\":\"123\",\"name\":\"John Doe\"}";
    private final String TEST_TENANT_ID = "tenant-001";

    @BeforeEach
    void setUp() {
        kafkaEventPublisher = new KafkaEventPublisher();
        // 使用反射设置私有字段
        try {
            java.lang.reflect.Field kafkaTemplateField = KafkaEventPublisher.class.getDeclaredField("kafkaTemplate");
            kafkaTemplateField.setAccessible(true);
            kafkaTemplateField.set(kafkaEventPublisher, kafkaTemplate);
            
            java.lang.reflect.Field topicRepositoryField = KafkaEventPublisher.class.getDeclaredField("topicRepository");
            topicRepositoryField.setAccessible(true);
            topicRepositoryField.set(kafkaEventPublisher, topicRepository);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up KafkaEventPublisher dependencies", e);
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
            Topic topic = createValidTopic();
            CompletableFuture<SendResult<String, Object>> future = createSuccessfulFuture();
            
            when(topicRepository.findByName(TEST_TOPIC_NAME)).thenReturn(Optional.of(topic));
            when(kafkaTemplate.send(eq(TEST_TOPIC_NAME), eq(event.getEventId().toString()), eq(event)))
                .thenReturn(future);

            // When
            kafkaEventPublisher.publishEvent(TEST_TOPIC_NAME, event);

            // Then
            verify(topicRepository).findByName(TEST_TOPIC_NAME);
            verify(kafkaTemplate).send(TEST_TOPIC_NAME, event.getEventId().toString(), event);
        }

        @Test
        @DisplayName("Topic未注册应该抛出异常")
        void unregisteredTopicShouldThrowException() {
            // Given
            Event event = createValidEvent();
            when(topicRepository.findByName(TEST_TOPIC_NAME)).thenReturn(Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> kafkaEventPublisher.publishEvent(TEST_TOPIC_NAME, event));
            assertTrue(exception.getMessage().contains("Topic未注册: " + TEST_TOPIC_NAME));
            verify(topicRepository).findByName(TEST_TOPIC_NAME);
            verify(kafkaTemplate, never()).send(anyString(), anyString(), any());
        }

        @Test
        @DisplayName("Topic未激活应该抛出异常")
        void inactiveTopicShouldThrowException() {
            // Given
            Event event = createValidEvent();
            Topic topic = createValidTopic();
            topic.setStatus(Topic.TopicStatus.INACTIVE);
            
            when(topicRepository.findByName(TEST_TOPIC_NAME)).thenReturn(Optional.of(topic));

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> kafkaEventPublisher.publishEvent(TEST_TOPIC_NAME, event));
            assertTrue(exception.getMessage().contains("Topic未激活: " + TEST_TOPIC_NAME));
            verify(topicRepository).findByName(TEST_TOPIC_NAME);
            verify(kafkaTemplate, never()).send(anyString(), anyString(), any());
        }

        @Test
        @DisplayName("Kafka发送失败应该抛出异常")
        void kafkaSendFailureShouldThrowException() {
            // Given
            Event event = createValidEvent();
            Topic topic = createValidTopic();
            
            when(topicRepository.findByName(TEST_TOPIC_NAME)).thenReturn(Optional.of(topic));
            when(kafkaTemplate.send(eq(TEST_TOPIC_NAME), eq(event.getEventId().toString()), eq(event)))
                .thenThrow(new RuntimeException("Kafka connection failed"));

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> kafkaEventPublisher.publishEvent(TEST_TOPIC_NAME, event));
            assertTrue(exception.getMessage().contains("事件发布失败"));
            verify(topicRepository).findByName(TEST_TOPIC_NAME);
            verify(kafkaTemplate).send(TEST_TOPIC_NAME, event.getEventId().toString(), event);
        }
    }

    @Nested
    @DisplayName("使用自定义Key发布事件测试")
    class PublishEventWithCustomKeyTests {

        @Test
        @DisplayName("应该成功使用自定义Key发布事件")
        void shouldPublishEventWithCustomKeySuccessfully() {
            // Given
            String customKey = "custom-key-123";
            Event event = createValidEvent();
            Topic topic = createValidTopic();
            CompletableFuture<SendResult<String, Object>> future = createSuccessfulFuture();
            
            when(topicRepository.findByName(TEST_TOPIC_NAME)).thenReturn(Optional.of(topic));
            when(kafkaTemplate.send(eq(TEST_TOPIC_NAME), eq(customKey), eq(event)))
                .thenReturn(future);

            // When
            kafkaEventPublisher.publishEvent(TEST_TOPIC_NAME, customKey, event);

            // Then
            verify(topicRepository).findByName(TEST_TOPIC_NAME);
            verify(kafkaTemplate).send(TEST_TOPIC_NAME, customKey, event);
        }

        @Test
        @DisplayName("Topic未注册应该抛出异常")
        void unregisteredTopicShouldThrowException() {
            // Given
            String customKey = "custom-key-123";
            Event event = createValidEvent();
            when(topicRepository.findByName(TEST_TOPIC_NAME)).thenReturn(Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> kafkaEventPublisher.publishEvent(TEST_TOPIC_NAME, customKey, event));
            assertTrue(exception.getMessage().contains("Topic未注册: " + TEST_TOPIC_NAME));
            verify(topicRepository).findByName(TEST_TOPIC_NAME);
            verify(kafkaTemplate, never()).send(anyString(), anyString(), any());
        }

        @Test
        @DisplayName("Topic未激活应该抛出异常")
        void inactiveTopicShouldThrowException() {
            // Given
            String customKey = "custom-key-123";
            Event event = createValidEvent();
            Topic topic = createValidTopic();
            topic.setStatus(Topic.TopicStatus.INACTIVE);
            
            when(topicRepository.findByName(TEST_TOPIC_NAME)).thenReturn(Optional.of(topic));

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> kafkaEventPublisher.publishEvent(TEST_TOPIC_NAME, customKey, event));
            assertTrue(exception.getMessage().contains("Topic未激活: " + TEST_TOPIC_NAME));
            verify(topicRepository).findByName(TEST_TOPIC_NAME);
            verify(kafkaTemplate, never()).send(anyString(), anyString(), any());
        }

        @Test
        @DisplayName("Kafka发送失败应该抛出异常")
        void kafkaSendFailureShouldThrowException() {
            // Given
            String customKey = "custom-key-123";
            Event event = createValidEvent();
            Topic topic = createValidTopic();
            
            when(topicRepository.findByName(TEST_TOPIC_NAME)).thenReturn(Optional.of(topic));
            when(kafkaTemplate.send(eq(TEST_TOPIC_NAME), eq(customKey), eq(event)))
                .thenThrow(new RuntimeException("Kafka connection failed"));

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> kafkaEventPublisher.publishEvent(TEST_TOPIC_NAME, customKey, event));
            assertTrue(exception.getMessage().contains("事件发布失败"));
            verify(topicRepository).findByName(TEST_TOPIC_NAME);
            verify(kafkaTemplate).send(TEST_TOPIC_NAME, customKey, event);
        }
    }

    @Nested
    @DisplayName("Topic状态验证测试")
    class TopicStatusValidationTests {

        @Test
        @DisplayName("应该验证Topic的PENDING状态")
        void shouldValidateTopicPendingStatus() {
            // Given
            Event event = createValidEvent();
            Topic topic = createValidTopic();
            topic.setStatus(Topic.TopicStatus.PENDING);
            
            when(topicRepository.findByName(TEST_TOPIC_NAME)).thenReturn(Optional.of(topic));

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> kafkaEventPublisher.publishEvent(TEST_TOPIC_NAME, event));
            assertTrue(exception.getMessage().contains("Topic未激活: " + TEST_TOPIC_NAME));
        }

        @Test
        @DisplayName("应该验证Topic的DISABLED状态")
        void shouldValidateTopicDisabledStatus() {
            // Given
            Event event = createValidEvent();
            Topic topic = createValidTopic();
            topic.setStatus(Topic.TopicStatus.INACTIVE);
            
            when(topicRepository.findByName(TEST_TOPIC_NAME)).thenReturn(Optional.of(topic));

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> kafkaEventPublisher.publishEvent(TEST_TOPIC_NAME, event));
            assertTrue(exception.getMessage().contains("Topic未激活: " + TEST_TOPIC_NAME));
        }
    }

    private Event createValidEvent() {
        Event event = new Event();
        event.setEventId(1L);
        event.setEventType(TEST_EVENT_TYPE);
        event.setSource(TEST_SOURCE);
        event.setData(TEST_DATA);
        event.setTenantId(TEST_TENANT_ID);
        event.setTimestamp(Instant.now());
        return event;
    }

    private Topic createValidTopic() {
        Topic topic = new Topic();
        topic.setName(TEST_TOPIC_NAME);
        topic.setStatus(Topic.TopicStatus.ACTIVE);
        topic.setTenantId(TEST_TENANT_ID);
        return topic;
    }

    private CompletableFuture<SendResult<String, Object>> createSuccessfulFuture() {
        return CompletableFuture.completedFuture(new SendResult<>(null, null));
    }
}
