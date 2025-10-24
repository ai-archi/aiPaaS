package com.aixone.common.ddd;

import com.aixone.session.SessionContext;
import java.time.Instant;
import java.util.UUID;

/**
 * 领域事件基类
 * 表示在领域模型中发生的重要事件
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
public abstract class DomainEvent {
    
    private final String eventId;
    private final Instant occurredOn;
    private final String tenantId;
    
    protected DomainEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = Instant.now();
        this.tenantId = getCurrentTenantId();
    }
    
    protected DomainEvent(String tenantId) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = Instant.now();
        this.tenantId = tenantId;
    }
    
    public String getEventId() {
        return eventId;
    }
    
    public Instant getOccurredOn() {
        return occurredOn;
    }
    
    public String getTenantId() {
        return tenantId;
    }
    
    /**
     * 获取当前租户ID
     * 
     * @return 租户ID
     */
    private String getCurrentTenantId() {
        try {
            return SessionContext.getTenantId();
        } catch (Exception e) {
            return null; // 如果无法获取租户ID，返回null
        }
    }
}
