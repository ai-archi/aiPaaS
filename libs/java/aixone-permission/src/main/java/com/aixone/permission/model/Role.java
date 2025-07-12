package com.aixone.permission.model;

import lombok.Data;
import java.util.List;

@Data
public class Role {
    private String roleId;
    private String name;
    private List<Permission> permissions;
} 