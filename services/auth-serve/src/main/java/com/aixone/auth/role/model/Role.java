package com.aixone.auth.role.model;

import lombok.Data;
import java.util.List;
import com.aixone.auth.permission.model.Permission;

@Data
public class Role {
    private String roleId;
    private String name;
    private List<Permission> permissions;
} 