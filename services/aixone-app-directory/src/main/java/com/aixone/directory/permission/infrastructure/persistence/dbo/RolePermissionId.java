package com.aixone.directory.permission.infrastructure.persistence.dbo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 角色权限关系复合主键类
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionId implements Serializable {
    private String roleId;
    private String permissionId;
}


