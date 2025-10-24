package com.aixone.tech.auth.authentication.application.dto.verification;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VerificationCodeResponse {
    private boolean success;
    private String message;
    private String codeId;
    private int expiresIn; // seconds
    private String tenantId;
    private String userId;
    private String type;
    private String destination;
    private String purpose;
    private String code;
    private boolean verified;
    
    // 用于测试的构造函数
    public VerificationCodeResponse(boolean success, String message, String codeId, int expiresIn) {
        this.success = success;
        this.message = message;
        this.codeId = codeId;
        this.expiresIn = expiresIn;
    }
    
    // 用于测试的构造函数
    public VerificationCodeResponse(boolean success, String message, String codeId, int expiresIn, 
                                   String tenantId, String userId, String type, String destination, 
                                   String purpose, String code, boolean verified) {
        this.success = success;
        this.message = message;
        this.codeId = codeId;
        this.expiresIn = expiresIn;
        this.tenantId = tenantId;
        this.userId = userId;
        this.type = type;
        this.destination = destination;
        this.purpose = purpose;
        this.code = code;
        this.verified = verified;
    }
}
