package com.aixone.metacenter.permissionservice.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 权限实体
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "permissions", indexes = {
    @Index(name = "idx_permissions_tenant_id", columnList = "tenant_id"),
    @Index(name = "idx_permissions_subject", columnList = "subject"),
    @Index(name = "idx_permissions_object", columnList = "object"),
    @Index(name = "idx_permissions_action", columnList = "action")
})
@EntityListeners(AuditingEntityListener.class)
public class Permission {

    /** 唯一标识 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 权限主体 */
    @Column(name = "subject", nullable = false, length = 100)
    private String subject;

    /** 权限对象 */
    @Column(name = "object", nullable = false, length = 100)
    private String object;

    /** 操作类型 */
    @Column(name = "action", nullable = false, length = 50)
    private String action;

    /** 控制粒度 */
    @Column(name = "scope", length = 50)
    private String scope;

    /** 权限策略 */
    @Column(name = "policy", length = 50)
    private String policy;

    /** 附加条件 */
    @Column(name = "condition", columnDefinition = "text")
    private String condition;

    /** 是否脱敏 */
    @Column(name = "masking", nullable = false)
    private Boolean masking = false;

    /** 脱敏类型 */
    @Column(name = "masking_type", length = 50)
    private String maskingType;

    /** 是否启用 */
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    /** 租户ID */
    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;

    /** 扩展字段 */
    @Column(name = "extension_fields", columnDefinition = "jsonb")
    private Map<String, Object> extensionFields = new HashMap<>();

    /** 创建人 */
    @Column(name = "created_by", length = 100)
    private String createdBy;

    /** 创建时间 */
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** 更新人 */
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    /** 更新时间 */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 检查权限是否启用
     * 
     * @return 是否启用
     */
    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled);
    }

    /**
     * 检查是否需要脱敏
     * 
     * @return 是否需要脱敏
     */
    public boolean isMasking() {
        return Boolean.TRUE.equals(masking);
    }
} 