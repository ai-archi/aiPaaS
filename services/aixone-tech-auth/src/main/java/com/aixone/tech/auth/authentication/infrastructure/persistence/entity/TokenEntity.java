package com.aixone.tech.auth.authentication.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 令牌实体
 */
@Entity
@Table(name = "tokens")
@Data
@NoArgsConstructor
public class TokenEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "token", nullable = false, unique = true, length = 500)
    private String token;
    
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "client_id", nullable = false)
    private String clientId;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(name = "type", nullable = false, length = 50)
    private String type;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
