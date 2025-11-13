package com.aixone.eventcenter.event.domain;

import com.aixone.common.ddd.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * 事件分发记录仓储接口
 */
public interface EventDeliveryRecordRepository extends Repository<EventDeliveryRecord, Long> {
    
    /**
     * 根据事件ID和订阅ID查找记录
     */
    Optional<EventDeliveryRecord> findByEventIdAndSubscriptionId(Long eventId, Long subscriptionId);
    
    /**
     * 查找需要重试的记录
     */
    List<EventDeliveryRecord> findRecordsForRetry(Instant now);
    
    /**
     * 根据租户ID查找记录
     */
    List<EventDeliveryRecord> findByTenantId(String tenantId);
    
    /**
     * 根据事件ID查找所有记录
     */
    List<EventDeliveryRecord> findByEventId(Long eventId);
    
    /**
     * 根据订阅ID查找所有记录
     */
    List<EventDeliveryRecord> findBySubscriptionId(Long subscriptionId);
}

