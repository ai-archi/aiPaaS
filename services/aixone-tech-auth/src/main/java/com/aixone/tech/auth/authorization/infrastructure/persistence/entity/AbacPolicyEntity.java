package com.aixone.tech.auth.authorization.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * ABAC策略实体
 */
@Entity
@Table(name = "abac_policies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbacPolicyEntity {
    
    @Id
    @Column(name = "policy_id")
    private String policyId;
    
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "resource", nullable = false)
    private String resource;
    
    @Column(name = "action", nullable = false)
    private String action;
    
    @Column(name = "condition", columnDefinition = "TEXT")
    private String condition;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @ElementCollection
    @CollectionTable(name = "abac_policy_attributes", joinColumns = @JoinColumn(name = "policy_id"))
    @MapKeyColumn(name = "attribute_key")
    @Column(name = "attribute_value")
    private Map<String, String> attributes;
    
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
