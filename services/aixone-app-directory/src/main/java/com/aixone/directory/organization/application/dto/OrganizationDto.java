package com.aixone.directory.organization.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class OrganizationDto {
    private String id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<DepartmentDto> departments;
    private List<PositionDto> positions;
} 