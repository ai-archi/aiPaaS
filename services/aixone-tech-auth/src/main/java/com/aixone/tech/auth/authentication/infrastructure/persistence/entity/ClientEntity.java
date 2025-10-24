package com.aixone.tech.auth.authentication.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 客户端实体
 */
@Entity
@Table(name = "clients", uniqueConstraints = {
    @UniqueConstraint(name = "uk_clients_client_tenant", columnNames = {"client_id", "tenant_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "client_id", nullable = false)
    private String clientId;
    
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;
    
    @Column(name = "client_secret", nullable = false)
    private String clientSecret;
    
    @Column(name = "redirect_uri", length = 500)
    private String redirectUri;
    
    @Column(name = "scopes", columnDefinition = "TEXT")
    private String scopes;
    
    @Column(name = "grant_types", columnDefinition = "TEXT")
    private String grantTypes;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
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
