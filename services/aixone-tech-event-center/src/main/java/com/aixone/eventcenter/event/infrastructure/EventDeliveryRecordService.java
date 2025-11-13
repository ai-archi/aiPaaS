package com.aixone.eventcenter.event.infrastructure;

import com.aixone.eventcenter.event.domain.EventDeliveryRecord;
import com.aixone.eventcenter.event.domain.EventDeliveryRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 事件分发记录服务
 * 管理事件分发记录的创建和更新
 */
@Service
@Transactional
public class EventDeliveryRecordService {
    
    @Autowired
    private EventDeliveryRecordRepository deliveryRecordRepository;
    
    /**
     * 创建分发记录
     */
    public EventDeliveryRecord createRecord(Long eventId, Long subscriptionId, String tenantId, Integer maxRetries) {
        EventDeliveryRecord record = new EventDeliveryRecord(eventId, subscriptionId, tenantId, maxRetries);
        return deliveryRecordRepository.save(record);
    }
    
    /**
     * 保存分发记录
     */
    public EventDeliveryRecord save(EventDeliveryRecord record) {
        record.setUpdatedAt(java.time.Instant.now());
        return deliveryRecordRepository.save(record);
    }
    
    /**
     * 查找或创建分发记录
     */
    public EventDeliveryRecord findOrCreateRecord(Long eventId, Long subscriptionId, String tenantId, Integer maxRetries) {
        return deliveryRecordRepository.findByEventIdAndSubscriptionId(eventId, subscriptionId)
                .orElseGet(() -> createRecord(eventId, subscriptionId, tenantId, maxRetries));
    }
}

