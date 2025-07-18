package com.aixone.metacenter.metamanagement.domain;

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
 * 元数据规则实体
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "meta_rules", indexes = {
    @Index(name = "idx_meta_rules_meta_object_id", columnList = "meta_object_id"),
    @Index(name = "idx_meta_rules_rule_type", columnList = "rule_type"),
    @Index(name = "idx_meta_rules_name", columnList = "name")
})
@EntityListeners(AuditingEntityListener.class)
public class MetaRule {

    /** 唯一标识 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 规则名称 */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** 规则类型 */
    @Column(name = "rule_type", nullable = false, length = 50)
    private String ruleType;

    /** 规则组 */
    @Column(name = "rule_group", length = 100)
    private String ruleGroup;

    /** 规则表达式 */
    @Column(name = "expression", nullable = false, length = 1000)
    private String expression;

    /** 错误消息 */
    @Column(name = "error_message", length = 500)
    private String errorMessage;

    /** 是否启用 */
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    /** 优先级 */
    @Column(name = "priority")
    private Integer priority = 0;

    /** 触发条件 */
    @Column(name = "trigger", length = 100)
    private String trigger;

    /** 执行动作 */
    @Column(name = "action", length = 500)
    private String action;

    /** 规则流程 */
    @Column(name = "flow", columnDefinition = "jsonb")
    private Map<String, Object> flow = new HashMap<>();

    /** 扩展字段 */
    @Column(name = "extension_fields", columnDefinition = "jsonb")
    private Map<String, Object> extensionFields = new HashMap<>();

    /** 创建时间 */
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** 关联的元数据对象 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meta_object_id", nullable = false)
    private MetaObject metaObject;

    /**
     * 检查是否为启用状态
     * 
     * @return 是否为启用状态
     */
    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled);
    }

    /**
     * 获取规则流程步骤
     * 
     * @return 规则流程步骤
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getFlowSteps() {
        return (Map<String, Object>) flow.getOrDefault("steps", new HashMap<>());
    }

    /**
     * 设置规则流程步骤
     * 
     * @param steps 规则流程步骤
     */
    public void setFlowSteps(Map<String, Object> steps) {
        flow.put("steps", steps);
    }
} 