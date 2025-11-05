package com.aixone.tech.auth.authentication.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 用户领域模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    private UUID id;
    private String username;
    private String hashedPassword;
    private String email;
    private String phone;
    private String avatarUrl;
    private String bio;
    private String status;
    private String tenantId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public User(String username, String hashedPassword, String email, String tenantId) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.email = email;
        this.tenantId = tenantId;
        this.status = "ACTIVE";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public User(UUID id, String username, String hashedPassword, String email, String tenantId) {
        this.id = id;
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.email = email;
        this.tenantId = tenantId;
        this.status = "ACTIVE";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
    
    // Getters
    public UUID getId() {
        return id;
    }
    
    public String getHashedPassword() {
        return hashedPassword;
    }
}
