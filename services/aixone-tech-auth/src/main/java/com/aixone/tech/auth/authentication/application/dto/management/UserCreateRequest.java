package com.aixone.tech.auth.authentication.application.dto.management;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import lombok.Data;

/**
 * 创建认证用户请求DTO
 */
@Data
public class UserCreateRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    private String phone;
    
    @NotBlank(message = "租户ID不能为空")
    private String tenantId;
}

