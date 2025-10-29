package com.aixone.directory.group.application.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupDto {
    private String id;
    private String tenantId;
    private String name;
    private Set<String> members;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 