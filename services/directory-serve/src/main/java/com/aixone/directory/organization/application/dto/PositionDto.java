package com.aixone.directory.organization.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class PositionDto {
    private UUID id;
    private UUID orgId;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 