package com.aixone.eventcenter.event.domain;

import com.aixone.common.ddd.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * 事件仓储接口
 * 定义事件聚合的持久化操作
 */
public interface EventRepository extends Repository<Event, Long> {
    
    /**
     * 根据租户ID查找事件
     */
    List<Event> findByTenantId(String tenantId);
    
    /**
     * 根据事件ID和租户ID查找事件
     */
    Optional<Event> findByEventIdAndTenantId(Long eventId, String tenantId);
    
    /**
     * 根据事件类型查找事件
     */
    List<Event> findByEventType(String eventType);
    
    /**
     * 根据租户ID和事件类型查找事件
     */
    List<Event> findByTenantIdAndEventType(String tenantId, String eventType);
    
    /**
     * 根据时间范围查找事件
     */
    List<Event> findByTimestampBetween(Instant startTime, Instant endTime);
    
    /**
     * 根据租户ID和时间范围查找事件
     */
    List<Event> findByTenantIdAndTimestampBetween(String tenantId, Instant startTime, Instant endTime);
    
    /**
     * 根据关联ID查找事件
     */
    List<Event> findByCorrelationId(String correlationId);
    
    /**
     * 根据租户ID和关联ID查找事件
     */
    List<Event> findByTenantIdAndCorrelationId(String tenantId, String correlationId);
    
    /**
     * 统计租户的事件数量
     */
    long countByTenantId(String tenantId);
    
    /**
     * 统计事件类型的数量
     */
    long countByEventType(String eventType);
}
