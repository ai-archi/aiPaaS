package com.aixone.auth.permission.model;

import lombok.Data;

@Data
public class Permission {
    private String permissionId;
    private String resource;
    private String action;
    // 可扩展更多属性，如描述、级别等
} 