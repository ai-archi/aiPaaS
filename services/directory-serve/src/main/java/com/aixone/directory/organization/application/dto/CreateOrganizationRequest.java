package com.aixone.directory.organization.application.dto;

import lombok.Data;

@Data
public class CreateOrganizationRequest {
    private String tenantId;
    private String name;
} 