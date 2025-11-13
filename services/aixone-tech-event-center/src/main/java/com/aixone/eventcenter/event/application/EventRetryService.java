package com.aixone.eventcenter.event.application;

import com.aixone.eventcenter.event.domain.Event;
import com.aixone.eventcenter.event.domain.EventDeliveryRecord;
import com.aixone.eventcenter.event.domain.EventDeliveryRecordRepository;
import com.aixone.eventcenter.event.domain.EventRepository;
import com.aixone.eventcenter.event.domain.Subscription;
import com.aixone.eventcenter.event.domain.SubscriptionRepository;
import com.aixone.eventcenter.event.infrastructure.EventDistributor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;

/**
 * 事件重试服务
 * 定期检查失败的分发记录并重试
 */
@Service
public class EventRetryService {
    private static final Logger logger = LoggerFactory.getLogger(EventRetryService.class);
    
    @Autowired
    private EventDeliveryRecordRepository deliveryRecordRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    
    @Autowired
    private EventDistributor eventDistributor;
    
    /**
     * 定期重试失败的事件分发
     * 每30秒执行一次
     */
    @Scheduled(fixedDelay = 30000) // 30秒
    @Transactional
    public void retryFailedDeliveries() {
        try {
            List<EventDeliveryRecord> recordsToRetry = deliveryRecordRepository.findRecordsForRetry(Instant.now());
            
            if (recordsToRetry.isEmpty()) {
                return;
            }
            
            logger.info("开始重试 {} 个失败的事件分发", recordsToRetry.size());
            
            for (EventDeliveryRecord record : recordsToRetry) {
                retryDelivery(record);
            }
            
            logger.info("事件分发重试完成，处理了 {} 条记录", recordsToRetry.size());
            
        } catch (Exception e) {
            logger.error("事件分发重试服务执行失败", e);
        }
    }
    
    /**
     * 重试单个分发记录
     */
    private void retryDelivery(EventDeliveryRecord record) {
        try {
            // 查找事件
            Event event = eventRepository.findById(record.getEventId())
                    .orElse(null);
            
            if (event == null) {
                logger.warn("事件不存在，跳过重试 - EventId: {}, RecordId: {}", 
                        record.getEventId(), record.getRecordId());
                record.markAsFailed("事件不存在");
                deliveryRecordRepository.save(record);
                return;
            }
            
            // 查找订阅
            Subscription subscription = subscriptionRepository.findById(record.getSubscriptionId())
                    .orElse(null);
            
            if (subscription == null) {
                logger.warn("订阅不存在，跳过重试 - SubscriptionId: {}, RecordId: {}", 
                        record.getSubscriptionId(), record.getRecordId());
                record.markAsFailed("订阅不存在");
                deliveryRecordRepository.save(record);
                return;
            }
            
            // 检查订阅是否仍然活跃
            if (subscription.getStatus() != Subscription.SubscriptionStatus.ACTIVE) {
                logger.warn("订阅已停用，跳过重试 - SubscriptionId: {}, RecordId: {}", 
                        subscription.getSubscriptionId(), record.getRecordId());
                record.markAsFailed("订阅已停用");
                deliveryRecordRepository.save(record);
                return;
            }
            
            // 执行重试
            logger.info("重试事件分发 - EventId: {}, SubscriptionId: {}, RetryCount: {}", 
                    event.getEventId(), subscription.getSubscriptionId(), record.getRetryCount());
            
            boolean success = eventDistributor.distribute(event, subscription, record);
            
            if (!success && record.canRetry()) {
                // 如果失败且可以重试，记录已由distribute方法更新
                logger.info("事件分发重试失败，将再次重试 - EventId: {}, SubscriptionId: {}, NextRetryAt: {}", 
                        event.getEventId(), subscription.getSubscriptionId(), record.getNextRetryAt());
            }
            
        } catch (Exception e) {
            logger.error("重试事件分发异常 - RecordId: {}, Error: {}", 
                    record.getRecordId(), e.getMessage(), e);
            
            if (record.canRetry()) {
                record.markAsFailed(e.getMessage());
                deliveryRecordRepository.save(record);
            } else {
                record.markAsFailed("重试异常: " + e.getMessage());
                deliveryRecordRepository.save(record);
            }
        }
    }
}

