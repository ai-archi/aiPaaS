package com.aixone.eventcenter.notification.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

/**
 * 通知聚合根
 * 表示系统中发送的通知消息
 */
@Entity
@Table(name = "notifications")
@EqualsAndHashCode(callSuper = true)
@Data
public class Notification extends com.aixone.common.ddd.Entity<Long> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;
    
    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 50)
    private NotificationType notificationType;
    
    @Column(name = "recipient_info", columnDefinition = "JSONB", nullable = false)
    private String recipientInfo;
    
    @Column(name = "notification_content", columnDefinition = "JSONB", nullable = false)
    private String notificationContent;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private NotificationStatus status = NotificationStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    private NotificationPriority priority = NotificationPriority.NORMAL;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private NotificationChannel channel;
    
    @Column(name = "template_id")
    private Long templateId;
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "sent_at")
    private Instant sentAt;
    
    @Column(name = "error_message", length = 1000)
    private String errorMessage;
    
    /**
     * 默认构造函数
     */
    public Notification() {
        super(0L);
        this.status = NotificationStatus.PENDING;
        this.priority = NotificationPriority.NORMAL;
        this.createdAt = Instant.now();
    }
    
    /**
     * 业务构造函数
     */
    public Notification(String tenantId, NotificationType notificationType, 
                       String recipientInfo, String notificationContent, 
                       NotificationChannel channel) {
        super(0L);
        this.tenantId = tenantId;
        this.notificationType = notificationType;
        this.recipientInfo = recipientInfo;
        this.notificationContent = notificationContent;
        this.channel = channel;
        this.status = NotificationStatus.PENDING;
        this.priority = NotificationPriority.NORMAL;
        this.createdAt = Instant.now();
    }
    
    /**
     * 标记为已发送
     */
    public void markAsSent() {
        this.status = NotificationStatus.SENT;
        this.sentAt = Instant.now();
    }
    
    /**
     * 标记为失败
     */
    public void markAsFailed(String errorMessage) {
        this.status = NotificationStatus.FAILED;
        this.errorMessage = errorMessage;
    }
    
    /**
     * 取消通知
     */
    public void cancel() {
        this.status = NotificationStatus.CANCELLED;
    }
    
    /**
     * 设置优先级
     */
    public void setPriority(NotificationPriority priority) {
        this.priority = priority;
    }
    
    @Override
    public Long getId() {
        return notificationId;
    }
    
    /**
     * 通知类型枚举
     */
    public enum NotificationType {
        EMAIL,      // 邮件
        SMS,        // 短信
        PUSH,       // 推送
        IM,         // 即时通讯
        SYSTEM      // 系统通知
    }
    
    /**
     * 通知状态枚举
     */
    public enum NotificationStatus {
        PENDING,    // 待发送
        SENT,       // 已发送
        FAILED,     // 发送失败
        CANCELLED   // 已取消
    }
    
    /**
     * 通知优先级枚举
     */
    public enum NotificationPriority {
        LOW,        // 低
        NORMAL,     // 普通
        HIGH,       // 高
        URGENT      // 紧急
    }
    
    /**
     * 通知渠道枚举
     */
    public enum NotificationChannel {
        EMAIL,      // 邮件
        SMS,        // 短信
        PUSH,       // 推送
        WECHAT,     // 微信
        DINGTALK,   // 钉钉
        SYSTEM      // 系统
    }
}

