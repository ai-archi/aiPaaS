package com.aixone.audit.domain;

import com.aixone.common.ddd.DomainEvent;
import lombok.Getter;

import java.util.Map;

/**
 * 审计事件
 * 当审计日志被创建时触发的事件
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Getter
public class AuditEvent extends DomainEvent {
    
    /** 审计日志ID */
    private final Long auditLogId;
    
    /** 操作用户ID */
    private final String userId;
    
    /** 操作类型 */
    private final String action;
    
    /** 操作结果 */
    private final String result;
    
    /**
     * 构造函数
     * 
     * @param auditLogId 审计日志ID
     * @param userId 用户ID
     * @param action 操作类型
     * @param result 操作结果
     */
    public AuditEvent(Long auditLogId, String userId, String action, String result) {
        super();
        this.auditLogId = auditLogId;
        this.userId = userId;
        this.action = action;
        this.result = result;
    }
    
    /**
     * 构造函数（指定租户ID）
     * 
     * @param auditLogId 审计日志ID
     * @param userId 用户ID
     * @param action 操作类型
     * @param result 操作结果
     * @param tenantId 租户ID
     */
    public AuditEvent(Long auditLogId, String userId, String action, String result, String tenantId) {
        super(tenantId);
        this.auditLogId = auditLogId;
        this.userId = userId;
        this.action = action;
        this.result = result;
    }
}
