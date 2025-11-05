package com.aixone.tech.auth.authentication.application.dto.management;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Token信息响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenInfoResponse {
    private String token;
    private String userId;
    private String clientId;
    private String tenantId;
    private String type; // ACCESS, REFRESH
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}

