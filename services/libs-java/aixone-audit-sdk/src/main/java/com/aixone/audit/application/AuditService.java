package com.aixone.audit.application;

import com.aixone.audit.domain.AuditContext;
import com.aixone.audit.domain.AuditEvent;
import com.aixone.audit.domain.AuditLog;
import com.aixone.audit.domain.AuditLogRepository;
import com.aixone.audit.infrastructure.AuditEventPublisher;
import com.aixone.common.exception.ValidationException;
import com.aixone.common.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 审计服务
 * 提供审计日志的创建、查询等业务功能
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Service
@Transactional
public class AuditService {
    
    private final AuditLogRepository auditLogRepository;
    private final AuditEventPublisher auditEventPublisher;
    
    @Autowired
    public AuditService(AuditLogRepository auditLogRepository, AuditEventPublisher auditEventPublisher) {
        this.auditLogRepository = auditLogRepository;
        this.auditEventPublisher = auditEventPublisher;
    }
    
    /**
     * 记录操作成功日志
     * 
     * @param action 操作类型
     * @param resource 操作资源
     * @param details 操作详情
     * @return 审计日志
     */
    public AuditLog logSuccess(String action, String resource, Map<String, Object> details) {
        return logAction(action, resource, "SUCCESS", details, null);
    }
    
    /**
     * 记录操作成功日志
     * 
     * @param action 操作类型
     * @param resource 操作资源
     * @return 审计日志
     */
    public AuditLog logSuccess(String action, String resource) {
        return logAction(action, resource, "SUCCESS", null, null);
    }
    
    /**
     * 记录操作失败日志
     * 
     * @param action 操作类型
     * @param resource 操作资源
     * @param errorMessage 错误信息
     * @return 审计日志
     */
    public AuditLog logFailure(String action, String resource, String errorMessage) {
        return logAction(action, resource, "FAILURE", null, errorMessage);
    }
    
    /**
     * 记录操作失败日志
     * 
     * @param action 操作类型
     * @param resource 操作资源
     * @param errorMessage 错误信息
     * @param details 操作详情
     * @return 审计日志
     */
    public AuditLog logFailure(String action, String resource, String errorMessage, Map<String, Object> details) {
        return logAction(action, resource, "FAILURE", details, errorMessage);
    }
    
    /**
     * 记录操作日志
     * 
     * @param action 操作类型
     * @param resource 操作资源
     * @param result 操作结果
     * @param details 操作详情
     * @param errorMessage 错误信息
     * @return 审计日志
     */
    public AuditLog logAction(String action, String resource, String result, 
                             Map<String, Object> details, String errorMessage) {
        ValidationUtils.notBlank(action, "操作类型不能为空");
        ValidationUtils.notBlank(resource, "操作资源不能为空");
        ValidationUtils.notBlank(result, "操作结果不能为空");
        
        AuditContext context = new AuditContext(action, resource);
        if (details != null) {
            context.addDetails(details);
        }
        
        AuditLog auditLog = new AuditLog(
            null, // ID由数据库生成
            context.getUserId(),
            action,
            resource,
            result
        );
        
        if (details != null && !details.isEmpty()) {
            auditLog.setDetails(details);
        }
        
        if (errorMessage != null) {
            auditLog.setErrorMessage(errorMessage);
        }
        
        auditLog.setClientIp(context.getClientIp());
        auditLog.setUserAgent(context.getUserAgent());
        auditLog.setSessionId(context.getSessionId());
        
        // 保存审计日志
        AuditLog savedLog = auditLogRepository.save(auditLog);
        
        // 发布审计事件
        AuditEvent auditEvent = new AuditEvent(
            savedLog.getId(),
            savedLog.getUserId(),
            savedLog.getAction(),
            savedLog.getResult(),
            savedLog.getTenantId()
        );
        auditEventPublisher.publish(auditEvent);
        
        return savedLog;
    }
    
    /**
     * 记录登录成功日志
     * 
     * @param userId 用户ID
     * @param clientIp 客户端IP
     * @param userAgent 用户代理
     * @return 审计日志
     */
    public AuditLog logLoginSuccess(String userId, String clientIp, String userAgent) {
        ValidationUtils.notBlank(userId, "用户ID不能为空");
        
        AuditLog auditLog = new AuditLog(
            null,
            userId,
            "LOGIN",
            "AUTH_SERVICE",
            "SUCCESS"
        );
        
        auditLog.setClientIp(clientIp);
        auditLog.setUserAgent(userAgent);
        auditLog.setTimestamp(LocalDateTime.now());
        
        return auditLogRepository.save(auditLog);
    }
    
    /**
     * 记录登录失败日志
     * 
     * @param userId 用户ID
     * @param reason 失败原因
     * @param clientIp 客户端IP
     * @param userAgent 用户代理
     * @return 审计日志
     */
    public AuditLog logLoginFailure(String userId, String reason, String clientIp, String userAgent) {
        ValidationUtils.notBlank(userId, "用户ID不能为空");
        ValidationUtils.notBlank(reason, "失败原因不能为空");
        
        AuditLog auditLog = new AuditLog(
            null,
            userId,
            "LOGIN",
            "AUTH_SERVICE",
            "FAILURE"
        );
        
        auditLog.setClientIp(clientIp);
        auditLog.setUserAgent(userAgent);
        auditLog.setErrorMessage(reason);
        auditLog.setTimestamp(LocalDateTime.now());
        
        return auditLogRepository.save(auditLog);
    }
    
    /**
     * 记录登出日志
     * 
     * @param userId 用户ID
     * @param clientIp 客户端IP
     * @param userAgent 用户代理
     * @return 审计日志
     */
    public AuditLog logLogout(String userId, String clientIp, String userAgent) {
        ValidationUtils.notBlank(userId, "用户ID不能为空");
        
        AuditLog auditLog = new AuditLog(
            null,
            userId,
            "LOGOUT",
            "AUTH_SERVICE",
            "SUCCESS"
        );
        
        auditLog.setClientIp(clientIp);
        auditLog.setUserAgent(userAgent);
        auditLog.setTimestamp(LocalDateTime.now());
        
        return auditLogRepository.save(auditLog);
    }
    
    /**
     * 记录权限校验日志
     * 
     * @param userId 用户ID
     * @param resource 资源
     * @param action 操作
     * @param result 结果
     * @param reason 原因
     * @return 审计日志
     */
    public AuditLog logPermissionCheck(String userId, String resource, String action, 
                                     String result, String reason) {
        ValidationUtils.notBlank(userId, "用户ID不能为空");
        ValidationUtils.notBlank(resource, "资源不能为空");
        ValidationUtils.notBlank(action, "操作不能为空");
        ValidationUtils.notBlank(result, "结果不能为空");
        
        AuditLog auditLog = new AuditLog(
            null,
            userId,
            "PERMISSION_CHECK",
            resource + ":" + action,
            result
        );
        
        if (reason != null) {
            auditLog.setErrorMessage(reason);
        }
        auditLog.setTimestamp(LocalDateTime.now());
        
        return auditLogRepository.save(auditLog);
    }
}
