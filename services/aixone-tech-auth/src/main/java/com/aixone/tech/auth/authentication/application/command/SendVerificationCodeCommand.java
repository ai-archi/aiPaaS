package com.aixone.tech.auth.authentication.application.command;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 发送验证码命令
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendVerificationCodeCommand {
    
    private String tenantId;
    private String phone;
    private String email;
    private String type; // SMS, EMAIL
    private String clientId;
    private String clientSecret;
    private String clientIp;
    private String userAgent;
}
