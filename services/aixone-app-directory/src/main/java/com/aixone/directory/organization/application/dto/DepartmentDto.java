package com.aixone.directory.organization.application.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DepartmentDto {
    private String id;
    private String orgId;
    private String name;
    private String parentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 