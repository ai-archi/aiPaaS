package com.aixone.tech.auth.authorization.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 权限实体
 */
@Entity
@Table(name = "permissions", uniqueConstraints = {
    @UniqueConstraint(name = "uk_permissions_tenant_resource_action", columnNames = {"tenant_id", "resource", "action"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionEntity {
    
    @Id
    @Column(name = "permission_id")
    private String permissionId;
    
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "resource", nullable = false)
    private String resource;
    
    @Column(name = "action", nullable = false)
    private String action;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
