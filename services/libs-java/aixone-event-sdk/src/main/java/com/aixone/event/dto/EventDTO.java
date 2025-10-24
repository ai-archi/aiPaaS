package com.aixone.event.dto;

import java.io.Serializable;
import java.time.Instant;

/**
 * 事件数据传输对象
 * 基于事件中心的Event实体设计，提供事件相关的核心数据
 */
public class EventDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long eventId;
    private String eventType;
    private String source;
    private String data;
    private Instant timestamp;
    private String tenantId;
    private String correlationId;
    private Integer version = 1;

    // 默认构造函数
    public EventDTO() {}

    // 业务构造函数
    public EventDTO(String eventType, String source, String data, String tenantId) {
        this.eventType = eventType;
        this.source = source;
        this.data = data;
        this.tenantId = tenantId;
        this.timestamp = Instant.now();
        this.version = 1;
    }

    // Getters and Setters
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
    
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    @Override
    public String toString() {
        return "EventDTO{" +
                "eventId=" + eventId +
                ", eventType='" + eventType + '\'' +
                ", source='" + source + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", timestamp=" + timestamp +
                ", version=" + version +
                '}';
    }
} 