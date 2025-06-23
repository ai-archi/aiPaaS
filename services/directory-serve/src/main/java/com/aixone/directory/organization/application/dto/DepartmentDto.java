package com.aixone.directory.organization.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class DepartmentDto {
    private UUID id;
    private UUID orgId;
    private String name;
    private UUID parentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 