package com.aixone.event.dto;

import java.io.Serializable;
import java.time.Instant;

/**
 * 事件订阅数据传输对象
 * 提供事件订阅相关的核心数据
 */
public class SubscriptionDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String subscriptionId;
    private String eventType;
    private String callbackUrl;
    private String subscriber;
    private String tenantId;
    private String status;
    private Instant createTime;
    private Instant updateTime;

    // 默认构造函数
    public SubscriptionDTO() {}

    // 业务构造函数
    public SubscriptionDTO(String eventType, String callbackUrl, String subscriber, String tenantId) {
        this.eventType = eventType;
        this.callbackUrl = callbackUrl;
        this.subscriber = subscriber;
        this.tenantId = tenantId;
        this.status = "ACTIVE";
        this.createTime = Instant.now();
        this.updateTime = Instant.now();
    }

    // Getters and Setters
    public String getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(String subscriptionId) { this.subscriptionId = subscriptionId; }
    
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    
    public String getCallbackUrl() { return callbackUrl; }
    public void setCallbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }
    
    public String getSubscriber() { return subscriber; }
    public void setSubscriber(String subscriber) { this.subscriber = subscriber; }
    
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Instant getCreateTime() { return createTime; }
    public void setCreateTime(Instant createTime) { this.createTime = createTime; }
    
    public Instant getUpdateTime() { return updateTime; }
    public void setUpdateTime(Instant updateTime) { this.updateTime = updateTime; }

    @Override
    public String toString() {
        return "SubscriptionDTO{" +
                "subscriptionId='" + subscriptionId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", subscriber='" + subscriber + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
} 