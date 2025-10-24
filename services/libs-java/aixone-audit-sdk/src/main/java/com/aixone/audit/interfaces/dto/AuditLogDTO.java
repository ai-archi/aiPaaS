package com.aixone.audit.interfaces.dto;

import com.aixone.common.api.BaseDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 审计日志DTO
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuditLogDTO extends BaseDTO {
    
    /** 审计日志ID */
    private Long id;
    
    /** 操作用户ID */
    private String userId;
    
    /** 操作类型 */
    private String action;
    
    /** 操作资源 */
    private String resource;
    
    /** 操作结果 */
    private String result;
    
    /** 操作时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
     * 构造函数
     */
    public AuditLogDTO() {
        super();
    }
    
    /**
     * 构造函数
     * 
     * @param tenantId 租户ID
     */
    public AuditLogDTO(String tenantId) {
        super(tenantId);
    }
}
