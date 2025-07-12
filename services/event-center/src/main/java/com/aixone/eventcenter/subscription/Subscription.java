package com.aixone.eventcenter.subscription;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 订阅实体
 * subscription_id, event_type, callback_url, subscriber
 */
@Entity
@Table(name = "subscriptions")
@Data
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subscriptionId; // 订阅ID

    private String eventType;    // 订阅的事件类型
    private String callbackUrl;  // 回调地址
    private String subscriber;   // 订阅方标识
    private String tenantId; // 租户ID
} 