package com.aixone.event.dto;

import java.io.Serializable;
import java.time.Instant;

/**
 * 事件数据结构
 */
public class EventDTO implements Serializable {
    private String eventId;
    private String eventType;
    private String source;
    private String data;
    private Instant timestamp;
    private String tenantId;

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
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
} 