package com.aixone.event.dto;

import java.io.Serializable;

/**
 * 事件订阅数据结构
 */
public class SubscriptionDTO implements Serializable {
    private String subscriptionId;
    private String eventType;
    private String callbackUrl;
    private String subscriber;
    private String tenantId;

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
} 