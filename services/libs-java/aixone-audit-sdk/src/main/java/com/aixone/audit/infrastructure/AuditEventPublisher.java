package com.aixone.audit.infrastructure;

import com.aixone.audit.domain.AuditEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 审计事件发布器
 * 负责发布审计相关的事件
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Component
public class AuditEventPublisher {
    
    private final ApplicationEventPublisher eventPublisher;
    
    public AuditEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * 发布审计事件
     * 
     * @param auditEvent 审计事件
     */
    public void publish(AuditEvent auditEvent) {
        eventPublisher.publishEvent(auditEvent);
    }
}
