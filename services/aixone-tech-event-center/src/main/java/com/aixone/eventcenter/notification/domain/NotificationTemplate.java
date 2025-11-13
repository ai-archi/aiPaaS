package com.aixone.eventcenter.notification.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

/**
 * 通知模板聚合根
 * 表示通知消息的模板配置
 */
@Entity
@Table(name = "notification_templates")
@EqualsAndHashCode(callSuper = true)
@Data
public class NotificationTemplate extends com.aixone.common.ddd.Entity<Long> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "template_id")
    private Long templateId;
    
    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;
    
    @Column(name = "template_name", nullable = false, length = 100)
    private String templateName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 50)
    private Notification.NotificationType notificationType;
    
    @Column(name = "subject_template", length = 500)
    private String subjectTemplate;
    
    @Column(name = "body_template", columnDefinition = "TEXT", nullable = false)
    private String bodyTemplate;
    
    @Column(name = "channels", length = 200)
    private String channels; // 逗号分隔的渠道列表，如 "EMAIL,SMS"
    
    @Column(name = "variables", columnDefinition = "JSONB")
    private String variables; // 模板变量定义（JSON格式）
    
    @Column(name = "version", nullable = false)
    private Integer version = 1;
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at")
    private Instant updatedAt;
    
    /**
     * 默认构造函数
     */
    public NotificationTemplate() {
        super(0L);
        this.version = 1;
        this.createdAt = Instant.now();
    }
    
    /**
     * 业务构造函数
     */
    public NotificationTemplate(String tenantId, String templateName, 
                               Notification.NotificationType notificationType,
                               String subjectTemplate, String bodyTemplate, 
                               String channels) {
        super(0L);
        this.tenantId = tenantId;
        this.templateName = templateName;
        this.notificationType = notificationType;
        this.subjectTemplate = subjectTemplate;
        this.bodyTemplate = bodyTemplate;
        this.channels = channels;
        this.version = 1;
        this.createdAt = Instant.now();
    }
    
    /**
     * 更新模板内容
     */
    public void updateContent(String subjectTemplate, String bodyTemplate) {
        this.subjectTemplate = subjectTemplate;
        this.bodyTemplate = bodyTemplate;
        this.version++;
        this.updatedAt = Instant.now();
    }
    
    /**
     * 更新渠道配置
     */
    public void updateChannels(String channels) {
        this.channels = channels;
        this.updatedAt = Instant.now();
    }
    
    /**
     * 更新变量定义
     */
    public void updateVariables(String variables) {
        this.variables = variables;
        this.updatedAt = Instant.now();
    }
    
    @Override
    public Long getId() {
        return templateId;
    }
}

