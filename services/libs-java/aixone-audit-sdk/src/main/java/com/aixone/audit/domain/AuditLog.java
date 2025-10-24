package com.aixone.audit.domain;

import com.aixone.common.ddd.Entity;
import com.aixone.session.SessionContext;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * 审计日志聚合根
 * 负责记录系统操作的重要审计信息
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Getter
@Setter
public class AuditLog extends Entity<Long> {
    
    /** 操作用户ID */
    private String userId;
    
    /** 操作类型 */
    private String action;
    
    /** 操作资源 */
    private String resource;
    
    /** 操作结果 */
    private String result;
    
    /** 操作时间 */
    private LocalDateTime timestamp;
    
    /** 客户端IP */
    private String clientIp;
    
    /** 用户代理 */
    private String userAgent;
    
    /** 操作详情（JSON格式） */
    private String details;
    
    /** 错误信息 */
    private String errorMessage;
    
    /** 会话ID */
    private String sessionId;
    
    /**
     * No-argument constructor for JPA/frameworks
     */
    public AuditLog() {
        super(0L); // Use 0L as placeholder, actual ID will be set by JPA
    }
    
    /**
     * 构造函数
     * 
     * @param id 日志ID
     * @param userId 用户ID
     * @param action 操作类型
     * @param resource 操作资源
     * @param result 操作结果
     */
    public AuditLog(Long id, String userId, String action, String resource, String result) {
        super(id);
        this.userId = userId;
        this.action = action;
        this.resource = resource;
        this.result = result;
        this.timestamp = LocalDateTime.now();
        this.tenantId = getCurrentTenantId();
    }
    
    /**
     * 构造函数（完整参数）
     * 
     * @param id 日志ID
     * @param userId 用户ID
     * @param action 操作类型
     * @param resource 操作资源
     * @param result 操作结果
     * @param clientIp 客户端IP
     * @param userAgent 用户代理
     * @param details 操作详情
     */
    public AuditLog(Long id, String userId, String action, String resource, String result,
                   String clientIp, String userAgent, String details) {
        super(id);
        this.userId = userId;
        this.action = action;
        this.resource = resource;
        this.result = result;
        this.clientIp = clientIp;
        this.userAgent = userAgent;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * 构造函数（从持久化实体恢复）
     * 
     * @param id 日志ID
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param action 操作类型
     * @param resource 操作资源
     * @param result 操作结果
     * @param timestamp 操作时间
     * @param clientIp 客户端IP
     * @param userAgent 用户代理
     * @param details 操作详情
     * @param errorMessage 错误消息
     * @param sessionId 会话ID
     */
    public AuditLog(Long id, String tenantId, String userId, String action, String resource, String result,
                   LocalDateTime timestamp, String clientIp, String userAgent, String details, 
                   String errorMessage, String sessionId) {
        super(id, tenantId);
        this.userId = userId;
        this.action = action;
        this.resource = resource;
        this.result = result;
        this.timestamp = timestamp;
        this.clientIp = clientIp;
        this.userAgent = userAgent;
        this.details = details;
        this.errorMessage = errorMessage;
        this.sessionId = sessionId;
    }
    
    /**
     * 设置操作详情
     * 
     * @param details 操作详情
     */
    public void setDetails(String details) {
        this.details = details;
    }
    
    /**
     * 设置操作详情（Map格式）
     * 
     * @param detailsMap 操作详情Map
     */
    public void setDetails(Map<String, Object> detailsMap) {
        this.details = com.aixone.common.util.JsonUtils.toJson(detailsMap);
    }
    
    /**
     * 设置错误信息
     * 
     * @param errorMessage 错误信息
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    /**
     * 设置会话ID
     * 
     * @param sessionId 会话ID
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    /**
     * 检查是否为成功操作
     * 
     * @return 是否成功
     */
    public boolean isSuccess() {
        return "SUCCESS".equalsIgnoreCase(result);
    }
    
    /**
     * 检查是否为失败操作
     * 
     * @return 是否失败
     */
    public boolean isFailure() {
        return "FAILURE".equalsIgnoreCase(result);
    }
    
    /**
     * 检查是否属于指定用户
     * 
     * @param userId 用户ID
     * @return 是否属于指定用户
     */
    public boolean belongsToUser(String userId) {
        return Objects.equals(this.userId, userId);
    }
    
    /**
     * 检查是否为登录操作
     * 
     * @return 是否为登录操作
     */
    public boolean isLoginAction() {
        return "LOGIN".equalsIgnoreCase(action);
    }
    
    /**
     * 检查是否为登出操作
     * 
     * @return 是否为登出操作
     */
    public boolean isLogoutAction() {
        return "LOGOUT".equalsIgnoreCase(action);
    }
    
    /**
     * 检查是否为权限校验操作
     * 
     * @return 是否为权限校验操作
     */
    public boolean isPermissionCheckAction() {
        return "PERMISSION_CHECK".equalsIgnoreCase(action);
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
}
