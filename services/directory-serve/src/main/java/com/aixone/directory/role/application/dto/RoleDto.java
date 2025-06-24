package com.aixone.directory.role.application.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {
    private UUID id;
    private UUID tenantId;
    private String name;
    private Set<UUID> userIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 