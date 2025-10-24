package com.aixone.tech.auth.authentication.application.dto.verification;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 验证码发送响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendVerificationCodeResponse {
    
    private boolean success;
    private String message;
    private String codeId; // 用于调试，生产环境不返回
    private int expireMinutes;
}
