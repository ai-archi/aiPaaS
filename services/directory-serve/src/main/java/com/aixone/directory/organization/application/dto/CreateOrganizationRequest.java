package com.aixone.directory.organization.application.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class CreateOrganizationRequest {
    private UUID tenantId;
    private String name;
} 