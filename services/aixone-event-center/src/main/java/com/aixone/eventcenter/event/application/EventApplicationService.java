package com.aixone.eventcenter.event.application;

import com.aixone.eventcenter.event.domain.Event;
import com.aixone.eventcenter.event.domain.EventRepository;
import com.aixone.eventcenter.event.domain.EventType;
import com.aixone.eventcenter.event.infrastructure.KafkaEventPublisher;
import com.aixone.common.ddd.DomainEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * 事件应用服务
 * 协调领域对象完成业务用例
 */
@Service
@Transactional
public class EventApplicationService {
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private KafkaEventPublisher kafkaEventPublisher;
    
    /**
     * 发布事件（仅持久化）
     */
    public Event publishEvent(String eventType, String source, String data, String tenantId) {
        Event event = new Event(eventType, source, data, tenantId);
        event.setTimestamp(Instant.now());
        
        Event savedEvent = eventRepository.save(event);
        
        // 发布领域事件
        publishDomainEvent(new EventPublishedEvent(savedEvent));
        
        return savedEvent;
    }
    
    /**
     * 发布事件到Kafka
     */
    public Event publishEventToKafka(String topicName, String eventType, String source, String data, String tenantId) {
        Event event = new Event(eventType, source, data, tenantId);
        event.setTimestamp(Instant.now());
        
        // 先持久化
        Event savedEvent = eventRepository.save(event);
        
        // 发布到Kafka
        kafkaEventPublisher.publishEvent(topicName, savedEvent);
        
        // 发布领域事件
        publishDomainEvent(new EventPublishedToKafkaEvent(savedEvent, topicName));
        
        return savedEvent;
    }
    
    /**
     * 查询租户的所有事件
     */
    @Transactional(readOnly = true)
    public List<Event> getEventsByTenant(String tenantId) {
        return eventRepository.findByTenantId(tenantId);
    }
    
    /**
     * 根据ID查询事件
     */
    @Transactional(readOnly = true)
    public Optional<Event> getEventById(Long eventId, String tenantId) {
        return eventRepository.findById(eventId)
                .filter(event -> event.getTenantId().equals(tenantId));
    }
    
    /**
     * 根据事件类型查询事件
     */
    @Transactional(readOnly = true)
    public List<Event> getEventsByType(String eventType, String tenantId) {
        return eventRepository.findByTenantIdAndEventType(tenantId, eventType);
    }
    
    /**
     * 根据时间范围查询事件
     */
    @Transactional(readOnly = true)
    public List<Event> getEventsByTimeRange(String tenantId, Instant startTime, Instant endTime) {
        return eventRepository.findByTenantIdAndTimestampBetween(tenantId, startTime, endTime);
    }
    
    /**
     * 根据关联ID查询事件
     */
    @Transactional(readOnly = true)
    public List<Event> getEventsByCorrelationId(String correlationId, String tenantId) {
        return eventRepository.findByTenantIdAndCorrelationId(tenantId, correlationId);
    }
    
    /**
     * 统计租户事件数量
     */
    @Transactional(readOnly = true)
    public long getEventCountByTenant(String tenantId) {
        return eventRepository.countByTenantId(tenantId);
    }
    
    /**
     * 发布领域事件
     */
    private void publishDomainEvent(DomainEvent domainEvent) {
        // 这里可以集成事件总线或直接处理
        // 暂时简单实现
        System.out.println("Domain event published: " + domainEvent.getClass().getSimpleName());
    }
    
    /**
     * 事件发布领域事件
     */
    public static class EventPublishedEvent extends DomainEvent {
        private final Event event;
        
        public EventPublishedEvent(Event event) {
            super(event.getTenantId());
            this.event = event;
        }
        
        public Event getEvent() {
            return event;
        }
    }
    
    /**
     * 事件发布到Kafka领域事件
     */
    public static class EventPublishedToKafkaEvent extends DomainEvent {
        private final Event event;
        private final String topicName;
        
        public EventPublishedToKafkaEvent(Event event, String topicName) {
            super(event.getTenantId());
            this.event = event;
            this.topicName = topicName;
        }
        
        public Event getEvent() {
            return event;
        }
        
        public String getTopicName() {
            return topicName;
        }
    }
}
