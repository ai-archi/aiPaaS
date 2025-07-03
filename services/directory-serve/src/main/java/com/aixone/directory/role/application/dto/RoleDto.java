package com.aixone.directory.role.application.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {
    private String id;
    private String tenantId;
    private String name;
    private Set<String> userIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 