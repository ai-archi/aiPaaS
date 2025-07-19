package com.aixone.event.dto;

import java.io.Serializable;
import java.time.Instant;

/**
 * 审计日志数据结构
 */
public class AuditLogDTO implements Serializable {
    private String logId;
    private String eventId;
    private String userId;
    private String eventType;
    private String data;
    private Instant timestamp;
    private String tenantId;

    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
} 