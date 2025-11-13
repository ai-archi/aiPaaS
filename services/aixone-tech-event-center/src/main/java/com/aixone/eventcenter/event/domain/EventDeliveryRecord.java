package com.aixone.eventcenter.event.domain;

import com.aixone.common.ddd.Entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

/**
 * 事件分发记录聚合根
 * 记录事件分发到订阅者的历史，用于重试和追踪
 */
@jakarta.persistence.Entity
@Table(name = "event_delivery_records")
@Data
@EqualsAndHashCode(callSuper = true)
public class EventDeliveryRecord extends Entity<Long> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long recordId;
    
    @Column(name = "event_id", nullable = false)
    private Long eventId;
    
    @Column(name = "subscription_id", nullable = false)
    private Long subscriptionId;
    
    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private DeliveryStatus status = DeliveryStatus.PENDING;
    
    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;
    
    @Column(name = "max_retries", nullable = false)
    private Integer maxRetries = 3;
    
    @Column(name = "next_retry_at")
    private Instant nextRetryAt;
    
    @Column(name = "delivered_at")
    private Instant deliveredAt;
    
    @Column(name = "error_message", length = 1000)
    private String errorMessage;
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at")
    private Instant updatedAt;
    
    public EventDeliveryRecord() {
        super(0L);
        this.status = DeliveryStatus.PENDING;
        this.retryCount = 0;
        this.maxRetries = 3;
        this.createdAt = Instant.now();
    }
    
    public EventDeliveryRecord(Long eventId, Long subscriptionId, String tenantId, Integer maxRetries) {
        super(0L);
        this.eventId = eventId;
        this.subscriptionId = subscriptionId;
        this.tenantId = tenantId;
        this.maxRetries = maxRetries != null ? maxRetries : 3;
        this.status = DeliveryStatus.PENDING;
        this.retryCount = 0;
        this.createdAt = Instant.now();
    }
    
    /**
     * 标记为成功
     */
    public void markAsDelivered() {
        this.status = DeliveryStatus.DELIVERED;
        this.deliveredAt = Instant.now();
        this.updatedAt = Instant.now();
    }
    
    /**
     * 标记为失败
     */
    public void markAsFailed(String errorMessage) {
        this.errorMessage = errorMessage;
        this.updatedAt = Instant.now();
        
        if (canRetry()) {
            this.status = DeliveryStatus.RETRYING;
            scheduleNextRetry();
        } else {
            this.status = DeliveryStatus.FAILED;
        }
    }
    
    /**
     * 是否可以重试
     */
    public boolean canRetry() {
        return retryCount < maxRetries;
    }
    
    /**
     * 安排下次重试（指数退避）
     */
    public void scheduleNextRetry() {
        this.retryCount++;
        long delaySeconds = calculateRetryDelay(retryCount);
        this.nextRetryAt = Instant.now().plusSeconds(delaySeconds);
        this.updatedAt = Instant.now();
    }
    
    /**
     * 计算重试延迟（指数退避）
     */
    private long calculateRetryDelay(int retryCount) {
        return Math.min(300, (long) Math.pow(2, retryCount) * 30); // 最大5分钟
    }
    
    /**
     * 是否应该重试（时间到了）
     */
    public boolean shouldRetry() {
        return status == DeliveryStatus.RETRYING 
            && nextRetryAt != null 
            && Instant.now().isAfter(nextRetryAt);
    }
    
    @Override
    public Long getId() {
        return recordId;
    }
    
    public enum DeliveryStatus {
        PENDING,    // 待分发
        DELIVERED,  // 已成功分发
        RETRYING,   // 重试中
        FAILED      // 失败（重试次数用完）
    }
}

