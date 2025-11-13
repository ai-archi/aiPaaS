package com.aixone.eventcenter.event.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

/**
 * 订阅聚合根
 * 表示对特定事件类型的订阅配置
 */
@Entity
@Table(name = "subscriptions")
@EqualsAndHashCode(callSuper = true)
@Data
public class Subscription extends com.aixone.common.ddd.Entity<Long> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private Long subscriptionId;
    
    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;
    
    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;
    
    @Column(name = "subscriber_service", nullable = false, length = 200)
    private String subscriberService;
    
    @Column(name = "subscriber_endpoint", nullable = false, length = 500)
    private String subscriberEndpoint;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;
    
    @Column(name = "filter_config", columnDefinition = "JSONB")
    private String filterConfig;
    
    @Column(name = "retry_config", columnDefinition = "JSONB")
    private String retryConfig;
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at")
    private Instant updatedAt;
    
    /**
     * 默认构造函数
     */
    public Subscription() {
        super(0L);
        this.status = SubscriptionStatus.ACTIVE;
        this.createdAt = Instant.now();
    }
    
    /**
     * 业务构造函数
     */
    public Subscription(String tenantId, String eventType, String subscriberService, String subscriberEndpoint) {
        super(0L);
        this.tenantId = tenantId;
        this.eventType = eventType;
        this.subscriberService = subscriberService;
        this.subscriberEndpoint = subscriberEndpoint;
        this.status = SubscriptionStatus.ACTIVE;
        this.createdAt = Instant.now();
    }
    
    /**
     * 激活订阅
     */
    public void activate() {
        this.status = SubscriptionStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }
    
    /**
     * 停用订阅
     */
    public void deactivate() {
        this.status = SubscriptionStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }
    
    /**
     * 取消订阅
     */
    public void cancel() {
        this.status = SubscriptionStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }
    
    /**
     * 更新订阅配置
     */
    public void updateConfig(String filterConfig, String retryConfig) {
        this.filterConfig = filterConfig;
        this.retryConfig = retryConfig;
        this.updatedAt = Instant.now();
    }
    
    /**
     * 更新订阅端点
     */
    public void updateEndpoint(String endpoint) {
        this.subscriberEndpoint = endpoint;
        this.updatedAt = Instant.now();
    }
    
    @Override
    public Long getId() {
        return subscriptionId;
    }
    
    /**
     * 订阅状态枚举
     */
    public enum SubscriptionStatus {
        ACTIVE,     // 激活
        INACTIVE,   // 停用
        CANCELLED   // 已取消
    }
}

