package com.aixone.tech.auth.authorization.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbacPolicyResponse {
    private String policyId;
    private String tenantId;
    private String name;
    private String description;
    private String resource;
    private String action;
    private String condition;
    private Map<String, Object> attributes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
