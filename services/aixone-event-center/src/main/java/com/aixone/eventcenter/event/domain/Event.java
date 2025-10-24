package com.aixone.eventcenter.event.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.Map;

/**
 * 事件聚合根
 * 表示系统中发生的重要业务事件
 */
@jakarta.persistence.Entity
@Table(name = "events")
@EqualsAndHashCode(callSuper = true)
public class Event extends com.aixone.common.ddd.Entity<Long> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;
    
    @Column(name = "event_type", nullable = false, length = 100, insertable = true, updatable = true)
    private String eventType;
    
    @Column(name = "source", nullable = false, length = 100)
    private String source;
    
    @Column(name = "data", columnDefinition = "TEXT")
    private String data;
    
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;
    
    @Column(name = "tenant_id", length = 50)
    private String tenantId;
    
    @Column(name = "correlation_id", length = 100)
    private String correlationId;
    
    @Column(name = "version", nullable = false)
    private Integer version = 1;
    
    // 默认构造函数
    public Event() {
        super(0L); // 临时ID，实际由JPA生成
    }
    
    // 业务构造函数
    public Event(String eventType, String source, String data, String tenantId) {
        super(0L); // 临时ID
        this.eventType = eventType;
        this.source = source;
        this.data = data;
        this.tenantId = tenantId;
        this.timestamp = Instant.now();
        this.version = 1;
    }
    
    /**
     * 更新事件数据
     */
    public void updateData(String newData) {
        this.data = newData;
        this.version++;
    }
    
    @Override
    public Long getId() {
        return eventId;
    }
    
    // Getters and Setters
    public Long getEventId() {
        return eventId;
    }
    
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getData() {
        return data;
    }
    
    public void setData(String data) {
        this.data = data;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getTenantId() {
        return tenantId;
    }
    
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    
    public String getCorrelationId() {
        return correlationId;
    }
    
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
    
    public Integer getVersion() {
        return version;
    }
    
    public void setVersion(Integer version) {
        this.version = version;
    }
}
