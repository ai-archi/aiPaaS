package com.aixone.eventcenter.event;

import com.aixone.common.exception.BizException;
import com.aixone.common.util.ValidationUtils;
import com.aixone.eventcenter.event.domain.Event;
import com.aixone.eventcenter.event.infrastructure.KafkaEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aixone.common.session.SessionContext;
import java.util.Map;

/**
 * 事件服务，负责事件发布与查询
 */
@Service
public class EventService {
    private static final Logger logger = LoggerFactory.getLogger(EventService.class);
    
    @Autowired
    private EventRepository eventRepository;
    
    
    @Autowired
    private KafkaEventPublisher kafkaEventPublisher;
    

    /**
     * 发布新事件（仅持久化到数据库）
     */
    public Event publishEvent(Event event) {
        ValidationUtils.notNull(event, "事件不能为空");
        ValidationUtils.notBlank(event.getEventType(), "事件类型不能为空");
        
        try {
            event.setTimestamp(java.time.Instant.now());
            // tenantId is managed by Entity base class
            Event saved = eventRepository.save(event);
            
            // 审计日志由公共模块处理
            
            // 监控指标由独立的监控服务处理
            return saved;
        } catch (Exception ex) {
            // 监控指标由独立的监控服务处理
            logger.error("[ALERT] 事件发布失败: {}", ex.getMessage(), ex);
            
            // 审计日志由公共模块处理
            
            throw new BizException("EVENT_PUBLISH_FAILED", "事件发布失败: " + ex.getMessage(), ex);
        }
    }

    /**
     * 发布事件到Kafka Topic
     */
    public Event publishEventToKafka(String topicName, Event event) {
        ValidationUtils.notBlank(topicName, "Topic名称不能为空");
        ValidationUtils.notNull(event, "事件不能为空");
        ValidationUtils.notBlank(event.getEventType(), "事件类型不能为空");
        
        try {
            // 1. 先持久化到数据库
            event.setTimestamp(java.time.Instant.now());
            // tenantId is managed by Entity base class
            Event saved = eventRepository.save(event);
            
            // 2. 发布到Kafka
            kafkaEventPublisher.publishEvent(topicName, saved);
            
            // 审计日志由公共模块处理
            
            // 监控指标由独立的监控服务处理
            logger.info("事件发布到Kafka成功 - Topic: {}, EventId: {}", topicName, saved.getEventId());
            return saved;
            
        } catch (Exception ex) {
            // 监控指标由独立的监控服务处理
            logger.error("[ALERT] 事件发布到Kafka失败 - Topic: {}, Error: {}", topicName, ex.getMessage(), ex);
            
            // 审计日志由公共模块处理
            
            throw new BizException("EVENT_PUBLISH_KAFKA_FAILED", 
                "事件发布到Kafka失败: " + ex.getMessage(), ex);
        }
    }

    /**
     * 查询所有事件（按租户）
     */
    public List<Event> getAllEvents() {
        return eventRepository.findByTenantId(SessionContext.getTenantId());
    }

    /**
     * 按ID查询事件（按租户）
     */
    public Optional<Event> getEventById(Long id) {
        return eventRepository.findByEventIdAndTenantId(id, SessionContext.getTenantId());
    }
} 