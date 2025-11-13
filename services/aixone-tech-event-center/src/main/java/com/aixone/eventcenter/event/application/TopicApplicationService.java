package com.aixone.eventcenter.event.application;

import com.aixone.eventcenter.event.domain.Topic;
import com.aixone.eventcenter.event.domain.TopicRepository;
import com.aixone.eventcenter.event.infrastructure.KafkaTopicManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Topic应用服务
 * 协调Topic聚合完成业务用例
 */
@Service
@Transactional
public class TopicApplicationService {
    
    @Autowired
    private TopicRepository topicRepository;
    
    @Autowired
    private KafkaTopicManager kafkaTopicManager;
    
    /**
     * 注册Topic
     */
    public Topic registerTopic(String name, String owner, String description, String tenantId) {
        // 验证Topic名称
        validateTopicName(name);
        
        // 检查Topic是否已存在
        if (topicRepository.existsByName(name)) {
            throw new IllegalArgumentException("Topic已存在: " + name);
        }
        
        // 创建Topic聚合
        Topic topic = new Topic(name, owner, description, tenantId);
        
        // 在Kafka中创建Topic
        kafkaTopicManager.createTopic(name, topic.getPartitionCount(), topic.getReplicationFactor());
        
        // 持久化Topic
        Topic savedTopic = topicRepository.save(topic);
        
        return savedTopic;
    }
    
    /**
     * 查询所有Topic
     */
    @Transactional(readOnly = true)
    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }
    
    /**
     * 根据租户查询Topic
     */
    @Transactional(readOnly = true)
    public List<Topic> getTopicsByTenant(String tenantId) {
        return topicRepository.findByTenantId(tenantId);
    }
    
    /**
     * 根据名称查询Topic
     */
    @Transactional(readOnly = true)
    public Optional<Topic> getTopicByName(String name) {
        return topicRepository.findByName(name);
    }
    
    /**
     * 根据状态查询Topic
     */
    @Transactional(readOnly = true)
    public List<Topic> getTopicsByStatus(Topic.TopicStatus status) {
        return topicRepository.findByStatus(status);
    }
    
    /**
     * 激活Topic
     */
    public void activateTopic(String name) {
        Topic topic = topicRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Topic不存在: " + name));
        
        topic.activate();
        topicRepository.save(topic);
    }
    
    /**
     * 停用Topic
     */
    public void deactivateTopic(String name) {
        Topic topic = topicRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Topic不存在: " + name));
        
        topic.deactivate();
        topicRepository.save(topic);
    }
    
    /**
     * 更新Topic描述
     */
    public void updateTopicDescription(String name, String description) {
        Topic topic = topicRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Topic不存在: " + name));
        
        topic.updateDescription(description);
        topicRepository.save(topic);
    }
    
    /**
     * 删除Topic
     */
    public void deleteTopic(String name) {
        Topic topic = topicRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Topic不存在: " + name));
        
        // 从Kafka中删除Topic
        kafkaTopicManager.deleteTopic(name);
        
        // 删除持久化数据
        topicRepository.delete(topic);
    }
    
    /**
     * 验证Topic名称
     */
    private void validateTopicName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Topic名称不能为空");
        }
        
        if (!name.matches("^[a-zA-Z0-9._-]+$")) {
            throw new IllegalArgumentException("Topic名称只能包含字母、数字、点、下划线和连字符");
        }
        
        if (name.length() > 249) {
            throw new IllegalArgumentException("Topic名称长度不能超过249个字符");
        }
    }
}
