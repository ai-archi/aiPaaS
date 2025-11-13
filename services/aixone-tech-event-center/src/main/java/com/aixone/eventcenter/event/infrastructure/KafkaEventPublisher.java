package com.aixone.eventcenter.event.infrastructure;

import com.aixone.eventcenter.event.domain.Event;
import com.aixone.eventcenter.event.domain.Topic;
import com.aixone.eventcenter.event.domain.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Kafka事件发布器
 * 负责将事件发布到Kafka Topic
 */
@Service
public class KafkaEventPublisher {
    private static final Logger logger = LoggerFactory.getLogger(KafkaEventPublisher.class);
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @Autowired
    private TopicRepository topicRepository;

    /**
     * 发布事件到指定Topic
     */
    public void publishEvent(String topicName, Event event) {
        try {
            // 验证Topic是否已注册
            Optional<Topic> topicOpt = topicRepository.findByName(topicName);
            if (!topicOpt.isPresent()) {
                throw new IllegalArgumentException("Topic未注册: " + topicName);
            }
            
            Topic topic = topicOpt.get();
            if (topic.getStatus() != Topic.TopicStatus.ACTIVE) {
                throw new IllegalStateException("Topic未激活: " + topicName);
            }
            
            // 发布事件到Kafka
            kafkaTemplate.send(topicName, event.getEventId().toString(), event);
            logger.info("事件发布成功 - Topic: {}, EventId: {}", topicName, event.getEventId());
            
        } catch (Exception e) {
            logger.error("事件发布失败 - Topic: {}, EventId: {}, Error: {}", 
                    topicName, event.getEventId(), e.getMessage(), e);
            throw new RuntimeException("事件发布失败: " + e.getMessage(), e);
        }
    }

    /**
     * 发布事件到指定Topic（使用自定义Key）
     */
    public void publishEvent(String topicName, String key, Event event) {
        try {
            // 验证Topic是否已注册
            Optional<Topic> topicOpt = topicRepository.findByName(topicName);
            if (!topicOpt.isPresent()) {
                throw new IllegalArgumentException("Topic未注册: " + topicName);
            }
            
            Topic topic = topicOpt.get();
            if (topic.getStatus() != Topic.TopicStatus.ACTIVE) {
                throw new IllegalStateException("Topic未激活: " + topicName);
            }
            
            // 发布事件到Kafka
            kafkaTemplate.send(topicName, key, event);
            logger.info("事件发布成功 - Topic: {}, Key: {}, EventId: {}", 
                    topicName, key, event.getEventId());
            
        } catch (Exception e) {
            logger.error("事件发布失败 - Topic: {}, Key: {}, EventId: {}, Error: {}", 
                    topicName, key, event.getEventId(), e.getMessage(), e);
            throw new RuntimeException("事件发布失败: " + e.getMessage(), e);
        }
    }
}
