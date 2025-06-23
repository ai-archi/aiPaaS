package com.aixone.directory.organization.application.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class CreateDepartmentRequest {
    private String name;
    private UUID parentId; // Optional, for creating a sub-department
} 