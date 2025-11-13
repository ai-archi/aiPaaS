package com.aixone.directory.permission.infrastructure.persistence.dbo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 角色权限关系数据对象
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Entity
@Table(name = "role_permissions", indexes = {
    @Index(name = "idx_role_permissions_role_id", columnList = "role_id"),
    @Index(name = "idx_role_permissions_permission_id", columnList = "permission_id"),
    @Index(name = "idx_role_permissions_tenant_id", columnList = "tenant_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(RolePermissionId.class)
public class RolePermissionDbo {
    
    @Id
    @Column(name = "role_id", columnDefinition = "UUID")
    private String roleId;
    
    @Id
    @Column(name = "permission_id", columnDefinition = "UUID")
    private String permissionId;
    
    @Column(name = "tenant_id", nullable = false, length = 255)
    private String tenantId;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}

