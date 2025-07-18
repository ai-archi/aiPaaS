package com.aixone.metacenter.ruleengine.domain;

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
 * 规则实体
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "rules", indexes = {
    @Index(name = "idx_rules_tenant_id", columnList = "tenant_id"),
    @Index(name = "idx_rules_name", columnList = "name"),
    @Index(name = "idx_rules_type", columnList = "type"),
    @Index(name = "idx_rules_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
public class Rule {

    /** 唯一标识 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 规则名称 */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** 规则类型 */
    @Column(name = "type", nullable = false, length = 50)
    private String type;

    /** 规则描述 */
    @Column(name = "description", length = 500)
    private String description;

    /** 规则表达式 */
    @Column(name = "expression", nullable = false, columnDefinition = "text")
    private String expression;

    /** 规则条件 */
    @Column(name = "condition", columnDefinition = "text")
    private String condition;

    /** 规则动作 */
    @Column(name = "action", columnDefinition = "text")
    private String action;

    /** 优先级 */
    @Column(name = "priority")
    private Integer priority = 0;

    /** 是否启用 */
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    /** 状态 */
    @Column(name = "status", nullable = false, length = 20)
    private String status = "active";

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
     * 检查规则是否启用
     * 
     * @return 是否启用
     */
    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled);
    }

    /**
     * 检查规则是否激活
     * 
     * @return 是否激活
     */
    public boolean isActive() {
        return "active".equals(status);
    }
} 