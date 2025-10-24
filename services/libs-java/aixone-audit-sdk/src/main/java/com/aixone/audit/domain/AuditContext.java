package com.aixone.audit.domain;

import com.aixone.session.SessionContext;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 审计上下文
 * 用于在请求处理过程中收集审计信息
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Getter
@Setter
public class AuditContext {
    
    /** 操作用户ID */
    private String userId;
    
    /** 操作类型 */
    private String action;
    
    /** 操作资源 */
    private String resource;
    
    /** 客户端IP */
    private String clientIp;
    
    /** 用户代理 */
    private String userAgent;
    
    /** 会话ID */
    private String sessionId;
    
    /** 操作开始时间 */
    private LocalDateTime startTime;
    
    /** 操作详情 */
    private Map<String, Object> details = new HashMap<>();
    
    /** 租户ID */
    private String tenantId;
    
    /**
     * 构造函数
     */
    public AuditContext() {
        this.startTime = LocalDateTime.now();
        this.tenantId = getCurrentTenantId();
        this.userId = getCurrentUserId();
    }
    
    /**
     * 构造函数（指定操作类型和资源）
     * 
     * @param action 操作类型
     * @param resource 操作资源
     */
    public AuditContext(String action, String resource) {
        this();
        this.action = action;
        this.resource = resource;
    }
    
    /**
     * 添加操作详情
     * 
     * @param key 键
     * @param value 值
     */
    public void addDetail(String key, Object value) {
        this.details.put(key, value);
    }
    
    /**
     * 添加多个操作详情
     * 
     * @param details 详情Map
     */
    public void addDetails(Map<String, Object> details) {
        this.details.putAll(details);
    }
    
    /**
     * 获取操作持续时间（毫秒）
     * 
     * @return 持续时间
     */
    public long getDuration() {
        if (startTime == null) {
            return 0;
        }
        return java.time.Duration.between(startTime, LocalDateTime.now()).toMillis();
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
            return null;
        }
    }
    
    /**
     * 获取当前用户ID
     * 
     * @return 用户ID
     */
    private String getCurrentUserId() {
        try {
            return SessionContext.getUserId();
        } catch (Exception e) {
            return null;
        }
    }
}
