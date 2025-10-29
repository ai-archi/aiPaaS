package com.aixone.directory.organization.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class PositionDto {
    private String id;
    private String orgId;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 