package com.aixone.directory.organization.application.dto;

import lombok.Data;

@Data
public class CreateDepartmentRequest {
    private String name;
    private String parentId; // Optional, for creating a sub-department
} 