package com.aixone.tech.auth.authorization.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户角色关联实体
 */
@Entity
@Table(name = "user_roles", uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_roles_tenant_user_role", columnNames = {"tenant_id", "user_id", "role_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleEntity {
    
    @Id
    @Column(name = "user_role_id")
    private String userRoleId;
    
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "role_id", nullable = false)
    private String roleId;
    
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
