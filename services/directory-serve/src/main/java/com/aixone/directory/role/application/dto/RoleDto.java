package com.aixone.directory.role.application.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleDto {
    private UUID id;
    private UUID tenantId;
    private String name;
    private Set<UUID> members;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 