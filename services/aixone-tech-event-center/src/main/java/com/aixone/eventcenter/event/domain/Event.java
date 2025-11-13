package com.aixone.eventcenter.event.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

/**
 * 事件聚合根
 * 表示系统中发生的重要业务事件
 */
@Entity
@Table(name = "events")
@EqualsAndHashCode(callSuper = true)
@Data
public class Event extends com.aixone.common.ddd.Entity<Long> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;
    
    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;
    
    @Column(name = "event_source", nullable = false, length = 100)
    private String eventSource;
    
    @Column(name = "event_data", columnDefinition = "JSONB")
    private String eventData;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EventStatus status = EventStatus.PENDING;
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "tenant_id", length = 50)
    private String tenantId;
    
    @Column(name = "correlation_id", length = 100)
    private String correlationId;
    
    @Column(name = "version", nullable = false)
    private Integer version = 1;
    
    /**
     * 默认构造函数
     */
    public Event() {
        super(0L); // 临时ID，实际由JPA生成
        this.status = EventStatus.PENDING;
        this.createdAt = Instant.now();
        this.version = 1;
    }
    
    /**
     * 业务构造函数
     */
    public Event(String eventType, String eventSource, String eventData, String tenantId) {
        super(0L); // 临时ID
        this.eventType = eventType;
        this.eventSource = eventSource;
        this.eventData = eventData;
        this.tenantId = tenantId;
        this.status = EventStatus.PENDING;
        this.createdAt = Instant.now();
        this.version = 1;
    }
    
    /**
     * 更新事件数据
     */
    public void updateData(String newData) {
        this.eventData = newData;
        this.version++;
    }
    
    /**
     * 更新事件状态
     */
    public void updateStatus(EventStatus newStatus) {
        this.status = newStatus;
    }
    
    /**
     * 标记为已发布
     */
    public void markAsPublished() {
        this.status = EventStatus.PUBLISHED;
    }
    
    /**
     * 标记为失败
     */
    public void markAsFailed() {
        this.status = EventStatus.FAILED;
    }
    
    /**
     * 取消事件
     */
    public void cancel() {
        this.status = EventStatus.CANCELLED;
    }
    
    @Override
    public Long getId() {
        return eventId;
    }
    
    /**
     * 事件状态枚举
     */
    public enum EventStatus {
        PENDING,    // 待处理
        PUBLISHED,  // 已发布
        FAILED,     // 失败
        CANCELLED   // 已取消
    }
}
