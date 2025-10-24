package com.aixone.tech.auth.authentication.domain.model;

import java.time.LocalDateTime;

/**
 * 验证码领域模型
 */
public class VerificationCode {
    
    /**
     * 验证码类型枚举
     */
    public enum CodeType {
        SMS, EMAIL
    }
    
    private String id;
    private String phone;
    private String email;
    private String tenantId;
    private String code;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private String type; // SMS, EMAIL
    private boolean used;
    
    public VerificationCode() {}
    
    public VerificationCode(String phone, String email, String tenantId, 
                          String code, LocalDateTime expiresAt, String type) {
        this.phone = phone;
        this.email = email;
        this.tenantId = tenantId;
        this.code = code;
        this.expiresAt = expiresAt;
        this.type = type;
        this.createdAt = LocalDateTime.now();
        this.used = false;
    }

    public VerificationCode(String id, String tenantId, String phone, String email, 
                          String code, String type, LocalDateTime expiresAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.phone = phone;
        this.email = email;
        this.code = code;
        this.type = type;
        this.expiresAt = expiresAt;
        this.createdAt = LocalDateTime.now();
        this.used = false;
    }
    
    /**
     * 验证码是否过期
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
    
    /**
     * 验证码是否有效
     */
    public boolean isValid() {
        return !used && !isExpired();
    }
    
    /**
     * 标记验证码为已使用
     */
    public void markAsUsed() {
        this.used = true;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public String getCodeId() {
        return id;
    }

    public void setCodeId(String codeId) {
        this.id = codeId;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getTenantId() {
        return tenantId;
    }
    
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public boolean isUsed() {
        return used;
    }
    
    public void setUsed(boolean used) {
        this.used = used;
    }
    
    public boolean isVerified() {
        return used;
    }
    
    public void setVerified(boolean verified) {
        this.used = verified;
    }
}
