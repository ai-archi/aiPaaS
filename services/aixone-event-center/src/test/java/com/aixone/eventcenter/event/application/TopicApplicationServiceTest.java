package com.aixone.eventcenter.event.application;

import com.aixone.eventcenter.event.domain.Topic;
import com.aixone.eventcenter.event.domain.TopicRepository;
import com.aixone.eventcenter.event.infrastructure.KafkaTopicManager;
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
 * TopicApplicationService 应用服务测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TopicApplicationService 应用服务测试")
class TopicApplicationServiceTest {

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private KafkaTopicManager kafkaTopicManager;

    private TopicApplicationService topicApplicationService;

    private final String TEST_TENANT_ID = "tenant-001";
    private final String TEST_TOPIC_NAME = "test-topic";
    private final String TEST_OWNER = "test-owner";
    private final String TEST_DESCRIPTION = "Test topic description";

    @BeforeEach
    void setUp() {
        topicApplicationService = new TopicApplicationService();
        // 使用反射设置私有字段
        try {
            java.lang.reflect.Field topicRepositoryField = TopicApplicationService.class.getDeclaredField("topicRepository");
            topicRepositoryField.setAccessible(true);
            topicRepositoryField.set(topicApplicationService, topicRepository);
            
            java.lang.reflect.Field kafkaTopicManagerField = TopicApplicationService.class.getDeclaredField("kafkaTopicManager");
            kafkaTopicManagerField.setAccessible(true);
            kafkaTopicManagerField.set(topicApplicationService, kafkaTopicManager);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up TopicApplicationService dependencies", e);
        }
    }

    @Nested
    @DisplayName("注册Topic测试")
    class RegisterTopicTests {

        @Test
        @DisplayName("应该成功注册Topic")
        void shouldRegisterTopicSuccessfully() {
            // Given
            Topic savedTopic = createValidTopic();
            savedTopic.setTopicId(1L);
            when(topicRepository.existsByName(TEST_TOPIC_NAME)).thenReturn(false);
            when(topicRepository.save(any(Topic.class))).thenReturn(savedTopic);
            doNothing().when(kafkaTopicManager).createTopic(anyString(), anyInt(), anyShort());

            // When
            Topic result = topicApplicationService.registerTopic(TEST_TOPIC_NAME, TEST_OWNER, TEST_DESCRIPTION, TEST_TENANT_ID);

            // Then
            assertNotNull(result);
            assertEquals(savedTopic, result);
            assertEquals(TEST_TOPIC_NAME, result.getName());
            assertEquals(TEST_OWNER, result.getOwner());
            assertEquals(TEST_DESCRIPTION, result.getDescription());
            assertEquals(TEST_TENANT_ID, result.getTenantId());
            verify(topicRepository).existsByName(TEST_TOPIC_NAME);
            verify(topicRepository).save(any(Topic.class));
            verify(kafkaTopicManager).createTopic(eq(TEST_TOPIC_NAME), anyInt(), anyShort());
        }

        @Test
        @DisplayName("Topic名称已存在应该抛出异常")
        void existingTopicNameShouldThrowException() {
            // Given
            when(topicRepository.existsByName(TEST_TOPIC_NAME)).thenReturn(true);

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> topicApplicationService.registerTopic(TEST_TOPIC_NAME, TEST_OWNER, TEST_DESCRIPTION, TEST_TENANT_ID));
            assertEquals("Topic已存在: " + TEST_TOPIC_NAME, exception.getMessage());
            verify(topicRepository).existsByName(TEST_TOPIC_NAME);
            verify(topicRepository, never()).save(any(Topic.class));
        }

        @Test
        @DisplayName("Topic名称为空应该抛出异常")
        void nullTopicNameShouldThrowException() {
            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> topicApplicationService.registerTopic(null, TEST_OWNER, TEST_DESCRIPTION, TEST_TENANT_ID));
            assertEquals("Topic名称不能为空", exception.getMessage());
        }

        @Test
        @DisplayName("Topic名称为空字符串应该抛出异常")
        void emptyTopicNameShouldThrowException() {
            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> topicApplicationService.registerTopic("", TEST_OWNER, TEST_DESCRIPTION, TEST_TENANT_ID));
            assertEquals("Topic名称不能为空", exception.getMessage());
        }

        @Test
        @DisplayName("Topic名称包含非法字符应该抛出异常")
        void invalidTopicNameShouldThrowException() {
            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> topicApplicationService.registerTopic("test@topic", TEST_OWNER, TEST_DESCRIPTION, TEST_TENANT_ID));
            assertEquals("Topic名称只能包含字母、数字、点、下划线和连字符", exception.getMessage());
        }

        @Test
        @DisplayName("Topic名称过长应该抛出异常")
        void tooLongTopicNameShouldThrowException() {
            // Given
            String longName = "a".repeat(250);

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> topicApplicationService.registerTopic(longName, TEST_OWNER, TEST_DESCRIPTION, TEST_TENANT_ID));
            assertEquals("Topic名称长度不能超过249个字符", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("查询Topic测试")
    class QueryTopicTests {

        @Test
        @DisplayName("应该成功获取所有Topic")
        void shouldGetAllTopicsSuccessfully() {
            // Given
            List<Topic> topics = Arrays.asList(createValidTopic(), createValidTopic());
            when(topicRepository.findAll()).thenReturn(topics);

            // When
            List<Topic> result = topicApplicationService.getAllTopics();

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(topicRepository).findAll();
        }

        @Test
        @DisplayName("应该成功根据租户获取Topic")
        void shouldGetTopicsByTenantSuccessfully() {
            // Given
            List<Topic> topics = Arrays.asList(createValidTopic(), createValidTopic());
            when(topicRepository.findByTenantId(TEST_TENANT_ID)).thenReturn(topics);

            // When
            List<Topic> result = topicApplicationService.getTopicsByTenant(TEST_TENANT_ID);

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(topicRepository).findByTenantId(TEST_TENANT_ID);
        }

        @Test
        @DisplayName("应该成功根据名称获取Topic")
        void shouldGetTopicByNameSuccessfully() {
            // Given
            Topic topic = createValidTopic();
            topic.setTopicId(1L);
            when(topicRepository.findByName(TEST_TOPIC_NAME)).thenReturn(Optional.of(topic));

            // When
            Optional<Topic> result = topicApplicationService.getTopicByName(TEST_TOPIC_NAME);

            // Then
            assertTrue(result.isPresent());
            assertEquals(TEST_TOPIC_NAME, result.get().getName());
            verify(topicRepository).findByName(TEST_TOPIC_NAME);
        }

        @Test
        @DisplayName("不存在的Topic应该返回空")
        void nonExistentTopicShouldReturnEmpty() {
            // Given
            when(topicRepository.findByName(TEST_TOPIC_NAME)).thenReturn(Optional.empty());

            // When
            Optional<Topic> result = topicApplicationService.getTopicByName(TEST_TOPIC_NAME);

            // Then
            assertFalse(result.isPresent());
            verify(topicRepository).findByName(TEST_TOPIC_NAME);
        }

        @Test
        @DisplayName("应该成功根据状态获取Topic")
        void shouldGetTopicsByStatusSuccessfully() {
            // Given
            List<Topic> topics = Arrays.asList(createValidTopic(), createValidTopic());
            when(topicRepository.findByStatus(Topic.TopicStatus.ACTIVE)).thenReturn(topics);

            // When
            List<Topic> result = topicApplicationService.getTopicsByStatus(Topic.TopicStatus.ACTIVE);

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(topicRepository).findByStatus(Topic.TopicStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("Topic状态管理测试")
    class TopicStatusManagementTests {

        @Test
        @DisplayName("应该成功激活Topic")
        void shouldActivateTopicSuccessfully() {
            // Given
            Topic topic = createValidTopic();
            topic.setTopicId(1L);
            when(topicRepository.findByName(TEST_TOPIC_NAME)).thenReturn(Optional.of(topic));
            when(topicRepository.save(any(Topic.class))).thenReturn(topic);

            // When
            topicApplicationService.activateTopic(TEST_TOPIC_NAME);

            // Then
            verify(topicRepository).findByName(TEST_TOPIC_NAME);
            verify(topicRepository).save(topic);
        }

        @Test
        @DisplayName("激活不存在的Topic应该抛出异常")
        void activateNonExistentTopicShouldThrowException() {
            // Given
            when(topicRepository.findByName(TEST_TOPIC_NAME)).thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> topicApplicationService.activateTopic(TEST_TOPIC_NAME));
            assertEquals("Topic不存在: " + TEST_TOPIC_NAME, exception.getMessage());
            verify(topicRepository).findByName(TEST_TOPIC_NAME);
        }

        @Test
        @DisplayName("应该成功停用Topic")
        void shouldDeactivateTopicSuccessfully() {
            // Given
            Topic topic = createValidTopic();
            topic.setTopicId(1L);
            when(topicRepository.findByName(TEST_TOPIC_NAME)).thenReturn(Optional.of(topic));
            when(topicRepository.save(any(Topic.class))).thenReturn(topic);

            // When
            topicApplicationService.deactivateTopic(TEST_TOPIC_NAME);

            // Then
            verify(topicRepository).findByName(TEST_TOPIC_NAME);
            verify(topicRepository).save(topic);
        }

        @Test
        @DisplayName("停用不存在的Topic应该抛出异常")
        void deactivateNonExistentTopicShouldThrowException() {
            // Given
            when(topicRepository.findByName(TEST_TOPIC_NAME)).thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> topicApplicationService.deactivateTopic(TEST_TOPIC_NAME));
            assertEquals("Topic不存在: " + TEST_TOPIC_NAME, exception.getMessage());
            verify(topicRepository).findByName(TEST_TOPIC_NAME);
        }
    }

    @Nested
    @DisplayName("Topic更新测试")
    class TopicUpdateTests {

        @Test
        @DisplayName("应该成功更新Topic描述")
        void shouldUpdateTopicDescriptionSuccessfully() {
            // Given
            Topic topic = createValidTopic();
            topic.setTopicId(1L);
            String newDescription = "Updated description";
            when(topicRepository.findByName(TEST_TOPIC_NAME)).thenReturn(Optional.of(topic));
            when(topicRepository.save(any(Topic.class))).thenReturn(topic);

            // When
            topicApplicationService.updateTopicDescription(TEST_TOPIC_NAME, newDescription);

            // Then
            verify(topicRepository).findByName(TEST_TOPIC_NAME);
            verify(topicRepository).save(topic);
        }

        @Test
        @DisplayName("更新不存在的Topic描述应该抛出异常")
        void updateNonExistentTopicDescriptionShouldThrowException() {
            // Given
            String newDescription = "Updated description";
            when(topicRepository.findByName(TEST_TOPIC_NAME)).thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> topicApplicationService.updateTopicDescription(TEST_TOPIC_NAME, newDescription));
            assertEquals("Topic不存在: " + TEST_TOPIC_NAME, exception.getMessage());
            verify(topicRepository).findByName(TEST_TOPIC_NAME);
        }
    }

    @Nested
    @DisplayName("删除Topic测试")
    class DeleteTopicTests {

        @Test
        @DisplayName("应该成功删除Topic")
        void shouldDeleteTopicSuccessfully() {
            // Given
            Topic topic = createValidTopic();
            topic.setTopicId(1L);
            when(topicRepository.findByName(TEST_TOPIC_NAME)).thenReturn(Optional.of(topic));
            doNothing().when(kafkaTopicManager).deleteTopic(TEST_TOPIC_NAME);
            doNothing().when(topicRepository).delete(topic);

            // When
            topicApplicationService.deleteTopic(TEST_TOPIC_NAME);

            // Then
            verify(topicRepository).findByName(TEST_TOPIC_NAME);
            verify(kafkaTopicManager).deleteTopic(TEST_TOPIC_NAME);
            verify(topicRepository).delete(topic);
        }

        @Test
        @DisplayName("删除不存在的Topic应该抛出异常")
        void deleteNonExistentTopicShouldThrowException() {
            // Given
            when(topicRepository.findByName(TEST_TOPIC_NAME)).thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> topicApplicationService.deleteTopic(TEST_TOPIC_NAME));
            assertEquals("Topic不存在: " + TEST_TOPIC_NAME, exception.getMessage());
            verify(topicRepository).findByName(TEST_TOPIC_NAME);
            verify(kafkaTopicManager, never()).deleteTopic(anyString());
            verify(topicRepository, never()).delete(any(Topic.class));
        }
    }

    private Topic createValidTopic() {
        Topic topic = new Topic();
        topic.setName(TEST_TOPIC_NAME);
        topic.setOwner(TEST_OWNER);
        topic.setDescription(TEST_DESCRIPTION);
        topic.setTenantId(TEST_TENANT_ID);
        topic.setStatus(Topic.TopicStatus.ACTIVE);
        return topic;
    }
}
