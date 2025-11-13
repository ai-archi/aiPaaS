package com.aixone.directory.permission.infrastructure.persistence.dbo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 权限数据对象
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Entity
@Table(name = "permissions", indexes = {
    @Index(name = "idx_permissions_tenant_id", columnList = "tenant_id"),
    @Index(name = "idx_permissions_code", columnList = "code"),
    @Index(name = "idx_permissions_resource", columnList = "resource"),
    @Index(name = "idx_permissions_action", columnList = "action"),
    @Index(name = "idx_permissions_type", columnList = "type")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDbo {
    
    @Id
    @Column(name = "permission_id", columnDefinition = "UUID")
    private String permissionId;
    
    @Column(name = "tenant_id", nullable = false, length = 255)
    private String tenantId;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    @Column(name = "code", nullable = false, length = 255)
    private String code;
    
    @Column(name = "resource", nullable = false, length = 255)
    private String resource;
    
    @Column(name = "action", nullable = false, length = 100)
    private String action;
    
    @Column(name = "type", nullable = false, length = 50)
    private String type;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "abac_conditions", columnDefinition = "JSONB")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> abacConditions;
    
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
        if (type == null) {
            type = "FUNCTIONAL";
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

