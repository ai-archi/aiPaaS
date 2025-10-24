package com.aixone.eventcenter.event.infrastructure;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;

/**
 * Kafka Topic管理器
 * 负责Kafka Topic的创建和删除
 */
@Service
public class KafkaTopicManager {
    private static final Logger logger = LoggerFactory.getLogger(KafkaTopicManager.class);
    
    @Autowired
    private KafkaAdmin kafkaAdmin;

    /**
     * 创建Kafka Topic
     */
    public void createTopic(String topicName, int partitionCount, short replicationFactor) {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            // 检查Topic是否已存在
            Set<String> existingTopics = adminClient.listTopics().names().get();
            if (existingTopics.contains(topicName)) {
                logger.warn("Kafka Topic已存在: {}", topicName);
                return;
            }
            
            // 创建新Topic
            NewTopic newTopic = new NewTopic(topicName, partitionCount, replicationFactor);
            adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
            logger.info("Kafka Topic创建成功: {} (partitions: {}, replication: {})", 
                    topicName, partitionCount, replicationFactor);
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Kafka Topic创建失败: {}", e.getMessage(), e);
            throw new RuntimeException("Kafka Topic创建失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除Kafka Topic
     */
    public void deleteTopic(String topicName) {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            // 检查Topic是否存在
            Set<String> existingTopics = adminClient.listTopics().names().get();
            if (!existingTopics.contains(topicName)) {
                logger.warn("Kafka Topic不存在: {}", topicName);
                return;
            }
            
            // 删除Topic
            adminClient.deleteTopics(Collections.singletonList(topicName)).all().get();
            logger.info("Kafka Topic删除成功: {}", topicName);
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Kafka Topic删除失败: {}", e.getMessage(), e);
            throw new RuntimeException("Kafka Topic删除失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 检查Topic是否存在
     */
    public boolean topicExists(String topicName) {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            Set<String> existingTopics = adminClient.listTopics().names().get();
            return existingTopics.contains(topicName);
        } catch (ExecutionException | InterruptedException e) {
            logger.error("检查Topic存在性失败: {}", e.getMessage(), e);
            return false;
        }
    }
}
