package com.aixone.directory.permission.infrastructure.persistence.dbo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限规则数据对象
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Entity
@Table(name = "permission_rules", indexes = {
    @Index(name = "idx_permission_rules_tenant_id", columnList = "tenant_id"),
    @Index(name = "idx_permission_rules_pattern", columnList = "pattern"),
    @Index(name = "idx_permission_rules_permission", columnList = "permission"),
    @Index(name = "idx_permission_rules_enabled", columnList = "enabled"),
    @Index(name = "idx_permission_rules_priority", columnList = "priority")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionRuleDbo {
    
    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private String id;
    
    @Column(name = "tenant_id", nullable = false, length = 255)
    private String tenantId;
    
    @Column(name = "pattern", nullable = false, length = 500)
    private String pattern;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "rule_id", referencedColumnName = "id")
    private List<PermissionRuleMethodDbo> methodEntities;
    
    /**
     * 获取HTTP方法列表（便捷方法）
     */
    @Transient
    public List<String> getMethods() {
        if (methodEntities == null) {
            return List.of();
        }
        return methodEntities.stream()
                .map(PermissionRuleMethodDbo::getMethod)
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 设置HTTP方法列表（便捷方法）
     */
    @Transient
    public void setMethods(List<String> methods) {
        if (methods == null || methods.isEmpty()) {
            this.methodEntities = List.of();
            return;
        }
        this.methodEntities = methods.stream()
                .map(method -> new PermissionRuleMethodDbo(this.id, method))
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Column(name = "permission", nullable = false, length = 255)
    private String permission;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "enabled", nullable = false)
    private Boolean enabled;
    
    @Column(name = "priority", nullable = false)
    private Integer priority;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (enabled == null) {
            enabled = true;
        }
        if (priority == null) {
            priority = 0;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

