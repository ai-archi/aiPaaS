package com.aixone.permission.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色实体
 * 与aixone-tech-auth保持一致
 * 
 * @author aixone
 */
@Data
@EqualsAndHashCode(of = "roleId")
public class Role {
    
    /**
     * 角色ID
     */
    private String roleId;
    
    /**
     * 租户ID
     */
    private String tenantId;
    
    /**
     * 角色名称
     */
    private String name;
    
    /**
     * 角色描述
     */
    private String description;
    
    /**
     * 权限ID列表
     */
    private List<String> permissionIds;
    
    /**
     * 权限列表（为了兼容性）
     */
    private List<Permission> permissions;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 