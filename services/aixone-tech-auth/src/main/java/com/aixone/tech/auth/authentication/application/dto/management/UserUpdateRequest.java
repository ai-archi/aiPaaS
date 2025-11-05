package com.aixone.tech.auth.authentication.application.dto.management;

import lombok.Data;

/**
 * 更新认证用户请求DTO
 */
@Data
public class UserUpdateRequest {
    private String email;
    private String phone;
    private String status; // ACTIVE, INACTIVE, LOCKED
}

